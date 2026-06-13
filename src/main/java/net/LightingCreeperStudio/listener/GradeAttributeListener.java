package net.LightingCreeperStudio.listener;

import net.LightingCreeperStudio.grade.ItemPropertyModifier;
import net.LightingCreeperStudio.grade.NBTUtil;
import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 物品等级属性事件监听器
 * 通过事件动态计算属性加成和自定义耐久消耗
 */
public class GradeAttributeListener implements Listener {

    /**
     * 实体伤害事件 - 计算攻击伤害加成
     * 包括近战武器和远程武器
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // === 远程武器伤害加成（弓/弩） ===
        // 模仿力量附魔机制:弹射物伤害时 检查射击者手持的弓/弩品级
        if (event.getDamager() instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player shooter) {
                ItemStack rangedWeapon = getRangedWeapon(shooter);
                if (rangedWeapon != null) {
                    double bonus = ItemPropertyModifier.getAttributeBonus(rangedWeapon);
                    if (bonus != 0) {
                        // 模仿力量附魔:每级 +0.5 伤害,乘数与原版一致
                        // 品级偏移值本身就是伤害增量 直接加上去
                        double originalDamage = event.getDamage();
                        event.setDamage(originalDamage + bonus);
                    }
                }
            }
            return;
        }

        // === 近战武器伤害加成 ===
        if (!(event.getDamager() instanceof Player attacker)) return;

        ItemStack weapon = attacker.getInventory().getItemInMainHand();

        // 弓/弩在主手时不通过近战路径加成
        if (isRangedWeapon(weapon)) return;

        if (weapon == null || weapon.getType().isAir()) return;

        double bonus = ItemPropertyModifier.getAttributeBonus(weapon);
        if (bonus == 0) return;

        double originalDamage = event.getDamage();
        double newDamage = originalDamage + bonus;
        event.setDamage(newDamage);
    }

    /**
     * 获取玩家手持的远程武器
     * 优先主手，其次副手
     */
    private ItemStack getRangedWeapon(Player player) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (isRangedWeapon(mainHand)) return mainHand;

        ItemStack offHand = player.getInventory().getItemInOffHand();
        if (isRangedWeapon(offHand)) return offHand;

        return null;
    }

    /**
     * 判断物品是否为远程武器
     */
    private boolean isRangedWeapon(ItemStack item) {
        return item != null && !item.getType().isAir()
                && (item.getType() == Material.BOW || item.getType() == Material.CROSSBOW);
    }

    /**
     * 实体受到伤害事件 - 计算防御减伤
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;

        ItemStack[] armor = victim.getInventory().getArmorContents();
        double totalDefense = 0;

        for (ItemStack item : armor) {
            if (item != null) {
                totalDefense += ItemPropertyModifier.getAttributeBonus(item);
            }
        }

        if (totalDefense == 0) return;

        double originalDamage = event.getDamage();
        double reduction = totalDefense * 0.5;
        double newDamage = Math.max(1, originalDamage - reduction);
        event.setDamage(newDamage);
    }

    /**
     * 物品耐久损耗事件 - 自定义耐久系统核心
     * 
     * 工作原理:
     * - 品级物品在 NBT 中存储了 custommaxdurability
     * - 每次物品受损时 检查当前损耗量是否已达到自定义最大耐久
     * - 如果达到 直接将损耗量设为原版最大耐久
     * - 如果未达到 确保损耗量不超过自定义最大耐久
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;

        // 检查是否有自定义最大耐久
        int customMaxDurability = NBTUtil.getCustomMaxDurability(item);
        if (customMaxDurability < 0) return; // 没有品级标签的物品 走原版逻辑

        // 原版最大耐久
        int vanillaMaxDurability = item.getType().getMaxDurability();

        // 计算事件后的损耗量
        short currentDamage = item.getDurability();
        int newDamage = currentDamage + event.getDamage();

        if (newDamage >= customMaxDurability) {
            // 已达到自定义最大耐久 物品应该爆掉
            // 将损耗量设为原版最大耐久 让 Minecraft 自动销毁物品
            event.setDamage(vanillaMaxDurability - currentDamage);
        }
        // 否则正常消耗
    }
}
