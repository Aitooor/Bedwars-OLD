package dev.eugenio.nasgarbedwars.listeners;

import net.minecraft.server.v1_8_R3.PacketPlayOutCloseWindow;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class DenyRageDrop implements Listener {
    @EventHandler
    public void onRageDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().getFallDistance() > 4.5) if ((event.getItemDrop().getItemStack().getType() == Material.DIAMOND) || (event.getItemDrop().getItemStack().getType() == Material.EMERALD) || (event.getItemDrop().getItemStack().getType() == Material.GOLD_INGOT) || (event.getItemDrop().getItemStack().getType() == Material.IRON_INGOT)) if (event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            event.setCancelled(true);
            ((CraftPlayer) event.getPlayer()).getHandle().playerConnection.sendPacket(new PacketPlayOutCloseWindow());
        }
    }

    @EventHandler
    public void onRageDrag(InventoryDragEvent event) {
        if (event.getWhoClicked().getFallDistance() > 2.0) if ((event.getCursor().getType() == Material.DIAMOND) || (event.getCursor().getType() == Material.EMERALD) || (event.getCursor().getType() == Material.GOLD_INGOT) || (event.getCursor().getType() == Material.IRON_INGOT)) if (event.getWhoClicked().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            event.setCancelled(true);
            ((CraftPlayer) event.getWhoClicked()).getHandle().playerConnection.sendPacket(new PacketPlayOutCloseWindow());
        }
    }

    @EventHandler
    public void onRageClick(InventoryClickEvent event) {
        if (event.getWhoClicked().getFallDistance() > 2.0) if ((event.getCursor().getType() == Material.DIAMOND) || (event.getCursor().getType() == Material.EMERALD) || (event.getCursor().getType() == Material.GOLD_INGOT) || (event.getCursor().getType() == Material.IRON_INGOT)) if (event.getWhoClicked().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            event.setCancelled(true);
            ((CraftPlayer) event.getWhoClicked()).getHandle().playerConnection.sendPacket(new PacketPlayOutCloseWindow());
        }
    }
}
