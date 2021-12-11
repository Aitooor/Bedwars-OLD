package dev.eugenio.nasgarbedwars.arena.compass.listeners;

import de.tr7zw.nbtapi.NBTItem;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerKillEvent;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.GameStatus;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.events.gameplay.GameStatusChangeEvent;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerLeaveArenaEvent;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerSpawnEvent;
import dev.eugenio.nasgarbedwars.arena.compass.data.ConfigData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

public class GameListener implements Listener {
    @EventHandler
    public void onServerQuit(PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        final UUID uuid = player.getUniqueId();
        if (!BedWars.getInstance().getApi().getArenaUtil().isPlaying(player)) return;
        final IArena arena = BedWars.getInstance().getApi().getArenaUtil().getArenaByPlayer(player);
        if (!BedWars.getInstance().isTracking(arena, uuid)) return;
        BedWars.getInstance().removeTrackingTeam(arena, uuid);
    }

    @EventHandler
    public void onLeave(PlayerLeaveArenaEvent e) {
        final IArena arena = e.getArena();
        final Player player = e.getPlayer();
        final UUID uuid = player.getUniqueId();
        if (BedWars.getInstance().isTracking(arena, uuid)) BedWars.getInstance().removeTrackingTeam(arena, uuid);
    }

    @EventHandler
    public void onKill(PlayerDeathEvent e) {
        final Player player = e.getEntity();
        if (!BedWars.getInstance().getApi().getArenaUtil().isPlaying(player)) return;
        final NBTItem nbti = new NBTItem(BedWars.getInstance().getConfigData().getItem(player, ConfigData.COMPASS_ITEM, true, "compass-item"));
        e.getDrops().remove(nbti.getItem());
    }

    @EventHandler
    public void onKill(PlayerKillEvent e) {
        final IArena arena = e.getArena();
        final Player victim = e.getVictim();
        final UUID victimUniqueId = victim.getUniqueId();
        final ITeam victimTeam = arena.getTeam(victim);
        if (BedWars.getInstance().isTracking(arena, victimUniqueId))
            BedWars.getInstance().removeTrackingTeam(arena, victimUniqueId);
        if (victimTeam.getMembers().size() == 0)
            BedWars.getInstance().getTrackingArenaMap().values().removeIf(victimTeam::equals);
    }

    @EventHandler
    public void onRespawn(PlayerSpawnEvent e) {
        final Player player = e.getPlayer();
        addToInventory(player);
    }

    @EventHandler
    public void onStateChange(GameStatusChangeEvent e) {
        final IArena arena = e.getArena();
        if (e.getNewState().equals(GameStatus.playing)) {
            arena.getPlayers().forEach(this::addToInventory);
        } else if (e.getNewState().equals(GameStatus.restarting)) {
            if (!BedWars.getInstance().getTrackingArenaMap().containsKey(arena)) return;
            BedWars.getInstance().removeTrackingArena(arena);
        }
    }

    @EventHandler
    public void onCompassDrop(ItemSpawnEvent e) {
        final ItemStack itemStack = e.getEntity().getItemStack();
        if (itemStack == null) return;
        final NBTItem nbtItem = new NBTItem(itemStack);
        if (nbtItem.getString("data").equals("compass-item")) e.setCancelled(true);
    }

    public void addToInventory(Player player) {
        final PlayerInventory inventory = player.getInventory();
        final NBTItem nbti = new NBTItem(BedWars.getInstance().getConfigData().getItem(player, ConfigData.COMPASS_ITEM, true, "compass-item"));
        inventory.setItem(nbti.getInteger("slot"), nbti.getItem());
    }
}