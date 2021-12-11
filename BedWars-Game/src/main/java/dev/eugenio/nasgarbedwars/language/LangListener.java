package dev.eugenio.nasgarbedwars.language;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerLangChangeEvent;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.sidebar.BedWarsScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class LangListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onLanguageChangeEvent(final PlayerLangChangeEvent event) {
        if (event == null) return;
        if (event.isCancelled()) return;
        Bukkit.getScheduler().runTaskLaterAsynchronously(BedWars.getInstance(), () -> {
            BedWarsScoreboard.giveScoreboard(event.getPlayer(), Arena.getArenaByPlayer(event.getPlayer()), false);
            Bukkit.getScheduler().runTaskAsynchronously(BedWars.getInstance(), () -> BedWars.getInstance().getMySQLDatabase().setLanguage(event.getPlayer().getUniqueId(), event.getNewLang()));
        }, 10L);
    }
}
