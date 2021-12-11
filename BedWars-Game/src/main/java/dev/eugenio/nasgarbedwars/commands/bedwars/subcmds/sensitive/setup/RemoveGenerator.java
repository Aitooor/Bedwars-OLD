package dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.sensitive.setup;

import dev.eugenio.nasgarbedwars.configuration.Permissions;
import dev.eugenio.nasgarbedwars.configuration.Sounds;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.api.server.SetupType;
import dev.eugenio.nasgarbedwars.arena.SetupSession;
import dev.eugenio.nasgarbedwars.commands.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RemoveGenerator extends SubCommand {
    public RemoveGenerator(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setArenaSetupCommand(true);
        this.setPermission(Permissions.PERMISSION_SETUP_ARENA);
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (!(commandSender instanceof Player)) return false;
        final Player player = (Player)commandSender;
        final SetupSession session = SetupSession.getSession(player.getUniqueId());
        if (session == null) return false;
        if (array.length != 0) return true;
        final String[] array2 = { "", "", "" };
        Location location = null;
        if (session.getConfig().getYml().get("Team") != null) {
            for (final String s : session.getConfig().getYml().getConfigurationSection("Team").getKeys(false)) {
                for (final String s2 : new String[] { "Iron", "Gold", "Emerald" }) {
                    if (session.getConfig().getYml().get("Team." + s + "." + s2) != null) {
                        for (final String s3 : session.getConfig().getList("Team." + s + "." + s2)) {
                            final Location convertStringToArenaLocation = session.getConfig().convertStringToArenaLocation(s3);
                            if (convertStringToArenaLocation != null && player.getLocation().distance(convertStringToArenaLocation) <= 2.0) {
                                if (location != null) {
                                    if (player.getLocation().distance(location) <= player.getLocation().distance(convertStringToArenaLocation)) continue;
                                    location = convertStringToArenaLocation;
                                    array2[0] = s2;
                                    array2[1] = s3;
                                    array2[2] = s;
                                } else {
                                    location = convertStringToArenaLocation;
                                    array2[0] = s2;
                                    array2[1] = s3;
                                    array2[2] = s;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (session.getConfig().getYml().get("generator") != null) {
            for (final String s4 : new String[] { "Emerald", "Diamond" }) {
                if (session.getConfig().getYml().get("generator." + s4) != null) {
                    for (final String s5 : session.getConfig().getList("generator." + s4)) {
                        final Location convertStringToArenaLocation2 = session.getConfig().convertStringToArenaLocation(s5);
                        if (convertStringToArenaLocation2 == null) continue;
                        if (player.getLocation().distance(convertStringToArenaLocation2) > 2.0) continue;
                        if (location != null) {
                            if (player.getLocation().distance(location) <= player.getLocation().distance(convertStringToArenaLocation2)) continue;
                            location = convertStringToArenaLocation2;
                            array2[0] = s4;
                            array2[1] = s5;
                            array2[2] = "";
                        } else {
                            location = convertStringToArenaLocation2;
                            array2[0] = s4;
                            array2[1] = s5;
                            array2[2] = "";
                        }
                    }
                }
            }
        }
        if (location == null) {
            player.sendMessage(session.getPrefix() + "No se ha podido encontrar un generador cerca (Rango 2x2).");
            player.sendMessage(session.getPrefix() + "Debes de estar cerca del generador que quieres quitar.");
            BedWars.getInstance().getNms().sendTitle(player, " ", ChatColor.RED + "No se ha podido encontrar un generador cerca.", 5, 40, 5);
            Sounds.playSound("shop-insufficient-money", player);
            return true;
        }
        if (array2[2].isEmpty()) {
            final List<String> list = session.getConfig().getList("generator." + array2[0]);
            list.remove(array2[1]);
            session.getConfig().set("generator." + array2[0], list);
            player.sendMessage(session.getPrefix() + "Removido generador de " + array2[0] + " en la localización X:" + location.getBlockX() + " Y:" + location.getBlockY() + " Z:" + location.getZ());
            BedWars.getInstance().getNms().sendTitle(player, " ", ChatColor.GREEN + "Generador de " + array2[0] + " removido.", 5, 40, 5);
            Sounds.playSound("shop-bought", player);
            Misc.removeArmorStand(array2[0], location, array2[1]);
            return true;
        }
        if (session.getSetupType() == SetupType.ASSISTED) {
            session.getConfig().set("Team." + array2[2] + ".Emerald", new ArrayList<>());
            session.getConfig().set("Team." + array2[2] + ".Iron", new ArrayList<>());
            session.getConfig().set("Team." + array2[2] + ".Gold", new ArrayList<>());
            BedWars.getInstance().getNms().sendTitle(player, " ", session.getTeamColor(array2[2]) + array2[2] + " removido.", 5, 40, 5);
            Sounds.playSound("shop-bought", player);
            Misc.removeArmorStand(null, location, array2[1]);
            player.sendMessage(session.getPrefix() + "Generadores de " + session.getTeamColor(array2[2]) + array2[2] + ChatColor.getLastColors(session.getPrefix()) + " removidos.");
            return true;
        }
        final List<String> list2 = session.getConfig().getList("Team." + array2[2] + "." + array2[0]);
        list2.remove(array2[1]);
        session.getConfig().set("Team." + array2[2] + "." + array2[0], list2);
        player.sendMessage(session.getPrefix() + "Removido generador de " + session.getTeamColor(array2[2]) + array2[2] + " " + ChatColor.getLastColors(session.getPrefix()) + array2[0] + " en la localización X:" + location.getBlockX() + " Y:" + location.getBlockY() + " Z:" + location.getZ());
        BedWars.getInstance().getNms().sendTitle(player, " ", session.getTeamColor(array2[2]) + array2[2] + " " + ChatColor.GREEN + array2[0] + " removido.", 5, 40, 5);
        Sounds.playSound("shop-bought", player);
        Misc.removeArmorStand(array2[0], location, array2[1]);
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        return new ArrayList<>();
    }
}
