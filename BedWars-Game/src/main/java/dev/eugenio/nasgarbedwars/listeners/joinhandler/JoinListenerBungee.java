package dev.eugenio.nasgarbedwars.listeners.joinhandler;

import dev.eugenio.nasgarbedwars.api.arena.GameStatus;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.arena.ReJoin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class JoinListenerBungee implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onLogin(final PlayerLoginEvent event) {
        final Player player = event.getPlayer();
        final ReJoin player2 = ReJoin.getPlayer(player);
        if (player2 != null) {
            if (!player2.canReJoin()) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Language.getDefaultLanguage().m(Messages.REJOIN_DENIED));
                player2.destroy(true);
            }
            return;
        }
        final IArena arena = Arena.getArenas().get(0);
        if (arena != null) {
            if (arena.getStatus() == GameStatus.waiting || (arena.getStatus() == GameStatus.starting && arena.getStartingTask().getCountdown() > 1)) {
                if (arena.getPlayers().size() >= arena.getMaxPlayers()) {
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Language.getMsg(event.getPlayer(), Messages.COMMAND_JOIN_DENIED_IS_FULL));
                }
            } else if (arena.getStatus() == GameStatus.playing) {
                if (!arena.isAllowSpectate()) {
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Language.getDefaultLanguage().m(Messages.COMMAND_JOIN_SPECTATOR_DENIED_MSG));
                }
            } else {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Language.getDefaultLanguage().m(Messages.ARENA_STATUS_RESTARTING_NAME));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(final PlayerJoinEvent playerJoinEvent) {
        playerJoinEvent.setJoinMessage(null);
        final Player player = playerJoinEvent.getPlayer();
        if (Arena.getArenas().isEmpty()) {
            if (player.hasPermission("bw.setup")) player.performCommand("bw");
        } else {
            final IArena arena = Arena.getArenas().get(0);
            if (arena.getStatus() == GameStatus.waiting || arena.getStatus() == GameStatus.starting) {
                if (arena.addPlayer(player, false)) {
                    // Nada
                } else {
                    player.kickPlayer(Language.getMsg(player, Messages.COMMAND_JOIN_DENIED_IS_FULL));
                }
            } else {
                final ReJoin player2 = ReJoin.getPlayer(player);
                if (player2 != null) {
                    if (player2.canReJoin()) {
                        player2.reJoin(player);
                        player2.destroy(false);
                        return;
                    }
                    player.sendMessage(Language.getMsg(player, Messages.REJOIN_DENIED));
                    player2.destroy(true);
                }
                if (arena.addSpectator(player, false, null)) {
                    // Nada
                } else {
                    player.kickPlayer(Language.getMsg(player, Messages.COMMAND_JOIN_SPECTATOR_DENIED_MSG));
                }
            }
        }
    }
}
