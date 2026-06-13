package net.LightingCreeperStudio.listener;

import net.LightingCreeperStudio.EquipmentGrade;
import net.LightingCreeperStudio.grade.GradeItemManager;
import net.LightingCreeperStudio.grade.GradeType;
import net.LightingCreeperStudio.grade.NBTUtil;
import net.LightingCreeperStudio.grade.assignment.GradeAssignmentManager;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;

/**
 * 村民交易监听器
 * 为村民交易获得的物品赋予等级
 *
 * 使用 InventoryClickEvent 监听交易结果槽位点击
 * 确保玩家切换交易后新结果也能正确赋予品级
 */
public class VillagerTradeListener implements Listener {

    private final GradeAssignmentManager assignmentManager = GradeAssignmentManager.getInstance();

    /**
     * 玩家点击村民交易结果槽位时触发
     * 在玩家实际取走物品前为物品赋予品级
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onTradeResultClick(InventoryClickEvent event) {
        // 只处理村民交易界面中的结果槽位点击
        if (!(event.getClickedInventory() instanceof MerchantInventory merchantInv)) return;
        if (event.getSlot() != 2) return; // 结果槽位

        // 检查是否为村民
        if (!(merchantInv.getMerchant() instanceof Villager)) return;

        ItemStack result = event.getCurrentItem();
        if (result == null || result.getType().isAir()) return;

        // 检查物品是否已有等级
        if (NBTUtil.getGrade(result) != null) return;

        // 检查物品是否应该被分级
        if (!GradeItemManager.shouldGradeItem(result)) return;

        // 为交易产物赋予等级
        GradeType grade = GradeItemManager.rollGradeFromSource("villager_trade");
        if (grade != null) {
            assignmentManager.assignFixedGrade(result, grade);
        }
    }

    /**
     * 为玩家的村民交易产物赋予等级
     */
    public static void applyGradeToTradeResult(ItemStack item) {
        if (item == null) return;
        if (NBTUtil.getGrade(item) != null) return;
        if (!GradeItemManager.shouldGradeItem(item)) return;

        GradeType grade = GradeItemManager.rollGradeFromSource("villager_trade");
        if (grade != null) {
            GradeAssignmentManager.getInstance().assignFixedGrade(item, grade);
        }
    }
}
