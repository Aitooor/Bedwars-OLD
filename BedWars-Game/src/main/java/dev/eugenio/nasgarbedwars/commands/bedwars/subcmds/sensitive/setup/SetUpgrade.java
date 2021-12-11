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

public class SetUpgrade extends SubCommand {
    public SetUpgrade(final ParentCommand parentCommand, final String s) {
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
            final String nearestTeam = session.getNearestTeam();
            if (nearestTeam.isEmpty()) {
                player.sendMessage("");
                player.sendMessage(session.getPrefix() + ChatColor.RED + "No se ha podido encontrar a ningún equipo cerca..");
                player.spigot().sendMessage(dev.eugenio.nasgarbedwars.arena.Misc.msgHoverClick(session.getPrefix() + "Asegúrate de haber puesto el spawn del equipo antes.", ChatColor.WHITE + "Set a team spawn.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
                player.spigot().sendMessage(dev.eugenio.nasgarbedwars.arena.Misc.msgHoverClick(session.getPrefix() + "O si lo has hecho pero no lo ha encontrado automáticamente: /bw " + this.getSubCommandName() + " <equipo>", "Setear el NPC de upgrades para el equipo.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
                BedWars.getInstance().getNms().sendTitle(player, " ", ChatColor.RED + "No se ha encontrado un equipo", 5, 60, 5);
                Sounds.playSound("shop-insufficient-money", player);
            } else {
                Bukkit.dispatchCommand(commandSender, this.getParent().getName() + " " + this.getSubCommandName() + " " + nearestTeam);
            }
        } else if (session.getConfig().getYml().get("Team." + array[0]) == null) {
            player.sendMessage(session.getPrefix() + ChatColor.RED + "Ese equipo no existe.");
            if (session.getConfig().getYml().get("Team") != null) {
                player.sendMessage(session.getPrefix() + "Equipos disponibles: ");
                for (final String s : Objects.requireNonNull(session.getConfig().getYml().getConfigurationSection("Team")).getKeys(false)) player.spigot().sendMessage(dev.eugenio.nasgarbedwars.arena.Misc.msgHoverClick(ChatColor.GOLD + " " + '\u25aa' + " " + session.getTeamColor(s) + s + " " + ChatColor.getLastColors(session.getPrefix()) + "(click para establecer)", ChatColor.WHITE + "NPC de mejoras seteado para el equipo " + TeamColor.getChatColor(Objects.requireNonNull(session.getConfig().getYml().getString("Team." + s + ".Color"))) + s, "/bw setUpgrade " + s, ClickEvent.Action.RUN_COMMAND));
            }
        } else {
            final String string = session.getTeamColor(array[0]) + array[0];
            if (session.getConfig().getYml().get("Team." + array[0] + ".Upgrade") != null) Misc.removeArmorStand("upgrade", session.getConfig().getArenaLoc("Team." + array[0] + ".Upgrade"), null);
            Misc.createArmorStand(string + " " + ChatColor.GOLD + "UPGRADE SETEADO", player.getLocation(), null);
            session.getConfig().saveArenaLoc("Team." + array[0] + ".Upgrade", player.getLocation());
            player.sendMessage(session.getPrefix() + "NPC de upgrade seteado para: " + string);
            if (session.getSetupType() == SetupType.ASSISTED) Bukkit.dispatchCommand(player, this.getParent().getName());
        }
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        return new ArrayList<>();
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BedWarsAPI bedWarsAPI) {
        return !(commandSender instanceof ConsoleCommandSender) && SetupSession.isInSetupSession(((Player)commandSender).getUniqueId()) && this.hasPermission(commandSender);
    }
}
