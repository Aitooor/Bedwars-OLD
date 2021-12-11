package dev.eugenio.nasgarbedwars.configuration;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.configuration.ConfigManager;
import dev.eugenio.nasgarbedwars.api.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainConfig extends ConfigManager {
    public MainConfig(final Plugin plugin, final String s) {
        super(plugin, s, BedWars.getInstance().getDataFolder().getPath());
        final YamlConfiguration yml = this.getYml();
        yml.options().header("Archivo de configuración general de BedWars. Modo game. Hecho para Nasgar de NotEugenio_");
        yml.addDefault("Redis.Address", "127.0.0.1");
        yml.addDefault("Redis.Port", 6379);
        yml.addDefault("Redis.Password", "tucontraseña");
        yml.addDefault("language", "es");
        yml.addDefault("lobbyServer", "l1");
        yml.addDefault("globalChat", false);
        yml.addDefault("formatChat", true);
        yml.addDefault("mark-leave-as-abandon", false);
        yml.addDefault("scoreboard-settings.sidebar.enable-game-sidebar", true);
        yml.addDefault("scoreboard-settings.sidebar.placeholders-refresh-interval", 20);
        yml.addDefault("scoreboard-settings.player-list.format-lobby-list", false);
        yml.addDefault("scoreboard-settings.player-list.format-waiting-list", false);
        yml.addDefault("scoreboard-settings.player-list.format-starting-list", false);
        yml.addDefault("scoreboard-settings.player-list.format-playing-list", true);
        yml.addDefault("scoreboard-settings.player-list.format-restarting-list", true);
        yml.addDefault("scoreboard-settings.player-list.names-refresh-interval", 1200);
        yml.addDefault("scoreboard-settings.health.display-in-tab", true);
        yml.addDefault("scoreboard-settings.health.animation-refresh-interval", 300);
        yml.addDefault("rejoin-time", 300);
        yml.addDefault("re-spawn-invulnerability", 4000);
        yml.addDefault("bungee-settings.games-before-restart", 15);
        yml.addDefault("bungee-settings.restart-cmd", "stop");
        yml.addDefault("countdowns.game-start-regular", 40);
        yml.addDefault("countdowns.game-start-half-arena", 25);
        yml.addDefault("countdowns.game-start-shortened", 5);
        yml.addDefault("countdowns.game-restart", 15);
        yml.addDefault("countdowns.player-re-spawn", 5);
        yml.addDefault("countdowns.next-event-beds-destroy", 360);
        yml.addDefault("countdowns.next-event-dragon-spawn", 600);
        yml.addDefault("countdowns.next-event-game-end", 120);
        yml.addDefault("shout-cmd-cooldown", 30);
        yml.addDefault("bungee-settings.server-id", "bw01");
        yml.addDefault("database.enable", true);
        yml.addDefault("database.host", "127.0.0.1");
        yml.addDefault("database.port", 3306);
        yml.addDefault("database.database", "bedwars");
        yml.addDefault("database.user", "nasgar");
        yml.addDefault("database.pass", "contraseñasupersegurasijaja123");
        yml.addDefault("database.ssl", false);
        yml.addDefault("inventories.disable-crafting-table", true);
        yml.addDefault("inventories.disable-enchanting-table", true);
        yml.addDefault("inventories.disable-furnace", true);
        yml.addDefault("inventories.disable-brewing-stand", true);
        yml.addDefault("inventories.disable-anvil", true);
        this.savePreGameCommandItem("leave", "bw leave", false, BedWars.getInstance().getForCurrentVersion("BED", "BED", "RED_BED"), 0, 8);
        this.saveSpectatorCommandItem("teleporter", "bw teleporter", false, BedWars.getInstance().getForCurrentVersion("COMPASS", "COMPASS", "COMPASS"), 3, 0);
        this.saveSpectatorCommandItem("leave", "bw leave", false, BedWars.getInstance().getForCurrentVersion("BED", "BED", "RED_BED"), 0, 8);
        yml.addDefault("start-items-per-group.Default", Collections.singletonList(BedWars.getInstance().getForCurrentVersion("WOOD_SWORD", "WOOD_SWORD", "WOODEN_SWORD")));
        yml.addDefault("allowed-commands", Arrays.asList("shout", "bw", "party", "p", "leave"));
        yml.options().copyDefaults(true);
        this.save();
        if (yml.get("bungee-settings.lobby-servers") != null) {
            yml.set("bungee-settings.lobby-sockets", new ArrayList<>(yml.getStringList("bungee-settings.lobby-servers")));
            yml.set("bungee-settings.lobby-servers", null);
        }
        if (this.getYml().get("disableCrafting") != null) this.set("inventories.disable-crafting-table", this.getString("disableCrafting"));
        if (yml.get("server-name") != null) this.set("bungee-settings.server-id", yml.get("server-name"));
        if (yml.get("game-scoreboard") != null) {
            this.set("scoreboard-settings.sidebar.enable-game-sidebar", yml.getBoolean("game-scoreboard"));
            this.set("game-scoreboard", null);
        }
        if (yml.get("enable-party-cmd") != null) {
            this.set("party-settings.enable-party-cmd", yml.getBoolean("enable-party-cmd"));
            this.set("enable-party-cmd", null);
        }
        this.set("server-name", null);
        this.set("statsGUI", null);
        this.set("startItems", null);
        this.set("generators", null);
        this.set("bedsDestroyCountdown", null);
        this.set("dragonSpawnCountdown", null);
        this.set("gameEndCountdown", null);
        this.set("npcLoc", null);
        this.set("blockedCmds", null);
        this.set("lobbyScoreboard", null);
        this.set("arenaGui.settings.startSlot", null);
        this.set("arenaGui.settings.endSlot", null);
        this.set("items", null);
        this.set("start-items-per-arena", null);
        this.set("safeMode", null);
        this.set("disableCrafting", null);
        this.set("performance-settings.disable-armor-packets", null);
        this.set("performance-settings.disable-respawn-packets", null);
        String replace = "es";
        final File[] listFiles = new File(plugin.getDataFolder(), "/Languages").listFiles();
        if (listFiles != null) {
            for (final File file : listFiles) {
                if (file.isFile() && file.getName().contains("messages_") && file.getName().contains(".yml")) {
                    final String replace2 = file.getName().replace("messages_", "").replace(".yml", "");
                    if (replace2.equalsIgnoreCase(yml.getString("language"))) replace = file.getName().replace("messages_", "").replace(".yml", "");
                    if (Language.getLang(replace2) == null) new Language(BedWars.getInstance(), replace2);
                }
            }
        }
        final Language lang = Language.getLang(replace);
        if (lang == null) throw new IllegalStateException("No se ha podido encontrar el lenguaje por defecto (¿te has leído el README de GitHub?): " + replace);
        Language.setDefaultLanguage(lang);
        new ConfigManager(plugin, "bukkit", Bukkit.getWorldContainer().getPath()).set("ticks-per.autosave", -1);
        Bukkit.spigot().getConfig().set("commands.send-namespaced", false);
        try {
            Bukkit.spigot().getConfig().save("spigot.yml");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void savePreGameCommandItem(final String s, final String s2, final boolean b, final String s3, final int n, final int n2) {
        if (this.isFirstTime()) {
            this.getYml().addDefault("pre-game-items.%path%.command".replace("%path%", s), s2);
            this.getYml().addDefault("pre-game-items.%path%.material".replace("%path%", s), s3);
            this.getYml().addDefault("pre-game-items.%path%.data".replace("%path%", s), n);
            this.getYml().addDefault("pre-game-items.%path%.enchanted".replace("%path%", s), b);
            this.getYml().addDefault("pre-game-items.%path%.slot".replace("%path%", s), n2);
            this.getYml().options().copyDefaults(true);
            this.save();
        }
    }
    
    public void saveSpectatorCommandItem(final String s, final String s2, final boolean b, final String s3, final int n, final int n2) {
        if (this.isFirstTime()) {
            this.getYml().addDefault("spectator-items.%path%.command".replace("%path%", s), s2);
            this.getYml().addDefault("spectator-items.%path%.material".replace("%path%", s), s3);
            this.getYml().addDefault("spectator-items.%path%.data".replace("%path%", s), n);
            this.getYml().addDefault("spectator-items.%path%.enchanted".replace("%path%", s), b);
            this.getYml().addDefault("spectator-items.%path%.slot".replace("%path%", s), n2);
            this.getYml().options().copyDefaults(true);
            this.save();
        }
    }
}
