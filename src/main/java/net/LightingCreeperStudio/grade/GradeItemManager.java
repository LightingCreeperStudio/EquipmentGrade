package net.LightingCreeperStudio.grade;

import net.LightingCreeperStudio.listener.ItemGradeListener;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * 物品等级管理器
 * 提供简洁的API来给物品添加等级NBT标签和属性
 * 
 * 使用方式:
 *   GradeItemManager.applyGrade(Material.DIAMOND_SWORD, GradeType.HIGH);
 *   GradeItemManager.applyGradeToHand(player, GradeType.MEDIUM);
 */
public class GradeItemManager {

    // NBT标签键名
    public static final String NBT_KEY = "EquipmentGrade";
    
    // 注册的物品配置缓存: Material -> GradeConfig
    private static final Map<Material, GradeConfig> registeredItems = new HashMap<>();

    // 装备属性缓存: Material -> 是否为装备
    private static final Map<Material, Boolean> equipmentCache = new EnumMap<>(Material.class);

    /**
     * 物品等级配置
     */
    public static class GradeConfig {
        private final GradeType grade;
        private final int customAttribute;
        private final int customDurability;

        public GradeConfig(GradeType grade, int customAttribute, int customDurability) {
            this.grade = grade;
            this.customAttribute = customAttribute;
            this.customDurability = customDurability;
        }

        public GradeType getGrade() {
            return grade;
        }

        public int getCustomAttribute() {
            return customAttribute;
        }

        public int getCustomDurability() {
            return customDurability;
        }

        public double getAttributeOffset() {
            if (grade == GradeType.UNKNOWN) {
                return customAttribute;
            }
            return grade.getAttributeOffset();
        }

        public int getDurabilityOffset() {
            if (grade == GradeType.UNKNOWN) {
                return customDurability;
            }
            return grade.getDurabilityOffset();
        }
    }

    // ==================== 核心NBT操作 ====================

    /**
     * 给物品设置等级NBT标签
     * @param item 目标物品
     * @param grade 目标等级
     */
    public static void setGradeNBT(ItemStack item, GradeType grade) {
        setGradeNBT(item, grade, 0, 0);
    }

    /**
     * 给物品设置等级NBT标签
     * @param item 目标物品
     * @param grade 目标等级
     * @param customAttribute 自定义属性值
     * @param customDurability 自定义耐久值
     */
    public static void setGradeNBT(ItemStack item, GradeType grade, int customAttribute, int customDurability) {
        if (item == null || item.getType() == Material.AIR) return;

        // 设置等级标签
        NBTUtil.setGrade(item, grade);

        // UNKNOWN等级 设置自定义属性
        if (grade == GradeType.UNKNOWN) {
            NBTUtil.setCustomAttribute(item, customAttribute);
            NBTUtil.setCustomDurability(item, customDurability);
        }
    }

    /**
     * 获取物品的等级NBT
     * @param item 目标物品
     * @return 等级类型 如果没有则返回null
     */
    public static GradeType getGradeNBT(ItemStack item) {
        return NBTUtil.getGrade(item);
    }

    /**
     * 检查物品是否有等级NBT
     * @param item 目标物品
     * @return 是否有等级
     */
    public static boolean hasGradeNBT(ItemStack item) {
        return NBTUtil.hasGrade(item);
    }

    /**
     * 移除物品的等级NBT
     * @param item 目标物品
     */
    public static void removeGradeNBT(ItemStack item) {
        NBTUtil.removeGrade(item);
    }

    // ==================== 属性应用 ====================

    /**
     * 应用等级属性到物品
     * 检测原有NBT值 如果为空则添加 否则累加属性
     * @param item 目标物品
     * @param grade 目标等级
     */
    public static void applyGrade(ItemStack item, GradeType grade) {
        applyGrade(item, grade, 0, 0);
    }

