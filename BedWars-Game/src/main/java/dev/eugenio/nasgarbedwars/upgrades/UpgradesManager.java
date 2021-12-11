package dev.eugenio.nasgarbedwars.upgrades;

import dev.eugenio.nasgarbedwars.configuration.UpgradesConfig;
import dev.eugenio.nasgarbedwars.upgrades.listeners.InventoryListener;
import dev.eugenio.nasgarbedwars.upgrades.listeners.UpgradeOpenListener;
import dev.eugenio.nasgarbedwars.upgrades.menu.*;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.api.upgrades.MenuContent;
import dev.eugenio.nasgarbedwars.api.upgrades.UpgradesIndex;
import dev.eugenio.nasgarbedwars.arena.Misc;
import dev.eugenio.nasgarbedwars.upgrades.menu.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;
import java.util.logging.Level;

public class UpgradesManager {
    private static final LinkedList<UUID> upgradeViewers;
    private static final HashMap<String, MenuContent> menuContentByName;
    private static final HashMap<String, UpgradesIndex> menuByName;
    private static final HashMap<IArena, UpgradesIndex> customMenuForArena;
    private static UpgradesConfig upgrades;
    
    public static void init() {
        UpgradesManager.upgrades = new UpgradesConfig("upgrades", BedWars.getInstance().getDataFolder().getPath());
        for (final String s2 : UpgradesManager.upgrades.getYml().getConfigurationSection("").getKeys(false)) {
            if (s2.startsWith("upgrade-")) {
                if (getMenuContent(s2) != null || loadUpgrade(s2)) continue;
                BedWars.getInstance().getLogger().log(Level.WARNING, "No se pudo cargar upgrade: " + s2);
            } else if (s2.startsWith("separator-")) {
                if (getMenuContent(s2) != null || loadSeparator(s2)) continue;
                BedWars.getInstance().getLogger().log(Level.WARNING, "No se pudo cargar separator: " + s2);
            } else if (s2.startsWith("category-")) {
                if (getMenuContent(s2) != null || loadCategory(s2)) continue;
                BedWars.getInstance().getLogger().log(Level.WARNING, "No se pudo cargar categoría: " + s2);
            } else if (s2.startsWith("base-trap-")) {
                if (getMenuContent(s2) != null || loadBaseTrap(s2)) continue;
                BedWars.getInstance().getLogger().log(Level.WARNING, "No se pudo cargar trampa: " + s2);
            } else {
                if (!s2.endsWith("-upgrades-settings")) continue;
                final String replace = s2.replace("-upgrades-settings", "");
                if (replace.isEmpty() || loadMenu(replace)) continue;
                BedWars.getInstance().getLogger().log(Level.WARNING, "No se pudo cargar menú: " + replace);
            }
        }
        BedWars.getInstance().registerEvents(new InventoryListener(), new UpgradeOpenListener());
    }
    
    public static boolean isWatchingUpgrades(final UUID uuid) {
        return UpgradesManager.upgradeViewers.contains(uuid);
    }
    
    public static void setWatchingUpgrades(final UUID uuid) {
        if (!UpgradesManager.upgradeViewers.contains(uuid)) UpgradesManager.upgradeViewers.add(uuid);
    }
    
    public static void removeWatchingUpgrades(final UUID uuid) {
        UpgradesManager.upgradeViewers.remove(uuid);
    }
    
    public static boolean loadMenu(final String s) {
        if (!UpgradesManager.upgrades.getYml().isSet(s + "-upgrades-settings.menu-content")) return false;
        if (UpgradesManager.menuByName.containsKey(s.toLowerCase())) return false;
        final InternalMenu internalMenu = new InternalMenu(s);
        for (String value : UpgradesManager.upgrades.getYml().getStringList(s + "-upgrades-settings.menu-content")) {
            final String[] split = value.split(",");
            if (split.length <= 1) continue;
            MenuContent menuContent = getMenuContent(split[0]);
            if (split[0].startsWith("category-")) {
                if (menuContent == null && loadCategory(split[0])) menuContent = getMenuContent(split[0]);
            } else if (split[0].startsWith("upgrade-")) {
                if (menuContent == null && loadUpgrade(split[0])) menuContent = getMenuContent(split[0]);
            } else if (split[0].startsWith("trap-slot-")) {
                if (menuContent == null && loadTrapSlot(split[0])) menuContent = getMenuContent(split[0]);
            } else if (split[0].startsWith("separator-")) {
                if (menuContent == null && loadSeparator(split[0])) menuContent = getMenuContent(split[0]);
            } else if (split[0].startsWith("base-trap-") && menuContent == null && loadBaseTrap(split[0])) {
                menuContent = getMenuContent(split[0]);
            }
            if (menuContent == null) continue;
            for (int i = 1; i < split.length; ++i) if (Misc.isNumber(split[i])) internalMenu.addContent(menuContent, Integer.parseInt(split[i]));
        }

        UpgradesManager.menuByName.put(s.toLowerCase(), internalMenu);
        BedWars.debug("Registrando menú de mejoras: " + s);
        return true;
    }
    
