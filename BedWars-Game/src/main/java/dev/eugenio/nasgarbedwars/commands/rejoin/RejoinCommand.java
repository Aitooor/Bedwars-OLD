package dev.eugenio.nasgarbedwars.commands.rejoin;

import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.arena.ReJoin;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class RejoinCommand extends BukkitCommand {
    public RejoinCommand(final String s) {
        super(s);
    }
    
    public boolean execute(final CommandSender commandSender, final String s, final String[] array) {
        if (commandSender instanceof ConsoleCommandSender) {
            commandSender.sendMessage("Este comando es para jugadores.");
            return true;
        }
        final Player player = (Player)commandSender;
        final ReJoin player2 = ReJoin.getPlayer(player);
        if (player2 == null) {
            player.sendMessage(Language.getMsg(player, Messages.REJOIN_NO_ARENA));
            return true;
        }
        if (!player2.canReJoin()) {
            player.sendMessage(Language.getMsg(player, Messages.REJOIN_DENIED));
            return true;
        }
        player.sendMessage(Language.getMsg(player, Messages.REJOIN_ALLOWED).replace("{arena}", player2.getArena().getDisplayName()));
        player2.reJoin(player);
        return true;
    }
}
