package dev.eugenio.nasgarbedwars.listeners.physics;

import dev.eugenio.nasgarbedwars.BedWars;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collection;

public class FireballTNTPhysics implements Listener {
    @EventHandler
    public void damage(EntityDamageEvent e) {
        if (e.getEntity().getType() == EntityType.ARROW) return;
        switch (e.getCause()) {
            case PROJECTILE:
            case BLOCK_EXPLOSION:
            case ENTITY_EXPLOSION:
                e.setDamage(2);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        double y = 0.3 + (e.getEntity().getVelocity().getY() * 0.3);
                        e.getEntity().setVelocity(e.getEntity().getVelocity().clone().add(new Vector(
                                e.getEntity().getVelocity().getX() * 0.5,
                                Math.min(y, 0.4),
                                e.getEntity().getVelocity().getZ() * 0.5
                        )));
                    }
                }.runTaskLaterAsynchronously(BedWars.getInstance(), 1);
                break;
        }
    }

    @EventHandler
    public void explode(EntityExplodeEvent e) {
        final Location location = e.getEntity().getLocation();
        final Collection<Entity> nearbyEntites = location.getWorld().getNearbyEntities(location, 4, 4, 4);
        for (Entity entity : nearbyEntites) {
            //TNT HOVER OTHER TNT
            if (entity.getType() == EntityType.PRIMED_TNT) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        entity.setVelocity(entity.getVelocity().clone().add(new Vector(
                                entity.getVelocity().getX() * 0.1,
                                0.3,
                                entity.getVelocity().getZ() * 0.1
                        )));
                    }
                }.runTaskLaterAsynchronously(BedWars.getInstance(), 0);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player && (e.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || e.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || e.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.FALL)) {
            ((Player) e.getEntity()).getInventory().clear();
            e.getDrops().clear();
        }
    }
}
