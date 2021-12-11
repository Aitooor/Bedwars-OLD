package dev.eugenio.nasgarbedwars.support.ecochat;

import org.bukkit.entity.Player;

public interface Economy {
    boolean isEconomy();
    
    double getMoney(final Player p0);
    
    void buyAction(final Player p0, final double p1);
}
