package dev.eugenio.nasgarbedwars.shop.main;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.configuration.Sounds;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.shop.IBuyItem;
import dev.eugenio.nasgarbedwars.api.arena.team.TeamEnchantment;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BuyItem implements IBuyItem {
    @Getter
    private final String upgradeIdentifier;
    @Getter
    private ItemStack itemStack;
    private boolean autoEquip;
    private boolean permanent;
    private boolean loaded;

    public BuyItem(final String s, final YamlConfiguration yamlConfiguration, final String upgradeIdentifier, final ContentTier contentTier) {
        this.autoEquip = false;
        this.permanent = false;
        this.loaded = false;
        BedWars.debug("Cargando BuyItems: " + s);
        this.upgradeIdentifier = upgradeIdentifier;
        if (yamlConfiguration.get(s + ".material") == null) {
            BedWars.getInstance().getLogger().severe("BuyItem: Material no seteado en " + s);
            return;
        }
        this.itemStack = BedWars.getInstance().getNms().createItemStack(yamlConfiguration.getString(s + ".material"), (yamlConfiguration.get(s + ".amount") == null) ? 1 : yamlConfiguration.getInt(s + ".amount"), (short) ((yamlConfiguration.get(s + ".data") == null) ? 1 : yamlConfiguration.getInt(s + ".data")));
        if (yamlConfiguration.get(s + ".name") != null) {
            final ItemMeta itemMeta = this.itemStack.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', yamlConfiguration.getString(s + ".name")));
                this.itemStack.setItemMeta(itemMeta);
            }
        }
        if (yamlConfiguration.get(s + ".enchants") != null && this.itemStack.getItemMeta() != null) {
            final ItemMeta itemMeta2 = this.itemStack.getItemMeta();
            final String[] split = yamlConfiguration.getString(s + ".enchants").split(",");
            for (int length = split.length, i = 0; i < length; ++i) {
                final String[] split2 = split[i].split(" ");
                try {
                    Enchantment.getByName(split2[0]);
                } catch (Exception ex) {
                    BedWars.getInstance().getLogger().severe("BuyItem: Encantamientos inválidos " + split2[0] + " en: " + s + ".enchants");
                    continue;
                }
                int int1 = 1;
                if (split2.length >= 2) {
                    try {
                        int1 = Integer.parseInt(split2[1]);
                    } catch (Exception ex2) {
                        BedWars.getInstance().getLogger().severe("BuyItem: Int inválido " + split2[1] + " en: " + s + ".enchants");
                        continue;
                    }
                }
                itemMeta2.addEnchant(Enchantment.getByName(split2[0]), int1, true);
            }
            this.itemStack.setItemMeta(itemMeta2);
        }
        if (yamlConfiguration.get(s + ".potion") != null && this.itemStack.getType() == Material.POTION) {
            if (yamlConfiguration.getString(s + ".potion-color") != null && !yamlConfiguration.getString(s + ".potion-color").isEmpty())
                this.itemStack = BedWars.getInstance().getNms().setTag(this.itemStack, "CustomPotionColor", yamlConfiguration.getString(s + ".potion-color"));
            final PotionMeta itemMeta3 = (PotionMeta) this.itemStack.getItemMeta();
            if (itemMeta3 != null) {
                final String[] split3 = yamlConfiguration.getString(s + ".potion").split(",");
                for (int length2 = split3.length, j = 0; j < length2; ++j) {
                    final String[] split4 = split3[j].split(" ");
                    try {
                        PotionEffectType.getByName(split4[0].toUpperCase());
                    } catch (Exception ex3) {
                        BedWars.getInstance().getLogger().severe("BuyItem: Efecto de poción inválido " + split4[0] + " en: " + s + ".potion");
                        continue;
                    }
                    int int2 = 50;
                    int int3 = 1;
                    if (split4.length >= 3) {
                        try {
                            int2 = Integer.parseInt(split4[1]);
                        } catch (Exception ex4) {
                            BedWars.getInstance().getLogger().severe("BuyItem: Int inválido (duración) " + split4[1] + " en: " + s + ".potion");
                            continue;
                        }
                        try {
                            int3 = Integer.parseInt(split4[2]);
                        } catch (Exception ex5) {
                            BedWars.getInstance().getLogger().severe("BuyItem: Int inválido (amplificador) " + split4[2] + " en: " + s + ".potion");
                            continue;
                        }
                    }
                    itemMeta3.addCustomEffect(new PotionEffect(PotionEffectType.getByName(split4[0].toUpperCase()), int2 * 20, int3), true);
                }
                this.itemStack.setItemMeta(itemMeta3);
            }
            this.itemStack = BedWars.getInstance().getNms().setTag(this.itemStack, "Potion", "minecraft:water");
            if (contentTier.getItemStack().getType() == Material.POTION && itemMeta3 != null && !itemMeta3.getCustomEffects().isEmpty()) {
                final ItemStack itemStack = contentTier.getItemStack();
                if (itemStack.getItemMeta() != null) {
                    final PotionMeta itemMeta4 = (PotionMeta) itemStack.getItemMeta();
                    for (PotionEffect potionEffect : itemMeta3.getCustomEffects())
                        itemMeta4.addCustomEffect(potionEffect, true);
                    itemStack.setItemMeta(itemMeta4);
                }
                contentTier.setItemStack(BedWars.getInstance().getNms().setTag(itemStack, "Potion", "minecraft:water"));
            }
        }
        if (yamlConfiguration.get(s + ".auto-equip") != null)
            this.autoEquip = yamlConfiguration.getBoolean(s + ".auto-equip");
        if (yamlConfiguration.get(upgradeIdentifier + "." + "content-settings.is-permanent") != null)
            this.permanent = yamlConfiguration.getBoolean(upgradeIdentifier + "." + "content-settings.is-permanent");
        this.loaded = true;
    }

    @Override
    public boolean isLoaded() {
        return this.loaded;
    }

    @Override
    public void give(final Player player, final IArena arena) {
        ItemStack itemStack = this.itemStack.clone();
        BedWars.debug("Dando BuyItem: " + this.getUpgradeIdentifier() + " a: " + player.getName());
        if (!this.autoEquip || !BedWars.getInstance().getNms().isArmor(this.itemStack)) {
            final ItemMeta itemMeta = itemStack.getItemMeta();
            ItemStack itemStack2 = BedWars.getInstance().getNms().colourItem(itemStack, arena.getTeam(player));
            if (itemMeta != null) {
                if (this.permanent) BedWars.getInstance().getNms().setUnbreakable(itemMeta);
                if (itemStack2.getType() == Material.BOW) {
                    if (this.permanent) BedWars.getInstance().getNms().setUnbreakable(itemMeta);
                    for (final TeamEnchantment teamEnchantment : arena.getTeam(player).getBowsEnchantments())
                        itemMeta.addEnchant(teamEnchantment.getEnchantment(), teamEnchantment.getAmplifier(), true);
                } else if (BedWars.getInstance().getNms().isSword(itemStack2) || BedWars.getInstance().getNms().isAxe(itemStack2)) {
                    for (final TeamEnchantment teamEnchantment2 : arena.getTeam(player).getSwordsEnchantments())
                        itemMeta.addEnchant(teamEnchantment2.getEnchantment(), teamEnchantment2.getAmplifier(), true);
                }
                itemStack2.setItemMeta(itemMeta);
            }
            if (this.permanent)
                itemStack2 = BedWars.getInstance().getNms().setShopUpgradeIdentifier(itemStack2, this.upgradeIdentifier);
            if (BedWars.getInstance().getNms().isSword(itemStack2)) {
                for (final ItemStack itemStack3 : player.getInventory().getContents()) {
                    if (itemStack3 != null) {
                        if (itemStack3.getType() != Material.AIR) {
                            if (BedWars.getInstance().getNms().isSword(itemStack3)) {
                                if (itemStack3 != itemStack2) {
                                    if (BedWars.getInstance().getNms().isCustomBedWarsItem(itemStack3) && BedWars.getInstance().getNms().getCustomData(itemStack3).equals("DEFAULT_ITEM") && BedWars.getInstance().getNms().getDamage(itemStack3) <= BedWars.getInstance().getNms().getDamage(itemStack2)) {
                                        player.getInventory().remove(itemStack3);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            player.getInventory().addItem(itemStack2);
            player.updateInventory();
            return;
        }
        final Material type = itemStack.getType();
        final ItemMeta itemMeta2 = itemStack.getItemMeta();
        if (arena.getTeam(player) == null) {
            BedWars.debug("No se ha podido dar BuyItem a " + player.getName() + " - TEAM ES NULL");
            return;
        }
        if (itemMeta2 != null) {
            for (final TeamEnchantment teamEnchantment3 : arena.getTeam(player).getArmorsEnchantments())
                itemMeta2.addEnchant(teamEnchantment3.getEnchantment(), teamEnchantment3.getAmplifier(), true);
            if (this.permanent) BedWars.getInstance().getNms().setUnbreakable(itemMeta2);
            itemStack.setItemMeta(itemMeta2);
        }
        if (type == Material.LEATHER_HELMET || type == Material.CHAINMAIL_HELMET || type == Material.DIAMOND_HELMET || type == BedWars.getInstance().getNms().materialGoldenHelmet() || type == Material.IRON_HELMET) {
            if (this.permanent)
                itemStack = BedWars.getInstance().getNms().setShopUpgradeIdentifier(itemStack, this.upgradeIdentifier);
            player.getInventory().setHelmet(itemStack);
        } else if (type == Material.LEATHER_CHESTPLATE || type == Material.CHAINMAIL_CHESTPLATE || type == BedWars.getInstance().getNms().materialGoldenChestPlate() || type == Material.DIAMOND_CHESTPLATE || type == Material.IRON_CHESTPLATE) {
            if (this.permanent)
                itemStack = BedWars.getInstance().getNms().setShopUpgradeIdentifier(itemStack, this.upgradeIdentifier);
            player.getInventory().setChestplate(itemStack);
        } else if (type == Material.LEATHER_LEGGINGS || type == Material.CHAINMAIL_LEGGINGS || type == Material.DIAMOND_LEGGINGS || type == BedWars.getInstance().getNms().materialGoldenLeggings() || type == Material.IRON_LEGGINGS) {
            if (this.permanent)
                itemStack = BedWars.getInstance().getNms().setShopUpgradeIdentifier(itemStack, this.upgradeIdentifier);
            player.getInventory().setLeggings(itemStack);
        } else {
            if (this.permanent)
                itemStack = BedWars.getInstance().getNms().setShopUpgradeIdentifier(itemStack, this.upgradeIdentifier);
            player.getInventory().setBoots(itemStack);
        }
        player.updateInventory();
        Sounds.playSound("shop-bought", player);
        Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
            if (player.hasPotionEffect(PotionEffectType.INVISIBILITY))
                for (Player player1 : arena.getPlayers()) BedWars.getInstance().getNms().hideArmor(player, player1);
        }, 20L);
    }

    @Override
    public void setItemStack(final ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public boolean isAutoEquip() {
        return this.autoEquip;
    }

    @Override
    public void setAutoEquip(final boolean autoEquip) {
        this.autoEquip = autoEquip;
    }

    @Override
    public boolean isPermanent() {
        return this.permanent;
    }

    @Override
    public void setPermanent(final boolean permanent) {
        this.permanent = permanent;
    }
}
