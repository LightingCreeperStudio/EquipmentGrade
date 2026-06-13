package net.LightingCreeperStudio.grade;

import java.util.Map;

/**
 * 战利品结构配置
 * 定义每个结构的等级概率分布
 */
public class LootConfig {
    
    private final String structureId;
    private final Map<GradeType, Double> gradeProbabilities;
    private final boolean enabled;
    
    public LootConfig(String structureId, Map<GradeType, Double> gradeProbabilities, boolean enabled) {
        this.structureId = structureId;
        this.gradeProbabilities = gradeProbabilities;
        this.enabled = enabled;
    }
    
    public String getStructureId() {
        return structureId;
    }
    
    public Map<GradeType, Double> getGradeProbabilities() {
        return gradeProbabilities;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * 获取等级概率
     */
    public double getProbability(GradeType grade) {
        return gradeProbabilities.getOrDefault(grade, 0.0);
    }
    
    /**
     * 根据随机数生成等级
     * @param random 0-100的随机数
     * @return 对应的等级
     */
    public GradeType rollGrade(double random) {
        double cumulative = 0;
        for (GradeType grade : GradeType.values()) {
            if (grade == GradeType.UNKNOWN) continue;
            cumulative += getProbability(grade);
            if (random < cumulative) {
                return grade;
            }
        }
        // 默认返回凡品
        return GradeType.COMMON;
    }
}
