package dev.eugenio.nasgarbedwars.commands.bedwars;

import dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.sensitive.SetNPCPos;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainCommand extends BukkitCommand implements ParentCommand {
    private static final List<SubCommand> subCommandList = new ArrayList<>();
    private static MainCommand instance;
    
    public MainCommand(final String s) {
        super(s);
        this.setAliases(Arrays.asList("bedwars", "bw"));
        MainCommand.instance = this;
        new SetNPCPos(this, "setnpcpos");
    }
    
    public boolean execute(final CommandSender commandSender, final String s, final String[] array) {
        if (array.length == 0) {
            if (commandSender.isOp() || commandSender.hasPermission("bedwars.*")) {
                if (commandSender instanceof Player) {
                        commandSender.sendMessage("");
                        commandSender.sendMessage("§aBedWars v" + BedWars.getInstance().getDescription().getVersion() + " §8- §e Comandos administrativos");
                        commandSender.sendMessage("");
                        this.sendSubCommands((Player)commandSender);
                }
            } else {
                if (commandSender instanceof ConsoleCommandSender) {
                    commandSender.sendMessage("§cNo hay comandos de consola por el momento.");
                    return true;
                }
                Bukkit.dispatchCommand(commandSender, "bw cmds");
            }
            return true;
        }
        boolean execute = false;
        for (final SubCommand subCommand : this.getSubCommands()) if (subCommand.getSubCommandName().equalsIgnoreCase(array[0]) && subCommand.hasPermission(commandSender)) execute = subCommand.execute(Arrays.copyOfRange(array, 1, array.length), commandSender);
        if (!execute) {
            if (commandSender instanceof Player) {
                commandSender.sendMessage(Language.getMsg((Player)commandSender, Messages.COMMAND_NOT_FOUND_OR_INSUFF_PERMS));
            } else {
                commandSender.sendMessage(Language.getDefaultLanguage().m(Messages.COMMAND_NOT_FOUND_OR_INSUFF_PERMS));
            }
        }
        return true;
    }

    public static TextComponent createTC(final String s, final String s2, final String s3) {
        final TextComponent textComponent = new TextComponent(s);
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, s2));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(s3).create()));
        return textComponent;
    }
    
    public void addSubCommand(final SubCommand subCommand) {
        MainCommand.subCommandList.add(subCommand);
    }
    
    public void sendSubCommands(final Player player) {
        for (int i = 0; i <= 20; ++i) for (final SubCommand subCommand : this.getSubCommands()) if (subCommand.getPriority() == i && subCommand.isShow()) player.spigot().sendMessage(subCommand.getDisplayInfo());
    }
    
    public List<SubCommand> getSubCommands() {
        return MainCommand.subCommandList;
    }
    
    public static MainCommand getInstance() {
        return MainCommand.instance;
    }
    
    public boolean hasSubCommand(final String s) {
        for (SubCommand subCommand : this.getSubCommands()) if (subCommand.getSubCommandName().equalsIgnoreCase(s)) return true;
        return false;
    }
}
