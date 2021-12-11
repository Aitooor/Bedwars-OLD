package dev.eugenio.nasgarbedwars.cosmetics.killmessages;

import dev.eugenio.nasgarbedwars.BedWars;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class KillMessagesManager {

    BedWars plugin = BedWars.getInstance();

    private final Set<KillMessage> killMessagesList = new HashSet<>();

    public KillMessagesManager() {
        File cosmetics = new File(plugin.getDataFolder(), "cosmetics");
        if (!cosmetics.exists())
            cosmetics.mkdirs();

        File handItems = new File(plugin.getDataFolder() + "/cosmetics/", "killMessages");
        if (!handItems.exists())
            handItems.mkdirs();

        if (handItems.listFiles().length == 0 || Arrays.stream(handItems.listFiles()).noneMatch(file -> file.getName().contains(".yml"))) {
            plugin.getLogger().log(Level.WARNING, "No hay items");
            return;
        }

        for (File itemConfig : handItems.listFiles()) {
            if (!itemConfig.getName().contains(".yml")) continue;

            plugin.getLogger().log(Level.WARNING, "Se ha encontrado un archivo llamado " + itemConfig.getName().replaceAll(".yml", ""));
            plugin.getLogger().log(Level.WARNING, "Cargando kill message " + itemConfig.getName().replaceAll(".yml", ""));

            FileConfiguration configItem = YamlConfiguration.loadConfiguration(itemConfig);

            String name = configItem.getString("name");
            String basic = configItem.getString("basic");
            String finalKill = configItem.getString("finalKill");
            String knockedHighPlace = configItem.getString("knockedHighPlace");
            String shotByBow = configItem.getString("shotByBow");
            String blownUp = configItem.getString("blownUp");
            String golemandSilverfishKill = configItem.getString("golemandSilverfishKill");
            String bedBreak = configItem.getString("bedBreak");

            KillMessage killMessage = new KillMessage(name, basic, finalKill, knockedHighPlace, shotByBow, blownUp, golemandSilverfishKill, bedBreak);
            killMessagesList.add(killMessage);

        }
    }

    public KillMessage getKillMessage(String name) {
        return killMessagesList.stream().filter(killMessage -> killMessage.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
