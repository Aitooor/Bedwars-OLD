package dev.eugenio.nasgarbedwars.api.events.player;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;

public class PlayerLeaveArenaEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    @Getter
    private final Player player;
    private final boolean spectator;
    @Getter
    private final IArena arena;
    @Nullable
    @Getter
    private final Player lastDamager;

    public PlayerLeaveArenaEvent(final Player player, final IArena arena, @Nullable final Player lastDamager) {
        this.player = player;
        this.spectator = arena.isSpectator(player);
        this.arena = arena;
        this.lastDamager = lastDamager;
    }

    public PlayerLeaveArenaEvent(final Player player, final IArena arena) {
        this(player, arena, null);
    }

    public static HandlerList getHandlerList() {
        return PlayerLeaveArenaEvent.HANDLERS;
    }

    public boolean isSpectator() {
        return this.spectator;
    }

    public HandlerList getHandlers() {
        return PlayerLeaveArenaEvent.HANDLERS;
    }
}
