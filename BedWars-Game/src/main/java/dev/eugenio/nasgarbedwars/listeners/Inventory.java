package dev.eugenio.nasgarbedwars.listeners;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.server.SetupType;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.arena.SetupSession;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class Inventory implements Listener {
    @EventHandler
    public void onClose(final InventoryCloseEvent event) {
        final Player player = (Player)event.getPlayer();
        if (BedWars.getInstance().getNms().getInventoryName(event).equalsIgnoreCase(SetupSession.getInvName())) {
            final SetupSession session = SetupSession.getSession(player.getUniqueId());
            if (session != null && session.getSetupType() == null) session.cancel();
        }
    }
    
    @EventHandler
    public void onCommandItemClick(final InventoryClickEvent event) {
        if (event.getAction() == InventoryAction.HOTBAR_SWAP && event.getClick() == ClickType.NUMBER_KEY && event.getHotbarButton() > -1) {
            final ItemStack item = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
            if (isCommandItem(item)) {
                event.setCancelled(true);
                return;
            }
        }
        if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
            if (event.getClickedInventory() == null) {
                if (isCommandItem(event.getCursor())) {
                    event.getWhoClicked().closeInventory();
                    event.setCancelled(true);
                }
            } else if (event.getClickedInventory().getType() != event.getWhoClicked().getInventory().getType()) {
                if (isCommandItem(event.getCursor())) {
                    event.getWhoClicked().closeInventory();
                    event.setCancelled(true);
                }
            } else if (isCommandItem(event.getCursor())) {
                event.setCancelled(true);
            }
        }
        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
            if (event.getClickedInventory() == null) {
                if (isCommandItem(event.getCurrentItem())) {
                    event.getWhoClicked().closeInventory();
                    event.setCancelled(true);
                }
            } else if (event.getClickedInventory().getType() != event.getWhoClicked().getInventory().getType()) {
                if (isCommandItem(event.getCurrentItem())) {
                    event.getWhoClicked().closeInventory();
                    event.setCancelled(true);
                }
            } else if (isCommandItem(event.getCurrentItem())) {
                event.setCancelled(true);
            }
        }
        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && isCommandItem(event.getCurrentItem())) event.setCancelled(true);
    }
    
    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        if (event.getSlotType() == InventoryType.SlotType.ARMOR && Arena.getArenaByPlayer((Player)event.getWhoClicked()) != null && event.getWhoClicked().hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            event.getWhoClicked().closeInventory();
            for (Player player : event.getWhoClicked().getWorld().getPlayers()) BedWars.getInstance().getNms().hideArmor((Player) event.getWhoClicked(), player);
        }
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getType() == Material.AIR) return;
        final Player player = (Player)event.getWhoClicked();
        final ItemStack currentItem = event.getCurrentItem();
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer != null) {
            if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
                event.setCancelled(true);
                return;
            }
        }
        if (!currentItem.hasItemMeta()) return;
        if (!currentItem.getItemMeta().hasDisplayName()) return;
        if (SetupSession.isInSetupSession(player.getUniqueId()) && BedWars.getInstance().getNms().getInventoryName(event).equalsIgnoreCase(SetupSession.getInvName())) {
            final SetupSession session = SetupSession.getSession(player.getUniqueId());
            if (event.getSlot() == SetupSession.getAdvancedSlot()) {
                Objects.requireNonNull(session).setSetupType(SetupType.ADVANCED);
            } else if (event.getSlot() == SetupSession.getAssistedSlot()) {
                Objects.requireNonNull(session).setSetupType(SetupType.ASSISTED);
            }
            if (!Objects.requireNonNull(session).startSetup()) {
                session.getPlayer().sendMessage(ChatColor.RED + "No se ha podido empezar una sesión de setup, mira la consola.");
            }
            player.closeInventory();
            return;
        }
        if (arenaByPlayer != null && arenaByPlayer.isSpectator(player)) event.setCancelled(true);
    }
    
    private static boolean isCommandItem(final ItemStack itemStack) {
        if (itemStack == null) return false;
        if (itemStack.getType() == Material.AIR) return false;
        if (BedWars.getInstance().getNms().isCustomBedWarsItem(itemStack)) {
            final String[] split = BedWars.getInstance().getNms().getCustomData(itemStack).split("_");
            if (split.length >= 2) return split[0].equals("RUNCOMMAND");
        }
        return false;
    }
}
