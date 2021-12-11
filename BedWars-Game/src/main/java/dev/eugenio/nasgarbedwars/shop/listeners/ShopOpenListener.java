package dev.eugenio.nasgarbedwars.shop.listeners;

import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.shop.ShopManager;
import dev.eugenio.nasgarbedwars.shop.quickbuy.PlayerQuickBuyCache;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class ShopOpenListener implements Listener {
    @EventHandler
    public void onShopOpen(final PlayerInteractAtEntityEvent event) {
        InventoryListener.slotClick = 0;
        final IArena arenaByPlayer = Arena.getArenaByPlayer(event.getPlayer());
        if (arenaByPlayer == null) return;
        final Location location = event.getRightClicked().getLocation();
        for (ITeam iTeam : arenaByPlayer.getTeams()) {
            final Location shop = iTeam.getShop();
            if (location.getBlockX() == shop.getBlockX() && location.getBlockY() == shop.getBlockY() && location.getBlockZ() == shop.getBlockZ()) {
                event.setCancelled(true);
                if (!arenaByPlayer.isPlayer(event.getPlayer())) continue;
                ShopManager.shop.open(event.getPlayer(), PlayerQuickBuyCache.getQuickBuyCache(event.getPlayer().getUniqueId()), true);
            }
        }
    }
}
