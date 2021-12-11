package dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.regular;

import dev.eugenio.nasgarbedwars.commands.bedwars.MainCommand;
import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.arena.Misc;
import dev.eugenio.nasgarbedwars.arena.SetupSession;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CmdLeave extends SubCommand {
    private static HashMap<UUID, Long> delay;
    
    public CmdLeave(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(20);
        this.showInList(false);
        this.setDisplayInfo(MainCommand.createTC("§6 ♦ §e/" + MainCommand.getInstance().getName() + " leave", "/" + this.getParent().getName() + " " + this.getSubCommandName(), " §8- §aDeja la partida."));
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) return false;
        final Player player = (Player)commandSender;
        if (cancel(player.getUniqueId())) return true;
        update(player.getUniqueId());
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        Misc.moveToLobbyOrKick(player, arenaByPlayer, arenaByPlayer != null && arenaByPlayer.isSpectator(player.getUniqueId()));
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
        return !SetupSession.isInSetupSession(player.getUniqueId()) && this.hasPermission(commandSender);
    }
    
    private static boolean cancel(final UUID uuid) {
        return CmdLeave.delay.getOrDefault(uuid, 0L) > System.currentTimeMillis();
    }
    
    private static void update(final UUID uuid) {
        if (CmdLeave.delay.containsKey(uuid)) {
            CmdLeave.delay.replace(uuid, System.currentTimeMillis() + 2500L);
            return;
        }
        CmdLeave.delay.put(uuid, System.currentTimeMillis() + 2500L);
    }
    
    static {
        CmdLeave.delay = new HashMap<>();
    }
}
