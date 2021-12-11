package dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.sensitive.setup;

import dev.eugenio.nasgarbedwars.commands.Misc;
import dev.eugenio.nasgarbedwars.configuration.ArenaConfig;
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

public class SetKillDropsLoc extends SubCommand {
    public SetKillDropsLoc(final ParentCommand parentCommand, final String s) {
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
        final ArenaConfig config = session.getConfig();
        if (array.length < 1) {
            String s = "";
            double n = 100.0;
            if (session.getConfig().getYml().getConfigurationSection("Team") == null) {
                player.sendMessage(session.getPrefix() + "Crea los equipos primero.");
                BedWars.getInstance().getNms().sendTitle(player, " ", ChatColor.RED + "Crea los equipos primero", 5, 40, 5);
                Sounds.playSound("shop-insufficient-money", player);
                return true;
            }
            for (final String s2 : session.getConfig().getYml().getConfigurationSection("Team").getKeys(false)) {
                if (session.getConfig().getYml().get("Team." + s2 + ".Spawn") == null) continue;
                final double distance = session.getConfig().getArenaLoc("Team." + s2 + ".Spawn").distance(player.getLocation());
                if (distance > session.getConfig().getInt("island-radius") || distance >= n) continue;
                n = distance;
                s = s2;
            }
            if (!s.isEmpty()) {
                if (session.getConfig().getYml().get("Team." + s + "." + "kill-drops-loc") != null) Misc.removeArmorStand("Kill drops", session.getConfig().getArenaLoc("Team." + s + "." + "kill-drops-loc"), null);
                config.set("Team." + s + "." + "kill-drops-loc", config.stringLocationArenaFormat(player.getLocation()));
                final String string = session.getTeamColor(s) + s;
                player.sendMessage(session.getPrefix() + "Drop de kills seteado para el equipo: " + string);
                Misc.createArmorStand(ChatColor.GOLD + "Kill drops " + string, player.getLocation(), null);
                BedWars.getInstance().getNms().sendTitle(player, " ", ChatColor.GREEN + "Kill drops seteado para el equipo: " + string, 5, 40, 5);
                Sounds.playSound("shop-bought", player);
                if (session.getSetupType() == SetupType.ASSISTED) Bukkit.dispatchCommand(player, this.getParent().getName());
                return true;
            }
            player.sendMessage(session.getPrefix() + ChatColor.RED + "Uso: /bw setKillDrops <equipo>");
            return true;
        } else {
            String nearestTeam = session.getNearestTeam();
            if (nearestTeam.isEmpty()) {
                player.sendMessage("");
                player.sendMessage(session.getPrefix() + ChatColor.RED + "No se ha podido encontrar ningún equipo cerca.");
                player.spigot().sendMessage(dev.eugenio.nasgarbedwars.arena.Misc.msgHoverClick(session.getPrefix() + "Asegúrate de que seteas el spawn del equipo primero.", ChatColor.WHITE + "Establece el spawn del equipo.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
                player.spigot().sendMessage(dev.eugenio.nasgarbedwars.arena.Misc.msgHoverClick(session.getPrefix() + "O si el spawn está pero no ha sido encontrado automáticamente, usa /bw " + this.getSubCommandName() + " <equipo>", "Establece localización de kill drops para el equipo.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
                BedWars.getInstance().getNms().sendTitle(player, " ", ChatColor.RED + "Could not find any nearby team.", 5, 60, 5);
                Sounds.playSound("shop-insufficient-money", player);
                return true;
            }
            if (array.length == 1) {
                if (config.getYml().get("Team." + array[0]) == null) {
                    player.sendMessage(session.getPrefix() + ChatColor.RED + "Ese equipo no existe.");
                    if (config.getYml().get("Team") != null) {
                        player.sendMessage(session.getPrefix() + "Equipos disponibles: ");
                        for (final String s3 : Objects.requireNonNull(config.getYml().getConfigurationSection("Team")).getKeys(false)) {
                            player.spigot().sendMessage(dev.eugenio.nasgarbedwars.arena.Misc.msgHoverClick(ChatColor.GOLD + " " + '\u25aa' + " Kill drops " + session.getTeamColor(s3) + s3 + " " + ChatColor.getLastColors(session.getPrefix()) + "(click para setear)", ChatColor.WHITE + "Seteado kill drops para " + session.getTeamColor(s3) + s3, "/bw setKillDrops " + s3, ClickEvent.Action.RUN_COMMAND));
                        }
                    }
                    return true;
                }
                nearestTeam = array[0];
            }
            config.set("Team." + nearestTeam + "." + "kill-drops-loc", config.stringLocationArenaFormat(player.getLocation()));
            player.sendMessage(session.getPrefix() + "Kill drops seteado para: " + session.getTeamColor(nearestTeam) + nearestTeam);
            if (session.getSetupType() == SetupType.ASSISTED) {
                Bukkit.dispatchCommand(player, this.getParent().getName());
            }
            return true;
        }
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
