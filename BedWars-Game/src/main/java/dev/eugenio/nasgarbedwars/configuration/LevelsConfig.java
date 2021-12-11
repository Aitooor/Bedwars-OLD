package dev.eugenio.nasgarbedwars.configuration;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.configuration.ConfigManager;

public class LevelsConfig extends ConfigManager {
    public static LevelsConfig levels;
    
    private LevelsConfig() {
        super(BedWars.getInstance(), "levels", BedWars.getInstance().getDataFolder().toString());
    }
    
    public static void init() {
        LevelsConfig.levels = new LevelsConfig();
        LevelsConfig.levels.getYml().options().copyDefaults(true);
        if (LevelsConfig.levels.isFirstTime()) {
            LevelsConfig.levels.getYml().options().header("Archivo de configuración de niveles de BedWars.");
            LevelsConfig.levels.getYml().addDefault("levels.1.name", "&7[{number}\u2729] ");
            LevelsConfig.levels.getYml().addDefault("levels.1.rankup-cost", 500);
            LevelsConfig.levels.getYml().addDefault("levels.2.name", "&7[{number}\u2729] ");
            LevelsConfig.levels.getYml().addDefault("levels.2.rankup-cost", 1000);
            LevelsConfig.levels.getYml().addDefault("levels.3.name", "&7[{number}\u2729] ");
            LevelsConfig.levels.getYml().addDefault("levels.3.rankup-cost", 2000);
            LevelsConfig.levels.getYml().addDefault("levels.4.name", "&7[{number}\u2729] ");
            LevelsConfig.levels.getYml().addDefault("levels.4.rankup-cost", 3500);
            LevelsConfig.levels.getYml().addDefault("levels.5-10.name", "&e[{number}\u2729] ");
            LevelsConfig.levels.getYml().addDefault("levels.5-10.rankup-cost", 5000);
            LevelsConfig.levels.getYml().addDefault("levels.others.name", "&7[{number}\u2729] ");
            LevelsConfig.levels.getYml().addDefault("levels.others.rankup-cost", 5000);
        }
        LevelsConfig.levels.getYml().addDefault("xp-rewards.per-minute", 25);
        LevelsConfig.levels.getYml().addDefault("xp-rewards.per-teammate", 5);
        LevelsConfig.levels.getYml().addDefault("xp-rewards.game-win", 100);
        LevelsConfig.levels.getYml().addDefault("progress-bar.symbol", "\u25a0");
        LevelsConfig.levels.getYml().addDefault("progress-bar.unlocked-color", "&b");
        LevelsConfig.levels.getYml().addDefault("progress-bar.locked-color", "&7");
        LevelsConfig.levels.getYml().addDefault("progress-bar.format", "&8 [{progress}&8]");
        LevelsConfig.levels.save();
    }

    public static String getLevelName(final int n) {
        final String string = LevelsConfig.levels.getYml().getString("levels." + n + ".name");
        if (string != null) return string;
        for (final String s : LevelsConfig.levels.getYml().getConfigurationSection("levels").getKeys(false)) {
            if (s.contains("-")) {
                final String[] split = s.split("-");
                if (split.length != 2) continue;
                int int1;
                int int2;
                try {
                    int1 = Integer.parseInt(split[0]);
                    int2 = Integer.parseInt(split[1]);
                } catch (Exception ex) {
                    continue;
                }
                if (int1 <= n && n <= int2) return LevelsConfig.levels.getYml().getString("levels." + s + ".name");
            }
        }
        return LevelsConfig.levels.getYml().getString("levels.others.name");
    }
    
    public static int getNextCost(final int n) {
        if (LevelsConfig.levels.getYml().get("levels." + n + ".rankup-cost") != null) return LevelsConfig.levels.getYml().getInt("levels." + n + ".rankup-cost");
        for (final String s : LevelsConfig.levels.getYml().getConfigurationSection("levels").getKeys(false)) {
            if (s.contains("-")) {
                final String[] split = s.split("-");
                if (split.length != 2) continue;
                int int1;
                int int2;
                try {
                    int1 = Integer.parseInt(split[0]);
                    int2 = Integer.parseInt(split[1]);
                } catch (Exception ex) {
                    continue;
                }
                if (int1 <= n && n <= int2) return LevelsConfig.levels.getYml().getInt("levels." + s + ".rankup-cost");
            }
        }
        return LevelsConfig.levels.getYml().getInt("levels.others.rankup-cost");
    }
}
