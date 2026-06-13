package net.LightingCreeperStudio.command;

import net.LightingCreeperStudio.config.ConfigManager;
import net.LightingCreeperStudio.grade.GradeItemManager;
import net.LightingCreeperStudio.grade.GradeItemManager.GradeConfig;
import net.LightingCreeperStudio.grade.GradeType;
import net.LightingCreeperStudio.grade.ItemPropertyModifier;
import net.LightingCreeperStudio.grade.NBTUtil;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 物品等级管理指令
 * 用法:
 * /grade set <等级> - 设置手中物品的等级
 * /grade remove - 移除手中物品的等级
 * /grade unknown <属性> <耐久> - 设置未知等级物品的自定义属性
 * /grade info - 查看手中物品的等级信息
 */
public class GradeCommand implements CommandExecutor, TabCompleter {

    private static final String PREFIX = "§6[等级] §r";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "set" -> handleSet(sender, args);
            case "remove" -> handleRemove(sender);
            case "unknown" -> handleUnknown(sender, args);
            case "info" -> handleInfo(sender);
            case "reload" -> handleReload(sender);
            case "register" -> handleRegister(sender, args);
            case "unregister" -> handleUnregister(sender, args);
            case "list" -> handleList(sender);
            case "apply" -> handleApply(sender);
            case "upgrade" -> handleUpgrade(sender, args);
            default -> sendHelp(sender);
        }

        return true;
    }

    /**
     * 设置物品等级
     */
    private void handleSet(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(PREFIX + "§c只有玩家可以使用此指令");
            return;
        }

        if (!player.hasPermission("equipmentgrade.set")) {
            player.sendMessage(PREFIX + "§c你没有权限使用此指令");
            return;
        }

        if (args.length < 2) {
            player.sendMessage(PREFIX + "§c请指定等级: /grade set <等级>");
            player.sendMessage(PREFIX + "§e可用等级: §f" + getGradeList());
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage(PREFIX + "§c请手持要设置等级的物品");
            return;
        }

        // 解析等级
        GradeType grade = parseGrade(args[1]);
        if (grade == null) {
            player.sendMessage(PREFIX + "§c无效的等级: " + args[1]);
            player.sendMessage(PREFIX + "§e可用等级: §f" + getGradeList());
            return;
        }

        // 无耐久物品不支持耐久相关等级
        // 三叉戟、弓、弩等无耐久武器也可以有品级

        // 如果是未知等级且提供了参数
        int customAttr = 0;
        int customDura = 0;
        if (grade == GradeType.UNKNOWN && args.length >= 4) {
            try {
                customAttr = Integer.parseInt(args[2]);
                customDura = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                player.sendMessage(PREFIX + "§c参数格式错误,属性和耐久必须是数字");
                return;
            }
        }

        // 使用统一入口设置等级
        GradeItemManager.setGradeOverwrite(item, grade, customAttr, customDura);

        player.sendMessage(PREFIX + "§a已设置物品等级为: " + grade.getDisplayName());
    }

    /**
     * 移除物品等级
     */
    private void handleRemove(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(PREFIX + "§c只有玩家可以使用此指令");
            return;
        }

        if (!player.hasPermission("equipmentgrade.remove")) {
            player.sendMessage(PREFIX + "§c你没有权限使用此指令");
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage(PREFIX + "§c请手持要移除等级的物品");
            return;
        }

        if (!NBTUtil.hasGrade(item)) {
            player.sendMessage(PREFIX + "§c该物品没有设置等级");
            return;
        }

        // 使用统一入口移除等级
        ItemGradeListener.removeGrade(item);

        player.sendMessage(PREFIX + "§a已移除物品等级");
    }

    /**
     * 设置未知等级物品的自定义属性
     */
    private void handleUnknown(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(PREFIX + "§c只有玩家可以使用此指令");
            return;
        }

        if (!player.hasPermission("equipmentgrade.unknown")) {
            player.sendMessage(PREFIX + "§c你没有权限使用此指令");
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage(PREFIX + "§c请手持要设置的物品");
            return;
        }

        GradeType currentGrade = NBTUtil.getGrade(item);
        if (currentGrade != GradeType.UNKNOWN && currentGrade != null) {
            player.sendMessage(PREFIX + "§c该物品当前等级为: " + currentGrade.getDisplayName() + "§c,不是「未知」等级");
            return;
        }

        if (args.length < 3) {
            player.sendMessage(PREFIX + "§c用法: /grade unknown <属性> <耐久>");
            player.sendMessage(PREFIX + "§e属性: 正数增加属性,负数减少属性");
            player.sendMessage(PREFIX + "§e耐久: 正数增加耐久上限,负数减少耐久上限");
            return;
        }

        try {
            int attr = Integer.parseInt(args[1]);
            int dura = Integer.parseInt(args[2]);

            // 如果物品还没有等级 先设置为未知
            if (currentGrade == null) {
                NBTUtil.setGrade(item, GradeType.UNKNOWN);
            }

            NBTUtil.setCustomAttribute(item, attr);
            NBTUtil.setCustomDurability(item, dura);

            // 重新应用lore
            ItemGradeListener.removeGradeLore(item);
            ItemGradeListener.addGradeLore(item);

            player.sendMessage(PREFIX + "§a已设置自定义属性: §f属性 " + formatNumber(attr) + "§a, 耐久 " + formatNumber(dura));
        } catch (NumberFormatException e) {
            player.sendMessage(PREFIX + "§c参数必须是数字");
        }
    }

    /**
     * 查看物品等级信息
     */
    private void handleInfo(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(PREFIX + "§c只有玩家可以使用此指令");
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage(PREFIX + "§c请手持要查看的物品");
            return;
        }

        GradeType grade = NBTUtil.getGrade(item);

        if (grade == null) {
            player.sendMessage(PREFIX + "§7该物品没有设置等级");
            return;
        }

        player.sendMessage(Component.text("═══════════════════════════").color(NamedTextColor.GOLD));
        player.sendMessage(Component.text("物品等级信息").color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true));
        player.sendMessage(Component.text("═══════════════════════════").color(NamedTextColor.GOLD));
        player.sendMessage(Component.text("当前等级: ").color(NamedTextColor.GRAY)
                .append(Component.text(grade.getDisplayName()).color(NamedTextColor.WHITE)));
        player.sendMessage(Component.text("等级名称: ").color(NamedTextColor.GRAY)
                .append(Component.text(grade.getName()).color(NamedTextColor.WHITE)));

        double attrOffset = ItemPropertyModifier.getAppliedAttributeOffset(item);
        int duraOffset = ItemPropertyModifier.getAppliedDurabilityOffset(item);

        player.sendMessage(Component.text("属性调整: ").color(NamedTextColor.GRAY)
                .append(Component.text(formatDoubleNumber(attrOffset)).color(attrOffset >= 0 ? NamedTextColor.GREEN : NamedTextColor.RED)));
        
        // 正向+ 负向-
        int actualDuraOffset = attrOffset >= 0 ? duraOffset : -duraOffset;
        player.sendMessage(Component.text("耐久调整: ").color(NamedTextColor.GRAY)
                .append(Component.text(formatNumber(actualDuraOffset)).color(actualDuraOffset >= 0 ? NamedTextColor.GREEN : NamedTextColor.RED)));

        // 显示当前最大耐久
        Material material = item.getType();
        if (material.getMaxDurability() > 0) {
            player.sendMessage(Component.text("当前最大耐久: ").color(NamedTextColor.GRAY)
                    .append(Component.text(String.valueOf(material.getMaxDurability() + actualDuraOffset)).color(NamedTextColor.AQUA)));
        }

        player.sendMessage(Component.text("═══════════════════════════").color(NamedTextColor.GOLD));
    }

    /**
     * 重载配置
     */
    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("equipmentgrade.reload")) {
            sender.sendMessage(PREFIX + "§c你没有权限使用此指令");
            return;
        }

        // 重载配置文件
        ConfigManager.getInstance().reloadConfig();
        sender.sendMessage(PREFIX + "§a插件配置已重载");
    }

    /**
     * 注册物品等级配置
     * /grade register <物品ID> <等级>
     */
    private void handleRegister(CommandSender sender, String[] args) {
        if (!sender.hasPermission("equipmentgrade.register")) {
            sender.sendMessage(PREFIX + "§c你没有权限使用此指令");
            return;
        }

        if (args.length < 3) {
            sender.sendMessage(PREFIX + "§c用法: /grade register <物品ID> <等级>");
            sender.sendMessage(PREFIX + "§e示例: /grade register diamond_sword HIGH");
            sender.sendMessage(PREFIX + "§e示例: /grade register iron_helmet MEDIUM 1 25");
            sender.sendMessage(PREFIX + "§e最后两个参数为自定义属性和耐久");
            return;
        }

        String itemId = args[1];
        GradeType grade = parseGrade(args[2]);
        if (grade == null) {
            sender.sendMessage(PREFIX + "§c无效的等级: " + args[2]);
            return;
        }

        try {
            if (args.length >= 5) {
                int customAttr = Integer.parseInt(args[3]);
                int customDura = Integer.parseInt(args[4]);
                GradeItemManager.registerItemGrade(itemId, grade, customAttr, customDura);
                sender.sendMessage(PREFIX + "§a已注册物品 §f" + itemId + " §a为等级 §f" + grade.getDisplayName() 
                        + " §a(属性:" + formatNumber(customAttr) + " 耐久:" + formatNumber(customDura) + ")");
            } else {
                GradeItemManager.registerItemGrade(itemId, grade);
                sender.sendMessage(PREFIX + "§a已注册物品 §f" + itemId + " §a为等级 §f" + grade.getDisplayName());
            }
        } catch (IllegalArgumentException e) {
            sender.sendMessage(PREFIX + "§c" + e.getMessage());
        }
    }

    /**
     * 取消注册物品等级
     * /grade unregister <物品ID>
     */
    private void handleUnregister(CommandSender sender, String[] args) {
        if (!sender.hasPermission("equipmentgrade.register")) {
            sender.sendMessage(PREFIX + "§c你没有权限使用此指令");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(PREFIX + "§c用法: /grade unregister <物品ID>");
            return;
        }

        String itemId = args[1];
        if (GradeItemManager.isItemRegistered(itemId)) {
            GradeItemManager.unregisterItemGrade(itemId);
            sender.sendMessage(PREFIX + "§a已取消注册物品 §f" + itemId);
        } else {
            sender.sendMessage(PREFIX + "§c该物品 §f" + itemId + " §c未注册");
        }
    }

    /**
     * 列出所有注册的物品
     * /grade list
     */
    private void handleList(CommandSender sender) {
        if (!sender.hasPermission("equipmentgrade.list")) {
            sender.sendMessage(PREFIX + "§c你没有权限使用此指令");
            return;
        }

        Material[] registered = GradeItemManager.getRegisteredItems();
        if (registered.length == 0) {
            sender.sendMessage(PREFIX + "§7暂无注册的物品");
            return;
        }

        sender.sendMessage(Component.text("═══════════════════════════").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("已注册的物品等级配置").color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true));
        sender.sendMessage(Component.text("═══════════════════════════").color(NamedTextColor.GOLD));

        for (Material mat : registered) {
            GradeConfig config = GradeItemManager.getGradeConfig(new ItemStack(mat));
            sender.sendMessage(Component.text(mat.name() + ": ").color(NamedTextColor.GRAY)
                    .append(Component.text(config.getGrade().getDisplayName()).color(NamedTextColor.WHITE)));
        }

        sender.sendMessage(Component.text("═══════════════════════════").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("共 " + registered.length + " 个物品").color(NamedTextColor.YELLOW));
    }

    /**
     * 对手中物品应用注册的等级
     * /grade apply
     */
    private void handleApply(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(PREFIX + "§c只有玩家可以使用此指令");
            return;
        }

        if (!player.hasPermission("equipmentgrade.apply")) {
            player.sendMessage(PREFIX + "§c你没有权限使用此指令");
            return;
        }

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        boolean mainApplied = false;
        boolean offApplied = false;

        if (mainHand.getType() != Material.AIR) {
            GradeConfig config = GradeItemManager.getGradeConfig(mainHand);
            if (config != null) {
                GradeItemManager.applyGrade(mainHand, config.getGrade(), config.getCustomAttribute(), config.getCustomDurability());
                ItemGradeListener.addGradeLore(mainHand);
                mainApplied = true;
            }
        }

        if (offHand.getType() != Material.AIR) {
            GradeConfig config = GradeItemManager.getGradeConfig(offHand);
            if (config != null) {
                GradeItemManager.applyGrade(offHand, config.getGrade(), config.getCustomAttribute(), config.getCustomDurability());
                ItemGradeListener.addGradeLore(offHand);
                offApplied = true;
            }
        }

        if (mainApplied || offApplied) {
            StringBuilder msg = new StringBuilder(PREFIX + "§a已应用等级:");
            if (mainApplied) msg.append(" 主手");
            if (offApplied) msg.append(" 副手");
            player.sendMessage(msg.toString());
        } else {
            player.sendMessage(PREFIX + "§c手中物品未注册等级配置");
        }
    }

    /**
     * 提升/降低手中物品等级
     * /grade upgrade <步数>
     */
    private void handleUpgrade(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(PREFIX + "§c只有玩家可以使用此指令");
            return;
        }

        if (!player.hasPermission("equipmentgrade.upgrade")) {
            player.sendMessage(PREFIX + "§c你没有权限使用此指令");
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage(PREFIX + "§c请手持要升级的物品");
            return;
        }

        int steps = 1; // 默认提升1级
        if (args.length >= 2) {
            try {
                steps = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(PREFIX + "§c步数必须是数字");
                return;
            }
        }

        GradeType oldGrade = GradeItemManager.getGradeNBT(item);
        GradeType newGrade = GradeItemManager.upgradeGrade(item, steps);

        // 重新应用lore
        ItemGradeListener.removeGradeLore(item);
        ItemGradeListener.addGradeLore(item);

        if (oldGrade == null) {
            player.sendMessage(PREFIX + "§a已设置物品等级为: §f" + newGrade.getDisplayName());
        } else {
            player.sendMessage(PREFIX + "§a物品等级从 §f" + oldGrade.getDisplayName() + " §a提升至 §f" + newGrade.getDisplayName());
        }
    }

    /**
     * 发送帮助信息
     */
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Component.text("═══════════════════════════").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("物品等级系统").color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true));
        sender.sendMessage(Component.text("═══════════════════════════").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/grade set <等级>").color(NamedTextColor.GREEN)
                .append(Component.text(" - 设置手中物品等级").color(NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/grade remove").color(NamedTextColor.GREEN)
                .append(Component.text(" - 移除手中物品等级").color(NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/grade unknown <属性> <耐久>").color(NamedTextColor.GREEN)
                .append(Component.text(" - 设置未知等级属性").color(NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/grade info").color(NamedTextColor.GREEN)
                .append(Component.text(" - 查看手中物品等级信息").color(NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/grade upgrade [步数]").color(NamedTextColor.GREEN)
                .append(Component.text(" - 提升/降低物品等级").color(NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("═══════════════════════════").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("物品ID注册命令:").color(NamedTextColor.AQUA));
        sender.sendMessage(Component.text("/grade register <物品ID> <等级>").color(NamedTextColor.GREEN)
                .append(Component.text(" - 注册物品等级").color(NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/grade unregister <物品ID>").color(NamedTextColor.GREEN)
                .append(Component.text(" - 取消注册").color(NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/grade list").color(NamedTextColor.GREEN)
                .append(Component.text(" - 查看已注册物品").color(NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/grade apply").color(NamedTextColor.GREEN)
                .append(Component.text(" - 应用手中物品的注册等级").color(NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("═══════════════════════════").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("可用等级: ").color(NamedTextColor.YELLOW)
                .append(Component.text(getGradeList()).color(NamedTextColor.WHITE)));
    }

    /**
     * 解析等级名称
     */
    private GradeType parseGrade(String name) {
        return GradeType.fromName(name);
    }

    /**
     * 获取等级列表
     */
    private String getGradeList() {
        return Arrays.stream(GradeType.values())
                .map(GradeType::getName)
                .collect(Collectors.joining("§7, §f"));
    }

    /**
     * 格式化数字显示
     */
    private String formatNumber(int num) {
        return num >= 0 ? "+" + num : String.valueOf(num);
    }

    /**
     * 格式化double数字显示
     */
    private String formatDoubleNumber(double num) {
        if (num == (int) num) {
            return num >= 0 ? "+" + (int) num : String.valueOf((int) num);
        }
        return num >= 0 ? "+" + num : String.valueOf(num);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("set");
            completions.add("remove");
            completions.add("unknown");
            completions.add("info");
            completions.add("upgrade");
            completions.add("apply");
            if (sender.hasPermission("equipmentgrade.register")) {
                completions.add("register");
                completions.add("unregister");
                completions.add("list");
            }
            if (sender.hasPermission("equipmentgrade.reload")) {
                completions.add("reload");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            for (GradeType grade : GradeType.values()) {
                if (grade.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(grade.name());
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("register")) {
            for (Material mat : Material.values()) {
                if (mat.isItem() && mat.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(mat.name().toLowerCase());
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("register")) {
            for (GradeType grade : GradeType.values()) {
                if (grade.name().toLowerCase().startsWith(args[2].toLowerCase())) {
                    completions.add(grade.name());
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("unregister")) {
            for (Material mat : GradeItemManager.getRegisteredItems()) {
                completions.add(mat.name().toLowerCase());
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("unknown")) {
            completions.add("<属性值>");
        } else if (args.length == 3 && args[0].equalsIgnoreCase("unknown")) {
            completions.add("<耐久值>");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("upgrade")) {
            completions.add("1");
            completions.add("-1");
            completions.add("5");
            completions.add("-5");
        }

        return completions;
    }
}
