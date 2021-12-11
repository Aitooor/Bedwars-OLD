package dev.eugenio.nasgarbedwars.commands.shout;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.arena.Arena;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ShoutCommand extends BukkitCommand {
    private static HashMap<UUID, Long> shoutCooldown;
    
    public ShoutCommand(final String s) {
        super(s);
    }
    
    public boolean execute(final CommandSender commandSender, final String s, final String[] array) {
        if (commandSender instanceof ConsoleCommandSender) return true;
        final Player player = (Player)commandSender;
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null || arenaByPlayer.isSpectator(player)) {
            player.sendMessage(Language.getMsg(player, Messages.COMMAND_NOT_FOUND_OR_INSUFF_PERMS));
            return true;
        }
        final StringBuilder sb = new StringBuilder();
        for (int length = array.length, i = 0; i < length; ++i) sb.append(array[i]).append(" ");
        player.chat("!" + sb.toString());
        return false;
    }
    
    public static void updateShout(final Player player) {
        if (player.hasPermission("bedwars.shout.bypass")) return;
        if (ShoutCommand.shoutCooldown.containsKey(player.getUniqueId())) {
            ShoutCommand.shoutCooldown.replace(player.getUniqueId(), System.currentTimeMillis() + (long) BedWars.getInstance().getMainConfig().getInt("shout-cmd-cooldown") * 1000);
        } else {
            ShoutCommand.shoutCooldown.put(player.getUniqueId(), System.currentTimeMillis() + (long) BedWars.getInstance().getMainConfig().getInt("shout-cmd-cooldown") * 1000);
        }
    }
    
    public static boolean isShoutCooldown(final Player player) {
        return !player.hasPermission("bedwars.shout.bypass") && ShoutCommand.shoutCooldown.containsKey(player.getUniqueId()) && ShoutCommand.shoutCooldown.get(player.getUniqueId()) > System.currentTimeMillis();
    }
    
    public static double getShoutCooldown(final Player player) {
        return (ShoutCommand.shoutCooldown.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000.0f;
    }
    
    static {
        ShoutCommand.shoutCooldown = new HashMap<>();
    }
}
