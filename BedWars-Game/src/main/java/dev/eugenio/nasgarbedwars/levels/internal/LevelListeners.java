package dev.eugenio.nasgarbedwars.levels.internal;

import dev.eugenio.nasgarbedwars.configuration.LevelsConfig;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.events.gameplay.GameEndEvent;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerLeaveArenaEvent;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerXPGainEvent;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class LevelListeners implements Listener {
    public static LevelListeners instance;
    
    public LevelListeners() {
        LevelListeners.instance = this;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        UUID uUID = event.getPlayer().getUniqueId();
        new PlayerLevel(uUID, 1, 0);
        Bukkit.getScheduler().runTaskAsynchronously(BedWars.getInstance(), () -> {
            Object[] arrayOfObject = BedWars.getInstance().getMySQLDatabase().getLevelData(uUID);
            PlayerLevel.getLevelByPlayer(uUID).lazyLoad((Integer) arrayOfObject[0], (Integer) arrayOfObject[1]);
        });
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(BedWars.getInstance(), () -> PlayerLevel.getLevelByPlayer(event.getPlayer().getUniqueId()).destroy());
    }
    
    @EventHandler
    public void onGameEnd(final GameEndEvent event) {
        for (final UUID uuid : event.getWinners()) {
            if (PlayerLevel.getLevelByPlayer(uuid) != null) {
                final Player player = Bukkit.getPlayer(uuid);
                if (player == null) continue;
                PlayerLevel.getLevelByPlayer(uuid).addXp(LevelsConfig.levels.getInt("xp-rewards.game-win"), PlayerXPGainEvent.XpSource.GAME_WIN);
                player.sendMessage(Language.getMsg(player, Messages.XP_REWARD_WIN).replace("{xp}", String.valueOf(LevelsConfig.levels.getInt("xp-rewards.game-win"))));
                final ITeam exTeam = event.getArena().getExTeam(player.getUniqueId());
                if (exTeam == null || exTeam.getMembersCache().size() <= 1) continue;
                final int n = LevelsConfig.levels.getInt("xp-rewards.per-teammate") * exTeam.getMembersCache().size();
                PlayerLevel.getLevelByPlayer(uuid).addXp(n, PlayerXPGainEvent.XpSource.PER_TEAMMATE);
                player.sendMessage(Language.getMsg(player, "xp-reward-per-teammate").replace("{xp}", String.valueOf(n)));
            }
        }
        for (final UUID uuid2 : event.getLosers()) {
            if (PlayerLevel.getLevelByPlayer(uuid2) != null) {
                final Player player2 = Bukkit.getPlayer(uuid2);
                if (player2 == null) continue;
                final ITeam exTeam2 = event.getArena().getExTeam(player2.getUniqueId());
                if (exTeam2 == null || exTeam2.getMembersCache().size() <= 1) continue;
                final int n2 = LevelsConfig.levels.getInt("xp-rewards.per-teammate") * exTeam2.getMembersCache().size();
                PlayerLevel.getLevelByPlayer(uuid2).addXp(n2, PlayerXPGainEvent.XpSource.PER_TEAMMATE);
                player2.sendMessage(Language.getMsg(player2, Messages.XP_REWARD_PER_TEAMMATE).replace("{xp}", String.valueOf(n2)));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onArenaLeave(final PlayerLeaveArenaEvent event) {
        UUID uUID = event.getPlayer().getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(BedWars.getInstance(), () -> {
            PlayerLevel playerLevel = PlayerLevel.getLevelByPlayer(uUID);
            if (playerLevel != null) playerLevel.updateDatabase();
        });
    }
}
