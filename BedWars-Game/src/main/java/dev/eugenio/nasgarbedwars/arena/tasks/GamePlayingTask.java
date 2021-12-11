package dev.eugenio.nasgarbedwars.arena.tasks;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.GameStatus;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.generator.IGenerator;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerInvisibilityDrinkEvent;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.api.tasks.PlayingTask;
import dev.eugenio.nasgarbedwars.arena.Arena;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;

public class GamePlayingTask implements Runnable, PlayingTask {
    @Getter private final Arena arena;
    private final BukkitTask task;
    private int beds_destroy_countdown;
    private int dragon_spawn_countdown;
    private int game_end_countdown;
    
    public GamePlayingTask(final Arena arena) {
        this.arena = arena;
        this.beds_destroy_countdown = BedWars.getInstance().getMainConfig().getInt("countdowns.next-event-beds-destroy");
        this.dragon_spawn_countdown = BedWars.getInstance().getMainConfig().getInt("countdowns.next-event-dragon-spawn");
        this.game_end_countdown = BedWars.getInstance().getMainConfig().getInt("countdowns.next-event-game-end");
        this.task = Bukkit.getScheduler().runTaskTimer(BedWars.getInstance(), this, 0L, 20L);
    }
    
    @Override
    public BukkitTask getBukkitTask() {
        return this.task;
    }
    
    @Override
    public int getTask() {
        return this.task.getTaskId();
    }
    
    @Override
    public int getBedsDestroyCountdown() {
        return this.beds_destroy_countdown;
    }
    
    @Override
    public int getDragonSpawnCountdown() {
        return this.dragon_spawn_countdown;
    }
    
    @Override
    public int getGameEndCountdown() {
        return this.game_end_countdown;
    }
    
