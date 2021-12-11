package dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.regular;

import dev.eugenio.nasgarbedwars.upgrades.UpgradesManager;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.arena.Arena;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdUpgrades extends SubCommand {
    public CmdUpgrades(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.showInList(false);
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (!(commandSender instanceof Player)) return false;
        final IArena arenaByPlayer = Arena.getArenaByPlayer((Player)commandSender);
        if (arenaByPlayer == null) return false;
        if (!arenaByPlayer.isPlayer((Player)commandSender)) return false;
        if (arenaByPlayer.getTeam((Player)commandSender).getTeamUpgrades().distance(((Player)commandSender).getLocation()) < 4.0) {
            UpgradesManager.getMenuForArena(arenaByPlayer).open((Player)commandSender);
            return true;
        }
        return false;
    }
    
    @Override
    public List<String> getTabComplete() {
        return new ArrayList<>();
    }
}
