package dev.eugenio.nasgarbedwars.api.tasks;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import org.bukkit.scheduler.BukkitTask;

public interface StartingTask {
    int getCountdown();

    void setCountdown(final int p0);

    IArena getArena();

    int getTask();

    BukkitTask getBukkitTask();

    void cancel();
}
