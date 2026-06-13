package net.LightingCreeperStudio.grade;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * NBT 工具类 - 处理物品的 NBT 标签读写
 */
public final class NBTUtil {

    public static final String NBT_KEY_GRADE = "equipmentgrade";
    public static final String NBT_KEY_CUSTOM_ATTRIBUTE = "customattribute";
    public static final String NBT_KEY_CUSTOM_DURABILITY = "customdurability";
    public static final String NBT_KEY_PENDING_SOURCE = "pendinggradesource";
    public static final String NBT_KEY_CUSTOM_MAX_DURABILITY = "custommaxdurability";
    public static final String NBT_KEY_EQUIPMENT_NAME = "equipmentname";
    public static final String NBT_KEY_ANVIL_RESULT = "anvilresult";
    private NBTUtil() {}

    /**
     * 设置物品等级
     */
    public static void setGrade(ItemStack item, GradeType grade) {
        if (item == null || item.getType() == Material.AIR) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        NamespacedKey key = new NamespacedKey("equipmentgrade", NBT_KEY_GRADE);
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, grade.name());

        item.setItemMeta(meta);
    }

    /**
     * 获取物品等级
     */
    public static GradeType getGrade(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey("equipmentgrade", NBT_KEY_GRADE);
        String value = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);

        if (value == null) return null;

        try {
            return GradeType.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 设置品级待定来源
     * 物品在预览阶段不分配真实品级 只标记来源
     */
    public static void setPendingSource(ItemStack item, String source) {
        if (item == null || item.getType() == Material.AIR) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        NamespacedKey key = new NamespacedKey("equipmentgrade", NBT_KEY_PENDING_SOURCE);
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, source);

        item.setItemMeta(meta);
    }

    /**
     * 获取品级待定来源
     * @return 来源标识 如果没有待定品级则返回 null
     */
    public static String getPendingSource(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey("equipmentgrade", NBT_KEY_PENDING_SOURCE);
        String value = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);

        return value;
    }

    /**
     * 检查物品是否有品级待定标记
     */
    public static boolean hasPendingGrade(ItemStack item) {
        return getPendingSource(item) != null;
    }

    /**
     * 移除品级待定标记
     */
    public static void removePendingSource(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey("equipmentgrade", NBT_KEY_PENDING_SOURCE);
        meta.getPersistentDataContainer().remove(key);

        item.setItemMeta(meta);
    }

    /**
     * 检查物品是否有等级标签
     */
    public static boolean hasGrade(ItemStack item) {
        return getGrade(item) != null;
    }

    /**
     * 设置自定义属性值
     */
    public static void setCustomAttribute(ItemStack item, int attribute) {
        if (item == null || item.getType() == Material.AIR) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        NamespacedKey key = new NamespacedKey("equipmentgrade", NBT_KEY_CUSTOM_ATTRIBUTE);
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, attribute);

        item.setItemMeta(meta);
    }

    /**
     * 获取自定义属性值
     */
    public static int getCustomAttribute(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 0;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey("equipmentgrade", NBT_KEY_CUSTOM_ATTRIBUTE);
        Integer value = meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);

        return value != null ? value : 0;
    }

    /**
     * 设置自定义耐久值
     */
    public static void setCustomDurability(ItemStack item, int durability) {
        if (item == null || item.getType() == Material.AIR) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        NamespacedKey key = new NamespacedKey("equipmentgrade", NBT_KEY_CUSTOM_DURABILITY);
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, durability);

        item.setItemMeta(meta);
    }

    /**
     * 获取自定义耐久值
     */
    public static int getCustomDurability(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 0;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey("equipmentgrade", NBT_KEY_CUSTOM_DURABILITY);
        Integer value = meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);

        return value != null ? value : 0;
    }

    /**
     * 设置自定义最大耐久
     */
    public static void setCustomMaxDurability(ItemStack item, int maxDurability) {
        if (item == null || item.getType() == Material.AIR) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        NamespacedKey key = new NamespacedKey("equipmentgrade", NBT_KEY_CUSTOM_MAX_DURABILITY);
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, maxDurability);

        item.setItemMeta(meta);
    }

    /**
     * 获取自定义最大耐久
     * @return 自定义最大耐久值 如果没有设置则返回 -1
     */
    public static int getCustomMaxDurability(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return -1;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey("equipmentgrade", NBT_KEY_CUSTOM_MAX_DURABILITY);
        Integer value = meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);

        return value != null ? value : -1;
    }

    /**
     * 检查物品是否有自定义最大耐久
     */
    public static boolean hasCustomMaxDurability(ItemStack item) {
        return getCustomMaxDurability(item) >= 0;
    }

    /**
     * 移除物品等级NBT数据
     */
    public static void removeGrade(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        NamespacedKey gradeKey = new NamespacedKey("equipmentgrade", NBT_KEY_GRADE);
        NamespacedKey attrKey = new NamespacedKey("equipmentgrade", NBT_KEY_CUSTOM_ATTRIBUTE);
        NamespacedKey duraKey = new NamespacedKey("equipmentgrade", NBT_KEY_CUSTOM_DURABILITY);
        NamespacedKey maxDuraKey = new NamespacedKey("equipmentgrade", NBT_KEY_CUSTOM_MAX_DURABILITY);
        NamespacedKey equipNameKey = new NamespacedKey("equipmentgrade", NBT_KEY_EQUIPMENT_NAME);

        container.remove(gradeKey);
        container.remove(attrKey);
        container.remove(duraKey);
        container.remove(maxDuraKey);
        container.remove(equipNameKey);

        item.setItemMeta(meta);
    }

    /**
     * 设置装备自定义名称
     */
    public static void setEquipmentName(ItemStack item, String name) {
        if (item == null || item.getType() == Material.AIR) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        NamespacedKey key = new NamespacedKey("equipmentgrade", NBT_KEY_EQUIPMENT_NAME);
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, name);

        item.setItemMeta(meta);
    }

    /**
     * 获取装备自定义名称
     * @return 自定义名称 如果没有则返回 null
     */
    public static String getEquipmentName(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey("equipmentgrade", NBT_KEY_EQUIPMENT_NAME);
        return meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }

    /**
     * 检查物品是否有装备自定义名称
     */
    public static boolean hasEquipmentName(ItemStack item) {
        return getEquipmentName(item) != null;
    }

    /**
     * 移除装备自定义名称
     */
    public static void removeEquipmentName(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey("equipmentgrade", NBT_KEY_EQUIPMENT_NAME);
        meta.getPersistentDataContainer().remove(key);

        item.setItemMeta(meta);
    }

    /**
     * 设置铁砧结果标记
     */
    public static void setAnvilResultMarker(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        NamespacedKey key = new NamespacedKey("equipmentgrade", NBT_KEY_ANVIL_RESULT);
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);

        item.setItemMeta(meta);
    }

    /**
     * 检查物品是否有铁砧结果标记
     */
    public static boolean hasAnvilResultMarker(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey("equipmentgrade", NBT_KEY_ANVIL_RESULT);
        Byte value = meta.getPersistentDataContainer().get(key, PersistentDataType.BYTE);

        return value != null && value == 1;
    }

    /**
     * 移除铁砧结果标记
     */
    public static void removeAnvilResultMarker(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey("equipmentgrade", NBT_KEY_ANVIL_RESULT);
        meta.getPersistentDataContainer().remove(key);

        item.setItemMeta(meta);
    }

    /**
     * 检查物品是否有耐久属性
     */
    public static boolean hasDurability(Material material) {
        return material.getMaxDurability() > 0;
    }

    /**
     * 创建带等级的物品lore
     */
    public static List<Component> createGradeLore(GradeType grade, int customAttribute, int customDurability) {
        List<Component> lore = new ArrayList<>();

        // 所有品级:添加品级名行
        lore.add(Component.text(grade.getName(), grade.getColor())
                .decoration(TextDecoration.ITALIC, false));

        // UNKNOWN 等级:额外显示自定义属性/耐久详情
        if (grade == GradeType.UNKNOWN) {
            boolean hasDetails = (customAttribute != 0 || customDurability != 0);
            if (hasDetails) {
                lore.add(Component.text("─────────────────────", NamedTextColor.GRAY)
                        .decoration(TextDecoration.STRIKETHROUGH, true)
                        .decoration(TextDecoration.ITALIC, false));
            }

            if (customAttribute != 0) {
                String attrText = formatSignedNumber(customAttribute);
                lore.add(Component.text()
                        .append(Component.text("[", NamedTextColor.YELLOW))
                        .append(Component.text("属性", NamedTextColor.BLUE))
                        .append(Component.text("] ", NamedTextColor.YELLOW))
                        .append(Component.text(attrText, getValueColor(customAttribute)))
                        .decoration(TextDecoration.ITALIC, false)
                        .build());
            }
            if (customDurability != 0) {
                String duraText = formatSignedNumber(customDurability);
                lore.add(Component.text()
                        .append(Component.text("[", NamedTextColor.YELLOW))
                        .append(Component.text("耐久", NamedTextColor.BLUE))
                        .append(Component.text("] ", NamedTextColor.YELLOW))
                        .append(Component.text(duraText, getValueColor(customDurability)))
                        .decoration(TextDecoration.ITALIC, false)
                        .build());
            }

            if (hasDetails) {
                lore.add(Component.text("─────────────────────", NamedTextColor.GRAY)
                        .decoration(TextDecoration.STRIKETHROUGH, true)
                        .decoration(TextDecoration.ITALIC, false));
            }
        }

        return lore;
    }

    /**
     * 格式化带符号的数字
     */
    private static String formatSignedNumber(int num) {
        return num >= 0 ? "+" + num : String.valueOf(num);
    }

    /**
     * 格式化带符号的double数字
     */
    private static String formatSignedDouble(double num) {
        if (num == (int) num) {
            return num >= 0 ? "+" + (int) num : String.valueOf((int) num);
        }
        return num >= 0 ? "+" + num : String.valueOf(num);
    }

    /**
     * 根据数值获取颜色
     */
    private static NamedTextColor getValueColor(int value) {
        if (value > 0) return NamedTextColor.GREEN;
        if (value < 0) return NamedTextColor.RED;
        return NamedTextColor.GRAY;
    }

    /**
     * 根据数值获取颜色
     */
    private static NamedTextColor getValueColor(double value) {
        if (value > 0) return NamedTextColor.GREEN;
        if (value < 0) return NamedTextColor.RED;
        return NamedTextColor.GRAY;
    }
}
