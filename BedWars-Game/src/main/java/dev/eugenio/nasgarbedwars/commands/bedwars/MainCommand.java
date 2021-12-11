package dev.eugenio.nasgarbedwars.commands.bedwars;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.arena.SetupSession;
import dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.regular.*;
import dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.sensitive.*;
import dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.sensitive.setup.*;
import dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.regular.*;
import dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.sensitive.*;
import dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.sensitive.setup.*;
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
        new CmdLeave(this, "leave");
        new CmdTeleporter(this, "teleporter");
        new CmdStart(this, "forceStart");
        new CmdStart(this, "start");
        new SetupArena(this, "setupArena");
        new DelArena(this, "delArena");
        new EnableArena(this, "enableArena");
        new DisableArena(this, "disableArena");
        new CloneArena(this, "cloneArena");
        new ArenaGroup(this, "arenaGroup");
        new Level(this, "level");
        new Reload(this, "reload");
        new AutoCreateTeams(this, "autoCreateTeams");
        new SetWaitingSpawn(this, "setWaitingSpawn");
        new SetSpectatorPos(this, "setSpectSpawn");
        new CreateTeam(this, "createTeam");
        new WaitingPos(this, "waitingPos");
        new RemoveTeam(this, "removeTeam");
        new SetMaxInTeam(this, "setMaxInTeam");
        new SetSpawn(this, "setSpawn");
        new SetBed(this, "setBed");
        new SetShop(this, "setShop");
        new SetUpgrade(this, "setUpgrade");
        new AddGenerator(this, "addGenerator");
        new RemoveGenerator(this, "removeGenerator");
        new SetType(this, "setType");
        new Save(this, "save");
        new CmdUpgrades(this, "upgradesmenu");
        new SetKillDropsLoc(this, "setKillDrops");
        new SetTopper(this, "setTopper");
        new CmdList(this, "cmds");
    }
    
    public boolean execute(final CommandSender commandSender, final String s, final String[] array) {
        if (array.length == 0) {
            if (commandSender.isOp() || commandSender.hasPermission("bedwars.*")) {
                if (commandSender instanceof Player) {
                    if (SetupSession.isInSetupSession(((Player) commandSender).getUniqueId())) {
                        Bukkit.dispatchCommand(commandSender, this.getName() + " cmds");
                    } else {
                        commandSender.sendMessage("");
                        commandSender.sendMessage("§aBedWars v" + BedWars.getInstance().getDescription().getVersion() + " §8- §e Comandos administrativos");
                        commandSender.sendMessage("");
                        this.sendSubCommands((Player) commandSender);
                    }
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
        for (int i = 0; i <= 20; ++i) for (final SubCommand subCommand : this.getSubCommands()) if (subCommand.getPriority() == i && subCommand.isShow() && subCommand.canSee(player, BedWars.getInstance().getApi())) player.spigot().sendMessage(subCommand.getDisplayInfo());
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
