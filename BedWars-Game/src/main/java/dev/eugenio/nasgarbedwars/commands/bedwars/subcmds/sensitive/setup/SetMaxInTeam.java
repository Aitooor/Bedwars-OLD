package dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.sensitive.setup;

import dev.eugenio.nasgarbedwars.configuration.Permissions;
import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.arena.SetupSession;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class SetMaxInTeam extends SubCommand {
    public SetMaxInTeam(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setArenaSetupCommand(true);
        this.setPermission(Permissions.PERMISSION_SETUP_ARENA);
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) {
            return false;
        }
        final Player player = (Player)commandSender;
        final SetupSession session = SetupSession.getSession(player.getUniqueId());
        if (session == null) {
            commandSender.sendMessage("§c \u25aa §7No estás en una sesión de instalación.");
            return true;
        }
        if (array.length == 0) {
            player.sendMessage("§c\u25aa §7Uso: /bw setMaxInTeam <int>");
        } else {
            try {
                Integer.parseInt(array[0]);
            } catch (Exception ex) {
                player.sendMessage("§c\u25aa §7Uso: /bw setMaxInTeam <int>");
                return true;
            }
            session.getConfig().set("maxInTeam", Integer.valueOf(array[0]));
            player.sendMessage("§6 \u25aa §7Máximo en el team seteado.");
        }
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        return Arrays.asList("1", "2", "4");
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BedWarsAPI bedWarsAPI) {
        return !(commandSender instanceof ConsoleCommandSender) && SetupSession.isInSetupSession(((Player)commandSender).getUniqueId()) && this.hasPermission(commandSender);
    }
}
