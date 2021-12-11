package dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.sensitive;

import dev.eugenio.nasgarbedwars.configuration.Permissions;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.arena.Misc;
import dev.eugenio.nasgarbedwars.arena.SetupSession;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DisableArena extends SubCommand {
    public DisableArena(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(6);
        this.showInList(true);
        this.setDisplayInfo(Misc.msgHoverClick("§6 \u25aa §7/" + this.getParent().getName() + " " + this.getSubCommandName() + " §6<mundo>", "§fDeshabilita una arena. Esto kickeará a los jugadores antes de deshabilitarla.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
        this.setPermission(Permissions.PERMISSION_ARENA_DISABLE);
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) return false;
        final Player player = (Player)commandSender;
        if (array.length != 1) {
            player.sendMessage("§c\u25aa §7Uso: §o/" + this.getParent().getName() + " " + this.getSubCommandName() + " <mundo>");
            return true;
        }
        if (!BedWars.getInstance().getApi().getRestoreAdapter().isWorld(array[0])) {
            player.sendMessage("§c\u25aa §7" + array[0] + " no existe.");
            return true;
        }
        final IArena arenaByName = Arena.getArenaByName(array[0]);
        if (arenaByName == null) {
            player.sendMessage("§c\u25aa §7Esta arena está deshabilitada.");
            return true;
        }
        player.sendMessage("§6 \u25aa §7Deshabilitando arena...");
        arenaByName.disable();
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        final ArrayList<String> list = new ArrayList<>();
        for (IArena iArena : Arena.getArenas()) list.add(iArena.getArenaName());
        return list;
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BedWarsAPI bedWarsAPI) {
        if (commandSender instanceof ConsoleCommandSender) return false;
        final Player player = (Player)commandSender;
        return !Arena.isInArena(player) && !SetupSession.isInSetupSession(player.getUniqueId()) && this.hasPermission(commandSender);
    }
}
