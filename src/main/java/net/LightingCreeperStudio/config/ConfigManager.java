package net.LightingCreeperStudio.config;

import net.LightingCreeperStudio.EquipmentGrade;
import net.LightingCreeperStudio.grade.LootProbabilities;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置管理器
 * 负责从 config.yml 读取所有配置项
 */
public class ConfigManager {

    private static ConfigManager instance;
    private FileConfiguration config;

    // 背包扫描配置
    private boolean backpackScannerEnabled;
    private int backpackScannerIntervalMinutes;

    // 概率配置映射
    private final Map<String, double[]> probabilityConfigs = new HashMap<>();

    private ConfigManager() {
        loadConfig();
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    /**
     * 重新加载配置
     */
    public void reloadConfig() {
        loadConfig();
    }

    /**
     * 加载配置文件
     */
    private void loadConfig() {
        EquipmentGrade plugin = EquipmentGrade.getInstance();
        
        // 保存默认配置
        plugin.saveDefaultConfig();
        
        // 重新加载配置
        plugin.reloadConfig();
        config = plugin.getConfig();

        // 加载背包扫描配置
        loadBackpackScannerConfig();

        // 加载概率配置
        loadProbabilityConfigs();

        // 应用概率配置到 LootProbabilities
        applyProbabilityConfigs();
    }

    /**
     * 加载背包扫描配置
     */
    private void loadBackpackScannerConfig() {
        backpackScannerEnabled = config.getBoolean("backpack-scanner.enabled", true);
        backpackScannerIntervalMinutes = config.getInt("backpack-scanner.interval-minutes", 60);
    }

    /**
     * 加载所有概率配置
     */
    private void loadProbabilityConfigs() {
        probabilityConfigs.clear();
        
        String[] keys = {
            "village", "desert-well", "shipwreck",
            "desert-temple", "jungle-temple", "mineshaft", "swamp-hut", "igloo", "pillager-outpost",
            "stronghold", "stronghold-library", "ocean-ruins", "sunken-ship", 
            "woodland-mansion", "fortress", "buried-treasure", "simple-dungeon",
            "nether-fortress", "end-city", "ancient-city",
            "end-ship", "end-stronghold",
            "crafting", "villager-trade", "wandering-trade", "fishing", "item-frame", "entity-drop",
            "command-default", "command-random"
        };

        for (String key : keys) {
            String path = "probabilities." + key;
            if (config.contains(path)) {
                List<?> list = config.getList(path);
                if (list != null) {
                    double[] values = new double[16];
                    for (int i = 0; i < Math.min(list.size(), 16); i++) {
                        Object val = list.get(i);
                        if (val instanceof Number) {
                            values[i] = ((Number) val).doubleValue();
                        }
                    }
                    probabilityConfigs.put(key, values);
                }
            }
        }
    }

    /**
     * 将概率配置应用到 LootProbabilities 枚举
     */
    private void applyProbabilityConfigs() {
        // 结构类型映射
        applyConfig("village", LootProbabilities.VILLAGE);
        applyConfig("desert-well", LootProbabilities.DESERT_WELL);
        applyConfig("shipwreck", LootProbabilities.SHIPWRECK);
        applyConfig("desert-temple", LootProbabilities.DESERT_TEMPLE);
        applyConfig("jungle-temple", LootProbabilities.JUNGLE_TEMPLE);
        applyConfig("mineshaft", LootProbabilities.MINESHAFT);
        applyConfig("swamp-hut", LootProbabilities.SWAMP_HUT);
        applyConfig("igloo", LootProbabilities.IGLOO);
        applyConfig("pillager-outpost", LootProbabilities.PILLAGER_OUTPOST);
        applyConfig("stronghold", LootProbabilities.STRONGHOLD);
        applyConfig("stronghold-library", LootProbabilities.STRONGHOLD_LIBRARY);
        applyConfig("ocean-ruins", LootProbabilities.OCEAN_RUINS);
        applyConfig("sunken-ship", LootProbabilities.SUNKEN_SHIP);
        applyConfig("woodland-mansion", LootProbabilities.WOODLAND_MANSION);
        applyConfig("fortress", LootProbabilities.FORTRESS);
        applyConfig("buried-treasure", LootProbabilities.BURIED_TREASURE);
        applyConfig("simple-dungeon", LootProbabilities.SIMPLE_DUNGEON);
        applyConfig("nether-fortress", LootProbabilities.NETHER_FORTRESS);
        applyConfig("end-city", LootProbabilities.END_CITY);
        applyConfig("ancient-city", LootProbabilities.ANCIENT_CITY);
        applyConfig("end-ship", LootProbabilities.END_SHIP);
        applyConfig("end-stronghold", LootProbabilities.END_STRONGHOLD);
        
        // 来源类型映射
        applyConfig("crafting", LootProbabilities.CRAFTING);
        applyConfig("villager-trade", LootProbabilities.VILLAGER_TRADE);
        applyConfig("wandering-trade", LootProbabilities.WANDERING_TRADE);
        applyConfig("fishing", LootProbabilities.FISHING);
        applyConfig("item-frame", LootProbabilities.ITEM_FRAME);
        applyConfig("entity-drop", LootProbabilities.ENTITY_DROP);
        applyConfig("command-default", LootProbabilities.COMMAND_DEFAULT);
        applyConfig("command-random", LootProbabilities.COMMAND_RANDOM);
    }

    /**
     * 应用单个配置到 LootProbabilities
     */
    private void applyConfig(String key, LootProbabilities probabilities) {
        double[] config = probabilityConfigs.get(key);
        if (config != null) {
            probabilities.setCustomProbabilities(config);
        }
    }

    /**
     * 检查背包扫描是否启用
     */
    public boolean isBackpackScannerEnabled() {
        return backpackScannerEnabled;
    }

    /**
     * 获取背包扫描间隔（分钟）
     */
    public int getBackpackScannerIntervalMinutes() {
        return backpackScannerIntervalMinutes;
    }

    /**
     * 获取背包扫描间隔（游戏刻）
     * 1分钟 = 1200 游戏刻
     */
    public long getBackpackScannerIntervalTicks() {
        return backpackScannerIntervalMinutes * 1200L;
    }

    /**
     * 获取指定来源的概率配置
     * @param source 来源标识
     * @return 概率数组 如果不存在返回 null
     */
    public double[] getProbabilityConfig(String source) {
        return probabilityConfigs.get(source.toLowerCase());
    }

    /**
     * 检查是否有自定义概率配置
     */
    public boolean hasProbabilityConfig(String source) {
        return probabilityConfigs.containsKey(source.toLowerCase());
    }

    /**
     * 获取所有已配置的概率来源
     */
    public String[] getConfiguredSources() {
        return probabilityConfigs.keySet().toArray(new String[0]);
    }
}
