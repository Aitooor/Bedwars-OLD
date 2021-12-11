package dev.eugenio.nasgarbedwars.configuration;

import dev.eugenio.nasgarbedwars.api.configuration.ConfigManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class ArenaConfig extends ConfigManager {
    public ArenaConfig(final Plugin plugin, final String s, final String s2) {
        super(plugin, s, s2);
        final YamlConfiguration yml = this.getYml();
        yml.options().header("Archivo de configuración de arenas de BedWars.");
        yml.addDefault("group", "Default");
        yml.addDefault("display-name", "");
        yml.addDefault("minPlayers", 4);
        yml.addDefault("maxInTeam", 1);
        yml.addDefault("allowSpectate", true);
        yml.addDefault("spawn-protection", 6);
        yml.addDefault("shop-protection", 8);
        yml.addDefault("upgrades-protection", 8);
        yml.addDefault("island-radius", 17);
        yml.addDefault("worldBorder", 300);
        yml.addDefault("y-kill-height", -40);
        yml.addDefault("max-build-y", 180);
        yml.addDefault("disable-generator-for-empty-teams", false);
        yml.addDefault("disable-npcs-for-empty-teams", false);
        yml.addDefault("vanilla-death-drops", false);
        yml.addDefault("allow-map-break", false);
        yml.addDefault("enable-gen-split", true);
        final ArrayList<String> list = new ArrayList<>();
        list.add("doDaylightCycle:false");
        list.add("announceAdvancements:false");
        list.add("doInsomnia:false");
        list.add("doImmediateRespawn:true");
        list.add("doWeatherCycle:false");
        yml.addDefault("game-rules", list);
        yml.options().copyDefaults(true);
        this.save();
        if (yml.get("spawnProtection") != null) {
            this.set("spawn-protection", yml.getInt("spawnProtection"));
            this.set("spawnProtection", null);
        }
        if (yml.get("shopProtection") != null) {
            this.set("shop-protection", yml.getInt("shopProtection"));
            this.set("shopProtection", null);
        }
        if (yml.get("upgradesProtection") != null) {
            this.set("upgrades-protection", yml.getInt("upgradesProtection"));
            this.set("upgradesProtection", null);
        }
        if (yml.get("islandRadius") != null) this.set("island-radius", yml.getInt("islandRadius"));
        if (yml.get("voidKill") != null) this.set("voidKill", null);
    }
}
