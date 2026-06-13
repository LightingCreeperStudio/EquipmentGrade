package net.LightingCreeperStudio.listener;

import net.LightingCreeperStudio.grade.GradeType;
import net.LightingCreeperStudio.grade.ItemPropertyModifier;
import net.LightingCreeperStudio.grade.NBTUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * 物品等级事件监听器
 * 处理物品等级相关的各种事件
 * 合成和铁砧的逻辑已移至 CraftingListener 和 AnvilListener
 */
public class ItemGradeListener implements Listener {

    /**
     * 玩家登录时处理背包中的分级物品
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        processPlayerInventory(event.getPlayer().getInventory().getContents());
    }

    /**
     * 玩家重生时处理物品
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        // 重生后会在下次登录时处理
    }

    /**
     * 砂轮准备事件 - 移除等级属性
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareGrindstone(PrepareGrindstoneEvent event) {
        ItemStack result = event.getResult();
        if (result == null || result.getType().isAir()) return;

        // 移除等级属性和lore
        removeGrade(result);
        event.setResult(result);
    }

    /**
     * 玩家死亡时保持掉落物品的等级属性
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        for (ItemStack item : event.getDrops()) {
            GradeType grade = NBTUtil.getGrade(item);
            if (grade != null) {
                ItemPropertyModifier.applyGradeProperties(item);
            }
        }
    }

    /**
     * 玩家右键物品时更新显示
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;

        GradeType grade = NBTUtil.getGrade(item);
        if (grade != null) {
            // 确保属性已应用
            ItemPropertyModifier.applyGradeProperties(item);
        }
    }

    /**
     * 处理玩家背包中的所有物品
     */
    private void processPlayerInventory(ItemStack[] items) {
        for (ItemStack item : items) {
            if (item == null) continue;
            GradeType grade = NBTUtil.getGrade(item);
            if (grade != null) {
                ItemPropertyModifier.applyGradeProperties(item);
            }
        }
    }

    /**
     * 为物品添加品级显示
     */
    public static ItemStack addGradeLore(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return item;

        GradeType grade = NBTUtil.getGrade(item);
        if (grade == null) return item;

        // === 处理 lore ===
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = meta.lore();
        if (lore == null) {
            lore = new ArrayList<>();
        } else {
            lore = removeGradeLoreFromList(lore);
        }

        List<Component> gradeLore = NBTUtil.createGradeLore(
                grade,
                NBTUtil.getCustomAttribute(item),
                NBTUtil.getCustomDurability(item)
        );

        lore.addAll(0, gradeLore);

        meta.lore(lore);
        item.setItemMeta(meta);

        // 应用属性修改
        ItemPropertyModifier.applyGradeProperties(item);

        return item;
    }

    /**
     * 从lore列表中移除等级相关的所有行
     * 匹配规则:
     * - 品级行:包含 "品"/"完美"/"先天"/"灵宝"/"至宝" 等品级关键词
     * - [属性] / [耐久] 格式的属性行
     * - 分割线 ─────────────
     * - 等级:??? 占位符行
     * - 品级行前后的空行
     */
    private static List<Component> removeGradeLoreFromList(List<Component> lore) {
        List<Component> newLore = new ArrayList<>();
        boolean inGradeSection = false;

        for (Component line : lore) {
            String text = PlainTextComponentSerializer.plainText().serialize(line);

            // 检测品级行:匹配所有品级关键词
            if (isGradeLine(text)) {
                inGradeSection = true;
                // 移除品级前的空行
                while (!newLore.isEmpty()) {
                    Component last = newLore.get(newLore.size() - 1);
                    String lastText = PlainTextComponentSerializer.plainText().serialize(last);
                    if (lastText.isEmpty()) {
                        newLore.remove(newLore.size() - 1);
                    } else {
                        break;
                    }
                }
                continue;
            }

            // 检测等级: ??? 占位行
            if (text.contains("等级: ???") || text.contains("等级：???")) {
                // 移除前面的空行
                if (!newLore.isEmpty()) {
                    Component last = newLore.get(newLore.size() - 1);
                    String lastText = PlainTextComponentSerializer.plainText().serialize(last);
                    if (lastText.isEmpty()) {
                        newLore.remove(newLore.size() - 1);
                    }
                }
                continue;
            }

            // 检测 [属性] / [耐久] 格式的属性行
            if (text.contains("[属性]") || text.contains("[耐久]")) {
                continue;
            }

            // 检测分割线
            if (text.contains("─────────────────────")) {
                inGradeSection = !inGradeSection;
                continue;
            }

            // 如果在等级区域内 空行属于品级区域
            if (inGradeSection) {
                if (text.isEmpty()) {
                    continue;
                } else {
                    inGradeSection = false;
                }
            }

            newLore.add(line);
        }

        // 移除末尾可能残留的空行
        while (!newLore.isEmpty()) {
            Component last = newLore.get(newLore.size() - 1);
            String lastText = PlainTextComponentSerializer.plainText().serialize(last);
            if (lastText.isEmpty()) {
                newLore.remove(newLore.size() - 1);
            } else {
                break;
            }
        }

        return newLore;
    }

