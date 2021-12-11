package dev.eugenio.nasgarbedwars.arena.upgrades;

import dev.eugenio.nasgarbedwars.api.arena.GameStatus;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerBaseEnterEvent;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerBaseLeaveEvent;
import dev.eugenio.nasgarbedwars.api.events.upgrades.UpgradeEvent;
import dev.eugenio.nasgarbedwars.api.upgrades.EnemyBaseEnterTrap;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.arena.team.BedWarsTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;

import java.util.Map;
import java.util.WeakHashMap;

public class BaseListener implements Listener {
    public static Map<Player, ITeam> isOnABase;
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final IArena arenaByIdentifier = Arena.getArenaByIdentifier(player.getWorld().getName());
        if (arenaByIdentifier == null) return;
        if (arenaByIdentifier.getStatus() != GameStatus.playing) return;
        checkEvents(player, arenaByIdentifier);
    }
    
    @EventHandler
    public void onTeleport(final PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        if (BaseListener.isOnABase.containsKey(player)) {
            final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
            if (arenaByPlayer == null) {
                BaseListener.isOnABase.remove(player);
                return;
            }
            checkEvents(player, arenaByPlayer);
        }
    }
    
    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final IArena arenaByPlayer = Arena.getArenaByPlayer(event.getEntity());
        if (arenaByPlayer == null) return;
        checkEvents(event.getEntity(), arenaByPlayer);
    }
    
    private static void checkEvents(final Player player, final IArena arena) {
        if (player == null || arena == null) return;
        if (arena.isSpectator(player)) return;
        boolean b = true;
        for (final ITeam team : arena.getTeams()) {
            if (player.getLocation().distance(team.getBed()) <= arena.getIslandRadius()) {
                b = false;
                if (BaseListener.isOnABase.containsKey(player)) {
                    if (BaseListener.isOnABase.get(player) == team) continue;
                    Bukkit.getPluginManager().callEvent(new PlayerBaseLeaveEvent(player, BaseListener.isOnABase.get(player)));
                    if (!Arena.magicMilk.containsKey(player.getUniqueId())) Bukkit.getPluginManager().callEvent(new PlayerBaseEnterEvent(player, team));
                    BaseListener.isOnABase.replace(player, team);
                } else {
                    if (Arena.magicMilk.containsKey(player.getUniqueId())) continue;
                    Bukkit.getPluginManager().callEvent(new PlayerBaseEnterEvent(player, team));
                    BaseListener.isOnABase.put(player, team);
                }
            }
        }
        if (b && BaseListener.isOnABase.containsKey(player)) {
            Bukkit.getPluginManager().callEvent(new PlayerBaseLeaveEvent(player, BaseListener.isOnABase.get(player)));
            BaseListener.isOnABase.remove(player);
        }
    }
    
    @EventHandler
    public void onUpgradeBuy(final UpgradeEvent event) {
        if (event.getTeamUpgrade() instanceof EnemyBaseEnterTrap) {
            for (final Player player : event.getTeam().getArena().getPlayers()) {
                if (event.getTeam().isMember(player)) continue;
                if (event.getTeam().getArena().isReSpawning(player)) continue;
                if (player.getLocation().distance(event.getTeam().getBed()) <= event.getTeam().getArena().getIslandRadius()) {
                    event.getTeam().getActiveTraps().get(0).trigger(event.getTeam(), player);
                    event.getTeam().getActiveTraps().remove(0);
                }
            }
        }
    }
    
    @EventHandler
    public void onBaseEnter(final PlayerBaseEnterEvent event) {
        if (event == null) return;
        final ITeam team = event.getTeam();
        if (team.isMember(event.getPlayer())) {
            for (PotionEffect potionEffect : team.getBaseEffects()) event.getPlayer().addPotionEffect(potionEffect, true);
        } else if (!team.getActiveTraps().isEmpty() && !team.isBedDestroyed()) {
            team.getActiveTraps().get(0).trigger(team, event.getPlayer());
            team.getActiveTraps().remove(0);
        }
    }
    
    @EventHandler
    public void onBaseLeave(final PlayerBaseLeaveEvent event) {
        if (event == null) return;
        final BedWarsTeam bedWarsTeam = (BedWarsTeam)event.getTeam();
        if (bedWarsTeam.isMember(event.getPlayer())) for (final PotionEffect potionEffect : event.getPlayer().getActivePotionEffects()) for (final PotionEffect potionEffect2 : bedWarsTeam.getBaseEffects()) if (potionEffect.getType() == potionEffect2.getType()) event.getPlayer().removePotionEffect(potionEffect2.getType());
    }
    
    static {
        BaseListener.isOnABase = new WeakHashMap<>();
    }
}
