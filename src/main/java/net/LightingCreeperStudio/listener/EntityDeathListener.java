package net.LightingCreeperStudio.listener;

import net.LightingCreeperStudio.grade.GradeItemManager;
import net.LightingCreeperStudio.grade.GradeType;
import net.LightingCreeperStudio.grade.NBTUtil;
import net.LightingCreeperStudio.grade.assignment.GradeAssignmentManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * 生物掉落监听器
 * 为生物掉落的可装备物品赋予等级
 */
public class EntityDeathListener implements Listener {

    private final GradeAssignmentManager assignmentManager = GradeAssignmentManager.getInstance();

    /**
     * 生物死亡事件 - 为掉落物品赋予等级
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        List<ItemStack> drops = event.getDrops();
        
        for (ItemStack drop : drops) {
            if (drop == null || drop.getType().isAir()) continue;
            
            // 使用 shouldGradeItem 统一判断
            if (!GradeItemManager.shouldGradeItem(drop)) continue;
            
            // 为生物掉落物赋予等级
            GradeType grade = GradeItemManager.rollGradeFromSource("entity_drop");
            if (grade != null) {
                assignmentManager.assignFixedGrade(drop, grade);
            }
        }
    }
}
