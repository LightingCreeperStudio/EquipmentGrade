package net.LightingCreeperStudio.listener;

import net.LightingCreeperStudio.grade.GradeItemManager;
import net.LightingCreeperStudio.grade.GradeType;
import net.LightingCreeperStudio.grade.NBTUtil;
import net.LightingCreeperStudio.grade.assignment.GradeAssignmentManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

/**
 * 村民交易监听器
 * 处理村民交易获得的物品赋予等级
 * 村民升级本身不直接给物品 主要通过交易监听
 */
public class VillagerLevelUpListener implements Listener {

    private final GradeAssignmentManager assignmentManager;

    public VillagerLevelUpListener() {
        this.assignmentManager = GradeAssignmentManager.getInstance();
    }

    /**
     * 监听村民交易窗口的关闭
     * 用于处理村民交易给予的物品
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVillagerTradeComplete(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.MERCHANT) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        // 获取交易完成的物品
        ItemStack cursorItem = player.getItemOnCursor();
        if (cursorItem == null || cursorItem.getType().isAir()) return;
        
        // 检查物品是否已有等级
        if (NBTUtil.getGrade(cursorItem) != null) return;

        // 检查物品是否应该被分级
        if (!GradeItemManager.shouldGradeItem(cursorItem)) return;

        // 为村民交易产物赋予等级
        GradeType grade = GradeItemManager.rollGrade("villager_trade");
        assignmentManager.assignFixedGrade(cursorItem, grade);
        
        // 应用等级属性
        net.LightingCreeperStudio.grade.ItemPropertyModifier.applyGradeProperties(cursorItem);
        ItemGradeListener.addGradeLore(cursorItem);
    }
}
