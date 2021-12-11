package dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.regular;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.commands.bedwars.MainCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdLang extends SubCommand {
    public CmdLang(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(18);
        this.showInList(true);
        this.setDisplayInfo(MainCommand.createTC("§6 ♦ §e/" + MainCommand.getInstance().getName() + " " + this.getSubCommandName(), "/" + this.getParent().getName() + " " + this.getSubCommandName(), " §8- §aCambia tu idioma a otro."));
    }

    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) return false;
        final Player player = (Player) commandSender;
        if (array.length == 0) {
            player.sendMessage(Language.getMsg(player, Messages.COMMAND_LANG_LIST_HEADER));
            for (Language language : Language.getLanguages())
                player.sendMessage(Language.getMsg(player, Messages.COMMAND_LANG_LIST_FORMAT).replace("{iso}", language.getIso()).replace("{name}", language.getLangName()));
            player.sendMessage(Language.getMsg(player, Messages.COMMAND_LANG_USAGE));
            return true;
        }
        if (Language.isLanguageExist(array[0])) {
            if (Language.setPlayerLanguage(player.getUniqueId(), array[0])) {
                Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> player.sendMessage(Language.getMsg(player, Messages.COMMAND_LANG_SELECTED_SUCCESSFULLY)), 3L);
            } else {
                player.sendMessage(Language.getMsg(player, Messages.COMMAND_LANG_LIST_HEADER));
                for (Language language : Language.getLanguages())
                    player.sendMessage(Language.getMsg(player, Messages.COMMAND_LANG_LIST_FORMAT).replace("{iso}", language.getIso()).replace("{name}", language.getLangName()));
                player.sendMessage(Language.getMsg(player, Messages.COMMAND_LANG_USAGE));
                return true;
            }
        } else {
            player.sendMessage(Language.getMsg(player, Messages.COMMAND_LANG_SELECTED_NOT_EXIST));
        }
        return true;
    }

    @Override
    public List<String> getTabComplete() {
        final ArrayList<String> list = new ArrayList<>();
        for (Language language : Language.getLanguages()) list.add(language.getIso());
        return list;
    }
}
