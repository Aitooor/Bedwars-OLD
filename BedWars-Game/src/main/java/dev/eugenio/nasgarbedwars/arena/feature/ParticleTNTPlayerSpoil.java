package dev.eugenio.nasgarbedwars.arena.feature;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.events.shop.ShopBuyEvent;
import dev.eugenio.nasgarbedwars.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.LinkedList;

public class ParticleTNTPlayerSpoil implements Listener {
    private static ParticleTNTPlayerSpoil instance;
    private final LinkedList<Player> playersWithTnt;

    private ParticleTNTPlayerSpoil() {
        this.playersWithTnt = new LinkedList<>();
        Bukkit.getPluginManager().registerEvents(new TNTListener(), BedWars.getInstance());
        Bukkit.getScheduler().runTaskTimer(BedWars.getInstance(), new ParticleTask(), 20L, 1L);
    }

    public static void init() {
        if (BedWars.getInstance().getMainConfig().getBoolean("performance-settings.spoil-tnt-players") && ParticleTNTPlayerSpoil.instance == null) ParticleTNTPlayerSpoil.instance = new ParticleTNTPlayerSpoil();
    }

    private static class ParticleTask implements Runnable {
        @Override
        public void run() {
            for (Player player : ParticleTNTPlayerSpoil.instance.playersWithTnt) BedWars.getInstance().getNms().playRedStoneDot(player);
        }
    }

    private static class TNTListener implements Listener {
        @EventHandler(ignoreCancelled = true)
        public void onPickUp(final PlayerPickupItemEvent event) {
            final Player player = event.getPlayer();
            if (event.getItem().getItemStack().getType() == Material.TNT && Arena.getArenaByPlayer(player) != null) return;
            ParticleTNTPlayerSpoil.instance.playersWithTnt.add(player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrop(final PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        if (event.getItemDrop().getItemStack().getType() == Material.TNT && Arena.getArenaByPlayer(player) != null && ParticleTNTPlayerSpoil.instance.playersWithTnt.contains(player) && !player.getInventory().contains(Material.TNT)) ParticleTNTPlayerSpoil.instance.playersWithTnt.remove(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(final BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        if (event.getItemInHand().getType() == Material.TNT && Arena.getArenaByPlayer(player) != null && ParticleTNTPlayerSpoil.instance.playersWithTnt.contains(player)) {
            Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
                if (!player.getInventory().contains(Material.TNT)) ParticleTNTPlayerSpoil.instance.playersWithTnt.remove(player);
            }, 1L);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void inventorySwitch(final InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        if (player == null) return;
        if (ParticleTNTPlayerSpoil.instance.playersWithTnt.contains(player)) {
            if (!player.getInventory().contains(Material.TNT))
                ParticleTNTPlayerSpoil.instance.playersWithTnt.remove(player);
        } else if (player.getInventory().contains(Material.TNT)) {
            ParticleTNTPlayerSpoil.instance.playersWithTnt.add(player);
        }
    }

    @EventHandler
    public void onBuy(final ShopBuyEvent event) {
        if (event.getBuyer().getInventory().contains(Material.TNT) && !ParticleTNTPlayerSpoil.instance.playersWithTnt.contains(event.getBuyer())) ParticleTNTPlayerSpoil.instance.playersWithTnt.add(event.getBuyer());
    }
}
