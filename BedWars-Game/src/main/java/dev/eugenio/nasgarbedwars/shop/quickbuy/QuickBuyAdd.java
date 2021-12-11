package dev.eugenio.nasgarbedwars.shop.quickbuy;

import dev.eugenio.nasgarbedwars.shop.ShopCache;
import dev.eugenio.nasgarbedwars.shop.ShopManager;
import dev.eugenio.nasgarbedwars.shop.main.CategoryContent;
import dev.eugenio.nasgarbedwars.shop.main.ShopCategory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class QuickBuyAdd {
    public static HashMap<UUID, CategoryContent> quickBuyAdds;

    static {
        QuickBuyAdd.quickBuyAdds = new HashMap<>();
    }

    public QuickBuyAdd(final Player player, final CategoryContent categoryContent) {
        ShopCategory.categoryViewers.remove(player.getUniqueId());
        this.open(player, categoryContent);
    }

    public static HashMap<UUID, CategoryContent> getQuickBuyAdds() {
        return new HashMap<>(QuickBuyAdd.quickBuyAdds);
    }

    public void open(final Player player, final CategoryContent categoryContent) {
        final Inventory inventory = Bukkit.createInventory(null, ShopManager.getShop().getInvSize());
        final PlayerQuickBuyCache quickBuyCache = PlayerQuickBuyCache.getQuickBuyCache(player.getUniqueId());
        final ShopCache shopCache = ShopCache.getShopCache(player.getUniqueId());
        if (shopCache == null || quickBuyCache == null) player.closeInventory();
        inventory.setItem(4, categoryContent.getItemStack(player, Objects.requireNonNull(shopCache)));
        Objects.requireNonNull(quickBuyCache).addInInventory(inventory, shopCache);
        player.openInventory(inventory);
        QuickBuyAdd.quickBuyAdds.put(player.getUniqueId(), categoryContent);
    }
}
