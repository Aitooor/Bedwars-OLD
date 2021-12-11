package dev.eugenio.nasgarbedwars.shop.listeners;

import dev.eugenio.nasgarbedwars.api.events.player.PlayerJoinArenaEvent;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerReJoinEvent;
import dev.eugenio.nasgarbedwars.shop.quickbuy.PlayerQuickBuyCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuickBuyListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onArenaJoin(final PlayerJoinArenaEvent event) {
        if (event == null) return;
        if (event.isSpectator()) return;
        final PlayerQuickBuyCache quickBuyCache = PlayerQuickBuyCache.getQuickBuyCache(event.getPlayer().getUniqueId());
        if (quickBuyCache != null) quickBuyCache.destroy();
        new PlayerQuickBuyCache(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onArenaJoin(final PlayerReJoinEvent event) {
        if (event == null) return;
        final PlayerQuickBuyCache quickBuyCache = PlayerQuickBuyCache.getQuickBuyCache(event.getPlayer().getUniqueId());
        if (quickBuyCache != null) quickBuyCache.destroy();
        new PlayerQuickBuyCache(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(final PlayerQuitEvent event) {
        if (event == null) return;
        final PlayerQuickBuyCache quickBuyCache = PlayerQuickBuyCache.getQuickBuyCache(event.getPlayer().getUniqueId());
        if (quickBuyCache == null) return;
        quickBuyCache.destroy();
    }
}
