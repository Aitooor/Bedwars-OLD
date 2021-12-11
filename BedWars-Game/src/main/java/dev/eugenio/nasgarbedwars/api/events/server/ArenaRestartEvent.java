package dev.eugenio.nasgarbedwars.api.events.server;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaRestartEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    private final String arena;
    private final String worldName;

    public ArenaRestartEvent(final String arena, final String worldName) {
        this.arena = arena;
        this.worldName = worldName;
    }

    public static HandlerList getHandlerList() {
        return ArenaRestartEvent.HANDLERS;
    }

    public String getArenaName() {
        return this.arena;
    }

    public HandlerList getHandlers() {
        return ArenaRestartEvent.HANDLERS;
    }

    public String getWorldName() {
        return this.worldName;
    }
}
