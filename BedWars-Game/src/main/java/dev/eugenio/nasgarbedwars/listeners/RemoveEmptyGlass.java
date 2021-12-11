package dev.eugenio.nasgarbedwars.listeners;

import dev.eugenio.nasgarbedwars.BedWars;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class RemoveEmptyGlass implements Listener {
    @EventHandler
    public void onDrinkPotion(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.POTION) Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(BedWars.getInstance(), () -> event.getPlayer().setItemInHand(new ItemStack(Material.AIR)), 1L);
    }
}