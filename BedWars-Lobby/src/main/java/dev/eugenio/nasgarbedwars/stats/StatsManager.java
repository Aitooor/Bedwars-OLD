package dev.eugenio.nasgarbedwars.stats;

import dev.eugenio.nasgarbedwars.BedWars;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StatsManager {
    private final Map<UUID, PlayerStats> stats;
    
    public StatsManager() {
        this.stats = new ConcurrentHashMap<>();
        this.registerListeners();
    }
    
    public void remove(final UUID uuid) {
        this.stats.remove(uuid);
    }
    
    public void put(final UUID uuid, final PlayerStats playerStats) {
        this.stats.put(uuid, playerStats);
    }
    
    public PlayerStats get(final UUID uuid) {
        final PlayerStats playerStats = this.stats.get(uuid);
        if (playerStats == null) throw new IllegalStateException("Intentando coger stats de un jugador ya descargado.");
        return playerStats;
    }
    
    public PlayerStats getUnsafe(final UUID uuid) {
        return this.stats.get(uuid);
    }
    
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new StatsListener(), BedWars.getInstance());
    }
}
