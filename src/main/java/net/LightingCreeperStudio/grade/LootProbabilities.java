package net.LightingCreeperStudio.grade;

/**
 * 各结构类型的等级概率分布配置
 * 概率值范围 0-100，表示百分比
 */
public enum LootProbabilities {

    // 等级顺序: 废品, 残次品, 不入品, 凡品, 下品, 中品, 上品, 极品, 完美, 灵宝★, 灵宝★★, 灵宝★★★, 至宝★, 至宝★★, 至宝★★★, 先天

    // ==================== 简单结构 ====================
    
    /** 村庄箱子 - 基础简单资源 */
    VILLAGE(25, 15, 12, 18, 15, 8, 4, 2, 0.5, 0.3, 0.1, 0.05, 0.03, 0.02, 0, 0),
    
    /** 沙漠水井 */
    DESERT_WELL(25, 15, 12, 18, 15, 8, 4, 2, 0.5, 0.3, 0.1, 0.05, 0.03, 0.02, 0, 0),
    
    /** 沉船宝箱 - 海洋探索奖励 */
    SHIPWRECK(20, 15, 12, 18, 16, 10, 5, 2.5, 1, 0.3, 0.1, 0.05, 0.03, 0.02, 0, 0),

    // ==================== 中等难度结构 ====================
    
    /** 沙漠神殿 */
    DESERT_TEMPLE(15, 12, 10, 18, 18, 12, 8, 4, 1.5, 0.8, 0.4, 0.2, 0.08, 0.02, 0, 0),
    
    /** 丛林神殿 */
    JUNGLE_TEMPLE(15, 12, 10, 18, 18, 12, 8, 4, 1.5, 0.8, 0.4, 0.2, 0.08, 0.02, 0, 0),
    
    /** 废弃矿井 */
    MINESHAFT(18, 14, 12, 20, 16, 10, 6, 2.5, 1, 0.3, 0.1, 0.05, 0.03, 0.02, 0, 0),
    
    /** 沼泽小屋 */
    SWAMP_HUT(20, 15, 12, 20, 15, 10, 5, 2, 0.6, 0.25, 0.1, 0.05, 0, 0, 0, 0),
    
    /** 雪屋 */
    IGLOO(20, 15, 12, 20, 15, 10, 5, 2, 0.6, 0.25, 0.1, 0.05, 0, 0, 0, 0),
    
    /** 掠夺者前哨 */
    PILLAGER_OUTPOST(15, 12, 10, 18, 18, 12, 8, 4, 1.5, 0.8, 0.4, 0.2, 0.08, 0.02, 0, 0),

    // ==================== 较高难度结构 ====================
    
    /** 要塞走廊 */
    STRONGHOLD(10, 10, 10, 20, 18, 14, 10, 4, 2, 1, 0.5, 0.25, 0.15, 0.08, 0.02, 0),
    
    /** 要塞图书馆 - 额外奖励 */
    STRONGHOLD_LIBRARY(8, 8, 10, 18, 18, 16, 12, 5, 2.5, 1.5, 0.6, 0.25, 0.1, 0.05, 0, 0),
    
    /** 海底遗迹 */
    OCEAN_RUINS(12, 10, 10, 20, 18, 14, 10, 3, 1.5, 0.8, 0.4, 0.2, 0.08, 0.02, 0, 0),
    
    /** 沉没的遗迹 */
    SUNKEN_SHIP(10, 10, 10, 20, 18, 14, 10, 4, 2, 1, 0.5, 0.25, 0.15, 0.08, 0.02, 0),
    
    /** 林地府邸 */
    WOODLAND_MANSION(10, 10, 10, 20, 18, 14, 10, 4, 2, 1, 0.5, 0.25, 0.15, 0.08, 0.02, 0),
    
    /** 诡异堡垒 */
    FORTRESS(12, 10, 10, 20, 18, 14, 10, 3, 1.5, 0.8, 0.4, 0.2, 0.08, 0.02, 0, 0),
    
