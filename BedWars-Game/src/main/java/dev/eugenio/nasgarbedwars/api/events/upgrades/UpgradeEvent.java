package dev.eugenio.nasgarbedwars.api.events.upgrades;

import dev.eugenio.nasgarbedwars.api.upgrades.TeamUpgrade;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UpgradeEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    private final TeamUpgrade teamUpgrade;
    @Getter
    private final Player player;
    @Getter
    private final ITeam team;


    public UpgradeEvent(final TeamUpgrade teamUpgrade, final Player player, final ITeam team) {
        this.teamUpgrade = teamUpgrade;
        this.player = player;
        this.team = team;
    }

    public static HandlerList getHandlerList() {
        return UpgradeEvent.HANDLERS;
    }

    public TeamUpgrade getTeamUpgrade() {
        return this.teamUpgrade;
    }

    public HandlerList getHandlers() {
        return UpgradeEvent.HANDLERS;
    }
}
