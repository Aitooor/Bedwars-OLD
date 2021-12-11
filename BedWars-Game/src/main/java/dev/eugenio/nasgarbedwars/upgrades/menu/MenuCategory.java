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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuCategory implements MenuContent {
    private final ItemStack displayItem;
    @Getter private final String name;
    private final HashMap<Integer, MenuContent> menuContentBySlot = new HashMap<>();;
    
    public MenuCategory(final String name, final ItemStack itemStack) {
        this.name = name;
        this.displayItem = BedWars.getInstance().getNms().addCustomData(itemStack, "MCONT_" + name);
        Language.saveIfNotExists(Messages.UPGRADES_CATEGORY_GUI_NAME_PATH + name.replace("category-", ""), "&8" + name);
        Language.saveIfNotExists(Messages.UPGRADES_CATEGORY_ITEM_NAME_PATH + name.replace("category-", ""), "&cNombre no especificado");
        Language.saveIfNotExists(Messages.UPGRADES_CATEGORY_ITEM_LORE_PATH + name.replace("category-", ""), Collections.singletonList("&cLore no especificado"));
    }
    
    public boolean addContent(final MenuContent menuContent, final int n) {
        if (this.menuContentBySlot.get(n) != null) return false;
        this.menuContentBySlot.put(n, menuContent);
        return true;
    }
    
    @Override
    public ItemStack getDisplayItem(final Player player, final ITeam team) {
        final ItemStack itemStack = new ItemStack(this.displayItem);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(Language.getMsg(player, Messages.UPGRADES_CATEGORY_ITEM_NAME_PATH + this.name.replace("category-", "")));
            final List<String> list = Language.getList(player, Messages.UPGRADES_CATEGORY_ITEM_LORE_PATH + this.name.replace("category-", ""));
            if (this.name.equalsIgnoreCase("traps")) {
                int n = UpgradesManager.getConfiguration().getInt(team.getArena().getGroup().toLowerCase() + "-upgrades-settings.trap-queue-limit");
                if (n == 0) n = UpgradesManager.getConfiguration().getInt("default-upgrades-settings.trap-queue-limit");
                if (n == team.getActiveTraps().size()) {
                    list.add("");
                    list.add(Language.getMsg(player, Messages.UPGRADES_TRAP_QUEUE_LIMIT));
                }
            }
            itemMeta.setLore(list);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
    
    @Override
    public void onClick(final Player player, final ClickType clickType, final ITeam team) {
        if (this.name.equalsIgnoreCase("category-traps")) {
            int n = UpgradesManager.getConfiguration().getInt(team.getArena().getGroup().toLowerCase() + "-upgrades-settings.trap-queue-limit");
            if (n == 0) n = UpgradesManager.getConfiguration().getInt("default-upgrades-settings.trap-queue-limit");
            if (n <= team.getActiveTraps().size()) {
                player.sendMessage(Language.getMsg(player, Messages.UPGRADES_TRAP_QUEUE_LIMIT));
                return;
            }
        }
        final Inventory inventory = Bukkit.createInventory(null, 45, Language.getMsg(player, Messages.UPGRADES_CATEGORY_GUI_NAME_PATH + this.name.replace("category-", "")));
        for (final Map.Entry<Integer, MenuContent> entry : this.menuContentBySlot.entrySet()) inventory.setItem(entry.getKey(), entry.getValue().getDisplayItem(player, team));
        player.openInventory(inventory);
        UpgradesManager.setWatchingUpgrades(player.getUniqueId());
    }
}
