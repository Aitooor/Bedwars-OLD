package dev.eugenio.nasgarbedwars.listeners.tower;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.team.TeamColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TowerNorth {
    public TowerNorth(Location loc, Block chest, TeamColor color, Player p) {
        ItemStack itemInHand = p.getInventory().getItemInHand();
        if (itemInHand.getAmount() > 1) {
            itemInHand.setAmount(itemInHand.getAmount() - 1);
        } else {
            p.getInventory().setItemInHand(null);
        }
        new PlaceBlock(chest, -1, 0, -2, color, p, false, 0);
        new PlaceBlock(chest, -2, 0, -1, color, p, false, 0);
        new PlaceBlock(chest, -2, 0, 0, color, p, false, 0);
        new PlaceBlock(chest, -1, 0, 1, color, p, false, 0);
        new PlaceBlock(chest, 0, 0, 1, color, p, false, 0);
        new PlaceBlock(chest, 1, 0, 1, color, p, false, 0);
        new PlaceBlock(chest, 2, 0, 0, color, p, false, 0);
        new PlaceBlock(chest, 2, 0, -1, color, p, false, 0);
        new PlaceBlock(chest, 1, 0, -2, color, p, false, 0);
        if (Bukkit.getServer().getClass().getPackage().getName().contains("v1_8")) {
            loc.getWorld().playSound(loc, Sound.valueOf("CHICKEN_EGG_POP"), 1.0f, 1.0f);
        } else {
            loc.getWorld().playSound(loc, Sound.valueOf("ENTITY_CHICKEN_EGG"), 1.0f, 1.0f);
        }
        Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
            new PlaceBlock(chest, -1, 1, -2, color, p, false, 0);
            new PlaceBlock(chest, -2, 1, -1, color, p, false, 0);
            new PlaceBlock(chest, -2, 1, 0, color, p, false, 0);
            new PlaceBlock(chest, -1, 1, 1, color, p, false, 0);
            new PlaceBlock(chest, 0, 1, 1, color, p, false, 0);
            new PlaceBlock(chest, 1, 1, 1, color, p, false, 0);
            new PlaceBlock(chest, 2, 1, 0, color, p, false, 0);
            new PlaceBlock(chest, 2, 1, -1, color, p, false, 0);
            new PlaceBlock(chest, 1, 1, -2, color, p, false, 0);
            if (Bukkit.getServer().getClass().getPackage().getName().contains("v1_8")) {
                loc.getWorld().playSound(loc, Sound.valueOf("CHICKEN_EGG_POP"), 1.0f, 1.0f);
            } else {
                loc.getWorld().playSound(loc, Sound.valueOf("ENTITY_CHICKEN_EGG"), 1.0f, 1.0f);
            }
            Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
                new PlaceBlock(chest, -1, 2, -2, color, p, false, 0);
                new PlaceBlock(chest, -2, 2, -1, color, p, false, 0);
                new PlaceBlock(chest, -2, 2, 0, color, p, false, 0);
                new PlaceBlock(chest, -1, 2, 1, color, p, false, 0);
                new PlaceBlock(chest, 0, 2, 1, color, p, false, 0);
                new PlaceBlock(chest, 1, 2, 1, color, p, false, 0);
                new PlaceBlock(chest, 2, 2, 0, color, p, false, 0);
                new PlaceBlock(chest, 2, 2, -1, color, p, false, 0);
                new PlaceBlock(chest, 1, 2, -2, color, p, false, 0);
                if (Bukkit.getServer().getClass().getPackage().getName().contains("v1_8")) {
                    loc.getWorld().playSound(loc, Sound.valueOf("CHICKEN_EGG_POP"), 1.0f, 1.0f);
                } else {
                    loc.getWorld().playSound(loc, Sound.valueOf("ENTITY_CHICKEN_EGG"), 1.0f, 1.0f);
                }
                Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
                    new PlaceBlock(chest, 0, 3, -2, color, p, false, 0);
                    new PlaceBlock(chest, -1, 3, -2, color, p, false, 0);
                    new PlaceBlock(chest, -2, 3, -1, color, p, false, 0);
                    new PlaceBlock(chest, -2, 3, 0, color, p, false, 0);
                    new PlaceBlock(chest, -1, 3, 1, color, p, false, 0);
                    new PlaceBlock(chest, 0, 3, 1, color, p, false, 0);
                    new PlaceBlock(chest, 1, 3, 1, color, p, false, 0);
                    new PlaceBlock(chest, 2, 3, 0, color, p, false, 0);
                    new PlaceBlock(chest, 2, 3, -1, color, p, false, 0);
                    new PlaceBlock(chest, 1, 3, -2, color, p, false, 0);
                    if (Bukkit.getServer().getClass().getPackage().getName().contains("v1_8")) {
                        loc.getWorld().playSound(loc, Sound.valueOf("CHICKEN_EGG_POP"), 1.0f, 1.0f);
                    } else {
                        loc.getWorld().playSound(loc, Sound.valueOf("ENTITY_CHICKEN_EGG"), 1.0f, 1.0f);
                    }
                    Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
                        new PlaceBlock(chest, -2, 4, 1, color, p, false, 0);
                        new PlaceBlock(chest, -2, 4, 0, color, p, false, 0);
                        new PlaceBlock(chest, -2, 4, -1, color, p, false, 0);
                        new PlaceBlock(chest, -2, 4, -2, color, p, false, 0);
                        new PlaceBlock(chest, -1, 4, 1, color, p, false, 0);
                        new PlaceBlock(chest, -1, 4, 0, color, p, false, 0);
                        new PlaceBlock(chest, -1, 4, -1, color, p, false, 0);
                        new PlaceBlock(chest, -1, 4, -2, color, p, false, 0);
                        new PlaceBlock(chest, 0, 4, 1, color, p, false, 0);
                        new PlaceBlock(chest, 0, 4, -1, color, p, false, 0);
                        new PlaceBlock(chest, 0, 4, -2, color, p, false, 0);
                        new PlaceBlock(chest, 1, 4, 1, color, p, false, 0);
                        new PlaceBlock(chest, 1, 4, 0, color, p, false, 0);
                        new PlaceBlock(chest, 1, 4, -1, color, p, false, 0);
                        new PlaceBlock(chest, 1, 4, -2, color, p, false, 0);
                        new PlaceBlock(chest, 2, 4, 1, color, p, false, 0);
                        new PlaceBlock(chest, 2, 4, 0, color, p, false, 0);
                        new PlaceBlock(chest, 2, 4, -1, color, p, false, 0);
                        new PlaceBlock(chest, 2, 4, -2, color, p, false, 0);
                        if (Bukkit.getServer().getClass().getPackage().getName().contains("v1_8")) {
                            loc.getWorld().playSound(loc, Sound.valueOf("CHICKEN_EGG_POP"), 1.0f, 1.0f);
                        } else {
                            loc.getWorld().playSound(loc, Sound.valueOf("ENTITY_CHICKEN_EGG"), 1.0f, 1.0f);
                        }
                        Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
                            new PlaceBlock(chest, -3, 4, -2, color, p, false, 0);
                            new PlaceBlock(chest, -3, 5, -2, color, p, false, 0);
                            new PlaceBlock(chest, -3, 6, -2, color, p, false, 0);
                            new PlaceBlock(chest, -3, 5, -1, color, p, false, 0);
                            new PlaceBlock(chest, -3, 5, 0, color, p, false, 0);
                            new PlaceBlock(chest, -3, 4, 1, color, p, false, 0);
                            new PlaceBlock(chest, -3, 5, 1, color, p, false, 0);
                            new PlaceBlock(chest, -3, 6, 1, color, p, false, 0);
                            new PlaceBlock(chest, 3, 4, -2, color, p, false, 0);
                            new PlaceBlock(chest, 3, 5, -2, color, p, false, 0);
                            new PlaceBlock(chest, 3, 6, -2, color, p, false, 0);
                            new PlaceBlock(chest, 3, 5, -1, color, p, false, 0);
                            new PlaceBlock(chest, 3, 5, 0, color, p, false, 0);
                            new PlaceBlock(chest, 3, 4, 1, color, p, false, 0);
                            new PlaceBlock(chest, 3, 5, 1, color, p, false, 0);
                            new PlaceBlock(chest, 3, 6, 1, color, p, false, 0);
                            new PlaceBlock(chest, -2, 4, 2, color, p, false, 0);
                            new PlaceBlock(chest, -2, 5, 2, color, p, false, 0);
                            new PlaceBlock(chest, -2, 6, 2, color, p, false, 0);
                            new PlaceBlock(chest, -1, 5, 2, color, p, false, 0);
                            new PlaceBlock(chest, 0, 4, 2, color, p, false, 0);
                            new PlaceBlock(chest, 0, 5, 2, color, p, false, 0);
                            new PlaceBlock(chest, 0, 6, 2, color, p, false, 0);
                            new PlaceBlock(chest, 1, 5, 2, color, p, false, 0);
                            new PlaceBlock(chest, 2, 4, 2, color, p, false, 0);
                            new PlaceBlock(chest, 2, 5, 2, color, p, false, 0);
                            new PlaceBlock(chest, 2, 6, 2, color, p, false, 0);
                            new PlaceBlock(chest, -2, 4, -3, color, p, false, 0);
                            new PlaceBlock(chest, -2, 5, -3, color, p, false, 0);
                            new PlaceBlock(chest, -2, 6, -3, color, p, false, 0);
                            new PlaceBlock(chest, -1, 5, -3, color, p, false, 0);
                            new PlaceBlock(chest, 0, 4, -3, color, p, false, 0);
                            new PlaceBlock(chest, 0, 5, -3, color, p, false, 0);
                            new PlaceBlock(chest, 0, 6, -3, color, p, false, 0);
                            new PlaceBlock(chest, 1, 5, -3, color, p, false, 0);
                            new PlaceBlock(chest, 2, 4, -3, color, p, false, 0);
                            new PlaceBlock(chest, 2, 5, -3, color, p, false, 0);
                            new PlaceBlock(chest, 2, 6, -3, color, p, false, 0);
                            if (Bukkit.getServer().getClass().getPackage().getName().contains("v1_8")) {
                                loc.getWorld().playSound(loc, Sound.valueOf("CHICKEN_EGG_POP"), 1.0f, 1.0f);
                            } else {
                                loc.getWorld().playSound(loc, Sound.valueOf("ENTITY_CHICKEN_EGG"), 1.0f, 1.0f);
                            }
                            Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
                                new PlaceBlock(chest, 0, 0, 0, color, p, true, 2);
                                new PlaceBlock(chest, 0, 1, 0, color, p, true, 2);
                                new PlaceBlock(chest, 0, 2, 0, color, p, true, 2);
                                new PlaceBlock(chest, 0, 3, 0, color, p, true, 2);
                                new PlaceBlock(chest, 0, 4, 0, color, p, true, 2);
                                if (Bukkit.getServer().getClass().getPackage().getName().contains("v1_8")) {
                                    loc.getWorld().playSound(loc, Sound.valueOf("CHICKEN_EGG_POP"), 1.0f, 1.0f);
                                } else {
                                    loc.getWorld().playSound(loc, Sound.valueOf("ENTITY_CHICKEN_EGG"), 1.0f, 1.0f);
                                }
                            }, 3L);
                        }, 3L);
                    }, 3L);
                }, 3L);
            }, 3L);
        }, 3L);
    }
}
 
