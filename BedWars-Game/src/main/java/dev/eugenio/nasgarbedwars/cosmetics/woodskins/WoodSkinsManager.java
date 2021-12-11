package dev.eugenio.nasgarbedwars.cosmetics.woodskins;

import dev.eugenio.nasgarbedwars.BedWars;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class WoodSkinsManager {

    BedWars plugin = BedWars.getInstance();

    private final Set<WoodSkin> woodSkinsList = new HashSet<>();

    public WoodSkinsManager() {
        File cosmetics = new File(plugin.getDataFolder(), "cosmetics");
        if (!cosmetics.exists())
            cosmetics.mkdirs();

        File handItems = new File(plugin.getDataFolder() + "/cosmetics/", "woodSkins");
        if (!handItems.exists())
            handItems.mkdirs();

        if (handItems.listFiles().length == 0 || Arrays.stream(handItems.listFiles()).noneMatch(file -> file.getName().contains(".yml"))) {
            plugin.getLogger().log(Level.WARNING, "No hay items");
            return;
        }

        for (File itemConfig : handItems.listFiles()) {
            if (!itemConfig.getName().contains(".yml")) continue;

            plugin.getLogger().log(Level.WARNING, "Se ha encontrado un archivo llamado " + itemConfig.getName().replaceAll(".yml", ""));
            plugin.getLogger().log(Level.WARNING, "Cargando wood skin " + itemConfig.getName().replaceAll(".yml", ""));

            FileConfiguration configItem = YamlConfiguration.loadConfiguration(itemConfig);

            String name = configItem.getString("name");
            int data = configItem.getInt("data");

            WoodSkin woolSkin = new WoodSkin(name, data);
            woodSkinsList.add(woolSkin);

        }
    }

    public WoodSkin getWoodSkin(String name) {
        return woodSkinsList.stream().filter(woolSkin -> woolSkin.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
