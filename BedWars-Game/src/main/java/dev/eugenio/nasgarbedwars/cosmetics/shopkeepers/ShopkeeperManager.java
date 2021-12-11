package dev.eugenio.nasgarbedwars.cosmetics.shopkeepers;

import dev.eugenio.nasgarbedwars.BedWars;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class ShopkeeperManager {

    BedWars plugin = BedWars.getInstance();

    private final Set<Shopkeeper> shopkeeperList = new HashSet<>();

    public ShopkeeperManager() {
        File cosmetics = new File(plugin.getDataFolder(), "cosmetics");
        if (!cosmetics.exists())
            cosmetics.mkdirs();

        File handItems = new File(plugin.getDataFolder() + "/cosmetics/", "shopkeepers");
        if (!handItems.exists())
            handItems.mkdirs();

        if (handItems.listFiles().length == 0 || Arrays.stream(handItems.listFiles()).noneMatch(file -> file.getName().contains(".yml"))) {
            plugin.getLogger().log(Level.WARNING, "No hay items");
            return;
        }

        for (File itemConfig : handItems.listFiles()) {
            if (!itemConfig.getName().contains(".yml")) continue;

            plugin.getLogger().log(Level.WARNING, "Se ha encontrado un archivo llamado " + itemConfig.getName().replaceAll(".yml", ""));
            plugin.getLogger().log(Level.WARNING, "Cargando shopkeeper " + itemConfig.getName().replaceAll(".yml", ""));

            FileConfiguration configItem = YamlConfiguration.loadConfiguration(itemConfig);

            String shopkeeperName = configItem.getString("name");
            String shopkeeperEntity = configItem.getString("entity");
            String shopkeeperSkin = configItem.getString("skin");

            Shopkeeper topper = new Shopkeeper(shopkeeperName, shopkeeperEntity, shopkeeperSkin);
            shopkeeperList.add(topper);

        }
    }

    public Shopkeeper getShopkeeper(String name) {
        return shopkeeperList.stream().filter(shopkeeper -> shopkeeper.getShopkeeperName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
