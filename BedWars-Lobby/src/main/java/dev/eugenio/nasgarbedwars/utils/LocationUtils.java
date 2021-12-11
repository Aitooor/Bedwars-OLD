package dev.eugenio.nasgarbedwars.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtils {
    public static Location locFromString(String coordinates) {
        if (coordinates == null) return null;
        String[] split = coordinates.split(":");
        World world = Bukkit.getWorld(split[0]);
        if (world == null) return null;

        try {
            return new Location(world, Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
        } catch (ArrayIndexOutOfBoundsException ignored) {
            return new Location(world, Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
        }
    }

    public static String locToString(Location loc, boolean includeYawPitch) {
        if(loc == null) {
            return null;
        }
        if(includeYawPitch) {
            return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ() + ":" + loc.getYaw() + ":" + loc.getPitch();
        }
        return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
    }
}
