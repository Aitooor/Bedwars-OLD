package dev.eugenio.nasgarbedwars.api.events.spectator;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class SpectatorTeleportToPlayerEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    private final UUID spectator;
    private final UUID target;
    @Getter
    private final IArena arena;
    private boolean cancelled;

    public SpectatorTeleportToPlayerEvent(final Player player, final Player player2, final IArena arena) {
        this.cancelled = false;
        this.spectator = player.getUniqueId();
        this.target = player2.getUniqueId();
        this.arena = arena;
    }

    public static HandlerList getHandlerList() {
        return SpectatorTeleportToPlayerEvent.HANDLERS;
    }

    public Player getSpectator() {
        return Bukkit.getPlayer(this.spectator);
    }

    public Player getTarget() {
        return Bukkit.getPlayer(this.target);
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return SpectatorTeleportToPlayerEvent.HANDLERS;
    }
}
