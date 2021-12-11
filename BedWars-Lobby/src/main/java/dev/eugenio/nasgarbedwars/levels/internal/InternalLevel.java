package dev.eugenio.nasgarbedwars.levels.internal;

import dev.eugenio.nasgarbedwars.api.levels.Level;
import org.bukkit.entity.Player;

public class InternalLevel implements Level {
    @Override
    public String getLevel(final Player player) {
        return PlayerLevel.getLevelByPlayer(player.getUniqueId()).getLevelName();
    }
    
    @Override
    public int getPlayerLevel(final Player player) {
        return PlayerLevel.getLevelByPlayer(player.getUniqueId()).getPlayerLevel();
    }
    
    @Override
    public String getRequiredXpFormatted(final Player player) {
        return PlayerLevel.getLevelByPlayer(player.getUniqueId()).getFormattedRequiredXp();
    }
    
    @Override
    public String getProgressBar(final Player player) {
        return PlayerLevel.getLevelByPlayer(player.getUniqueId()).getProgress();
    }
    
    @Override
    public int getCurrentXp(final Player player) {
        return PlayerLevel.getLevelByPlayer(player.getUniqueId()).getCurrentXp();
    }
    
    @Override
    public String getCurrentXpFormatted(final Player player) {
        return PlayerLevel.getLevelByPlayer(player.getUniqueId()).getFormattedCurrentXp();
    }
    
    @Override
    public int getRequiredXp(final Player player) {
        return PlayerLevel.getLevelByPlayer(player.getUniqueId()).getNextLevelCost();
    }
    
    @Override
    public void setLevel(final Player player, final int level) {
        PlayerLevel.getLevelByPlayer(player.getUniqueId()).setLevel(level);
    }
}
