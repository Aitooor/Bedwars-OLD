package dev.eugenio.nasgarbedwars.api.arena.generator;

import org.bukkit.entity.Player;

public interface IGeneratorHolo {
    void setTimerName(final String p0);

    void setTierName(final String p0);

    String getIso();

    void updateForPlayer(final Player p0, final String p1);

    void updateForAll();

    void destroy();
}
