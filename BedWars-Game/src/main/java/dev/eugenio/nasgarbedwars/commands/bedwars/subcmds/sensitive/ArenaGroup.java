package dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.sensitive;

import dev.eugenio.nasgarbedwars.configuration.ArenaConfig;
import dev.eugenio.nasgarbedwars.configuration.Permissions;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.arena.Misc;
import dev.eugenio.nasgarbedwars.arena.SetupSession;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArenaGroup extends SubCommand {
    public ArenaGroup(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(8);
        this.showInList(true);
        this.setPermission(Permissions.PERMISSION_ARENA_GROUP);
        this.setDisplayInfo(Misc.msgHoverClick("§6 \u25aa §7/" + this.getParent().getName() + " " + this.getSubCommandName() + " §8- §eclick para detalles", "§fManejar grupos de arenas.", "/" + this.getParent().getName() + " " + this.getSubCommandName(), ClickEvent.Action.RUN_COMMAND));
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) return false;
        final Player player = (Player)commandSender;
        if (array.length < 2) {
            this.sendArenaGroupCmdList(player);
        } else if (array[0].equalsIgnoreCase("create")) {
            if (array[0].contains("+")) {
                player.sendMessage("§c\u25aa §7" + array[0] + " no puede contener este símbolo: " + ChatColor.RED + "+");
                return true;
            }
            List<String> stringList;
            if (BedWars.getInstance().getMainConfig().getYml().getStringList("arenaGroups") == null) {
                stringList = new ArrayList<>();
            } else {
                stringList = BedWars.getInstance().getMainConfig().getYml().getStringList("arenaGroups");
            }
            if (stringList.contains(array[1])) {
                player.sendMessage("§c\u25aa §7Este grupo ya existe.");
                return true;
            }
            stringList.add(array[1]);
            BedWars.getInstance().getMainConfig().set("arenaGroups", stringList);
            player.sendMessage("§6 \u25aa §7Grupo creado.");
        } else if (array[0].equalsIgnoreCase("remove")) {
            List<String> stringList2;
            if (BedWars.getInstance().getMainConfig().getYml().getStringList("arenaGroups") == null) {
                stringList2 = new ArrayList<>();
            }
            else {
                stringList2 = BedWars.getInstance().getMainConfig().getYml().getStringList("arenaGroups");
            }
            if (!stringList2.contains(array[1])) {
                player.sendMessage("§c\u25aa §7Ese grupo no existe.");
                return true;
            }
            stringList2.remove(array[1]);
            BedWars.getInstance().getMainConfig().set("arenaGroups", stringList2);
            player.sendMessage("§6 \u25aa §7Grupo eliminado.");
        }
        else if (array[0].equalsIgnoreCase("list")) {
            List<String> stringList3;
            if (BedWars.getInstance().getMainConfig().getYml().getStringList("arenaGroups") == null) {
                stringList3 = new ArrayList<>();
            }
            else {
                stringList3 = BedWars.getInstance().getMainConfig().getYml().getStringList("arenaGroups");
            }
            player.sendMessage("§7Available arena groups:");
            player.sendMessage("§6 \u25aa §fDefault");
            for (String s : stringList3) player.sendMessage("§6 \u25aa §f" + s);
        }
        else if (array[0].equalsIgnoreCase("set")) {
            if (array.length < 3) {
                this.sendArenaGroupCmdList(player);
                return true;
            }
            if (BedWars.getInstance().getMainConfig().getYml().get("arenaGroups") != null) {
                if (BedWars.getInstance().getMainConfig().getYml().getStringList("arenaGroups").contains(array[2])) {
                    if (!new File(BedWars.getInstance().getDataFolder(), "/Arenas/" + array[1] + ".yml").exists()) {
                        player.sendMessage("§c\u25aa §7Arena " + array[1] + " doesn't exist!");
                        return true;
                    }
                    ArenaConfig arenaConfig = new ArenaConfig(BedWars.getInstance(), array[1], BedWars.getInstance().getDataFolder().getPath() + "/Arenas");
                    arenaConfig.set("group", array[2]);
                    if (Arena.getArenaByName(array[1]) != null) Arena.getArenaByName(array[1]).setGroup(array[2]);
                    player.sendMessage("§6 \u25aa §7" + array[1] + " fue añadido al grupo: " + array[2]);
                } else {
                    player.sendMessage("§6 \u25aa §7No hay ningún grupo llamado: " + array[2]);
                    Bukkit.dispatchCommand(player, "/bw list");
                }
            } else {
                player.sendMessage("§6 \u25aa §7No hay ningún grupo llamado: " + array[2]);
                Bukkit.dispatchCommand(player, "/bw list");
            }
        } else {
            this.sendArenaGroupCmdList(player);
        }
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        return Arrays.asList("create", "remove", "list", "set");
    }
    
    private void sendArenaGroupCmdList(final Player player) {
        player.spigot().sendMessage(Misc.msgHoverClick("§6 \u25aa §7/" + this.getParent().getName() + " " + this.getSubCommandName() + " create §o<nombre>", "Crea un grupo de arenas.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " create", ClickEvent.Action.SUGGEST_COMMAND));
        player.spigot().sendMessage(Misc.msgHoverClick("§6 \u25aa §7/" + this.getParent().getName() + " " + this.getSubCommandName() + " list", "Te enseña los grupos disponibles.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " list", ClickEvent.Action.RUN_COMMAND));
        player.spigot().sendMessage(Misc.msgHoverClick("§6 \u25aa §7/" + this.getParent().getName() + " " + this.getSubCommandName() + " remove §o<nombre>", "Remueve un grupo de arenas.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " remove", ClickEvent.Action.SUGGEST_COMMAND));
        player.spigot().sendMessage(Misc.msgHoverClick("§6 \u25aa §7/" + this.getParent().getName() + " " + this.getSubCommandName() + " §r§7set §o<nombreArena> <nombreGrupo>", "Setea el grupo de la arena.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " set", ClickEvent.Action.SUGGEST_COMMAND));
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BedWarsAPI bedWarsAPI) {
        if (commandSender instanceof ConsoleCommandSender) return false;
        final Player player = (Player)commandSender;
        return !Arena.isInArena(player) && !SetupSession.isInSetupSession(player.getUniqueId()) && this.hasPermission(commandSender);
    }
}
