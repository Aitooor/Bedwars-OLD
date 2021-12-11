package dev.eugenio.nasgarbedwars.api.events.gameplay;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EggBridgeThrowEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    private final Player player;
    private final IArena arena;
    private boolean cancelled;

    public EggBridgeThrowEvent(final Player player, final IArena arena) {
        this.cancelled = false;
        this.player = player;
        this.arena = arena;
    }

    public static HandlerList getHandlerList() {
        return EggBridgeThrowEvent.HANDLERS;
    }

    public Player getPlayer() {
        return this.player;
    }

    public IArena getArena() {
        return this.arena;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return EggBridgeThrowEvent.HANDLERS;
    }
}
