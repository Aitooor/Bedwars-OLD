package dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.sensitive;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetNPCPos extends SubCommand {
    public SetNPCPos(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(7);
        this.showInList(true);
        this.setPermission("bedwars.admin");
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) return false;
        final Player player = (Player)commandSender;
        player.sendMessage("§aEn 10 segundos se tomará la posición donde estás junto al Yaw y el Pitch (la mira) como posición para el NPC. §e¡Prepárate!");
        Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
            player.sendMessage("§eTomando posición.");
            final Location location = player.getLocation();
            BedWars.getInstance().getMainConfig().getYml().set("npcs.location", LocationUtils.locToString(location, true));
            player.sendMessage("§a¡Listo! Guardando y recargando configuración.");
            BedWars.getInstance().getMainConfig().save();
            BedWars.getInstance().getMainConfig().reload();
        }, 200L);
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        return null;
    }
}
