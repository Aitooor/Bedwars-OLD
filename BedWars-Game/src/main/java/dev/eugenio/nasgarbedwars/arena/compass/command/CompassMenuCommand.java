package dev.eugenio.nasgarbedwars.arena.compass.command;

import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.GameStatus;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.arena.compass.menus.MainMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CompassMenuCommand extends SubCommand {
    public CompassMenuCommand(ParentCommand parent, String name) {
        super(parent, name);
        setArenaSetupCommand(false);
    }

    @Override
    public boolean execute(String[] args, CommandSender sender) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            if (!BedWars.getInstance().getApi().getArenaUtil().isPlaying(player)) return true;
            final IArena arena = BedWars.getInstance().getApi().getArenaUtil().getArenaByPlayer(player);
            if (!arena.getStatus().equals(GameStatus.playing)) return true;
            if (arena.isSpectator(player)) return true;
            new MainMenu(BedWars.getInstance().getPlayerMenuUtility(player)).open();
        }
        return true;
    }

    @Override
    public List<String> getTabComplete() {
        return null;
    }
}