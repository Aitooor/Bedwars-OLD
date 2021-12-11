package dev.eugenio.nasgarbedwars.commands;

import dev.eugenio.nasgarbedwars.BedWars;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;

public class Misc {
    public static void createArmorStand(final String customName, final Location location, final String s) {
        final ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location.getBlock().getLocation().add(0.5, 2.0, 0.5), EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setMarker(true);
        armorStand.setGravity(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(customName);
        armorStand.setMetadata("nb-bw-setup", new FixedMetadataValue(BedWars.getInstance(), "hologram"));
        if (s != null) {
            armorStand.setMetadata("nb-bw-loc", new FixedMetadataValue(BedWars.getInstance(), s));
        }
    }

    public static void removeArmorStand(final String s, final Location location, final String s2) {
        for (final Entity entity : location.getWorld().getNearbyEntities(location, 1.0, 3.0, 1.0)) {
            if (entity.hasMetadata("nb-bw-setup")) {
                if (entity.hasMetadata("nb-bw-loc")) {
                    if (!entity.getMetadata("nb-bw-loc").get(0).asString().equalsIgnoreCase(s2)) continue;
                    if (s != null && !s.isEmpty() && ChatColor.stripColor(entity.getCustomName()).contains(s)) {
                        entity.remove();
                        return;
                    }
                }
            } else {
                if (entity.getType() != EntityType.ARMOR_STAND || ((ArmorStand) entity).isVisible() || s == null || !entity.getCustomName().contains(s))
                    continue;
            }
            entity.remove();
        }
    }
}