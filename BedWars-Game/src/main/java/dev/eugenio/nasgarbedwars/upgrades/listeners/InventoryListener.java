package dev.eugenio.nasgarbedwars.upgrades.listeners;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.upgrades.MenuContent;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.upgrades.UpgradesManager;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryListener implements Listener {
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (event.isCancelled()) return;

        final HumanEntity humanEntity = event.getWhoClicked();
        if (!(humanEntity instanceof Player)) return;

        final Player player = (Player) event.getWhoClicked();

        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) return;

        if (arenaByPlayer.isSpectator(player)) return;

        if (!UpgradesManager.isWatchingUpgrades(player.getUniqueId())) return;

        event.setCancelled(true);

        if (event.getCurrentItem() == null) return;

        if (event.getCurrentItem().getType() == Material.AIR) return;

        final MenuContent menuContent = UpgradesManager.getMenuContent(event.getCurrentItem());
        if (menuContent == null) return;

        menuContent.onClick(player, event.getClick(), arenaByPlayer.getTeam((Player)event.getWhoClicked()));
    }
    
    @EventHandler
    public void onUpgradesClose(final InventoryCloseEvent inventoryCloseEvent) {
        UpgradesManager.removeWatchingUpgrades(inventoryCloseEvent.getPlayer().getUniqueId());
    }
}
