package dev.eugenio.nasgarbedwars.cosmetics.deathcries;

import dev.eugenio.nasgarbedwars.BedWars;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class DeathCriesManager {

    BedWars plugin = BedWars.getInstance();

    private final Set<DeathCry> deathCriesList = new HashSet<>();

    public DeathCriesManager() {
        File cosmetics = new File(plugin.getDataFolder(), "cosmetics");
        if (!cosmetics.exists())
            cosmetics.mkdirs();

        File handItems = new File(plugin.getDataFolder() + "/cosmetics/", "deathCries");
        if (!handItems.exists())
            handItems.mkdirs();

        if (handItems.listFiles().length == 0 || Arrays.stream(handItems.listFiles()).noneMatch(file -> file.getName().contains(".yml"))) {
            plugin.getLogger().log(Level.WARNING, "No hay items");
            return;
        }

        for (File itemConfig : handItems.listFiles()) {
            if (!itemConfig.getName().contains(".yml")) continue;

            plugin.getLogger().log(Level.WARNING, "Se ha encontrado un archivo llamado " + itemConfig.getName().replaceAll(".yml", ""));
            plugin.getLogger().log(Level.WARNING, "Cargando death cry " + itemConfig.getName().replaceAll(".yml", ""));

            FileConfiguration configItem = YamlConfiguration.loadConfiguration(itemConfig);

            String name = configItem.getString("name");
            String sound = configItem.getString("sound.sound");
            float pitch = configItem.getInt("sound.pitch");

            DeathCry deathCry = new DeathCry(name, Sound.valueOf(sound.toUpperCase()), pitch);
            deathCriesList.add(deathCry);

        }
    }

    public DeathCry getDeathCry(String name) {
        return deathCriesList.stream().filter(deathCry -> deathCry.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
