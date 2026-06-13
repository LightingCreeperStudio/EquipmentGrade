package net.LightingCreeperStudio.grade;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * 装备命名器
 * 完美品级及以上的装备自动从取名池抽取 A+B 组合名称
 * 命名规则：
 * - 品级 >= 完美 + 尚未被自动命名过 → 随机取名
 * - 命名只执行一次 已有装备名的不重复生成
 * - 品级变更时:降级到完美以下移除命名，品级变化更新颜色
 * - 玩家主动改名后不被覆盖
 * - 装备名通过 displayName 显示 不在 lore 中显示
 */
public class EquipmentNamer {

    /**
     * 尝试为装备自动命名
     * 条件:品级 >= 完美 且 尚未被自动命名过
     * 只执行一次 已有装备名的不重复生成
     *
     * @param item 物品
     * @param grade 品级
     */
    public static void tryAutoName(ItemStack item, GradeType grade) {
        if (item == null || grade == null) return;
        if (!EquipmentNamePool.hasNamePool(item.getType())) return;

        // 已有装备名 = 不再重复
        if (NBTUtil.hasEquipmentName(item)) return;

        // 品级不够
        if (!isEligibleForNaming(grade)) return;

        // 品级达标且从未命名过
        String name = EquipmentNamePool.rollName(item.getType());
        if (name != null) {
            NBTUtil.setEquipmentName(item, name);
            setDisplayName(item, name, grade);
        }
    }

    /**
     * 品级变更时处理装备命名
     * - 降级到完美以下:移除自动命名
     * - 品级变化:更新装备名颜色
     *
     * @param item 物品
     * @param newGrade 新品级
     */
    public static void onGradeChange(ItemStack item, GradeType newGrade) {
        if (item == null || newGrade == null) return;

        if (!NBTUtil.hasEquipmentName(item)) return;

        String equipName = NBTUtil.getEquipmentName(item);
        if (equipName == null) return;

        if (isEligibleForNaming(newGrade)) {
            // 品级仍 >= 完美 更新显示名颜色
            if (isDisplayNameMatchEquipmentName(item, equipName)) {
                setDisplayName(item, equipName, newGrade);
            }
        } else {
            // 品级降到完美以下 移除自动命名
            NBTUtil.removeEquipmentName(item);
            // 仅当 displayName 匹配装备名时才清除 避免清除玩家自定义名
            if (isDisplayNameMatchEquipmentName(item, equipName)) {
                clearCustomDisplayName(item);
            }
        }
    }

    /**
     * 判断品级是否达到命名标准
     */
    public static boolean isEligibleForNaming(GradeType grade) {
        if (grade == null) return false;
        return grade.ordinal() >= GradeType.PERFECT.ordinal();
    }

    /**
     * 检查物品的 displayName 是否与装备名一致
     */
    private static boolean isDisplayNameMatchEquipmentName(ItemStack item, String equipName) {
        if (item == null || !item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return false;

        String displayName = PlainTextComponentSerializer.plainText().serialize(meta.displayName());
        return equipName.equals(displayName);
    }

    /**
     * 设置物品的显示名称
     */
    private static void setDisplayName(ItemStack item, String equipmentName, GradeType grade) {
        if (item == null || equipmentName == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        meta.displayName(Component.text(equipmentName, grade.getColor())
                .decoration(net.kyori.adventure.text.format.TextDecoration.ITALIC, false));

        item.setItemMeta(meta);
    }

    /**
     * 清除自定义显示名 恢复原版物品名
     */
    private static void clearCustomDisplayName(ItemStack item) {
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        // 恢复为 null 让 Minecraft 使用默认物品名
        meta.displayName(null);
        item.setItemMeta(meta);
    }
}
