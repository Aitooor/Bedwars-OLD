package dev.eugenio.nasgarbedwars.support.version.v1_8_R3;

import dev.eugenio.nasgarbedwars.api.server.NMSUtil;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.plugin.Plugin;

public class v1_8_R3 extends NMSUtil {
    public v1_8_R3(Plugin paramPlugin, String paramString) {
        super(paramPlugin, paramString);
    }

    public void registerCommand(String paramString, Command paramCommand) {
        ((CraftServer)getPlugin().getServer()).getCommandMap().register(paramString, paramCommand);
    }
}
