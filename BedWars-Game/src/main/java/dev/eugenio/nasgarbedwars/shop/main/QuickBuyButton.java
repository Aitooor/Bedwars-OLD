package dev.eugenio.nasgarbedwars.shop.main;

import dev.eugenio.nasgarbedwars.api.language.Language;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class QuickBuyButton {
    @Getter
    private final int slot;
    private final ItemStack itemStack;
    private final String namePath;
    private final String lorePath;

    public QuickBuyButton(final int slot, final ItemStack itemStack, final String namePath, final String lorePath) {
        this.slot = slot;
        this.itemStack = itemStack;
        this.namePath = namePath;
        this.lorePath = lorePath;
    }

    public ItemStack getItemStack(final Player player) {
        final ItemStack clone = this.itemStack.clone();
        final ItemMeta itemMeta = clone.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(Language.getMsg(player, this.namePath));
            itemMeta.setLore(Language.getList(player, this.lorePath));
            clone.setItemMeta(itemMeta);
        }
        return clone;
    }
}
