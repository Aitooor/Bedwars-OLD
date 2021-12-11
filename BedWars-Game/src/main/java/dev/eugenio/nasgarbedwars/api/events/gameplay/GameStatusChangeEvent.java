package dev.eugenio.nasgarbedwars.api.events.gameplay;

import dev.eugenio.nasgarbedwars.api.arena.GameStatus;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStatusChangeEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    @Getter
    private final IArena arena;
    @Getter
    private final GameStatus oldState;
    @Getter
    private final GameStatus newState;

    public GameStatusChangeEvent(final IArena arena, final GameStatus oldState, final GameStatus newState) {
        this.arena = arena;
        this.oldState = oldState;
        this.newState = newState;
    }

    public static HandlerList getHandlerList() {
        return GameStatusChangeEvent.HANDLERS;
    }

    public HandlerList getHandlers() {
        return GameStatusChangeEvent.HANDLERS;
    }
}
