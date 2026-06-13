package net.LightingCreeperStudio.command;

import net.LightingCreeperStudio.grade.GradeItemManager;
import net.LightingCreeperStudio.grade.GradeType;
import net.LightingCreeperStudio.listener.ItemGradeListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 快速设置手中物品等级属性指令
 * 用法:
 * /eqset <等级> - 添加指定等级属性
 * /eqset unknown <属性> <耐久> - 添加自定义属性
 */
public class EqSetCommand implements CommandExecutor, TabCompleter {

    private static final String PREFIX = "§b[EQSET] §e";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sendHelp(sender);
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(PREFIX + "§c只有玩家可以使用此指令");
            return true;
        }

        if (!player.hasPermission("equipmentgrade.eqset")) {
            player.sendMessage(PREFIX + "§c你没有权限使用此指令");
            return true;
        }

        // 获取手中物品
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage(PREFIX + "§c请手持要设置的物品");
            return true;
        }

        String type = args[0].toUpperCase();

        // 处理UNKNOWN等级
        if (type.equals("UNKNOWN")) {
            if (args.length < 3) {
                player.sendMessage(PREFIX + "§cUNKNOWN等级需要两个参数: 属性值 耐久值");
                player.sendMessage(PREFIX + "§e用法: /eqset unknown <属性> <耐久>");
                player.sendMessage(PREFIX + "§e示例: /eqset unknown 1 50  或  /eqset unknown -0.5 -25");
                return true;
            }

            try {
                double attribute = Double.parseDouble(args[1]);
                int durability = Integer.parseInt(args[2]);

                // 获取原有属性值
                double oldAttr = GradeItemManager.getCurrentAttribute(item);
                int oldDura = GradeItemManager.getCurrentDurability(item);

                // 累加属性
                GradeItemManager.applyGrade(item, GradeType.UNKNOWN, (int) attribute, durability);

                // 获取新值
                double newAttr = GradeItemManager.getCurrentAttribute(item);
                int newDura = GradeItemManager.getCurrentDurability(item);

                // 更新lore
                ItemGradeListener.removeGradeLore(item);
                ItemGradeListener.addGradeLore(item);

                player.sendMessage(Component.text("═══════════════════════════").color(NamedTextColor.GOLD));
                player.sendMessage(Component.text("已添加属性").color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true));
                player.sendMessage(Component.text("═══════════════════════════").color(NamedTextColor.GOLD));
                player.sendMessage(Component.text("本次添加: ").color(NamedTextColor.GRAY)
                        .append(Component.text("属性 " + formatNumber(attribute) + "，耐久 " + formatNumber(durability))
                                .color(attribute >= 0 && durability >= 0 ? NamedTextColor.GREEN : NamedTextColor.RED)));
                player.sendMessage(Component.text("累计属性: ").color(NamedTextColor.GRAY)
                        .append(Component.text(formatNumber(newAttr)).color(newAttr >= 0 ? NamedTextColor.GREEN : NamedTextColor.RED)));
                if (item.getType().getMaxDurability() > 0) {
                    player.sendMessage(Component.text("累计耐久: ").color(NamedTextColor.GRAY)
                            .append(Component.text(formatNumber(newDura)).color(newDura >= 0 ? NamedTextColor.GREEN : NamedTextColor.RED)));
                }
                player.sendMessage(Component.text("═══════════════════════════").color(NamedTextColor.GOLD));

            } catch (NumberFormatException e) {
                player.sendMessage(PREFIX + "§c属性和耐久必须是有效数字");
            }
            return true;
        }

        // 解析固定等级
        GradeType grade = GradeType.fromName(type);
        if (grade == null) {
            player.sendMessage(PREFIX + "§c无效的等级: " + args[0]);
            player.sendMessage(PREFIX + "§e可用等级: §f" + getGradeList());
            return true;
        }

        // 检查物品是否有耐久
        if (!isDurable(item) && grade != GradeType.UNKNOWN) {
            player.sendMessage(PREFIX + "§c该物品没有耐久属性");
            return true;
        }

        // 获取原有属性值
        double oldAttr = GradeItemManager.getCurrentAttribute(item);
        int oldDura = GradeItemManager.getCurrentDurability(item);

        // 累加属性
        GradeItemManager.applyGrade(item, grade);

        // 获取新值
        double newAttr = GradeItemManager.getCurrentAttribute(item);
        int newDura = GradeItemManager.getCurrentDurability(item);

        // 更新lore
        ItemGradeListener.removeGradeLore(item);
        ItemGradeListener.addGradeLore(item);

        player.sendMessage(Component.text("═══════════════════════════").color(NamedTextColor.GOLD));
        player.sendMessage(Component.text("已添加等级: " + grade.getDisplayName()).color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true));
        player.sendMessage(Component.text("═══════════════════════════").color(NamedTextColor.GOLD));
        player.sendMessage(Component.text("等级: ").color(NamedTextColor.GRAY)
                .append(Component.text(grade.getDisplayName()).color(NamedTextColor.WHITE)));
        player.sendMessage(Component.text("累计属性: ").color(NamedTextColor.GRAY)
                .append(Component.text(formatNumber(newAttr)).color(newAttr >= 0 ? NamedTextColor.GREEN : NamedTextColor.RED)));
        if (item.getType().getMaxDurability() > 0) {
            player.sendMessage(Component.text("累计耐久: ").color(NamedTextColor.GRAY)
                    .append(Component.text(formatNumber(newDura)).color(newDura >= 0 ? NamedTextColor.GREEN : NamedTextColor.RED)));
        }
        player.sendMessage(Component.text("═══════════════════════════").color(NamedTextColor.GOLD));

        return true;
    }

    private boolean isDurable(ItemStack item) {
        return item.getType().getMaxDurability() > 0;
    }

    private String formatNumber(double num) {
        if (num == (int) num) {
            return num >= 0 ? "+" + (int) num : String.valueOf((int) num);
        }
        return num >= 0 ? "+" + num : String.valueOf(num);
    }

    private String getGradeList() {
        StringBuilder sb = new StringBuilder();
        for (GradeType grade : GradeType.values()) {
            if (sb.length() > 0) sb.append("§7, §f");
            sb.append(grade.name());
        }
        return sb.toString();
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Component.text("═══════════════════════════").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("快速设置手中物品等级").color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true));
        sender.sendMessage(Component.text("═══════════════════════════").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/eqset <等级>").color(NamedTextColor.GREEN)
                .append(Component.text(" - 添加固定等级属性（累加）").color(NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/eqset unknown <属性> <耐久>").color(NamedTextColor.GREEN)
                .append(Component.text(" - 添加自定义属性（累加）").color(NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("═══════════════════════════").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("可用等级: ").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text(getGradeList()).color(NamedTextColor.WHITE));
        sender.sendMessage(Component.text("═══════════════════════════").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("示例:").color(NamedTextColor.AQUA));
        sender.sendMessage(Component.text("/eqset HIGH").color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/eqset unknown 1 50").color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/eqset unknown -0.5 -25").color(NamedTextColor.GRAY));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (GradeType grade : GradeType.values()) {
                if (grade.name().toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(grade.name());
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("unknown")) {
            completions.add("1");
            completions.add("-1");
            completions.add("0.5");
            completions.add("-0.5");
        } else if (args.length == 3 && args[0].equalsIgnoreCase("unknown")) {
            completions.add("50");
            completions.add("-50");
            completions.add("100");
            completions.add("-100");
        }

        return completions;
    }
}
