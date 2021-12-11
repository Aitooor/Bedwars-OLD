package dev.eugenio.nasgarbedwars.upgrades.upgradeaction;

import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.upgrades.UpgradeAction;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class DragonAction implements UpgradeAction {
    private final int amount;
    
    public DragonAction(final int amount) {
        this.amount = amount;
    }
    
    @Override
    public void onBuy(@Nullable final Player player, final ITeam team) {
        team.setDragons(this.amount);
    }
}
