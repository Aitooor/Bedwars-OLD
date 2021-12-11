package dev.eugenio.nasgarbedwars.shop.listeners;

import dev.eugenio.nasgarbedwars.api.events.player.PlayerJoinArenaEvent;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerLeaveArenaEvent;
import dev.eugenio.nasgarbedwars.shop.ShopCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class ShopCacheListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onArenaJoin(final PlayerJoinArenaEvent event) {
        if (event.isSpectator()) return;
        final ShopCache shopCache = ShopCache.getShopCache(event.getPlayer().getUniqueId());
        if (shopCache != null) shopCache.destroy();
        new ShopCache(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onArenaLeave(final PlayerLeaveArenaEvent event) {
        final ShopCache shopCache = ShopCache.getShopCache(event.getPlayer().getUniqueId());
        if (shopCache != null) shopCache.destroy();
    }

    @EventHandler
    public void onServerLeave(final PlayerQuitEvent event) {
        final ShopCache shopCache = ShopCache.getShopCache(event.getPlayer().getUniqueId());
        if (shopCache != null) shopCache.destroy();
    }
}
