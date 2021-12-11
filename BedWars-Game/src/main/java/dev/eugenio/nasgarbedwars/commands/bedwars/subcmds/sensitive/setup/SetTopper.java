package dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.sensitive.setup;

import dev.eugenio.nasgarbedwars.commands.Misc;
import dev.eugenio.nasgarbedwars.configuration.Permissions;
import dev.eugenio.nasgarbedwars.configuration.Sounds;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.api.server.SetupType;
import dev.eugenio.nasgarbedwars.arena.SetupSession;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetTopper extends SubCommand {
    public SetTopper(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setArenaSetupCommand(true);
        this.setPermission(Permissions.PERMISSION_SETUP_ARENA);
    }

    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) return false;
        final Player player = (Player) commandSender;
        final SetupSession session = SetupSession.getSession(player.getUniqueId());
        if (session == null) return false;
        if (array.length == 0 && session.getSetupType() == SetupType.ASSISTED) {
            final String nearestTeam = session.getFarNearestTeam();
            if (!nearestTeam.isEmpty()) {
                saveTeamTopper(player.getLocation(), nearestTeam, session, "Topper", player);
                Misc.createArmorStand(ChatColor.GOLD + "Topper seteado para equipo: " + session.getTeamColor(nearestTeam) + nearestTeam, player.getLocation(), session.getConfig().stringLocationArenaFormat(player.getLocation()));
                player.sendMessage(session.getPrefix() + "Topper seteado para equipo: " + session.getTeamColor(nearestTeam) + nearestTeam);
                Bukkit.dispatchCommand(player, this.getParent().getName());
                BedWars.getInstance().getNms().sendTitle(player, " ", ChatColor.GREEN + "Topper seteado para equipo: " + session.getTeamColor(nearestTeam) + nearestTeam, 5, 60, 5);
                Sounds.playSound("shop-bought", player);
                return true;
            }
            player.sendMessage(session.getPrefix() + ChatColor.RED + "No se ha podido encontrar ningún equipo cerca. ¿Estás seguro de que lo estás haciendo en una isla?");
            player.spigot().sendMessage(dev.eugenio.nasgarbedwars.arena.Misc.msgHoverClick(session.getPrefix() + "Si lo estás, ¡asegúrate que hayas puesto primero el spawn del equipo!", ChatColor.WHITE + "Setea el spawn del equipo.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
            player.spigot().sendMessage(dev.eugenio.nasgarbedwars.arena.Misc.msgHoverClick(session.getPrefix() + "Si ya lo has hecho y aún así no lo ha encontrado, puedes usar /bw setTopper", "Añade un topper del equipo.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
            BedWars.getInstance().getNms().sendTitle(player, " ", ChatColor.RED + "No hay equipos cerca.", 5, 60, 5);
            Sounds.playSound("shop-insufficient-money", player);
            return true;
        }
        return false;
    }
    
    @Override
    public List<String> getTabComplete() {
        return null;
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BedWarsAPI bedWarsAPI) {
        return !(commandSender instanceof ConsoleCommandSender) && SetupSession.isInSetupSession(((Player)commandSender).getUniqueId()) && this.hasPermission(commandSender);
    }
    
    private static void saveTeamTopper(final Location location, final String s, final SetupSession setupSession, final String s2, final Player player) {
        String locToConfig = setupSession.getConfig().stringLocationArenaFormat(location);
        setupSession.getConfig().set("Team." + s + "." + s2, locToConfig);
        setupSession.getConfig().set("Team." + s + "." + s2 + "Face.yaw", (int) player.getLocation().getYaw());
        setupSession.getConfig().set("Team." + s + "." + s2 + "Face.pitch", (int) player.getLocation().getPitch());
        BedWars.debug(locToConfig);
    }
}
