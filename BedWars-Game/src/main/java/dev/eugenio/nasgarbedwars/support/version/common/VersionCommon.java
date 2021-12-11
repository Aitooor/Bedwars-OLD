package dev.eugenio.nasgarbedwars.support.version.common;

import dev.eugenio.nasgarbedwars.listeners.ItemDropPickListener;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.shop.defaultrestore.ShopItemRestoreListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class VersionCommon {
    public VersionCommon() {
        registerListeners(BedWars.getInstance(), new ItemDropPickListener.PlayerPickup(), new ShopItemRestoreListener.PlayerPickup());
        registerListeners(BedWars.getInstance(), new ItemDropPickListener.PlayerDrop(), new ShopItemRestoreListener.PlayerDrop());
        registerListeners(BedWars.getInstance(), new ItemDropPickListener.GeneratorCollect(), new ShopItemRestoreListener.DefaultRestoreInvClose());
    }

    private void registerListeners(Plugin plugin, Listener... varArgs) {
        for (Listener listener : varArgs) plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }
}
