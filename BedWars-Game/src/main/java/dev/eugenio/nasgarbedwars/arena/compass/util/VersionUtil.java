package dev.eugenio.nasgarbedwars.arena.compass.util;

import dev.eugenio.nasgarbedwars.BedWars;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VersionUtil {
    public static ItemStack buildItemStack(String material) {
        final String[] args = material.split(":");
        if (args.length == 2) {
            if (isLegacy()) {
                return new ItemStack(Material.valueOf(args[0]), 1, (byte) Integer.parseInt(args[1]));
            } else {
                return new ItemStack(Material.valueOf(args[0]));
            }
        }
        return new ItemStack(Material.valueOf(material));
    }

    public static void playSound(Player player, String sound) {
        String[] args = sound.split(",");
        if (!(args.length == 1 || args.length == 3)) return;
        player.playSound(player.getLocation(), Sound.valueOf(args[0]), args.length == 3 ? Float.parseFloat(args[1]) : 1.0F, args.length == 3 ? Float.parseFloat(args[2]) : 1.0F);
    }

    public static boolean isLegacy() {
        return BedWars.getInstance().getVersion().contains("1_8") || BedWars.getInstance().getVersion().contains("1_9") || BedWars.getInstance().getVersion().contains("1_10") || BedWars.getInstance().getVersion().contains("1_11") || BedWars.getInstance().getVersion().contains("1_12");
    }
}