    private static boolean loadCategory(final String s) {
        if (s == null) return false;
        if (!s.startsWith("category-")) return false;
        if (UpgradesManager.upgrades.getYml().get(s) == null) return false;
        if (getMenuContent(s) != null) return false;
        final MenuCategory menuCategory = new MenuCategory(s, createDisplayItem(s));
        for (String value : UpgradesManager.upgrades.getYml().getStringList(s + ".category-content")) {
            final String[] split = value.split(",");
            if (split.length <= 1) continue;
            MenuContent menuContent = null;
            if (split[0].startsWith("category-")) {
                menuContent = getMenuContent(split[0]);
                if (menuContent == null && loadCategory(split[0])) menuContent = getMenuContent(split[0]);
            } else if (split[0].startsWith("upgrade-")) {
                menuContent = getMenuContent(split[0]);
                if (menuContent == null && loadUpgrade(split[0])) menuContent = getMenuContent(split[0]);
            } else if (split[0].startsWith("trap-slot-")) {
                menuContent = getMenuContent(split[0]);
                if (menuContent == null && loadTrapSlot(split[0])) menuContent = getMenuContent(split[0]);
            } else if (split[0].startsWith("separator-")) {
                menuContent = getMenuContent(split[0]);
                if (menuContent == null && loadSeparator(split[0])) menuContent = getMenuContent(split[0]);
            } else if (split[0].startsWith("base-trap-")) {
                menuContent = getMenuContent(split[0]);
                if (menuContent == null && loadBaseTrap(split[0])) menuContent = getMenuContent(split[0]);
            }
            if (menuContent == null) continue;
            for (int i = 1; i < split.length; ++i) if (Misc.isNumber(split[i])) menuCategory.addContent(menuContent, Integer.parseInt(split[i]));
        }
        UpgradesManager.menuContentByName.put(s.toLowerCase(), menuCategory);
        BedWars.debug("Registrando upgrade: " + s);
        return true;
    }
    
    private static boolean loadUpgrade(final String s) {
        if (s == null) return false;
        if (!s.startsWith("upgrade-")) return false;
        if (UpgradesManager.upgrades.getYml().get(s) == null) return false;
        if (UpgradesManager.upgrades.getYml().get(s + ".tier-1") == null) return false;
        if (getMenuContent(s) != null) return false;
        final MenuUpgrade menuUpgrade = new MenuUpgrade(s);
        for (final String s2 : UpgradesManager.upgrades.getYml().getConfigurationSection(s).getKeys(false)) {
            if (!s2.startsWith("tier-")) continue;
            if (UpgradesManager.upgrades.getYml().get(s + "." + s2 + ".receive") == null) {
                BedWars.debug("No se pudo cargar Upgrade " + s + " tier: " + s2 + ". Item que recibes no seteado.");
            } else if (UpgradesManager.upgrades.getYml().get(s + "." + s2 + ".display-item") == null) {
                BedWars.debug("No se pudo cargar Upgrade " + s + " tier: " + s2 + ". Display item no seteado.");
            } else if (UpgradesManager.upgrades.getYml().get(s + "." + s2 + ".cost") == null) {
                BedWars.debug("No se pudo cargar Upgrade " + s + " tier: " + s2 + ". Coste no seteado.");
            } else if (UpgradesManager.upgrades.getYml().get(s + "." + s2 + ".currency") == null) {
                BedWars.debug("No se pudo cargar Upgrade " + s + " tier: " + s2 + ". Currency no seteada.");
            } else {
                if (menuUpgrade.addTier(new UpgradeTier(s, s2, createDisplayItem(s + "." + s2), UpgradesManager.upgrades.getYml().getInt(s + "." + s2 + ".cost"), getCurrency(UpgradesManager.upgrades.getYml().getString(s + "." + s2 + ".currency"))))) continue;
                BedWars.getInstance().getLogger().log(Level.WARNING, "No se pudo cargar tier: " + s2 + " at upgrade: " + s);
            }
        }
        BedWars.debug("Registrando upgrade: " + s);
        UpgradesManager.menuContentByName.put(s.toLowerCase(), menuUpgrade);
        return true;
    }
    
