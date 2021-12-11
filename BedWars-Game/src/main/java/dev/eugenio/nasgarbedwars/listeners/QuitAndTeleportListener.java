package dev.eugenio.nasgarbedwars.listeners;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.GameStatus;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.arena.LastHit;
import dev.eugenio.nasgarbedwars.arena.SetupSession;
import dev.eugenio.nasgarbedwars.arena.team.BedWarsTeam;
import dev.eugenio.nasgarbedwars.sidebar.BedWarsScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;

public class QuitAndTeleportListener implements Listener {
    @EventHandler
    public void onLeave(final PlayerQuitEvent event) {
        event.setQuitMessage(null);
        final Player player = event.getPlayer();
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer != null) {
            if (arenaByPlayer.isPlayer(player)) {
                arenaByPlayer.removePlayer(player, true);
            } else if (arenaByPlayer.isSpectator(player)) {
                arenaByPlayer.removeSpectator(player, true);
            }
        }
        if (Language.getLangByPlayer().containsKey(player.getUniqueId())) {
            UUID uUID = player.getUniqueId();
            Bukkit.getScheduler().runTaskAsynchronously(BedWars.getInstance(), () -> {
                String str = Language.getLangByPlayer().get(player.getUniqueId()).getIso();
                if (Language.isLanguageExist(str)) BedWars.getInstance().getMySQLDatabase().setLanguage(uUID, str);
                Language.getLangByPlayer().remove(player.getUniqueId());
            });
        }
        final SetupSession session = SetupSession.getSession(player.getUniqueId());
        if (session != null) session.cancel();
        final BedWarsScoreboard sBoard = BedWarsScoreboard.getSBoard(event.getPlayer().getUniqueId());
        if (sBoard != null) sBoard.remove();
        BedWarsTeam.reSpawnInvulnerability.remove(event.getPlayer().getUniqueId());
        final LastHit lastHit = LastHit.getLastHit(player);
        if (lastHit != null) lastHit.remove();
    }
    
    @EventHandler
    public void onTeleport(final PlayerTeleportEvent event) {
        if (event == null) return;
        if (event.isCancelled()) return;
        if (event.getTo() == null) return;
        if (event.getTo().getWorld() == null) return;
        final IArena arenaByPlayer = Arena.getArenaByPlayer(event.getPlayer());
        if (arenaByPlayer != null) {
            final IArena arenaByIdentifier = Arena.getArenaByIdentifier(event.getTo().getWorld().getName());
            if (arenaByIdentifier != null && !arenaByIdentifier.equals(arenaByPlayer)) {
                if (arenaByPlayer.isSpectator(event.getPlayer())) arenaByPlayer.removeSpectator(event.getPlayer(), false);
                if (arenaByPlayer.isPlayer(event.getPlayer())) arenaByPlayer.removePlayer(event.getPlayer(), false);
                event.getPlayer().sendMessage("PlayerTeleportEvent algo ha ido mal, te has unido a una arena estando en un mapa diferente");
            }
        }
    }
    
    @EventHandler
    public void onWorldChange(final PlayerChangedWorldEvent event) {
        if (Arena.isInArena(event.getPlayer())) {
            final IArena arenaByPlayer = Arena.getArenaByPlayer(event.getPlayer());
            if (arenaByPlayer.isPlayer(event.getPlayer())) {
                if (arenaByPlayer.getStatus() == GameStatus.waiting || arenaByPlayer.getStatus() == GameStatus.starting) return;
                if (!event.getPlayer().getWorld().getName().equalsIgnoreCase(arenaByPlayer.getWorld().getName())) {
                    arenaByPlayer.removePlayer(event.getPlayer(), true);
                    BedWars.debug(event.getPlayer().getName() + " fue removido de " + arenaByPlayer.getDisplayName() + " porque fue teletransportado fuera de la arena.");
                }
            }
        }
    }
}
