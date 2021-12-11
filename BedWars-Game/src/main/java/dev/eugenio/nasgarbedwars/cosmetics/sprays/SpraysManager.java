package dev.eugenio.nasgarbedwars.cosmetics.sprays;

import dev.eugenio.nasgarbedwars.BedWars;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class SpraysManager {

    BedWars plugin = BedWars.getInstance();

    private final Set<Spray> spraysList = new HashSet<>();

    public SpraysManager() {
        File cosmetics = new File(plugin.getDataFolder(), "cosmetics");
        if (!cosmetics.exists())
            cosmetics.mkdirs();

        File handItems = new File(plugin.getDataFolder() + "/cosmetics/", "sprays");
        if (!handItems.exists())
            handItems.mkdirs();

        if (handItems.listFiles().length == 0 || Arrays.stream(handItems.listFiles()).noneMatch(file -> file.getName().contains(".yml"))) {
            plugin.getLogger().log(Level.WARNING, "No hay items");
            return;
        }

        for (File itemConfig : handItems.listFiles()) {
            if (!itemConfig.getName().contains(".yml")) continue;

            plugin.getLogger().log(Level.WARNING, "Se ha encontrado un archivo llamado " + itemConfig.getName().replaceAll(".yml", ""));
            plugin.getLogger().log(Level.WARNING, "Cargando sprays " + itemConfig.getName().replaceAll(".yml", ""));

            FileConfiguration configItem = YamlConfiguration.loadConfiguration(itemConfig);

            String spraysName = configItem.getString("name");
            String spraysFile = configItem.getString("imageFile");

            Spray spray = new Spray(spraysName, spraysFile);
            spraysList.add(spray);

        }
    }

    public Spray getSpray(String name) {
        return spraysList.stream().filter(spray -> spray.getSprayName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
