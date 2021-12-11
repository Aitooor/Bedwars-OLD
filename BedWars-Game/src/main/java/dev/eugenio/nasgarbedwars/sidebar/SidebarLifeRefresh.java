package dev.eugenio.nasgarbedwars.sidebar;

import org.bukkit.entity.Player;

public class SidebarLifeRefresh implements Runnable {
    @Override
    public void run() {
        for (final BedWarsScoreboard bedWarsScoreboard : BedWarsScoreboard.getScoreboards().values()) {
            if (bedWarsScoreboard.getArena() != null) {
                bedWarsScoreboard.getHandle().refreshHealthAnimation();
                for (final Player player : bedWarsScoreboard.getArena().getPlayers()) bedWarsScoreboard.getHandle().refreshHealth(player, (int)player.getHealth());
            }
        }
    }
}