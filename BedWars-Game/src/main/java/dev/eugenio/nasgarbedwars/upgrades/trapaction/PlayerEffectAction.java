package dev.eugenio.nasgarbedwars.upgrades.trapaction;

import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.upgrades.TrapAction;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerEffectAction implements TrapAction {
    private final PotionEffectType potionEffectType;
    private final int amplifier;
    private int duration;
    private final ApplyType type;
    
    public PlayerEffectAction(final PotionEffectType potionEffectType, final int amplifier, final int duration, final ApplyType type) {
        this.potionEffectType = potionEffectType;
        this.amplifier = amplifier;
        this.type = type;
        this.duration = duration;
        if (duration < 0) this.duration *= -1;
        if (duration == 0) {
            this.duration = Integer.MAX_VALUE;
        } else {
            this.duration *= 20;
        }
    }
    
    @Override
    public String getName() {
        return "player-effect";
    }
    
    @Override
    public void onTrigger(final Player player, final ITeam team, final ITeam team2) {
        if (this.type == ApplyType.TEAM) {
            for (Player value : team2.getMembers()) {
                value.addPotionEffect(new PotionEffect(this.potionEffectType, this.duration, this.amplifier), true);
            }
        } else if (this.type == ApplyType.BASE) {
            for (final Player player2 : team2.getMembers()) {
                if (player2.getLocation().distance(team2.getBed()) <= team2.getArena().getIslandRadius()) player2.addPotionEffect(new PotionEffect(this.potionEffectType, this.duration, this.amplifier), true);
            }
        } else if (this.type == ApplyType.ENEMY) {
            player.addPotionEffect(new PotionEffect(this.potionEffectType, this.duration, this.amplifier), true);
        }
    }
    
    public enum ApplyType {
        TEAM, 
        BASE, 
        ENEMY
    }
}
