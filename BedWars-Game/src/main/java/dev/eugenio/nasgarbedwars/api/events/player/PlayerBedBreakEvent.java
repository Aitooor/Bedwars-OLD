package dev.eugenio.nasgarbedwars.api.events.player;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerBedBreakEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    @Getter
    private final Player player;
    @Getter
    private final IArena arena;
    @Getter
    private final ITeam playerTeam;
    @Getter
    private final ITeam victimTeam;

    public PlayerBedBreakEvent(final Player player, final ITeam playerTeam, final ITeam victimTeam, final IArena arena) {
        this.player = player;
        this.playerTeam = playerTeam;
        this.victimTeam = victimTeam;
        this.arena = arena;
    }

    public static HandlerList getHandlerList() {
        return PlayerBedBreakEvent.HANDLERS;
    }

    public HandlerList getHandlers() {
        return PlayerBedBreakEvent.HANDLERS;
    }
}
