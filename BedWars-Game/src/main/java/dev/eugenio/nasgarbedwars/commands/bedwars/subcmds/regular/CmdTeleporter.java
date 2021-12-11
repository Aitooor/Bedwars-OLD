package dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.regular;

import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.arena.SetupSession;
import dev.eugenio.nasgarbedwars.arena.spectator.TeleporterGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdTeleporter extends SubCommand {
    public CmdTeleporter(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.showInList(false);
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) return false;
        final Player player = (Player)commandSender;
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) return false;
        if (!arenaByPlayer.isSpectator(player)) return false;
        TeleporterGUI.openGUI(player);
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
        return arenaByPlayer != null && arenaByPlayer.isSpectator(player) && !SetupSession.isInSetupSession(player.getUniqueId()) && this.hasPermission(commandSender);
    }
}
