package dev.eugenio.nasgarbedwars.api.tasks;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import org.bukkit.scheduler.BukkitTask;

public interface PlayingTask {
    IArena getArena();

    BukkitTask getBukkitTask();

    int getTask();

    int getBedsDestroyCountdown();

    int getDragonSpawnCountdown();

    int getGameEndCountdown();

    void cancel();
}
