package net.LightingCreeperStudio.grade.assignment;

import net.LightingCreeperStudio.grade.GradeType;
import org.bukkit.inventory.ItemStack;

/**
 * 固定等级赋予器
 * 始终赋予相同的等级
 */
public class FixedGradeAssigner implements GradeAssigner {

    private final GradeType fixedGrade;
    private final String sourceName;

    public FixedGradeAssigner(GradeType fixedGrade, String sourceName) {
        this.fixedGrade = fixedGrade;
        this.sourceName = sourceName;
    }

    @Override
    public GradeType assignGrade(ItemStack item) {
        if (item == null) return null;
        return fixedGrade;
    }

    @Override
    public String getName() {
        return sourceName;
    }

    public GradeType getFixedGrade() {
        return fixedGrade;
    }
}
