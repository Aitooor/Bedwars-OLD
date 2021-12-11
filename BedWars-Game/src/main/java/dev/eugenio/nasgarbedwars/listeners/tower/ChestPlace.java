package dev.eugenio.nasgarbedwars.listeners.tower;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.team.TeamColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class ChestPlace implements Listener {
    @EventHandler(priority=EventPriority.HIGH)
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        if (e.getBlockPlaced().getType() == Material.CHEST && BedWars.getInstance().getApi().getArenaUtil().isPlaying(player) && !e.isCancelled()) {
            e.setCancelled(true);
            Location loc = e.getBlockPlaced().getLocation();
            Block chest = e.getBlockPlaced();
            TeamColor col = BedWars.getInstance().getApi().getArenaUtil().getArenaByPlayer(player).getTeam(player).getColor();
            double rotation = (player.getLocation().getYaw() - 90.0f) % 360.0f;
            if (rotation < 0.0) {
                rotation += 360.0;
            }
            if (45.0 <= rotation && rotation < 135.0) {
                new TowerSouth(loc, chest, col, player);
            } else if (225.0 <= rotation && rotation < 315.0) {
                new TowerNorth(loc, chest, col, player);
            } else if (135.0 <= rotation && rotation < 225.0) {
                new TowerWest(loc, chest, col, player);
            } else if (0.0 <= rotation && rotation < 45.0) {
                new TowerEast(loc, chest, col, player);
            } else if (315.0 <= rotation && rotation < 360.0) {
                new TowerEast(loc, chest, col, player);
            }
        }
    }
}
