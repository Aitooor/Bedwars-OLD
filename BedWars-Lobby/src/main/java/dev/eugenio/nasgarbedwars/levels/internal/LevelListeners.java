package dev.eugenio.nasgarbedwars.levels.internal;

import dev.eugenio.nasgarbedwars.BedWars;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class LevelListeners implements Listener {
    public static LevelListeners instance;
    
    public LevelListeners() {
        LevelListeners.instance = this;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        UUID uUID = event.getPlayer().getUniqueId();
        new PlayerLevel(uUID, 1, 0);
        Bukkit.getScheduler().runTaskAsynchronously(BedWars.getInstance(), () -> {
            Object[] arrayOfObject = BedWars.getInstance().getMySQLDatabase().getLevelData(uUID);
            PlayerLevel.getLevelByPlayer(uUID).lazyLoad((Integer) arrayOfObject[0], (Integer) arrayOfObject[1]);
        });
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(BedWars.getInstance(), () -> PlayerLevel.getLevelByPlayer(event.getPlayer().getUniqueId()).destroy());
    }
}
