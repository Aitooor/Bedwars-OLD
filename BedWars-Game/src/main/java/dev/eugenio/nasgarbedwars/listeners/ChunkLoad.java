package dev.eugenio.nasgarbedwars.listeners;

import dev.eugenio.nasgarbedwars.BedWars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class ChunkLoad implements Listener {
    @EventHandler
    public void onChunkLoadEvent(ChunkLoadEvent event) {
        if (event == null) return;
        if (event.getChunk() == null) return;
        if (event.getChunk().getEntities() == null) return;
        Bukkit.getScheduler().runTaskAsynchronously(BedWars.getInstance(), () -> {
            for (Entity entity : event.getChunk().getEntities()) {
                if (!(entity instanceof ArmorStand)) continue;
                if (entity.hasMetadata("nb-bw-setup")) {
                    Bukkit.getScheduler().runTask(BedWars.getInstance(), entity::remove);
                    continue;
                }
                if (((ArmorStand)entity).isVisible() || !((ArmorStand)entity).isMarker() || !entity.isCustomNameVisible()) continue;
                if (!ChatColor.stripColor(entity.getCustomName()).contains(" SET")) if (!ChatColor.stripColor(entity.getCustomName()).contains(" set")) continue;
                Bukkit.getScheduler().runTask(BedWars.getInstance(), entity::remove);
            }
        });
    }
}
