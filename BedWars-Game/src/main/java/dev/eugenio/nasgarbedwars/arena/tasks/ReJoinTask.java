package dev.eugenio.nasgarbedwars.arena.tasks;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ReJoinTask implements Runnable {
    private static final List<ReJoinTask> reJoinTasks;
    @Getter private final IArena arena;
    private final ITeam bedWarsTeam;
    private final BukkitTask task;
    
    public ReJoinTask(final IArena arena, final ITeam bedWarsTeam) {
        this.arena = arena;
        this.bedWarsTeam = bedWarsTeam;
        this.task = Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), this, BedWars.getInstance().getMainConfig().getInt("rejoin-time") * 20L);
    }
    
    @Override
    public void run() {
        if (this.arena == null) {
            this.destroy();
            return;
        }
        if (this.bedWarsTeam == null) {
            this.destroy();
            return;
        }
        if (this.bedWarsTeam.getMembers() == null) {
            this.destroy();
            return;
        }
        if (this.bedWarsTeam.getMembers().isEmpty()) {
            this.bedWarsTeam.setBedDestroyed(true);
            this.destroy();
        }
    }
    
    public void destroy() {
        ReJoinTask.reJoinTasks.remove(this);
        this.task.cancel();
    }

    public static Collection<ReJoinTask> getReJoinTasks() {
        return Collections.unmodifiableCollection(ReJoinTask.reJoinTasks);
    }
    
    public void cancel() {
        this.task.cancel();
    }

    static {
        reJoinTasks = new ArrayList<>();
    }
}
