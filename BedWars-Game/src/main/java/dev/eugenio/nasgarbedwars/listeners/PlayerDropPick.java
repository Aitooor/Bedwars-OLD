package dev.eugenio.nasgarbedwars.listeners;

import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.arena.GameStatus;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerGeneratorCollectEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerDropPick implements Listener {
    private static BedWarsAPI api;
    
    public PlayerDropPick(final BedWarsAPI api) {
        PlayerDropPick.api = api;
    }
    
    @EventHandler
    public void onPickup(final PlayerPickupItemEvent event) {
        final IArena arenaByPlayer = PlayerDropPick.api.getArenaUtil().getArenaByPlayer(event.getPlayer());
        if (arenaByPlayer == null) return;
        if (!arenaByPlayer.isPlayer(event.getPlayer())) {
            event.setCancelled(true);
            return;
        }
        if (arenaByPlayer.getStatus() != GameStatus.playing) {
            event.setCancelled(true);
            return;
        }
        if (arenaByPlayer.getRespawnSessions().containsKey(event.getPlayer())) {
            event.setCancelled(true);
            return;
        }
        if (event.getItem().getItemStack().getType() == Material.ARROW) {
            event.getItem().setItemStack(PlayerDropPick.api.getVersionSupport().createItemStack(event.getItem().getItemStack().getType().toString(), event.getItem().getItemStack().getAmount(), (short)0));
            return;
        }
        if (event.getItem().getItemStack().getType().toString().equals("BED")) {
            event.setCancelled(true);
            event.getItem().remove();
        } else if (event.getItem().getItemStack().hasItemMeta() && event.getItem().getItemStack().getItemMeta().hasDisplayName() && event.getItem().getItemStack().getItemMeta().getDisplayName().contains("custom")) {
            final ItemMeta itemMeta = new ItemStack(event.getItem().getItemStack().getType()).getItemMeta();
            final PlayerGeneratorCollectEvent playerGeneratorCollectEvent = new PlayerGeneratorCollectEvent(event.getPlayer(), event.getItem(), arenaByPlayer);
            Bukkit.getPluginManager().callEvent(playerGeneratorCollectEvent);
            if (playerGeneratorCollectEvent.isCancelled()) {
                event.setCancelled(true);
            } else {
                event.getItem().getItemStack().setItemMeta(itemMeta);
            }
        }
    }
    
    @EventHandler
    public void onDrop(final PlayerDropItemEvent event) {
        final IArena arenaByPlayer = PlayerDropPick.api.getArenaUtil().getArenaByPlayer(event.getPlayer());
        if (arenaByPlayer == null) return;
        if (!arenaByPlayer.isPlayer(event.getPlayer())) {
            event.setCancelled(true);
            return;
        }
        if (arenaByPlayer.getStatus() != GameStatus.playing) {
            event.setCancelled(true);
        } else if (event.getItemDrop().getItemStack().getType() == Material.COMPASS) {
            event.setCancelled(true);
            return;
        }
        if (arenaByPlayer.getRespawnSessions().containsKey(event.getPlayer())) event.setCancelled(true);
    }
    
    @EventHandler
    public void onCollect(final PlayerGeneratorCollectEvent event) {
        if (PlayerDropPick.api.getAFKUtil().isPlayerAFK(event.getPlayer())) event.setCancelled(true);
    }
}
