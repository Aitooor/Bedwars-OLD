package dev.eugenio.nasgarbedwars.npc.listeners;

import dev.eugenio.nasgarbedwars.BedWars;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class NPCDestroyEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerQuitEvent event) {
        if (event == null) return;
        BedWars.getInstance().getNpcManager().destroyPlayerAndNPC(event.getPlayer());
    }
}
