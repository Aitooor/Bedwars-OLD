package dev.eugenio.nasgarbedwars.api.region;

import org.bukkit.Location;

public interface Region {
    boolean isInRegion(final Location p0);

    boolean isProtected();
}
