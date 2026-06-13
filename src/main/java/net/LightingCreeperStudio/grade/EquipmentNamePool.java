package net.LightingCreeperStudio.grade;

import org.bukkit.Material;

import java.util.List;
import java.util.Random;

/**
 * 装备命名池
 * 根据物品 Material 分类 提供修饰词(A)和本体词(B)的取名池
 * 完美品级及以上的装备自动从池中抽取 A+B 组合命名
 */
public class EquipmentNamePool {

    private static final Random random = new Random();

    // ==================== 剑 ====================
    private static final List<String> SWORD_A = List.of(
        "暗影", "泯灭", "疾风", "寒霜", "熔岩", "星辰", "幽影", "裂魂", "破晓", "狂怒",
        "荒芜", "流光", "赤焰", "苍雷", "幻尘"
    );
    private static final List<String> SWORD_B = List.of("剑", "刃", "锋", "灵剑", "重刃");

    // ==================== 斧 ====================
    private static final List<String> AXE_A = List.of(
        "断岳", "碎岩", "凶煞", "狂啸", "沉铁", "荒土", "凛风", "裂地", "焚风", "暗蚀",
        "惊雷", "厚甲", "野魂", "断山", "浊浪"
    );
    private static final List<String> AXE_B = List.of("斧", "战斧", "巨斧", "狂斧", "裂斧");

    // ==================== 三叉戟 ====================
    private static final List<String> TRIDENT_A = List.of(
        "闪电", "疾风", "穿刺", "破风", "噬魂", "龙鳞", "寒晶", "赤血", "幽影", "破晓",
        "毒蛇", "烈风", "霜牙", "火舌", "星光", "月影", "死神", "战争", "永恒", "尖牙",
        "破空", "斩月", "追魂", "破甲", "穿云", "裂风", "血牙", "暗影", "圣光", "毁灭"
    );
    private static final List<String> TRIDENT_B = List.of(
        "三叉戟", "海神戟", "深渊戟", "裂波戟", "破浪戟", "龙戟", "风暴戟", "寒渊戟", "怒潮戟", "潮汐戟",
        "噬海戟", "冰霜戟", "雷霆戟", "暗流戟", "碧波戟", "血戟", "魂戟", "圣戟", "魔戟", "灭神戟",
        "流星戟", "残月戟", "惊雷戟", "寒星戟", "烈焰戟", "幽冥戟", "苍穹戟", "镇海戟", "斩浪戟", "裂空戟"
    );

    // ==================== 矛 ====================
    private static final List<String> SPEAR_A = List.of(
        "闪电", "疾风", "穿刺", "破风", "噬魂", "龙鳞", "寒晶", "赤血", "幽影", "破晓",
        "毒蛇", "烈风", "霜牙", "火舌", "星光", "月影", "死神", "战争", "永恒", "尖牙",
        "破空", "斩月", "追魂", "破甲", "穿云", "裂风", "血牙", "暗影", "圣光", "毁灭"
    );
    private static final List<String> SPEAR_B = List.of(
        "长矛", "战矛", "长枪", "尖枪", "穿刺", "突刺", "锋刃", "獠牙", "龙枪", "风枪",
        "血矛", "魂矛", "圣矛", "魔矛", "破甲", "穿云", "裂风", "追魂", "灭神", "破空",
        "流星", "残月", "惊雷", "寒星", "烈焰", "冰霜", "暗影", "光明", "死神", "战神"
    );

