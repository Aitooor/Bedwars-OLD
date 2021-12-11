package dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.sensitive.setup;

import dev.eugenio.nasgarbedwars.configuration.Permissions;
import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.arena.team.TeamColor;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.api.server.SetupType;
import dev.eugenio.nasgarbedwars.arena.SetupSession;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CreateTeam extends SubCommand {
    public CreateTeam(final ParentCommand parentCommand, final String s) {
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
            commandSender.sendMessage("§c ♦ §7No estás en una sesión de setup.");
            return true;
        }
        if (array.length < 2) {
            player.sendMessage("§c ♦ §7Uso: /" + "bw" + " createTeam §o<nombre> §o<color>");
            final StringBuilder sb = new StringBuilder("§7");
            for (final TeamColor teamColor : TeamColor.values()) sb.append(teamColor.chat()).append(teamColor.toString()).append(ChatColor.GRAY).append(", ");
            player.sendMessage("§6 ♦ §7Colores disponibles: " + sb.substring(0, sb.toString().length() - 2) + ChatColor.GRAY + ".");
        } else {
            boolean b = true;
            final TeamColor[] values2 = TeamColor.values();
            for (int length2 = values2.length, j = 0; j < length2; ++j) {
                if (values2[j].toString().equalsIgnoreCase(array[1])) {
                    b = false;
                }
            }
            if (b) {
                player.sendMessage("§c ♦ §7Color inválido.");
                final StringBuilder sb2 = new StringBuilder("§7");
                for (final TeamColor teamColor2 : TeamColor.values()) sb2.append(teamColor2.chat()).append(teamColor2.toString()).append(ChatColor.GRAY).append(", ");
                player.sendMessage("§6 ♦ §7Colores disponible " + sb2.substring(0, sb2.toString().length() - 2) + ChatColor.GRAY + ".");
            } else {
                if (session.getConfig().getYml().get("Team." + array[0] + ".Color") != null) {
                    player.sendMessage("§c ♦ §7El equipo " + array[0] + " ya existe.");
                    return true;
                }
                session.getConfig().set("Team." + array[0] + ".Color", array[1].toUpperCase());
                player.sendMessage("§6 ♦ §7Equipo " + TeamColor.getChatColor(array[1]) + array[0] + " §7creado.");
                if (session.getSetupType() == SetupType.ASSISTED) {
                    session.getConfig().reload();
                    final int size = session.getConfig().getYml().getConfigurationSection("Team").getKeys(false).size();
                    int n = 1;
                    if (size == 4) n = 2;
                    session.getConfig().set("maxInTeam", n);
                }
            }
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
