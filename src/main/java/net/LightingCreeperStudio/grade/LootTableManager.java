package net.LightingCreeperStudio.grade;

import java.util.*;

/**
 * 战利品表管理器
 * 负责根据结构类型生成随机等级
 */
public class LootTableManager {

    private static LootTableManager instance;
    private final Random random;

    private LootTableManager() {
        this.random = new Random();
    }

    public static LootTableManager getInstance() {
        if (instance == null) {
            instance = new LootTableManager();
        }
        return instance;
    }

    /**
     * 根据结构类型掷骰获取等级
     *
     * @param structureType 结构类型
     * @return 随机等级
     */
    public GradeType rollGrade(LootStructureType structureType) {
        if (structureType == null) {
            structureType = LootStructureType.FALLBACK;
        }

        LootProbabilities probabilities = structureType.getProbabilities();
        double roll = random.nextDouble() * 100; // 0-100随机数

        double cumulative = 0;
        for (GradeType grade : GradeType.values()) {
            if (grade == GradeType.UNKNOWN) continue;

            cumulative += probabilities.getProbability(grade);
            if (roll < cumulative) {
                return grade;
            }
        }

        // 如果没命中，返回凡品
        return GradeType.COMMON;
    }

    /**
     * 根据箱子ID获取等级
     *
     * @param chestId 箱子ID
     * @return 随机等级
     */
    public GradeType rollGrade(String chestId) {
        LootStructureType type = LootStructureType.fromChestId(chestId);
        return rollGrade(type);
    }

    /**
     * 生成一批随机等级
     *
     * @param structureType 结构类型
     * @param count 数量
     * @return 等级列表
     */
    public List<GradeType> rollGrades(LootStructureType structureType, int count) {
        List<GradeType> grades = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            grades.add(rollGrade(structureType));
        }
        return grades;
    }

    /**
     * 生成一批随机等级
     *
     * @param chestId 箱子ID
     * @param count 数量
     * @return 等级列表
     */
    public List<GradeType> rollGrades(String chestId, int count) {
        List<GradeType> grades = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            grades.add(rollGrade(chestId));
        }
        return grades;
    }

    /**
     * 获取结构类型的显示名称
     */
    public String getStructureDisplayName(LootStructureType type) {
        return type != null ? type.getDisplayName() : "未知";
    }

    /**
     * 获取结构类型的显示名称
     */
    public String getStructureDisplayName(String chestId) {
        LootStructureType type = LootStructureType.fromChestId(chestId);
        return type.getDisplayName();
    }

    /**
     * 获取所有结构类型及其显示名称
     */
    public Map<String, String> getAllStructureNames() {
        Map<String, String> names = new LinkedHashMap<>();
        for (LootStructureType type : LootStructureType.values()) {
            names.put(type.getChestId(), type.getDisplayName());
        }
        return names;
    }

    /**
     * 验证所有概率配置的总和是否为100%
     */
    public boolean validateProbabilities() {
        boolean valid = true;
        for (LootProbabilities prob : LootProbabilities.values()) {
            double total = prob.getTotalProbability();
            if (Math.abs(total - 100.0) > 0.01) {
                valid = false;
                break;
            }
        }
        return valid;
    }

    /**
     * 获取概率验证报告
     */
    public String getProbabilityValidationReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== 概率验证报告 ===\n\n");

        for (LootProbabilities prob : LootProbabilities.values()) {
            double total = prob.getTotalProbability();
            String status = Math.abs(total - 100.0) < 0.01 ? "✓" : "✗";
            report.append(String.format("%s: %.2f%% %s\n", prob.name(), total, status));
        }

        return report.toString();
    }
}
