package dev.eugenio.nasgarbedwars.api.events.gameplay;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamAssignEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;

    private final ITeam team;

    private final IArena arena;

    private boolean cancelled = false;

    public TeamAssignEvent(Player paramPlayer, ITeam paramITeam, IArena paramIArena) {
        this.player = paramPlayer;
        this.team = paramITeam;
        this.arena = paramIArena;
    }

    public ITeam getTeam() {
        return this.team;
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

    public void setCancelled(boolean paramBoolean) {
        this.cancelled = paramBoolean;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
