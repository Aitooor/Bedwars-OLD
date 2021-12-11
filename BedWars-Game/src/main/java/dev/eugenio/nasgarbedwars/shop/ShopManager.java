package dev.eugenio.nasgarbedwars.shop;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.shop.listeners.*;
import dev.eugenio.nasgarbedwars.shop.main.QuickBuyButton;
import dev.eugenio.nasgarbedwars.shop.main.ShopCategory;
import dev.eugenio.nasgarbedwars.shop.main.ShopIndex;
import dev.eugenio.nasgarbedwars.api.configuration.ConfigManager;
import dev.eugenio.nasgarbedwars.shop.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;

public class ShopManager extends ConfigManager {
    public static ShopIndex shop;
    
    public ShopManager() {
        super(BedWars.getInstance(), "shop", BedWars.getInstance().getDataFolder().getPath());
        this.saveDefaults();
        this.loadShop();
        this.registerListeners();
    }
    
    private void saveDefaults() {
        this.getYml().options().header("Tienda con quick buy y tiers");
        this.getYml().addDefault("shop-settings.quick-buy-category.material", BedWars.getInstance().getForCurrentVersion("NETHER_STAR", "NETHER_STAR", "NETHER_STAR"));
        this.getYml().addDefault("shop-settings.quick-buy-category.amount", 1);
        this.getYml().addDefault("shop-settings.quick-buy-category.data", 0);
        this.getYml().addDefault("shop-settings.quick-buy-category.enchanted", false);
        this.getYml().addDefault("shop-settings.quick-buy-empty-item.material", BedWars.getInstance().getForCurrentVersion("STAINED_GLASS_PANE", "STAINED_GLASS_PANE", "RED_STAINED_GLASS_PANE"));
        this.getYml().addDefault("shop-settings.quick-buy-empty-item.amount", 1);
        this.getYml().addDefault("shop-settings.quick-buy-empty-item.data", 14);
        this.getYml().addDefault("shop-settings.quick-buy-empty-item.enchanted", false);
        this.getYml().addDefault("shop-settings.regular-separator-item.material", BedWars.getInstance().getForCurrentVersion("STAINED_GLASS_PANE", "STAINED_GLASS_PANE", "GRAY_STAINED_GLASS_PANE"));
        this.getYml().addDefault("shop-settings.regular-separator-item.amount", 1);
        this.getYml().addDefault("shop-settings.regular-separator-item.data", 7);
        this.getYml().addDefault("shop-settings.regular-separator-item.enchanted", false);
        this.getYml().addDefault("shop-settings.selected-separator-item.material", BedWars.getInstance().getForCurrentVersion("STAINED_GLASS_PANE", "STAINED_GLASS_PANE", "GREEN_STAINED_GLASS_PANE"));
        this.getYml().addDefault("shop-settings.selected-separator-item.amount", 1);
        this.getYml().addDefault("shop-settings.selected-separator-item.data", 13);
        this.getYml().addDefault("shop-settings.selected-separator-item.enchanted", false);
        this.getYml().addDefault("shop-specials.silverfish.enable", true);
        this.getYml().addDefault("shop-specials.silverfish.material", BedWars.getInstance().getForCurrentVersion("SNOW_BALL", "SNOW_BALL", "SNOWBALL"));
        this.getYml().addDefault("shop-specials.silverfish.data", 0);
        this.getYml().addDefault("shop-specials.silverfish.health", 8.0);
        this.getYml().addDefault("shop-specials.silverfish.damage", 4.0);
        this.getYml().addDefault("shop-specials.silverfish.speed", 0.25);
        this.getYml().addDefault("shop-specials.silverfish.despawn", 15);
        this.getYml().addDefault("shop-specials.iron-golem.enable", true);
        this.getYml().addDefault("shop-specials.iron-golem.material", BedWars.getInstance().getForCurrentVersion("MONSTER_EGG", "MONSTER_EGG", "HORSE_SPAWN_EGG"));
        this.getYml().addDefault("shop-specials.iron-golem.data", 0);
        this.getYml().addDefault("shop-specials.iron-golem.health", 100.0);
        this.getYml().addDefault("shop-specials.iron-golem.despawn", 240);
        this.getYml().addDefault("shop-specials.iron-golem.speed", 0.25);
        if (this.isFirstTime()) {
            this.getYml().addDefault("quick-buy-defaults.element1.path", "blocks-category.category-content.wool");
            this.getYml().addDefault("quick-buy-defaults.element1.slot", 19);
            this.getYml().addDefault("quick-buy-defaults.element2.path", "melee-category.category-content.stone-sword");
            this.getYml().addDefault("quick-buy-defaults.element2.slot", 20);
            this.getYml().addDefault("quick-buy-defaults.element3.path", "armor-category.category-content.chainmail");
            this.getYml().addDefault("quick-buy-defaults.element3.slot", 21);
            this.getYml().addDefault("quick-buy-defaults.element4.path", "ranged-category.category-content.bow1");
            this.getYml().addDefault("quick-buy-defaults.element4.slot", 23);
            this.getYml().addDefault("quick-buy-defaults.element5.path", "potions-category.category-content.speed-potion");
            this.getYml().addDefault("quick-buy-defaults.element5.slot", 24);
            this.getYml().addDefault("quick-buy-defaults.element6.path", "utility-category.category-content.tnt");
            this.getYml().addDefault("quick-buy-defaults.element6.slot", 25);
            this.getYml().addDefault("quick-buy-defaults.element7.path", "blocks-category.category-content.wood");
            this.getYml().addDefault("quick-buy-defaults.element7.slot", 28);
            this.getYml().addDefault("quick-buy-defaults.element8.path", "melee-category.category-content.iron-sword");
            this.getYml().addDefault("quick-buy-defaults.element8.slot", 29);
            this.getYml().addDefault("quick-buy-defaults.element9.path", "armor-category.category-content.iron-armor");
            this.getYml().addDefault("quick-buy-defaults.element9.slot", 30);
            this.getYml().addDefault("quick-buy-defaults.element10.path", "tools-category.category-content.shears");
            this.getYml().addDefault("quick-buy-defaults.element10.slot", 31);
            this.getYml().addDefault("quick-buy-defaults.element11.path", "ranged-category.category-content.arrow");
            this.getYml().addDefault("quick-buy-defaults.element11.slot", 32);
            this.getYml().addDefault("quick-buy-defaults.element12.path", "potions-category.category-content.jump-potion");
            this.getYml().addDefault("quick-buy-defaults.element12.slot", 33);
            this.getYml().addDefault("quick-buy-defaults.element13.path", "utility-category.category-content.water-bucket");
            this.getYml().addDefault("quick-buy-defaults.element13.slot", 34);
        }
        if (this.isFirstTime()) {
            this.addDefaultShopCategory("blocks-category", 1, BedWars.getInstance().getForCurrentVersion("STAINED_CLAY", "STAINED_CLAY", "ORANGE_TERRACOTTA"), 1, 1, false);
            this.adCategoryContentTier("blocks-category", "wool", 19, "tier1", BedWars.getInstance().getForCurrentVersion("WOOL", "WOOL", "WHITE_WOOL"), 0, 16, false, 4, "iron", false, false);
            this.addBuyItem("blocks-category", "wool", "tier1", "wool", BedWars.getInstance().getForCurrentVersion("WOOL", "WOOL", "WHITE_WOOL"), 0, 16, "", "", "", false);
            this.adCategoryContentTier("blocks-category", "clay", 20, "tier1", BedWars.getInstance().getForCurrentVersion("STAINED_CLAY", "STAINED_CLAY", "ORANGE_TERRACOTTA"), 1, 16, false, 12, "iron", false, false);
            this.addBuyItem("blocks-category", "clay", "tier1", "clay", BedWars.getInstance().getForCurrentVersion("STAINED_CLAY", "STAINED_CLAY", "ORANGE_TERRACOTTA"), 1, 16, "", "", "", false);
            this.adCategoryContentTier("blocks-category", "glass", 21, "tier1", BedWars.getInstance().getForCurrentVersion("GLASS", "GLASS", "GLASS"), 0, 4, false, 12, "iron", false, false);
            this.addBuyItem("blocks-category", "glass", "tier1", "glass", BedWars.getInstance().getForCurrentVersion("GLASS", "GLASS", "GLASS"), 0, 4, "", "", "", false);
            this.adCategoryContentTier("blocks-category", "stone", 22, "tier1", BedWars.getInstance().getForCurrentVersion("ENDER_STONE", "ENDER_STONE", "END_STONE"), 0, 16, false, 24, "iron", false, false);
            this.addBuyItem("blocks-category", "stone", "tier1", "stone", BedWars.getInstance().getForCurrentVersion("ENDER_STONE", "ENDER_STONE", "END_STONE"), 0, 16, "", "", "", false);
            this.adCategoryContentTier("blocks-category", "ladder", 23, "tier1", BedWars.getInstance().getForCurrentVersion("LADDER", "LADDER", "LADDER"), 0, 16, false, 4, "iron", false, false);
            this.addBuyItem("blocks-category", "ladder", "tier1", "ladder", BedWars.getInstance().getForCurrentVersion("LADDER", "LADDER", "LADDER"), 0, 16, "", "", "", false);
            this.adCategoryContentTier("blocks-category", "wood", 24, "tier1", BedWars.getInstance().getForCurrentVersion("WOOD", "WOOD", "OAK_WOOD"), 0, 16, false, 4, "gold", false, false);
            this.addBuyItem("blocks-category", "wood", "tier1", "wood", BedWars.getInstance().getForCurrentVersion("WOOD", "WOOD", "OAK_WOOD"), 0, 16, "", "", "", false);
            this.adCategoryContentTier("blocks-category", "obsidian", 25, "tier1", BedWars.getInstance().getForCurrentVersion("OBSIDIAN", "OBSIDIAN", "OBSIDIAN"), 0, 4, false, 4, "emerald", false, false);
            this.addBuyItem("blocks-category", "obsidian", "tier1", "obsidian", BedWars.getInstance().getForCurrentVersion("OBSIDIAN", "OBSIDIAN", "OBSIDIAN"), 0, 4, "", "", "", false);
            this.addDefaultShopCategory("melee-category", 2, BedWars.getInstance().getForCurrentVersion("GOLD_SWORD", "GOLD_SWORD", "GOLDEN_SWORD"), 0, 1, false);
            this.adCategoryContentTier("melee-category", "stone-sword", 19, "tier1", BedWars.getInstance().getForCurrentVersion("STONE_SWORD", "STONE_SWORD", "STONE_SWORD"), 0, 1, false, 10, "iron", false, false);
            this.addBuyItem("melee-category", "stone-sword", "tier1", "sword", BedWars.getInstance().getForCurrentVersion("STONE_SWORD", "STONE_SWORD", "STONE_SWORD"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("melee-category", "iron-sword", 20, "tier1", BedWars.getInstance().getForCurrentVersion("IRON_SWORD", "IRON_SWORD", "IRON_SWORD"), 0, 1, false, 7, "gold", false, false);
            this.addBuyItem("melee-category", "iron-sword", "tier1", "sword", BedWars.getInstance().getForCurrentVersion("IRON_SWORD", "IRON_SWORD", "IRON_SWORD"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("melee-category", "diamond-sword", 21, "tier1", BedWars.getInstance().getForCurrentVersion("DIAMOND_SWORD", "DIAMOND_SWORD", "DIAMOND_SWORD"), 0, 1, false, 4, "emerald", false, false);
            this.addBuyItem("melee-category", "diamond-sword", "tier1", "sword", BedWars.getInstance().getForCurrentVersion("DIAMOND_SWORD", "DIAMOND_SWORD", "DIAMOND_SWORD"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("melee-category", "stick", 22, "tier1", BedWars.getInstance().getForCurrentVersion("STICK", "STICK", "STICK"), 0, 1, true, 10, "gold", false, false);
            this.addBuyItem("melee-category", "stick", "tier1", "stick", BedWars.getInstance().getForCurrentVersion("STICK", "STICK", "STICK"), 0, 1, "KNOCKBACK 1", "", "", false);
            this.addDefaultShopCategory("armor-category", 3, BedWars.getInstance().getForCurrentVersion("CHAINMAIL_BOOTS", "CHAINMAIL_BOOTS", "CHAINMAIL_BOOTS"), 0, 1, false);
            this.adCategoryContentTier("armor-category", "chainmail", 19, "tier1", BedWars.getInstance().getForCurrentVersion("CHAINMAIL_BOOTS", "CHAINMAIL_BOOTS", "CHAINMAIL_BOOTS"), 0, 1, false, 40, "iron", true, false);
            this.addBuyItem("armor-category", "chainmail", "tier1", "boots", BedWars.getInstance().getForCurrentVersion("CHAINMAIL_BOOTS", "CHAINMAIL_BOOTS", "CHAINMAIL_BOOTS"), 0, 1, "", "", "", true);
            this.addBuyItem("armor-category", "chainmail", "tier1", "leggings", BedWars.getInstance().getForCurrentVersion("CHAINMAIL_LEGGINGS", "CHAINMAIL_LEGGINGS", "CHAINMAIL_LEGGINGS"), 0, 1, "", "", "", true);
            this.adCategoryContentTier("armor-category", "iron-armor", 20, "tier1", BedWars.getInstance().getForCurrentVersion("IRON_BOOTS", "IRON_BOOTS", "IRON_BOOTS"), 0, 1, false, 12, "gold", true, false);
            this.addBuyItem("armor-category", "iron-armor", "tier1", "boots", BedWars.getInstance().getForCurrentVersion("IRON_BOOTS", "IRON_BOOTS", "IRON_BOOTS"), 0, 1, "", "", "", true);
            this.addBuyItem("armor-category", "iron-armor", "tier1", "leggings", BedWars.getInstance().getForCurrentVersion("IRON_LEGGINGS", "IRON_LEGGINGS", "IRON_LEGGINGS"), 0, 1, "", "", "", true);
            this.adCategoryContentTier("armor-category", "diamond-armor", 21, "tier1", BedWars.getInstance().getForCurrentVersion("DIAMOND_BOOTS", "DIAMOND_BOOTS", "DIAMOND_BOOTS"), 0, 1, false, 6, "emerald", true, false);
            this.addBuyItem("armor-category", "diamond-armor", "tier1", "boots", BedWars.getInstance().getForCurrentVersion("DIAMOND_BOOTS", "DIAMOND_BOOTS", "DIAMOND_BOOTS"), 0, 1, "", "", "", true);
            this.addBuyItem("armor-category", "diamond-armor", "tier1", "leggings", BedWars.getInstance().getForCurrentVersion("DIAMOND_LEGGINGS", "DIAMOND_LEGGINGS", "DIAMOND_LEGGINGS"), 0, 1, "", "", "", true);
            this.addDefaultShopCategory("tools-category", 4, BedWars.getInstance().getForCurrentVersion("STONE_PICKAXE", "STONE_PICKAXE", "STONE_PICKAXE"), 0, 1, false);
            this.adCategoryContentTier("tools-category", "shears", 19, "tier1", BedWars.getInstance().getForCurrentVersion("SHEARS", "SHEARS", "SHEARS"), 0, 1, false, 20, "iron", true, false);
            this.addBuyItem("tools-category", "shears", "tier1", "shears", BedWars.getInstance().getForCurrentVersion("SHEARS", "SHEARS", "SHEARS"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("tools-category", "pickaxe", 20, "tier1", BedWars.getInstance().getForCurrentVersion("WOOD_PICKAXE", "WOOD_PICKAXE", "WOODEN_PICKAXE"), 0, 1, false, 10, "iron", true, true);
            this.addBuyItem("tools-category", "pickaxe", "tier1", "wooden-pickaxe", BedWars.getInstance().getForCurrentVersion("WOOD_PICKAXE", "WOOD_PICKAXE", "WOODEN_PICKAXE"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("tools-category", "pickaxe", 20, "tier2", BedWars.getInstance().getForCurrentVersion("IRON_PICKAXE", "IRON_PICKAXE", "IRON_PICKAXE"), 0, 1, true, 10, "iron", true, true);
            this.addBuyItem("tools-category", "pickaxe", "tier2", "iron-pickaxe", BedWars.getInstance().getForCurrentVersion("IRON_PICKAXE", "IRON_PICKAXE", "IRON_PICKAXE"), 0, 1, "DIG_SPEED 2", "", "", false);
            this.adCategoryContentTier("tools-category", "pickaxe", 20, "tier3", BedWars.getInstance().getForCurrentVersion("GOLD_PICKAXE", "GOLD_PICKAXE", "GOLDEN_PICKAXE"), 0, 1, true, 3, "gold", true, true);
            this.addBuyItem("tools-category", "pickaxe", "tier3", "gold-pickaxe", BedWars.getInstance().getForCurrentVersion("GOLD_PICKAXE", "GOLD_PICKAXE", "GOLDEN_PICKAXE"), 0, 1, "DIG_SPEED 3,DAMAGE_ALL 2", "", "", false);
            this.adCategoryContentTier("tools-category", "pickaxe", 20, "tier4", BedWars.getInstance().getForCurrentVersion("DIAMOND_PICKAXE", "DIAMOND_PICKAXE", "DIAMOND_PICKAXE"), 0, 1, true, 6, "gold", true, true);
            this.addBuyItem("tools-category", "pickaxe", "tier4", "diamond-pickaxe", BedWars.getInstance().getForCurrentVersion("DIAMOND_PICKAXE", "DIAMOND_PICKAXE", "DIAMOND_PICKAXE"), 0, 1, "DIG_SPEED 3", "", "", false);
            this.adCategoryContentTier("tools-category", "axe", 21, "tier1", BedWars.getInstance().getForCurrentVersion("WOOD_AXE", "WOOD_AXE", "WOODEN_AXE"), 0, 1, false, 10, "iron", true, true);
            this.addBuyItem("tools-category", "axe", "tier1", "wooden-axe", BedWars.getInstance().getForCurrentVersion("WOOD_AXE", "WOOD_AXE", "WOODEN_AXE"), 0, 1, "DIG_SPEED 1", "", "", false);
            this.adCategoryContentTier("tools-category", "axe", 21, "tier2", BedWars.getInstance().getForCurrentVersion("IRON_AXE", "IRON_AXE", "IRON_AXE"), 0, 1, true, 10, "iron", true, true);
            this.addBuyItem("tools-category", "axe", "tier2", "iron-axe", BedWars.getInstance().getForCurrentVersion("IRON_AXE", "IRON_AXE", "IRON_AXE"), 0, 1, "DIG_SPEED 1", "", "", false);
            this.adCategoryContentTier("tools-category", "axe", 21, "tier3", BedWars.getInstance().getForCurrentVersion("GOLD_AXE", "GOLD_AXE", "GOLDEN_AXE"), 0, 1, true, 3, "gold", true, true);
            this.addBuyItem("tools-category", "axe", "tier3", "gold-axe", BedWars.getInstance().getForCurrentVersion("GOLD_AXE", "GOLD_AXE", "GOLDEN_AXE"), 0, 1, "DIG_SPEED 2", "", "", false);
            this.adCategoryContentTier("tools-category", "axe", 21, "tier4", BedWars.getInstance().getForCurrentVersion("DIAMOND_AXE", "DIAMOND_AXE", "DIAMOND_AXE"), 0, 1, true, 6, "gold", true, true);
            this.addBuyItem("tools-category", "axe", "tier4", "diamond-axe", BedWars.getInstance().getForCurrentVersion("DIAMOND_AXE", "DIAMOND_AXE", "DIAMOND_AXE"), 0, 1, "DIG_SPEED 3", "", "", false);
            this.addDefaultShopCategory("ranged-category", 5, BedWars.getInstance().getForCurrentVersion("BOW", "BOW", "BOW"), 0, 1, false);
            this.adCategoryContentTier("ranged-category", "arrow", 19, "tier1", BedWars.getInstance().getForCurrentVersion("ARROW", "ARROW", "ARROW"), 0, 8, false, 2, "gold", false, false);
            this.addBuyItem("ranged-category", "arrow", "tier1", "arrows", BedWars.getInstance().getForCurrentVersion("ARROW", "ARROW", "ARROW"), 0, 8, "", "", "", false);
            this.adCategoryContentTier("ranged-category", "bow1", 20, "tier1", BedWars.getInstance().getForCurrentVersion("BOW", "BOW", "BOW"), 0, 1, false, 12, "gold", false, false);
            this.addBuyItem("ranged-category", "bow1", "tier1", "bow", BedWars.getInstance().getForCurrentVersion("BOW", "BOW", "BOW"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("ranged-category", "bow2", 21, "tier1", BedWars.getInstance().getForCurrentVersion("BOW", "BOW", "BOW"), 0, 1, true, 24, "gold", false, false);
            this.addBuyItem("ranged-category", "bow2", "tier1", "bow", BedWars.getInstance().getForCurrentVersion("BOW", "BOW", "BOW"), 0, 1, "ARROW_DAMAGE 1", "", "", false);
            this.adCategoryContentTier("ranged-category", "bow3", 22, "tier1", BedWars.getInstance().getForCurrentVersion("BOW", "BOW", "BOW"), 0, 1, true, 6, "emerald", false, false);
            this.addBuyItem("ranged-category", "bow3", "tier1", "bow", BedWars.getInstance().getForCurrentVersion("BOW", "BOW", "BOW"), 0, 1, "ARROW_DAMAGE 1,ARROW_KNOCKBACK 1", "", "", false);
            this.addDefaultShopCategory("potions-category", 6, BedWars.getInstance().getForCurrentVersion("BREWING_STAND_ITEM", "BREWING_STAND_ITEM", "BREWING_STAND"), 0, 1, false);
            this.adCategoryContentTier("potions-category", "jump-potion", 20, "tier1", BedWars.getInstance().getForCurrentVersion("POTION", "POTION", "POTION"), 0, 1, false, 1, "emerald", false, false);
            this.addBuyPotion("potions-category", "jump-potion", "tier1", "jump", BedWars.getInstance().getForCurrentVersion("POTION", "POTION", "POTION"), 0, 1, "", "JUMP 45 5", "Jump Potion");
            this.adCategoryContentTier("potions-category", "speed-potion", 19, "tier1", BedWars.getInstance().getForCurrentVersion("POTION", "POTION", "POTION"), 0, 1, false, 1, "emerald", false, false);
            this.addBuyPotion("potions-category", "speed-potion", "tier1", "speed", BedWars.getInstance().getForCurrentVersion("POTION", "POTION", "POTION"), 0, 1, "", "SPEED 45 2", "Speed Potion");
            this.adCategoryContentTier("potions-category", "invisibility", 21, "tier1", BedWars.getInstance().getForCurrentVersion("POTION", "POTION", "POTION"), 0, 1, false, 2, "emerald", false, false);
            this.addBuyPotion("potions-category", "invisibility", "tier1", "invisibility", BedWars.getInstance().getForCurrentVersion("POTION", "POTION", "POTION"), 0, 1, "", "INVISIBILITY 30 1", "Invisibility Potion");
            this.addDefaultShopCategory("utility-category", 7, BedWars.getInstance().getForCurrentVersion("TNT", "TNT", "TNT"), 0, 1, false);
            this.adCategoryContentTier("utility-category", "golden-apple", 19, "tier1", BedWars.getInstance().getForCurrentVersion("GOLDEN_APPLE", "GOLDEN_APPLE", "GOLDEN_APPLE"), 0, 1, false, 3, "gold", false, false);
            this.addBuyItem("utility-category", "golden-apple", "tier1", "apple", BedWars.getInstance().getForCurrentVersion("GOLDEN_APPLE", "GOLDEN_APPLE", "GOLDEN_APPLE"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("utility-category", "bedbug", 20, "tier1", BedWars.getInstance().getForCurrentVersion("SNOW_BALL", "SNOW_BALL", "SNOWBALL"), 0, 1, false, 40, "iron", false, false);
            this.addBuyItem("utility-category", "bedbug", "tier1", "bedbug", BedWars.getInstance().getForCurrentVersion("SNOW_BALL", "SNOW_BALL", "SNOWBALL"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("utility-category", "dream-defender", 21, "tier1", BedWars.getInstance().getForCurrentVersion("MONSTER_EGG", "MONSTER_EGG", "HORSE_SPAWN_EGG"), 0, 1, false, 120, "iron", false, false);
            this.addBuyItem("utility-category", "dream-defender", "tier1", "defender", BedWars.getInstance().getForCurrentVersion("MONSTER_EGG", "MONSTER_EGG", "HORSE_SPAWN_EGG"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("utility-category", "fireball", 22, "tier1", BedWars.getInstance().getForCurrentVersion("FIREBALL", "FIREBALL", "FIRE_CHARGE"), 0, 1, false, 40, "iron", false, false);
            this.addBuyItem("utility-category", "fireball", "tier1", "fireball", BedWars.getInstance().getForCurrentVersion("FIREBALL", "FIREBALL", "FIRE_CHARGE"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("utility-category", "tnt", 23, "tier1", BedWars.getInstance().getForCurrentVersion("TNT", "TNT", "TNT"), 0, 1, false, 4, "gold", false, false);
            this.addBuyItem("utility-category", "tnt", "tier1", "tnt", BedWars.getInstance().getForCurrentVersion("TNT", "TNT", "TNT"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("utility-category", "ender-pearl", 24, "tier1", BedWars.getInstance().getForCurrentVersion("ENDER_PEARL", "ENDER_PEARL", "ENDER_PEARL"), 0, 1, false, 4, "emerald", false, false);
            this.addBuyItem("utility-category", "ender-pearl", "tier1", "ender-pearl", BedWars.getInstance().getForCurrentVersion("ENDER_PEARL", "ENDER_PEARL", "ENDER_PEARL"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("utility-category", "water-bucket", 25, "tier1", BedWars.getInstance().getForCurrentVersion("WATER_BUCKET", "WATER_BUCKET", "WATER_BUCKET"), 0, 1, false, 4, "gold", false, false);
            this.addBuyItem("utility-category", "water-bucket", "tier1", "water-bucket", BedWars.getInstance().getForCurrentVersion("WATER_BUCKET", "WATER_BUCKET", "WATER_BUCKET"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("utility-category", "bridge-egg", 28, "tier1", BedWars.getInstance().getForCurrentVersion("EGG", "EGG", "EGG"), 0, 1, false, 3, "emerald", false, false);
            this.addBuyItem("utility-category", "bridge-egg", "tier1", "egg", BedWars.getInstance().getForCurrentVersion("EGG", "EGG", "EGG"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("utility-category", "magic-milk", 29, "tier1", BedWars.getInstance().getForCurrentVersion("MILK_BUCKET", "MILK_BUCKET", "MILK_BUCKET"), 0, 1, false, 4, "gold", false, false);
            this.addBuyItem("utility-category", "magic-milk", "tier1", "milk", BedWars.getInstance().getForCurrentVersion("MILK_BUCKET", "MILK_BUCKET", "MILK_BUCKET"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("utility-category", "sponge", 30, "tier1", BedWars.getInstance().getForCurrentVersion("SPONGE", "SPONGE", "SPONGE"), 0, 1, false, 3, "gold", false, false);
            this.addBuyItem("utility-category", "sponge", "tier1", "sponge", BedWars.getInstance().getForCurrentVersion("SPONGE", "SPONGE", "SPONGE"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("utility-category", "tower", 31, "tier1", BedWars.getInstance().getForCurrentVersion("CHEST", "CHEST", "CHEST"), 0, 1, false, 24, "iron", false, false);
            this.addBuyItem("utility-category", "tower", "tier1", "chest", BedWars.getInstance().getForCurrentVersion("CHEST", "CHEST", "CHEST"), 0, 1, "", "", "", false);
        }
        if (this.getYml().get("armor-category.category-content.diamond-armor") != null) this.getYml().addDefault("armor-category.category-content.diamond-armor.content-settings.weight", 2);
        if (this.getYml().get("armor-category.category-content.iron-armor") != null) this.getYml().addDefault("armor-category.category-content.iron-armor.content-settings.weight", 1);
        try {
            final String string = this.getYml().getString("shop-specials.iron-golem.material");
            BedWars.debug("shop-specials.iron-golem.material is set to: " + string);
            Material.valueOf(string);
        } catch (Exception ex) {
            BedWars.getInstance().getLogger().severe("Invalid material at shop-specials.iron-golem.material");
        }
        try {
            final String string2 = this.getYml().getString("shop-specials.silverfish.material");
            BedWars.debug("shop-specials.silverfish.material is set to: " + string2);
            Material.valueOf(string2);
        } catch (Exception ex2) {
            BedWars.getInstance().getLogger().severe("Invalid material at shop-specials.silverfish.material");
        }
        this.getYml().options().copyDefaults(true);
        this.save();
    }
    
    private void loadShop() {
        ItemStack itemStack = BedWars.getInstance().getNms().createItemStack(this.getYml().getString("shop-settings.quick-buy-category.material"), this.getYml().getInt("shop-settings.quick-buy-category.amount"), (short)this.getYml().getInt("shop-settings.quick-buy-category.data"));
        if (this.getYml().getBoolean("shop-settings.quick-buy-category.enchanted")) itemStack = enchantItem(itemStack);
        final QuickBuyButton quickBuyButton = new QuickBuyButton(0, itemStack, "shop-items-messages.quick-buy-item-name", "shop-items-messages.quick-buy-item-lore");
        ItemStack itemStack2 = BedWars.getInstance().getNms().createItemStack(this.getYml().getString("shop-settings.regular-separator-item.material"), this.getYml().getInt("shop-settings.regular-separator-item.amount"), (short)this.getYml().getInt("shop-settings.regular-separator-item.data"));
        if (this.getYml().getBoolean("shop-settings.regular-separator-item.enchanted")) itemStack2 = enchantItem(itemStack2);
        ItemStack itemStack3 = BedWars.getInstance().getNms().createItemStack(this.getYml().getString("shop-settings.selected-separator-item.material"), this.getYml().getInt("shop-settings.selected-separator-item.amount"), (short)this.getYml().getInt("shop-settings.selected-separator-item.data"));
        if (this.getYml().getBoolean("shop-settings.selected-separator-item.enchanted")) itemStack3 = enchantItem(itemStack3);
        ShopManager.shop = new ShopIndex("shop-items-messages.inventory-name", quickBuyButton, "shop-items-messages.separator-item-name", "shop-items-messages.separator-item-lore", itemStack3, itemStack2);
        for (final String s : this.getYml().getConfigurationSection("").getKeys(false)) {
            if (s.equalsIgnoreCase("shop-settings")) continue;
            if (s.equals("quick-buy-defaults")) continue;
            if (s.equalsIgnoreCase("shop-specials")) continue;
            final ShopCategory shopCategory = new ShopCategory(s, this.getYml());
            if (!shopCategory.isLoaded()) continue;
            ShopManager.shop.addShopCategory(shopCategory);
        }
    }
    
    public static ItemMeta hideItemStuff(final ItemMeta itemMeta) {
        if (itemMeta != null) itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON);
        return itemMeta;
    }
    
    public static ItemStack enchantItem(final ItemStack itemStack) {
        final ItemStack itemStack2 = new ItemStack(itemStack);
        final ItemMeta itemMeta = itemStack2.getItemMeta();
        if (itemMeta != null) {
            itemMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
            itemStack2.setItemMeta(hideItemStuff(itemMeta));
        }
        return itemStack2;
    }
    
    private void addDefaultShopCategory(final String s, final int n, final String s2, final int n2, final int n3, final boolean b) {
        this.getYml().addDefault(s + ".category-slot", n);
        this.getYml().addDefault(s + ".category-item.material", s2);
        this.getYml().addDefault(s + ".category-item.data", n2);
        this.getYml().addDefault(s + ".category-item.amount", n3);
        this.getYml().addDefault(s + ".category-item.enchanted", b);
    }
    
    public void adCategoryContentTier(String s, final String s2, final int n, final String s3, final String s4, final int n2, final int n3, final boolean b, final int n4, final String s5, final boolean b2, final boolean b3) {
        s = s + ".category-content." + s2 + ".";
        this.getYml().addDefault(s + "content-settings.content-slot", n);
        this.getYml().addDefault(s + "content-settings.is-permanent", b2);
        this.getYml().addDefault(s + "content-settings.is-downgradable", b3);
        s = s + "content-tiers." + s3;
        this.getYml().addDefault(s + ".tier-item.material", s4);
        this.getYml().addDefault(s + ".tier-item.data", n2);
        this.getYml().addDefault(s + ".tier-item.amount", n3);
        this.getYml().addDefault(s + ".tier-item.enchanted", b);
        this.getYml().addDefault(s + ".tier-settings.cost", n4);
        this.getYml().addDefault(s + ".tier-settings.currency", s5);
    }
    
    public void addBuyItem(String string, final String s, final String s2, final String s3, final String s4, final int n, final int n2, final String s5, final String s6, final String s7, final boolean b) {
        string = string + ".category-content." + s + "." + "content-tiers" + "." + s2 + "." + "buy-items" + "." + s3 + ".";
        this.getYml().addDefault(string + "material", s4);
        this.getYml().addDefault(string + "data", n);
        this.getYml().addDefault(string + "amount", n2);
        if (!s5.isEmpty()) {
            this.getYml().addDefault(string + "enchants", s5);
        }
        if (!s6.isEmpty()) {
            this.getYml().addDefault(string + "potion", s6);
        }
        if (b) {
            this.getYml().addDefault(string + "auto-equip", true);
        }
        if (!s7.isEmpty()) {
            this.getYml().addDefault(string + "name", s7);
        }
    }
    
    public void addBuyPotion(String string, final String s, final String s2, final String s3, final String s4, final int n, final int n2, final String s5, final String s6, final String s7) {
        string = string + ".category-content." + s + "." + "content-tiers" + "." + s2 + "." + "buy-items" + "." + s3 + ".";
        this.getYml().addDefault(string + "material", s4);
        this.getYml().addDefault(string + "data", n);
        this.getYml().addDefault(string + "amount", n2);
        if (!s5.isEmpty()) this.getYml().addDefault(string + "enchants", s5);
        if (!s6.isEmpty()) this.getYml().addDefault(string + "potion", s6);
        this.getYml().addDefault(string + "potion-color", "");
        if (!s7.isEmpty()) this.getYml().addDefault(string + "name", s7);
    }
    
    public static ShopIndex getShop() {
        return ShopManager.shop;
    }
    
    private void registerListeners() {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new InventoryListener(), BedWars.getInstance());
        pluginManager.registerEvents(new ShopCacheListener(), BedWars.getInstance());
        pluginManager.registerEvents(new QuickBuyListener(), BedWars.getInstance());
        pluginManager.registerEvents(new ShopOpenListener(), BedWars.getInstance());
        pluginManager.registerEvents(new PlayerDropListener(), BedWars.getInstance());
        pluginManager.registerEvents(new SpecialsListener(), BedWars.getInstance());
    }
}
