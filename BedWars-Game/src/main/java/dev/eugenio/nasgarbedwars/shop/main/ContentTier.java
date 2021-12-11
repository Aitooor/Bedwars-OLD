package dev.eugenio.nasgarbedwars.shop.main;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.shop.IBuyItem;
import dev.eugenio.nasgarbedwars.api.arena.shop.IContentTier;
import dev.eugenio.nasgarbedwars.shop.ShopManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ContentTier implements IContentTier {
    @Getter
    private int value;
    @Getter
    private int price;
    @Getter
    private ItemStack itemStack;
    @Getter
    private Material currency;
    @Getter
    private List<IBuyItem> buyItemsList;
    private boolean loaded;

    public ContentTier(final String s, final String s2, final String s3, final YamlConfiguration yamlConfiguration) {
        this.buyItemsList = new ArrayList<>();
        this.loaded = false;
        BedWars.debug("Cargando contenido del tier " + s);
        if (yamlConfiguration.get(s + ".tier-item.material") == null) {
            BedWars.getInstance().getLogger().severe("tier-item material no seteado en " + s);
            return;
        }
        try {
            this.value = Integer.parseInt(s2.replace("tier", ""));
        } catch (Exception ex) {
            BedWars.getInstance().getLogger().severe(s + " no termina en un número. No es reconocido como un tier!");
            return;
        }
        if (yamlConfiguration.get(s + ".tier-settings.cost") == null) {
            BedWars.getInstance().getLogger().severe("Coste no seteado para " + s);
            return;
        }
        this.price = yamlConfiguration.getInt(s + ".tier-settings.cost");
        if (yamlConfiguration.get(s + ".tier-settings.currency") == null) {
            BedWars.getInstance().getLogger().severe("Coste no seteado para " + s);
            return;
        }
        if (yamlConfiguration.getString(s + ".tier-settings.currency").isEmpty()) {
            BedWars.getInstance().getLogger().severe("Coste no seteado para " + s);
            return;
        }
        final String lowerCase = yamlConfiguration.getString(s + ".tier-settings.currency").toLowerCase();
        switch (lowerCase) {
            case "iron":
            case "gold":
            case "diamond":
            case "vault":
            case "emerald":
                this.currency = CategoryContent.getCurrency(yamlConfiguration.getString(s + ".tier-settings.currency").toLowerCase());
                break;
            default:
                BedWars.getInstance().getLogger().severe("Coste no seteado para " + s);
                this.currency = Material.IRON_INGOT;
                break;
        }
        this.itemStack = BedWars.getInstance().getNms().createItemStack(yamlConfiguration.getString(s + ".tier-item.material"), (yamlConfiguration.get(s + ".tier-item.amount") == null) ? 1 : yamlConfiguration.getInt(s + ".tier-item.amount"), (short) ((yamlConfiguration.get(s + ".tier-item.data") == null) ? 0 : yamlConfiguration.getInt(s + ".tier-item.data")));
        if (yamlConfiguration.get(s + ".tier-item.enchanted") != null && yamlConfiguration.getBoolean(s + ".tier-item.enchanted"))
            this.itemStack = ShopManager.enchantItem(this.itemStack);
        if (yamlConfiguration.getString(s + ".tier-item.potion-display") != null && !yamlConfiguration.getString(s + ".tier-item.potion-display").isEmpty())
            this.itemStack = BedWars.getInstance().getNms().setTag(this.itemStack, "Potion", yamlConfiguration.getString(s + ".tier-item.potion-display"));
        if (yamlConfiguration.getString(s + ".tier-item.potion-color") != null && !yamlConfiguration.getString(s + ".tier-item.potion-color").isEmpty())
            this.itemStack = BedWars.getInstance().getNms().setTag(this.itemStack, "CustomPotionColor", yamlConfiguration.getString(s + ".tier-item.potion-color"));
        if (this.itemStack != null) this.itemStack.setItemMeta(ShopManager.hideItemStuff(this.itemStack.getItemMeta()));
        if (yamlConfiguration.get(s + "." + "buy-items") != null) {
            for (String item : yamlConfiguration.getConfigurationSection(s + "." + "buy-items").getKeys(false)) {
                final BuyItem buyItem = new BuyItem(s + "." + "buy-items" + "." + item, yamlConfiguration, s3, this);
                if (buyItem.isLoaded()) this.buyItemsList.add(buyItem);
            }
        }
        if (yamlConfiguration.get(s + "." + "buy-cmds") != null) {
            final BuyCommand buyCommand = new BuyCommand(s + "." + "buy-cmds", yamlConfiguration, s3);
            if (buyCommand.isLoaded()) this.buyItemsList.add(buyCommand);
        }
        if (this.buyItemsList.isEmpty()) {
            Bukkit.getLogger().warning("Cargados 0 contenidos de compra para: " + s);
        }
        this.loaded = true;
    }

    @Override
    public void setCurrency(final Material currency) {
        this.currency = currency;
    }

    @Override
    public void setPrice(final int price) {
        this.price = price;
    }

    @Override
    public void setItemStack(final ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public void setBuyItemsList(final List<IBuyItem> buyItemsList) {
        this.buyItemsList = buyItemsList;
    }

    public boolean isLoaded() {
        return this.loaded;
    }
}
