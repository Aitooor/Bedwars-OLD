package dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.sensitive.setup;

import dev.eugenio.nasgarbedwars.commands.Misc;
import dev.eugenio.nasgarbedwars.configuration.Permissions;
import dev.eugenio.nasgarbedwars.configuration.Sounds;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.arena.team.TeamColor;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.api.server.SetupType;
import dev.eugenio.nasgarbedwars.arena.SetupSession;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SetShop extends SubCommand {
    public SetShop(final ParentCommand parentCommand, final String s) {
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
        if (array.length == 0) {
            final String nearestTeam = session.getNearestTeam();
            if (nearestTeam.isEmpty()) {
                player.sendMessage("");
                player.sendMessage(session.getPrefix() + ChatColor.RED + "No se ha podido encontrar un equipo cerca.");
                player.spigot().sendMessage(dev.eugenio.nasgarbedwars.arena.Misc.msgHoverClick(session.getPrefix() + "Asegúrate de que has seteado el spawn del equipo primero.", ChatColor.WHITE + "Setea el spawn del equipo.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
                player.spigot().sendMessage(dev.eugenio.nasgarbedwars.arena.Misc.msgHoverClick(session.getPrefix() + "O si lo has establecido pero no se ha encontrado usa: /bw " + this.getSubCommandName() + " <equipo>", "Setea la tienda del equipo.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
                player.spigot().sendMessage(dev.eugenio.nasgarbedwars.arena.Misc.msgHoverClick(session.getPrefix() + "Otro uso: /bw setShop <equipo>", "Setea la tienda del equipo.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
                BedWars.getInstance().getNms().sendTitle(player, " ", ChatColor.RED + "Ningún equipo cerca", 5, 60, 5);
                Sounds.playSound("shop-insufficient-money", player);
            } else {
                Bukkit.dispatchCommand(commandSender, this.getParent().getName() + " " + this.getSubCommandName() + " " + nearestTeam);
            }
        } else if (session.getConfig().getYml().get("Team." + array[0]) == null) {
            player.sendMessage(session.getPrefix() + ChatColor.RED + "Ese equipo no existe.");
            if (session.getConfig().getYml().get("Team") != null) {
                player.sendMessage(session.getPrefix() + "Equipos disponibles: ");
                for (final String s : Objects.requireNonNull(session.getConfig().getYml().getConfigurationSection("Team")).getKeys(false)) player.spigot().sendMessage(dev.eugenio.nasgarbedwars.arena.Misc.msgHoverClick(ChatColor.GOLD + " " + '\u25aa' + " " + session.getTeamColor(s) + s + ChatColor.GRAY + " (click para setear)", ChatColor.GRAY + "Setear tienda para " + TeamColor.getChatColor(Objects.requireNonNull(session.getConfig().getYml().getString("Team." + s + ".Color"))) + s, "/bw setShop " + s, ClickEvent.Action.RUN_COMMAND));
            }
        }
        else {
            final String string = session.getTeamColor(array[0]) + array[0];
            if (session.getConfig().getYml().get("Team." + array[0] + ".Shop") != null) Misc.removeArmorStand("shop", session.getConfig().getArenaLoc("Team." + array[0] + ".Shop"), session.getConfig().getString("Team." + array[0] + ".Shop"));
            Misc.createArmorStand(string + " " + ChatColor.GOLD + "SHOP SETEADA", player.getLocation(), session.getConfig().stringLocationArenaFormat(player.getLocation()));
            session.getConfig().saveArenaLoc("Team." + array[0] + ".Shop", player.getLocation());
            player.sendMessage(session.getPrefix() + "Shop seteada para: " + string);
            if (session.getSetupType() == SetupType.ASSISTED) Bukkit.dispatchCommand(player, this.getParent().getName());
        }
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        return new ArrayList<String>();
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BedWarsAPI bedWarsAPI) {
        return !(commandSender instanceof ConsoleCommandSender) && SetupSession.isInSetupSession(((Player)commandSender).getUniqueId()) && this.hasPermission(commandSender);
    }
}
