package dev.eugenio.nasgarbedwars.arena;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class VoidGenerator extends ChunkGenerator {
    public ChunkGenerator.ChunkData generateChunkData(final World world, final Random random, final int n, final int n2, final ChunkGenerator.BiomeGrid biomeGrid) {
        return this.createChunkData(world);
    }
    
    public final Location getFixedSpawnLocation(final World world, final Random random) {
        return new Location(world, 0.0, 64.0, 0.0);
    }
}
