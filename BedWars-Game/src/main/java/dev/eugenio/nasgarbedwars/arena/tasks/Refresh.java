package dev.eugenio.nasgarbedwars.arena.tasks;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.entity.Despawnable;

public class Refresh implements Runnable {
    @Override
    public void run() {
        for (Despawnable despawnable : BedWars.getInstance().getNms().getDespawnablesList().values()) despawnable.refresh();
    }
}
