package dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.sensitive.setup;

import dev.eugenio.nasgarbedwars.configuration.Permissions;
import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.arena.Misc;
import dev.eugenio.nasgarbedwars.arena.SetupSession;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class WaitingPos extends SubCommand {
    public WaitingPos(final ParentCommand parentCommand, final String s) {
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
            commandSender.sendMessage("§c \u25aa §7No estás en una sesión de setup.");
            return true;
        }
        if (array.length == 0) {
            player.sendMessage("§c\u25aa §7Uso: /bw " + this.getSubCommandName() + " 1 o 2");
        }
        else if (array[0].equalsIgnoreCase("1") || array[0].equalsIgnoreCase("2")) {
            player.sendMessage("§6 \u25aa §7Pos " + array[0] + " seteada");
            session.getConfig().saveArenaLoc("waiting.Pos" + array[0], player.getLocation());
            session.getConfig().reload();
            if (session.getConfig().getYml().get("waiting.Pos1") == null) {
                player.sendMessage("§c \u25aa §7Setea la otra posición:");
                player.spigot().sendMessage(Misc.msgHoverClick("§c \u25aa §7/bw waitingPos 1", "§dSetear pos 1", "/" + this.getParent().getName() + " waitingPos 1", ClickEvent.Action.RUN_COMMAND));
            } else if (session.getConfig().getYml().get("waiting.Pos2") == null) {
                player.sendMessage("§c \u25aa §7Setear la posición restante:");
                player.spigot().sendMessage(Misc.msgHoverClick("§c \u25aa §7/bw waitingPos 2", "§dSetear pos 2", "/" + this.getParent().getName() + " waitingPos 2", ClickEvent.Action.RUN_COMMAND));
            }
        } else {
            player.sendMessage("§c\u25aa §7Uso: /bw " + this.getSubCommandName() + " 1 o 2");
        }
        if (session.getConfig().getYml().get("waiting.Pos1") != null && session.getConfig().getYml().get("waiting.Pos2") != null) {
            Bukkit.dispatchCommand(player, "bw cmds");
            commandSender.sendMessage("§6 \u25aa §7Setea el spawn de teams si no lo has hecho.");
        }
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        return Arrays.asList("1", "2");
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BedWarsAPI bedWarsAPI) {
        return !(commandSender instanceof ConsoleCommandSender) && SetupSession.isInSetupSession(((Player)commandSender).getUniqueId()) && this.hasPermission(commandSender);
    }
}
