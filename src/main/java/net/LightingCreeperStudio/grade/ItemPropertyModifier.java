package net.LightingCreeperStudio.grade;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.Map;

/**
 * 物品属性修改器
 * Paper 1.21.11+ 版本
 * 
 * 属性分配规则：
 * 遍历原版默认属性，有哪些就加哪些：
 * - 有 ATTACK_DAMAGE → 合并攻击加成
 * - 有 ARMOR → 合并护甲加成
 * - 有 ARMOR_TOUGHNESS → 合并韧性加成
 * - 有耐久→ 计算耐久偏移
 * 
 * 重要：Minecraft 1.20.5+ 的 minecraft:attribute_modifiers 组件一旦设置就会替代物品类型的默认属性。
 * 因此必须使用 Material.getDefaultAttributeModifiers() 获取原版默认属性
 * 加上等级修饰符后用 setAttributeModifiers() 一次性写入 确保叠加而非覆盖。
 */
public class ItemPropertyModifier {

    // 命名空间键
    private static final NamespacedKey ATTACK_KEY = NamespacedKey.fromString("equipmentgrade:attack");
    private static final NamespacedKey ARMOR_KEY = NamespacedKey.fromString("equipmentgrade:armor");
    private static final NamespacedKey TOUGHNESS_KEY = NamespacedKey.fromString("equipmentgrade:toughness");

    // 应用等级属性到物品
    public static void applyGradeProperties(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return;

        Material material = item.getType();
        
        // 检查黑名单
        if (ItemBlacklist.isBlacklisted(material)) return;

        GradeType grade = NBTUtil.getGrade(item);
        if (grade == null) return;

        boolean hasDurability = material.getMaxDurability() > 0;

        // 获取耐久偏移
        int durabilityOffset = 0;
        if (hasDurability) {
            if (grade == GradeType.UNKNOWN) {
                durabilityOffset = NBTUtil.getCustomDurability(item);
            } else {
                durabilityOffset = grade.getDurabilityOffset();
                // 凡品以下等级 耐久偏移为负数
                if (grade.getAttributeOffset() < 0) {
                    durabilityOffset = -durabilityOffset;
                }
            }
        }

        // 获取属性偏移
        double attributeOffset = 0;
        if (grade != GradeType.COMMON) {
            if (grade == GradeType.UNKNOWN) {
                attributeOffset = NBTUtil.getCustomAttribute(item);
            } else {
                attributeOffset = grade.getAttributeOffset();
            }
        }

        // ====== 一次性获取 meta 所有修改都在同一个 meta 上完成 ======
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        // 存储自定义最大耐久到 NBT
        if (hasDurability) {
            int customMaxDurability = material.getMaxDurability() + durabilityOffset;
            if (customMaxDurability < 1) customMaxDurability = 1;
            NamespacedKey maxDuraKey = new NamespacedKey("equipmentgrade", NBTUtil.NBT_KEY_CUSTOM_MAX_DURABILITY);
            meta.getPersistentDataContainer().set(maxDuraKey, PersistentDataType.INTEGER, customMaxDurability);
        }

        // 构建完整的属性修饰符列表:原版默认值 + 其他插件修饰符 + 等级加成
        Multimap<Attribute, AttributeModifier> modifiers = buildModifiers(material, meta, attributeOffset);
        meta.setAttributeModifiers(modifiers);

        // ====== 一次性设置 meta ======
        item.setItemMeta(meta);

        // 如果当前已损耗量超过自定义最大耐久 立即损坏物品
        if (hasDurability) {
            int customMaxDurability = material.getMaxDurability() + durabilityOffset;
            if (customMaxDurability < 1) customMaxDurability = 1;
            short currentDamage = item.getDurability();
            if (currentDamage >= customMaxDurability) {
                item.setDurability((short) customMaxDurability);
            }
        }
    }

