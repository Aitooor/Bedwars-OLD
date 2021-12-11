package dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.sensitive.setup;

import dev.eugenio.nasgarbedwars.commands.Misc;
import dev.eugenio.nasgarbedwars.configuration.Permissions;
import dev.eugenio.nasgarbedwars.configuration.Sounds;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.arena.team.TeamColor;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.arena.SetupSession;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class RemoveTeam extends SubCommand {
    public RemoveTeam(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setArenaSetupCommand(true);
        this.setPermission(Permissions.PERMISSION_SETUP_ARENA);
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) return false;
        final Player player = (Player)commandSender;
        final SetupSession session = SetupSession.getSession(player.getUniqueId());
        if (session == null) return false;
        if (array.length < 1) {
            player.sendMessage(session.getPrefix() + ChatColor.RED + "Uso: /bw removeTeam <nombre>");
            if (session.getConfig().getYml().get("Team") != null) {
                player.sendMessage(session.getPrefix() + "Teams disponibles: ");
                for (final String s : Objects.requireNonNull(session.getConfig().getYml().getConfigurationSection("Team")).getKeys(false)) player.spigot().sendMessage(dev.eugenio.nasgarbedwars.arena.Misc.msgHoverClick(ChatColor.GOLD + " " + '\u25aa' + " " + TeamColor.getChatColor(s) + s, ChatColor.GRAY + "Quitar " + TeamColor.getChatColor(s) + s + " " + ChatColor.GRAY + "(click para quitar)", "/bw removeTeam " + s, ClickEvent.Action.RUN_COMMAND));
            }
        } else if (session.getConfig().getYml().get("Team." + array[0] + ".Color") == null) {
            player.sendMessage(session.getPrefix() + "Este equipo no existe: " + array[0]);
            BedWars.getInstance().getNms().sendTitle(player, " ", ChatColor.RED + "Equipo no encontrado: " + array[0], 5, 40, 5);
            Sounds.playSound("shop-insufficient-money", player);
        } else {
            if (session.getConfig().getYml().get("Team." + array[0] + ".Iron") != null) {
                for (Location location : session.getConfig().getArenaLocations("Team." + array[0] + ".Iron")) {
                    Misc.removeArmorStand(null, location, null);
                }
            }
            if (session.getConfig().getYml().get("Team." + array[0] + ".Gold") != null) for (Location location : session.getConfig().getArenaLocations("Team." + array[0] + ".Gold")) Misc.removeArmorStand(null, location, null);
            if (session.getConfig().getYml().get("Team." + array[0] + ".Emerald") != null) for (Location location : session.getConfig().getArenaLocations("Team." + array[0] + ".Emerald")) Misc.removeArmorStand(null, location, null);
            if (session.getConfig().getYml().get("Team." + array[0] + ".Shop") != null) Misc.removeArmorStand(null, session.getConfig().getArenaLoc("Team." + array[0] + ".Shop"), null);
            if (session.getConfig().getYml().get("Team." + array[0] + ".Upgrade") != null) Misc.removeArmorStand(null, session.getConfig().getArenaLoc("Team." + array[0] + ".Upgrade"), null);
            if (session.getConfig().getYml().get("Team." + array[0] + "." + "kill-drops-loc") != null) Misc.removeArmorStand(null, session.getConfig().getArenaLoc("Team." + array[0] + "." + "kill-drops-loc"), null);
            player.sendMessage(session.getPrefix() + "Equipo removido: " + session.getTeamColor(array[0]) + array[0]);
            BedWars.getInstance().getNms().sendTitle(player, " ", ChatColor.GREEN + "Equipo removido: " + session.getTeamColor(array[0]) + array[0], 5, 40, 5);
            Sounds.playSound("shop-bought", player);
            session.getConfig().set("Team." + array[0], null);
        }
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        return null;
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BedWarsAPI bedWarsAPI) {
        return !(commandSender instanceof ConsoleCommandSender) && SetupSession.isInSetupSession(((Player)commandSender).getUniqueId()) && this.hasPermission(commandSender);
    }
}
