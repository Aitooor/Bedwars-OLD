package dev.eugenio.nasgarbedwars.shop.main;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.shop.quickbuy.PlayerQuickBuyCache;
import dev.eugenio.nasgarbedwars.api.events.shop.ShopOpenEvent;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.shop.ShopCache;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShopIndex {
    public static List<UUID> indexViewers;

    static {
        ShopIndex.indexViewers = new ArrayList<>();
    }

    @Getter
    private final int invSize;
    @Getter
    private final String namePath;
    private final String separatorNamePath;
    private final String separatorLorePath;
    @Getter
    private final List<ShopCategory> categoryList;
    @Getter
    private final QuickBuyButton quickBuyButton;
    public ItemStack separatorSelected;
    public ItemStack separatorStandard;

    public ShopIndex(final String namePath, final QuickBuyButton quickBuyButton, final String separatorNamePath, final String separatorLorePath, final ItemStack separatorSelected, final ItemStack separatorStandard) {
        this.invSize = 54;
        this.categoryList = new ArrayList<>();
        this.namePath = namePath;
        this.separatorLorePath = separatorLorePath;
        this.separatorNamePath = separatorNamePath;
        this.quickBuyButton = quickBuyButton;
        this.separatorStandard = separatorStandard;
        this.separatorSelected = separatorSelected;
    }

    public static List<UUID> getIndexViewers() {
        return new ArrayList<>(ShopIndex.indexViewers);
    }

    public void open(final Player player, final PlayerQuickBuyCache playerQuickBuyCache, final boolean b) {
        if (playerQuickBuyCache == null) {
            return;
        }
        if (b) {
            final ShopOpenEvent shopOpenEvent = new ShopOpenEvent(player);
            Bukkit.getPluginManager().callEvent(shopOpenEvent);
            if (shopOpenEvent.isCancelled()) return;
        }
        final Inventory inventory = Bukkit.createInventory(null, this.invSize, Language.getMsg(player, this.getNamePath()));
        inventory.setItem(this.getQuickBuyButton().getSlot(), this.getQuickBuyButton().getItemStack(player));
        for (final ShopCategory shopCategory : this.getCategoryList())
            inventory.setItem(shopCategory.getSlot(), shopCategory.getItemStack(player));
        this.addSeparator(player, inventory);
        inventory.setItem(this.getQuickBuyButton().getSlot() + 9, this.getSelectedItem(player));
        ShopCache.getShopCache(player.getUniqueId()).setSelectedCategory(this.getQuickBuyButton().getSlot());
        playerQuickBuyCache.addInInventory(inventory, ShopCache.getShopCache(player.getUniqueId()));
        player.openInventory(inventory);
        if (!ShopIndex.indexViewers.contains(player.getUniqueId())) ShopIndex.indexViewers.add(player.getUniqueId());
    }

    public void addSeparator(final Player player, final Inventory inventory) {
        final ItemStack clone = this.separatorStandard.clone();
        final ItemMeta itemMeta = clone.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(Language.getMsg(player, this.separatorNamePath));
            itemMeta.setLore(Language.getList(player, this.separatorLorePath));
            clone.setItemMeta(itemMeta);
        }
        for (int i = 9; i < 18; ++i) inventory.setItem(i, clone);
    }

    public ItemStack getSelectedItem(final Player player) {
        final ItemStack clone = this.separatorSelected.clone();
        final ItemMeta itemMeta = clone.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(Language.getMsg(player, this.separatorNamePath));
            itemMeta.setLore(Language.getList(player, this.separatorLorePath));
            clone.setItemMeta(itemMeta);
        }
        return clone;
    }

    public void addShopCategory(final ShopCategory shopCategory) {
        this.categoryList.add(shopCategory);
        BedWars.debug("Añadiendo categoría de tienda: " + shopCategory + " en slot " + shopCategory.getSlot());
    }
}
