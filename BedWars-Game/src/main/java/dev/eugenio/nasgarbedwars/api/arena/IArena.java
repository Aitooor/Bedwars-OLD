package dev.eugenio.nasgarbedwars.api.arena;

import dev.eugenio.nasgarbedwars.api.arena.generator.IGenerator;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeamAssigner;
import dev.eugenio.nasgarbedwars.api.configuration.ConfigManager;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.region.Region;
import dev.eugenio.nasgarbedwars.api.tasks.PlayingTask;
import dev.eugenio.nasgarbedwars.api.tasks.RestartingTask;
import dev.eugenio.nasgarbedwars.api.tasks.StartingTask;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public interface IArena {
    boolean isSpectator(final Player p0);

    boolean isSpectator(final UUID p0);

    boolean isReSpawning(final UUID p0);

    String getArenaName();

    void init(final World p0);

    ConfigManager getConfig();

    boolean isPlayer(final Player p0);

    List<Player> getSpectators();

    ITeam getTeam(final Player p0);

    ITeam getExTeam(final UUID p0);

    String getDisplayName();

    GameStatus getStatus();

    void setStatus(final GameStatus p0);

    List<Player> getPlayers();

    int getMaxPlayers();

    String getGroup();

    void setGroup(final String p0);

    int getMaxInTeam();

    ConcurrentHashMap<Player, Integer> getRespawnSessions();

    void updateSpectatorCollideRule(final Player p0, final boolean p1);

    void updateNextEvent();

    boolean addPlayer(final Player p0, final boolean p1);

    boolean addSpectator(final Player p0, final boolean p1, final Location p2);

    void removePlayer(final Player p0, final boolean p1);

    void removeSpectator(final Player p0, final boolean p1);

    boolean reJoin(final Player p0);

    void disable();

    void restart();

    World getWorld();

    String getDisplayStatus(final Language p0);

    String getDisplayGroup(final Player p0);

    String getDisplayGroup(final Language p0);

    List<ITeam> getTeams();

    void addPlacedBlock(final Block p0);

    void removePlacedBlock(final Block p0);

    boolean isBlockPlaced(final Block p0);

    int getPlayerKills(final Player p0, final boolean p1);

    int getPlayerBedsDestroyed(final Player p0);

    int getIslandRadius();

    void changeStatus(final GameStatus p0);

    @Deprecated
    default boolean isRespawning(final Player p) {
        return this.isReSpawning(p);
    }

    void addPlayerKill(final Player p0, final boolean p1, final Player p2);

    void addPlayerBedDestroyed(final Player p0);

    @Deprecated
    ITeam getPlayerTeam(final String p0);

    void checkWinner();

    void addPlayerDeath(final Player p0);

    NextEvent getNextEvent();

    void setNextEvent(final NextEvent p0);

    void sendPreGameCommandItems(final Player p0);

    void sendSpectatorCommandItems(final Player p0);

    ITeam getTeam(final String p0);

    StartingTask getStartingTask();

    PlayingTask getPlayingTask();

    RestartingTask getRestartingTask();

    List<IGenerator> getOreGenerators();

    List<String> getNextEvents();

    int getPlayerDeaths(final Player p0, final boolean p1);

    void sendDiamondsUpgradeMessages();

    void sendEmeraldsUpgradeMessages();

    LinkedList<Vector> getPlaced();

    void destroyData();

    int getUpgradeDiamondsCount();

    int getUpgradeEmeraldsCount();

    List<Region> getRegionsList();

    ConcurrentHashMap<Player, Integer> getShowTime();

    boolean isAllowSpectate();

    void setAllowSpectate(final boolean p0);

    String getWorldName();

    void setWorldName(final String p0);

    int getRenderDistance();

    boolean startReSpawnSession(final Player p0, final int p1);

    boolean isReSpawning(final Player p0);

    Location getReSpawnLocation();

    Location getSpectatorLocation();

    Location getWaitingLocation();

    boolean isProtected(final Location p0);

    void abandonGame(final Player p0);

    int getYKillHeight();

    Instant getStartTime();

    ITeamAssigner getTeamAssigner();

    void setTeamAssigner(final ITeamAssigner p0);
}
