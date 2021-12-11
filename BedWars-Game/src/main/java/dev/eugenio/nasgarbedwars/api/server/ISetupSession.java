package dev.eugenio.nasgarbedwars.api.server;

import dev.eugenio.nasgarbedwars.api.configuration.ConfigManager;
import org.bukkit.entity.Player;

public interface ISetupSession {
    String getWorldName();

    Player getPlayer();

    SetupType getSetupType();

    ConfigManager getConfig();

    void teleportPlayer();

    void close();
}
