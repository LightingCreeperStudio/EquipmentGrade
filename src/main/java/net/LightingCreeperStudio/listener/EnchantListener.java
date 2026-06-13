package net.LightingCreeperStudio.listener;

import net.LightingCreeperStudio.grade.EnchantLimitUtil;
import net.LightingCreeperStudio.grade.GradeType;
import net.LightingCreeperStudio.grade.NBTUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 附魔台事件监听器
 * 根据品级限制附魔台附魔：
 * - 废品：禁止附魔
 * - 附魔已达上限：阻止附魔，提示已满
 */
public class EnchantListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEnchantItem(EnchantItemEvent event) {
        ItemStack item = event.getItem();
        GradeType grade = NBTUtil.getGrade(item);
        if (grade == null) return;

        Player player = event.getEnchanter();

        // 废品禁止附魔
        if (grade.getMaxEnchantCount() == 0) {
            event.setCancelled(true);
            player.sendMessage(Component.text("该品级物品无法附魔！", NamedTextColor.RED));
            return;
        }

        // 附魔已达上限
        if (EnchantLimitUtil.isEnchantLimitReached(item)) {
            event.setCancelled(true);
            int current = EnchantLimitUtil.getEnchantCount(item);
            int max = grade.getMaxEnchantCount();
            player.sendMessage(Component.text()
                    .append(Component.text("该品级物品附魔已满 (", NamedTextColor.RED))
                    .append(Component.text(String.valueOf(current), NamedTextColor.YELLOW))
                    .append(Component.text("/", NamedTextColor.RED))
                    .append(Component.text(String.valueOf(max), NamedTextColor.YELLOW))
                    .append(Component.text(")", NamedTextColor.RED))
                    .build());
        }
    }
}
