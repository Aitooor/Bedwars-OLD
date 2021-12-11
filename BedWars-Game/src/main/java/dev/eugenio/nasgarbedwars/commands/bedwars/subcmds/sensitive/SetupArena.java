package dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.sensitive;

import dev.eugenio.nasgarbedwars.commands.bedwars.MainCommand;
import dev.eugenio.nasgarbedwars.configuration.Permissions;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.arena.Misc;
import dev.eugenio.nasgarbedwars.arena.SetupSession;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetupArena extends SubCommand {
    public SetupArena(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(2);
        this.setDisplayInfo(Misc.msgHoverClick("§6 \u25aa §7/" + MainCommand.getInstance().getName() + " setupArena §6<mundo>", "§fCrea o edita una arena.\n'_' and '-' no serán mostrados como nombre de la arena.", "/" + MainCommand.getInstance().getName() + " setupArena ", ClickEvent.Action.SUGGEST_COMMAND));
        this.showInList(true);
        this.setPermission(Permissions.PERMISSION_SETUP_ARENA);
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) return false;
        final Player player = (Player)commandSender;
        if (array.length != 1) {
            player.sendMessage("§c\u25aa §7Uso: §o/" + this.getParent().getName() + " " + this.getSubCommandName() + " <mundo>");
            return true;
        }
        if (!array[0].equals(array[0].toLowerCase())) {
            player.sendMessage("§c\u25aa §c" + array[0] + ChatColor.GRAY + " no debe de contener letras mayúsculas. Renombra la carpeta a: " + ChatColor.GREEN + array[0].toLowerCase());
            return true;
        }
        if (array[0].contains("+")) {
            player.sendMessage("§c\u25aa §7" + array[0] + " no debe de contener este símbolo: " + ChatColor.RED + "+");
            return true;
        }
        if (Arena.getArenaByName(array[0]) != null) {
            player.sendMessage("§c\u25aa §7Deshabilita la arena primero.");
            return true;
        }
        if (SetupSession.isInSetupSession(player.getUniqueId())) {
            player.sendMessage("§c \u25aa §7Ya estás en una sesión de setup.");
            return true;
        }
        new SetupSession(player, array[0]);
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        return BedWars.getInstance().getApi().getRestoreAdapter().getWorldsList();
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BedWarsAPI bedWarsAPI) {
        if (commandSender instanceof ConsoleCommandSender) return false;
        final Player player = (Player)commandSender;
        return !Arena.isInArena(player) && !SetupSession.isInSetupSession(player.getUniqueId()) && this.hasPermission(commandSender);
    }
}
