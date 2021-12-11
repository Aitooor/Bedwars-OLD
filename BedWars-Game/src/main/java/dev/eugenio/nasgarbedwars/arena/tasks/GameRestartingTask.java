package dev.eugenio.nasgarbedwars.arena.tasks;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.configuration.Sounds;
import dev.eugenio.nasgarbedwars.api.arena.generator.IGenerator;
import dev.eugenio.nasgarbedwars.api.arena.shop.ShopHologram;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.tasks.RestartingTask;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.arena.Misc;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class GameRestartingTask implements Runnable, RestartingTask {
    @Getter private Arena arena;
    @Getter private int restarting;
    private final BukkitTask task;
    
    public GameRestartingTask(final Arena arena) {
        this.restarting = BedWars.getInstance().getMainConfig().getInt("countdowns.game-restart") + 3;
        this.arena = arena;
        this.task = Bukkit.getScheduler().runTaskTimer(BedWars.getInstance(), this, 0L, 20L);
        Sounds.playSound("game-end", arena.getPlayers());
        Sounds.playSound("game-end", arena.getSpectators());
    }
    
    @Override
    public int getTask() {
        return this.task.getTaskId();
    }
    
    @Override
    public BukkitTask getBukkitTask() {
        return this.task;
    }
    
    @Override
    public void run() {
        --this.restarting;
        if (this.getArena().getPlayers().isEmpty() && this.restarting > 9) this.restarting = 9;
        if (this.restarting == 7) {
            for (Player player : new ArrayList<>(this.getArena().getPlayers())) this.getArena().removePlayer(player, true);
            for (Player player : new ArrayList<>(this.getArena().getSpectators())) this.getArena().removeSpectator(player, true);
        } else if (this.restarting == 4) {
            ShopHologram.clearForArena(this.getArena());
            for (final Entity entity : this.getArena().getWorld().getEntities()) {
                if (entity.getType() == EntityType.PLAYER) {
                    final Player player = (Player)entity;
                    Misc.moveToLobbyOrKick(player, this.getArena(), true);
                    if (this.getArena().isSpectator(player)) this.getArena().removeSpectator(player, false);
                    if (!this.getArena().isPlayer(player)) continue;
                    this.getArena().removePlayer(player, false);
                }
            }
            for (IGenerator iGenerator : this.getArena().getOreGenerators()) iGenerator.disable();
            for (ITeam iTeam : this.getArena().getTeams()) for (IGenerator iGenerator : iTeam.getGenerators()) iGenerator.disable();
        } else if (this.restarting == 0) {
            this.getArena().restart();
            this.task.cancel();
            this.arena = null;
        }
    }
    
    @Override
    public void cancel() {
        this.task.cancel();
    }
}
