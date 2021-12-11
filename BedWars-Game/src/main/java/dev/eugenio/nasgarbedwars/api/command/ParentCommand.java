package dev.eugenio.nasgarbedwars.api.command;

import org.bukkit.entity.Player;

import java.util.List;

public interface ParentCommand {
    boolean hasSubCommand(final String p0);

    void addSubCommand(final SubCommand p0);

    void sendSubCommands(final Player p0);

    List<SubCommand> getSubCommands();

    String getName();
}
