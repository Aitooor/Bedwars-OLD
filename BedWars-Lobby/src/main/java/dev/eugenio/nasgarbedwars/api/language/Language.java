package dev.eugenio.nasgarbedwars.api.language;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.configuration.ConfigManager;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerLangChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class Language extends ConfigManager {
    private final String iso;
    private final String prefix;
    private static final HashMap<UUID, Language> langByPlayer;
    private static final List<Language> languages;
    private static Language defaultLanguage;

    public Language(final Plugin plugin, final String iso) {
        super(plugin, "messages_" + iso, plugin.getDataFolder().getPath() + "/Languages");
        this.prefix = "";
        this.iso = iso;
        Language.languages.add(this);
    }

    public String getLangName() {
        return this.getYml().getString("name");
    }

    public static String getMsg(final Player player, final String s) {
        if (player == null) return getDefaultLanguage().m(s);
        return Language.langByPlayer.getOrDefault(player.getUniqueId(), getDefaultLanguage()).m(s);
    }

    public static Language getPlayerLanguage(final Player player) {
        return Language.langByPlayer.getOrDefault(player.getUniqueId(), getDefaultLanguage());
    }

    public static Language getPlayerLanguage(final UUID uuid) {
        return Language.langByPlayer.getOrDefault(uuid, getDefaultLanguage());
    }

    public String m(final String s) {
        String string = this.getYml().getString(s);
        if (string == null) {
            System.err.println("Key de mensaje faltante " + s + " en language " + this.getIso());
            string = "MISSING_LANG";
        }
        return ChatColor.translateAlternateColorCodes('&', string.replace("{prefix}", this.prefix));
    }

    public static boolean isLanguageExist(final String s) {
        for (Language language : Language.languages) if (language.iso.equalsIgnoreCase(s)) return true;
        return false;
    }

    public static Language getLang(final String s) {
        for (final Language language : Language.languages) if (language.iso.equalsIgnoreCase(s)) return language;
        return getDefaultLanguage();
    }

    public String getIso() {
        return this.iso;
    }

    public static List<Language> getLanguages() {
        return Language.languages;
    }

    public static void setupCustomStatsMessages() {
        for (final Language language : getLanguages()) {
            if (language == null) continue;
            if (language.getYml() == null) continue;
            if (BedWars.getInstance().getMainConfig().getYml().get("ConfigPath.GENERAL_CONFIGURATION_STATS_PATH") == null) return;
            for (final String s : BedWars.getInstance().getMainConfig().getYml().getConfigurationSection("stats-gui").getKeys(false)) {
                if ("stats-gui.inv-size".contains(s)) continue;
                if (language.getYml().getDefaults() == null || !language.getYml().getDefaults().contains(Messages.PLAYER_STATS_GUI_PATH + "-" + s + "-name")) language.getYml().addDefault(Messages.PLAYER_STATS_GUI_PATH + "-" + s + "-name", "Name not set");
                if (language.getYml().getDefaults() != null && language.getYml().getDefaults().contains(Messages.PLAYER_STATS_GUI_PATH + "-" + s + "-lore")) continue;
                language.getYml().addDefault(Messages.PLAYER_STATS_GUI_PATH + "-" + s + "-lore", Collections.singletonList("lore not set"));
            }
            language.save();
        }
    }

    public void addDefaultStatsMsg(final YamlConfiguration yamlConfiguration, final String s, final String s2, final String... array) {
        if (yamlConfiguration.getDefaults() == null || !yamlConfiguration.getDefaults().contains(Messages.PLAYER_STATS_GUI_PATH + "-" + s + "-name")) {
            yamlConfiguration.addDefault(Messages.PLAYER_STATS_GUI_PATH + "-" + s + "-name", s2);
        }
        if (yamlConfiguration.getDefaults() == null || !yamlConfiguration.getDefaults().contains(Messages.PLAYER_STATS_GUI_PATH + "-" + s + "-lore")) {
            yamlConfiguration.addDefault(Messages.PLAYER_STATS_GUI_PATH + "-" + s + "-lore", array);
        }
    }

    public static void addCategoryMessages(final YamlConfiguration yamlConfiguration, final String s, final String s2, final String s3, final List<String> list) {
        if (yamlConfiguration.getDefaults() == null || !yamlConfiguration.getDefaults().contains("shop-items-messages.%category%.inventory-name".replace("%category%", s))) yamlConfiguration.addDefault("shop-items-messages.%category%.inventory-name".replace("%category%", s), s2);
        if (yamlConfiguration.getDefaults() == null || !yamlConfiguration.getDefaults().contains("shop-items-messages.%category%.category-item-name".replace("%category%", s))) yamlConfiguration.addDefault("shop-items-messages.%category%.category-item-name".replace("%category%", s), s3);
        if (yamlConfiguration.getDefaults() == null || !yamlConfiguration.getDefaults().contains("shop-items-messages.%category%.category-item-lore".replace("%category%", s))) yamlConfiguration.addDefault("shop-items-messages.%category%.category-item-lore".replace("%category%", s), list);
    }

    public static void addContentMessages(final YamlConfiguration yamlConfiguration, final String s, final String s2, final String s3, final List<String> list) {
        final String replace = "shop-items-messages.%category%.content-item-%content%-name".replace("%category%", s2).replace("%content%", s);
        final String replace2 = "shop-items-messages.%category%.content-item-%content%-lore".replace("%category%", s2).replace("%content%", s);
        if (yamlConfiguration.getDefaults() == null || !yamlConfiguration.getDefaults().contains(replace)) yamlConfiguration.addDefault(replace, s3);
        if (yamlConfiguration.getDefaults() == null || !yamlConfiguration.getDefaults().contains(replace2)) yamlConfiguration.addDefault(replace2, list);
    }

    public static boolean setPlayerLanguage(final UUID uuid, final String s) {
        if (s == null) {
            if (Language.langByPlayer.containsKey(uuid)) {
                final Player player = Bukkit.getPlayer(uuid);
                if (player != null && player.isOnline()) {
                    final PlayerLangChangeEvent playerLangChangeEvent = new PlayerLangChangeEvent(player, Language.langByPlayer.get(uuid).iso, getDefaultLanguage().iso);
                    Bukkit.getPluginManager().callEvent(playerLangChangeEvent);
                    if (playerLangChangeEvent.isCancelled()) return false;
                }
            }
            Language.langByPlayer.remove(uuid);
            return true;
        }
        final Language lang = getLang(s);
        if (lang == null) return false;
        final Language playerLanguage = getPlayerLanguage(uuid);
        if (playerLanguage.getIso().equals(lang.getIso())) return false;
        final Player player2 = Bukkit.getPlayer(uuid);
        if (player2 != null && player2.isOnline()) {
            final PlayerLangChangeEvent playerLangChangeEvent2 = new PlayerLangChangeEvent(player2, playerLanguage.getIso(), lang.getIso());
            Bukkit.getPluginManager().callEvent(playerLangChangeEvent2);
            if (playerLangChangeEvent2.isCancelled()) {
                return false;
            }
        }
        if (getDefaultLanguage().getIso().equals(lang.getIso())) {
            Language.langByPlayer.remove(uuid);
            return true;
        }
        if (Language.langByPlayer.containsKey(uuid)) {
            Language.langByPlayer.replace(uuid, lang);
        } else {
            Language.langByPlayer.put(uuid, lang);
        }
        return true;
    }

    public static void setDefaultLanguage(final Language defaultLanguage) {
        Language.defaultLanguage = defaultLanguage;
    }

    public static Language getDefaultLanguage() {
        return Language.defaultLanguage;
    }

    static {
        langByPlayer = new HashMap<>();
        languages = new ArrayList<>();
    }
}
