package dev.eugenio.nasgarbedwars.api.arena.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface ICategoryContent {
    int getSlot();

    ItemStack getItemStack(final Player p0);

    boolean hasQuick(final Player p0);

    boolean isPermanent();

    boolean isDowngradable();

    String getIdentifier();

    List<IContentTier> getContentTiers();
}
