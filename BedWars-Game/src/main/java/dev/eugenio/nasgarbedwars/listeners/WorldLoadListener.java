package dev.eugenio.nasgarbedwars.listeners;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.arena.Arena;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.LinkedList;

public class WorldLoadListener implements Listener {
    @EventHandler
    public void onLoad(final WorldLoadEvent event) {
        for (final IArena arena : new LinkedList<>(Arena.getEnableQueue())) if (arena.getWorldName().equalsIgnoreCase(event.getWorld().getName())) arena.init(event.getWorld());
    }
}
