package dev.eugenio.nasgarbedwars.shop.main;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.shop.ShopCache;
import dev.eugenio.nasgarbedwars.shop.ShopManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class ShopCategory {
    public static List<UUID> categoryViewers;

    static {
        ShopCategory.categoryViewers = new ArrayList<>();
    }

    @Getter
    private final List<CategoryContent> categoryContentList;
    @Getter
    private final String name;
    @Getter
    private int slot;
    private ItemStack itemStack;
    private String itemNamePath;
    private String itemLorePath;
    private String invNamePath;
    private boolean loaded;

    public ShopCategory(final String name, final YamlConfiguration yamlConfiguration) {
        this.loaded = false;
        this.categoryContentList = new ArrayList<>();
        BedWars.debug("Cargando categoría de tienda: " + name);
        this.name = name;
        if (yamlConfiguration.get(name + ".category-item.material") == null) {
            BedWars.getInstance().getLogger().severe("Material de categoría no seteado en: " + name);
            return;
        }
        if (yamlConfiguration.get(name + ".category-slot") == null) {
            BedWars.getInstance().getLogger().severe("Material de categoría no seteado en: " + name);
            return;
        }
        this.slot = yamlConfiguration.getInt(name + ".category-slot");
        if (this.slot < 1 || this.slot > 8) {
            BedWars.getInstance().getLogger().severe("Slot debe de ser n > 1 y n < 9 en: " + name);
            return;
        }
        for (ShopCategory shopCategory : ShopManager.shop.getCategoryList()) {
            if (shopCategory.getSlot() == this.slot) {
                BedWars.getInstance().getLogger().severe("Slot ya está en uso en: " + name);
                return;
            }
        }
        this.itemStack = BedWars.getInstance().getNms().createItemStack(yamlConfiguration.getString(name + ".category-item.material"), (yamlConfiguration.get(name + ".category-item.amount") == null) ? 1 : yamlConfiguration.getInt(name + ".category-item.amount"), (short) ((yamlConfiguration.get(name + ".category-item.data") == null) ? 0 : yamlConfiguration.getInt(name + ".category-item.data")));
        if (yamlConfiguration.get(name + ".category-item.enchanted") != null && yamlConfiguration.getBoolean(name + ".category-item.enchanted"))
            this.itemStack = ShopManager.enchantItem(this.itemStack);
        if (yamlConfiguration.getString(name + ".category-item.potion-display") != null && !yamlConfiguration.getString(name + ".category-item.potion-display").isEmpty())
            this.itemStack = BedWars.getInstance().getNms().setTag(this.itemStack, "Potion", yamlConfiguration.getString(name + ".category-item.potion-display"));
        if (yamlConfiguration.getString(name + ".category-item.potion-color") != null && !yamlConfiguration.getString(name + ".category-item.potion-color").isEmpty())
            this.itemStack = BedWars.getInstance().getNms().setTag(this.itemStack, "CustomPotionColor", yamlConfiguration.getString(name + ".category-item.potion-color"));
        if (this.itemStack.getItemMeta() != null)
            this.itemStack.setItemMeta(ShopManager.hideItemStuff(this.itemStack.getItemMeta()));
        this.itemNamePath = "shop-items-messages.%category%.category-item-name".replace("%category%", name);
        this.itemLorePath = "shop-items-messages.%category%.category-item-lore".replace("%category%", name);
        this.invNamePath = "shop-items-messages.%category%.inventory-name".replace("%category%", name);
        this.loaded = true;
        for (final String s : yamlConfiguration.getConfigurationSection(name + "." + ".category-content").getKeys(false)) {
            final CategoryContent categoryContent = new CategoryContent(name + ".category-content" + "." + s, s, name, yamlConfiguration, this);
            if (categoryContent.isLoaded()) {
                this.categoryContentList.add(categoryContent);
                BedWars.debug("Añadiendo CategoryContent: " + s + " a la categoría de Shop: " + name);
            }
        }
    }

    public static CategoryContent getCategoryContent(final String s, final ShopIndex shopIndex) {
        final Iterator<ShopCategory> iterator = shopIndex.getCategoryList().iterator();
        while (iterator.hasNext()) {
            for (final CategoryContent categoryContent : iterator.next().getCategoryContentList())
                if (categoryContent.getIdentifier().equals(s)) return categoryContent;
        }
        return null;
    }

    public static List<UUID> getCategoryViewers() {
        return new ArrayList<>(ShopCategory.categoryViewers);
    }

    public void open(final Player player, final ShopIndex shopIndex, final ShopCache shopCache) {
        if (player.getOpenInventory().getTopInventory() == null) return;
        ShopIndex.indexViewers.remove(player.getUniqueId());
        final Inventory inventory = Bukkit.createInventory(null, shopIndex.getInvSize(), Language.getMsg(player, this.invNamePath));
        inventory.setItem(shopIndex.getQuickBuyButton().getSlot(), shopIndex.getQuickBuyButton().getItemStack(player));
        for (final ShopCategory shopCategory : shopIndex.getCategoryList())
            inventory.setItem(shopCategory.getSlot(), shopCategory.getItemStack(player));
        shopIndex.addSeparator(player, inventory);
        inventory.setItem(this.getSlot() + 9, shopIndex.getSelectedItem(player));
        shopCache.setSelectedCategory(this.getSlot());
        for (final CategoryContent categoryContent : this.getCategoryContentList())
            inventory.setItem(categoryContent.getSlot(), categoryContent.getItemStack(player, shopCache));
        player.openInventory(inventory);
        if (!ShopCategory.categoryViewers.contains(player.getUniqueId()))
            ShopCategory.categoryViewers.add(player.getUniqueId());
    }

    public ItemStack getItemStack(final Player player) {
        final ItemStack clone = this.itemStack.clone();
        final ItemMeta itemMeta = clone.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(Language.getMsg(player, this.itemNamePath));
            itemMeta.setLore(Language.getList(player, this.itemLorePath));
            clone.setItemMeta(itemMeta);
        }
        return clone;
    }

    public boolean isLoaded() {
        return this.loaded;
    }
}
