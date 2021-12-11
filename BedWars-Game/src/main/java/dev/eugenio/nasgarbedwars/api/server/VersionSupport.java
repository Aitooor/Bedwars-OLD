package dev.eugenio.nasgarbedwars.api.server;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public abstract class VersionSupport {
    private static String name2;

    private final Plugin plugin;

    public VersionSupport(Plugin paramPlugin, String paramString) {
        name2 = paramString;
        this.plugin = paramPlugin;
    }

    public abstract void sendTitle(Player paramPlayer, String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3);

    public abstract ItemStack getItemInHand(Player paramPlayer);

    public abstract ItemStack addCustomData(ItemStack paramItemStack, String paramString);

    public abstract ItemStack createItemStack(String paramString, int paramInt, short paramShort);

    public static String getName() {
        return name2;
    }

    public abstract int getVersion();

    public Plugin getPlugin() {
        return this.plugin;
    }
}
