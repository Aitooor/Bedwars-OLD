package dev.eugenio.nasgarbedwars.cosmetics.finalkills;

import dev.eugenio.nasgarbedwars.BedWars;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class FinalKillsManager {

    BedWars plugin = BedWars.getInstance();

    private final Set<FinalKill> finalKillsList = new HashSet<>();

    public FinalKillsManager() {
        File cosmetics = new File(plugin.getDataFolder(), "cosmetics");
        if (!cosmetics.exists())
            cosmetics.mkdirs();

        File handItems = new File(plugin.getDataFolder() + "/cosmetics/", "finalKills");
        if (!handItems.exists())
            handItems.mkdirs();

        if (handItems.listFiles().length == 0 || Arrays.stream(handItems.listFiles()).noneMatch(file -> file.getName().contains(".yml"))) {
            plugin.getLogger().log(Level.WARNING, "No hay items");
            return;
        }

        for (File itemConfig : handItems.listFiles()) {
            if (!itemConfig.getName().contains(".yml")) continue;

            plugin.getLogger().log(Level.WARNING, "Se ha encontrado un archivo llamado " + itemConfig.getName().replaceAll(".yml", ""));
            plugin.getLogger().log(Level.WARNING, "Cargando final kill " + itemConfig.getName().replaceAll(".yml", ""));

            FileConfiguration configItem = YamlConfiguration.loadConfiguration(itemConfig);

            String name = configItem.getString("name");

            FinalKill finalKill = new FinalKill(name);
            finalKillsList.add(finalKill);

        }
    }

    public FinalKill getFinalKill(String name) {
        return finalKillsList.stream().filter(deathCry -> deathCry.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
