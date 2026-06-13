package net.LightingCreeperStudio.listener;

import net.LightingCreeperStudio.grade.GradeItemManager;
import net.LightingCreeperStudio.grade.GradeType;
import net.LightingCreeperStudio.grade.NBTUtil;
import net.LightingCreeperStudio.grade.assignment.GradeAssignmentManager;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 钓鱼监听器
 * 为钓鱼获得的物品赋予等级
 */
public class FishingListener implements Listener {

    private final GradeAssignmentManager assignmentManager = GradeAssignmentManager.getInstance();

    /**
     * 玩家钓鱼事件
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
        // 只处理钓到物品的情况
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH &&
            event.getState() != PlayerFishEvent.State.CAUGHT_ENTITY) {
            return;
        }

        // 获取钓到的物品
        if (event.getCaught() instanceof Item caughtItem) {
            ItemStack itemStack = caughtItem.getItemStack().clone();
            
            if (itemStack != null && !itemStack.getType().isAir()) {
                applyGradeToFishingResult(itemStack, caughtItem);
            }
        }
    }

    /**
     * 为钓鱼获得的物品赋予等级
     */
    private void applyGradeToFishingResult(ItemStack item, Item caught) {
        if (item == null || item.getType().isAir()) return;

        // 检查物品是否已有等级
        if (NBTUtil.getGrade(item) != null) return;

        // 检查物品是否应该被分级
        if (!GradeItemManager.shouldGradeItem(item)) return;

        // 为钓鱼产物赋予等级
        GradeType grade = GradeItemManager.rollGradeFromSource("fishing");
        if (grade != null) {
            assignmentManager.assignFixedGrade(item, grade);

            // 应用等级属性和lore
            net.LightingCreeperStudio.grade.ItemPropertyModifier.applyGradeProperties(item);
            ItemGradeListener.addGradeLore(item);
            
            // 更新物品
            caught.setItemStack(item);
        }
    }

    /**
     * 为钓鱼产物赋予等级的静态方法
     */
    public static void applyGradeToFishingResult(ItemStack item) {
        if (item == null || item.getType().isAir()) return;
        if (NBTUtil.getGrade(item) != null) return;
        if (!GradeItemManager.shouldGradeItem(item)) return;

        GradeType grade = GradeItemManager.rollGradeFromSource("fishing");
        if (grade != null) {
            GradeAssignmentManager.getInstance().assignFixedGrade(item, grade);
            net.LightingCreeperStudio.grade.ItemPropertyModifier.applyGradeProperties(item);
            ItemGradeListener.addGradeLore(item);
        }
    }
}
