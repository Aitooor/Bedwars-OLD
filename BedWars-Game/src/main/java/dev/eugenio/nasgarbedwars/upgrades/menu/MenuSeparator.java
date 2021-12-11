package dev.eugenio.nasgarbedwars.upgrades.menu;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.api.upgrades.MenuContent;
import dev.eugenio.nasgarbedwars.upgrades.UpgradesManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuSeparator implements MenuContent {
    private ItemStack displayItem;
    @Getter private String name;
    private final List<String> playerCommands;
    private final List<String> consoleCommands;
    
    public MenuSeparator(final String name, final ItemStack itemStack) {
        this.playerCommands = new ArrayList<>();
        this.consoleCommands = new ArrayList<>();
        if (name == null) return;
        this.displayItem = BedWars.getInstance().getNms().addCustomData(itemStack, "MCONT_" + name);
        this.name = name;
        Language.saveIfNotExists(Messages.UPGRADES_SEPARATOR_ITEM_NAME_PATH + name.replace("separator-", ""), "&cName not set");
        Language.saveIfNotExists(Messages.UPGRADES_SEPARATOR_ITEM_LORE_PATH + name.replace("separator-", ""), Collections.singletonList("&cLore not set"));
        if (UpgradesManager.getConfiguration().getYml().getStringList(name + ".on-click.player") != null) this.playerCommands.addAll(UpgradesManager.getConfiguration().getYml().getStringList(name + ".on-click.player"));
        if (UpgradesManager.getConfiguration().getYml().getStringList(name + ".on-click.console") != null) this.consoleCommands.addAll(UpgradesManager.getConfiguration().getYml().getStringList(name + ".on-click.console"));
    }
    
    @Override
    public ItemStack getDisplayItem(final Player player, final ITeam team) {
        final ItemStack itemStack = new ItemStack(this.displayItem);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(Language.getMsg(player, Messages.UPGRADES_SEPARATOR_ITEM_NAME_PATH + this.name.replace("separator-", "")));
            itemMeta.setLore(Language.getList(player, Messages.UPGRADES_SEPARATOR_ITEM_LORE_PATH + this.name.replace("separator-", "")));
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
    
    @Override
    public void onClick(final Player player, final ClickType clickType, final ITeam team) {
        for (final String s : this.playerCommands) {
            if (s.trim().isEmpty()) continue;
            Bukkit.dispatchCommand(player, s.replace("{player}", player.getDisplayName()).replace("{team}", (team == null) ? "null" : team.getDisplayName(Language.getPlayerLanguage(player))));
        }
        for (final String s2 : this.consoleCommands) {
            if (s2.trim().isEmpty()) continue;
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s2.replace("{player}", player.getDisplayName()).replace("{team}", (team == null) ? "null" : team.getDisplayName(Language.getPlayerLanguage(player))));
        }
    }
}
