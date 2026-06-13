package net.LightingCreeperStudio.listener;

import net.LightingCreeperStudio.grade.EnchantLimitUtil;
import net.LightingCreeperStudio.grade.GradeType;
import net.LightingCreeperStudio.grade.NBTUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.view.AnvilView;

import java.util.Random;

/**
 * 铁砧监听器
 * 处理铁砧修复/附魔时的等级变化
 * - 5% 升级 (+1)
 * - 25% 掉级 (-1)(至宝★及以上仅 2%)
 * - 70% 不变
 *
 * 经验扣除封顶：
 * - 废品~上品：实际扣除封顶 40
 * - 极品：≥50 触发"过于昂贵" 否则支付实际费用
 * - 完美：≥75 触发"过于昂贵" 否则支付实际费用
 * - 灵宝★及以上:实际扣除封顶 75
 *
 * 核心逻辑：
 * - 预览阶段:只标记"品级待定"并显示 ???,不计算真实品级
 * - 取出物品时:掷骰确定品级
 * 玩家无法通过反复放入/取出铁砧来刷品级
 */
public class AnvilListener implements Listener {

    private static AnvilListener instance;

    private static final double UPGRADE_CHANCE = 5.0;    // 升级概率
    private static final double DOWNGRADE_CHANCE = 25.0; // 掉级概率

    private final Random random = new Random();

    public AnvilListener() {
        instance = this;
    }

    public static AnvilListener getInstance() {
        return instance;
    }

    /**
     * 铁砧准备事件 - 标记为品级待定 只显示 ???
     * 铁砧结果物品会继承 slot0 的 meta
     * 所以必须先移除旧品级 再设 pendingSource + ???
     * 否则玩家在预览阶段就能看到真实品级
     *
     * 注意：PrepareAnvilEvent 每次输入变化都会触发 每次 result 都是新的
     * 所以每次都需要先清除旧品级再设 pendingSource
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        // 先获取 slot0 的物品等级
        ItemStack slot0 = event.getInventory().getItem(0);
        if (slot0 == null || slot0.getType().isAir()) return;

        GradeType slot0Grade = NBTUtil.getGrade(slot0);
        if (slot0Grade == null) return;

        AnvilView anvilView = (AnvilView) event.getView();

        // === 处理铁砧经验上限 ===
        int maxAnvilCost = slot0Grade.getMaxAnvilCost();
        anvilView.setMaximumRepairCost(maxAnvilCost);

        // === 获取原始经验消耗 ===
        int originalRepairCost = anvilView.getRepairCost();

        // === 检查"过于昂贵" ===
        // 极品:≥50 触发过于昂贵
        // 完美:≥75 触发过于昂贵
        // 废品~上品、灵宝★及以上:永不触发
        if (maxAnvilCost != Integer.MAX_VALUE && originalRepairCost >= maxAnvilCost) {
            event.setResult(null);
            return;
        }

        // === 经验扣除封顶 ===
        // 仅对永不过于昂贵的品级有效：
        // 废品~上品:实际扣除封顶 40
        // 灵宝★及以上:实际扣除封顶 75
        // 极品/完美:由"过于昂贵"机制限制 不需要额外封顶
        int deductionCap = slot0Grade.getDeductionCap();
        if (deductionCap > 0 && originalRepairCost > deductionCap) {
            anvilView.setRepairCost(deductionCap);
        }

        ItemStack result = event.getResult();
        if (result == null || result.getType().isAir()) return;

        // 检测是否为纯改名操作:slot1 为空,且铁砧只修改了物品名称
        ItemStack slot1 = event.getInventory().getItem(1);
        boolean isRenameOnly = (slot1 == null || slot1.getType().isAir())
                && anvilView.getRenameText() != null;

        if (isRenameOnly) {
            // 纯改名不改品级 保留原有品级显示
            return;
        }

        // === 检查附魔限制 ===
        // 废品禁止附魔:清空结果
        if (slot0Grade.getMaxEnchantCount() == 0) {
            event.setResult(null);
            return;
        }

        // 附魔数量超过品级上限:清空结果
        if (slot0Grade.getMaxEnchantCount() > 0
                && EnchantLimitUtil.getEnchantCount(result) > slot0Grade.getMaxEnchantCount()) {
            event.setResult(null);
            return;
        }

        // 铁砧结果继承 slot0 的品级 NBT、lore 和属性修饰符 需要先清除
        // 这样预览阶段只显示 ???,不让玩家预言品质
        // removeGrade 内部先清 lore/属性再清 NBT,不依赖 getGrade 检查
        NBTUtil.removePendingSource(result);
        ItemGradeListener.removePendingLore(result);
        if (NBTUtil.getGrade(result) != null) {
            ItemGradeListener.removeGrade(result);
        }

        // 标记为品级待定 来源为 anvil
        NBTUtil.setPendingSource(result, "anvil");

        // 添加 ??? 占位符 lore
        ItemGradeListener.addPendingLore(result);
        event.setResult(result);
    }

    /**
     * 根据概率计算铁砧结果等级
     *
     * @param baseGrade slot0 的物品等级
     * @return 新的等级
     */
    public GradeType calculateAnvilGrade(GradeType baseGrade) {
        // 至宝★及以上:大幅降低掉级概率 (2%)
        if (baseGrade.ordinal() >= GradeType.SUPREME_TREASURE_1.ordinal()) {
            double roll = random.nextDouble() * 100;
            if (roll < 2.0) {
                return downgradeGrade(baseGrade);
            }
            return baseGrade;
        }

        double roll = random.nextDouble() * 100;

        if (roll < UPGRADE_CHANCE) {
            return upgradeGrade(baseGrade);
        } else if (roll < UPGRADE_CHANCE + DOWNGRADE_CHANCE) {
            return downgradeGrade(baseGrade);
        }

        return baseGrade;
    }

    /**
     * 升级等级
     */
    private GradeType upgradeGrade(GradeType current) {
        return switch (current) {
            case WASTE -> GradeType.DEFECTIVE;
            case DEFECTIVE -> GradeType.UNRANKED;
            case UNRANKED -> GradeType.COMMON;
            case COMMON -> GradeType.LOW;
            case LOW -> GradeType.MEDIUM;
            case MEDIUM -> GradeType.HIGH;
            case HIGH -> GradeType.EXCELLENT;
            case EXCELLENT -> GradeType.PERFECT;
            case PERFECT, SPIRIT_TREASURE_1, SPIRIT_TREASURE_2, SPIRIT_TREASURE_3,
                 SUPREME_TREASURE_1, SUPREME_TREASURE_2, SUPREME_TREASURE_3,
                 CELESTIAL, UNKNOWN -> current;
        };
    }

    /**
     * 掉级等级
     */
    private GradeType downgradeGrade(GradeType current) {
        return switch (current) {
            case CELESTIAL, SUPREME_TREASURE_3, SUPREME_TREASURE_2, SUPREME_TREASURE_1,
                 SPIRIT_TREASURE_3, SPIRIT_TREASURE_2, SPIRIT_TREASURE_1,
                 PERFECT -> GradeType.EXCELLENT;
            case EXCELLENT -> GradeType.HIGH;
            case HIGH -> GradeType.MEDIUM;
            case MEDIUM -> GradeType.LOW;
            case LOW -> GradeType.COMMON;
            case COMMON -> GradeType.UNRANKED;
            case UNRANKED -> GradeType.DEFECTIVE;
            case DEFECTIVE, WASTE, UNKNOWN -> current;
        };
    }

}
