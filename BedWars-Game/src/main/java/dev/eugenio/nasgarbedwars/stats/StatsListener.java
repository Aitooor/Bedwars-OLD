package dev.eugenio.nasgarbedwars.stats;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.GameStatus;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.events.gameplay.GameEndEvent;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerBedBreakEvent;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerKillEvent;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerLeaveArenaEvent;
import dev.eugenio.nasgarbedwars.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Instant;
import java.util.UUID;

public class StatsListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPreLoginEvent(final AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) return;
        final PlayerStats fetchStats = BedWars.getInstance().getMySQLDatabase().fetchStats(event.getUniqueId());
        fetchStats.setName(event.getName());
        BedWars.getInstance().getStatsManager().put(event.getUniqueId(), fetchStats);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLoginEvent(final PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) BedWars.getInstance().getStatsManager().remove(event.getPlayer().getUniqueId());
    }
    
    @EventHandler
    public void onBedBreak(final PlayerBedBreakEvent event) {
        final PlayerStats value = BedWars.getInstance().getStatsManager().get(event.getPlayer().getUniqueId());
        value.setBedsDestroyed(value.getBedsDestroyed() + 1);
    }
    
    @EventHandler
    public void onPlayerKill(final PlayerKillEvent event) {
        final PlayerStats value = BedWars.getInstance().getStatsManager().get(event.getVictim().getUniqueId());
        final PlayerStats playerStats = event.getVictim().equals(event.getKiller()) ? null : ((event.getKiller() == null) ? null : BedWars.getInstance().getStatsManager().getUnsafe(event.getKiller().getUniqueId()));
        if (event.getCause().isFinalKill()) {
            value.setFinalDeaths(value.getFinalDeaths() + 1);
            value.setLosses(value.getLosses() + 1);
            value.setGamesPlayed(value.getGamesPlayed() + 1);
            if (playerStats != null) playerStats.setFinalKills(playerStats.getFinalKills() + 1);
        } else {
            value.setDeaths(value.getDeaths() + 1);
            if (playerStats != null) playerStats.setKills(playerStats.getKills() + 1);
        }
    }
    
    @EventHandler
    public void onGameEnd(final GameEndEvent event) {
        for (final UUID uuid : event.getWinners()) {
            final Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            if (!player.isOnline()) continue;
            final PlayerStats value = BedWars.getInstance().getStatsManager().get(uuid);
            value.setWins(value.getWins() + 1);
            final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
            if (arenaByPlayer == null || !arenaByPlayer.equals(event.getArena())) continue;
            value.setGamesPlayed(value.getGamesPlayed() + 1);
        }
    }
    
    @EventHandler
    public void onArenaLeave(final PlayerLeaveArenaEvent event) {
        final Player player = event.getPlayer();
        final ITeam exTeam = event.getArena().getExTeam(player.getUniqueId());
        if (exTeam == null) return;
        if (event.getArena().getStatus() == GameStatus.starting || event.getArena().getStatus() == GameStatus.waiting) return;
        final PlayerStats value = BedWars.getInstance().getStatsManager().get(player.getUniqueId());
        if (value == null) return;
        final Instant now = Instant.now();
        value.setLastPlay(now);
        if (value.getFirstPlay() == null) value.setFirstPlay(now);
        if (event.getArena().getStatus() == GameStatus.playing) {
            if (exTeam.isBedDestroyed()) {
                if (event.getArena().isPlayer(player)) {
                    value.setFinalDeaths(value.getFinalDeaths() + 1);
                    value.setLosses(value.getLosses() + 1);
                }
                final Player lastDamager = event.getLastDamager();
                final ITeam team = event.getArena().getTeam(lastDamager);
                if (lastDamager != null && event.getArena().isPlayer(lastDamager) && team != null) {
                    final PlayerStats value2 = BedWars.getInstance().getStatsManager().get(lastDamager.getUniqueId());
                    value2.setFinalKills(value2.getFinalKills() + 1);
                    event.getArena().addPlayerKill(lastDamager, true, player);
                }
            } else {
                final Player lastDamager2 = event.getLastDamager();
                final ITeam team2 = event.getArena().getTeam(lastDamager2);
                if (event.getLastDamager() != null && event.getArena().isPlayer(lastDamager2) && team2 != null) {
                    value.setDeaths(value.getDeaths() + 1);
                    event.getArena().addPlayerDeath(player);
                    event.getArena().addPlayerKill(lastDamager2, false, player);
                    final PlayerStats value3 = BedWars.getInstance().getStatsManager().get(lastDamager2.getUniqueId());
                    value3.setKills(value3.getKills() + 1);
                }
            }
        }
        Bukkit.getScheduler().runTaskAsynchronously(BedWars.getInstance(), () -> BedWars.getInstance().getMySQLDatabase().saveStats(value));
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(final PlayerQuitEvent event) {
        BedWars.getInstance().getStatsManager().remove(event.getPlayer().getUniqueId());
    }
}
