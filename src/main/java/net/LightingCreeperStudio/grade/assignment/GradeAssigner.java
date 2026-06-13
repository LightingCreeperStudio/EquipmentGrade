package net.LightingCreeperStudio.grade.assignment;

/**
 * 等级赋予器接口
 * 定义如何为物品赋予等级的策略
 */
public interface GradeAssigner {

    /**
     * 为物品赋予等级
     *
     * @param item 物品
     * @return 赋予的等级 null表示不赋予等级
     */
    net.LightingCreeperStudio.grade.GradeType assignGrade(org.bukkit.inventory.ItemStack item);

    /**
     * 获取赋予器的标识名称
     */
    String getName();
}