    @Override
    public void run() {
        switch (this.getArena().getNextEvent()) {
            case EMERALD_GENERATOR_TIER_II:
            case EMERALD_GENERATOR_TIER_III:
            case DIAMOND_GENERATOR_TIER_II:
            case DIAMOND_GENERATOR_TIER_III: {
                if (this.getArena().upgradeDiamondsCount > 0) {
                    final Arena arena = this.getArena();
                    --arena.upgradeDiamondsCount;
                    if (this.getArena().upgradeDiamondsCount == 0) this.getArena().updateNextEvent();
                }
                if (this.getArena().upgradeEmeraldsCount <= 0) break;
                final Arena arena2 = this.getArena();
                --arena2.upgradeEmeraldsCount;
                if (this.getArena().upgradeEmeraldsCount == 0) {
                    this.getArena().updateNextEvent();
                    break;
                }
                break;
            }
            case BEDS_DESTROY: {
                --this.beds_destroy_countdown;
                if (this.getBedsDestroyCountdown() == 0) {
                    for (final Player player : this.getArena().getPlayers()) {
                        BedWars.getInstance().getNms().sendTitle(player, Language.getMsg(player, Messages.NEXT_EVENT_TITLE_ANNOUNCE_BEDS_DESTROYED), Language.getMsg(player, Messages.NEXT_EVENT_SUBTITLE_ANNOUNCE_BEDS_DESTROYED), 0, 30, 0);
                        player.sendMessage(Language.getMsg(player, Messages.NEXT_EVENT_CHAT_ANNOUNCE_BEDS_DESTROYED));
                    }
                    for (final Player player2 : this.getArena().getSpectators()) {
                        BedWars.getInstance().getNms().sendTitle(player2, Language.getMsg(player2, Messages.NEXT_EVENT_TITLE_ANNOUNCE_BEDS_DESTROYED), Language.getMsg(player2, Messages.NEXT_EVENT_SUBTITLE_ANNOUNCE_BEDS_DESTROYED), 0, 30, 0);
                        player2.sendMessage(Language.getMsg(player2, Messages.NEXT_EVENT_CHAT_ANNOUNCE_BEDS_DESTROYED));
                    }
                    for (ITeam iTeam : this.getArena().getTeams()) iTeam.setBedDestroyed(true);
                    this.getArena().updateNextEvent();
                    break;
                }
                break;
            }
            case ENDER_DRAGON: {
                --this.dragon_spawn_countdown;
                if (this.getDragonSpawnCountdown() == 0) {
                    for (final Player player3 : this.getArena().getPlayers()) {
                        BedWars.getInstance().getNms().sendTitle(player3, Language.getMsg(player3, Messages.NEXT_EVENT_TITLE_ANNOUNCE_SUDDEN_DEATH), Language.getMsg(player3, Messages.NEXT_EVENT_SUBTITLE_ANNOUNCE_SUDDEN_DEATH), 0, 30, 0);
                        for (final ITeam team : this.getArena().getTeams()) {
                            if (team.getMembers().isEmpty()) continue;
                            player3.sendMessage(Language.getMsg(player3, Messages.NEXT_EVENT_CHAT_ANNOUNCE_SUDDEN_DEATH).replace("{TeamDragons}", String.valueOf(team.getDragons())).replace("{TeamColor}", team.getColor().chat().toString()).replace("{TeamName}", team.getDisplayName(Language.getPlayerLanguage(player3))));
                        }
                    }
                    for (final Player player4 : this.getArena().getSpectators()) {
                        BedWars.getInstance().getNms().sendTitle(player4, Language.getMsg(player4, Messages.NEXT_EVENT_TITLE_ANNOUNCE_SUDDEN_DEATH), Language.getMsg(player4, Messages.NEXT_EVENT_SUBTITLE_ANNOUNCE_SUDDEN_DEATH), 0, 30, 0);
                        for (final ITeam team2 : this.getArena().getTeams()) {
                            if (team2.getMembers().isEmpty()) continue;
                            player4.sendMessage(Language.getMsg(player4, Messages.NEXT_EVENT_CHAT_ANNOUNCE_SUDDEN_DEATH).replace("{TeamDragons}", String.valueOf(team2.getDragons())).replace("{TeamColor}", team2.getColor().chat().toString()).replace("{TeamName}", team2.getDisplayName(Language.getPlayerLanguage(player4))));
                        }
                    }
                    this.getArena().updateNextEvent();
                    for (IGenerator iGenerator : this.arena.getOreGenerators()) {
                        final Location location = iGenerator.getLocation();
                        for (int i = 0; i < 20; ++i) {
                            location.clone().subtract(0.0, i, 0.0).getBlock().setType(Material.AIR);
                        }
                    }
                    for (ITeam iTeam : this.arena.getTeams()) {
                        for (IGenerator iGenerator : iTeam.getGenerators()) {
                            final Location location2 = iGenerator.getLocation();
                            for (int j = 0; j < 20; ++j) {
                                location2.clone().subtract(0.0, j, 0.0).getBlock().setType(Material.AIR);
                            }
                        }
                    }
                    for (final ITeam team3 : this.getArena().getTeams()) {
                        if (team3.getMembers().isEmpty()) continue;
                        for (int k = 0; k < team3.getDragons(); ++k) BedWars.getInstance().getNms().spawnDragon(this.getArena().getConfig().getArenaLoc("waiting.Loc").add(0.0, 10.0, 0.0), team3);
                    }
                    break;
                }
                break;
            }
            case GAME_END: {
                --this.game_end_countdown;
                if (this.getGameEndCountdown() == 0) {
                    this.getArena().checkWinner();
                    this.getArena().changeStatus(GameStatus.restarting);
                    break;
                }
                break;
            }
        }
        int n = 0;
        for (final ITeam team4 : this.getArena().getTeams()) {
            if (team4.getSize() > 1) {
                for (final Player player5 : team4.getMembers()) {
                    for (final Player player6 : team4.getMembers()) {
                        if (player6 == player5) continue;
                        if (n == 0) {
                            n = (int)player5.getLocation().distance(player6.getLocation());
                        } else {
                            if ((int)player5.getLocation().distance(player6.getLocation()) >= n) continue;
                            n = (int)player5.getLocation().distance(player6.getLocation());
                        }
                    }
                }
            }
            for (IGenerator iGenerator : team4.getGenerators()) iGenerator.spawn();
        }
        int intValue = 0;
        for (final Player player7 : this.getArena().getPlayers()) {
            if (Arena.afkCheck.get(player7.getUniqueId()) == null) {
                Arena.afkCheck.put(player7.getUniqueId(), intValue);
            } else {
                intValue = Arena.afkCheck.get(player7.getUniqueId());
                ++intValue;
                Arena.afkCheck.replace(player7.getUniqueId(), intValue);
                if (intValue != 45) {
                    continue;
                }
                BedWars.getInstance().getApi().getAFKUtil().setPlayerAFK(player7, true);
            }
        }
        if (!this.getArena().getRespawnSessions().isEmpty()) {
            for (final Map.Entry<Player, Integer> entry : this.getArena().getRespawnSessions().entrySet()) {
                if (entry.getValue() <= 0) {
                    final IArena arenaByPlayer = Arena.getArenaByPlayer(entry.getKey());
                    if (arenaByPlayer == null) {
                        this.getArena().getRespawnSessions().remove(entry.getKey());
                    } else {
                        final ITeam team5 = arenaByPlayer.getTeam(entry.getKey());
                        if (team5 == null) {
                            arenaByPlayer.addSpectator(entry.getKey(), true, null);
                        } else {
                            team5.respawnMember(entry.getKey());
                        }
                    }
                } else {
                    BedWars.getInstance().getNms().sendTitle(entry.getKey(), Language.getMsg(entry.getKey(), Messages.PLAYER_DIE_RESPAWN_TITLE).replace("{time}", String.valueOf(entry.getValue())), Language.getMsg(entry.getKey(), Messages.PLAYER_DIE_RESPAWN_SUBTITLE).replace("{time}", String.valueOf(entry.getValue())), 0, 30, 0);
                    entry.getKey().sendMessage(Language.getMsg(entry.getKey(), Messages.PLAYER_DIE_RESPAWN_CHAT).replace("{time}", String.valueOf(entry.getValue())));
                    this.getArena().getRespawnSessions().replace(entry.getKey(), entry.getValue() - 1);
                }
            }
        }
        if (!this.getArena().getShowTime().isEmpty()) {
            for (final Map.Entry<Player, Integer> entry2 : this.getArena().getShowTime().entrySet()) {
                if (entry2.getValue() <= 0) {
                    this.getArena().getShowTime().remove(entry2.getKey());
                    Bukkit.getPluginManager().callEvent(new PlayerInvisibilityDrinkEvent(PlayerInvisibilityDrinkEvent.Type.REMOVED, this.getArena().getTeam(entry2.getKey()), entry2.getKey(), this.getArena()));
                    for (Player player : entry2.getKey().getWorld().getPlayers()) BedWars.getInstance().getNms().showArmor(entry2.getKey(), player);
                } else {
                    this.getArena().getShowTime().replace(entry2.getKey(), entry2.getValue() - 1);
                }
            }
        }
        for (IGenerator iGenerator : this.getArena().getOreGenerators()) iGenerator.spawn();
    }
    
    @Override
    public void cancel() {
        this.task.cancel();
    }
}
