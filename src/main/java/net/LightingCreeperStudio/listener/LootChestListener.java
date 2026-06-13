package net.LightingCreeperStudio.listener;

import net.LightingCreeperStudio.grade.GradeItemManager;
import net.LightingCreeperStudio.grade.GradeType;
import net.LightingCreeperStudio.grade.assignment.GradeAssignmentManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;

import java.util.List;

/**
 * 战利品箱监听器
 * 当战利品表生成时 为物品赋予随机等级
 *
 * 使用 LootGenerateEvent 监听战利品表生成，
 * 通过 LootTable 的 NamespacedKey 识别结构类型
 */
public class LootChestListener implements Listener {

    private final GradeAssignmentManager assignmentManager;

    public LootChestListener() {
        this.assignmentManager = GradeAssignmentManager.getInstance();
    }

    /**
     * 战利品表生成时触发
     * LootGenerateEvent 在原版战利品表填充物品后触发，
     * 可以直接修改生成的物品列表
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onLootGenerate(LootGenerateEvent event) {
        LootTable lootTable = event.getLootTable();
        if (lootTable == null) return;

        String key = lootTable.getKey().toString();

        // 只处理箱子战利品表
        if (!key.contains("chests")) return;

        // 根据战利品表识别结构类型
        String structureId = identifyByLootTable(lootTable);
        if (structureId == null) {
            structureId = "fallback";
        }

        // 为所有生成的物品赋予等级
        List<ItemStack> loot = event.getLoot();
        for (int i = 0; i < loot.size(); i++) {
            ItemStack item = loot.get(i);
            if (item != null && GradeItemManager.shouldGradeItem(item)) {
                GradeType grade = GradeItemManager.rollGrade(structureId);
                assignmentManager.assignFixedGrade(item, grade);
            }
        }
    }

    /**
     * 根据战利品表的 NamespacedKey 识别结构类型
     *
     * Minecraft 原版战利品表键名格:minecraft:chests/<结构名>
     * 例如:minecraft:chests/village/village_weaponsmith
     *       minecraft:chests/stronghold_corridor
     */
    private String identifyByLootTable(LootTable lootTable) {
        String key = lootTable.getKey().toString().toLowerCase();

        // ===== 村庄箱子 (minecraft:chests/village/*) =====
        if (key.contains("village")) return "village";

        // ===== 沙漠水井 (minecraft:chests/desert_well) =====
        if (key.contains("desert_well")) return "desert_well";

        // ===== 沉船 (minecraft:chests/shipwreck*) =====
        if (key.contains("shipwreck")) return "shipwreck_treasure";

        // ===== 沙漠神殿 (minecraft:chests/desert_pyramid) =====
        if (key.contains("desert_pyramid")) return "desert_pyramid";

        // ===== 丛林神殿 (minecraft:chests/jungle_temple) =====
        if (key.contains("jungle_temple")) return "jungle_temple";

        // ===== 废弃矿井 (minecraft:chests/abandoned_mineshaft) =====
        if (key.contains("abandoned_mineshaft") || key.contains("mineshaft")) return "abandoned_mineshaft";

        // ===== 沼泽小屋 (minecraft:chests/swamp_hut) =====
        if (key.contains("swamp_hut")) return "swamp_hut";

        // ===== 雪屋 (minecraft:chests/igloo_chest) =====
        if (key.contains("igloo")) return "igloo";

        // ===== 掠夺者前哨 (minecraft:chests/pillager_outpost) =====
        if (key.contains("pillager_outpost")) return "pillager_outpost";

        // ===== 要塞图书馆（需在要塞之前检查）(minecraft:chests/stronghold_library) =====
        if (key.contains("stronghold_library")) return "stronghold_library";

        // ===== 要塞 (minecraft:chests/stronghold_corridor / stronghold_crossing) =====
        if (key.contains("stronghold")) return "stronghold_corridor";

        // ===== 海底遗迹 (minecraft:chests/ocean_ruin* / underwater_ruin*) =====
        if (key.contains("ocean_ruin") || key.contains("underwater_ruin")) return "ocean_ruins";

        // ===== 林地府邸 (minecraft:chests/woodland_mansion) =====
        if (key.contains("woodland_mansion")) return "woodland_mansion";

        // ===== 下界堡垒 (minecraft:chests/nether_bridge) =====
        if (key.contains("nether_bridge")) return "nether_fortress";

        // ===== 堡垒遗迹 (minecraft:chests/bastion*) =====
        if (key.contains("bastion")) return "nether_fortress_high";

        // ===== 藏宝图宝藏 (minecraft:chests/buried_treasure) =====
        if (key.contains("buried_treasure")) return "buried_treasure";

        // ===== 简单地牢 (minecraft:chests/simple_dungeon) =====
        if (key.contains("simple_dungeon")) return "simple_dungeon";

        // ===== 末地城/末地船 (minecraft:chests/end_city_treasure) =====
        if (key.contains("end_city")) return "end_city";

        // ===== 远古城市 (minecraft:chests/ancient_city*) =====
        if (key.contains("ancient_city")) return "ancient_city";

        // ===== 废弃传送门 (minecraft:chests/ruined_portal) =====
        if (key.contains("ruined_portal")) return "fortress";

        // ===== 试炼密室 (minecraft:chests/trial_chambers*) - 1.21+ =====
        if (key.contains("trial_chambers")) return "stronghold";

        // 未匹配的战利品箱,使用 fallback
        return null;
    }
}
