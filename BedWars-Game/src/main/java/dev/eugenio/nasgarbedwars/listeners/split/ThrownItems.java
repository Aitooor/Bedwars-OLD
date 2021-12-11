package dev.eugenio.nasgarbedwars.listeners.split;

import dev.eugenio.nasgarbedwars.BedWars;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class ThrownItems implements Listener {
    @EventHandler
    public void onThrow(PlayerDropItemEvent e) {
        if (e.getItemDrop().getItemStack().getType() == Material.IRON_INGOT || e.getItemDrop().getItemStack().getType() == Material.GOLD_INGOT) {
            e.getItemDrop().setMetadata("thrownitem", new FixedMetadataValue(BedWars.getInstance(), "yes"));
        } else {
            return;
        }
    }
}
