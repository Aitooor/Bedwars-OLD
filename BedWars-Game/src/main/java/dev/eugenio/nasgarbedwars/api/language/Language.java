package dev.eugenio.nasgarbedwars.api.language;

import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.configuration.ConfigManager;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerLangChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class Language extends ConfigManager {
    private static final HashMap<UUID, Language> langByPlayer = new HashMap<>();
    private static final List<Language> languages = new ArrayList<>();
    private static Language defaultLanguage;
    private final String iso;
    private final String prefix;

    public Language(final Plugin plugin, final String iso) {
        super(plugin, "messages_" + iso, plugin.getDataFolder().getPath() + "/Languages");
        this.prefix = "";
        this.iso = iso;
        Language.languages.add(this);
    }

    public static List<String> getScoreboard(final Player player, final String s, final String s2) {
        final Language playerLanguage = getPlayerLanguage(player);
        if (playerLanguage.exists(s)) return playerLanguage.l(s);
        if (s.split("\\.").length == 3) {
            final String[] split = s.split("\\.");
            final String s3 = split[1];
            if (playerLanguage.exists(split[0] + "." + (String.valueOf(s3.charAt(0)).toUpperCase() + s3.substring(1).toLowerCase()) + "." + split[2]))
                return playerLanguage.l(s);
            if (playerLanguage.exists(split[0] + "." + split[1].toUpperCase() + "." + split[2]))
                return playerLanguage.l(split[0] + "." + split[1].toUpperCase() + "." + split[2]);
        }
        return playerLanguage.l(s2);
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

    public static List<String> getList(final Player player, final String s) {
        return Language.langByPlayer.getOrDefault(player.getUniqueId(), getDefaultLanguage()).l(s);
    }

    public static void saveIfNotExists(final String s, final Object o) {
        for (final Language language : Language.languages) {
            if (language.getYml().get(s) == null) {
                language.set(s, o);
            }
        }
    }

    public static HashMap<UUID, Language> getLangByPlayer() {
        return Language.langByPlayer;
    }

    public static boolean isLanguageExist(final String s) {
        for (Language language : Language.languages) {
            if (language.iso.equalsIgnoreCase(s)) return true;
        }
        return false;
    }

    public static Language getLang(final String s) {
        for (final Language language : Language.languages) {
            if (language.iso.equalsIgnoreCase(s)) {
                return language;
            }
        }
        return getDefaultLanguage();
    }

    public static List<Language> getLanguages() {
        return Language.languages;
    }

    public static void setupCustomStatsMessages() {
        final BedWarsAPI bedWarsAPI = Bukkit.getServer().getServicesManager().getRegistration(BedWarsAPI.class).getProvider();
        for (final Language language : getLanguages()) {
            if (language == null) continue;
            if (language.getYml() == null) continue;
            if (bedWarsAPI.getConfigs().getMainConfig().getYml().get("ConfigPath.GENERAL_CONFIGURATION_STATS_PATH") == null)
                return;
            for (final String s : bedWarsAPI.getConfigs().getMainConfig().getYml().getConfigurationSection("stats-gui").getKeys(false)) {
                if ("stats-gui.inv-size".contains(s)) continue;
                if (language.getYml().getDefaults() == null || !language.getYml().getDefaults().contains(Messages.PLAYER_STATS_GUI_PATH + "-" + s + "-name"))
                    language.getYml().addDefault(Messages.PLAYER_STATS_GUI_PATH + "-" + s + "-name", "Name not set");
                if (language.getYml().getDefaults() != null && language.getYml().getDefaults().contains(Messages.PLAYER_STATS_GUI_PATH + "-" + s + "-lore"))
                    continue;
                language.getYml().addDefault(Messages.PLAYER_STATS_GUI_PATH + "-" + s + "-lore", Collections.singletonList("lore not set"));
            }
            language.save();
        }
    }

    public static void addDefaultMessagesCommandItems(final Language language) {
        if (language == null) return;
        final YamlConfiguration yml = language.getYml();
        if (yml == null) return;
        final BedWarsAPI bedWarsAPI = Bukkit.getServer().getServicesManager().getRegistration(BedWarsAPI.class).getProvider();
        if (bedWarsAPI.getConfigs().getMainConfig().getYml().get("lobby-items") != null) {
            for (final String s : bedWarsAPI.getConfigs().getMainConfig().getYml().getConfigurationSection("lobby-items").getKeys(false)) {
                if (s.isEmpty()) {
                    continue;
                }
                final String replace = "lobby-items-%path%-name".replace("%path%", s);
                final String replace2 = "lobby-items-%path%-lore".replace("%path%", s);
                if (yml.getDefaults() == null || !yml.getDefaults().contains(replace))
                    yml.addDefault(replace, "&cName not set at: &f" + replace);
                if (yml.getDefaults() != null && yml.getDefaults().contains(replace)) continue;
                yml.addDefault(replace2, Arrays.asList("&cLore not set at:", " &f" + replace2));
            }
        }
        if (bedWarsAPI.getConfigs().getMainConfig().getYml().get("spectator-items") != null) {
            for (final String s2 : bedWarsAPI.getConfigs().getMainConfig().getYml().getConfigurationSection("spectator-items").getKeys(false)) {
                if (s2.isEmpty()) continue;
                final String replace3 = "spectator-items-%path%-name".replace("%path%", s2);
                final String replace4 = "spectator-items-%path%-lore".replace("%path%", s2);
                if (yml.getDefaults() == null || !yml.getDefaults().contains(replace3))
                    yml.addDefault(replace3, "&cName not set at: &f" + replace3);
                if (yml.getDefaults() != null && yml.getDefaults().contains(replace3)) continue;
                yml.addDefault(replace4, Arrays.asList("&cLore not set at:", " &f" + replace4));
            }
        }
        if (bedWarsAPI.getConfigs().getMainConfig().getYml().get("pre-game-items") != null) {
            for (final String s3 : bedWarsAPI.getConfigs().getMainConfig().getYml().getConfigurationSection("pre-game-items").getKeys(false)) {
                if (s3.isEmpty()) continue;
                final String replace5 = "pre-game-items-%path%-name".replace("%path%", s3);
                final String replace6 = "pre-game-items-%path%-lore".replace("%path%", s3);
                if (yml.getDefaults() == null || !yml.getDefaults().contains(replace5))
                    yml.addDefault(replace5, "&cName not set at: &f" + replace5);
                if (yml.getDefaults() != null && yml.getDefaults().contains(replace5)) continue;
                yml.addDefault(replace6, Arrays.asList("&cLore not set at:", " &f" + replace6));
            }
        }
        yml.options().copyDefaults(true);
        language.save();
    }

    public static void addCategoryMessages(final YamlConfiguration yamlConfiguration, final String s, final String s2, final String s3, final List<String> list) {
        if (yamlConfiguration.getDefaults() == null || !yamlConfiguration.getDefaults().contains("shop-items-messages.%category%.inventory-name".replace("%category%", s)))
            yamlConfiguration.addDefault("shop-items-messages.%category%.inventory-name".replace("%category%", s), s2);
        if (yamlConfiguration.getDefaults() == null || !yamlConfiguration.getDefaults().contains("shop-items-messages.%category%.category-item-name".replace("%category%", s)))
            yamlConfiguration.addDefault("shop-items-messages.%category%.category-item-name".replace("%category%", s), s3);
        if (yamlConfiguration.getDefaults() == null || !yamlConfiguration.getDefaults().contains("shop-items-messages.%category%.category-item-lore".replace("%category%", s)))
            yamlConfiguration.addDefault("shop-items-messages.%category%.category-item-lore".replace("%category%", s), list);
    }

    public static void addContentMessages(final YamlConfiguration yamlConfiguration, final String s, final String s2, final String s3, final List<String> list) {
        final String replace = "shop-items-messages.%category%.content-item-%content%-name".replace("%category%", s2).replace("%content%", s);
        final String replace2 = "shop-items-messages.%category%.content-item-%content%-lore".replace("%category%", s2).replace("%content%", s);
        if (yamlConfiguration.getDefaults() == null || !yamlConfiguration.getDefaults().contains(replace)) {
            yamlConfiguration.addDefault(replace, s3);
        }
        if (yamlConfiguration.getDefaults() == null || !yamlConfiguration.getDefaults().contains(replace2)) {
            yamlConfiguration.addDefault(replace2, list);
        }
    }

    public static boolean setPlayerLanguage(final UUID uuid, final String s) {
        if (s == null) {
            if (Language.langByPlayer.containsKey(uuid)) {
                final Player player = Bukkit.getPlayer(uuid);
                if (player != null && player.isOnline()) {
                    final PlayerLangChangeEvent playerLangChangeEvent = new PlayerLangChangeEvent(player, Language.langByPlayer.get(uuid).iso, getDefaultLanguage().iso);
                    Bukkit.getPluginManager().callEvent(playerLangChangeEvent);
                    if (playerLangChangeEvent.isCancelled()) {
                        return false;
                    }
                }
            }
            Language.langByPlayer.remove(uuid);
            return true;
        }
        final Language lang = getLang(s);
        if (lang == null) {
            return false;
        }
        final Language playerLanguage = getPlayerLanguage(uuid);
        if (playerLanguage.getIso().equals(lang.getIso())) {
            return false;
        }
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

    public static String[] getCountDownTitle(final Language language, final int n) {
        final String[] array = {ChatColor.translateAlternateColorCodes('&', language.getYml().get(Messages.ARENA_STATUS_START_COUNTDOWN_TITLE + "-" + n, language.getString(Messages.ARENA_STATUS_START_COUNTDOWN_TITLE)).toString().replace("{second}", String.valueOf(n))), null};
        if (array[0].isEmpty()) {
            array[0] = " ";
        }
        array[1] = ChatColor.translateAlternateColorCodes('&', language.getYml().get(Messages.ARENA_STATUS_START_COUNTDOWN_SUB_TITLE + "-" + n, language.getString(Messages.ARENA_STATUS_START_COUNTDOWN_SUB_TITLE)).toString().replace("{second}", String.valueOf(n)));
        if (array[1].isEmpty()) {
            array[1] = " ";
        }
        return array;
    }

    public static Language getDefaultLanguage() {
        return Language.defaultLanguage;
    }

    public static void setDefaultLanguage(final Language defaultLanguage) {
        Language.defaultLanguage = defaultLanguage;
    }

    public String getLangName() {
        return this.getYml().getString("name");
    }

    public boolean exists(final String s) {
        return this.getYml().get(s) != null;
    }

    public String m(final String s) {
        String string = this.getYml().getString(s);
        if (string == null) {
            System.err.println("Mensaje no encontrado " + s + " en language " + this.getIso());
            string = "MISSING_LANG";
        }
        return ChatColor.translateAlternateColorCodes('&', string.replace("{prefix}", this.prefix));
    }

    public List<String> l(final String s) {
        final ArrayList<String> list = new ArrayList<>();
        List<String> list2 = this.getYml().getStringList(s);
        if (list2 == null) {
            System.err.println("Lista de mensajes no encontrados " + s + " en language " + this.getIso());
            list2 = Collections.emptyList();
        }
        for (String value : list2) list.add(ChatColor.translateAlternateColorCodes('&', value));
        return list;
    }

    public String getIso() {
        return this.iso;
    }

    public void addDefaultStatsMsg(final YamlConfiguration yamlConfiguration, final String s, final String s2, final String... array) {
        if (yamlConfiguration.getDefaults() == null || !yamlConfiguration.getDefaults().contains(Messages.PLAYER_STATS_GUI_PATH + "-" + s + "-name"))
            yamlConfiguration.addDefault(Messages.PLAYER_STATS_GUI_PATH + "-" + s + "-name", s2);
        if (yamlConfiguration.getDefaults() == null || !yamlConfiguration.getDefaults().contains(Messages.PLAYER_STATS_GUI_PATH + "-" + s + "-lore"))
            yamlConfiguration.addDefault(Messages.PLAYER_STATS_GUI_PATH + "-" + s + "-lore", array);
    }

    public void setupUnSetCategories() {
        final BedWarsAPI bedWarsAPI = Bukkit.getServer().getServicesManager().getRegistration(BedWarsAPI.class).getProvider();
        for (final String s : bedWarsAPI.getConfigs().getShopConfig().getYml().getConfigurationSection("").getKeys(false)) {
            if (s.equalsIgnoreCase("shop-settings")) continue;
            if (s.equalsIgnoreCase("shop-specials")) continue;
            if (s.equals("quick-buy-defaults")) continue;
            if (!this.exists("shop-items-messages.%category%.inventory-name".replace("%category%", s)))
                this.set("shop-items-messages.%category%.inventory-name".replace("%category%", s), "&8Nombre no especificado");
            if (!this.exists("shop-items-messages.%category%.category-item-name".replace("%category%", s)))
                this.set("shop-items-messages.%category%.category-item-name".replace("%category%", s), "&8Nombre no especificado");
            if (!this.exists("shop-items-messages.%category%.category-item-lore".replace("%category%", s)))
                this.set("shop-items-messages.%category%.category-item-lore".replace("%category%", s), Collections.singletonList("&8Lore no especificado"));
            if (bedWarsAPI.getConfigs().getShopConfig().getYml().get(s + ".category-content") == null) continue;
            for (String s2 : bedWarsAPI.getConfigs().getShopConfig().getYml().getConfigurationSection(s + ".category-content").getKeys(false)) {
                if (!exists("shop-items-messages.%category%.content-item-%content%-name".replace("%category%", s).replace("%content%", s2)))
                    set("shop-items-messages.%category%.content-item-%content%-name".replace("%category%", s).replace("%content%", s2), "&8Nombre no especificado");
                if (!exists("shop-items-messages.%category%.content-item-%content%-lore".replace("%category%", s).replace("%content%", s2)))
                    set("shop-items-messages.%category%.content-item-%content%-lore".replace("%category%", s).replace("%content%", s2), Collections.singletonList("&8Lore no especificado"));
            }
        }
    }
}
