package dev.eugenio.nasgarbedwars.api.arena.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IContentTier {
    int getPrice();

    void setPrice(final int p0);

    Material getCurrency();

    void setCurrency(final Material p0);

    ItemStack getItemStack();

    void setItemStack(final ItemStack p0);

    int getValue();

    List<IBuyItem> getBuyItemsList();

    void setBuyItemsList(final List<IBuyItem> p0);
}
