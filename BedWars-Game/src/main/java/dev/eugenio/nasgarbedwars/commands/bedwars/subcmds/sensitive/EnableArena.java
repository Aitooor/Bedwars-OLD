package dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.sensitive;

import dev.eugenio.nasgarbedwars.configuration.Permissions;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.arena.Misc;
import dev.eugenio.nasgarbedwars.arena.SetupSession;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EnableArena extends SubCommand {
    public EnableArena(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setDisplayInfo(Misc.msgHoverClick("§6 \u25aa §7/" + this.getParent().getName() + " " + this.getSubCommandName() + " §6<arena>", "§fHabilita una arena.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
        this.showInList(true);
        this.setPriority(5);
        this.setPermission(Permissions.PERMISSION_ARENA_ENABLE);
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) return false;
        final Player player = (Player)commandSender;
        if (array.length != 1) {
            player.sendMessage("§c\u25aa §7Uso: §o/" + this.getParent().getName() + " enableRotation <arena>");
            return true;
        }
        if (!BedWars.getInstance().getApi().getRestoreAdapter().isWorld(array[0])) {
            player.sendMessage("§c\u25aa §7" + array[0] + " no existe.");
            return true;
        }
        for (IArena iArena : Arena.getEnableQueue()) {
            if (iArena.getArenaName().equalsIgnoreCase(array[0])) {
                player.sendMessage("§c\u25aa §7Esta arena ya está en la cola de habilitarse.");
                return true;
            }
        }
        if (Arena.getArenaByName(array[0]) != null) {
            player.sendMessage("§c\u25aa §7Esta arena ya está habilitada.");
            return true;
        }
        player.sendMessage("§6 \u25aa §7Habilitando arena...");
        new Arena(array[0], player);
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        final ArrayList<String> list = new ArrayList<>();
        final File file = new File(BedWars.getInstance().getDataFolder(), "/Arenas");
        if (file.exists()) for (final File file2 : Objects.requireNonNull(file.listFiles())) if (file2.isFile() && file2.getName().contains(".yml")) list.add(file2.getName().replace(".yml", ""));
        return list;
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BedWarsAPI bedWarsAPI) {
        if (commandSender instanceof ConsoleCommandSender) return false;
        final Player player = (Player)commandSender;
        return !Arena.isInArena(player) && !SetupSession.isInSetupSession(player.getUniqueId()) && this.hasPermission(commandSender);
    }
}