    // ==================== 重锤（MACE） ====================
    private static final List<String> MACE_A = List.of(
        "雷霆", "暴风", "碎岩", "裂山", "陨星", "黑铁", "赤焰", "寒霜", "暗影", "幽能",
        "铁血", "圣辉", "诅咒", "不朽", "巨人", "熔炉", "符文", "狂怒", "深渊", "末日",
        "钢骨", "碎颅", "震地", "焚风", "冻骨", "无光", "噬魂", "破法", "斩铁", "灭世"
    );
    private static final List<String> MACE_B = List.of(
        "巨锤", "战锤", "重锤", "铁砧", "碎击", "碾压", "重击", "壁垒", "山崩", "岩碎",
        "雷霆", "风暴", "熔炉", "要塞", "铁拳", "碎星", "陨击", "骨锤", "血锤", "魂锤",
        "壁垒", "镇岳", "开山", "破甲", "撼地", "轰天", "断岳", "裂石", "碎魂", "灭神"
    );

    // ==================== 弓 ====================
    private static final List<String> BOW_A = List.of(
        "穿云", "逐月", "风啸", "落星", "寒羽", "暗月", "疾影", "鸣风", "焚羽", "静幽",
        "飞霜", "破空", "残阳", "轻羽", "雾隐"
    );
    private static final List<String> BOW_B = List.of("弓", "长弓", "劲弓", "猎弓", "月弓");

    // ==================== 弩 ====================
    private static final List<String> CROSSBOW_A = List.of(
        "破甲", "追魂", "疾射", "寒锋", "暗袭", "穿甲", "瞬影", "冷芒", "封喉", "锐芒",
        "沉影", "速击", "凝寒", "断风", "诡影"
    );
    private static final List<String> CROSSBOW_B = List.of("弩", "劲弩", "连弩", "锐弩", "暗弩");

    // ==================== 镐 ====================
    private static final List<String> PICKAXE_A = List.of(
        "掘地", "晶芒", "裂石", "深岩", "陨铁", "坚石", "洞幽", "凿空", "赤岩", "玄石",
        "碎晶", "地脉", "寒岩", "沉晶", "破矿"
    );
    private static final List<String> PICKAXE_B = List.of("镐", "岩镐", "晶镐", "裂地镐", "矿镐");

    // ==================== 铲 ====================
    private static final List<String> SHOVEL_A = List.of(
        "流沙", "疾行", "冻土", "飞尘", "卷土", "寒沙", "疾风", "浮尘", "荒沙", "软泥",
        "掠影", "积霜", "扬风", "沉沙", "碎土"
    );
    private static final List<String> SHOVEL_B = List.of("铲", "疾铲", "寒铲", "流沙铲", "风铲");

    // ==================== 锄 ====================
    private static final List<String> HOE_A = List.of(
        "沃土", "枯荣", "青禾", "灵壤", "润风", "荒田", "青芜", "沐风", "春泥", "幽壤",
        "生息", "枯木", "柔风", "翠影", "浅泥"
    );
    private static final List<String> HOE_B = List.of("锄", "灵锄", "田锄", "幽锄", "青锄");

    // ==================== 头盔 ====================
    private static final List<String> HELMET_A = List.of(
        "镇颅", "御魂", "龙纹", "寒钢", "圣辉", "暗甲", "坚颅", "凌顶", "霜纹", "火纹",
        "玄纹", "铁壁", "幽光", "金芒", "墨纹"
    );
    private static final List<String> HELMET_B = List.of("头盔", "战盔", "兜鍪", "灵盔", "龙盔");

    // ==================== 胸甲 ====================
    private static final List<String> CHESTPLATE_A = List.of(
        "凝铠", "固甲", "鳞纹", "厚盾", "炎甲", "冰甲", "玄铠", "重甲", "轻铠", "魔纹",
        "铁纹", "云纹", "苍甲", "坚甲", "幻甲"
    );
    private static final List<String> CHESTPLATE_B = List.of("胸甲", "战甲", "鳞甲", "玄甲", "重甲");

    // ==================== 护腿 ====================
    private static final List<String> LEGGINGS_A = List.of(
        "稳足", "踏地", "疾行", "韧甲", "寒纹", "风纹", "铁箍", "灵纹", "暗甲", "踏云",
        "沉甲", "劲骨", "霜甲", "奔行", "岩纹"
    );
    private static final List<String> LEGGINGS_B = List.of("护腿", "腿甲", "战腿", "鳞腿", "劲腿");

