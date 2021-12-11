package dev.eugenio.nasgarbedwars.stats;

import dev.eugenio.nasgarbedwars.BedWars;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class StatsListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPreLoginEvent(final AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) return;
        final PlayerStats fetchStats = BedWars.getInstance().getMySQLDatabase().fetchStats(event.getUniqueId());
        fetchStats.setName(event.getName());
        BedWars.getInstance().getStatsManager().put(event.getUniqueId(), fetchStats);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLoginEvent(final PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) BedWars.getInstance().getStatsManager().remove(event.getPlayer().getUniqueId());
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(final PlayerQuitEvent event) {
        BedWars.getInstance().getStatsManager().remove(event.getPlayer().getUniqueId());
    }
}
