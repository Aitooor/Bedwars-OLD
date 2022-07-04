package dev.eugenio.nasgarbedwars.configuration;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.configuration.ConfigManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class MainConfig extends ConfigManager {
    public MainConfig(final Plugin plugin, final String s) {
        super(plugin, s, BedWars.getInstance().getDataFolder().getPath());
        final YamlConfiguration yml = this.getYml();
        yml.options().header("Archivo de configuración de BedWars Lobby.");
        yml.addDefault("language", "en");
        yml.addDefault("database.enable", true);
        yml.addDefault("database.host", "127.0.0.1");
        yml.addDefault("database.port", 3306);
        yml.addDefault("database.database", "Bedwars");
        yml.addDefault("database.user", "root");
        yml.addDefault("database.pass", "pass");
        yml.addDefault("database.ssl", false);
    }
}
