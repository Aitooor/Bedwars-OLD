package dev.eugenio.nasgarbedwars.api.events.player;

import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerBaseLeaveEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    private final ITeam team;
    private final Player p;

    public PlayerBaseLeaveEvent(final Player p2, final ITeam team) {
        this.p = p2;
        this.team = team;
    }

    public static HandlerList getHandlerList() {
        return PlayerBaseLeaveEvent.HANDLERS;
    }

    public ITeam getTeam() {
        return this.team;
    }

    public Player getPlayer() {
        return this.p;
    }

    public HandlerList getHandlers() {
        return PlayerBaseLeaveEvent.HANDLERS;
    }
}
