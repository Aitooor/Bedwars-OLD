package dev.eugenio.nasgarbedwars.support.ecochat;

import org.bukkit.entity.Player;

public interface Chat {
    String getPrefix(final Player p0);
    
    String getSuffix(final Player p0);
}
