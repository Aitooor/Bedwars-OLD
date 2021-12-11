package dev.eugenio.nasgarbedwars.api.upgrades;

import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public interface UpgradeAction {
    @Deprecated
    default void onBuy(final ITeam bwt) {
        this.onBuy(null, bwt);
    }

    void onBuy(@Nullable final Player p0, final ITeam p1);
}
