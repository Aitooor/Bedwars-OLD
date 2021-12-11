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
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddGenerator extends SubCommand {
    public AddGenerator(final ParentCommand parentCommand, final String s) {
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
        if (array.length == 0 && session.getSetupType() == SetupType.ASSISTED) {
            final String nearestTeam = session.getNearestTeam();
            if (!nearestTeam.isEmpty()) {
                saveTeamGen(player.getLocation(), nearestTeam, session, "Iron");
                saveTeamGen(player.getLocation(), nearestTeam, session, "Gold");
                saveTeamGen(player.getLocation(), nearestTeam, session, "Emerald");
                Misc.createArmorStand(ChatColor.GOLD + "Generador seteado para equipo: " + session.getTeamColor(nearestTeam) + nearestTeam, player.getLocation(), session.getConfig().stringLocationArenaFormat(player.getLocation()));
                player.sendMessage(session.getPrefix() + "Generador seteado para equipo: " + session.getTeamColor(nearestTeam) + nearestTeam);
                Bukkit.dispatchCommand(player, this.getParent().getName());
                BedWars.getInstance().getNms().sendTitle(player, " ", ChatColor.GREEN + "Generador seteado para equipo: " + session.getTeamColor(nearestTeam) + nearestTeam, 5, 60, 5);
                Sounds.playSound("shop-bought", player);
                return true;
            }
            if (player.getLocation().add(0.0, -1.0, 0.0).getBlock().getType() == Material.DIAMOND_BLOCK) {
                Bukkit.dispatchCommand(player, this.getParent().getName() + " " + this.getSubCommandName() + " diamond");
                return true;
            }
            if (player.getLocation().add(0.0, -1.0, 0.0).getBlock().getType() == Material.EMERALD_BLOCK) {
                Bukkit.dispatchCommand(player, this.getParent().getName() + " " + this.getSubCommandName() + " emerald");
                return true;
            }
            player.sendMessage(session.getPrefix() + ChatColor.RED + "No se ha podido encontrar ningún equipo cerca. ¿Estás seguro de que lo estás haciendo en una isla?");
            player.spigot().sendMessage(dev.eugenio.nasgarbedwars.arena.Misc.msgHoverClick(session.getPrefix() + "Si lo estás, ¡asegúrate que hayas puesto primero el spawn del equipo!", ChatColor.WHITE + "Setea el spawn del equipo.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
            player.spigot().sendMessage(dev.eugenio.nasgarbedwars.arena.Misc.msgHoverClick(session.getPrefix() + "Si ya lo has hecho y aún así no lo ha encontrado, puedes usar /bw addGenerator <equipo>", "Añade un generador del equipo.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
            player.spigot().sendMessage(dev.eugenio.nasgarbedwars.arena.Misc.msgHoverClick(session.getPrefix() + "Si no es para el equipo, usa /bw addGenerator <emerald / diamond>", "Añade un generador de esmeralda o de diamante.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
            BedWars.getInstance().getNms().sendTitle(player, " ", ChatColor.RED + "No hay equipos cerca.", 5, 60, 5);
            Sounds.playSound("shop-insufficient-money", player);
            return true;
        }
        else {
            if (array.length == 1 && (array[0].equalsIgnoreCase("diamond") || array[0].equalsIgnoreCase("emerald"))) {
                for (Location location : session.getConfig().getArenaLocations("generator." + array[0].substring(0, 1).toUpperCase() + array[0].substring(1).toLowerCase())) {
                    if (session.getConfig().compareArenaLoc(location, player.getLocation())) {
                        player.sendMessage(session.getPrefix() + ChatColor.RED + "Este generador ya ha sido seteado.");
                        BedWars.getInstance().getNms().sendTitle(player, " ", ChatColor.RED + "Este generador ya ha sido seteado.", 5, 30, 5);
                        Sounds.playSound("shop-insufficient-money", player);
                        return true;
                    }
                }
                final String string = array[0].substring(0, 1).toUpperCase() + array[0].substring(1).toLowerCase();
                ArrayList<String> list;
                if (session.getConfig().getYml().get("generator." + string) == null) {
                    list = new ArrayList<>();
                } else {
                    list = (ArrayList<String>)session.getConfig().getYml().getStringList("generator." + string);
                }
                list.add(session.getConfig().stringLocationArenaFormat(player.getLocation()));
                session.getConfig().set("generator." + string, list);
                player.sendMessage("Generador de" + session.getPrefix() + string + " añadido.");
                Misc.createArmorStand(ChatColor.GOLD + string + " SETEADO", player.getLocation(), session.getConfig().stringLocationArenaFormat(player.getLocation()));
                if (session.getSetupType() == SetupType.ASSISTED) Bukkit.dispatchCommand(player, this.getParent().getName());
                BedWars.getInstance().getNms().sendTitle(player, " ", ChatColor.GREEN + "Generador de " + ChatColor.GOLD + string + ChatColor.GREEN + " añadido.", 5, 60, 5);
                Sounds.playSound("shop-bought", player);
                return true;
            }
            if (array.length >= 1 && (array[0].equalsIgnoreCase("iron") || array[0].equalsIgnoreCase("gold") || array[0].equalsIgnoreCase("upgrade")) && session.getSetupType() == SetupType.ADVANCED) {
                String nearestTeam2;
                if (array.length == 1) {
                    nearestTeam2 = session.getNearestTeam();
                } else {
                    nearestTeam2 = array[1];
                    if (session.getConfig().getYml().get("Team." + nearestTeam2 + ".Color") == null) {
                        player.sendMessage(session.getPrefix() + ChatColor.RED + "No se ha podido encontrar el equipo " + nearestTeam2);
                        player.sendMessage(session.getPrefix() + "Usa /bw createTeam si quieres crear uno.");
                        session.displayAvailableTeams();
                        BedWars.getInstance().getNms().sendTitle(player, " ", ChatColor.RED + "No se ha podido encontrar ningún equipo cerca.", 5, 60, 5);
                        Sounds.playSound("shop-insufficient-money", player);
                        return true;
                    }
                }
                if (nearestTeam2.isEmpty()) {
                    player.sendMessage(session.getPrefix() + ChatColor.RED + "No se ha podido encontrar ningún equipo cerca.");
                    player.sendMessage(session.getPrefix() + "Intenta usar /bw addGenerator <iron / gold / upgrade> <equipo>");
                    return true;
                }
                String string2 = array[0].substring(0, 1).toUpperCase() + array[0].substring(1).toLowerCase();
                if (string2.equalsIgnoreCase("upgrade")) string2 = "Emerald";
                Misc.createArmorStand(ChatColor.GOLD + "Generador de " + string2 + " añadido para el equipo: " + session.getTeamColor(nearestTeam2) + nearestTeam2, player.getLocation(), session.getConfig().stringLocationArenaFormat(player.getLocation()));
                player.sendMessage(session.getPrefix() + "Generador de " + string2 + " añadido para el equipo: " + session.getTeamColor(nearestTeam2) + nearestTeam2);
                saveTeamGen(player.getLocation(), nearestTeam2, session, string2);
                BedWars.getInstance().getNms().sendTitle(player, " ", ChatColor.GOLD + string2 + ChatColor.GREEN + " generador para " + session.getTeamColor(nearestTeam2) + nearestTeam2 + ChatColor.GREEN + " fue añadido.", 5, 60, 5);
                Sounds.playSound("shop-bought", player);
                return true;
            } else {
                if (array.length != 1 || session.getSetupType() != SetupType.ASSISTED) {
                    if (session.getSetupType() == SetupType.ASSISTED) {
                        player.spigot().sendMessage(dev.eugenio.nasgarbedwars.arena.Misc.msgHoverClick(session.getPrefix() + "/bw addGenerator (detectar equipo automáticamente)", "Añade un generador para el equipo.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
                        player.spigot().sendMessage(dev.eugenio.nasgarbedwars.arena.Misc.msgHoverClick(session.getPrefix() + "/bw addGenerator <equipo>", "Añade un generador para el equipo.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
                    }
                    if (session.getSetupType() == SetupType.ADVANCED) {
                        player.spigot().sendMessage(dev.eugenio.nasgarbedwars.arena.Misc.msgHoverClick(session.getPrefix() + "/bw addGenerator <iron / gold / upgrade>", "Añade un generador para el equipo.\nEl equipo será detectado automáticamente, en teoría.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
                        player.spigot().sendMessage(dev.eugenio.nasgarbedwars.arena.Misc.msgHoverClick(session.getPrefix() + "/bw addGenerator <iron / gold / upgrade> <equipo>", "Añade un generador para el equipo.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
                    }
                    player.spigot().sendMessage(dev.eugenio.nasgarbedwars.arena.Misc.msgHoverClick(session.getPrefix() + "/bw addGenerator <emerald / diamond>", "Añade un generador de esmeralda o diamante.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
                    return true;
                }
                final String s = array[0];
                if (session.getConfig().getYml().get("Team." + s + ".Color") == null) {
                    player.sendMessage(session.getPrefix() + "No se ha podido encontrar el equipo: " + ChatColor.RED + s);
                    player.sendMessage(session.getPrefix() + "Usa /bw createTeam si quieres crear uno.");
                    session.displayAvailableTeams();
                    BedWars.getInstance().getNms().sendTitle(player, " ", "No se ha podido encontrar el equipo: " + ChatColor.RED + s, 5, 40, 5);
                    Sounds.playSound("shop-insufficient-money", player);
                    return true;
                }
                saveTeamGen(player.getLocation(), s, session, "Iron");
                saveTeamGen(player.getLocation(), s, session, "Gold");
                saveTeamGen(player.getLocation(), s, session, "Emerald");
                Misc.createArmorStand(ChatColor.GOLD + "Generador seteado para el equipo: " + session.getTeamColor(s) + s, player.getLocation(), session.getConfig().stringLocationArenaFormat(player.getLocation()));
                player.sendMessage(session.getPrefix() + "Generador seteado para el equipo: " + session.getTeamColor(s) + s);
                Bukkit.dispatchCommand(player, this.getParent().getName());
                BedWars.getInstance().getNms().sendTitle(player, " ", ChatColor.GREEN + "Generador seteado para el equipo: " + session.getTeamColor(s) + s, 5, 60, 5);
                Sounds.playSound("shop-bought", player);
                return true;
            }
        }
    }
    
    @Override
    public List<String> getTabComplete() {
        return Arrays.asList("Diamond", "Emerald", "Iron", "Gold", "Upgrade");
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BedWarsAPI bedWarsAPI) {
        return !(commandSender instanceof ConsoleCommandSender) && SetupSession.isInSetupSession(((Player)commandSender).getUniqueId()) && this.hasPermission(commandSender);
    }
    
    private static void saveTeamGen(final Location location, final String s, final SetupSession setupSession, final String s2) {
        final Object value = setupSession.getConfig().getYml().get("Team." + s + "." + s2);
        List<String> list;
        if (value == null) {
            list = new ArrayList<>();
        } else if (value instanceof String) {
            list = new ArrayList<>();
            list.add((String)value);
        } else {
            list = setupSession.getConfig().getList("Team." + s + "." + s2);
        }
        list.add(setupSession.getConfig().stringLocationArenaFormat(location));
        setupSession.getConfig().set("Team." + s + "." + s2, list);
    }
}
