package net.LightingCreeperStudio.listener;

import net.LightingCreeperStudio.EquipmentGrade;
import net.LightingCreeperStudio.grade.EnchantLimitUtil;
import net.LightingCreeperStudio.grade.GradeItemManager;
import net.LightingCreeperStudio.grade.GradeType;
import net.LightingCreeperStudio.grade.NBTUtil;
import net.LightingCreeperStudio.grade.assignment.GradeAssignmentManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

/**
 * 工作台合成监听器
 * 核心逻辑:
 * - 预览阶段:标记为"品级待定" 只显示 ???
 * - 合成完成时:真正掷骰确定品级
 * - 延迟保障:下一 tick 扫描玩家背包 确保品级已正确赋予
 * - 铁砧取出时（:铁砧不走 CraftItemEvent,需单独处理
 * 这样玩家无法通过反复合成来刷品级
 */
public class CraftingListener implements Listener {

    private final GradeAssignmentManager assignmentManager = GradeAssignmentManager.getInstance();

    /**
     * 合成准备完成事件 - 标记为品级待定,只显示 ???
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getInventory().getResult();
        if (result == null || result.getType().isAir()) return;

        // 已有真实品级的物品不再处理
        if (NBTUtil.getGrade(result) != null) return;

        // 检查物品是否应该被分级
        if (!GradeItemManager.shouldGradeItem(result)) return;

        // 标记为品级待定 来源为 crafting
        NBTUtil.setPendingSource(result, "crafting");

        // 添加 ??? 占位符 lore
        ItemGradeListener.addPendingLore(result);
        event.getInventory().setResult(result);
    }

    /**
     * 合成完成事件 - 真正掷骰确定品级
     * CraftItemEvent 同时覆盖普通点击和 Shift+点击合成
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCraftItem(CraftItemEvent event) {
        ItemStack result = event.getCurrentItem();
        if (result == null || result.getType().isAir()) return;

        // 检查是否有品级待定标记
        if (!NBTUtil.hasPendingGrade(result)) return;

        String source = NBTUtil.getPendingSource(result);

        // 移除待定标记和占位 lore
        NBTUtil.removePendingSource(result);
        ItemGradeListener.removePendingLore(result);

        // 掷骰确定品级
        GradeType grade = GradeItemManager.rollGradeFromSource(source != null ? source : "crafting");
        if (grade != null) {
            assignmentManager.assignFixedGrade(result, grade);
        }

        // 下一 tick 扫描玩家背包 确保品级已正确赋予
        if (event.getWhoClicked() instanceof Player player) {
            GradeType rolledGrade = grade;
            String rolledSource = source;
            EquipmentGrade.getInstance().getServer().getScheduler().runTaskLater(
                EquipmentGrade.getInstance(),
                () -> ensureGradeApplied(player, rolledGrade, rolledSource),
                1L
            );
        }
    }

    /**
     * 铁砧取出事件 - 铁砧不走 CraftItemEvent，需在 InventoryClickEvent 中处理
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        // 只处理铁砧
        if (event.getInventory().getType() != InventoryType.ANVIL) return;
        if (event.getSlotType() != InventoryType.SlotType.RESULT) return;

        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null || currentItem.getType().isAir()) return;

        // 检查是否有品级待定标记
        if (!NBTUtil.hasPendingGrade(currentItem)) return;

        String source = NBTUtil.getPendingSource(currentItem);

        // 移除待定标记和占位 lore
        NBTUtil.removePendingSource(currentItem);
        ItemGradeListener.removePendingLore(currentItem);

        if ("anvil".equals(source)) {
            // 铁砧修装备:基于 slot0 的品级计算新品质
            GradeType newGrade = handleAnvilResult(currentItem, event);

            // 延迟保障:确保铁砧结果的品级也正确赋予
            if (newGrade != null && event.getWhoClicked() instanceof Player player) {
                EquipmentGrade.getInstance().getServer().getScheduler().runTaskLater(
                    EquipmentGrade.getInstance(),
                    () -> ensureGradeApplied(player, newGrade, "anvil"),
                    1L
                );
            }
        }
    }

    /**
     * 延迟保障：扫描玩家背包，确保品级已正确赋予
     * 如果发现带 pendingSource 的物品 补救处理
     */
    private void ensureGradeApplied(Player player, GradeType expectedGrade, String source) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType().isAir()) continue;

            // 物品仍然有 pendingSource 标记
            if (NBTUtil.hasPendingGrade(item)) {
                NBTUtil.removePendingSource(item);
                ItemGradeListener.removePendingLore(item);
                if (expectedGrade != null) {
                    if ("anvil".equals(source)) {
                        // 铁砧场景:需要覆盖旧品级
                        GradeItemManager.setGradeOverwrite(item, expectedGrade);
                    } else if (NBTUtil.getGrade(item) == null) {
                        assignmentManager.assignFixedGrade(item, expectedGrade);
                    }
                }
            }
            // 铁砧场景 — 物品有铁砧结果标记但品级不正确
            else if ("anvil".equals(source) && expectedGrade != null
                    && NBTUtil.hasAnvilResultMarker(item)) {
                GradeType currentGrade = NBTUtil.getGrade(item);
                if (currentGrade != expectedGrade) {
                    // 确认是铁砧结果，覆盖为正确品级
                    GradeItemManager.setGradeOverwrite(item, expectedGrade);
                }
                // 无论品级是否正确 都清除标记
                NBTUtil.removeAnvilResultMarker(item);
            }
        }
    }

    /**
     * 处理铁砧取出物品的品级确定
     * @return 计算出的新等级
     */
    private GradeType handleAnvilResult(ItemStack result, InventoryClickEvent event) {
        if (!(event.getInventory() instanceof AnvilInventory anvilInv)) return null;

        ItemStack slot0 = anvilInv.getItem(0);
        if (slot0 == null || slot0.getType().isAir()) return null;

        GradeType slot0Grade = NBTUtil.getGrade(slot0);
        if (slot0Grade == null) return null;

        // 获取 AnvilListener 实例并计算品级
        AnvilListener anvilListener = getAnvilListener();
        if (anvilListener == null) return null;

        GradeType newGrade = anvilListener.calculateAnvilGrade(slot0Grade);
        if (newGrade != null) {
            // 铁砧结果可能残留旧品级
            // 必须用 setGradeOverwrite 覆盖,assignFixedGrade 会因已有品级而跳过
            GradeItemManager.setGradeOverwrite(result, newGrade);

            // 设置铁砧结果标记,用于延迟保障时识别铁砧产物
            NBTUtil.setAnvilResultMarker(result);

            // 品级掉落后检查附魔是否超限,超限则随机移除一个附魔
            if (slot0Grade != newGrade && EnchantLimitUtil.isEnchantOverLimit(result, newGrade)) {
                String removed = EnchantLimitUtil.removeRandomEnchant(result);
                if (removed != null && event.getWhoClicked() instanceof Player player) {
                    player.sendMessage(Component.text()
                            .append(Component.text("品级掉落，附魔数量超出上限，已移除: ", NamedTextColor.YELLOW))
                            .append(Component.text(removed, NamedTextColor.RED))
                            .build());
                }
            }
        }
        return newGrade;
    }

    /**
     * 获取 AnvilListener 实例
     */
    private AnvilListener getAnvilListener() {
        return AnvilListener.getInstance();
    }

}
