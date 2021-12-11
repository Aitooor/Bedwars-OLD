package dev.eugenio.nasgarbedwars.api.events.player;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerInvisibilityDrinkEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    @Getter
    private final Type type;
    @Getter
    private final Player player;
    @Getter
    private final IArena arena;
    @Getter
    private final ITeam team;

    public PlayerInvisibilityDrinkEvent(final Type type, final ITeam team, final Player player, final IArena arena) {
        this.type = type;
        this.player = player;
        this.arena = arena;
        this.team = team;
    }

    public static HandlerList getHandlerList() {
        return PlayerInvisibilityDrinkEvent.HANDLERS;
    }

    public HandlerList getHandlers() {
        return PlayerInvisibilityDrinkEvent.HANDLERS;
    }

    public enum Type {
        ADDED,
        REMOVED
    }
}
