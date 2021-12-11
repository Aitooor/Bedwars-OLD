package dev.eugenio.nasgarbedwars.api.levels;

import org.bukkit.entity.Player;

public interface Level {
    String getLevel(final Player p0);

    int getPlayerLevel(final Player p0);

    String getRequiredXpFormatted(final Player p0);

    String getProgressBar(final Player p0);

    int getCurrentXp(final Player p0);

    String getCurrentXpFormatted(final Player p0);

    int getRequiredXp(final Player p0);

    void setLevel(final Player p0, final int p1);
}
