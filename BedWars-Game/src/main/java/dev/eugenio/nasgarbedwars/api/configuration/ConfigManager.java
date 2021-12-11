package dev.eugenio.nasgarbedwars.api.configuration;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ConfigManager {
    private YamlConfiguration yml;
    private File config;
    @Getter
    @Setter
    private String name;
    private boolean firstTime;

    public ConfigManager(final Plugin plugin, final String name, final String s) {
        this.firstTime = false;
        final File file = new File(s);
        if (!file.exists() && !file.mkdirs()) {
            plugin.getLogger().log(Level.SEVERE, "No se ha podido crear " + file.getPath());
            return;
        }
        this.config = new File(s, name + ".yml");
        if (!this.config.exists()) {
            this.firstTime = true;
            plugin.getLogger().log(Level.INFO, "Creando " + this.config.getPath());
            try {
                if (!this.config.createNewFile()) {
                    plugin.getLogger().log(Level.SEVERE, "No se ha podido crear " + this.config.getPath());
                    return;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        this.yml = YamlConfiguration.loadConfiguration(this.config);
        this.yml.options().copyDefaults(true);
        this.name = name;
    }

    public void reload() {
        this.yml = YamlConfiguration.loadConfiguration(this.config);
    }

    public String stringLocationArenaFormat(final Location location) {
        return location.getX() + "," + location.getY() + "," + location.getZ() + "," + (double) location.getYaw() + "," + (double) location.getPitch();
    }

    public String stringLocationConfigFormat(final Location location) {
        return location.getX() + "," + location.getY() + "," + location.getZ() + "," + (double) location.getYaw() + "," + (double) location.getPitch() + "," + location.getWorld().getName();
    }

    public void saveConfigLoc(final String s, final Location location) {
        this.yml.set(s, location.getX() + "," + location.getY() + "," + location.getZ() + "," + (double) location.getYaw() + "," + (double) location.getPitch() + "," + location.getWorld().getName());
        this.save();
    }

    public void saveArenaLoc(final String s, final Location location) {
        this.yml.set(s, location.getX() + "," + location.getY() + "," + location.getZ() + "," + (double) location.getYaw() + "," + (double) location.getPitch());
        this.save();
    }

    public Location getConfigLoc(final String s) {
        final String string = this.yml.getString(s);
        if (string == null) {
            return null;
        }
        final String[] split = string.replace("[", "").replace("]", "").split(",");
        return new Location(Bukkit.getWorld(split[5]), Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Float.parseFloat(split[3]), Float.parseFloat(split[4]));
    }

    public Location getArenaLoc(final String s) {
        final String string = this.yml.getString(s);
        if (string == null) {
            return null;
        }
        final String[] split = string.replace("[", "").replace("]", "").split(",");
        return new Location(Bukkit.getWorld(this.name), Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Float.parseFloat(split[3]), Float.parseFloat(split[4]));
    }

    public Location convertStringToArenaLocation(final String s) {
        final String[] split = s.split(",");
        return new Location(Bukkit.getWorld(this.name), Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Float.parseFloat(split[3]), Float.parseFloat(split[4]));
    }

    public List<Location> getArenaLocations(final String s) {
        final ArrayList<Location> list = new ArrayList<>();
        for (String value : this.yml.getStringList(s)) {
            final Location convertStringToArenaLocation = this.convertStringToArenaLocation(value);
            if (convertStringToArenaLocation != null) {
                list.add(convertStringToArenaLocation);
            }
        }
        return list;
    }

    public void set(final String s, final Object o) {
        this.yml.set(s, o);
        this.save();
    }

    public YamlConfiguration getYml() {
        return this.yml;
    }

    public void save() {
        try {
            this.yml.save(this.config);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public List<String> getList(final String s2) {
        return this.yml.getStringList(s2).stream().map(s -> s.replace("&", "�")).collect(Collectors.toList());
    }

    public boolean getBoolean(final String s) {
        return this.yml.getBoolean(s);
    }

    public int getInt(final String s) {
        return this.yml.getInt(s);
    }

    public String getString(final String s) {
        return this.yml.getString(s);
    }

    public boolean isFirstTime() {
        return this.firstTime;
    }

    public boolean compareArenaLoc(final Location location, final Location location2) {
        return location.getBlockX() == location2.getBlockX() && location.getBlockZ() == location2.getBlockZ() && location.getBlockY() == location2.getBlockY();
    }
}
