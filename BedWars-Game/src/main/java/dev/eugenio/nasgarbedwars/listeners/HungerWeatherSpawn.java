package dev.eugenio.nasgarbedwars.listeners;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.GameStatus;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class HungerWeatherSpawn implements Listener {
    @EventHandler
    public void onFoodChange(final FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onWeatherChange(final WeatherChangeEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onCreatureSpawn(final CreatureSpawnEvent creatureSpawnEvent) {
        if (creatureSpawnEvent.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) creatureSpawnEvent.setCancelled(true);
    }
    
    @EventHandler
    public void onDrink(final PlayerItemConsumeEvent event) {
        if (Arena.getArenaByPlayer(event.getPlayer()) == null) return;
        switch (event.getItem().getType()) {
            case GLASS_BOTTLE:
                BedWars.getInstance().getNms().minusAmount(event.getPlayer(), event.getItem(), 1);
                break;
            case MILK_BUCKET:
                event.setCancelled(true);
                BedWars.getInstance().getNms().minusAmount(event.getPlayer(), event.getItem(), 1);
                Arena.magicMilk.put(event.getPlayer().getUniqueId(), Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
                    Arena.magicMilk.remove(event.getPlayer().getUniqueId());
                    BedWars.debug("PlayerItemConsumeEvent player " + event.getPlayer() + " fue removido de la lechita mágica");
                }, 600L).getTaskId());
                break;
        }
    }
    
    @EventHandler
    public void onItemSpawn(final ItemSpawnEvent event) {
        final IArena arenaByIdentifier = Arena.getArenaByIdentifier(event.getEntity().getLocation().getWorld().getName());
        if (arenaByIdentifier == null) return;
        if (arenaByIdentifier.getStatus() != GameStatus.playing) event.setCancelled(true);
    }
}
