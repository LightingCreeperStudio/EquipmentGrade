package net.LightingCreeperStudio.grade;

import com.google.common.collect.Multimap;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * 物品分类检测器
 * 通过检测物品已有的属性修饰符(NBT标签)来判断物品类型
 */
public class EquipmentType {

    /**
     * 攻击类 - 有攻击力属性
     */
    public static final String TYPE_WEAPON = "WEAPON";
    
    /**
     * 防御类 - 有护甲值属性
     */
    public static final String TYPE_ARMOR = "ARMOR";
    
    /**
     * 其他类 - 无战斗属性
     */
    public static final String TYPE_OTHER = "OTHER";

    /**
     * 根据物品已有的属性修饰符判断物品类型
     * @param item 物品
     * @return 装备类型
     */
    public static String getType(ItemStack item) {
        if (item == null) return TYPE_OTHER;
        
        Material material = item.getType();
        
        // 优先通过物品已有的属性修饰符判断
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Multimap<Attribute, org.bukkit.attribute.AttributeModifier> modifiers = meta.getAttributeModifiers();
            if (modifiers != null && !modifiers.isEmpty()) {
                // 检测是否有攻击伤害属性
                if (modifiers.containsKey(Attribute.ATTACK_DAMAGE)) {
                    return TYPE_WEAPON;
                }
                // 检测是否有护甲属性
                if (modifiers.containsKey(Attribute.ARMOR)) {
                    return TYPE_ARMOR;
                }
            }
        }
        
        // 通过原版默认属性修饰符判断
        Multimap<Attribute, org.bukkit.attribute.AttributeModifier> defaults = material.getDefaultAttributeModifiers();
        if (defaults != null && !defaults.isEmpty()) {
            if (defaults.containsKey(Attribute.ATTACK_DAMAGE)) {
                return TYPE_WEAPON;
            }
            if (defaults.containsKey(Attribute.ARMOR)) {
                return TYPE_ARMOR;
            }
        }
        
        // 最后按物品材质名判断
        return getTypeByMaterial(material);
    }

    /**
     * 根据物品材质判断类型
     */
    private static String getTypeByMaterial(Material material) {
        // 原版武器
        if (isVanillaWeapon(material)) {
            return TYPE_WEAPON;
        }
        
        // 原版盔甲
        if (isVanillaArmor(material)) {
            return TYPE_ARMOR;
        }
        
        // 其他
        return TYPE_OTHER;
    }

    /**
     * 检查是否为原版武器
     */
    private static boolean isVanillaWeapon(Material material) {
        return material == Material.WOODEN_SWORD 
            || material == Material.GOLDEN_SWORD
            || material == Material.STONE_SWORD
            || material == Material.IRON_SWORD
            || material == Material.DIAMOND_SWORD
            || material == Material.NETHERITE_SWORD
            || material == Material.BOW
            || material == Material.CROSSBOW
            || material == Material.TRIDENT
            || isSpear(material);
    }

    /**
     * 检查是否为矛
     */
    private static boolean isSpear(Material m) {
        return m == Material.WOODEN_SPEAR || m == Material.STONE_SPEAR
            || m == Material.IRON_SPEAR || m == Material.GOLDEN_SPEAR
            || m == Material.DIAMOND_SPEAR || m == Material.NETHERITE_SPEAR
            || m == Material.COPPER_SPEAR;
    }

    /**
     * 检查是否为原版盔甲
     */
    private static boolean isVanillaArmor(Material material) {
        // 头盔
        if (material == Material.LEATHER_HELMET 
            || material == Material.GOLDEN_HELMET
            || material == Material.CHAINMAIL_HELMET
            || material == Material.IRON_HELMET
            || material == Material.DIAMOND_HELMET
            || material == Material.NETHERITE_HELMET
            || material == Material.TURTLE_HELMET) {
            return true;
        }
        
        // 胸甲
        if (material == Material.LEATHER_CHESTPLATE 
            || material == Material.GOLDEN_CHESTPLATE
            || material == Material.CHAINMAIL_CHESTPLATE
            || material == Material.IRON_CHESTPLATE
            || material == Material.DIAMOND_CHESTPLATE
            || material == Material.NETHERITE_CHESTPLATE
            || material == Material.ELYTRA) {
            return true;
        }
        
        // 护腿
        if (material == Material.LEATHER_LEGGINGS 
            || material == Material.GOLDEN_LEGGINGS
            || material == Material.CHAINMAIL_LEGGINGS
            || material == Material.IRON_LEGGINGS
            || material == Material.DIAMOND_LEGGINGS
            || material == Material.NETHERITE_LEGGINGS) {
            return true;
        }
        
        // 靴子
        if (material == Material.LEATHER_BOOTS 
            || material == Material.GOLDEN_BOOTS
            || material == Material.CHAINMAIL_BOOTS
            || material == Material.IRON_BOOTS
            || material == Material.DIAMOND_BOOTS
            || material == Material.NETHERITE_BOOTS) {
            return true;
        }
        
        // 盾牌
        return material == Material.SHIELD;
    }

    /**
     * 检查是否应该应用战斗属性
     */
    public static boolean shouldApplyAttributes(ItemStack item) {
        String type = getType(item);
        return TYPE_WEAPON.equals(type) || TYPE_ARMOR.equals(type);
    }
}