    // ==================== 靴子 ====================
    private static final List<String> BOOTS_A = List.of(
        "踏风", "逐影", "疾步", "踏云", "飞霜", "掠影", "奔雷", "轻踏", "寒足", "焰履",
        "云履", "迅步", "幻步", "尘影", "冰履"
    );
    private static final List<String> BOOTS_B = List.of("靴子", "战靴", "飞靴", "踏靴", "足履");

    // ==================== 盾牌 ====================
    private static final List<String> SHIELD_A = List.of(
        "壁垒", "镇御", "镇魂", "坚壁", "圣防", "暗盾", "铁墙", "守御", "寒壁", "火障",
        "玄壁", "厚盾", "灵障", "钢盾", "影壁"
    );
    private static final List<String> SHIELD_B = List.of("盾牌", "巨盾", "战盾", "壁垒", "镇盾");

    // ==================== 鞘翅 ====================
    private static final List<String> ELYTRA_A = List.of(
        "凌云", "逐风", "掠空", "星翔", "幽飞", "幻翼", "乘风", "逐月", "冥翼", "云翔",
        "疾飞", "流风", "苍翼", "夜翔", "光翼"
    );
    private static final List<String> ELYTRA_B = List.of("鞘翅", "飞翼", "灵翼", "风翼", "幻翼");

    // ==================== 钓鱼竿 ====================
    private static final List<String> FISHING_ROD_A = List.of(
        "寻渊", "引波", "垂星", "幽钓", "灵弦", "浮波", "寻踪", "清弦", "渊弦", "星垂",
        "风弦", "暗渊", "流波", "静钓", "云弦"
    );
    private static final List<String> FISHING_ROD_B = List.of("钓竿", "鱼竿", "灵竿", "渊竿", "星竿");

    /**
     * 根据 Material 获取对应的修饰词池(A)
     */
    public static List<String> getModifierPool(Material material) {
        if (isSword(material)) return SWORD_A;
        if (isAxe(material)) return AXE_A;
        if (material == Material.TRIDENT) return TRIDENT_A;
        if (isSpear(material)) return SPEAR_A;
        if (material == Material.MACE) return MACE_A;
        if (material == Material.BOW) return BOW_A;
        if (material == Material.CROSSBOW) return CROSSBOW_A;
        if (isPickaxe(material)) return PICKAXE_A;
        if (isShovel(material)) return SHOVEL_A;
        if (isHoe(material)) return HOE_A;
        if (isHelmet(material)) return HELMET_A;
        if (isChestplate(material)) return CHESTPLATE_A;
        if (isLeggings(material)) return LEGGINGS_A;
        if (isBoots(material)) return BOOTS_A;
        if (material == Material.SHIELD) return SHIELD_A;
        if (material == Material.ELYTRA) return ELYTRA_A;
        if (material == Material.FISHING_ROD) return FISHING_ROD_A;
        return null;
    }

    /**
     * 根据 Material 获取对应的本体词池(B)
     */
    public static List<String> getBodyPool(Material material) {
        if (isSword(material)) return SWORD_B;
        if (isAxe(material)) return AXE_B;
        if (material == Material.TRIDENT) return TRIDENT_B;
        if (isSpear(material)) return SPEAR_B;
        if (material == Material.MACE) return MACE_B;
        if (material == Material.BOW) return BOW_B;
        if (material == Material.CROSSBOW) return CROSSBOW_B;
        if (isPickaxe(material)) return PICKAXE_B;
        if (isShovel(material)) return SHOVEL_B;
        if (isHoe(material)) return HOE_B;
        if (isHelmet(material)) return HELMET_B;
        if (isChestplate(material)) return CHESTPLATE_B;
        if (isLeggings(material)) return LEGGINGS_B;
        if (isBoots(material)) return BOOTS_B;
        if (material == Material.SHIELD) return SHIELD_B;
        if (material == Material.ELYTRA) return ELYTRA_B;
        if (material == Material.FISHING_ROD) return FISHING_ROD_B;
        return null;
    }

