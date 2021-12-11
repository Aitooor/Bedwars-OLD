package dev.eugenio.nasgarbedwars.upgrades.menu;

import dev.eugenio.nasgarbedwars.configuration.Sounds;
import dev.eugenio.nasgarbedwars.upgrades.trapaction.DisenchantAction;
import dev.eugenio.nasgarbedwars.upgrades.trapaction.PlayerEffectAction;
import dev.eugenio.nasgarbedwars.upgrades.trapaction.RemoveEffectAction;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.events.upgrades.UpgradeEvent;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.api.upgrades.EnemyBaseEnterTrap;
import dev.eugenio.nasgarbedwars.api.upgrades.MenuContent;
import dev.eugenio.nasgarbedwars.api.upgrades.TeamUpgrade;
import dev.eugenio.nasgarbedwars.api.upgrades.TrapAction;
import dev.eugenio.nasgarbedwars.upgrades.UpgradesManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuBaseTrap implements MenuContent, EnemyBaseEnterTrap, TeamUpgrade {
    private final ItemStack displayItem;
    @Getter private final String name;
    private final int cost;
    private final Material currency;
    private final List<TrapAction> trapActions;
    
    public MenuBaseTrap(final String name, final ItemStack itemStack, final int cost, final Material currency) {
        this.trapActions = new ArrayList<>();
        this.displayItem = BedWars.getInstance().getNms().addCustomData(itemStack, "MCONT_" + name);
        this.name = name;
        final String replace = name.replace("base-trap-", "");
        Language.saveIfNotExists(Messages.UPGRADES_BASE_TRAP_ITEM_NAME_PATH + replace, "&cNombre no especificado");
        Language.saveIfNotExists(Messages.UPGRADES_BASE_TRAP_ITEM_LORE_PATH + replace, Collections.singletonList("&cLore no especificado"));
        if (UpgradesManager.getConfiguration().getBoolean(name + ".custom-announce")) {
            Language.saveIfNotExists(Messages.UPGRADES_TRAP_CUSTOM_MSG + replace, "Editar path: " + Messages.UPGRADES_TRAP_CUSTOM_MSG + replace);
            Language.saveIfNotExists(Messages.UPGRADES_TRAP_CUSTOM_TITLE + replace, "Editar path: " + Messages.UPGRADES_TRAP_CUSTOM_TITLE + replace);
            Language.saveIfNotExists(Messages.UPGRADES_TRAP_CUSTOM_SUBTITLE + replace, "Editar path: " + Messages.UPGRADES_TRAP_CUSTOM_SUBTITLE + replace);
        }

        this.cost = cost;
        this.currency = currency;

        for (String s : UpgradesManager.getConfiguration().getYml().getStringList(name + ".receive")) {
            final String[] split = s.trim().split(":");
            if (split.length < 2) continue;
            final String[] split2 = split[1].trim().toLowerCase().split(",");
            TrapAction trapAction = null;
            final String lowerCase = split[0].trim().toLowerCase();
            switch (lowerCase) {
                case "player-effect": {
                    if (split2.length < 4) {
                        BedWars.getInstance().getLogger().warning("Inválido " + split[0] + " en upgrades: " + name);
                        continue;
                    }
                    final PotionEffectType byName = PotionEffectType.getByName(split2[0].toUpperCase());
                    if (byName == null) {
                        BedWars.getInstance().getLogger().warning("Efecto de poción inválido " + split2[0] + " en upgrades: " + name);
                        continue;
                    }
                    PlayerEffectAction.ApplyType applyType = null;
                    final String lowerCase2 = split2[3].toLowerCase();
                    switch (lowerCase2) {
                        case "team": {
                            applyType = PlayerEffectAction.ApplyType.TEAM;
                            break;
                        }
                        case "base": {
                            applyType = PlayerEffectAction.ApplyType.BASE;
                            break;
                        }
                        case "enemy":
                        case "enemies": {
                            applyType = PlayerEffectAction.ApplyType.ENEMY;
                            break;
                        }
                    }
                    if (applyType == null) {
                        BedWars.getInstance().getLogger().warning("Inválido apply type " + split2[3] + " en upgrades: " + name);
                        continue;
                    }
                    int int1 = 1;
                    int int2 = 0;
                    try {
                        int1 = Integer.parseInt(split2[1]);
                        int2 = Integer.parseInt(split2[2]);
                    } catch (Exception ignored) {
                        // Ignored
                    }
                    trapAction = new PlayerEffectAction(byName, int1, int2, applyType);
                    break;
                }
                case "disenchant-item": {
                    if (split2.length < 2) {
                        BedWars.getInstance().getLogger().warning("Inválido " + split[0] + " en upgrades: " + name);
                        continue;
                    }
                    final Enchantment byName2 = Enchantment.getByName(split2[0].toUpperCase());
                    if (byName2 == null) {
                        BedWars.getInstance().getLogger().warning("Encantamiento inválido " + split2[0] + " en upgrades: " + name);
                        continue;
                    }
                    DisenchantAction.ApplyType applyType2 = null;
                    final String lowerCase3 = split2[1].toLowerCase();
                    switch (lowerCase3) {
                        case "sword": {
                            applyType2 = DisenchantAction.ApplyType.SWORD;
                            break;
                        }
                        case "armor": {
                            applyType2 = DisenchantAction.ApplyType.ARMOR;
                            break;
                        }
                        case "bow": {
                            applyType2 = DisenchantAction.ApplyType.BOW;
                            break;
                        }
                    }
                    if (applyType2 == null) {
                        BedWars.getInstance().getLogger().warning("Inválido apply type " + split2[3] + " en upgrades: " + name);
                        continue;
                    }
                    trapAction = new DisenchantAction(byName2, applyType2);
                    break;
                }
                case "remove-effect": {
                    if (split2.length < 1) {
                        BedWars.getInstance().getLogger().warning("Inválido " + split[0] + " en upgrades: " + name);
                        continue;
                    }
                    final PotionEffectType byName3 = PotionEffectType.getByName(split2[0].toUpperCase());
                    if (byName3 == null) {
                        BedWars.getInstance().getLogger().warning("Inválido potion effect " + split2[0] + " en upgrades: " + name);
                        continue;
                    }
                    trapAction = new RemoveEffectAction(byName3);
                    break;
                }
            }
            if (trapAction == null) continue;
            this.trapActions.add(trapAction);
        }
    }
    
    @Override
    public ItemStack getDisplayItem(final Player player, final ITeam team) {
        Material material = this.currency;
        if (this.currency == null) {
            String s = UpgradesManager.getConfiguration().getYml().getString(team.getArena().getGroup().toLowerCase() + "-upgrades-settings.trap-currency");
            if (s == null) {
                s = UpgradesManager.getConfiguration().getYml().getString("default-upgrades-settings.trap-currency");
            }
            material = Material.valueOf(s.toUpperCase());
        }
        int cost = this.cost;
        if (cost == 0) {
            final int size = team.getActiveTraps().size();
            int n = UpgradesManager.getConfiguration().getYml().getInt(team.getArena().getGroup().toLowerCase() + "-upgrades-settings.trap-increment-price");
            if (n == 0) {
                n = UpgradesManager.getConfiguration().getYml().getInt("default-upgrades-settings.trap-increment-price");
            }
            int n2 = UpgradesManager.getConfiguration().getYml().getInt(team.getArena().getGroup().toLowerCase() + "-upgrades-settings.trap-start-price");
            if (n2 == 0) {
                n2 = UpgradesManager.getConfiguration().getYml().getInt("default-upgrades-settings.trap-start-price");
            }
            cost = n2 + size * n;
        }
        final ItemStack clone = this.displayItem.clone();
        final ItemMeta itemMeta = clone.getItemMeta();
        if (itemMeta != null) {
            final boolean b = UpgradesManager.getMoney(player, material) >= cost;
            String s2;
            if (b) {
                s2 = Language.getMsg(player, Messages.FORMAT_UPGRADE_COLOR_CAN_AFFORD);
            } else {
                s2 = Language.getMsg(player, Messages.FORMAT_UPGRADE_COLOR_CANT_AFFORD);
            }
            itemMeta.setDisplayName(Language.getMsg(player, Messages.UPGRADES_BASE_TRAP_ITEM_NAME_PATH + this.name.replace("base-trap-", "")).replace("{color}", s2));
            final List<String> list = Language.getList(player, Messages.UPGRADES_BASE_TRAP_ITEM_LORE_PATH + this.name.replace("base-trap-", ""));
            final String currencyMsg = UpgradesManager.getCurrencyMsg(player, cost, material);
            list.add(Language.getMsg(player, Messages.FORMAT_UPGRADE_TRAP_COST).replace("{cost}", String.valueOf(cost)).replace("{currency}", currencyMsg).replace("{currencyColor}", String.valueOf(UpgradesManager.getCurrencyColor(material))));
            list.add("");
            if (b) {
                list.add(Language.getMsg(player, Messages.UPGRADES_LORE_REPLACEMENT_CLICK_TO_BUY).replace("{color}", s2));
            }
            else {
                list.add(Language.getMsg(player, Messages.UPGRADES_LORE_REPLACEMENT_INSUFFICIENT_MONEY).replace("{currency}", currencyMsg).replace("{color}", s2));
            }
            itemMeta.setLore(list);
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            clone.setItemMeta(itemMeta);
        }
        return clone;
    }
    
    @Override
    public void onClick(final Player player, final ClickType clickType, final ITeam team) {
        int n = UpgradesManager.getConfiguration().getInt(team.getArena().getGroup().toLowerCase() + "-upgrades-settings.trap-queue-limit");
        if (n == 0) n = UpgradesManager.getConfiguration().getInt("default-upgrades-settings.trap-queue-limit");
        if (n <= team.getActiveTraps().size()) {
            player.sendMessage(Language.getMsg(player, Messages.UPGRADES_TRAP_QUEUE_LIMIT));
            return;
        }
        Material material = this.currency;
        if (this.currency == null) {
            String s = UpgradesManager.getConfiguration().getYml().getString(team.getArena().getGroup().toLowerCase() + "-upgrades-settings.trap-currency");
            if (s == null) s = UpgradesManager.getConfiguration().getYml().getString("default-upgrades-settings.trap-currency");
            material = Material.valueOf(s.toUpperCase());
        }
        int cost = this.cost;
        if (cost == 0) {
            final int size = team.getActiveTraps().size();
            int n2 = UpgradesManager.getConfiguration().getYml().getInt(team.getArena().getGroup().toLowerCase() + "-upgrades-settings.trap-increment-price");
            if (n2 == 0) n2 = UpgradesManager.getConfiguration().getYml().getInt("default-upgrades-settings.trap-increment-price");
            int n3 = UpgradesManager.getConfiguration().getYml().getInt(team.getArena().getGroup().toLowerCase() + "-upgrades-settings.trap-start-price");
            if (n3 == 0) n3 = UpgradesManager.getConfiguration().getYml().getInt("default-upgrades-settings.trap-start-price");
            cost = n3 + size * n2;
        }
        final int money = UpgradesManager.getMoney(player, material);
        if (money < cost) {
            Sounds.playSound("shop-insufficient-money", player);
            player.sendMessage(Language.getMsg(player, Messages.SHOP_INSUFFICIENT_MONEY).replace("{currency}", UpgradesManager.getCurrencyMsg(player, cost, material)).replace("{amount}", String.valueOf(cost - money)));
            player.closeInventory();
            return;
        }
        if (material == Material.AIR) {
            BedWars.getInstance().getEconomy().buyAction(player, money);
        } else {
            BedWars.getInstance().getApi().getShopUtil().takeMoney(player, material, cost);
        }
        Sounds.playSound("shop-bought", player);
        team.getActiveTraps().add(this);
        for (final Player player2 : team.getMembers()) player2.sendMessage(Language.getMsg(player2, Messages.UPGRADES_UPGRADE_BOUGHT_CHAT).replace("{player}", player.getDisplayName()).replace("{upgradeName}", ChatColor.stripColor(Language.getMsg(player2, Messages.UPGRADES_BASE_TRAP_ITEM_NAME_PATH + this.getName().replace("base-trap-", "")).replace("{color}", ""))));
        Bukkit.getPluginManager().callEvent(new UpgradeEvent(this, player, team));
        UpgradesManager.getMenuForArena(team.getArena()).open(player);
    }
    
    @Override
    public String getNameMsgPath() {
        return Messages.UPGRADES_BASE_TRAP_ITEM_NAME_PATH + this.name.replace("base-trap-", "");
    }
    
    @Override
    public String getLoreMsgPath() {
        return Messages.UPGRADES_BASE_TRAP_ITEM_LORE_PATH + this.name.replace("base-trap-", "");
    }

    @Override
    public ItemStack getItemStack() {
        return this.displayItem;
    }
    
    @Override
    public void trigger(final ITeam team, final Player player) {
        Sound value = null;
        if (UpgradesManager.getConfiguration().getYml().get(this.name + ".sound") != null) {
            try {
                value = Sound.valueOf(UpgradesManager.getConfiguration().getYml().getString(this.name + ".sound"));
            }
            catch (Exception ignored) {
                // Ignored
            }
        }
        if (!Sounds.playSound(value, team.getMembers())) {
            Sounds.playSound("trap-sound", team.getMembers());
        }
        this.trapActions.forEach(trapAction -> trapAction.onTrigger(player, team.getArena().getTeam(player), team));
        if (UpgradesManager.getConfiguration().getBoolean(this.name + ".custom-announce")) {
            final String replace = this.name.replace("base-trap-", "");
            final String s = (team.getArena().getTeam(player) == null) ? "" : team.getArena().getTeam(player).getColor().chat().toString();
            for (final Player player2 : team.getMembers()) {
                final String replace2 = ChatColor.stripColor(Language.getMsg(player2, this.getNameMsgPath())).replace("{color}", "");
                final String s2 = (team.getArena().getTeam(player) == null) ? "NULL" : team.getArena().getTeam(player).getDisplayName(Language.getPlayerLanguage(player2));
                player2.sendMessage(Language.getMsg(player2, Messages.UPGRADES_TRAP_CUSTOM_MSG + replace).replace("{trap}", replace2).replace("{player}", player.getName()).replace("{team}", s2).replace("{color}", s));
                BedWars.getInstance().getNms().sendTitle(player2, Language.getMsg(player2, Messages.UPGRADES_TRAP_CUSTOM_TITLE + replace).replace("{trap}", replace2).replace("{player}", player.getName()).replace("{team}", s2).replace("{color}", s), Language.getMsg(player2, Messages.UPGRADES_TRAP_CUSTOM_SUBTITLE + replace).replace("{trap}", replace2).replace("{player}", player.getName()).replace("{team}", s2).replace("{color}", s), 15, 35, 10);
            }
        } else {
            for (final Player player3 : team.getMembers()) {
                final String replace3 = ChatColor.stripColor(Language.getMsg(player3, this.getNameMsgPath())).replace("{color}", "");
                player3.sendMessage(Language.getMsg(player3, Messages.UPGRADES_TRAP_DEFAULT_MSG).replace("{trap}", replace3));
                BedWars.getInstance().getNms().sendTitle(player3, Language.getMsg(player3, Messages.UPGRADES_TRAP_DEFAULT_TITLE).replace("{trap}", replace3), Language.getMsg(player3, Messages.UPGRADES_TRAP_DEFAULT_SUBTITLE).replace("{trap}", replace3), 15, 35, 10);
            }
        }
    }
}
