package dev.eugenio.nasgarbedwars.levels.internal;

import dev.eugenio.nasgarbedwars.configuration.LevelsConfig;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerXPGainEvent;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class PerMinuteTask {
    private final int xp;
    private final BukkitTask task;
    
    public PerMinuteTask(final Arena arena) {
        this.xp = LevelsConfig.levels.getInt("xp-rewards.per-minute");
        this.task = Bukkit.getScheduler().runTaskTimer(BedWars.getInstance(), () -> {
            for (Player player : arena.getPlayers()) {
                PlayerLevel.getLevelByPlayer(player.getUniqueId()).addXp(this.xp, PlayerXPGainEvent.XpSource.PER_MINUTE);
                player.sendMessage(Language.getMsg(player, Messages.XP_REWARD_PER_MINUTE).replace("{xp}", String.valueOf(this.xp)));
            }
        }, 1200L, 1200L);
    }
    
    public void cancel() {
        if (this.task != null) this.task.cancel();
    }
}
