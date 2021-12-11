package dev.eugenio.nasgarbedwars.support.ecochat;

import org.bukkit.entity.Player;

public class ChatFormat implements Chat {
    @Override
    public String getPrefix(final Player player) {
        return "";
    }
    
    @Override
    public String getSuffix(final Player player) {
        return "";
    }
}
