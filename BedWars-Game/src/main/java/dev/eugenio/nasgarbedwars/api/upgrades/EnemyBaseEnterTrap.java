package dev.eugenio.nasgarbedwars.api.upgrades;

import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface EnemyBaseEnterTrap {
    String getNameMsgPath();

    String getLoreMsgPath();

    ItemStack getItemStack();

    void trigger(final ITeam p0, final Player p1);
}
