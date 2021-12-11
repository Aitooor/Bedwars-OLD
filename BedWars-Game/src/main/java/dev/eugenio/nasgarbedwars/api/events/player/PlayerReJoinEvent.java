package dev.eugenio.nasgarbedwars.api.events.player;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerReJoinEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    @Getter
    private final Player player;
    @Getter
    private final IArena arena;
    private boolean cancelled;

    public PlayerReJoinEvent(final Player player, final IArena arena) {
        this.cancelled = false;
        this.player = player;
        this.arena = arena;
    }

    public static HandlerList getHandlerList() {
        return PlayerReJoinEvent.HANDLERS;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return PlayerReJoinEvent.HANDLERS;
    }
}
