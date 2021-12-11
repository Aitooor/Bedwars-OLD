package dev.eugenio.nasgarbedwars.listeners;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.arena.Arena;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerPingListener implements Listener {
    @EventHandler
    public void onPing(final ServerListPingEvent event) {
        if (!Arena.getArenas().isEmpty()) {
            final IArena arena = Arena.getArenas().get(0);
            if (arena != null) {
                event.setMaxPlayers(arena.getMaxPlayers());
                event.setMotd(arena.getDisplayStatus(Language.getDefaultLanguage()));
            }
        }
    }
}
