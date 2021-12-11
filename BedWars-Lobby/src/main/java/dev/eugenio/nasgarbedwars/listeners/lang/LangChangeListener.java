package dev.eugenio.nasgarbedwars.listeners.lang;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class LangChangeListener implements Listener {
    @EventHandler
    public void onMenu(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR) || event.getSlotType().equals(InventoryType.SlotType.OUTSIDE)) return;
        if (event.getAction().toString().equals("HOTBAR_SWAP")) {
            event.setCancelled(true);
            return;
        }
        if (event.getView().getTitle().equals("Idioma")) {
            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();
            String d = item.getItemMeta().getDisplayName();
            switch (d) {
                case "§e§lEspañol":
                    Language.setPlayerLanguage(event.getWhoClicked().getUniqueId(), "es");
                    player.closeInventory();
                    Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> player.sendMessage(Language.getMsg(player, Messages.COMMAND_LANG_SELECTED_SUCCESSFULLY)), 3L);
                    break;
                case "§e§lEnglish":
                    Language.setPlayerLanguage(event.getWhoClicked().getUniqueId(), "en");
                    player.closeInventory();
                    Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> player.sendMessage(Language.getMsg(player, Messages.COMMAND_LANG_SELECTED_SUCCESSFULLY)), 3L);
                    break;
                case "§9Volver":
                    player.closeInventory();
                    break;
            }
        }
    }
}
