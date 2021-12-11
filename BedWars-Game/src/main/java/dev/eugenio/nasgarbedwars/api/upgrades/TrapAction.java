package dev.eugenio.nasgarbedwars.api.upgrades;

import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import org.bukkit.entity.Player;

public interface TrapAction {
    String getName();

    void onTrigger(final Player p0, final ITeam p1, final ITeam p2);
}
