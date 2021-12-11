package dev.eugenio.nasgarbedwars.api.events.player;

import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerBaseEnterEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    @Getter
    private final ITeam team;
    @Getter
    private final Player p;

    public PlayerBaseEnterEvent(final Player p2, final ITeam team) {
        this.p = p2;
        this.team = team;
    }

    public static HandlerList getHandlerList() {
        return PlayerBaseEnterEvent.HANDLERS;
    }

    public Player getPlayer() {
        return this.p;
    }

    public HandlerList getHandlers() {
        return PlayerBaseEnterEvent.HANDLERS;
    }
}
