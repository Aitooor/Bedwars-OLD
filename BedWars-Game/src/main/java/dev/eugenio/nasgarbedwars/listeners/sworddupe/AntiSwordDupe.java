package dev.eugenio.nasgarbedwars.listeners.sworddupe;

import dev.eugenio.nasgarbedwars.BedWars;
import net.minecraft.server.v1_8_R3.PacketPlayOutCloseWindow;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class AntiSwordDupe implements Listener {
    @EventHandler
    public void onAntiRageAndDupe(InventoryClickEvent event) {
        if (event.getSlot() == -999) return;
        if (event.getInventory().getType().equals(InventoryType.CHEST) || event.getInventory().getType().equals(InventoryType.ENDER_CHEST)) {
            if (event.getCurrentItem().getType() == Material.WOOD_SWORD) event.setCancelled(true);
            if (event.getCurrentItem().getType() == Material.WOOD_AXE || event.getCurrentItem().getType() == Material.WOOD_PICKAXE || event.getCurrentItem().getType() == Material.IRON_AXE || event.getCurrentItem().getType() == Material.IRON_PICKAXE || event.getCurrentItem().getType() == Material.GOLD_AXE || event.getCurrentItem().getType() == Material.GOLD_PICKAXE || event.getCurrentItem().getType() == Material.DIAMOND_AXE || event.getCurrentItem().getType() == Material.DIAMOND_PICKAXE || event.getCurrentItem().getType() == Material.SHEARS || event.getCurrentItem().getType() == Material.COMPASS)
                event.setCancelled(true);
            if (event.getAction().toString().equals("HOTBAR_SWAP")) {
                final ItemStack itemStack = event.getClick() == ClickType.NUMBER_KEY ? event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) : event.getCurrentItem();
                if (!(itemStack.getType() == Material.WOOD_SWORD || itemStack.getType() == Material.WOOD_AXE || itemStack.getType() == Material.WOOD_PICKAXE || itemStack.getType() == Material.IRON_AXE || itemStack.getType() == Material.IRON_PICKAXE || itemStack.getType() == Material.GOLD_AXE || itemStack.getType() == Material.GOLD_PICKAXE || itemStack.getType() == Material.DIAMOND_AXE || itemStack.getType() == Material.DIAMOND_PICKAXE || itemStack.getType() == Material.SHEARS || itemStack.getType() == Material.COMPASS))
                    return;
                event.setCancelled(true);
                event.getClickedInventory().removeItem(itemStack);
            }
        } else if (event.getInventory().getType().equals(InventoryType.CRAFTING)) {
            if ((event.getRawSlot() == 1 || event.getRawSlot() == 2 || event.getRawSlot() == 3 || event.getRawSlot() == 4)) {
                event.setCancelled(true);
                Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
                    if (event.getWhoClicked().getItemOnCursor() == null) return;
                    event.getWhoClicked().getInventory().addItem(event.getWhoClicked().getItemOnCursor());
                    event.getWhoClicked().setItemOnCursor(null);
                    ((CraftPlayer) event.getWhoClicked()).getHandle().playerConnection.sendPacket(new PacketPlayOutCloseWindow());
                }, 1L);
            }
        }
    }

    @EventHandler
    public void onAntiRageAndDupe(InventoryDragEvent event) {
        if (event.getInventory().getSize() == 54) {
            event.setCancelled(true);
            return;
        }
        if (event.getInventory().getType().equals(InventoryType.CHEST) || event.getInventory().getType().equals(InventoryType.ENDER_CHEST)) {
            if (event.getCursor().getType() == null) event.setCancelled(true);
            if (event.getCursor().getType() == Material.WOOD_SWORD) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSwordDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getType() == Material.WOOD_SWORD) {
            event.setCancelled(true);
            ((CraftPlayer) event.getPlayer()).getHandle().playerConnection.sendPacket(new PacketPlayOutCloseWindow());
            Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
                if (event.getPlayer().getInventory().contains(Material.WOOD_SWORD, 2)) {
                    for (ItemStack itemStack : event.getPlayer().getInventory()) {
                        if (itemStack == null) continue;
                        if (itemStack.getType() == Material.WOOD_SWORD) event.getPlayer().getInventory().removeItem(itemStack);
                        event.getPlayer().updateInventory();
                        break;
                    }
                }
            }, 1L);
        }
    }
}
