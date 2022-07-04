package dev.eugenio.nasgarbedwars.api.arena.team;

import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public enum TeamColor {
    RED,
    BLUE,
    GREEN,
    YELLOW,
    AQUA,
    WHITE,
    PINK,
    GRAY,
    DARK_GREEN;

    public static ChatColor getChatColor(final String s) {
        final TeamColor value = valueOf(s.toUpperCase());
        ChatColor chatColor;
        if (value == TeamColor.PINK) {
            chatColor = ChatColor.LIGHT_PURPLE;
        } else {
            chatColor = ChatColor.valueOf(value.toString());
        }
        return chatColor;
    }

    @Deprecated
    public static ChatColor getChatColor(final TeamColor teamColor) {
        ChatColor chatColor;
        if (teamColor == TeamColor.PINK) {
            chatColor = ChatColor.LIGHT_PURPLE;
        } else {
            chatColor = ChatColor.valueOf(teamColor.toString());
        }
        return chatColor;
    }

    @Deprecated
    public static DyeColor getDyeColor(final String s) {
        DyeColor dyeColor;
        switch (valueOf(s.toUpperCase())) {
            case GREEN:
                dyeColor = DyeColor.LIME;
                break;
            case AQUA:
                dyeColor = DyeColor.LIGHT_BLUE;
                break;
            case DARK_GREEN:
                dyeColor = DyeColor.GREEN;
                break;
            default: {
                dyeColor = DyeColor.valueOf(s.toUpperCase());
                break;
            }
        }
        return dyeColor;
    }

    @Deprecated
    public static byte itemColor(final TeamColor teamColor) {
        int n = 0;
        switch (teamColor) {
            case PINK:
                n = 6;
                break;
            case RED:
                n = 14;
                break;
            case AQUA:
                n = 9;
                break;
            case GREEN:
                n = 5;
                break;
            case DARK_GREEN:
                n = 13;
                break;
            case YELLOW:
                n = 4;
                break;
            case BLUE:
                n = 11;
                break;
            case GRAY:
                n = 7;
                break;
        }
        return (byte) n;
    }

    public static String enName(final String s, Player player) {
        String s2 = "";
        final String upperCase = s.toUpperCase();
        switch (upperCase) {
            case "PINK_WOOL": {
                s2 = Language.getMsg(player, Messages.TEAM_PINK);;
                break;
            }
            case "RED": {
                s2 = "Red";
                break;
            }
            case "GRAY_WOOL": {
                s2 = "Gray";
                break;
            }
            case "BLUE_WOOL": {
                s2 = "Blue";
                break;
            }
            case "WHITE_WOOL": {
                s2 = "White";
                break;
            }
            case "LIGHT_BLUE_WOOL": {
                s2 = "Aqua";
                break;
            }
            case "LIME_WOOL": {
                s2 = "Green";
                break;
            }
            case "YELLOW_WOOL": {
                s2 = "Yellow";
                break;
            }
        }
        return s2;
    }

    public static String enName(final byte b) {
        String s = "";
        switch (b) {
            case 6: {
                s = "Pink";
                break;
            }
            case 14: {
                s = "Red";
                break;
            }
            case 9: {
                s = "Aqua";
                break;
            }
            case 5: {
                s = "Green";
                break;
            }
            case 4: {
                s = "Yellow";
                break;
            }
            case 11: {
                s = "Blue";
                break;
            }
            case 0: {
                s = "White";
                break;
            }
            case 7: {
                s = "Gray";
                break;
            }
        }
        return s;
    }

    @Deprecated
    public static Color getColor(final TeamColor teamColor) {
        Color color = Color.WHITE;
        switch (teamColor) {
            case PINK:
                color = Color.FUCHSIA;
                break;
            case GRAY:
                color = Color.GRAY;
                break;
            case BLUE:
                color = Color.BLUE;
                break;
            case DARK_GREEN:
                color = Color.GREEN;
                break;
            case AQUA:
                color = Color.AQUA;
                break;
            case RED:
                color = Color.RED;
                break;
            case GREEN:
                color = Color.LIME;
                break;
            case YELLOW:
                color = Color.YELLOW;
                break;
        }
        return color;
    }

    @Deprecated
    public static Material getBedBlock(final TeamColor teamColor) {
        String s = "RED_BED";
        switch (teamColor) {
            case PINK:
                s = "PINK_BED";
                break;
            case GRAY:
                s = "GRAY_BED";
                break;
            case BLUE:
                s = "BLUE_BED";
                break;
            case WHITE:
                s = "WHITE_BED";
                break;
            case DARK_GREEN:
                s = "GREEN_BED";
                break;
            case AQUA:
                s = "LIGHT_BLUE_BED";
                break;
            case GREEN:
                s = "LIME_BED";
                break;
            case YELLOW:
                s = "YELLOW_BED";
                break;
        }
        return Material.valueOf(s);
    }

    @Deprecated
    public static Material getGlass(final TeamColor teamColor) {
        String s = "GLASS";
        switch (teamColor) {
            case PINK:
                s = "PINK_STAINED_GLASS";
                break;
            case GRAY:
                s = "GRAY_STAINED_GLASS";
                break;
            case BLUE:
                s = "BLUE_STAINED_GLASS";
                break;
            case WHITE:
                s = "WHITE_STAINED_GLASS";
                break;
            case DARK_GREEN:
                s = "GREEN_STAINED_GLASS";
                break;
            case AQUA:
                s = "LIGHT_BLUE_STAINED_GLASS";
                break;
            case GREEN:
                s = "LIME_STAINED_GLASS";
                break;
            case YELLOW:
                s = "YELLOW_STAINED_GLASS";
                break;
            case RED:
                s = "RED_STAINED_GLASS";
                break;
        }
        return Material.valueOf(s);
    }

    @Deprecated
    public static Material getGlassPane(final TeamColor teamColor) {
        String s = "GLASS";
        switch (teamColor) {
            case PINK:
                s = "PINK_STAINED_GLASS_PANE";
                break;
            case GRAY:
                s = "GRAY_STAINED_GLASS_PANE";
                break;
            case BLUE:
                s = "BLUE_STAINED_GLASS_PANE";
                break;
            case WHITE:
                s = "WHITE_STAINED_GLASS_PANE";
                break;
            case DARK_GREEN:
                s = "GREEN_STAINED_GLASS_PANE";
                break;
            case AQUA:
                s = "LIGHT_BLUE_STAINED_GLASS_PANE";
                break;
            case GREEN:
                s = "LIME_STAINED_GLASS_PANE";
                break;
            case YELLOW:
                s = "YELLOW_STAINED_GLASS_PANE";
                break;
            case RED:
                s = "RED_STAINED_GLASS_PANE";
                break;
        }
        return Material.valueOf(s);
    }

    @Deprecated
    public static Material getGlazedTerracotta(final TeamColor teamColor) {
        String s = "ORANGE_TERRACOTTA";
        switch (teamColor) {
            case PINK:
                s = "PINK_TERRACOTTA";
                break;
            case GRAY:
                s = "GRAY_TERRACOTTA";
                break;
            case BLUE:
                s = "BLUE_TERRACOTTA";
                break;
            case WHITE:
                s = "WHITE_TERRACOTTA";
                break;
            case DARK_GREEN:
                s = "GREEN_TERRACOTTA";
                break;
            case AQUA:
                s = "LIGHT_BLUE_TERRACOTTA";
                break;
            case GREEN:
                s = "LIME_TERRACOTTA";
                break;
            case YELLOW:
                s = "YELLOW_TERRACOTTA";
                break;
            case RED:
                s = "RED_TERRACOTTA";
                break;
        }
        return Material.valueOf(s);
    }

    @Deprecated
    public static Material getWool(final TeamColor teamColor) {
        String s = "WHITE_WOOL";
        switch (teamColor) {
            case PINK:
                s = "PINK_WOOL";
                break;
            case GRAY:
                s = "GRAY_WOOL";
                break;
            case BLUE:
                s = "BLUE_WOOL";
                break;
            case WHITE:
                s = "WHITE_WOOL";
                break;
            case DARK_GREEN:
                s = "GREEN_WOOL";
                break;
            case AQUA:
                s = "LIGHT_BLUE_WOOL";
                break;
            case GREEN:
                s = "LIME_WOOL";
                break;
            case YELLOW:
                s = "YELLOW_WOOL";
                break;
            case RED:
                s = "RED_WOOL";
                break;
        }
        return Material.valueOf(s);
    }

    public ChatColor chat() {
        final TeamColor value = valueOf(this.toString());
        ChatColor chatColor;
        if (value == TeamColor.PINK) {
            chatColor = ChatColor.LIGHT_PURPLE;
        } else {
            chatColor = ChatColor.valueOf(value.toString());
        }
        return chatColor;
    }

    public DyeColor dye() {
        DyeColor dyeColor;
        switch (this) {
            case GREEN:
                dyeColor = DyeColor.LIME;
                break;
            case AQUA:
                dyeColor = DyeColor.LIGHT_BLUE;
                break;
            case DARK_GREEN:
                dyeColor = DyeColor.GREEN;
                break;
            default:
                dyeColor = DyeColor.valueOf(this.toString());
                break;
        }
        return dyeColor;
    }

    public byte itemByte() {
        int n = 0;
        switch (this) {
            case PINK:
                n = 6;
                break;
            case RED:
                n = 14;
                break;
            case AQUA:
                n = 9;
                break;
            case GREEN:
                n = 5;
                break;
            case DARK_GREEN:
                n = 13;
                break;
            case YELLOW:
                n = 4;
                break;
            case BLUE:
                n = 11;
                break;
            case GRAY:
                n = 7;
                break;
        }
        return (byte) n;
    }

    public Color bukkitColor() {
        Color color = Color.WHITE;
        switch (this) {
            case PINK:
                color = Color.FUCHSIA;
                break;
            case GRAY:
                color = Color.GRAY;
                break;
            case BLUE:
                color = Color.BLUE;
            case DARK_GREEN:
                color = Color.GREEN;
                break;
            case AQUA:
                color = Color.AQUA;
                break;
            case RED:
                color = Color.RED;
                break;
            case GREEN:
                color = Color.LIME;
                break;
            case YELLOW:
                color = Color.YELLOW;
                break;
        }
        return color;
    }

    public Material woolMaterial() {
        String s = "WHITE_WOOL";
        switch (this) {
            case PINK:
                s = "PINK_WOOL";
                break;
            case GRAY:
                s = "GRAY_WOOL";
                break;
            case BLUE:
                s = "BLUE_WOOL";
                break;
            case WHITE:
                s = "WHITE_WOOL";
                break;
            case DARK_GREEN:
                s = "GREEN_WOOL";
                break;
            case AQUA:
                s = "LIGHT_BLUE_WOOL";
                break;
            case GREEN:
                s = "LIME_WOOL";
                break;
            case YELLOW:
                s = "YELLOW_WOOL";
                break;
            case RED:
                s = "RED_WOOL";
                break;
        }
        return Material.valueOf(s);
    }
}
