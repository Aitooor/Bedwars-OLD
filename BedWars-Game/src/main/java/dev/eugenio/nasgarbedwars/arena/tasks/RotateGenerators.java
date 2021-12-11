package dev.eugenio.nasgarbedwars.arena.tasks;

import dev.eugenio.nasgarbedwars.arena.OreGenerator;

public class RotateGenerators implements Runnable {
    @Override
    public void run() {
        for (OreGenerator oreGenerator : OreGenerator.getRotation()) oreGenerator.rotate();
    }
}
