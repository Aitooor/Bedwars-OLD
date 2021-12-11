package dev.eugenio.nasgarbedwars.api.tasks;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import org.bukkit.scheduler.BukkitTask;

public interface RestartingTask {
    IArena getArena();

    BukkitTask getBukkitTask();

    int getTask();

    int getRestarting();

    void cancel();
}
