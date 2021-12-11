package dev.eugenio.nasgarbedwars.api;

import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.levels.Level;
import dev.eugenio.nasgarbedwars.api.configuration.ConfigManager;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.UUID;

public interface BedWarsAPI {
    IStats getStatsUtil();

    Configs getConfigs();

    Level getLevelsUtil();

    String getLangIso(final Player p0);

    ParentCommand getBedWarsCommand();

    Language getDefaultLang();

    String getForCurrentVersion(final String p0, final String p1, final String p2);

    void setLevelAdapter(final Level p0);

    Language getLanguageByIso(final String p0);

    Language getPlayerLanguage(final Player p0);

    interface Configs {
        ConfigManager getMainConfig();
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
