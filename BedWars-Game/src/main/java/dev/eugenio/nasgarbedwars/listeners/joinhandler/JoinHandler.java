package dev.eugenio.nasgarbedwars.listeners.joinhandler;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class JoinHandler implements Listener {
    
    @EventHandler
    public void requestLanguage(final AsyncPlayerPreLoginEvent asyncPlayerPreLoginEvent) {
        Bukkit.getScheduler().runTask(BedWars.getInstance(), () -> Language.setPlayerLanguage(asyncPlayerPreLoginEvent.getUniqueId(), BedWars.getInstance().getMySQLDatabase().getLanguage(asyncPlayerPreLoginEvent.getUniqueId())));
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void removeLanguage(final PlayerLoginEvent playerLoginEvent) {
        if (playerLoginEvent.getResult() != PlayerLoginEvent.Result.ALLOWED) Language.setPlayerLanguage(playerLoginEvent.getPlayer().getUniqueId(), null);
    }
}
