package dev.eugenio.nasgarbedwars.upgrades.listeners;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.upgrades.UpgradesManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class UpgradeOpenListener implements Listener {
    @EventHandler
    public void onUpgradesOpen(final PlayerInteractAtEntityEvent event) {
        final IArena arenaByPlayer = Arena.getArenaByPlayer(event.getPlayer());
        if (arenaByPlayer == null) return;

        final Location location = event.getRightClicked().getLocation();

        for (ITeam iTeam : arenaByPlayer.getTeams()) {
            final Location teamUpgrades = iTeam.getTeamUpgrades();
            if (location.getBlockX() == teamUpgrades.getBlockX() && location.getBlockY() == teamUpgrades.getBlockY() && location.getBlockZ() == teamUpgrades.getBlockZ()) {
                event.setCancelled(true);
                if (!arenaByPlayer.isPlayer(event.getPlayer())) continue;
                UpgradesManager.getMenuForArena(arenaByPlayer).open(event.getPlayer());
            }
        }
    }
}
