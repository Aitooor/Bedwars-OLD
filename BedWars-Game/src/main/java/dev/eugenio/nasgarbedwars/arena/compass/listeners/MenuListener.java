package dev.eugenio.nasgarbedwars.arena.compass.listeners;

import de.tr7zw.nbtapi.NBTItem;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;

public class MenuListener implements Listener {
    @EventHandler
    public void onMenuClick(InventoryClickEvent e){
        final InventoryHolder holder = e.getInventory().getHolder();
        if (e.getCurrentItem() == null || e.getCurrentItem().getType().toString().contains("AIR")) return;

        final NBTItem nbtItem = new NBTItem(e.getCurrentItem());
        if (nbtItem.getString("data").equals("compass-item"))  {
            if (e.getView().getTopInventory().getType().equals(InventoryType.CRAFTING) && e.getView().getBottomInventory().getType().equals(InventoryType.PLAYER)) return;
            e.setCancelled(true);
        }

        if (!(holder instanceof Menu)) return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)) return;

        final Menu menu = (Menu) holder;
        menu.handleMenu(e);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        if (BedWars.getInstance().getApi().getVersionSupport().getItemInHand(player) == null) return;
        if (BedWars.getInstance().getApi().getVersionSupport().getItemInHand(player).getType().toString().contains("AIR")) return;
        final NBTItem nbtItem = new NBTItem(BedWars.getInstance().getApi().getVersionSupport().getItemInHand(player));
        if (!nbtItem.getString("data").equals("compass-item")) return;
        Bukkit.dispatchCommand(player, "bw compass");
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getCursor() == null) return;
        if (e.getCursor().getType() == Material.AIR) return;
        final NBTItem nbtItem = new NBTItem(e.getCursor());
        if (nbtItem.getString("data").equals("compass-item")) e.setCancelled(true);
    }
}