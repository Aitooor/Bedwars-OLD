package dev.eugenio.nasgarbedwars.arena.compass.util;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.arena.compass.data.ConfigData;
import dev.eugenio.nasgarbedwars.arena.compass.data.MessagesData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MessagingUtil {
    public static void simpleMessage(Player player, ITeam team, String path) {
        player.closeInventory();
        for (Player p : team.getMembers()) {
            final YamlConfiguration yml = MessagesData.getYml(p);
            final String formatted = yml.getString(MessagesData.TEAM_MESSAGE_FORMAT).replace("{player}", player.getDisplayName()).replace("{message}", yml.getString(path));
            VersionUtil.playSound(p, BedWars.getInstance().getConfigData().getString(ConfigData.MESSAGE_SEND_SOUND));
            p.sendMessage(TextUtil.colorize(formatted));
        }
    }

    public static void resourceMessage(Player player, ITeam team, String path, String resourcePath) {
        player.closeInventory();
        for (Player p : team.getMembers()) {
            final YamlConfiguration yml = MessagesData.getYml(p);
            final String formatted = yml.getString(MessagesData.TEAM_MESSAGE_FORMAT).replace("{player}", player.getDisplayName()).replace("{message}", yml.getString(path).replace("{resource}", yml.getString(MessagesData.PATH + ConfigData.COMMUNICATIONS_MENU_RESOURCES + "." + resourcePath + ".resource-name")));
            VersionUtil.playSound(p, BedWars.getInstance().getConfigData().getString(ConfigData.MESSAGE_SEND_SOUND));
            p.sendMessage(TextUtil.colorize(formatted));
        }
    }

    public static void teamMessage(Player player, ITeam team, String path, ITeam specifiedTeam) {
        player.closeInventory();
        for (Player p : team.getMembers()) {
            final Language lang = MessagesData.getLang(p);
            final YamlConfiguration yml = lang.getYml();
            final String formatted = yml.getString(MessagesData.TEAM_MESSAGE_FORMAT).replace("{player}", player.getDisplayName()).replace("{message}", yml.getString(path).replace("{team}", specifiedTeam.getColor().chat() + "§l" + specifiedTeam.getDisplayName(lang)));
            VersionUtil.playSound(p, BedWars.getInstance().getConfigData().getString(ConfigData.MESSAGE_SEND_SOUND));
            p.sendMessage(TextUtil.colorize(formatted));
        }
    }
}