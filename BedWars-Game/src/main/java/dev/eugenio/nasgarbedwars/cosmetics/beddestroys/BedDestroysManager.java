package dev.eugenio.nasgarbedwars.cosmetics.beddestroys;

import dev.eugenio.nasgarbedwars.BedWars;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class BedDestroysManager {

    BedWars plugin = BedWars.getInstance();

    private final Set<BedDestroy> bedDestroysList = new HashSet<>();

    public BedDestroysManager() {
        File cosmetics = new File(plugin.getDataFolder(), "cosmetics");
        if (!cosmetics.exists())
            cosmetics.mkdirs();

        File handItems = new File(plugin.getDataFolder() + "/cosmetics/", "bedDestroys");
        if (!handItems.exists())
            handItems.mkdirs();

        if (handItems.listFiles().length == 0 || Arrays.stream(handItems.listFiles()).noneMatch(file -> file.getName().contains(".yml"))) {
            plugin.getLogger().log(Level.WARNING, "No hay items");
            return;
        }

        for (File itemConfig : handItems.listFiles()) {
            if (!itemConfig.getName().contains(".yml")) continue;

            plugin.getLogger().log(Level.WARNING, "Se ha encontrado un archivo llamado " + itemConfig.getName().replaceAll(".yml", ""));
            plugin.getLogger().log(Level.WARNING, "Cargando bed destroy " + itemConfig.getName().replaceAll(".yml", ""));

            FileConfiguration configItem = YamlConfiguration.loadConfiguration(itemConfig);

            String name = configItem.getString("name");

            BedDestroy bedDestroy = new BedDestroy(name);
            bedDestroysList.add(bedDestroy);

        }
    }

    public BedDestroy getBedDestroy(String name) {
        return bedDestroysList.stream().filter(bedDestroy -> bedDestroy.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
