package dev.eugenio.nasgarbedwars.listeners;

import dev.eugenio.nasgarbedwars.configuration.Permissions;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CmdProcess implements Listener {
    @EventHandler
    public void onCmd(final PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
        final Player player = playerCommandPreprocessEvent.getPlayer();
        if (player.hasPermission(Permissions.PERMISSION_COMMAND_BYPASS)) return;
        final String[] split = playerCommandPreprocessEvent.getMessage().replaceFirst("/", "").split(" ");
        if (split.length == 0) return;
        if (Arena.isInArena(player) && !BedWars.getInstance().getMainConfig().getList("allowed-commands").contains(split[0])) {
            player.sendMessage(Language.getMsg(player, Messages.COMMAND_NOT_ALLOWED_IN_GAME));
            playerCommandPreprocessEvent.setCancelled(true);
        }
    }
}
