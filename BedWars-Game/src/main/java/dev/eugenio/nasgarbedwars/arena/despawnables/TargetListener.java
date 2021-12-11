package dev.eugenio.nasgarbedwars.arena.despawnables;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.GameStatus;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class TargetListener implements Listener {
    @EventHandler
    public void onTarget(final EntityTargetLivingEntityEvent entityTargetLivingEntityEvent) {
        if (!(entityTargetLivingEntityEvent.getTarget() instanceof Player)) return;
        final IArena arenaByIdentifier = Arena.getArenaByIdentifier(entityTargetLivingEntityEvent.getEntity().getWorld().getName());
        final Player player = (Player)entityTargetLivingEntityEvent.getTarget();
        if (arenaByIdentifier == null) return;
        if (!arenaByIdentifier.isPlayer(player)) {
            entityTargetLivingEntityEvent.setCancelled(true);
            return;
        }
        if (arenaByIdentifier.getStatus() != GameStatus.playing) {
            entityTargetLivingEntityEvent.setCancelled(true);
            return;
        }
        if (BedWars.getInstance().getNms().isDespawnable(entityTargetLivingEntityEvent.getEntity()) && arenaByIdentifier.getTeam(player) == BedWars.getInstance().getNms().getDespawnablesList().get(entityTargetLivingEntityEvent.getEntity().getUniqueId()).getTeam()) entityTargetLivingEntityEvent.setCancelled(true);
    }
}
