package net.LightingCreeperStudio.grade;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 物品黑名单管理器
 * 用于禁止特定物品加入品级系统
 */
public class ItemBlacklist {

    private static Set<String> blacklist = new HashSet<>();
    private static File blacklistFile;

    /**
     * 初始化黑名单
     * @param dataFolder 插件数据文件夹
     */
    public static void init(File dataFolder) {
        blacklistFile = new File(dataFolder, "blacklist.yml");
        
        if (!blacklistFile.exists()) {
            // 创建默认黑名单
            createDefaultBlacklist();
        }
        
        loadBlacklist();
    }

    /**
     * 创建默认黑名单配置
     */
    private static void createDefaultBlacklist() {
        FileConfiguration config = new YamlConfiguration();
        
        // 默认黑名单物品(示例)
        config.set("blacklist", List.of(
            "NETHER_STAR",
            "TOTEM_OF_UNDYING",
            "FIREWORK_ROCKET",
            "BOOK",
            "PAPER"
        ));
        
        config.set("comment", "物品ID黑名单 被列入的物品无法加入品级系统");
        
        try {
            config.save(blacklistFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载黑名单
     */
    public static void loadBlacklist() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(blacklistFile);
        List<String> list = config.getStringList("blacklist");
        blacklist = new HashSet<>(list);
    }

    /**
     * 重新加载黑名单
     */
    public static void reload() {
        loadBlacklist();
    }

    /**
     * 检查物品是否在黑名单中
     * @param material 物品材质
     * @return 是否在黑名单中
     */
    public static boolean isBlacklisted(Material material) {
        if (material == null) return true;
        return blacklist.contains(material.name());
    }

    /**
     * 检查物品ID是否在黑名单中
     * @param itemId 物品ID
     * @return 是否在黑名单中
     */
    public static boolean isBlacklisted(String itemId) {
        if (itemId == null) return true;
        return blacklist.contains(itemId.toUpperCase());
    }

    /**
     * 添加物品到黑名单
     * @param itemId 物品ID
     */
    public static void addToBlacklist(String itemId) {
        blacklist.add(itemId.toUpperCase());
        saveBlacklist();
    }

    /**
     * 从黑名单移除物品
     * @param itemId 物品ID
     */
    public static void removeFromBlacklist(String itemId) {
        blacklist.remove(itemId.toUpperCase());
        saveBlacklist();
    }

    /**
     * 获取黑名单集合
     * @return 黑名单集合
     */
    public static Set<String> getBlacklist() {
        return new HashSet<>(blacklist);
    }

    /**
     * 保存黑名单到文件
     */
    private static void saveBlacklist() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(blacklistFile);
        config.set("blacklist", blacklist.stream().sorted().toList());
        try {
            config.save(blacklistFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取黑名单文件
     * @return 黑名单配置文件
     */
    public static File getBlacklistFile() {
        return blacklistFile;
    }
}