    /**
     * 应用等级属性到物品
     * 检测原有NBT值 如果为空则添加 否则累加属性
     * @param item 目标物品
     * @param grade 目标等级
     * @param customAttribute 自定义属性值
     * @param customDurability 自定义耐久值
     */
    public static void applyGrade(ItemStack item, GradeType grade, int customAttribute, int customDurability) {
        if (item == null || item.getType() == Material.AIR) return;

        // 计算目标属性和耐久值
        double targetAttribute = (grade == GradeType.UNKNOWN) ? customAttribute : grade.getAttributeOffset();
        int targetDurability = (grade == GradeType.UNKNOWN) ? customDurability : grade.getDurabilityOffset();

        // 检测原有NBT值并累加
        double finalAttribute = targetAttribute;
        int finalDurability = targetDurability;

        if (grade == GradeType.UNKNOWN) {
            // UNKNOWN等级:累加自定义属性和耐久
            if (hasGradeNBT(item)) {
                GradeType currentGrade = getGradeNBT(item);
                if (currentGrade == GradeType.UNKNOWN) {
                    // 原物品也是UNKNOWN 累加属性值
                    finalAttribute += NBTUtil.getCustomAttribute(item);
                    finalDurability += NBTUtil.getCustomDurability(item);
                } else {
                    // 原物品是其他等级 转换为UNKNOWN并累加原属性
                    finalAttribute += currentGrade.getAttributeOffset();
                    finalDurability += currentGrade.getDurabilityOffset();
                }
            }
        } else {
            // 固定等级:累加属性偏移值
            if (hasGradeNBT(item)) {
                GradeType currentGrade = getGradeNBT(item);
                if (currentGrade == GradeType.UNKNOWN) {
                    // 原物品是UNKNOWN 先获取其自定义属性
                    finalAttribute += NBTUtil.getCustomAttribute(item);
                    finalDurability += NBTUtil.getCustomDurability(item);
                } else {
                    // 累加属性偏移
                    finalAttribute += currentGrade.getAttributeOffset();
                    finalDurability += currentGrade.getDurabilityOffset();
                }
            }
        }

        // 设置NBT标签
        setGradeNBT(item, grade, (int) finalAttribute, finalDurability);

        // 应用属性修改和lore更新
        ItemPropertyModifier.applyGradeProperties(item);
        ItemGradeListener.addGradeLore(item);

        // 自动命名+ 品级变更处理
        EquipmentNamer.tryAutoName(item, grade);
    }

    /**
     * 设置物品等级
     * @param item 目标物品
     * @param grade 目标等级
     */
    public static void setGradeOverwrite(ItemStack item, GradeType grade) {
        setGradeOverwrite(item, grade, 0, 0);
    }

    /**
     * 设置物品等级
     * @param item 目标物品
     * @param grade 目标等级
     * @param customAttribute 自定义属性值
     * @param customDurability 自定义耐久值
     */
    public static void setGradeOverwrite(ItemStack item, GradeType grade, int customAttribute, int customDurability) {
        if (item == null || item.getType() == Material.AIR) return;

        // 保存现有装备名
        String existingEquipName = NBTUtil.getEquipmentName(item);

        // 先移除旧的NBT
        NBTUtil.removeGrade(item);

        // 设置新的NBT标签
        setGradeNBT(item, grade, customAttribute, customDurability);

        // 恢复装备名（
        if (existingEquipName != null) {
            NBTUtil.setEquipmentName(item, existingEquipName);
        }

        // 应用属性修改和lore更新
        ItemPropertyModifier.applyGradeProperties(item);
        ItemGradeListener.addGradeLore(item);

        // 自动命名 + 品级变更处理
        EquipmentNamer.tryAutoName(item, grade);
    }

    // ==================== 物品ID操作 ====================

