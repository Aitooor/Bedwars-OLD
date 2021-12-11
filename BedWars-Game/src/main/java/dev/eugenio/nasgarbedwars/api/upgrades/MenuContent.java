package dev.eugenio.nasgarbedwars.api.upgrades;

import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public interface MenuContent {
    ItemStack getDisplayItem(final Player p0, final ITeam p1);

    void onClick(final Player p0, final ClickType p1, final ITeam p2);

    String getName();
}
