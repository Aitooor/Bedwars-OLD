package dev.eugenio.nasgarbedwars.arena.compass.data;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.language.Language;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MessagesData {
    public final static String PATH = BedWars.getInstance().getDataFolder().getPath();

    public MessagesData() {
        setupMessages();
    }

    public void setupMessages() {
        for (Language l : Language.getLanguages()) {
            YamlConfiguration yml = l.getYml();

                yml.addDefault(NOT_ALL_BEDS_DESTROYED, "&cNot all enemy beds are destroyed yet!");
                yml.addDefault(PURCHASED, "&cYou will lose the ability to track this team when you die!");
                yml.addDefault(NOT_ENOUGH_RESOURCE, "&cYou don't have enough resources!");
                yml.addDefault(ALREADY_TRACKING, "&cYou are already tracking this team!");
                yml.addDefault(STATUS_NOT_ENOUGH, "&cYou don't have enough resource!");
                yml.addDefault(STATUS_LOCKED, "&cUnlocks when all enemy beds are destroyed!");
                yml.addDefault(STATUS_UNLOCKED, "&eClick to purchase!");
                yml.addDefault(ACTION_BAR_TRACKING, "&fTracking: {teamColor}&l{target} &f- Distance: &a&l{distance}m");
                yml.addDefault(TEAM_MESSAGE_FORMAT, "&a&lTEAM > &7{player}: {message}");

                saveItem(yml, ConfigData.COMPASS_ITEM, "&aCompass &7(Right Click)");
                yml.addDefault(MAIN_MENU_TITLE, "&8Tracker & Communications");
                saveItem(yml, ConfigData.MAIN_MENU_TRACKER, "&aTracker Shop", "&7Purchase tracking upgrade", "&7for your compass which will", "&7track each player on a", "&7specific team until you", "&7die.", "", "&eClick to open!");
                saveItem(yml, ConfigData.MAIN_MENU_TRACKER_TEAM, "&aTracker Shop", "&7Purchase tracking upgrade", "&7for your compass which will", "&7track each player on a", "&7specific team until you", "&7die.", "", "&eClick to open!");
                saveItem(yml, ConfigData.MAIN_MENU_COMMUNICATIONS, "&aQuick Communications", "&7Send highlighted chat", "&7messages to your teammates!", "", "&eClick to open!");
                yml.addDefault(TRACKER_MENU_TITLE, "&8Purchase Enemy Tracker");
                saveItem(yml, ConfigData.TRACKER_MENU_TEAM_ITEM, "&cTrack Team {team}", "&7Purchase tracking upgrade", "&7for your compass which will", "&7track each player on a", "&7specific team until you", "&7die.", "", "&7Cost: &22 Emeralds", "", "{status}");
                saveItem(yml, ConfigData.TRACKER_MENU_BACK_ITEM, "&aGo Back", "&7To Tracker & Communication");
                yml.addDefault(COMMUNICATIONS_MENU_TITLE, "&8Quick Communications");
                saveItem(yml, ConfigData.COMMUNICATIONS_MENU_BACK, "&aGo Back", "&7To Tracker & Communication");

                if (yml.getString(PATH + ConfigData.COMMUNICATIONS_MENU_ITEMS) == null) {
                    saveCommunicationItem(yml, "1", "&aHello ( ﾟ◡ﾟ)/!", "&aHello ( ﾟ◡ﾟ)/!", "", "&eClick to send!");
                    saveCommunicationItem(yml, "2", "&aI'm coming back to base!", "&aI'm coming back to base!", "", "&eClick to send!");
                    saveCommunicationItem(yml, "3", "&aI'm defending!", "&aI'm defending!", "", "&eClick to send!");
                    saveCommunicationItem(yml, "4", "&aI''m attacking {team}", "&aI'm attacking!", "&7You will be able to select", "&7the Team", "", "&eClick to send!");
                    saveCommunicationItem(yml, "5", "&aI'm collecting {resource}", "&aI'm collecting resources!", "&7You will be able to select", "&7the Resource", "", "&eClick to send!");
                    saveCommunicationItem(yml, "6", "&aI have {resource}", "&aI have resources!", "&7You will be able to select", "&7the Resource", "", "&eClick to send!");
                    saveCommunicationItem(yml, "7", "&aThank You", "&aThank You!", "", "&eClick to send!");
                    saveCommunicationItem(yml, "8", "&aGet back to base", "&aGet back to base!", "", "&eClick to send!");
                    saveCommunicationItem(yml, "9", "&aPlease defend!", "&aPlease defend!", "", "&eClick to send!");
                    saveCommunicationItem(yml, "10", "&aLet's attack {team}", "&aLet's attack!", "&7You will be able to select", "&7the Team", "", "&eClick to send!");
                    saveCommunicationItem(yml, "11", "&aWe need {resource}", "&aWe need resources!", "&7You will be able to select", "&7the Resource", "", "&eClick to send!");
                    saveCommunicationItem(yml, "12", "&aPlayer incoming!", "&aPlayer incoming!!", "", "&eClick to send!");
                }

                yml.addDefault(COMMUNICATIONS_MENU_LORE, new String[]{"&7Click to send message: '{message}&7'", "&7to your teammates!", "", "&eClick to send!"});
                yml.addDefault(COMMUNICATIONS_MENU_TEAMS_TITLE, "&8Select an option:");
                saveItem(yml, ConfigData.COMMUNICATIONS_MENU_TEAMS + ".back-item", "&aGo Back", "&7To Quick Communications");
                yml.addDefault(COMMUNICATIONS_MENU_RESOURCES_TITLE, "&8Select an option:");
                saveItem(yml, ConfigData.COMMUNICATIONS_MENU_RESOURCES + ".back-item", "&aGo Back", "&7To Quick Communications");
                saveResource(yml, ConfigData.COMMUNICATIONS_MENU_RESOURCES + ".iron", "&f&lIRON");
                saveResource(yml, ConfigData.COMMUNICATIONS_MENU_RESOURCES + ".gold", "&6&lGOLD");
                saveResource(yml, ConfigData.COMMUNICATIONS_MENU_RESOURCES + ".diamond", "&b&lDIAMOND");
                saveResource(yml, ConfigData.COMMUNICATIONS_MENU_RESOURCES + ".emerald", "&2&lEMERALD");
            //}

            l.getYml().options().copyDefaults(true);
            l.save();
        }

    }

    public static Language getLang(Player player) {
        return Language.getPlayerLanguage(player);
    }

    public static YamlConfiguration getYml(Player player) {
        return getLang(player).getYml();
    }

    public void saveResource(YamlConfiguration yml, String path, String resourceName) {
        path = PATH + path;
        yml.addDefault(path + ".resource-name", resourceName);
    }

    public void saveItem(YamlConfiguration yml, String path, String displayName, String... lore) {
        path = PATH + path;
        yml.addDefault(path + ".display-name", displayName);
        yml.addDefault(path + ".lore", lore);
    }

    public void saveCommunicationItem(YamlConfiguration yml, String path, String message, String displayName, String... lore) {
        path = PATH + ConfigData.COMMUNICATIONS_MENU_ITEMS + "." + path;
        yml.addDefault(path + ".message", message);
        yml.addDefault(path + ".display-name", displayName);
        yml.addDefault(path + ".lore", lore);
    }

    public static final String
            NOT_ALL_BEDS_DESTROYED = PATH + "messages.not-all-beds-destroyed",
            PURCHASED = PATH + "messages.purchase-message",
            NOT_ENOUGH_RESOURCE = PATH + "messages.not-enough-resource",
            ALREADY_TRACKING = PATH + "messages.already-tracking",
            STATUS_UNLOCKED = PATH + "tracker-status.unlocked",
            STATUS_LOCKED = PATH + "tracker-status.locked",
            STATUS_NOT_ENOUGH = PATH + "tracker-status.not-enough",
            ACTION_BAR_TRACKING = PATH + "action-bar.tracking-format",
            TEAM_MESSAGE_FORMAT = PATH + "team-message-format",

    MAIN_MENU_TITLE = PATH + "menus.main-menu.title",
            TRACKER_MENU_TITLE = PATH + "menus.tracker-menu.title",
            COMMUNICATIONS_MENU_TITLE = PATH + "menus.communications.title",
            COMMUNICATIONS_MENU_RESOURCES_TITLE = PATH + "communication-menus.resources.title",
            COMMUNICATIONS_MENU_TEAMS_TITLE = PATH + "communication-menus.teams.title",

    COMMUNICATIONS_MENU_LORE = PATH + "communication-menus.lore";

}