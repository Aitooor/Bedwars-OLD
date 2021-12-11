package dev.eugenio.nasgarbedwars.listeners;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.events.gameplay.EggBridgeThrowEvent;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.arena.tasks.EggBridgeTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EggBridge implements Listener {
    private static HashMap<Egg, EggBridgeTask> bridges;
    
    @EventHandler
    public void onLaunch(final ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Egg) {
            final Egg egg = (Egg)event.getEntity();
            if (egg.getShooter() instanceof Player) {
                final Player player = (Player)egg.getShooter();
                final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
                if (arenaByPlayer != null && arenaByPlayer.isPlayer(player)) {
                    Bukkit.getPluginManager().callEvent(new EggBridgeThrowEvent(player, arenaByPlayer));
                    if (event.isCancelled()) {
                        event.setCancelled(true);
                        return;
                    }
                    EggBridge.bridges.put(egg, new EggBridgeTask(player, egg, arenaByPlayer.getTeam(player).getColor()));
                }
            }
        }
    }
    
    @EventHandler
    public void onHit(final ProjectileHitEvent event) {
        if (event.getEntity() instanceof Egg) removeEgg((Egg)event.getEntity());
    }
    
    public static void removeEgg(final Egg egg) {
        if (EggBridge.bridges.containsKey(egg)) {
            if (EggBridge.bridges.get(egg) != null) EggBridge.bridges.get(egg).cancel();
            EggBridge.bridges.remove(egg);
        }
    }
    
    public static Map<Egg, EggBridgeTask> getBridges() {
        return Collections.unmodifiableMap(EggBridge.bridges);
    }
    
    static {
        EggBridge.bridges = new HashMap<>();
    }
}
