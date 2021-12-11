package dev.eugenio.nasgarbedwars;

import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.levels.Level;
import dev.eugenio.nasgarbedwars.commands.bedwars.MainCommand;
import dev.eugenio.nasgarbedwars.stats.StatsAPI;
import org.bukkit.entity.Player;

public class API implements BedWarsAPI {
    private final Configs configs;
    
    public API() {
        this.configs = () -> BedWars.getInstance().getMainConfig();
    }
    
    @Override
    public IStats getStatsUtil() {
        return StatsAPI.getInstance();
    }
    
    @Override
    public Configs getConfigs() {
        return this.configs;
    }
    
    @Override
    public Level getLevelsUtil() {
        return BedWars.getInstance().getLevels();
    }
    
    @Override
    public ParentCommand getBedWarsCommand() {
        return MainCommand.getInstance();
    }

    @Override
    public Language getDefaultLang() {
        return Language.getDefaultLanguage();
    }
    
    @Override
    public String getForCurrentVersion(final String s, final String s2, final String s3) {
        return BedWars.getInstance().getForCurrentVersion(s, s2, s3);
    }
    
    @Override
    public void setLevelAdapter(final Level levelAdapter) {
        BedWars.getInstance().setLevelAdapter(levelAdapter);
    }
    
    @Override
    public Language getLanguageByIso(final String s) {
        return Language.getLang(s);
    }
    
    @Override
    public Language getPlayerLanguage(final Player player) {
        return Language.getPlayerLanguage(player);
    }
    
    @Override
    public String getLangIso(final Player player) {
        return Language.getPlayerLanguage(player).getIso();
    }
}