    /**
     * 构建完整的属性修饰符列表
     * 确保原版默认属性始终被保留，等级加成叠加在原版值之上
     * 
     * 步骤：
     * 1. 从 Material.getDefaultAttributeModifiers() 获取原版默认属性
     * 2. 保留其他插件添加的非 minecraft/equipmentgrade 命名空间的修饰符
     * 3. 遍历原版默认属性 有哪些就加哪些:ATTACK_DAMAGE / ARMOR / ARMOR_TOUGHNESS
     */
    private static Multimap<Attribute, AttributeModifier> buildModifiers(
            Material material, ItemMeta meta, double attributeOffset) {
        
        // 以原版默认属性为基础
        Multimap<Attribute, AttributeModifier> result = ArrayListMultimap.create();
        Multimap<Attribute, AttributeModifier> defaults = material.getDefaultAttributeModifiers();
        if (defaults == null) {
            defaults = ArrayListMultimap.create();
        }

        result.putAll(defaults);

        // 保留其他插件的修饰符
        Multimap<Attribute, AttributeModifier> current = meta.getAttributeModifiers();
        if (current != null && !current.isEmpty()) {
            for (Map.Entry<Attribute, AttributeModifier> entry : current.entries()) {
                AttributeModifier modifier = entry.getValue();
                NamespacedKey key = modifier.getKey();
                // 跳过旧的 equipmentgrade 修饰符
                if (key != null && "equipmentgrade".equals(key.getNamespace())) {
                    continue;
                }
                // 跳过 minecraft 命名空间的修饰符
                if (key != null && "minecraft".equals(key.getNamespace())) {
                    continue;
                }
                result.put(entry.getKey(), modifier);
            }
        }

        // 遍历原版默认属性 有哪些就合并等级加成
        // 弓/弩原版默认属性不含 ATTACK_DAMAGE,但品级需要增加远程伤害
        if (attributeOffset != 0) {
            if (defaults.containsKey(Attribute.ATTACK_DAMAGE)) {
                mergeIntoDefault(result, Attribute.ATTACK_DAMAGE, attributeOffset, ATTACK_KEY, EquipmentSlotGroup.MAINHAND);
            } else if (isRangedWeapon(material)) {
                // 弓/弩:原版没有 ATTACK_DAMAGE 默认值 但品级需要显示攻击力加成
                // 添加品级攻击力修饰符使 tooltip 显示伤害数值
                result.put(Attribute.ATTACK_DAMAGE, new AttributeModifier(
                        ATTACK_KEY, attributeOffset,
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.MAINHAND
                ));
            }
            if (defaults.containsKey(Attribute.ARMOR)) {
                mergeIntoDefault(result, Attribute.ARMOR, attributeOffset, ARMOR_KEY, EquipmentSlotGroup.ARMOR);
            }
            if (defaults.containsKey(Attribute.ARMOR_TOUGHNESS)) {
                mergeIntoDefault(result, Attribute.ARMOR_TOUGHNESS, attributeOffset, TOUGHNESS_KEY, EquipmentSlotGroup.ARMOR);
            }
        }

        return result;
    }

    /**
     * 将等级加成合并到原版默认的 ADD_NUMBER 修饰符中
     * 如果找到原版默认修饰符 直接修改其数值
     * 如果没有原版默认修饰符 则作为独立修饰符添加
     */
    private static void mergeIntoDefault(
            Multimap<Attribute, AttributeModifier> modifiers,
            Attribute attribute, double offset,
            NamespacedKey fallbackKey, EquipmentSlotGroup fallbackSlot) {

        Collection<AttributeModifier> existing = modifiers.get(attribute);
        // 查找原版默认的 ADD_NUMBER 修饰符进行合并
        AttributeModifier target = null;
        for (AttributeModifier mod : existing) {
            if (mod.getOperation() == AttributeModifier.Operation.ADD_NUMBER) {
                target = mod;
                break;
            }
        }

        if (target != null) {
            // 替换原版修饰符 数值叠加
            modifiers.remove(attribute, target);
            modifiers.put(attribute, new AttributeModifier(
                    target.getKey(),
                    target.getAmount() + offset,
                    target.getOperation(),
                    target.getSlotGroup()
            ));
        } else {
            // 无原版默认值 作为独立修饰符添加
            modifiers.put(attribute, new AttributeModifier(
                    fallbackKey, offset,
                    AttributeModifier.Operation.ADD_NUMBER,
                    fallbackSlot
            ));
        }
    }

    // 移除等级属性
    public static void removeGradeProperties(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return;
        if (ItemBlacklist.isBlacklisted(item.getType())) return;

        // 检查是否有品级
        GradeType grade = NBTUtil.getGrade(item);
        if (grade == null) return;

        Material material = item.getType();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        // 构建不含等级加成的原版默认属性列表
        Multimap<Attribute, AttributeModifier> modifiers = buildModifiers(material, meta, 0);
        meta.setAttributeModifiers(modifiers);

        // 移除自定义最大耐久 NBT
        NamespacedKey maxDuraKey = new NamespacedKey("equipmentgrade", NBTUtil.NBT_KEY_CUSTOM_MAX_DURABILITY);
        meta.getPersistentDataContainer().remove(maxDuraKey);

        item.setItemMeta(meta);
    }

    // 获取物品属性偏移值
    public static double getAppliedAttributeOffset(ItemStack item) {
        GradeType grade = NBTUtil.getGrade(item);
        if (grade == null) return 0;
        if (grade == GradeType.UNKNOWN) {
            return NBTUtil.getCustomAttribute(item);
        }
        return grade.getAttributeOffset();
    }

    // 获取物品耐久偏移值
    public static int getAppliedDurabilityOffset(ItemStack item) {
        GradeType grade = NBTUtil.getGrade(item);
        if (grade == null) return 0;
        if (grade == GradeType.UNKNOWN) {
            return NBTUtil.getCustomDurability(item);
        }
        int offset = grade.getDurabilityOffset();
        if (grade.getAttributeOffset() < 0) {
            offset = -offset;
        }
        return offset;
    }

    // 检查物品是否需要处理
    public static boolean isDurableItem(ItemStack item) {
        if (item == null) return false;
        return item.getType().getMaxDurability() > 0;
    }

    /**
     * 判断物品是否为远程武器
     */
    private static boolean isRangedWeapon(Material material) {
        return material == Material.BOW || material == Material.CROSSBOW;
    }

    // 计算物品的属性加成
    public static double getAttributeBonus(ItemStack item) {
        if (item == null) return 0;
        return getAppliedAttributeOffset(item);
    }
}
