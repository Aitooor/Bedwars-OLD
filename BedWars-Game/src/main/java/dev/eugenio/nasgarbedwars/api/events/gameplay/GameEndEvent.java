package dev.eugenio.nasgarbedwars.api.events.gameplay;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameEndEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    @Getter
    private final List<UUID> winners;
    @Getter
    private final List<UUID> losers;
    @Getter
    private final List<UUID> aliveWinners;
    @Getter
    private final ITeam teamWinner;
    @Getter
    private final IArena arena;

    public GameEndEvent(final IArena arena, final List<UUID> list, final List<UUID> list2, final ITeam teamWinner, final List<UUID> list3) {
        this.winners = new ArrayList<>(list);
        this.arena = arena;
        this.losers = new ArrayList<>(list2);
        this.teamWinner = teamWinner;
        this.aliveWinners = new ArrayList<>(list3);
    }

    public static HandlerList getHandlerList() {
        return GameEndEvent.HANDLERS;
    }

    public HandlerList getHandlers() {
        return GameEndEvent.HANDLERS;
    }
}
