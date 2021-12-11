package dev.eugenio.nasgarbedwars.api.events.player;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerJoinArenaEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    @Getter
    private final Player player;
    private final boolean spectator;
    @Getter
    private final IArena arena;
    private boolean cancelled;

    public PlayerJoinArenaEvent(final IArena arena, final Player player, final boolean spectator) {
        this.cancelled = false;
        this.arena = arena;
        this.player = player;
        this.spectator = spectator;
    }

    public static HandlerList getHandlerList() {
        return PlayerJoinArenaEvent.HANDLERS;
    }

    public boolean isSpectator() {
        return this.spectator;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return PlayerJoinArenaEvent.HANDLERS;
    }
}
