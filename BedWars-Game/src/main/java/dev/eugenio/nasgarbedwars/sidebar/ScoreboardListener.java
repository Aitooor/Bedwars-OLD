package dev.eugenio.nasgarbedwars.sidebar;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerBedBreakEvent;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerKillEvent;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerReJoinEvent;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerSpawnEvent;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class ScoreboardListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDamage(final EntityDamageEvent event) {
        if (event == null) return;
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Player)) return;
        final Player player = (Player)event.getEntity();
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        final int n = (int)(player.getHealth() - event.getDamage()) + 1;
        if (arenaByPlayer == null) return;
        for (final BedWarsScoreboard bedWarsScoreboard : BedWarsScoreboard.getScoreboards().values()) if (arenaByPlayer.equals(bedWarsScoreboard.getArena())) bedWarsScoreboard.getHandle().refreshHealth(player, n);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRegain(final EntityRegainHealthEvent event) {
        if (event == null) return;
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Player)) return;
        final Player player = (Player)event.getEntity();
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) return;
        int n = (int)(player.getHealth() + event.getAmount()) + 1;
        if (n > player.getMaxHealth()) --n;
        for (final BedWarsScoreboard bedWarsScoreboard : BedWarsScoreboard.getScoreboards().values()) if (arenaByPlayer.equals(bedWarsScoreboard.getArena())) bedWarsScoreboard.getHandle().refreshHealth(player, n);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onReSpawn(final PlayerSpawnEvent event) {
        if (event == null) return;
        final IArena arena = event.getArena();
        for (final BedWarsScoreboard bedWarsScoreboard : BedWarsScoreboard.getScoreboards().values()) if (arena.equals(bedWarsScoreboard.getArena())) bedWarsScoreboard.getHandle().refreshHealth(event.getPlayer(), (int)event.getPlayer().getHealth());
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void reJoin(final PlayerReJoinEvent event) {
        if (event == null) return;
        if (event.isCancelled()) return;
        if (!BedWars.getInstance().getMainConfig().getBoolean("scoreboard-settings.player-list.format-playing-list")) return;
        final IArena arena = event.getArena();
        final Player player = event.getPlayer();
        for (final BedWarsScoreboard bedWarsScoreboard : BedWarsScoreboard.getScoreboards().values()) if (arena.equals(bedWarsScoreboard.getArena())) bedWarsScoreboard.addToTabList(player, Messages.FORMATTING_SCOREBOARD_TAB_PREFIX_PLAYING, Messages.FORMATTING_SCOREBOARD_TAB_SUFFIX_PLAYING);
    }
    
    @EventHandler
    public void onBedDestroy(final PlayerBedBreakEvent event) {
        if (event == null) return;
        BedWarsScoreboard.getScoreboards().values().forEach(bedWarsScoreboard -> {
            if (event.getArena().equals(bedWarsScoreboard.getArena())) bedWarsScoreboard.getHandle().refreshPlaceholders();
        });
    }
    
    @EventHandler
    public void onFinalKill(final PlayerKillEvent event) {
        if (event == null) return;
        if (!event.getCause().isFinalKill()) return;
        BedWarsScoreboard.getScoreboards().values().forEach(bedWarsScoreboard -> {
            if (event.getArena().equals(bedWarsScoreboard.getArena())) bedWarsScoreboard.getHandle().refreshPlaceholders();
        });
    }
}
