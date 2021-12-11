package dev.eugenio.nasgarbedwars.shop.defaultrestore;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.GameStatus;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class ShopItemRestoreListener {
    public static boolean managePickup(final Item item, final LivingEntity livingEntity) {
        if (!(livingEntity instanceof Player)) return false;
        if (BedWars.getInstance().getApi().getArenaUtil().getArenaByPlayer((Player) livingEntity) == null) return false;
        if (BedWars.getInstance().getApi().getArenaUtil().getArenaByPlayer((Player) livingEntity).getStatus() != GameStatus.playing)
            return false;
        if (!BedWars.getInstance().getApi().getArenaUtil().getArenaByPlayer((Player) livingEntity).isPlayer((Player) livingEntity))
            return false;
        if (BedWars.getInstance().getApi().getVersionSupport().isSword(item.getItemStack())) {
            for (final ItemStack itemStack : ((Player) livingEntity).getInventory()) {
                if (itemStack == null) continue;
                if (itemStack.getType() == Material.AIR) continue;
                if (!BedWars.getInstance().getApi().getVersionSupport().isCustomBedWarsItem(itemStack)) continue;
                if (!BedWars.getInstance().getApi().getVersionSupport().getCustomData(itemStack).equalsIgnoreCase("DEFAULT_ITEM"))
                    continue;
                if (BedWars.getInstance().getApi().getVersionSupport().isSword(item.getItemStack()) && BedWars.getInstance().getApi().getVersionSupport().getDamage(item.getItemStack()) >= BedWars.getInstance().getApi().getVersionSupport().getDamage(itemStack)) {
                    ((Player) livingEntity).getInventory().remove(itemStack);
                    ((Player) livingEntity).updateInventory();
                    return false;
                }
            }
            item.remove();
            return true;
        }
        return false;
    }

    private static boolean manageDrop(final Entity entity, final Item item) {
        if (!(entity instanceof Player)) return false;
        if (BedWars.getInstance().getApi().getArenaUtil().getArenaByPlayer((Player) entity) == null) return false;
        final IArena arenaByPlayer = BedWars.getInstance().getApi().getArenaUtil().getArenaByPlayer((Player) entity);
        if (arenaByPlayer.getStatus() != GameStatus.playing) return false;
        if (!arenaByPlayer.isPlayer((Player) entity)) return false;
        if (BedWars.getInstance().getApi().getVersionSupport().isCustomBedWarsItem(item.getItemStack()) && BedWars.getInstance().getApi().getVersionSupport().getCustomData(item.getItemStack()).equalsIgnoreCase("DEFAULT_ITEM") && BedWars.getInstance().getApi().getVersionSupport().isSword(item.getItemStack())) {
            boolean b = false;
            for (final ItemStack itemStack : ((Player) entity).getInventory()) {
                if (itemStack == null) continue;
                if (BedWars.getInstance().getApi().getVersionSupport().isSword(itemStack) && BedWars.getInstance().getApi().getVersionSupport().getDamage(itemStack) >= BedWars.getInstance().getApi().getVersionSupport().getDamage(item.getItemStack())) {
                    b = true;
                    break;
                }
            }
            return !b;
        } else {
            boolean b2 = false;
            for (final ItemStack itemStack2 : ((Player) entity).getInventory()) {
                if (itemStack2 == null) continue;
                if (BedWars.getInstance().getApi().getVersionSupport().isSword(itemStack2)) {
                    b2 = true;
                    break;
                }
            }
            if (!b2) arenaByPlayer.getTeam((Player) entity).defaultSword((Player) entity, true);
        }
        return false;
    }

    public static class PlayerDrop implements Listener {
        @EventHandler
        public void onDrop(final PlayerDropItemEvent event) {
            if (manageDrop(event.getPlayer(), event.getItemDrop())) event.setCancelled(true);
        }
    }

    public static class PlayerPickup implements Listener {
        @EventHandler
        public void onDrop(final PlayerPickupItemEvent event) {
            if (ShopItemRestoreListener.managePickup(event.getItem(), event.getPlayer())) event.setCancelled(true);
        }
    }

    public static class DefaultRestoreInvClose implements Listener {
        @EventHandler
        public void onInventoryClose(final InventoryCloseEvent event) {
            if (event.getInventory().getType() == InventoryType.PLAYER) return;
            if (BedWars.getInstance().getApi().getArenaUtil().getArenaByPlayer((Player) event.getPlayer()) == null) return;
            final IArena arenaByPlayer = BedWars.getInstance().getApi().getArenaUtil().getArenaByPlayer((Player) event.getPlayer());
            if (arenaByPlayer.getStatus() != GameStatus.playing) return;
            if (!arenaByPlayer.isPlayer((Player) event.getPlayer())) return;
            boolean b = false;
            for (final ItemStack itemStack : event.getPlayer().getInventory()) {
                if (itemStack == null) continue;
                if (itemStack.getType() == Material.AIR) continue;
                if (!BedWars.getInstance().getApi().getVersionSupport().isSword(itemStack)) continue;
                b = true;
            }
            if (!b) arenaByPlayer.getTeam((Player) event.getPlayer()).defaultSword((Player) event.getPlayer(), true);
        }
    }
}
