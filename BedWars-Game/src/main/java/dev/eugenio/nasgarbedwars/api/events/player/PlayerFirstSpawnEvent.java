package dev.eugenio.nasgarbedwars.api.events.player;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerFirstSpawnEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    @Getter
    private final Player player;
    @Getter
    private final IArena arena;
    @Getter
    private final ITeam team;

    public PlayerFirstSpawnEvent(final Player player, final IArena arena, final ITeam team) {
        this.player = player;
        this.arena = arena;
        this.team = team;
    }

    public static HandlerList getHandlerList() {
        return PlayerFirstSpawnEvent.HANDLERS;
    }

    public HandlerList getHandlers() {
        return PlayerFirstSpawnEvent.HANDLERS;
    }
}
