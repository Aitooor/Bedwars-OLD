package dev.eugenio.nasgarbedwars.arena;

import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LastHit {
    @Getter private final UUID victim;
    @Getter private Entity damager;
    @Getter private long time;
    private static ConcurrentHashMap<UUID, LastHit> lastHit;
    
    public LastHit(final Player player, final Entity damager, final long time) {
        this.victim = player.getUniqueId();
        this.damager = damager;
        this.time = time;
        LastHit.lastHit.put(player.getUniqueId(), this);
    }
    
    public void setTime(final long time) {
        this.time = time;
    }
    
    public void setDamager(final Entity damager) {
        this.damager = damager;
    }
    
    public void remove() {
        LastHit.lastHit.remove(this.victim);
    }
    
    public static LastHit getLastHit(final Player player) {
        return LastHit.lastHit.getOrDefault(player.getUniqueId(), null);
    }
    
    static {
        LastHit.lastHit = new ConcurrentHashMap<>();
    }
}
