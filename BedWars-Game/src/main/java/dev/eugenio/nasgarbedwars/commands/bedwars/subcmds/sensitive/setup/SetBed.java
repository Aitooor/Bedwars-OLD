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
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SetBed extends SubCommand {
    public SetBed(final ParentCommand parentCommand, final String s) {
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
                player.sendMessage(session.getPrefix() + ChatColor.RED + "No se ha podido encontrar ningún equipo cerca.");
                player.spigot().sendMessage(dev.eugenio.nasgarbedwars.arena.Misc.msgHoverClick(session.getPrefix() + "Asegúrate de que hayas establecido el spawn del equipo.", ChatColor.WHITE + "Establece la cama de un equipo.", "/bw " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
                player.spigot().sendMessage(dev.eugenio.nasgarbedwars.arena.Misc.msgHoverClick(session.getPrefix() + "O si has establecido el spawn pero no ha sido encontrado, usa /bw " + this.getSubCommandName() + " <equipo>", "Añade la cama de un equipo.", "/bw" + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
                BedWars.getInstance().getNms().sendTitle(player, " ", ChatColor.RED + "Ningún equipo cerca", 5, 60, 5);
                Sounds.playSound("shop-insufficient-money", player);
                session.displayAvailableTeams();
            } else {
                Bukkit.dispatchCommand(commandSender, this.getParent().getName() + " " + this.getSubCommandName() + " " + nearestTeam);
            }
        } else {
            if (!BedWars.getInstance().getNms().isBed(player.getLocation().clone().add(0.0, -0.5, 0.0).getBlock().getType()) && !BedWars.getInstance().getNms().isBed(player.getLocation().clone().add(0.0, 0.5, 0.0).getBlock().getType()) && !BedWars.getInstance().getNms().isBed(player.getLocation().clone().getBlock().getType())) {
                player.sendMessage(session.getPrefix() + ChatColor.RED + "Debes estar encima de una cama y mirandola mientras haces este comando.");
                BedWars.getInstance().getNms().sendTitle(player, " ", ChatColor.RED + "Necesitas estar en una cama.", 5, 40, 5);
                Sounds.playSound("shop-insufficient-money", player);
                return true;
            }
            if (session.getConfig().getYml().get("Team." + array[0]) == null) {
                player.sendMessage(session.getPrefix() + ChatColor.RED + "Ese equipo no existe.");
                if (session.getConfig().getYml().get("Team") != null) {
                    player.sendMessage(session.getPrefix() + "Equipos disponibles: ");
                    for (final String s : Objects.requireNonNull(session.getConfig().getYml().getConfigurationSection("Team")).getKeys(false)) {
                        player.spigot().sendMessage(dev.eugenio.nasgarbedwars.arena.Misc.msgHoverClick(ChatColor.GOLD + " " + '\u25aa' + " " + session.getTeamColor(s) + s, ChatColor.WHITE + "Setear cama para " + session.getTeamColor(s) + s, "/bw setBed " + s, ClickEvent.Action.RUN_COMMAND));
                    }
                }
            } else {
                final String string = session.getTeamColor(array[0]) + array[0];
                if (session.getConfig().getYml().get("Team." + array[0] + ".Bed") != null) Misc.removeArmorStand("bed", session.getConfig().getArenaLoc("Team." + array[0] + ".Bed"), null);
                Misc.createArmorStand(string + " " + ChatColor.GOLD + "CAMA SETEADA", player.getLocation().add(0.5, 0.0, 0.5), null);
                session.getConfig().saveArenaLoc("Team." + array[0] + ".Bed", player.getLocation());
                player.sendMessage(session.getPrefix() + "Cama seteada para: " + string);
                BedWars.getInstance().getNms().sendTitle(player, " ", ChatColor.GREEN + "Cama seteada para: " + string, 5, 40, 5);
                Sounds.playSound("shop-bought", player);
                if (session.getSetupType() == SetupType.ASSISTED) Bukkit.dispatchCommand(player, this.getParent().getName());
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
