package dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.sensitive.setup;

import dev.eugenio.nasgarbedwars.commands.Misc;
import dev.eugenio.nasgarbedwars.configuration.Permissions;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.arena.SetupSession;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SetSpawn extends SubCommand {
    public SetSpawn(final ParentCommand parentCommand, final String s) {
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
            player.sendMessage(session.getPrefix() + ChatColor.RED + "Uso: /bw setSpawn <team>");
            if (session.getConfig().getYml().get("Team") != null) {
                for (final String s : Objects.requireNonNull(session.getConfig().getYml().getConfigurationSection("Team")).getKeys(false)) {
                    if (session.getConfig().getYml().get("Team." + s + ".Spawn") == null) player.spigot().sendMessage(dev.eugenio.nasgarbedwars.arena.Misc.msgHoverClick(session.getPrefix() + "Setear spawn para: " + session.getTeamColor(s) + s + " " + ChatColor.getLastColors(session.getPrefix()) + "(click to set)", ChatColor.WHITE + "Setear spawn para " + session.getTeamColor(s) + s, "/bw setSpawn " + s, ClickEvent.Action.RUN_COMMAND));
                }
            }
        } else if (session.getConfig().getYml().get("Team." + array[0]) == null) {
            player.sendMessage(session.getPrefix() + ChatColor.RED + "No se ha podido encontrar el equipo: " + ChatColor.RED + array[0]);
            if (session.getConfig().getYml().get("Team") != null) {
                player.sendMessage(session.getPrefix() + "Lista de equipos: ");
                for (final String s2 : Objects.requireNonNull(session.getConfig().getYml().getConfigurationSection("Team")).getKeys(false)) player.spigot().sendMessage(dev.eugenio.nasgarbedwars.arena.Misc.msgHoverClick(ChatColor.GOLD + " " + '\u25aa' + " " + session.getTeamColor(s2) + s2 + " " + ChatColor.getLastColors(session.getPrefix()) + "(click para setear)", ChatColor.WHITE + "Setear spawn para " + session.getTeamColor(s2) + s2, "/bw setSpawn " + s2, ClickEvent.Action.RUN_COMMAND));
            }
        } else {
            if (session.getConfig().getYml().get("Team." + array[0] + ".Spawn") != null) Misc.removeArmorStand("spawn", session.getConfig().getArenaLoc("Team." + array[0] + ".Spawn"), session.getConfig().getString("Team." + array[0] + ".Spawn"));
            session.getConfig().saveArenaLoc("Team." + array[0] + ".Spawn", player.getLocation());
            final String string = session.getTeamColor(array[0]) + array[0];
            player.sendMessage(ChatColor.GOLD + " " + '\u25aa' + " Spawn seteado para: " + string);
            Misc.createArmorStand(string + " " + ChatColor.GOLD + "SPAWN SETEADO", player.getLocation(), session.getConfig().stringLocationArenaFormat(player.getLocation()));
            final int int1 = session.getConfig().getInt("island-radius");
            final Location location = player.getLocation();
            for (int i = -int1; i < int1; ++i) {
                for (int j = -int1; j < int1; ++j) {
                    for (int k = -int1; k < int1; ++k) {
                        final Block block = location.clone().add(i, j, k).getBlock();
                        if (BedWars.getInstance().getNms().isBed(block.getType())) {
                            player.teleport(block.getLocation());
                            Bukkit.dispatchCommand(player, this.getParent().getName() + " setBed " + array[0]);
                            return true;
                        }
                    }
                }
            }
            if (session.getConfig().getYml().get("Team") != null) {
                final StringBuilder sb = new StringBuilder();
                for (final String s3 : Objects.requireNonNull(session.getConfig().getYml().getConfigurationSection("Team")).getKeys(false)) if (session.getConfig().getYml().get("Team." + s3 + ".Spawn") == null) sb.append(session.getTeamColor(s3)).append(s3).append(" ");
                if (sb.toString().length() > 0) player.sendMessage(session.getPrefix() + "Restante: " + sb.toString());
            }
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
