package dev.eugenio.nasgarbedwars.api;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.shop.IContentTier;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.configuration.ConfigManager;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.levels.Level;
import dev.eugenio.nasgarbedwars.api.server.ISetupSession;
import dev.eugenio.nasgarbedwars.api.server.NMSUtil;
import dev.eugenio.nasgarbedwars.api.server.RestoreAdapter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.UUID;

public interface BedWarsAPI {
    IStats getStatsUtil();

    AFKUtil getAFKUtil();

    ArenaUtil getArenaUtil();

    Configs getConfigs();

    ShopUtil getShopUtil();

    TeamUpgradesUtil getTeamUpgradesUtil();

    Level getLevelsUtil();

    ISetupSession getSetupSession(final UUID p0);

    boolean isInSetupSession(final UUID p0);

    String getLangIso(final Player p0);

    ParentCommand getBedWarsCommand();

    RestoreAdapter getRestoreAdapter();

    void setRestoreAdapter(final RestoreAdapter p0) throws IllegalAccessError;

    NMSUtil getVersionSupport();

    Language getDefaultLang();

    String getForCurrentVersion(final String p0, final String p1, final String p2);

    void setLevelAdapter(final Level p0);

    Language getLanguageByIso(final String p0);

    Language getPlayerLanguage(final Player p0);

    interface TeamUpgradesUtil {
        boolean isWatchingGUI(final Player p0);

        void setWatchingGUI(final Player p0);

        void removeWatchingUpgrades(final UUID p0);
    }

    interface ShopUtil {
        int calculateMoney(final Player p0, final Material p1);

        Material getCurrency(final String p0);

        ChatColor getCurrencyColor(final Material p0);

        String getCurrencyMsgPath(final IContentTier p0);

        String getRomanNumber(final int p0);

        void takeMoney(final Player p0, final Material p1, final int p2);
    }

    interface Configs {
        ConfigManager getMainConfig();

        ConfigManager getGeneratorsConfig();

        ConfigManager getShopConfig();

        ConfigManager getUpgradesConfig();
    }

    interface ArenaUtil {
        void addToEnableQueue(final IArena p0);

        void removeFromEnableQueue(final IArena p0);

        boolean isPlaying(final Player p0);

        boolean isSpectating(final Player p0);

        void loadArena(final String p0, final Player p1);

        int getGamesBeforeRestart();

        void setGamesBeforeRestart(final int p0);

        IArena getArenaByPlayer(final Player p0);

        void setArenaByPlayer(final Player p0, final IArena p1);

        void removeArenaByPlayer(final Player p0, final IArena p1);

        IArena getArenaByName(final String p0);

        IArena getArenaByIdentifier(final String p0);

        void setArenaByName(final IArena p0);

        void removeArenaByName(final String p0);

        LinkedList<IArena> getArenas();

        int getPlayers(final String p0);

        boolean joinRandomArena(final Player p0);

        boolean joinRandomFromGroup(final Player p0, final String p1);

        LinkedList<IArena> getEnableQueue();
    }

    interface AFKUtil {
        boolean isPlayerAFK(final Player p0);

        void setPlayerAFK(final Player p0, final boolean p1);

        int getPlayerTimeAFK(final Player p0);
    }

    interface IStats {
        Timestamp getPlayerFirstPlay(final UUID p0);

        Timestamp getPlayerLastPlay(final UUID p0);

        int getPlayerWins(final UUID p0);

        int getPlayerKills(final UUID p0);

        int getPlayerTotalKills(final UUID p0);

        int getPlayerFinalKills(final UUID p0);

        int getPlayerLoses(final UUID p0);

        int getPlayerDeaths(final UUID p0);

        int getPlayerFinalDeaths(final UUID p0);

        int getPlayerBedsDestroyed(final UUID p0);

        int getPlayerGamesPlayed(final UUID p0);
    }
}