    /** 藏宝图宝藏 */
    BURIED_TREASURE(10, 10, 10, 20, 18, 14, 10, 4, 2, 1, 0.5, 0.25, 0.15, 0.08, 0.02, 0),
    
    /** 埋藏的宝藏 */
    SIMPLE_DUNGEON(12, 10, 10, 20, 18, 14, 10, 3, 1.5, 0.8, 0.4, 0.2, 0.08, 0.02, 0, 0),

    // ==================== 高难度结构 ====================
    
    /** 下界要塞 - 下界高价值奖励 */
    NETHER_FORTRESS(8, 8, 8, 18, 18, 16, 12, 6, 3, 1.5, 0.8, 0.4, 0.2, 0.1, 0.02, 0),
    
    /** 末地城 - 等同林地府邸 */
    END_CITY(10, 10, 10, 20, 18, 14, 10, 4, 2, 1, 0.5, 0.25, 0.15, 0.08, 0.02, 0),
    
    /** 远古城市 - 顶级难度，最高至宝★★★ */
    ANCIENT_CITY(5, 5, 8, 15, 18, 18, 14, 8, 4, 2, 1, 0.5, 0.3, 0.15, 0.05, 0.02),

    // ==================== 末地特殊结构 ====================
    
    /** 末地船 - 末地最珍贵奖励，有先天 */
    END_SHIP(5, 10, 12, 12, 13, 13, 12, 8, 5, 3.5, 2, 1, 0.3, 0.15, 0.05, 0.05),
    
    /** 末地要塞走廊 - 末地难度最高 */
    END_STRONGHOLD(3, 8, 10, 12, 13, 13, 12, 7, 4, 3, 2, 1, 0.4, 0.2, 0.08, 0.07),

    // ==================== 合成来源配置 ====================
    
