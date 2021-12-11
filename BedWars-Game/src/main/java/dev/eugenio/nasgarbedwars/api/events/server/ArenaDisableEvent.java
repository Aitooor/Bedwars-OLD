package dev.eugenio.nasgarbedwars.api.events.server;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaDisableEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    @Getter
    private final String arenaName;
    @Getter
    private final String worldName;

    public ArenaDisableEvent(final String arenaName, final String worldName) {
        this.arenaName = arenaName;
        this.worldName = worldName;
    }

    public static HandlerList getHandlerList() {
        return ArenaDisableEvent.HANDLERS;
    }

    public HandlerList getHandlers() {
        return ArenaDisableEvent.HANDLERS;
    }
}
