package dev.eugenio.nasgarbedwars.shop.listeners;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.arena.Arena;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDropListener implements Listener {
    @EventHandler
    public void onDrop(final PlayerDropItemEvent event) {
        if (Arena.getArenaByPlayer(event.getPlayer()) == null) return;
        final String shopUpgradeIdentifier = BedWars.getInstance().getNms().getShopUpgradeIdentifier(event.getItemDrop().getItemStack());
        if (shopUpgradeIdentifier == null) return;
        if (shopUpgradeIdentifier.isEmpty() || shopUpgradeIdentifier.equals(" ")) return;
        if (shopUpgradeIdentifier.equals("null")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent event) {
        if (!(event instanceof Player)) return;
        if (Arena.getArenaByPlayer((Player) event.getPlayer()) == null) return;
        for (final ItemStack itemStack : event.getInventory()) {
            if (itemStack == null) continue;
            if (itemStack.getType() == Material.AIR) continue;
            final String shopUpgradeIdentifier = BedWars.getInstance().getNms().getShopUpgradeIdentifier(itemStack);
            if (shopUpgradeIdentifier.isEmpty() || shopUpgradeIdentifier.equals(" ")) return;
        }
    }
}
