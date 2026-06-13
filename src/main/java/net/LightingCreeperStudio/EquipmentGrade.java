package net.LightingCreeperStudio;

import net.LightingCreeperStudio.command.EqSetCommand;
import net.LightingCreeperStudio.command.GradeCommand;
import net.LightingCreeperStudio.config.ConfigManager;
import net.LightingCreeperStudio.grade.ItemBlacklist;
import net.LightingCreeperStudio.listener.AnvilListener;
import net.LightingCreeperStudio.listener.CraftingListener;
import net.LightingCreeperStudio.listener.EnchantListener;
import net.LightingCreeperStudio.listener.EntityDeathListener;
import net.LightingCreeperStudio.listener.FishingListener;
import net.LightingCreeperStudio.listener.GradeAttributeListener;
import net.LightingCreeperStudio.listener.ItemFrameListener;
import net.LightingCreeperStudio.listener.ItemGradeListener;
import net.LightingCreeperStudio.listener.LootChestListener;
import net.LightingCreeperStudio.listener.VillagerLevelUpListener;
import net.LightingCreeperStudio.listener.VillagerTradeListener;
import net.LightingCreeperStudio.listener.WanderingTradeListener;
import net.LightingCreeperStudio.task.BackpackScanner;
import org.bukkit.plugin.java.JavaPlugin;

public class EquipmentGrade extends JavaPlugin {

    private static EquipmentGrade instance;

    @Override
    public void onEnable() {
        instance = this;

        // 初始化配置管理器
        ConfigManager.getInstance();

        // 初始化物品黑名单
        ItemBlacklist.init(getDataFolder());

        // 注册事件监听器 - 战利品箱
        getServer().getPluginManager().registerEvents(new LootChestListener(), this);

        // 注册事件监听器 - 物品展示框
        getServer().getPluginManager().registerEvents(new ItemFrameListener(), this);

        // 注册事件监听器 - 村民升级
        getServer().getPluginManager().registerEvents(new VillagerLevelUpListener(), this);

        // 注册事件监听器 - 合成相关
        getServer().getPluginManager().registerEvents(new CraftingListener(), this);
        getServer().getPluginManager().registerEvents(new AnvilListener(), this);

        // 注册事件监听器 - 附魔台
        getServer().getPluginManager().registerEvents(new EnchantListener(), this);

        // 注册事件监听器 - 村民交易
        getServer().getPluginManager().registerEvents(new VillagerTradeListener(), this);

        // 注册事件监听器 - 流浪商人交易
        getServer().getPluginManager().registerEvents(new WanderingTradeListener(), this);

        // 注册事件监听器 - 钓鱼
        getServer().getPluginManager().registerEvents(new FishingListener(), this);

        // 注册事件监听器 - 生物掉落
        getServer().getPluginManager().registerEvents(new EntityDeathListener(), this);

        // 注册事件监听器 - 物品等级
        getServer().getPluginManager().registerEvents(new ItemGradeListener(), this);
        getServer().getPluginManager().registerEvents(new GradeAttributeListener(), this);

        // 注册指令
        GradeCommand gradeCommand = new GradeCommand();
        getCommand("grade").setExecutor(gradeCommand);
        getCommand("grade").setTabCompleter(gradeCommand);

        // 注册快速设置指令
        EqSetCommand eqSetCommand = new EqSetCommand();
        getCommand("eqset").setExecutor(eqSetCommand);
        getCommand("eqset").setTabCompleter(eqSetCommand);

        // 启动背包扫描定时任务
        startBackpackScanner();

        getLogger().info("EquipmentGrade 系统已启用!");
        getLogger().info("可用等级: 废品 -> 残次品 -> 不入品 -> 凡品 -> 下品 -> 中品 -> 上品 -> 极品 -> 完美 -> 灵宝★~★★★ -> 至宝★~★★★ -> 先天 -> 未知");
    }

    /**
     * 启动背包扫描定时任务
     */
    private void startBackpackScanner() {
        ConfigManager config = ConfigManager.getInstance();
        if (!config.isBackpackScannerEnabled()) {
            getLogger().info("背包扫描已禁用");
            return;
        }

        BackpackScanner scanner = new BackpackScanner();
        long intervalTicks = config.getBackpackScannerIntervalTicks();
        int intervalMinutes = config.getBackpackScannerIntervalMinutes();
        
        // 首次执行延迟 1 分钟 之后按配置间隔执行
        getServer().getScheduler().runTaskTimer(this, () -> scanner.scanAllPlayers(), 1200L, intervalTicks);
        getLogger().info("背包扫描已启用 间隔: " + intervalMinutes + " 分钟");
    }

    @Override
    public void onDisable() {
        getLogger().info("EquipmentGrade 系统已禁用!");
    }

    public static EquipmentGrade getInstance() {
        return instance;
    }
}
