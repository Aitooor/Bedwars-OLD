package dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.sensitive;

import dev.eugenio.nasgarbedwars.configuration.Permissions;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.arena.Misc;
import dev.eugenio.nasgarbedwars.arena.SetupSession;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Reload extends SubCommand {
    public Reload(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(11);
        this.showInList(true);
        this.setPermission(Permissions.PERMISSION_RELOAD);
        this.setDisplayInfo(Misc.msgHoverClick("§6 \u25aa §7/" + this.getParent().getName() + " " + this.getSubCommandName() + "       §8 - §ereload messages", "§fRealod messages.\n§cNot recommended!", "/" + this.getParent().getName() + " " + this.getSubCommandName(), ClickEvent.Action.RUN_COMMAND));
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) return false;
        final Player player = (Player)commandSender;
        for (final Language language : Language.getLanguages()) {
            language.reload();
            player.sendMessage("§6 \u25aa §7" + language.getLangName() + " recargado.");
        }
        BedWars.getInstance().getConfigData().reload();
        player.sendMessage("§6 \u25aa §7Brújula recargada.");
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
        return !Arena.isInArena(player) && !SetupSession.isInSetupSession(player.getUniqueId()) && this.hasPermission(commandSender);
    }
}