    private static boolean loadSeparator(final String s) {
        if (s == null) return false;
        if (!s.startsWith("separator-")) return false;
        if (UpgradesManager.upgrades.getYml().get(s) == null) return false;
        if (getMenuContent(s) != null) return false;
        UpgradesManager.menuContentByName.put(s.toLowerCase(), new MenuSeparator(s, createDisplayItem(s)));
        BedWars.debug("Registrando upgrade: " + s);
        return true;
    }
    
    private static boolean loadTrapSlot(final String s) {
        if (s == null) return false;
        if (!s.startsWith("trap-slot-")) return false;
        if (UpgradesManager.upgrades.getYml().get(s) == null) return false;
        if (getMenuContent(s) != null) return false;
        UpgradesManager.menuContentByName.put(s.toLowerCase(), new MenuTrapSlot(s, createDisplayItem(s)));
        BedWars.debug("Registrando upgrade: " + s);
        return true;
    }
    
    private static boolean loadBaseTrap(final String s) {
        if (s == null) return false;
        if (!s.startsWith("base-trap-")) return false;
        if (UpgradesManager.upgrades.getYml().get(s) == null) return false;
        if (UpgradesManager.upgrades.getYml().get(s + ".receive") == null) {
            BedWars.debug("No se pudo cargar BaseTrap. Item que recibes no seteado.");
            return false;
        }
        if (UpgradesManager.upgrades.getYml().get(s + ".display-item") == null) {
            BedWars.debug("No se pudo cargar BaseTrap. Display item no seteado.");
            return false;
        }

        final MenuBaseTrap menuBaseTrap = new MenuBaseTrap(s, createDisplayItem(s), UpgradesManager.upgrades.getYml().getInt(s + ".cost"), getCurrency(UpgradesManager.upgrades.getYml().getString(s + ".currency")));
        BedWars.debug("Registrando upgrade: " + s);
        UpgradesManager.menuContentByName.put(s.toLowerCase(), menuBaseTrap);
        return true;
    }
    
    public static int getMoney(final Player player, final Material material) {
        if (material == Material.AIR) {
            final double money = BedWars.getInstance().getEconomy().getMoney(player);
            return (money % 2.0 == 0.0) ? ((int)money) : ((int)(money - 1.0));
        }
        return BedWars.getInstance().getApi().getShopUtil().calculateMoney(player, material);
    }
    
    public static Material getCurrency(final String s) {
        if (s == null || s.isEmpty()) return null;
        return BedWars.getInstance().getApi().getShopUtil().getCurrency(s);
    }
    
    public static MenuContent getMenuContent(final ItemStack itemStack) {
        if (itemStack == null) return null;
        final String customData = BedWars.getInstance().getNms().getCustomData(itemStack);
        if (customData == null) return null;
        if (customData.equals("null")) return null;
        if (!customData.startsWith("MCONT_")) return null;
        final String replaceFirst = customData.replaceFirst("MCONT_", "");
        if (replaceFirst.isEmpty()) return null;
        return UpgradesManager.menuContentByName.getOrDefault(replaceFirst.toLowerCase(), null);
    }
    
    public static MenuContent getMenuContent(final String s) {
        return UpgradesManager.menuContentByName.getOrDefault(s.toLowerCase(), null);
    }
    
    public static UpgradesIndex getMenuForArena(final IArena arena) {
        if (UpgradesManager.customMenuForArena.containsKey(arena)) return UpgradesManager.customMenuForArena.get(arena);
        return UpgradesManager.menuByName.getOrDefault(arena.getGroup().toLowerCase(), UpgradesManager.menuByName.get("default"));
    }
    