    /**
     * 判断一行文本是否是品级行
     * 匹配 createGradeLore 生成的品级显示格式
     */
    private static boolean isGradeLine(String text) {
        if (text.isEmpty()) return false;
        // 匹配所有品级名称
        return text.contains("废品") || text.contains("残次品") || text.contains("不入品")
            || text.contains("凡品") || text.contains("下品") || text.contains("中品")
            || text.contains("上品") || text.contains("极品") || text.contains("完美")
            || text.contains("灵宝") || text.contains("至宝")
            || text.contains("先天") || text.contains("未知");
    }

    /**
     * 为物品添加品级待定的 ??? 占位符 lore
     * 供 AnvilListener / CraftingListener 共用
     */
    public static void addPendingLore(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        List<Component> lore = meta.lore();
        if (lore == null) {
            lore = new ArrayList<>();
        }

        // 检查是否已有待定lore 避免重复添加
        for (Component line : lore) {
            String text = PlainTextComponentSerializer.plainText().serialize(line);
            if (text.contains("等级: ???")) return;
        }

        lore.add(Component.text("  等级: ???", NamedTextColor.GOLD));

        meta.lore(lore);
        item.setItemMeta(meta);
    }

    /**
     * 移除品级待定的 ??? 占位符 lore
     * 供 AnvilListener / CraftingListener 共用
     */
    public static void removePendingLore(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        List<Component> lore = meta.lore();
        if (lore == null || lore.isEmpty()) return;

        List<Component> newLore = new ArrayList<>();

        for (int i = 0; i < lore.size(); i++) {
            Component line = lore.get(i);
            String text = PlainTextComponentSerializer.plainText().serialize(line);

            if (text.contains("等级: ???")) {
                // 移除前面的空行
                if (!newLore.isEmpty()) {
                    Component last = newLore.get(newLore.size() - 1);
                    String lastText = PlainTextComponentSerializer.plainText().serialize(last);
                    if (lastText.isEmpty()) {
                        newLore.remove(newLore.size() - 1);
                    }
                }
                continue;
            }
            newLore.add(line);
        }

        meta.lore(newLore);
        item.setItemMeta(meta);
    }

    /**
     * 移除物品的品级显示
     */
    public static ItemStack removeGradeLore(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return item;

        // 移除品级相关 lore
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = meta.lore();
        if (lore != null && !lore.isEmpty()) {
            List<Component> newLore = removeGradeLoreFromList(lore);
            meta.lore(newLore);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 移除物品的等级属性和 lore
     */
    public static void removeGrade(ItemStack item) {
        if (item == null) return;

        // 保存装备名,用于判断 displayName 是否由自动命名设置
        String equipName = NBTUtil.getEquipmentName(item);

        // 移除品级显示
        removeGradeLore(item);

        // 移除属性
        ItemPropertyModifier.removeGradeProperties(item);

        // 移除装备名 NBT
        NBTUtil.removeEquipmentName(item);

        // 如果有装备名且 displayName 由自动命名设置,清除它
        if (equipName != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                String displayName = PlainTextComponentSerializer.plainText().serialize(meta.displayName());
                if (equipName.equals(displayName)) {
                    meta.displayName(null);
                    item.setItemMeta(meta);
                }
            }
        }
    }
}
