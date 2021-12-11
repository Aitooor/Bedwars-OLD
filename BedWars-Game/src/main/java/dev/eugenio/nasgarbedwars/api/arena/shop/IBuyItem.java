package dev.eugenio.nasgarbedwars.api.arena.shop;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IBuyItem {
    boolean isLoaded();

    void give(final Player p0, final IArena p1);

    String getUpgradeIdentifier();

    ItemStack getItemStack();

    void setItemStack(final ItemStack p0);

    boolean isAutoEquip();

    void setAutoEquip(final boolean p0);

    boolean isPermanent();

    void setPermanent(final boolean p0);
}
