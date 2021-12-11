package dev.eugenio.nasgarbedwars.arena.spectator;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeleporterGUI {
    private static HashMap<Player, Inventory> refresh;
    
    public static void refreshInv(final Player player, final Inventory inventory) {
        if (player.getOpenInventory() == null) return;
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) {
            player.closeInventory();
            return;
        }
        final List<Player> players = arenaByPlayer.getPlayers();
        for (int i = 0; i < inventory.getSize(); ++i) {
            if (i < players.size()) {
                inventory.setItem(i, createHead(players.get(i), player));
            } else {
                inventory.setItem(i, new ItemStack(Material.AIR));
            }
        }
    }
    
    public static void openGUI(final Player player) {
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) return;
        final int size = arenaByPlayer.getPlayers().size();
        int n;
        if (size <= 9) {
            n = 9;
        } else if (size <= 18) {
            n = 18;
        } else if (size > 19 && size <= 27) {
            n = 27;
        } else if (size > 27 && size <= 36) {
            n = 36;
        } else if (size > 36 && size <= 45) {
            n = 45;
        } else {
            n = 54;
        }
        final Inventory inventory = Bukkit.createInventory(player, n, Language.getMsg(player, Messages.ARENA_SPECTATOR_TELEPORTER_GUI_NAME));
        refreshInv(player, inventory);
        TeleporterGUI.refresh.put(player, inventory);
        player.openInventory(inventory);
    }
    
    public static HashMap<Player, Inventory> getRefresh() {
        return TeleporterGUI.refresh;
    }

    public static void refreshAllGUIs() {
        for (Map.Entry<?, ?> entry : (new HashMap<>(getRefresh())).entrySet()) refreshInv((Player)entry.getKey(), (Inventory)entry.getValue());
    }
    
    private static ItemStack createHead(final Player player, final Player player2) {
        final ItemStack setSkullOwner = BedWars.getInstance().getNms().setSkullOwner(BedWars.getInstance().getNms().createItemStack(BedWars.getInstance().getNms().materialPlayerHead().toString(), 1, (short)3), player);
        final ItemMeta itemMeta = setSkullOwner.getItemMeta();
        itemMeta.setDisplayName(Language.getMsg(player2, Messages.ARENA_SPECTATOR_TELEPORTER_GUI_HEAD_NAME).replace("{prefix}", BedWars.getInstance().getChat().getPrefix(player)).replace("{suffix}", BedWars.getInstance().getChat().getSuffix(player)).replace("{player}", player.getDisplayName()));
        final ArrayList<String> lore = new ArrayList<>();
        final String value = String.valueOf((int)player.getHealth() * 100 / player.getHealthScale());
        for (String s : Language.getList(player2, Messages.ARENA_SPECTATOR_TELEPORTER_GUI_HEAD_LORE)) {
            lore.add(s.replace("{health}", value).replace("{food}", String.valueOf(player.getFoodLevel())));
        }
        itemMeta.setLore(lore);
        setSkullOwner.setItemMeta(itemMeta);
        return BedWars.getInstance().getNms().addCustomData(setSkullOwner, "spectatorTeleporterGUIhead_" + player.getName());
    }
    
    public static void closeGUI(final Player player) {
        if (getRefresh().containsKey(player)) {
            TeleporterGUI.refresh.remove(player);
            if (player.getOpenInventory() != null) player.closeInventory();
        }
    }
    
    static {
        TeleporterGUI.refresh = new HashMap<>();
    }
}
