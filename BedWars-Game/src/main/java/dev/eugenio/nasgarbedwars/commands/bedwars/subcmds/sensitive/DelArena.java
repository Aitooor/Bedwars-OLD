package dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.sensitive;

import dev.eugenio.nasgarbedwars.commands.bedwars.MainCommand;
import dev.eugenio.nasgarbedwars.configuration.Permissions;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.arena.Misc;
import dev.eugenio.nasgarbedwars.arena.SetupSession;
import net.md_5.bungee.api.chat.ClickEvent;
import org.apache.commons.io.FileUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DelArena extends SubCommand {
    private static HashMap<Player, Long> delArenaConfirm;
    
    public DelArena(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(4);
        this.showInList(true);
        this.setPermission(Permissions.PERMISSION_DEL_ARENA);
        this.setDisplayInfo(Misc.msgHoverClick("§6 \u25aa §7/" + MainCommand.getInstance().getName() + " " + this.getSubCommandName() + " §6<mundo>", "§fElimina un mapa y su configuración.", "/" + MainCommand.getInstance().getName() + " " + this.getSubCommandName(), ClickEvent.Action.SUGGEST_COMMAND));
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) return false;
        final Player player = (Player)commandSender;
        if (array.length != 1) {
            player.sendMessage("§c\u25aa §7Uso: §o/" + MainCommand.getInstance().getName() + " delArena <mundo>");
            return true;
        }
        if (!BedWars.getInstance().getApi().getRestoreAdapter().isWorld(array[0])) {
            player.sendMessage("§c\u25aa §7" + array[0] + " no existe como una carpeta de mundo.");
            return true;
        }
        if (Arena.getArenaByName(array[0]) != null) {
            player.sendMessage("§c\u25aa §7Por favor, deshabilita la arena primero.");
            return true;
        }
        final File file = new File(BedWars.getInstance().getDataFolder(), "/Arenas/" + array[0] + ".yml");
        if (!file.exists()) {
            player.sendMessage("§c\u25aa §7Esa arena no existe.");
            return true;
        }
        if (DelArena.delArenaConfirm.containsKey(player)) {
            if (System.currentTimeMillis() - 2000L <= DelArena.delArenaConfirm.get(player)) {
                BedWars.getInstance().getApi().getRestoreAdapter().deleteWorld(array[0]);
                FileUtils.deleteQuietly(file);
                player.sendMessage("§c\u25aa §7La arena " + array[0] + " fue eliminada.");
                return true;
            }
            player.sendMessage("§6 \u25aa §7Pon otra vez el mismo comando para confirmar.");
            DelArena.delArenaConfirm.replace(player, System.currentTimeMillis());
        } else {
            player.sendMessage("§6 \u25aa §7Pon otra vez el mismo comando para confirmar.");
            DelArena.delArenaConfirm.put(player, System.currentTimeMillis());
        }
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
    
    static {
        DelArena.delArenaConfirm = new HashMap<>();
    }
}
