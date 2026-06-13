package net.LightingCreeperStudio.listener;

import net.LightingCreeperStudio.grade.GradeItemManager;
import net.LightingCreeperStudio.grade.GradeType;
import net.LightingCreeperStudio.grade.assignment.GradeAssignmentManager;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 物品展示框监听器
 * 当物品展示框被破坏时 为框内的物品赋予随机等级
 */
public class ItemFrameListener implements Listener {

    private final GradeAssignmentManager assignmentManager;

    public ItemFrameListener() {
        this.assignmentManager = GradeAssignmentManager.getInstance();
    }

    /**
     * 物品展示框被破坏时触发
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        // 检查是否为玩家破坏
        if (!(event.getRemover() instanceof Player)) return;

        // 检查是否为物品展示框
        if (!(event.getEntity() instanceof ItemFrame itemFrame)) return;

        // 获取物品展示框内的物品
        ItemStack frameItem = itemFrame.getItem();
        if (frameItem == null || frameItem.getType().isAir()) return;

        // 如果有物品 赋予等级
        if (GradeItemManager.shouldGradeItem(frameItem)) {
            GradeType grade = GradeItemManager.rollGrade("item_frame");
            assignmentManager.assignFixedGrade(frameItem, grade);
            itemFrame.setItem(frameItem);
        }
    }
}
