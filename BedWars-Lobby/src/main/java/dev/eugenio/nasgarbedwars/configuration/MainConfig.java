package dev.eugenio.nasgarbedwars.configuration;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.configuration.ConfigManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class MainConfig extends ConfigManager {
    public MainConfig(final Plugin plugin, final String s) {
        super(plugin, s, BedWars.getInstance().getDataFolder().getPath());
        final YamlConfiguration yml = this.getYml();
        yml.options().header("Archivo de configuración general de BedWars. Modo game. Hecho exclusivamente para ZoneCraft. Por ImMarvolo y NotEugenio_");
        yml.addDefault("language", "es");
        yml.addDefault("database.enable", true);
        yml.addDefault("database.host", "127.0.0.1");
        yml.addDefault("database.port", 3306);
        yml.addDefault("database.database", "bedwars");
        yml.addDefault("database.user", "zonecraft");
        yml.addDefault("database.pass", "sileesestososgei");
        yml.addDefault("database.ssl", false);
    }
}
