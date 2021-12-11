package dev.eugenio.nasgarbedwars.shop;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.shop.main.CategoryContent;
import dev.eugenio.nasgarbedwars.shop.main.ShopCategory;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ShopCache {
    @Getter
    private final UUID player;
    @Getter
    private List<CachedItem> cachedItems;
    @Getter
    private int selectedCategory;
    private HashMap<ShopCategory, Byte> categoryWeight;
    private static List<ShopCache> shopCaches;

    public ShopCache(final UUID player) {
        this.cachedItems = new LinkedList<>();
        this.categoryWeight = new HashMap<>();
        this.player = player;
        this.selectedCategory = ShopManager.getShop().getQuickBuyButton().getSlot();
        ShopCache.shopCaches.add(this);
    }

    public void setSelectedCategory(final int selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public int getContentTier(final String s) {
        final CachedItem cachedItem = this.getCachedItem(s);
        return (cachedItem == null) ? 1 : cachedItem.getTier();
    }

    public static ShopCache getShopCache(final UUID uuid) {
        for (final ShopCache shopCache : new ArrayList<>(ShopCache.shopCaches))
            if (shopCache.player.equals(uuid)) return shopCache;
        return null;
    }

    public void destroy() {
        ShopCache.shopCaches.remove(this);
        this.cachedItems.clear();
        this.cachedItems = null;
        this.categoryWeight = null;
    }

    public void managePermanentsAndDowngradables(final Arena arena) {
        BedWars.debug("Restaurando items permanentes en la muerte a: " + this.player);
        for (CachedItem cachedItem : this.cachedItems) cachedItem.manageDeath(arena);
    }

    public CachedItem getCachedItem(final String s) {
        for (final CachedItem cachedItem : this.cachedItems)
            if (cachedItem.getCc().getIdentifier().equals(s)) return cachedItem;
        return null;
    }

    public boolean hasCachedItem(final CategoryContent categoryContent) {
        for (CachedItem cachedItem : this.cachedItems) if (cachedItem.getCc() == categoryContent) return true;
        return false;
    }

    public CachedItem getCachedItem(final CategoryContent categoryContent) {
        for (final CachedItem cachedItem : this.cachedItems)
            if (cachedItem.getCc() == categoryContent) return cachedItem;
        return null;
    }

    public void upgradeCachedItem(final CategoryContent categoryContent, final int n) {
        final CachedItem cachedItem = this.getCachedItem(categoryContent.getIdentifier());
        if (cachedItem == null) {
            new CachedItem(categoryContent).updateItem(n, Bukkit.getPlayer(this.player));
        } else if (categoryContent.getContentTiers().size() > cachedItem.getTier()) {
            BedWars.debug("Mejora del cached item para " + categoryContent.getIdentifier() + " para jugador " + this.player);
            cachedItem.upgrade(n);
        }
    }

    public void setCategoryWeight(final ShopCategory shopCategory, final byte b) {
        if (this.categoryWeight.containsKey(shopCategory)) {
            this.categoryWeight.replace(shopCategory, b);
        } else {
            this.categoryWeight.put(shopCategory, b);
        }
    }

    public byte getCategoryWeight(final ShopCategory shopCategory) {
        return this.categoryWeight.getOrDefault(shopCategory, (byte) 0);
    }

    public List<CachedItem> getCachedPermanents() {
        final ArrayList<CachedItem> list = new ArrayList<>();
        for (final CachedItem cachedItem : this.cachedItems)
            if (cachedItem.getCc().isPermanent() && !cachedItem.getCc().isDowngradable()) list.add(cachedItem);
        return list;
    }

    static {
        ShopCache.shopCaches = new ArrayList<>();
    }

    public class CachedItem {
        @Getter
        private final CategoryContent cc;
        @Getter
        private int tier;

        public CachedItem(final CategoryContent cc) {
            this.tier = 1;
            this.cc = cc;
            ShopCache.this.cachedItems.add(this);
            BedWars.debug("Nuevo item cacheado " + cc.getIdentifier() + " para jugador " + ShopCache.this.player);
        }

        public void manageDeath(final Arena arena) {
            if (!this.cc.isPermanent()) return;
            if (this.cc.isDowngradable() && this.tier > 1) --this.tier;
            BedWars.debug("ShopCache item restaurado: " + this.cc.getIdentifier() + " para " + ShopCache.this.player);
            this.cc.giveItems(Bukkit.getPlayer(ShopCache.this.player), ShopCache.getShopCache(ShopCache.this.player), arena);
        }

        public void upgrade(final int n) {
            ++this.tier;
            final Player player = Bukkit.getPlayer(ShopCache.this.player);
            for (final ItemStack itemStack : player.getInventory().getContents())
                if (itemStack != null) if (itemStack.getType() != Material.AIR)
                    if (BedWars.getInstance().getNms().getShopUpgradeIdentifier(itemStack).equals(this.cc.getIdentifier()))
                        player.getInventory().remove(itemStack);
            this.updateItem(n, player);
            player.updateInventory();
        }

        public void updateItem(final int n, final Player player) {
            if (player.getOpenInventory() != null && player.getOpenInventory().getTopInventory() != null)
                player.getOpenInventory().getTopInventory().setItem(n, this.cc.getItemStack(Bukkit.getPlayer(ShopCache.this.player), ShopCache.getShopCache(ShopCache.this.player)));
        }
    }
}
