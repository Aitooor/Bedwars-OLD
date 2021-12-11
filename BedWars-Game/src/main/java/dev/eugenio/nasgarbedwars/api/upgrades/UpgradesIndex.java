package dev.eugenio.nasgarbedwars.api.upgrades;

import org.bukkit.entity.Player;

public interface UpgradesIndex {
    String getName();

    void open(final Player p0);

    boolean addContent(final MenuContent p0, final int p1);
}
