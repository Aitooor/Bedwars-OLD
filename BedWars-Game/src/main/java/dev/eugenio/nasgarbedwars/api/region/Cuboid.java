package dev.eugenio.nasgarbedwars.api.region;

import lombok.Getter;
import org.bukkit.Location;

public class Cuboid implements Region {
    @Getter
    private final int minX;
    @Getter
    private final int maxX;
    private final int minZ;
    private final int maxZ;
    @Getter
    private int minY;
    @Getter
    private int maxY;
    private final boolean protect;

    public Cuboid(final Location location, final int n, final boolean protect) {
        final Location subtract = location.clone().subtract(n, n, n);
        final Location add = location.clone().add(n, n, n);
        this.minX = Math.min(subtract.getBlockX(), add.getBlockX());
        this.maxX = Math.max(subtract.getBlockX(), add.getBlockX());
        this.minY = Math.min(subtract.getBlockY(), add.getBlockY());
        this.maxY = Math.max(subtract.getBlockY(), add.getBlockY());
        this.minZ = Math.min(subtract.getBlockZ(), add.getBlockZ());
        this.maxZ = Math.max(subtract.getBlockZ(), add.getBlockZ());
        this.protect = protect;
    }

    @Override
    public boolean isInRegion(final Location location) {
        return location.getBlockX() <= this.maxX && location.getBlockX() >= this.minX && location.getY() <= this.maxY && location.getY() >= this.minY && location.getBlockZ() <= this.maxZ && location.getBlockZ() >= this.minZ;
    }

    @Override
    public boolean isProtected() {
        return this.protect;
    }

    public void setMaxY(final int maxY) {
        this.maxY = maxY;
    }

    public void setMinY(final int minY) {
        this.minY = minY;
    }
}
