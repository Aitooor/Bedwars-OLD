package dev.eugenio.nasgarbedwars.listeners;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.GameStatus;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.commands.shout.ShoutCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChat implements Listener {
    @EventHandler
    public void onChat(final AsyncPlayerChatEvent event) {
        if (event == null) return;
        final Player player2 = event.getPlayer();
        if (event.isCancelled()) return;
        if (player2.hasPermission("bedwars.chatColor") || player2.hasPermission("bedwars.*") || player2.hasPermission("bedwars.vip")) event.setMessage(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
        if (Arena.getArenaByPlayer(player2) != null) {
            final IArena arenaByPlayer = Arena.getArenaByPlayer(player2);
            Arena.afkCheck.remove(player2.getUniqueId());
            if (BedWars.getInstance().getApi().getAFKUtil().isPlayerAFK(event.getPlayer())) {
                Bukkit.getScheduler().runTask(BedWars.getInstance(), () -> BedWars.getInstance().getApi().getAFKUtil().setPlayerAFK(event.getPlayer(), false));
            }
            if (arenaByPlayer.isSpectator(player2)) {
                if (!BedWars.getInstance().getMainConfig().getBoolean("globalChat")) {
                    event.getRecipients().clear();
                    event.getRecipients().addAll(arenaByPlayer.getSpectators());
                }
                event.setFormat(Language.getMsg(player2, Messages.FORMATTING_CHAT_SPECTATOR).replace("{vPrefix}", BedWars.getInstance().getChat().getPrefix(player2)).replace("{vSuffix}", BedWars.getInstance().getChat().getSuffix(player2)).replace("{player}", player2.getDisplayName()).replace("{message}", "%2$s").replace("{level}", BedWars.getInstance().getLevels().getLevel(player2)));
            } else {
                if (arenaByPlayer.getStatus() == GameStatus.waiting || arenaByPlayer.getStatus() == GameStatus.starting) {
                    event.setFormat(Language.getMsg(player2, Messages.FORMATTING_CHAT_WAITING).replace("{vPrefix}", BedWars.getInstance().getChat().getPrefix(player2)).replace("{vSuffix}", BedWars.getInstance().getChat().getSuffix(player2)).replace("{player}", player2.getDisplayName()).replace("{level}", BedWars.getInstance().getLevels().getLevel(player2)).replace("{message}", "%2$s"));
                    return;
                }
                final ITeam team = arenaByPlayer.getTeam(player2);
                String message = event.getMessage();
                if (message.startsWith("!") || message.startsWith("shout") || message.startsWith("SHOUT") || message.startsWith(Language.getMsg(player2, Messages.MEANING_SHOUT))) {
                    if (ShoutCommand.isShoutCooldown(player2)) {
                        event.setCancelled(true);
                        player2.sendMessage(Language.getMsg(player2, Messages.COMMAND_COOLDOWN).replace("{seconds}", String.valueOf(ShoutCommand.getShoutCooldown(player2))));
                        return;
                    }
                    ShoutCommand.updateShout(player2);
                    if (message.startsWith("!")) message = message.replaceFirst("!", "");
                    if (message.startsWith("shout")) message = message.replaceFirst("SHOUT", "");
                    if (message.startsWith("shout")) message = message.replaceFirst("shout", "");
                    if (message.startsWith(Language.getMsg(player2, Messages.MEANING_SHOUT))) message = message.replaceFirst(Language.getMsg(player2, Messages.MEANING_SHOUT), "");
                    if (message.isEmpty()) {
                        event.setCancelled(true);
                        return;
                    }
                    event.setMessage(message);
                    event.setFormat(Language.getMsg(player2, Messages.FORMATTING_CHAT_SHOUT).replace("{vPrefix}", BedWars.getInstance().getChat().getPrefix(player2)).replace("{vSuffix}", BedWars.getInstance().getChat().getSuffix(player2)).replace("{player}", player2.getDisplayName()).replace("{team}", team.getColor().chat() + "[" + team.getDisplayName(Language.getPlayerLanguage(event.getPlayer())).toUpperCase() + "]").replace("{level}", BedWars.getInstance().getLevels().getLevel(player2)).replace("{message}", "%2$s"));
                } else if (arenaByPlayer.getMaxInTeam() == 1) {
                    event.setFormat(Language.getMsg(player2, Messages.FORMATTING_CHAT_TEAM).replace("{vPrefix}", BedWars.getInstance().getChat().getPrefix(player2)).replace("{vSuffix}", BedWars.getInstance().getChat().getSuffix(player2)).replace("{player}", player2.getDisplayName()).replace("{team}", team.getColor().chat() + "[" + team.getDisplayName(Language.getPlayerLanguage(event.getPlayer())).toUpperCase() + "]").replace("{level}", BedWars.getInstance().getLevels().getLevel(player2)).replace("{message}", "%2$s"));
                } else {
                    event.setFormat(Language.getMsg(player2, Messages.FORMATTING_CHAT_TEAM).replace("{vPrefix}", BedWars.getInstance().getChat().getPrefix(player2)).replace("{vSuffix}", BedWars.getInstance().getChat().getSuffix(player2)).replace("{player}", player2.getDisplayName()).replace("{team}", team.getColor().chat() + "[" + team.getDisplayName(Language.getPlayerLanguage(event.getPlayer())).toUpperCase() + "]").replace("{level}", BedWars.getInstance().getLevels().getLevel(player2)).replace("{message}", "%2$s"));
                }
            }
        }
    }
}
