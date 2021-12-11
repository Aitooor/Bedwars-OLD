package dev.eugenio.nasgarbedwars.shop.quickbuy;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.shop.ShopCache;
import dev.eugenio.nasgarbedwars.shop.ShopManager;
import dev.eugenio.nasgarbedwars.shop.main.CategoryContent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerQuickBuyCache {
    public static int[] quickSlots;
    private static ConcurrentHashMap<UUID, PlayerQuickBuyCache> quickBuyCaches;

    static {
        PlayerQuickBuyCache.quickSlots = new int[]{19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
        PlayerQuickBuyCache.quickBuyCaches = new ConcurrentHashMap<>();
    }

    private final List<QuickBuyElement> elements;
    private String emptyItemNamePath;
    private String emptyItemLorePath;
    private ItemStack emptyItem;
    private UUID player;
    private QuickBuyTask task;

    public PlayerQuickBuyCache(final Player player) {
        this.elements = new ArrayList<>();
        if (player == null) return;
        this.player = player.getUniqueId();
        this.emptyItem = BedWars.getInstance().getNms().createItemStack(BedWars.getInstance().getShopManager().getYml().getString("shop-settings.quick-buy-empty-item.material"), BedWars.getInstance().getShopManager().getYml().getInt("shop-settings.quick-buy-empty-item.amount"), (short) BedWars.getInstance().getShopManager().getYml().getInt("shop-settings.quick-buy-empty-item.data"));
        if (BedWars.getInstance().getShopManager().getYml().getBoolean("shop-settings.quick-buy-empty-item.enchanted"))
            this.emptyItem = ShopManager.enchantItem(this.emptyItem);
        this.emptyItemNamePath = "shop-items-messages.quick-buy-empty-item-name";
        this.emptyItemLorePath = "shop-items-messages.quick-buy-empty-item-lore";
        this.task = new QuickBuyTask(player.getUniqueId());
        PlayerQuickBuyCache.quickBuyCaches.put(this.player, this);
    }

    @Nullable
    public static PlayerQuickBuyCache getQuickBuyCache(final UUID uuid) {
        return PlayerQuickBuyCache.quickBuyCaches.getOrDefault(uuid, null);
    }

    public void addInInventory(final Inventory inventory, final ShopCache shopCache) {
        final Player player = Bukkit.getPlayer(this.player);
        for (final QuickBuyElement quickBuyElement : this.elements)
            inventory.setItem(quickBuyElement.getSlot(), quickBuyElement.getCategoryContent().getItemStack(player, shopCache));
        if (this.elements.size() == 21) return;
        final ItemStack emptyItem = this.getEmptyItem(player);
        for (final int n : PlayerQuickBuyCache.quickSlots)
            if (inventory.getItem(n) == null) inventory.setItem(n, emptyItem);
    }

    public void destroy() {
        this.elements.clear();
        if (this.task != null) this.task.cancel();
        PlayerQuickBuyCache.quickBuyCaches.remove(this.player);
    }

    public void setElement(final int n, final CategoryContent categoryContent) {
        String s;
        this.elements.removeIf(quickBuyElement -> quickBuyElement.getSlot() == n);
        if (categoryContent == null) {
            s = "";
        } else {
            this.addQuickElement(new QuickBuyElement(categoryContent.getIdentifier(), n));
            s = categoryContent.getIdentifier();
        }
        BedWars.getInstance().getServer().getScheduler().runTaskAsynchronously(BedWars.getInstance(), () -> BedWars.getInstance().getMySQLDatabase().setQuickBuySlot(this.player, s, n));
    }

    private ItemStack getEmptyItem(final Player player) {
        final ItemStack clone = this.emptyItem.clone();
        final ItemMeta itemMeta = clone.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(Language.getMsg(player, this.emptyItemNamePath));
            itemMeta.setLore(Language.getList(player, this.emptyItemLorePath));
            clone.setItemMeta(itemMeta);
        }
        return clone;
    }

    public boolean hasCategoryContent(final CategoryContent categoryContent) {
        for (QuickBuyElement quickBuyElement : this.getElements())
            if (quickBuyElement.getCategoryContent() == categoryContent) return true;
        return false;
    }

    public List<QuickBuyElement> getElements() {
        return this.elements;
    }

    public void addQuickElement(final QuickBuyElement quickBuyElement) {
        this.elements.add(quickBuyElement);
    }
}
