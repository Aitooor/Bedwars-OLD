package dev.eugenio.nasgarbedwars.listeners.dropshandler;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerKillEvent;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerDrops {
    public static boolean handlePlayerDrops(final IArena arena, final Player player, final Player player2, final ITeam team, final ITeam team2, final PlayerKillEvent.PlayerKillCause playerKillCause, final List<ItemStack> list) {
        Location loc;
        if (arena.getConfig().getBoolean("vanilla-death-drops")) return false;
        if (playerKillCause == PlayerKillEvent.PlayerKillCause.PLAYER_PUSH || playerKillCause == PlayerKillEvent.PlayerKillCause.PLAYER_PUSH_FINAL) {
            dropItems(player, list);
            return true;
        }
        if (player2 == null) {
            dropItems(player, list);
            return true;
        }
        if (playerKillCause.isDespawnable()) {
            dropItems(player, list);
            return true;
        }
        if (playerKillCause.isPvpLogOut()) {
            dropItems(player, list);
            return true;
        }
        if (playerKillCause.isFinalKill() && team != null) {
            loc = new Location(player.getWorld(), team.getKillDropsLocation().getBlockX(), team.getKillDropsLocation().getY(), team.getKillDropsLocation().getZ());
            player.getEnderChest().forEach(itemStack -> {
                if (itemStack != null) {
                    player.getWorld().dropItemNaturally(loc, itemStack);
                }
            });
            player.getEnderChest().clear();
        }
        if (team != null && (!team.equals(team2) || !player.equals(player2))) {
            if (team.isBedDestroyed()) {
                for (final ItemStack itemStack2 : list) {
                    if (itemStack2 == null) continue;
                    if (itemStack2.getType() == Material.AIR) continue;
                    if (BedWars.getInstance().getNms().isArmor(itemStack2) || BedWars.getInstance().getNms().isBow(itemStack2) || BedWars.getInstance().getNms().isSword(itemStack2)) continue;
                    if (BedWars.getInstance().getNms().isTool(itemStack2)) continue;
                    if (!BedWars.getInstance().getNms().getShopUpgradeIdentifier(itemStack2).trim().isEmpty()) continue;
                    if (arena.getTeam(player2) == null) continue;
                    final Vector killDropsLocation = team.getKillDropsLocation();
                    player2.getWorld().dropItemNaturally(new Location(arena.getWorld(), killDropsLocation.getX(), killDropsLocation.getY(), killDropsLocation.getZ()), itemStack2);
                }
            } else {
                if (!arena.isPlayer(player2)) return true;
                if (arena.isReSpawning(player2)) return true;
                final HashMap<Material, Integer> hashMap = new HashMap<>();
                for (final ItemStack itemStack3 : list) {
                    if (itemStack3 == null) continue;
                    if (itemStack3.getType() == Material.AIR) continue;
                    if (itemStack3.getType() != Material.DIAMOND && itemStack3.getType() != Material.EMERALD && itemStack3.getType() != Material.IRON_INGOT && itemStack3.getType() != Material.GOLD_INGOT) continue;
                    player2.getInventory().addItem(itemStack3);
                    if (hashMap.containsKey(itemStack3.getType())) {
                        hashMap.replace(itemStack3.getType(), hashMap.get(itemStack3.getType()) + itemStack3.getAmount());
                    } else {
                        hashMap.put(itemStack3.getType(), itemStack3.getAmount());
                    }
                }
                for (final Map.Entry<Material, Integer> entry : hashMap.entrySet()) {
                    String s = "";
                    final int intValue = entry.getValue();
                    switch (entry.getKey()) {
                        case DIAMOND:
                            s = Language.getMsg(player2, Messages.PLAYER_DIE_REWARD_DIAMOND).replace("{meaning}", (intValue == 1) ? Language.getMsg(player2, Messages.MEANING_DIAMOND_SINGULAR) : Language.getMsg(player2, Messages.MEANING_DIAMOND_PLURAL));
                            break;
                        case EMERALD:
                            s = Language.getMsg(player2, Messages.PLAYER_DIE_REWARD_EMERALD).replace("{meaning}", (intValue == 1) ? Language.getMsg(player2, Messages.MEANING_EMERALD_SINGULAR) : Language.getMsg(player2, Messages.MEANING_EMERALD_PLURAL));
                            break;
                        case IRON_INGOT:
                            s = Language.getMsg(player2, Messages.PLAYER_DIE_REWARD_IRON).replace("{meaning}", (intValue == 1) ? Language.getMsg(player2, Messages.MEANING_IRON_SINGULAR) : Language.getMsg(player2, Messages.MEANING_IRON_PLURAL));
                            break;
                        case GOLD_INGOT:
                            s = Language.getMsg(player2, Messages.PLAYER_DIE_REWARD_GOLD).replace("{meaning}", (intValue == 1) ? Language.getMsg(player2, Messages.MEANING_GOLD_SINGULAR) : Language.getMsg(player2, Messages.MEANING_GOLD_PLURAL));
                            break;
                    }
                    player2.sendMessage(s.replace("{amount}", String.valueOf(intValue)));
                }
                hashMap.clear();
            }
        }
        return true;
    }
    
    private static void dropItems(final Player player, final List<ItemStack> list) {
        for (final ItemStack itemStack : list) {
            if (itemStack == null) continue;
            if (itemStack.getType() == Material.AIR) continue;
            if (itemStack.getType() != Material.DIAMOND && itemStack.getType() != Material.EMERALD && itemStack.getType() != Material.IRON_INGOT && itemStack.getType() != Material.GOLD_INGOT) continue;
            player.getLocation().getWorld().dropItemNaturally(player.getLocation(), itemStack);
        }
    }
}
