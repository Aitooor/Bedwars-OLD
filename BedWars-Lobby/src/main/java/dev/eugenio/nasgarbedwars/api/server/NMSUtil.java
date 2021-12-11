package dev.eugenio.nasgarbedwars.api.server;

import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;

public abstract class NMSUtil {
    private final Plugin plugin;

    private static String name2;

    public NMSUtil(final Plugin plugin, final String name2) {
        NMSUtil.name2 = name2;
        this.plugin = plugin;
    }

    public abstract void registerCommand(final String p0, final Command p1);

    public Plugin getPlugin() {
        return this.plugin;
    }
}
