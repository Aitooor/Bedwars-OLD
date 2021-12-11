package dev.eugenio.nasgarbedwars.shop.quickbuy;

import dev.eugenio.nasgarbedwars.shop.ShopManager;
import dev.eugenio.nasgarbedwars.shop.main.CategoryContent;
import dev.eugenio.nasgarbedwars.shop.main.ShopCategory;

public class QuickBuyElement {
    private final int slot;
    private final CategoryContent categoryContent;
    private boolean loaded;

    public QuickBuyElement(final String s, final int slot) {
        this.loaded = false;
        this.categoryContent = ShopCategory.getCategoryContent(s, ShopManager.getShop());
        if (this.categoryContent != null) this.loaded = true;
        this.slot = slot;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public int getSlot() {
        return this.slot;
    }

    public CategoryContent getCategoryContent() {
        return this.categoryContent;
    }
}