    /**
     * 从取名池中随机抽取 A+B 组合名称
     * @param material 物品材质
     * @return 组合名称 该材质没有取名池则返回 null
     */
    public static String rollName(Material material) {
        List<String> poolA = getModifierPool(material);
        List<String> poolB = getBodyPool(material);
        if (poolA == null || poolB == null) return null;

        String a = poolA.get(random.nextInt(poolA.size()));
        String b = poolB.get(random.nextInt(poolB.size()));
        return a + b;
    }

    /**
     * 检查 Material 是否有对应的取名池
     */
    public static boolean hasNamePool(Material material) {
        return getModifierPool(material) != null;
    }

    // ==================== 材质分类 ====================

    private static boolean isSword(Material m) {
        return m == Material.WOODEN_SWORD || m == Material.GOLDEN_SWORD
            || m == Material.STONE_SWORD || m == Material.IRON_SWORD
            || m == Material.DIAMOND_SWORD || m == Material.NETHERITE_SWORD;
    }

    private static boolean isAxe(Material m) {
        return m == Material.WOODEN_AXE || m == Material.GOLDEN_AXE
            || m == Material.STONE_AXE || m == Material.IRON_AXE
            || m == Material.DIAMOND_AXE || m == Material.NETHERITE_AXE;
    }

    private static boolean isPickaxe(Material m) {
        return m == Material.WOODEN_PICKAXE || m == Material.GOLDEN_PICKAXE
            || m == Material.STONE_PICKAXE || m == Material.IRON_PICKAXE
            || m == Material.DIAMOND_PICKAXE || m == Material.NETHERITE_PICKAXE;
    }

    private static boolean isShovel(Material m) {
        return m == Material.WOODEN_SHOVEL || m == Material.GOLDEN_SHOVEL
            || m == Material.STONE_SHOVEL || m == Material.IRON_SHOVEL
            || m == Material.DIAMOND_SHOVEL || m == Material.NETHERITE_SHOVEL;
    }

    private static boolean isHoe(Material m) {
        return m == Material.WOODEN_HOE || m == Material.GOLDEN_HOE
            || m == Material.STONE_HOE || m == Material.IRON_HOE
            || m == Material.DIAMOND_HOE || m == Material.NETHERITE_HOE;
    }

    private static boolean isHelmet(Material m) {
        return m == Material.LEATHER_HELMET || m == Material.GOLDEN_HELMET
            || m == Material.CHAINMAIL_HELMET || m == Material.IRON_HELMET
            || m == Material.DIAMOND_HELMET || m == Material.NETHERITE_HELMET
            || m == Material.TURTLE_HELMET;
    }

    private static boolean isChestplate(Material m) {
        return m == Material.LEATHER_CHESTPLATE || m == Material.GOLDEN_CHESTPLATE
            || m == Material.CHAINMAIL_CHESTPLATE || m == Material.IRON_CHESTPLATE
            || m == Material.DIAMOND_CHESTPLATE || m == Material.NETHERITE_CHESTPLATE;
    }

    private static boolean isLeggings(Material m) {
        return m == Material.LEATHER_LEGGINGS || m == Material.GOLDEN_LEGGINGS
            || m == Material.CHAINMAIL_LEGGINGS || m == Material.IRON_LEGGINGS
            || m == Material.DIAMOND_LEGGINGS || m == Material.NETHERITE_LEGGINGS;
    }

    private static boolean isBoots(Material m) {
        return m == Material.LEATHER_BOOTS || m == Material.GOLDEN_BOOTS
            || m == Material.CHAINMAIL_BOOTS || m == Material.IRON_BOOTS
            || m == Material.DIAMOND_BOOTS || m == Material.NETHERITE_BOOTS;
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
}
