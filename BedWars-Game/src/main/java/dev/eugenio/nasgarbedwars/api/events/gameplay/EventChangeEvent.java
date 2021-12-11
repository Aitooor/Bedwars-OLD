package dev.eugenio.nasgarbedwars.api.events.gameplay;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.NextEvent;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EventChangeEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    @Getter
    private final IArena arena;
    @Getter
    private final NextEvent newEvent;
    @Getter
    private final NextEvent oldEvent;

    public EventChangeEvent(final IArena arena, final NextEvent newEvent, final NextEvent oldEvent) {
        this.arena = arena;
        this.oldEvent = oldEvent;
        this.newEvent = newEvent;
    }

    public static HandlerList getHandlerList() {
        return EventChangeEvent.HANDLERS;
    }

    public HandlerList getHandlers() {
        return EventChangeEvent.HANDLERS;
    }
}
