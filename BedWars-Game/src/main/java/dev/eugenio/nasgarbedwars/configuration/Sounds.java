package dev.eugenio.nasgarbedwars.configuration;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.NextEvent;
import dev.eugenio.nasgarbedwars.api.configuration.ConfigManager;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class Sounds {
    private static final ConfigManager sounds;
    
    public static void init() {
        final YamlConfiguration yml = Sounds.sounds.getYml();
        addDefSound("game-end", BedWars.getInstance().getForCurrentVersion("AMBIENCE_THUNDER", "ENTITY_LIGHTNING_THUNDER", "ITEM_TRIDENT_THUNDER"));
        addDefSound("bed-destroy", BedWars.getInstance().getForCurrentVersion("ENDERDRAGON_GROWL", "ENTITY_ENDERDRAGON_GROWL", "ENTITY_ENDER_DRAGON_GROWL"));
        addDefSound("shop-insufficient-money", BedWars.getInstance().getForCurrentVersion("ENDERMAN_TELEPORT", "ENTITY_ENDERMAN_TELEPORT", "ENTITY_ENDERMAN_TELEPORT"));
        addDefSound("shop-bought", BedWars.getInstance().getForCurrentVersion("NOTE_PLING", "NOTE_PLING", "NOTE_PLING"));
        addDefSound(NextEvent.ENDER_DRAGON.getSoundPath(), BedWars.getInstance().getForCurrentVersion("ENDERDRAGON_WINGS", "ENTITY_ENDERDRAGON_FLAP", "ENTITY_ENDER_DRAGON_FLAP"));
        addDefSound("arena-selector-open", BedWars.getInstance().getForCurrentVersion("CHICKEN_EGG_POP", "ENTITY_CHICKEN_EGG", "ENTITY_CHICKEN_EGG"));
        addDefSound("stats-gui-open", BedWars.getInstance().getForCurrentVersion("CHICKEN_EGG_POP", "ENTITY_CHICKEN_EGG", "ENTITY_CHICKEN_EGG"));
        addDefSound("trap-sound", BedWars.getInstance().getForCurrentVersion("ENDERMAN_TELEPORT", "ENDERMAN_TELEPORT", "ENTITY_ENDERMAN_TELEPORT"));
        addDefSound("egg-bridge-block", BedWars.getInstance().getForCurrentVersion("CHICKEN_EGG_POP", "ENTITY_CHICKEN_EGG", "ENTITY_CHICKEN_EGG"));
        yml.options().copyDefaults(true);
        yml.set("bought", null);
        yml.set("insufficient-money", null);
        yml.set("player-kill", null);
        yml.set("countdown", null);
        Sounds.sounds.save();
    }
    
    private static Sound getSound(final String s) {
        try {
            return Sound.valueOf(Sounds.sounds.getString(s + ".sound"));
        } catch (Exception ex) {
            return Sound.valueOf(BedWars.getInstance().getForCurrentVersion("AMBIENCE_THUNDER", "ENTITY_LIGHTNING_THUNDER", "ITEM_TRIDENT_THUNDER"));
        }
    }
    
    public static void playSound(final String s, final List<Player> list) {
        Sound sound = getSound(s);
        int i = getSounds().getInt(s + ".volume");
        int j = getSounds().getInt(s + ".pitch");
        if (sound != null) list.forEach(paramPlayer -> paramPlayer.playSound(paramPlayer.getLocation(), s, i, j));
    }
    
    public static boolean playSound(final Sound sound, final List<Player> list) {
        if (sound == null) return false;
        list.forEach(player -> player.playSound(player.getLocation(), sound, 1.0f, 1.0f));
        return true;
    }
    
    public static void playSound(final String s, final Player player) {
        final Sound sound = getSound(s);
        final float n = (float)getSounds().getYml().getDouble(s + ".volume");
        final float n2 = (float)getSounds().getYml().getDouble(s + ".pitch");
        if (sound != null) player.playSound(player.getLocation(), sound, n, n2);
    }
    
    public static ConfigManager getSounds() {
        return Sounds.sounds;
    }
    
    private static void addDefSound(final String s, final String s2) {
        if (getSounds().getYml().get(s) != null && getSounds().getYml().get(s + ".volume") == null) {
            final String string = getSounds().getYml().getString(s);
            getSounds().getYml().set(s, null);
            getSounds().getYml().set(s + ".sound", string);
        }
        getSounds().getYml().addDefault(s + ".sound", s2);
        getSounds().getYml().addDefault(s + ".volume", 1);
        if (s.equals("shop-insufficient-money")) {
            getSounds().getYml().addDefault(s + ".pitch", 0);
        } else if (s.equals("shop-bought")) {
            getSounds().getYml().addDefault(s + ".pitch", -1);
        } else {
            getSounds().getYml().addDefault(s + ".pitch", 1);
        }
    }
    
    static {
        sounds = new ConfigManager(BedWars.getInstance(), "sounds", BedWars.getInstance().getDataFolder().getPath());
    }
}
