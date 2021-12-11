package dev.eugenio.nasgarbedwars.upgrades.trapaction;

import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.upgrades.TrapAction;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class RemoveEffectAction implements TrapAction {
    private final PotionEffectType potionEffectType;
    
    public RemoveEffectAction(final PotionEffectType potionEffectType) {
        this.potionEffectType = potionEffectType;
    }
    
    @Override
    public String getName() {
        return "remove-effect";
    }
    
    @Override
    public void onTrigger(final Player player, final ITeam team, final ITeam team2) {
        player.removePotionEffect(this.potionEffectType);
    }
}
