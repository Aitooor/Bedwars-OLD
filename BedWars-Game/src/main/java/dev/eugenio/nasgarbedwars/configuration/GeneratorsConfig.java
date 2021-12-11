package dev.eugenio.nasgarbedwars.configuration;

import dev.eugenio.nasgarbedwars.api.configuration.ConfigManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class GeneratorsConfig extends ConfigManager {
    public GeneratorsConfig(final Plugin plugin, final String s, final String s2) {
        super(plugin, s, s2);
        if (this.isFirstTime()) {
            final YamlConfiguration yml = this.getYml();
            yml.options().header("Archivo de configuración de hornos/generadores de BedWars.");
            yml.addDefault("Default.iron.delay", 1);
            yml.addDefault("Default.iron.amount", 1);
            yml.addDefault("Default.gold.delay", 10.5);
            yml.addDefault("Default.gold.amount", 1);
            yml.addDefault("Default.iron.spawn-limit", 32);
            yml.addDefault("Default.gold.spawn-limit", 7);
            yml.addDefault("stack-items", false);
            yml.addDefault("Default.diamond.tierI.delay", 30);
            yml.addDefault("Default.diamond.tierI.spawn-limit", 4);
            yml.addDefault("Default.diamond.tierII.delay", 20);
            yml.addDefault("Default.diamond.tierII.spawn-limit", 4);
            yml.addDefault("Default.diamond.tierII.start", 360);
            yml.addDefault("Default.diamond.tierIII.delay", 15);
            yml.addDefault("Default.diamond.tierIII.spawn-limit", 4);
            yml.addDefault("Default.diamond.tierIII.start", 1080);
            yml.addDefault("Default.emerald.tierI.delay", 70);
            yml.addDefault("Default.emerald.tierI.spawn-limit", 2);
            yml.addDefault("Default.emerald.tierII.delay", 50);
            yml.addDefault("Default.emerald.tierII.spawn-limit", 2);
            yml.addDefault("Default.emerald.tierII.start", 720);
            yml.addDefault("Default.emerald.tierIII.delay", 30);
            yml.addDefault("Default.emerald.tierIII.spawn-limit", 2);
            yml.addDefault("Default.emerald.tierIII.start", 1440);
            yml.options().copyDefaults(true);
            this.save();
        }
    }
}