    private static ItemStack createDisplayItem(final String s) {
        Material material;
        try {
            material = Material.valueOf(UpgradesManager.upgrades.getYml().getString(s + ".display-item.material"));
        } catch (Exception ex) {
            material = Material.BEDROCK;
        }
        final ItemStack itemStack = new ItemStack(material, Integer.parseInt(UpgradesManager.upgrades.getYml().getString(s + ".display-item.amount")), (short)UpgradesManager.upgrades.getYml().getInt(s + ".display-item.data"));
        if (UpgradesManager.upgrades.getYml().getBoolean(s + ".display-item.enchanted")) {
            itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            final ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                itemStack.setItemMeta(itemMeta);
            }
        }
        return itemStack;
    }
    
    public static String getCurrencyMsg(final Player player, final UpgradeTier upgradeTier) {
        String s = "";
        switch (upgradeTier.getCurrency()) {
            case IRON_INGOT: {
                s = ((upgradeTier.getCost() == 1) ? Messages.MEANING_IRON_SINGULAR : Messages.MEANING_IRON_PLURAL);
                break;
            }
            case GOLD_INGOT: {
                s = ((upgradeTier.getCost() == 1) ? Messages.MEANING_GOLD_SINGULAR : Messages.MEANING_GOLD_PLURAL);
                break;
            }
            case EMERALD: {
                s = ((upgradeTier.getCost() == 1) ? Messages.MEANING_EMERALD_SINGULAR : Messages.MEANING_EMERALD_PLURAL);
                break;
            }
            case DIAMOND: {
                s = ((upgradeTier.getCost() == 1) ? Messages.MEANING_DIAMOND_SINGULAR : Messages.MEANING_DIAMOND_PLURAL);
                break;
            }
            case AIR: {
                s = ((upgradeTier.getCost() == 1) ? Messages.MEANING_VAULT_SINGULAR : Messages.MEANING_VAULT_PLURAL);
                break;
            }
        }
        return Language.getMsg(player, s);
    }
    
    public static String getCurrencyMsg(final Player player, final int n, final String s) {
        if (s == null) return Language.getMsg(player, (n == 1) ? Messages.MEANING_VAULT_SINGULAR : Messages.MEANING_VAULT_PLURAL);
        final String lowerCase = s.toLowerCase();
        String s2;
        switch (lowerCase) {
            case "iron": {
                s2 = ((n == 1) ? Messages.MEANING_IRON_SINGULAR : Messages.MEANING_IRON_PLURAL);
                break;
            }
            case "gold": {
                s2 = ((n == 1) ? Messages.MEANING_GOLD_SINGULAR : Messages.MEANING_GOLD_PLURAL);
                break;
            }
            case "emerald": {
                s2 = ((n == 1) ? Messages.MEANING_EMERALD_SINGULAR : Messages.MEANING_EMERALD_PLURAL);
                break;
            }
            case "diamond": {
                s2 = ((n == 1) ? Messages.MEANING_DIAMOND_SINGULAR : Messages.MEANING_DIAMOND_PLURAL);
                break;
            }
            default: {
                s2 = ((n == 1) ? Messages.MEANING_VAULT_SINGULAR : Messages.MEANING_VAULT_PLURAL);
                break;
            }
        }
        return Language.getMsg(player, s2);
    }
    
    public static String getCurrencyMsg(final Player player, final int n, final Material material) {
        String s;
        switch (material) {
            case IRON_INGOT: {
                s = ((n == 1) ? Messages.MEANING_IRON_SINGULAR : Messages.MEANING_IRON_PLURAL);
                break;
            }
            case GOLD_INGOT: {
                s = ((n == 1) ? Messages.MEANING_GOLD_SINGULAR : Messages.MEANING_GOLD_PLURAL);
                break;
            }
            case EMERALD: {
                s = ((n == 1) ? Messages.MEANING_EMERALD_SINGULAR : Messages.MEANING_EMERALD_PLURAL);
                break;
            }
            case DIAMOND: {
                s = ((n == 1) ? Messages.MEANING_DIAMOND_SINGULAR : Messages.MEANING_DIAMOND_PLURAL);
                break;
            }
            default: {
                s = ((n == 1) ? Messages.MEANING_VAULT_SINGULAR : Messages.MEANING_VAULT_PLURAL);
                break;
            }
        }
        return Language.getMsg(player, s);
    }
    
    public static ChatColor getCurrencyColor(final Material material) {
        switch (material) {
            case DIAMOND: {
                return ChatColor.AQUA;
            }
            case GOLD_INGOT: {
                return ChatColor.GOLD;
            }
            case IRON_INGOT: {
                return ChatColor.WHITE;
            }
            case EMERALD: {
                return ChatColor.GREEN;
            }
            default: {
                return ChatColor.DARK_GREEN;
            }
        }
    }
    
    public static UpgradesConfig getConfiguration() {
        return UpgradesManager.upgrades;
    }
    
    static {
        upgradeViewers = new LinkedList<>();
        menuContentByName = new HashMap<>();
        menuByName = new HashMap<>();
        customMenuForArena = new HashMap<>();
    }
}
