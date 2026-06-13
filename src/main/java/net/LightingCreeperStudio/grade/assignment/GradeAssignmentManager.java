package net.LightingCreeperStudio.grade.assignment;

import net.LightingCreeperStudio.grade.GradeType;
import net.LightingCreeperStudio.grade.ItemBlacklist;
import net.LightingCreeperStudio.grade.NBTUtil;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * 等级赋予管理器
 * 统一管理所有等级赋予操作
 */
public class GradeAssignmentManager {

    private static GradeAssignmentManager instance;
    private final Map<String, GradeAssigner> assigners = new HashMap<>();

    private GradeAssignmentManager() {
        // 注册默认赋予器
        registerAssigner("fixed", new FixedGradeAssigner(GradeType.COMMON, "固定"));
    }

    public static GradeAssignmentManager getInstance() {
        if (instance == null) {
            instance = new GradeAssignmentManager();
        }
        return instance;
    }

    /**
     * 注册等级赋予器
     */
    public void registerAssigner(String key, GradeAssigner assigner) {
        if (key != null && assigner != null) {
            assigners.put(key, assigner);
        }
    }

    /**
     * 获取等级赋予器
     */
    public GradeAssigner getAssigner(String key) {
        return assigners.get(key);
    }

    /**
     * 使用指定赋予器为物品赋予等级
     */
    public GradeType assignGrade(String assignerKey, ItemStack item) {
        GradeAssigner assigner = assigners.get(assignerKey);
        if (assigner == null) return null;

        GradeType grade = assigner.assignGrade(item);
        if (grade != null) {
            RandomGradeAssigner.applyGrade(item, grade);
        }
        return grade;
    }

    /**
     * 为物品赋予随机等级
     */
    public GradeType assignRandomGrade(ItemStack item, double[] probabilities) {
        if (item == null) return null;
        if (NBTUtil.getGrade(item) != null) return null;

        GradeType grade = RandomGradeAssigner.rollFromProbabilities(probabilities);
        RandomGradeAssigner.applyGrade(item, grade);
        return grade;
    }

    /**
     * 为物品赋予固定等级
     */
    public GradeType assignFixedGrade(ItemStack item, GradeType grade) {
        if (item == null || grade == null) return null;

        RandomGradeAssigner.applyGrade(item, grade);
        return grade;
    }

    /**
     * 检查物品是否需要赋予等级
     */
    public boolean needsGrade(ItemStack item) {
        if (item == null) return false;
        if (ItemBlacklist.isBlacklisted(item.getType())) return false;
        return NBTUtil.getGrade(item) == null;
    }

    /**
     * 批量为物品赋予等级
     */
    public List<GradeType> assignGrades(String assignerKey, ItemStack[] items) {
        List<GradeType> results = new ArrayList<>();
        if (items != null) {
            for (ItemStack item : items) {
                if (item != null) {
                    results.add(assignGrade(assignerKey, item));
                }
            }
        }
        return results;
    }

    /**
     * 批量为物品赋予随机等级
     */
    public List<GradeType> assignRandomGrades(ItemStack[] items, double[] probabilities) {
        List<GradeType> results = new ArrayList<>();
        if (items != null) {
            for (ItemStack item : items) {
                if (item != null) {
                    results.add(assignRandomGrade(item, probabilities));
                }
            }
        }
        return results;
    }
}
