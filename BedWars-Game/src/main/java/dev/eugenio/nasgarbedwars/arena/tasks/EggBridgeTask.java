package dev.eugenio.nasgarbedwars.arena.tasks;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.configuration.Sounds;
import dev.eugenio.nasgarbedwars.listeners.EggBridge;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.TeamColor;
import dev.eugenio.nasgarbedwars.api.events.gameplay.EggBridgeBuildingEvent;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.arena.Misc;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class EggBridgeTask implements Runnable {
    @Getter private Egg projectile;
    @Getter private TeamColor teamColor;
    @Getter private Player player;
    @Getter private IArena arena;
    private BukkitTask task;
    
    public EggBridgeTask(final Player player, final Egg projectile, final TeamColor teamColor) {
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) return;
        this.arena = arenaByPlayer;
        this.projectile = projectile;
        this.teamColor = teamColor;
        this.player = player;
        this.task = Bukkit.getScheduler().runTaskTimer(BedWars.getInstance(), this, 0L, 1L);
    }
    
    @Override
    public void run() {
        final Location location = this.getProjectile().getLocation();
        if (this.getProjectile().isDead() || !this.arena.isPlayer(this.getPlayer()) || this.getPlayer().getLocation().distance(this.getProjectile().getLocation()) > 27.0 || this.getPlayer().getLocation().getY() - this.getProjectile().getLocation().getY() > 9.0) {
            EggBridge.removeEgg(this.projectile);
            return;
        }
        if (this.getPlayer().getLocation().distance(location) > 4.0) {
            final Block block = location.clone().subtract(0.0, 2.0, 0.0).getBlock();
            if (!Misc.isBuildProtected(block.getLocation(), this.getArena()) && block.getType() == Material.AIR) {
                block.setType(BedWars.getInstance().getNms().woolMaterial());
                BedWars.getInstance().getNms().setBlockTeamColor(block, this.getTeamColor());
                this.getArena().addPlacedBlock(block);
                Bukkit.getPluginManager().callEvent(new EggBridgeBuildingEvent(this.getTeamColor(), this.getArena(), block));
                location.getWorld().playEffect(block.getLocation(), BedWars.getInstance().getNms().eggBridge(), 3);
                Sounds.playSound("egg-bridge-block", this.getPlayer());
            }
            final Block block2 = location.clone().subtract(1.0, 2.0, 0.0).getBlock();
            if (!Misc.isBuildProtected(block2.getLocation(), this.getArena()) && block2.getType() == Material.AIR) {
                block2.setType(BedWars.getInstance().getNms().woolMaterial());
                BedWars.getInstance().getNms().setBlockTeamColor(block2, this.getTeamColor());
                this.getArena().addPlacedBlock(block2);
                Bukkit.getPluginManager().callEvent(new EggBridgeBuildingEvent(this.getTeamColor(), this.getArena(), block2));
                location.getWorld().playEffect(block2.getLocation(), BedWars.getInstance().getNms().eggBridge(), 3);
                Sounds.playSound("egg-bridge-block", this.getPlayer());
            }
            final Block block3 = location.clone().subtract(0.0, 2.0, 1.0).getBlock();
            if (!Misc.isBuildProtected(block3.getLocation(), this.getArena()) && block3.getType() == Material.AIR) {
                block3.setType(BedWars.getInstance().getNms().woolMaterial());
                BedWars.getInstance().getNms().setBlockTeamColor(block3, this.getTeamColor());
                this.getArena().addPlacedBlock(block3);
                Bukkit.getPluginManager().callEvent(new EggBridgeBuildingEvent(this.getTeamColor(), this.getArena(), block3));
                location.getWorld().playEffect(block3.getLocation(), BedWars.getInstance().getNms().eggBridge(), 3);
                Sounds.playSound("egg-bridge-block", this.getPlayer());
            }
        }
    }
    
    public void cancel() {
        this.task.cancel();
    }
}
