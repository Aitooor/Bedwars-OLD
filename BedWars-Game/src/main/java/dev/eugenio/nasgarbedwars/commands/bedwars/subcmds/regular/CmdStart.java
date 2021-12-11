package dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.regular;

import dev.eugenio.nasgarbedwars.commands.bedwars.MainCommand;
import dev.eugenio.nasgarbedwars.configuration.Permissions;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.arena.GameStatus;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.arena.SetupSession;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdStart extends SubCommand {
    public CmdStart(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(15);
        this.showInList(true);
        this.setDisplayInfo(MainCommand.createTC("§6 ♦ §e/" + MainCommand.getInstance().getName() + " " + this.getSubCommandName() + " §8-  §aempieza forzadamente una arena", "/" + this.getParent().getName() + " " + this.getSubCommandName(), "§fempieza forzadamente una arena"));
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) return false;
        final Player player = (Player)commandSender;
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) {
            player.sendMessage(Language.getMsg(player, Messages.COMMAND_FORCESTART_NOT_IN_GAME));
            return true;
        }
        if (!arenaByPlayer.isPlayer(player)) {
            player.sendMessage(Language.getMsg(player, Messages.COMMAND_FORCESTART_NOT_IN_GAME));
            return true;
        }
        if (!player.hasPermission(Permissions.PERMISSION_ALL) && !player.hasPermission(Permissions.PERMISSION_FORCESTART)) {
            player.sendMessage(Language.getMsg(player, Messages.COMMAND_FORCESTART_NO_PERM));
            return true;
        }
        if (arenaByPlayer.getStatus() == GameStatus.playing) return true;
        if (arenaByPlayer.getStatus() == GameStatus.restarting) return true;
        if (arenaByPlayer.getStartingTask() == null) {
            if (array.length != 1 || !array[0].equalsIgnoreCase("debug") || !commandSender.isOp()) return true;
            arenaByPlayer.changeStatus(GameStatus.starting);
            BedWars.debug = true;
        }
        if (arenaByPlayer.getStartingTask().getCountdown() < 5) return true;
        arenaByPlayer.getStartingTask().setCountdown(5);
        player.sendMessage(Language.getMsg(player, Messages.COMMAND_FORCESTART_SUCCESS));
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        return null;
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BedWarsAPI bedWarsAPI) {
        if (commandSender instanceof ConsoleCommandSender) return false;
        final Player player = (Player)commandSender;
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        return arenaByPlayer != null && (arenaByPlayer.getStatus() == GameStatus.waiting || arenaByPlayer.getStatus() == GameStatus.starting) && arenaByPlayer.isPlayer(player) && !SetupSession.isInSetupSession(player.getUniqueId()) && commandSender.hasPermission(Permissions.PERMISSION_FORCESTART);
    }
}
