package net.LightingCreeperStudio.grade;

/**
 * Minecraft战利品箱结构类型枚举
 * 定义所有可配置的箱子类型及其对应的概率分布
 */
public enum LootStructureType {
    // ==================== 简单结构 ====================
    /** 村庄箱子 */
    VILLAGE("village", "村庄箱子", LootProbabilities.VILLAGE),
    /** 沙漠水井 */
    DESERT_WELL("desert_well", "沙漠水井", LootProbabilities.DESERT_WELL),
    /** 沉船宝箱 */
    SHIPWRECK_TREASURE("shipwreck_treasure", "沉船宝箱", LootProbabilities.SHIPWRECK),

    // ==================== 中等难度结构 ====================
    /** 沙漠神殿 */
    DESERT_PYRAMID("desert_pyramid", "沙漠神殿", LootProbabilities.DESERT_TEMPLE),
    /** 丛林神殿 */
    JUNGLE_TEMPLE("jungle_temple", "丛林神殿", LootProbabilities.JUNGLE_TEMPLE),
    /** 废弃矿井 */
    ABANDONED_MINESHAFT("abandoned_mineshaft", "废弃矿井", LootProbabilities.MINESHAFT),
    /** 沼泽小屋 */
    SWAMP_HUT("swamp_hut", "沼泽小屋", LootProbabilities.SWAMP_HUT),
    /** 雪屋 */
    IGLOO("igloo", "雪屋", LootProbabilities.IGLOO),
    /** 掠夺者前哨 */
    PILLAGER_OUTPOST("pillager_outpost", "掠夺者前哨", LootProbabilities.PILLAGER_OUTPOST),

    // ==================== 较高难度结构 ====================
    /** 要塞走廊 */
    STRONGHOLD_CORRIDOR("stronghold_corridor", "要塞走廊", LootProbabilities.STRONGHOLD),
    /** 要塞图书馆 */
    STRONGHOLD_LIBRARY("stronghold_library", "要塞图书馆", LootProbabilities.STRONGHOLD_LIBRARY),
    /** 要塞交叉口 */
    STRONGHOLD_CROSSING("stronghold_crossing", "要塞交叉口", LootProbabilities.STRONGHOLD),
    /** 海底遗迹 */
    OCEAN_RUINS("ocean_ruins", "海底遗迹", LootProbabilities.OCEAN_RUINS),
    /** 沉没的遗迹 */
    SUNKEN_SHIP("sunken_ship", "沉没的遗迹", LootProbabilities.SUNKEN_SHIP),
    /** 林地府邸 */
    WOODLAND_MANSION("woodland_mansion", "林地府邸", LootProbabilities.WOODLAND_MANSION),
    /** 诡异堡垒 */
    FORTRESS("fortress", "诡异堡垒", LootProbabilities.FORTRESS),
    /** 藏宝图宝藏 */
    BURIED_TREASURE("buried_treasure", "藏宝图宝藏", LootProbabilities.BURIED_TREASURE),
    /** 埋藏的宝藏 */
    SIMPLE_DUNGEON("simple_dungeon", "埋藏的宝藏", LootProbabilities.SIMPLE_DUNGEON),

    // ==================== 高难度结构 ====================
    /** 下界要塞 */
    NETHER_FORTRESS("nether_fortress", "下界要塞", LootProbabilities.NETHER_FORTRESS),
    /** 末地城 */
    END_CITY("end_city", "末地城", LootProbabilities.END_CITY),
    /** 远古城市 */
    ANCIENT_CITY("ancient_city", "远古城市", LootProbabilities.ANCIENT_CITY),

    // ==================== 末地特殊结构 ====================
    /** 末地船 */
    END_SHIP("end_ship", "末地船", LootProbabilities.END_SHIP),
    /** 末地要塞走廊 */
    END_STRONGHOLD("end_stronghold", "末地要塞走廊", LootProbabilities.END_STRONGHOLD),

    // ==================== 兜底配置 ====================
    /** 其他战利品箱 */
    FALLBACK("fallback", "其他战利品箱", LootProbabilities.FALLBACK);

    private final String chestId;
    private final String displayName;
    private final LootProbabilities probabilities;

    LootStructureType(String chestId, String displayName, LootProbabilities probabilities) {
        this.chestId = chestId;
        this.displayName = displayName;
        this.probabilities = probabilities;
    }

    public String getChestId() {
        return chestId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public LootProbabilities getProbabilities() {
        return probabilities;
    }

    /**
     * 根据箱子ID查找对应的结构类型
     */
    public static LootStructureType fromChestId(String chestId) {
        if (chestId == null) return FALLBACK;
        
        for (LootStructureType type : values()) {
            if (type.chestId.equalsIgnoreCase(chestId)) {
                return type;
            }
        }
        return FALLBACK;
    }
}
