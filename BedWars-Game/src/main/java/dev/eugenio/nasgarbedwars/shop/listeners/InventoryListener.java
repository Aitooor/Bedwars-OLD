package dev.eugenio.nasgarbedwars.shop.listeners;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.shop.ShopCache;
import dev.eugenio.nasgarbedwars.shop.ShopManager;
import dev.eugenio.nasgarbedwars.shop.main.CategoryContent;
import dev.eugenio.nasgarbedwars.shop.main.ShopCategory;
import dev.eugenio.nasgarbedwars.shop.main.ShopIndex;
import dev.eugenio.nasgarbedwars.shop.quickbuy.PlayerQuickBuyCache;
import dev.eugenio.nasgarbedwars.shop.quickbuy.QuickBuyAdd;
import dev.eugenio.nasgarbedwars.shop.quickbuy.QuickBuyElement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {
    static int slotClick = 0;
    int slotClicked;

    public static boolean isUpgradable(final ItemStack itemStack, final ShopCache shopCache) {
        if (itemStack == null) return false;
        if (shopCache == null) return false;
        final String shopUpgradeIdentifier = BedWars.getInstance().getNms().getShopUpgradeIdentifier(itemStack);
        return shopUpgradeIdentifier != null && !shopUpgradeIdentifier.equals("null") && shopCache.getCachedItem(shopUpgradeIdentifier) != null && shopCache.getCachedItem(shopUpgradeIdentifier).getCc().getContentTiers().size() > 1;
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {
            slotClicked = event.getSlot();
        } else {
            if (event.getClickedInventory().getType() == InventoryType.PLAYER) return;
        }
        slotClicked = event.getSlot();
        if (event.isCancelled()) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        final IArena arenaByPlayer = Arena.getArenaByPlayer((Player) event.getWhoClicked());
        if (arenaByPlayer == null) return;
        if (arenaByPlayer.isSpectator((Player) event.getWhoClicked())) return;
        if (event.getSlot() == 0 || event.getSlot() == 1 || event.getSlot() == 2 || event.getSlot() == 3 || event.getSlot() == 4 || event.getSlot() == 6 || event.getSlot() == 7) slotClick = event.getSlot();
        if (event.getSlot() == -999) {
            if (event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.SHIFT_RIGHT) {
                switch (slotClick) {
                    case 0:
                        slotClicked = 1;
                        slotClick++;
                        break;
                    case 1:
                        slotClicked = 2;
                        slotClick++;
                        break;
                    case 2:
                        slotClicked = 3;
                        slotClick++;
                        break;
                    case 3:
                        slotClicked = 4;
                        slotClick++;
                        break;
                    case 4:
                        slotClicked = 5;
                        slotClick++;
                        break;
                    case 5:
                        slotClicked = 6;
                        slotClick++;
                        break;
                    case 6:
                        slotClicked = 7;
                        slotClick++;
                        break;
                }
            } else if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.SHIFT_LEFT) {
                switch (slotClick) {
                    case 0:
                        slotClicked = -1;
                        break;
                    case 1:
                        slotClicked = 0;
                        break;
                    case 2:
                        slotClicked = 1;
                        slotClick--;
                        break;
                    case 3:
                        slotClicked = 2;
                        slotClick--;
                        break;
                    case 4:
                        slotClicked = 3;
                        slotClick--;
                        break;
                    case 5:
                        slotClicked = 4;
                        slotClick--;
                        break;
                    case 6:
                        slotClicked = 5;
                        slotClick--;
                        break;
                    case 7:
                        slotClicked = 6;
                        slotClick--;
                        break;
                }
            }
        }
        final ShopCache shopCache = ShopCache.getShopCache(event.getWhoClicked().getUniqueId());
        final PlayerQuickBuyCache quickBuyCache = PlayerQuickBuyCache.getQuickBuyCache(event.getWhoClicked().getUniqueId());
        if (quickBuyCache == null) return;
        if (shopCache == null) return;
        if (ShopIndex.getIndexViewers().contains(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
            for (final ShopCategory shopCategory : ShopManager.getShop().getCategoryList()) {
                if (slotClicked == shopCategory.getSlot()) {
                    shopCategory.open((Player) event.getWhoClicked(), ShopManager.getShop(), shopCache);
                    return;
                }
            }

            for (final QuickBuyElement quickBuyElement : quickBuyCache.getElements()) {
                if (quickBuyElement.getSlot() == event.getSlot()) {
                    if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                        quickBuyCache.setElement(quickBuyElement.getSlot(), null);
                        event.getWhoClicked().closeInventory();
                        ShopManager.getShop().open((Player) event.getWhoClicked(), quickBuyCache, false);
                        return;
                    }
                    quickBuyElement.getCategoryContent().execute((Player) event.getWhoClicked(), shopCache, quickBuyElement.getSlot());
                }
            }
        } else if (ShopCategory.getCategoryViewers().contains(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
            for (final ShopCategory shopCategory2 : ShopManager.getShop().getCategoryList()) {
                if (ShopManager.getShop().getQuickBuyButton().getSlot() == event.getSlot()) {
                    ShopManager.getShop().open((Player) event.getWhoClicked(), quickBuyCache, false);
                    return;
                }
                if (slotClicked == shopCategory2.getSlot()) {
                    shopCategory2.open((Player) event.getWhoClicked(), ShopManager.getShop(), shopCache);
                    return;
                }
                if (shopCategory2.getSlot() != shopCache.getSelectedCategory()) continue;
                for (final CategoryContent categoryContent : shopCategory2.getCategoryContentList()) {
                    if (categoryContent.getSlot() == event.getSlot()) {
                        if (event.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                            categoryContent.execute((Player) event.getWhoClicked(), shopCache, categoryContent.getSlot());
                            return;
                        }
                        if (quickBuyCache.hasCategoryContent(categoryContent)) return;
                        new QuickBuyAdd((Player) event.getWhoClicked(), categoryContent);
                    }
                }
            }
        } else if (QuickBuyAdd.getQuickBuyAdds().containsKey(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
            boolean b = false;
            final int[] quickSlots = PlayerQuickBuyCache.quickSlots;
            for (int length = quickSlots.length, i = 0; i < length; ++i) {
                if (quickSlots[i] == event.getSlot()) b = true;
            }
            if (!b) return;
            final CategoryContent categoryContent2 = QuickBuyAdd.getQuickBuyAdds().get(event.getWhoClicked().getUniqueId());
            if (categoryContent2 != null) quickBuyCache.setElement(event.getSlot(), categoryContent2);
            event.getWhoClicked().closeInventory();
            ShopManager.getShop().open((Player) event.getWhoClicked(), quickBuyCache, false);
        }
    }

    @EventHandler
    public void onUpgradableMove(final InventoryClickEvent event) {
        final ShopCache shopCache = ShopCache.getShopCache(event.getWhoClicked().getUniqueId());
        if (shopCache == null) return;
        if (event.getAction() == InventoryAction.HOTBAR_SWAP && event.getClick() == ClickType.NUMBER_KEY && event.getHotbarButton() > -1) {
            final ItemStack item = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
            if (event.getClickedInventory().getType() != event.getWhoClicked().getInventory().getType() && isUpgradable(item, shopCache)) {
                event.setCancelled(true);
            }
        }
        if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
            if (event.getClickedInventory() == null) {
                if (isUpgradable(event.getCursor(), shopCache)) {
                    event.getWhoClicked().closeInventory();
                    event.setCancelled(true);
                }
            } else if (event.getClickedInventory().getType() != event.getWhoClicked().getInventory().getType() && isUpgradable(event.getCursor(), shopCache)) {
                event.getWhoClicked().closeInventory();
                event.setCancelled(true);
            }
        }
        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
            if (event.getClickedInventory() == null) {
                if (isUpgradable(event.getCursor(), shopCache)) {
                    event.getWhoClicked().closeInventory();
                    event.setCancelled(true);
                }
            } else if (event.getClickedInventory().getType() != event.getWhoClicked().getInventory().getType() && isUpgradable(event.getCurrentItem(), shopCache)) {
                event.getWhoClicked().closeInventory();
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onShopClose(final InventoryCloseEvent inventoryCloseEvent) {
        ShopIndex.indexViewers.remove(inventoryCloseEvent.getPlayer().getUniqueId());
        ShopCategory.categoryViewers.remove(inventoryCloseEvent.getPlayer().getUniqueId());
        QuickBuyAdd.quickBuyAdds.remove(inventoryCloseEvent.getPlayer().getUniqueId());
    }
}
