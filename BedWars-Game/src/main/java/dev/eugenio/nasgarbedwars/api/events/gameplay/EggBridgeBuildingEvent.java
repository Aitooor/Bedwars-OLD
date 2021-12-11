package dev.eugenio.nasgarbedwars.api.events.gameplay;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.TeamColor;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EggBridgeBuildingEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    private final TeamColor teamColor;
    private final IArena arena;
    private final Block block;

    public EggBridgeBuildingEvent(final TeamColor teamColor, final IArena arena, final Block block) {
        this.teamColor = teamColor;
        this.arena = arena;
        this.block = block;
    }

    public static HandlerList getHandlerList() {
        return EggBridgeBuildingEvent.HANDLERS;
    }

    public IArena getArena() {
        return this.arena;
    }

    public Block getBlock() {
        return this.block;
    }

    public TeamColor getTeamColor() {
        return this.teamColor;
    }

    public HandlerList getHandlers() {
        return EggBridgeBuildingEvent.HANDLERS;
    }
}
