package dev.eugenio.nasgarbedwars.arena.tasks;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.GameStatus;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.NextEvent;
import dev.eugenio.nasgarbedwars.api.arena.generator.GeneratorType;
import dev.eugenio.nasgarbedwars.api.arena.generator.IGenerator;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.api.tasks.StartingTask;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.arena.team.BedWarsTeam;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class GameStartingTask implements Runnable, StartingTask {
    @Getter private int countdown;
    @Getter private final IArena arena;
    private final BukkitTask task;
    
    public GameStartingTask(final Arena arena) {
        this.arena = arena;
        this.countdown = BedWars.getInstance().getMainConfig().getInt("countdowns.game-start-regular");
        this.task = Bukkit.getScheduler().runTaskTimer(BedWars.getInstance(), this, 0L, 20L);
    }
    
    @Override
    public void setCountdown(final int countdown) {
        this.countdown = countdown;
    }
    
    @Override
    public int getTask() {
        return this.task.getTaskId();
    }
    
    @Override
    public BukkitTask getBukkitTask() {
        return this.task;
    }
    
    @Override
    public void run() {
        if (this.countdown == 0) {
            this.getArena().getTeamAssigner().assignTeams(this.getArena());
            for (final ITeam team : this.getArena().getTeams()) {
                BedWars.getInstance().getNms().colorBed(team);
                if (team.getMembers().isEmpty()) {
                    team.setBedDestroyed(true);
                    if (!this.getArena().getConfig().getBoolean("disable-generator-for-empty-teams")) continue;
                    for (IGenerator iGenerator : team.getGenerators()) iGenerator.disable();
                }
            }
            Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
                for (IGenerator iGenerator : this.getArena().getOreGenerators()) {
                    if (iGenerator.getType() != GeneratorType.EMERALD && iGenerator.getType() != GeneratorType.DIAMOND) continue;
                    iGenerator.enableRotation();
                }
            }, 60L);
            BedWars.getInstance().getApi().getRestoreAdapter().onLobbyRemoval(this.arena);
            this.spawnPlayers();
            this.task.cancel();
            this.getArena().changeStatus(GameStatus.playing);
            if (this.getArena().getUpgradeDiamondsCount() < this.getArena().getUpgradeEmeraldsCount()) {
                this.getArena().setNextEvent(NextEvent.DIAMOND_GENERATOR_TIER_II);
            } else {
                this.getArena().setNextEvent(NextEvent.EMERALD_GENERATOR_TIER_II);
            }
            for (ITeam iTeam : this.getArena().getTeams()) iTeam.spawnNPCs();
            return;
        }
        if (this.getCountdown() % 10 == 0 || this.getCountdown() <= 5) {
            if (this.getCountdown() == 10) {
                for (Player player : this.getArena().getPlayers()) player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1, 1);
            } else if (this.getCountdown() < 5) {
                for (Player player : this.getArena().getPlayers()) player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1, 1);
            } else {
                for (Player player : this.getArena().getPlayers()) player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1, 1);
            }
            for (final Player player : this.getArena().getPlayers()) {
                final String[] countDownTitle = Language.getCountDownTitle(Language.getPlayerLanguage(player), this.getCountdown());
                BedWars.getInstance().getNms().sendTitle(player, countDownTitle[1], countDownTitle[0], 4, 22, 4);
                player.sendMessage(Language.getMsg(player, Messages.ARENA_STATUS_START_COUNTDOWN_CHAT).replace("{time}", String.valueOf(this.getCountdown())));
            }
        }
        --this.countdown;
    }
    
    private void spawnPlayers() {
        for (final ITeam team : this.getArena().getTeams()) {
            for (final Player player : new ArrayList<>(team.getMembers())) {
                BedWarsTeam.reSpawnInvulnerability.put(player.getUniqueId(), System.currentTimeMillis() + 2000L);
                team.firstSpawn(player);
                for (String s : Language.getList(player, Messages.ARENA_STATUS_START_PLAYER_TUTORIAL)) player.sendMessage(s);
            }
        }
    }
    
    @Override
    public void cancel() {
        this.task.cancel();
    }
}
