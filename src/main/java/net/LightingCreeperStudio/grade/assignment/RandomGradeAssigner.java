package net.LightingCreeperStudio.grade.assignment;

import net.LightingCreeperStudio.grade.EquipmentNamer;
import net.LightingCreeperStudio.grade.GradeItemManager;
import net.LightingCreeperStudio.grade.GradeType;
import net.LightingCreeperStudio.grade.NBTUtil;
import net.LightingCreeperStudio.grade.ItemPropertyModifier;
import net.LightingCreeperStudio.listener.ItemGradeListener;
import org.bukkit.inventory.ItemStack;

/**
 * 随机等级赋予器
 * 根据给定的概率分布随机生成等级
 */
public class RandomGradeAssigner implements GradeAssigner {

    private final String sourceName;

    public RandomGradeAssigner(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public GradeType assignGrade(ItemStack item) {
        if (item == null) return null;
        if (NBTUtil.getGrade(item) != null) return null; // 已有等级不覆盖

        // 由外部根据结构类型调用对应的随机等级
        return null;
    }

    /**
     * 根据指定概率数组掷骰获取等级
     *
     * @param probabilities 概率数组 索引对应 GradeType 顺序
     * @return 随机等级
     */
    public static GradeType rollFromProbabilities(double[] probabilities) {
        if (probabilities == null || probabilities.length < 16) {
            return GradeType.COMMON;
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
            cumulative += probabilities[i];
            if (roll < cumulative) {
                return grades[i];
            }
        }

        return GradeType.COMMON;
    }

    /**
     * 为物品赋予指定等级
     *
     * @param item 物品
     * @param grade 等级
     */
    public static void applyGrade(ItemStack item, GradeType grade) {
        if (item == null || grade == null) return;
        if (NBTUtil.getGrade(item) != null) return;

        NBTUtil.setGrade(item, grade);
        ItemPropertyModifier.applyGradeProperties(item);
        ItemGradeListener.addGradeLore(item);

        // 自动命名 品级>=完美且未被命名过时触发
        EquipmentNamer.tryAutoName(item, grade);
    }

    @Override
    public String getName() {
        return sourceName;
    }
}
