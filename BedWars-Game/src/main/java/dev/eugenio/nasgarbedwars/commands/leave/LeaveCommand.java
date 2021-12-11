package dev.eugenio.nasgarbedwars.commands.leave;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public class LeaveCommand extends BukkitCommand {
    public LeaveCommand(final String s) {
        super(s);
    }
    
    public boolean execute(final CommandSender commandSender, final String s, final String[] array) {
        if (commandSender instanceof ConsoleCommandSender) return true;
        Bukkit.dispatchCommand(commandSender, "bw leave");
        return true;
    }
}