    /** 原版工作台合成 - 限制较严格 */
    CRAFTING(15, 20, 25, 30, 8, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    
    /** 村民交易 - 奖励较好 */
    VILLAGER_TRADE(0, 0, 0, 5, 15, 25, 30, 20, 4, 0.9, 0.1, 0, 0, 0, 0, 0),
    
    /** 流浪商人交易 - 最低凡品 */
    WANDERING_TRADE(0, 0, 0, 18, 25, 25, 18, 9, 4, 1, 0, 0, 0, 0, 0, 0),
    
    /** 钓鱼 - 奖励一般，有机会钓到好东西 */
    FISHING(15, 18, 20, 25, 12, 6, 2.5, 1, 0.4, 0.08, 0.02, 0, 0, 0, 0, 0),
    
    /** 物品展示框 - 奖励较低 */
    ITEM_FRAME(20, 18, 15, 25, 12, 6, 2.5, 1, 0.4, 0.08, 0.02, 0, 0, 0, 0, 0),
    
    /** 生物战利品掉落 - 统一设定 */
    ENTITY_DROP(10, 15, 20, 30, 15, 7, 2, 0.8, 0.15, 0.05, 0, 0, 0, 0, 0, 0),
    
    /** 指令/give 默认 - 凡品 */
    COMMAND_DEFAULT(0, 0, 0, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    
    /** 指令/give 随机 - 随机概率分布 */
    COMMAND_RANDOM(0, 0, 0, 30, 25, 20, 15, 7, 2.5, 0.4, 0.1, 0, 0, 0, 0, 0),
    
    // ==================== 兜底配置 ====================
    
    /** 其他战利品箱 - 默认保守配置 */
    FALLBACK(20, 15, 12, 20, 15, 10, 5, 2, 0.6, 0.25, 0.1, 0.05, 0, 0, 0, 0);

    // 概率值
    private final double waste;           // 废品
    private final double defective;       // 残次品
    private final double unranked;       // 不入品
    private final double common;         // 凡品
    private final double low;            // 下品
    private final double medium;         // 中品
    private final double high;           // 上品
    private final double excellent;      // 极品
    private final double perfect;        // 完美
    private final double spirit1;         // 灵宝★
    private final double spirit2;         // 灵宝★★
    private final double spirit3;         // 灵宝★★★
    private final double supreme1;       // 至宝★
    private final double supreme2;       // 至宝★★
    private final double supreme3;       // 至宝★★★
    private final double celestial;      // 先天

    // 配置覆盖值
    private double[] customProbabilities;

    // 默认概率数组缓存
    private final double[] defaultProbabilities;

    LootProbabilities(double waste, double defective, double unranked, double common,
                      double low, double medium, double high, double excellent,
                      double perfect, double spirit1, double spirit2, double spirit3,
                      double supreme1, double supreme2, double supreme3, double celestial) {
        this.waste = waste;
        this.defective = defective;
        this.unranked = unranked;
        this.common = common;
        this.low = low;
        this.medium = medium;
        this.high = high;
        this.excellent = excellent;
        this.perfect = perfect;
        this.spirit1 = spirit1;
        this.spirit2 = spirit2;
        this.spirit3 = spirit3;
        this.supreme1 = supreme1;
        this.supreme2 = supreme2;
        this.supreme3 = supreme3;
        this.celestial = celestial;
        
        // 初始化默认概率数组
        this.defaultProbabilities = new double[]{
            waste, defective, unranked, common, low, medium, high, excellent,
            perfect, spirit1, spirit2, spirit3, supreme1, supreme2, supreme3, celestial
        };
    }

    /**
     * 设置自定义概率
     * @param probabilities 16个等级的概率数组
     */
    public void setCustomProbabilities(double[] probabilities) {
        if (probabilities != null && probabilities.length == 16) {
            this.customProbabilities = probabilities.clone();
        }
    }

    /**
     * 清除自定义概率 恢复默认值
     */
    public void clearCustomProbabilities() {
        this.customProbabilities = null;
    }

    /**
     * 检查是否有自定义概率
     */
    public boolean hasCustomProbabilities() {
        return this.customProbabilities != null;
    }

    /**
     * 获取实际使用的概率数组
     */
    public double[] getEffectiveProbabilities() {
        if (this.customProbabilities != null) {
            return this.customProbabilities;
        }
        return this.defaultProbabilities;
    }

    /**
     * 根据等级获取概率
     */
    public double getProbability(GradeType grade) {
        if (grade == null) return 0;
        int index = getGradeIndex(grade);
        if (index < 0) return 0;
        return getEffectiveProbabilities()[index];
    }

    /**
     * 获取等级在数组中的索引
     */
    private int getGradeIndex(GradeType grade) {
        return switch (grade) {
            case WASTE -> 0;
            case DEFECTIVE -> 1;
            case UNRANKED -> 2;
            case COMMON -> 3;
            case LOW -> 4;
            case MEDIUM -> 5;
            case HIGH -> 6;
            case EXCELLENT -> 7;
            case PERFECT -> 8;
            case SPIRIT_TREASURE_1 -> 9;
            case SPIRIT_TREASURE_2 -> 10;
            case SPIRIT_TREASURE_3 -> 11;
            case SUPREME_TREASURE_1 -> 12;
            case SUPREME_TREASURE_2 -> 13;
            case SUPREME_TREASURE_3 -> 14;
            case CELESTIAL -> 15;
            case UNKNOWN -> -1;
        };
    }

    /**
     * 获取所有概率值数组
     */
    public double[] toArray() {
        return getEffectiveProbabilities().clone();
    }

    /**
     * 验证概率总和是否为100
     */
    public double getTotalProbability() {
        double[] probs = getEffectiveProbabilities();
        double total = 0;
        for (double p : probs) {
            total += p;
        }
        return Math.round(total * 100) / 100.0;
    }
}
