package net.LightingCreeperStudio.task;

import net.LightingCreeperStudio.EquipmentGrade;
import net.LightingCreeperStudio.grade.GradeItemManager;
import net.LightingCreeperStudio.grade.GradeType;
import net.LightingCreeperStudio.grade.NBTUtil;
import net.LightingCreeperStudio.grade.assignment.GradeAssignmentManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 背包扫描器
 * 定期扫描玩家背包 确保所有物品都有品级
 */
public class BackpackScanner {

    private final GradeAssignmentManager assignmentManager;
    private static final NamespacedKey GRADE_KEY = new NamespacedKey("equipmentgrade", NBTUtil.NBT_KEY_GRADE);

    public BackpackScanner() {
        this.assignmentManager = GradeAssignmentManager.getInstance();
    }

    /**
     * 扫描所有在线玩家的背包
     */
    public void scanAllPlayers() {
        EquipmentGrade instance = EquipmentGrade.getInstance();
        AtomicInteger totalFixed = new AtomicInteger(0);

        for (Player player : instance.getServer().getOnlinePlayers()) {
            int fixed = scanPlayerInventory(player);
            totalFixed.addAndGet(fixed);
        }

        if (totalFixed.get() > 0) {
            instance.getLogger().info("背包扫描完成 修复了 " + totalFixed.get() + " 个物品的品级");
        } else {
            instance.getLogger().fine("背包扫描完成 所有物品品级正常");
        }
    }

    /**
     * 扫描单个玩家的背包
     */
    private int scanPlayerInventory(Player player) {
        int fixedCount = 0;

        // 扫描主背包
        for (ItemStack item : player.getInventory().getContents()) {
            if (shouldAssignGrade(item)) {
                assignGrade(item);
                fixedCount++;
            }
        }

        // 扫描盔甲栏
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (shouldAssignGrade(item)) {
                assignGrade(item);
                fixedCount++;
            }
        }

        // 扫描副手
        ItemStack offhand = player.getInventory().getItemInOffHand();
        if (shouldAssignGrade(offhand)) {
            assignGrade(offhand);
            fixedCount++;
        }

        // 扫描末影箱
        if (player.getEnderChest() != null) {
            for (ItemStack item : player.getEnderChest().getContents()) {
                if (shouldAssignGrade(item)) {
                    assignGrade(item);
                    fixedCount++;
                }
            }
        }

        return fixedCount;
    }

    /**
     * 检查物品是否应该赋予品级
     */
    private boolean shouldAssignGrade(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return false;
        }
        // 只处理可升级物品且没有品级标签的
        if (!GradeItemManager.shouldGradeItem(item)) {
            return false;
        }
        // 检查是否有品级NBT标签
        return !hasGradeTag(item);
    }

    /**
     * 检查物品是否有品级标签
     */
    private boolean hasGradeTag(ItemStack item) {
        if (!item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.has(GRADE_KEY, PersistentDataType.STRING);
    }

    /**
     * 为物品赋予凡品
     */
    private void assignGrade(ItemStack item) {
        assignmentManager.assignFixedGrade(item, GradeType.COMMON);
    }
}
