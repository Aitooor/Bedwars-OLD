package dev.eugenio.nasgarbedwars.cosmetics.toppers;

import dev.eugenio.nasgarbedwars.BedWars;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class ToppersManager {

    BedWars plugin = BedWars.getInstance();

    private final Set<Topper> topperList = new HashSet<>();

    public ToppersManager() {
        File cosmetics = new File(plugin.getDataFolder(), "cosmetics");
        if (!cosmetics.exists())
            cosmetics.mkdirs();

        File handItems = new File(plugin.getDataFolder() + "/cosmetics/", "topper");
        if (!handItems.exists())
            handItems.mkdirs();

        if (handItems.listFiles().length == 0 || Arrays.stream(handItems.listFiles()).noneMatch(file -> file.getName().contains(".yml"))) {
            plugin.getLogger().log(Level.WARNING, "No hay items");
            return;
        }

        for (File itemConfig : handItems.listFiles()) {
            if (!itemConfig.getName().contains(".yml")) continue;

            plugin.getLogger().log(Level.WARNING, "Se ha encontrado un archivo llamado " + itemConfig.getName().replaceAll(".yml", ""));
            plugin.getLogger().log(Level.WARNING, "Cargando toppers " + itemConfig.getName().replaceAll(".yml", ""));

            FileConfiguration configItem = YamlConfiguration.loadConfiguration(itemConfig);

            String topperName = configItem.getString("name");
            String topperSchem = configItem.getString("schematic");

            Topper topper = new Topper(topperName, topperSchem);
            topperList.add(topper);

        }
    }

    public Topper getTopper(String name) {
        return topperList.stream().filter(topper -> topper.getTopperName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
