package dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.sensitive;

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
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CloneArena extends SubCommand {
    public CloneArena(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(7);
        this.showInList(true);
        this.setPermission(Permissions.PERMISSION_CLONE);
        this.setDisplayInfo(Misc.msgHoverClick("§6 \u25aa §7/" + this.getParent().getName() + " " + this.getSubCommandName() + " §6<mundo> <nuevaArena>", "§fClona una arena existente.", "/" + this.getParent().getName() + " " + this.getSubCommandName(), ClickEvent.Action.SUGGEST_COMMAND));
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) return false;
        final Player player = (Player)commandSender;
        if (array.length != 2) {
            player.sendMessage("§c\u25aa §7Uso: §o/" + this.getParent().getName() + " " + this.getSubCommandName() + " <mundo> <nuevaArena>");
            return true;
        }
        if (!BedWars.getInstance().getApi().getRestoreAdapter().isWorld(array[0])) {
            player.sendMessage("§c\u25aa §7" + array[0] + " doesn't exist!");
            return true;
        }
        final File file = new File(BedWars.getInstance().getDataFolder(), "/Arenas/" + array[0] + ".yml");
        final File file2 = new File(BedWars.getInstance().getDataFolder(), "/Arenas/" + array[1] + ".yml");
        if (!file.exists()) {
            player.sendMessage("§c\u25aa §7" + array[0] + " no existe.");
            return true;
        }
        if (BedWars.getInstance().getApi().getRestoreAdapter().isWorld(array[1]) && file2.exists()) {
            player.sendMessage("§c\u25aa §7" + array[1] + " ya existe.");
            return true;
        }
        if (array[1].contains("+")) {
            player.sendMessage("§c\u25aa §7" + array[1] + " no debe de contener este símbolo: " + ChatColor.RED + "+");
            return true;
        }
        if (Arena.getArenaByName(array[0]) != null) {
            player.sendMessage("§c\u25aa §7Deshabilita " + array[0] + " primero.");
            return true;
        }
        BedWars.getInstance().getApi().getRestoreAdapter().cloneArena(array[0], array[1]);
        if (file.exists()) {
            try {
                FileUtils.copyFile(file, file2, true);
            } catch (IOException ex) {
                ex.printStackTrace();
                player.sendMessage("§c\u25aa §7Ha ocurrido un error copiando la configuración del mapa; revisa la consola para saber más detalles.");
            }
        }
        player.sendMessage("§6 \u25aa §a¡Listo!");
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        final ArrayList<String> list = new ArrayList<String>();
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
