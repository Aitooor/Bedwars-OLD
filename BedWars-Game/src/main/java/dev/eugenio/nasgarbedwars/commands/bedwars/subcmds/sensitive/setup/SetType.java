package dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.sensitive.setup;

import dev.eugenio.nasgarbedwars.configuration.Permissions;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.api.server.SetupType;
import dev.eugenio.nasgarbedwars.arena.Misc;
import dev.eugenio.nasgarbedwars.arena.SetupSession;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class SetType extends SubCommand {
    private static final List<String> available;
    
    public SetType(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setArenaSetupCommand(true);
        this.setPermission(Permissions.PERMISSION_SETUP_ARENA);
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) return false;
        final Player player = (Player)commandSender;
        final SetupSession session = SetupSession.getSession(player.getUniqueId());
        if (session == null) {
            commandSender.sendMessage("§c \u25aa §7No estás en una sesión de setup.");
            return true;
        }
        if (array.length == 0) {
            this.sendUsage(player);
        } else {
            if (!SetType.available.contains(array[0])) {
                this.sendUsage(player);
                return true;
            }
            final List<String> stringList = BedWars.getInstance().getMainConfig().getYml().getStringList("arenaGroups");
            final String string = array[0].substring(0, 1).toUpperCase() + array[0].substring(1).toLowerCase();
            if (!stringList.contains(string)) {
                stringList.add(string);
                BedWars.getInstance().getMainConfig().set("arenaGroups", stringList);
            }
            if (string.equals("Solo")) {
                session.getConfig().set("maxInTeam", 1);
            } else if (string.equalsIgnoreCase("Doubles")) {
                session.getConfig().set("maxInTeam", 2);
            } else if (string.equalsIgnoreCase("3v3v3v3")) {
                session.getConfig().set("maxInTeam", 3);
            } else if (string.equalsIgnoreCase("4v4v4v4")) {
                session.getConfig().set("maxInTeam", 4);
            }
            session.getConfig().set("group", string);
            player.sendMessage("§6 \u25aa §7Grupo de arena cambiado a: §d" + string);
            if (session.getSetupType() == SetupType.ASSISTED) Bukkit.dispatchCommand(player, this.getParent().getName());
        }
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        List<String> list = BedWars.getInstance().getMainConfig().getYml().getStringList("arenaGroups");
        available.forEach(paramString -> {
            if (!list.contains(paramString)) list.add(paramString);
        });
        return BedWars.getInstance().getMainConfig().getYml().getStringList("arenaGroups");
    }
    
    private void sendUsage(final Player player) {
        player.sendMessage("§9 \u25aa §7Uso: " + this.getParent().getName() + " " + this.getSubCommandName() + " <tipo>");
        player.sendMessage("§9Tipos disponibles: ");
        for (final String s : SetType.available) {
            player.spigot().sendMessage(Misc.msgHoverClick("§1 \u25aa §e" + s + " §7(click para setear)", "§dClick para hacer la arena " + s, "/" + this.getParent().getName() + " " + this.getSubCommandName() + " " + s, ClickEvent.Action.RUN_COMMAND));
        }
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BedWarsAPI bedWarsAPI) {
        return !(commandSender instanceof ConsoleCommandSender) && SetupSession.isInSetupSession(((Player)commandSender).getUniqueId()) && this.hasPermission(commandSender);
    }
    
    static {
        available = Arrays.asList("Solo", "Doubles", "3v3v3v3", "4v4v4v4");
    }
}
