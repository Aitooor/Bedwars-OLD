package dev.eugenio.nasgarbedwars.api.events.server;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaEnableEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    @Getter
    private final IArena arena;

    public ArenaEnableEvent(final IArena arena) {
        this.arena = arena;
    }

    public static HandlerList getHandlerList() {
        return ArenaEnableEvent.HANDLERS;
    }

    public HandlerList getHandlers() {
        return ArenaEnableEvent.HANDLERS;
    }
}