    /**
     * 根据物品ID获取Material
     * @param itemId 物品ID
     * @return Material 如果没有则返回null
     */
    public static Material getMaterialFromId(String itemId) {
        if (itemId == null || itemId.isEmpty()) return null;

        String id = itemId.toLowerCase();
        if (id.contains(":")) {
            String[] parts = id.split(":", 2);
            if (!parts[0].equals("minecraft") && !parts[0].equals("eg")) {
                return null; // 不支持其他命名空间
            }
            id = parts[1];
        }

        try {
            return Material.valueOf(id.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 给指定物品ID的所有实例设置等级
     * 带有此物品类型的物品会自动应用等级属性
     * 
     * @param itemId 物品ID
     * @param grade 目标等级
     */
    public static void registerItemGrade(String itemId, GradeType grade) {
        registerItemGrade(itemId, grade, 0, 0);
    }

    /**
     * 给指定物品ID的所有实例设置等级
     * 
     * @param itemId 物品ID
     * @param grade 目标等级
     * @param customAttribute 自定义属性值
     * @param customDurability 自定义耐久值
     */
    public static void registerItemGrade(String itemId, GradeType grade, int customAttribute, int customDurability) {
        Material material = getMaterialFromId(itemId);
        if (material == null) {
            throw new IllegalArgumentException("无效的物品ID: " + itemId);
        }
        
        registeredItems.put(material, new GradeConfig(grade, customAttribute, customDurability));
    }

    /**
     * 移除物品ID的等级配置
     * @param itemId 物品ID
     */
    public static void unregisterItemGrade(String itemId) {
        Material material = getMaterialFromId(itemId);
        if (material != null) {
            registeredItems.remove(material);
        }
    }

    /**
     * 检查物品ID是否已注册等级配置
     * @param itemId 物品ID
     * @return 是否已注册
     */
    public static boolean isItemRegistered(String itemId) {
        Material material = getMaterialFromId(itemId);
        return material != null && registeredItems.containsKey(material);
    }

    /**
     * 获取物品的等级配置
     * @param item 物品
     * @return 等级配置 如果没有则返回null
     */
    public static GradeConfig getGradeConfig(ItemStack item) {
        if (item == null) return null;
        return registeredItems.get(item.getType());
    }

    /**
     * 对玩家的主手物品应用注册的等级
     * @param player 玩家
     */
    public static void applyRegisteredGradeToMainHand(Player player) {
        applyRegisteredGrade(player.getInventory().getItemInMainHand(), player);
    }

    /**
     * 对玩家的副手物品应用注册的等级
     * @param player 玩家
     */
    public static void applyRegisteredGradeToOffHand(Player player) {
        applyRegisteredGrade(player.getInventory().getItemInOffHand(), player);
    }

    /**
     * 对玩家的主手和副手物品都应用注册的等级
     * @param player 玩家
     */
    public static void applyRegisteredGradeToBothHands(Player player) {
        applyRegisteredGradeToMainHand(player);
        applyRegisteredGradeToOffHand(player);
    }

    /**
     * 根据物品配置应用等级
     * @param item 物品
     * @param player 玩家
     */
    public static void applyRegisteredGrade(ItemStack item, Player player) {
        if (item == null || item.getType() == Material.AIR) return;

        GradeConfig config = registeredItems.get(item.getType());
        if (config == null) return;

        applyGrade(item, config.getGrade(), config.getCustomAttribute(), config.getCustomDurability());
    }

    // ==================== 便捷操作 ====================

    /**
     * 给玩家的盔甲栏所有物品应用注册的等级
     * @param player 玩家
     */
    public static void applyRegisteredGradeToArmor(Player player) {
        PlayerInventory inv = player.getInventory();
        
        ItemStack helmet = inv.getHelmet();
        ItemStack chestplate = inv.getChestplate();
        ItemStack leggings = inv.getLeggings();
        ItemStack boots = inv.getBoots();

        applyRegisteredGrade(helmet, player);
        applyRegisteredGrade(chestplate, player);
        applyRegisteredGrade(leggings, player);
        applyRegisteredGrade(boots, player);
    }

    /**
     * 提升物品等级
     * @param item 物品
     * @param steps 提升的等级数或降低的等级数
     * @return 新的等级
     */
    public static GradeType upgradeGrade(ItemStack item, int steps) {
        GradeType current = getGradeNBT(item);
        if (current == null) {
            current = GradeType.COMMON;
        }

        GradeType[] grades = GradeType.values();
        int currentIndex = current.ordinal();
        int newIndex = Math.max(0, Math.min(grades.length - 1, currentIndex + steps));
        
        GradeType newGrade = grades[newIndex];
        setGradeOverwrite(item, newGrade);
        
        return newGrade;
    }

    // ==================== 直接属性操作 ====================

    /**
     * 给物品添加属性值
     * 如果物品没有等级标签 自动创建UNKNOWN等级
     * @param item 目标物品
     * @param attributeValue 属性调整值
     * @param durabilityValue 耐久调整值
     */
    public static void addAttributeValue(ItemStack item, double attributeValue, int durabilityValue) {
        if (item == null || item.getType() == Material.AIR) return;

        double currentAttribute = 0;
        int currentDurability = 0;
        GradeType currentGrade = getGradeNBT(item);

        if (currentGrade != null) {
            if (currentGrade == GradeType.UNKNOWN) {
                currentAttribute = NBTUtil.getCustomAttribute(item);
                currentDurability = NBTUtil.getCustomDurability(item);
            } else {
                currentAttribute = currentGrade.getAttributeOffset();
                currentDurability = currentGrade.getDurabilityOffset();
            }
        }

        // 累加新值
        double newAttribute = currentAttribute + attributeValue;
        int newDurability = currentDurability + durabilityValue;

        // 如果物品没有等级 设置UNKNOWN等级
        if (currentGrade == null) {
            NBTUtil.setGrade(item, GradeType.UNKNOWN);
        }

        // 设置新的自定义属性
        NBTUtil.setCustomAttribute(item, (int) newAttribute);
        NBTUtil.setCustomDurability(item, newDurability);

        // 应用属性修改和lore更新
        ItemPropertyModifier.applyGradeProperties(item);
        ItemGradeListener.addGradeLore(item);

        // 自动命名（品级>=完美且未被命名过时触发）+ 品级变更处理
        EquipmentNamer.tryAutoName(item, currentGrade);
    }

    /**
     * 给物品设置属性值
     * @param item 目标物品
     * @param attributeValue 新的属性值
     * @param durabilityValue 新的耐久值
     */
    public static void setAttributeValue(ItemStack item, double attributeValue, int durabilityValue) {
        if (item == null || item.getType() == Material.AIR) return;

        // 设置UNKNOWN等级
        NBTUtil.setGrade(item, GradeType.UNKNOWN);
        NBTUtil.setCustomAttribute(item, (int) attributeValue);
        NBTUtil.setCustomDurability(item, durabilityValue);

        // 应用属性修改和lore更新
        ItemPropertyModifier.applyGradeProperties(item);
        ItemGradeListener.addGradeLore(item);

        // UNKNOWN 等级不自动命名
    }

    /**
     * 获取物品当前累计的属性值
     * @param item 目标物品
     * @return 当前属性值
     */
    public static double getCurrentAttribute(ItemStack item) {
        GradeType grade = getGradeNBT(item);
        if (grade == null) return 0;

        if (grade == GradeType.UNKNOWN) {
            return NBTUtil.getCustomAttribute(item);
        }
        return grade.getAttributeOffset();
    }

    /**
     * 获取物品当前累计的耐久值
     * @param item 目标物品
     * @return 当前耐久值
     */
    public static int getCurrentDurability(ItemStack item) {
        GradeType grade = getGradeNBT(item);
        if (grade == null) return 0;

        if (grade == GradeType.UNKNOWN) {
            return NBTUtil.getCustomDurability(item);
        }
        return grade.getDurabilityOffset();
    }

    /**
     * 获取所有已注册的物品
     * @return 注册物品的Material数组
     */
    public static Material[] getRegisteredItems() {
        return registeredItems.keySet().toArray(new Material[0]);
    }

    /**
     * 清除所有注册的物品配置
     */
    public static void clearRegisteredItems() {
        registeredItems.clear();
    }

    // ==================== 战利品箱等级随机 ====================

    /**
     * 根据结构ID掷骰获取随机等级
     *
     * @param structureId 结构ID
     * @return 随机等级
     */
    public static GradeType rollGrade(String structureId) {
        LootProbabilities probabilities = getProbabilities(structureId);
        return rollFromProbabilities(probabilities);
    }

    /**
     * 根据结构ID获取对应的概率分布
     */
    private static LootProbabilities getProbabilities(String structureId) {
        if (structureId == null) {
            return LootProbabilities.FALLBACK;
        }

        return switch (structureId.toLowerCase()) {
            // 简单结构
            case "village" -> LootProbabilities.VILLAGE;
            case "desert_well" -> LootProbabilities.DESERT_WELL;
            case "shipwreck_treasure", "shipwreck" -> LootProbabilities.SHIPWRECK;

            // 中等难度结构
            case "desert_pyramid", "desert_temple" -> LootProbabilities.DESERT_TEMPLE;
            case "jungle_temple" -> LootProbabilities.JUNGLE_TEMPLE;
            case "abandoned_mineshaft", "mineshaft" -> LootProbabilities.MINESHAFT;
            case "swamp_hut" -> LootProbabilities.SWAMP_HUT;
            case "igloo" -> LootProbabilities.IGLOO;
            case "pillager_outpost" -> LootProbabilities.PILLAGER_OUTPOST;

            // 较高难度结构
            case "stronghold_corridor", "stronghold", "stronghold_crossing" -> LootProbabilities.STRONGHOLD;
            case "stronghold_library" -> LootProbabilities.STRONGHOLD_LIBRARY;
            case "ocean_ruins" -> LootProbabilities.OCEAN_RUINS;
            case "sunken_ship" -> LootProbabilities.SUNKEN_SHIP;
            case "woodland_mansion" -> LootProbabilities.WOODLAND_MANSION;
            case "nether_fortress", "fortress" -> LootProbabilities.FORTRESS;
            case "buried_treasure" -> LootProbabilities.BURIED_TREASURE;
            case "simple_dungeon" -> LootProbabilities.SIMPLE_DUNGEON;

            // 高难度结构
            case "end_city" -> LootProbabilities.END_CITY;
            case "nether_fortress_high" -> LootProbabilities.NETHER_FORTRESS;
            case "ancient_city" -> LootProbabilities.ANCIENT_CITY;

            // 末地特殊结构
            case "end_ship" -> LootProbabilities.END_SHIP;
            case "end_stronghold" -> LootProbabilities.END_STRONGHOLD;

            default -> LootProbabilities.FALLBACK;
        };
    }

    /**
     * 根据概率分布掷骰获取等级
     */
    private static GradeType rollFromProbabilities(LootProbabilities probabilities) {
        if (probabilities == null) {
            probabilities = LootProbabilities.FALLBACK;
        }

        double roll = Math.random() * 100;
        double cumulative = 0;

        GradeType[] grades = {
            GradeType.WASTE, GradeType.DEFECTIVE, GradeType.UNRANKED, GradeType.COMMON,
            GradeType.LOW, GradeType.MEDIUM, GradeType.HIGH, GradeType.EXCELLENT,
            GradeType.PERFECT, GradeType.SPIRIT_TREASURE_1, GradeType.SPIRIT_TREASURE_2, GradeType.SPIRIT_TREASURE_3,
            GradeType.SUPREME_TREASURE_1, GradeType.SUPREME_TREASURE_2, GradeType.SUPREME_TREASURE_3,
            GradeType.CELESTIAL
        };

        for (int i = 0; i < grades.length; i++) {
            cumulative += probabilities.getProbability(grades[i]);
            if (roll < cumulative) {
                return grades[i];
            }
        }

        return GradeType.COMMON;
    }

    /**
     * 检查物品是否应该被赋予等级
     */
    public static boolean shouldGradeItem(ItemStack item) {
        if (item == null) return false;
        if (item.getType().isAir()) return false;
        if (hasGradeNBT(item)) return false;
        if (ItemBlacklist.isBlacklisted(item.getType())) return false;
        // 只有具有装备属性（攻击伤害、护甲等）的物品才参与品级系统
        return isEquipmentMaterial(item.getType());
    }

    /**
     * 检查材质是否为装备
     * 结果会被缓存 避免重复创建临时物品
     */
    private static boolean isEquipmentMaterial(Material material) {
        return equipmentCache.computeIfAbsent(material, GradeItemManager::checkEquipmentAttributes);
    }

    /**
     * 实际检查材质是否具有装备属性
     * 通过检查物品的默认属性修饰符来判断
     */
    private static boolean checkEquipmentAttributes(Material material) {
        // 特殊情况:弓/弩/盾牌/鞘翅没有默认攻击属性
        if (material == Material.BOW || material == Material.CROSSBOW
                || material == Material.SHIELD || material == Material.ELYTRA) {
            return true;
        }
        try {
            ItemStack temp = new ItemStack(material);
            if (!temp.hasItemMeta()) return false;
            ItemMeta meta = temp.getItemMeta();
            if (!meta.hasAttributeModifiers()) return false;
            for (Attribute attr : meta.getAttributeModifiers().keySet()) {
                if (attr == Attribute.ATTACK_DAMAGE
                        || attr == Attribute.ATTACK_SPEED
                        || attr == Attribute.ARMOR
                        || attr == Attribute.ARMOR_TOUGHNESS) {
                    return true;
                }
            }
        } catch (Exception e) {
            // 无效材质
        }
        return false;
    }

    // ==================== 合成来源等级随机 ====================

    /**
     * 根据来源类型掷骰获取随机等级
     *
     * @param source 来源类型
     * @return 随机等级
     */
    public static GradeType rollGradeFromSource(String source) {
        LootProbabilities probabilities = getSourceProbabilities(source);
        return rollFromProbabilities(probabilities);
    }

    /**
     * 根据来源类型获取对应的概率分布
     */
    private static LootProbabilities getSourceProbabilities(String source) {
        if (source == null) {
            return LootProbabilities.FALLBACK;
        }

        return switch (source.toLowerCase()) {
            case "crafting" -> LootProbabilities.CRAFTING;
            case "villager_trade", "villager" -> LootProbabilities.VILLAGER_TRADE;
            case "wandering_trade", "wandering_trader" -> LootProbabilities.WANDERING_TRADE;
            case "entity_drop", "entity", "mob_drop" -> LootProbabilities.ENTITY_DROP;
            case "fishing", "fish" -> LootProbabilities.FISHING;
            case "item_frame" -> LootProbabilities.ITEM_FRAME;
            case "command_random" -> LootProbabilities.COMMAND_RANDOM;
            default -> LootProbabilities.COMMAND_DEFAULT;  // 默认为凡品
        };
    }
}
