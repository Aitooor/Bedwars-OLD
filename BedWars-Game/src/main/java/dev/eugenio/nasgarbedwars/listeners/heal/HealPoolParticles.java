package dev.eugenio.nasgarbedwars.listeners.heal;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.events.gameplay.GameEndEvent;
import dev.eugenio.nasgarbedwars.api.events.server.ArenaDisableEvent;
import dev.eugenio.nasgarbedwars.api.events.upgrades.UpgradeEvent;
import dev.eugenio.nasgarbedwars.arena.Arena;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HealPoolParticles implements Listener {
    @EventHandler
    public void onTeamUpgrade(UpgradeEvent e) {
        net.minecraft.server.v1_8_R3.EnumParticle.a();
        org.bukkit.craftbukkit.v1_8_R3.inventory.CraftContainer.c(1);
        if (e.getTeamUpgrade().getName().equalsIgnoreCase("upgrade-heal-pool")) {
            IArena a = Arena.getArenaByPlayer(e.getPlayer());
            if (a == null) return;
            ITeam bwt = a.getTeam(e.getPlayer());
            if (bwt == null) return;
            if (!HealPoolTask.exists(a, bwt)) new HealPoolTask(bwt);
        }
    }

    @EventHandler
    public void onDisable(ArenaDisableEvent e) {
        HealPoolTask.removeForArena(e.getArenaName());
    }

    @EventHandler
    public void onEnd(GameEndEvent e) {
        HealPoolTask.removeForArena(e.getArena().getArenaName());
    }
}
