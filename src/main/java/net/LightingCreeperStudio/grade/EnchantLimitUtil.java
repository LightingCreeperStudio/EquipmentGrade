package net.LightingCreeperStudio.grade;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 附魔限制工具类
 * 根据品级限制物品附魔数量
 *
 * 规则：
 * - 废品:禁止附魔
 * - 残次品~极品(1~8):限制附魔数量
 * - 完美及以上(maxEnchantCount=-1):不限制附魔数量
 *
 * 逻辑：
 * - 附魔台/铁砧操作前:检查是否已达上限 是则拦截阻止
 * - 附魔成功后掉级:如果附魔数超过新品级上限 随机移除一个附魔
 */
public class EnchantLimitUtil {

    private static final Random random = new Random();

    /**
     * 检查物品是否禁止附魔
     */
    public static boolean isEnchantBanned(ItemStack item) {
        if (item == null) return false;
        GradeType grade = NBTUtil.getGrade(item);
        return grade != null && grade.getMaxEnchantCount() == 0;
    }

    /**
     * 检查物品附魔是否已达上限
     */
    public static boolean isEnchantLimitReached(ItemStack item) {
        GradeType grade = NBTUtil.getGrade(item);
        if (grade == null) return false;
        int max = grade.getMaxEnchantCount();
        if (max < 0) return false; // 不限制
        return getEnchantCount(item) >= max;
    }

    /**
     * 检查物品附魔数量是否超过指定品级上限
     */
    public static boolean isEnchantOverLimit(ItemStack item, GradeType grade) {
        if (grade == null) return false;
        int max = grade.getMaxEnchantCount();
        if (max < 0) return false; // 不限制
        return getEnchantCount(item) > max;
    }

    /**
     * 获取物品附魔数量
     */
    public static int getEnchantCount(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 0;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return 0;
        return meta.getEnchants().size();
    }

    /**
     * 随机移除一个附魔
     * @return 被移除的附魔名 如果没有可移除的则返回 null
     */
    public static String removeRandomEnchant(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        Map<Enchantment, Integer> enchants = meta.getEnchants();
        if (enchants.isEmpty()) return null;

        List<Enchantment> keys = new ArrayList<>(enchants.keySet());
        Enchantment removed = keys.get(random.nextInt(keys.size()));
        String removedName = removed.getKey().getKey();

        meta.removeEnchant(removed);
        item.setItemMeta(meta);

        return removedName;
    }
}
