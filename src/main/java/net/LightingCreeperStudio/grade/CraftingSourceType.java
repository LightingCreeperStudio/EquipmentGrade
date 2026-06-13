package net.LightingCreeperStudio.grade;

/**
 * 物品等级来源类型枚举
 * 用于区分不同的物品获取方式
 */
public enum CraftingSourceType {
    
    // 战利品箱
    LOOT_CHEST("战利品箱"),
    
    // 原版工作台合成
    CRAFTING("工作台合成"),
    
    // 村民交易
    VILLAGER_TRADE("村民交易"),
    
    // 生物掉落
    ENTITY_DROP("生物掉落"),
    
    // 指令/give
    COMMAND("指令获取"),
    
    // 未知来源
    UNKNOWN("未知");

    private final String displayName;

    CraftingSourceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 获取对应的概率配置
     */
    public LootProbabilities getProbabilities() {
        return switch (this) {
            case LOOT_CHEST -> LootProbabilities.FALLBACK;
            case CRAFTING -> LootProbabilities.CRAFTING;
            case VILLAGER_TRADE -> LootProbabilities.VILLAGER_TRADE;
            case ENTITY_DROP -> LootProbabilities.ENTITY_DROP;
            case COMMAND -> LootProbabilities.COMMAND_DEFAULT;
            case UNKNOWN -> LootProbabilities.FALLBACK;
        };
    }

    /**
     * 获取指令随机概率配置
     */
    public LootProbabilities getCommandProbabilities(boolean random) {
        return random ? LootProbabilities.COMMAND_RANDOM : LootProbabilities.COMMAND_DEFAULT;
    }
}
