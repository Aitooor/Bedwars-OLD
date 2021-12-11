package dev.eugenio.nasgarbedwars.upgrades.menu;

import dev.eugenio.nasgarbedwars.configuration.Sounds;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.events.upgrades.UpgradeEvent;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.api.upgrades.MenuContent;
import dev.eugenio.nasgarbedwars.api.upgrades.TeamUpgrade;
import dev.eugenio.nasgarbedwars.api.upgrades.UpgradeAction;
import dev.eugenio.nasgarbedwars.upgrades.UpgradesManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MenuUpgrade implements MenuContent, TeamUpgrade {
    @Getter
    private final String name;
    private final List<UpgradeTier> tiers;

    public MenuUpgrade(final String name) {
        this.tiers = new LinkedList<>();
        this.name = name;
    }

    @Override
    public ItemStack getDisplayItem(final Player player, final ITeam team) {
        if (this.tiers.isEmpty()) return new ItemStack(Material.BEDROCK);
        int intValue = -1;
        if (team.getTeamUpgradeTiers().containsKey(this.getName()))
            intValue = team.getTeamUpgradeTiers().get(this.getName());
        final boolean b = this.getTiers().size() == intValue + 1 && team.getTeamUpgradeTiers().containsKey(this.getName());
        if (!b) ++intValue;
        final UpgradeTier upgradeTier = this.getTiers().get(intValue);
        final boolean b2 = UpgradesManager.getMoney(player, upgradeTier.getCurrency()) >= upgradeTier.getCost();
        final ItemStack itemStack = new ItemStack(this.tiers.get(intValue).getDisplayItem());
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return itemStack;
        String s;
        if (!b) {
            if (b2) {
                s = Language.getMsg(player, Messages.FORMAT_UPGRADE_COLOR_CAN_AFFORD);
            } else {
                s = Language.getMsg(player, Messages.FORMAT_UPGRADE_COLOR_CANT_AFFORD);
            }
        } else {
            s = Language.getMsg(player, Messages.FORMAT_UPGRADE_COLOR_UNLOCKED);
        }
        itemMeta.setDisplayName(Language.getMsg(player, Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("{name}", this.getName().replace("upgrade-", "")).replace("{tier}", upgradeTier.getName())).replace("{color}", s));
        final ArrayList<String> lore = new ArrayList<>();
        final String currencyMsg = UpgradesManager.getCurrencyMsg(player, upgradeTier);
        for (String value : Language.getList(player, Messages.UPGRADES_UPGRADE_TIER_ITEM_LORE.replace("{name}", this.getName().replace("upgrade-", "")).replace("{tier}", upgradeTier.getName()))) {
            lore.add(value.replace("{cost}", String.valueOf(upgradeTier.getCost())).replace("{currency}", currencyMsg).replace("{tierColor}", Language.getMsg(player, b ? Messages.FORMAT_UPGRADE_TIER_UNLOCKED : Messages.FORMAT_UPGRADE_TIER_LOCKED)).replace("{color}", s));
        }
        if (b) {
            lore.add(Language.getMsg(player, Messages.UPGRADES_LORE_REPLACEMENT_UNLOCKED).replace("{color}", s));
        } else if (b2) {
            lore.add(Language.getMsg(player, Messages.UPGRADES_LORE_REPLACEMENT_CLICK_TO_BUY).replace("{color}", s));
        } else {
            lore.add(Language.getMsg(player, Messages.UPGRADES_LORE_REPLACEMENT_INSUFFICIENT_MONEY).replace("{currency}", currencyMsg).replace("{color}", s));
        }
        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public void onClick(final Player player, final ClickType clickType, final ITeam team) {
        int intValue = -1;
        if (team.getTeamUpgradeTiers().containsKey(this.getName()))
            intValue = team.getTeamUpgradeTiers().get(this.getName());
        if (this.getTiers().size() - 1 > intValue) {
            final UpgradeTier upgradeTier = this.getTiers().get(intValue + 1);
            final int money = UpgradesManager.getMoney(player, upgradeTier.getCurrency());
            if (money < upgradeTier.getCost()) {
                Sounds.playSound("shop-insufficient-money", player);
                player.sendMessage(Language.getMsg(player, Messages.SHOP_INSUFFICIENT_MONEY).replace("{currency}", UpgradesManager.getCurrencyMsg(player, upgradeTier)).replace("{amount}", String.valueOf(upgradeTier.getCost() - money)));
                player.closeInventory();
                return;
            }
            if (upgradeTier.getCurrency() == Material.AIR) {
                BedWars.getInstance().getEconomy().buyAction(player, upgradeTier.getCost());
            } else {
                BedWars.getInstance().getApi().getShopUtil().takeMoney(player, upgradeTier.getCurrency(), upgradeTier.getCost());
            }
            if (team.getTeamUpgradeTiers().containsKey(this.getName())) {
                team.getTeamUpgradeTiers().replace(this.getName(), team.getTeamUpgradeTiers().get(this.getName()) + 1);
            } else {
                team.getTeamUpgradeTiers().put(this.getName(), 0);
            }
            Sounds.playSound("shop-bought", player);
            for (UpgradeAction upgradeAction : upgradeTier.getUpgradeActions()) upgradeAction.onBuy(player, team);
            player.closeInventory();
            for (final Player player2 : team.getMembers())
                player2.sendMessage(Language.getMsg(player2, Messages.UPGRADES_UPGRADE_BOUGHT_CHAT).replace("{player}", player.getDisplayName()).replace("{upgradeName}", ChatColor.stripColor(Language.getMsg(player2, Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("{name}", this.getName().replace("upgrade-", "")).replace("{tier}", upgradeTier.getName())))).replace("{color}", ""));
            Bukkit.getPluginManager().callEvent(new UpgradeEvent(this, player, team));
        }
    }

    public boolean addTier(final UpgradeTier upgradeTier) {
        for (UpgradeTier tier : this.tiers) {
            if (tier.getName().equalsIgnoreCase(upgradeTier.getName())) {
                return false;
            }
        }
        this.tiers.add(upgradeTier);
        return true;
    }

    public List<UpgradeTier> getTiers() {
        return Collections.unmodifiableList(this.tiers);
    }
}
