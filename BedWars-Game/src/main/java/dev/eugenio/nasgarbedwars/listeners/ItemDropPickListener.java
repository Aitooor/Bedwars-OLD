package dev.eugenio.nasgarbedwars.listeners;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.GameStatus;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerGeneratorCollectEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemDropPickListener {
    private static boolean managePickup(final Item item, final LivingEntity livingEntity) {
        if (!(livingEntity instanceof Player)) return false;
        final IArena arenaByPlayer = BedWars.getInstance().getApi().getArenaUtil().getArenaByPlayer((Player)livingEntity);
        if (arenaByPlayer == null) return false;
        if (!arenaByPlayer.isPlayer((Player)livingEntity)) return true;
        if (arenaByPlayer.getStatus() != GameStatus.playing) return true;
        if (arenaByPlayer.getRespawnSessions().containsKey(livingEntity)) return true;
        if (item.getItemStack().getType() == Material.ARROW) {
            item.setItemStack(BedWars.getInstance().getApi().getVersionSupport().createItemStack(item.getItemStack().getType().toString(), item.getItemStack().getAmount(), (short)0));
            return false;
        }
        if (item.getItemStack().getType().toString().equals("BED")) {
            item.remove();
            return true;
        }
        if (item.getItemStack().hasItemMeta() && item.getItemStack().getItemMeta().hasDisplayName() && item.getItemStack().getItemMeta().getDisplayName().contains("custom")) {
            final ItemMeta itemMeta = new ItemStack(item.getItemStack().getType()).getItemMeta();
            final PlayerGeneratorCollectEvent playerGeneratorCollectEvent = new PlayerGeneratorCollectEvent((Player)livingEntity, item, arenaByPlayer);
            Bukkit.getPluginManager().callEvent(playerGeneratorCollectEvent);
            if (playerGeneratorCollectEvent.isCancelled()) return true;
            item.getItemStack().setItemMeta(itemMeta);
        }
        return false;
    }
    
    private static boolean manageDrop(final Entity entity, final Item item) {
        if (!(entity instanceof Player)) return false;
        final IArena arenaByPlayer = BedWars.getInstance().getApi().getArenaUtil().getArenaByPlayer((Player)entity);
        return arenaByPlayer != null && (!arenaByPlayer.isPlayer((Player)entity) || arenaByPlayer.getStatus() != GameStatus.playing || item.getItemStack().getType() == Material.COMPASS || arenaByPlayer.getRespawnSessions().containsKey(entity));
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
            if (managePickup(event.getItem(), event.getPlayer())) event.setCancelled(true);
        }
    }
    
    public static class GeneratorCollect implements Listener {
        @EventHandler
        public void onCollect(final PlayerGeneratorCollectEvent event) {
            if (BedWars.getInstance().getApi().getAFKUtil().isPlayerAFK(event.getPlayer())) event.setCancelled(true);
        }
    }
}
