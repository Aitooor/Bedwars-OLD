package dev.eugenio.nasgarbedwars.cosmetics.listeners;

import dev.eugenio.nasgarbedwars.utils.FireworkUtils;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerKillEvent;
import dev.eugenio.nasgarbedwars.stats.PlayerStats;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class PlayerFinalKill implements Listener {
    @EventHandler
    public void onFinalKillCosmetic(PlayerKillEvent event) {
        if (event == null) return;
        if (event.getKiller() == null || event.getVictim() == null) return;
        if (!event.getCause().isFinalKill()) return;
        final PlayerStats value = BedWars.getInstance().getStatsManager().get(event.getKiller().getUniqueId());
        switch (value.getFinalKill().getName()) {
            case "Cristales rotos":
                Location upperLoc = event.getVictim().getLocation().clone().getBlock().getLocation().add(0, 1.0, 0);
                event.getKiller().getWorld().playEffect(event.getVictim().getLocation(), Effect.STEP_SOUND, Material.STAINED_GLASS, 2);
                event.getKiller().getWorld().playEffect(upperLoc, Effect.STEP_SOUND, Material.STAINED_GLASS, 2);
                event.getKiller().playSound(event.getKiller().getLocation(), Sound.GLASS, 1L, 1L);
                break;
            case "Fuegos artificiales":
                FireworkUtils.spawnFireworks(event.getVictim().getLocation());
                break;
            case "Misil de pulpo":
                final long[] pitch = {1L};
                Entity squid = event.getKiller().getWorld().spawnEntity(event.getVictim().getLocation(), EntityType.SQUID);
                Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
                    Bukkit.getScheduler().scheduleSyncRepeatingTask(BedWars.getInstance(), () -> {
                        pitch[0]++;
                        squid.getWorld().playEffect(squid.getLocation(), Effect.FLAME, 1);
                        event.getKiller().playSound(event.getKiller().getLocation(), Sound.CHICKEN_EGG_POP, 1L, pitch[0]);
                    }, 5L, 30L);

                    Bukkit.getScheduler().scheduleSyncRepeatingTask(BedWars.getInstance(), () -> squid.setVelocity(new Vector(0.0, 0.4, 0.0)), 2L, 30L);

                    Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
                        FireworkUtils.spawnFireworks(squid.getLocation());
                        squid.remove();
                    }, 31L);

                }, 1L);
                break;
            case "Rayo":
                event.getKiller().getWorld().strikeLightningEffect(event.getVictim().getLocation());
                event.getKiller().playSound(event.getKiller().getLocation(), Sound.AMBIENCE_THUNDER, 10L, 1L);
                break;
            case "Rekt":
                createArmorStand("§b" + event.getKiller().getName() + "§eha #rekteado a §7" + event.getVictim().getName() + "§eaquí", event.getVictim().getLocation());
                Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
                    removeArmorStands(event.getKiller().getWorld());
                }, 300L);
                break;
            case "Explosión de sangre":
                Location upperLoc2 = event.getVictim().getLocation().clone().getBlock().getLocation().add(0, 1.0, 0);
                event.getKiller().getWorld().playEffect(event.getVictim().getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK, 1);
                event.getKiller().getWorld().playEffect(upperLoc2, Effect.STEP_SOUND, Material.REDSTONE_BLOCK, 1);
                event.getKiller().playSound(event.getKiller().getLocation(), Sound.STEP_WOOD, 2L, 1L);
                break;
            case "Dinamita":
                TNTPrimed tnt = event.getKiller().getWorld().spawn(event.getVictim().getLocation(), TNTPrimed.class);
                tnt.setFuseTicks(45);
                tnt.setMetadata("bwcosmetic", new FixedMetadataValue(BedWars.getInstance(), "tnt"));
                Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> tnt.setVelocity(new Vector(0.0, 0.6, 0.0)), 2L);
                break;
            case "Cohete de vaca":
                final long[] pitch2 = {1L};
                Entity cow = event.getKiller().getWorld().spawnEntity(event.getVictim().getLocation(), EntityType.COW);
                Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
                    Bukkit.getScheduler().scheduleSyncRepeatingTask(BedWars.getInstance(), () -> {
                        pitch2[0]++;
                        cow.getWorld().playEffect(cow.getLocation(), Effect.FLAME, 1);
                        event.getKiller().playSound(event.getKiller().getLocation(), Sound.CHICKEN_EGG_POP, 1L, pitch2[0]);
                    }, 5L, 30L);

                    Bukkit.getScheduler().scheduleSyncRepeatingTask(BedWars.getInstance(), () -> cow.setVelocity(new Vector(0.0, 0.4, 0.0)), 2L, 30L);

                    Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
                        FireworkUtils.spawnFireworks(cow.getLocation());
                        cow.remove();
                    }, 31L);

                }, 1L);
                break;
            case "Vela":

                break;
        }
    }

    @EventHandler
    public void onCosmeticTNTExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof TNTPrimed && event.getEntity().hasMetadata("bwcosmetic")) event.blockList().clear();
    }

    @EventHandler
    public void onCosmeticNoHurt(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
        if (event.getDamager() instanceof TNTPrimed && event.getEntity().hasMetadata("bwcosmetic")) event.setCancelled(true);
    }

    private void createArmorStand(final String customName, final Location location) {
        final ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location.getBlock().getLocation().add(0.5, 2.0, 0.5), EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setMarker(true);
        armorStand.setGravity(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(customName);
        armorStand.setMetadata("bwcosmetic", new FixedMetadataValue(BedWars.getInstance(), "hologram"));
    }

    private void removeArmorStands(final World world) {
        for (final Entity entity : world.getEntities()) if (entity.hasMetadata("bwcosmetic") && entity instanceof ArmorStand) entity.remove();
    }
}
