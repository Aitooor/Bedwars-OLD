package dev.eugenio.nasgarbedwars.npc.listeners;

import dev.eugenio.nasgarbedwars.utils.SkinUtil;
import dev.eugenio.nasgarbedwars.BedWars;
import net.jitse.npclib.api.NPC;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NPCSpawnEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event == null) return;
        if (event.getPlayer() == null) return;
        Bukkit.getScheduler().runTaskAsynchronously(BedWars.getInstance(), () -> SkinUtil.getSkin(event.getPlayer().getName(), event.getPlayer(), skin -> {
            final NPC npc = BedWars.getInstance().getNpcLib().createNPC();

            npc.setLocation(BedWars.getInstance().getNpcManager().getLocation());
            npc.setSkin(skin);

            npc.create();
            BedWars.getInstance().getNpcManager().addPlayerAndNPC(event.getPlayer(), npc);
        }));
    }
}
