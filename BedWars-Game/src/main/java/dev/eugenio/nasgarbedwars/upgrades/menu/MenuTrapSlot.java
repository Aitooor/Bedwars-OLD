package dev.eugenio.nasgarbedwars.upgrades.menu;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.api.upgrades.EnemyBaseEnterTrap;
import dev.eugenio.nasgarbedwars.api.upgrades.MenuContent;
import dev.eugenio.nasgarbedwars.upgrades.UpgradesManager;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;

public class MenuTrapSlot implements MenuContent {
    private final ItemStack displayItem;
    @Getter private final String name;
    private int trap;
    
    public MenuTrapSlot(final String name, final ItemStack itemStack) {
        this.displayItem = BedWars.getInstance().getNms().addCustomData(itemStack, "MCONT_" + name);
        this.name = name;
        Language.saveIfNotExists(Messages.UPGRADES_TRAP_SLOT_ITEM_NAME_PATH + name.replace("trap-slot-", ""), "&cName not set");
        Language.saveIfNotExists(Messages.UPGRADES_TRAP_SLOT_ITEM_LORE1_PATH + name.replace("trap-slot-", ""), Collections.singletonList("&cLore1 not set"));
        Language.saveIfNotExists(Messages.UPGRADES_TRAP_SLOT_ITEM_LORE2_PATH + name.replace("trap-slot-", ""), Collections.singletonList("&cLore2 not set"));
        this.trap = UpgradesManager.getConfiguration().getInt(name + ".trap");
        if (this.trap < 0) this.trap = 0;
        if (this.trap != 0) --this.trap;
    }
    
    @Override
    public ItemStack getDisplayItem(final Player player, final ITeam team) {
        ItemStack itemStack = this.displayItem.clone();
        EnemyBaseEnterTrap enemyBaseEnterTrap = null;
        if (!team.getActiveTraps().isEmpty() && team.getActiveTraps().size() > this.trap) enemyBaseEnterTrap = team.getActiveTraps().get(this.trap);
        if (enemyBaseEnterTrap != null) itemStack = enemyBaseEnterTrap.getItemStack().clone();
        itemStack.setAmount(this.trap + 1);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return itemStack;
        itemMeta.setDisplayName(Language.getMsg(player, Messages.UPGRADES_TRAP_SLOT_ITEM_NAME_PATH + this.name.replace("trap-slot-", "")).replace("{name}", Language.getMsg(player, (enemyBaseEnterTrap == null) ? Messages.MEANING_NO_TRAP : enemyBaseEnterTrap.getNameMsgPath())).replace("{color}", Language.getMsg(player, (enemyBaseEnterTrap == null) ? Messages.FORMAT_UPGRADE_COLOR_CANT_AFFORD : Messages.FORMAT_UPGRADE_COLOR_UNLOCKED)));
        final ArrayList<String> lore = new ArrayList<>();
        if (enemyBaseEnterTrap == null) {
            int n = UpgradesManager.getConfiguration().getInt(team.getArena().getArenaName().toLowerCase() + "-upgrades-settings.trap-start-price");
            if (n == 0) n = UpgradesManager.getConfiguration().getInt("default-upgrades-settings.trap-start-price");
            String s = UpgradesManager.getConfiguration().getString(team.getArena().getArenaName().toLowerCase() + "-upgrades-settings.trap-currency");
            if (s == null) s = UpgradesManager.getConfiguration().getString("default-upgrades-settings.trap-currency");
            final String currencyMsg = UpgradesManager.getCurrencyMsg(player, n, s);
            if (!team.getActiveTraps().isEmpty()) {
                int n2 = UpgradesManager.getConfiguration().getInt(team.getArena().getArenaName().toLowerCase() + "-upgrades-settings.trap-increment-price");
                if (n2 == 0) n2 = UpgradesManager.getConfiguration().getInt("default-upgrades-settings.trap-increment-price");
                n += team.getActiveTraps().size() * n2;
            }
            for (String value : Language.getList(player, Messages.UPGRADES_TRAP_SLOT_ITEM_LORE1_PATH + this.name.replace("trap-slot-", ""))) lore.add(value.replace("{cost}", String.valueOf(n)).replace("{currency}", currencyMsg));
            lore.add("");
            for (String value : Language.getList(player, Messages.UPGRADES_TRAP_SLOT_ITEM_LORE2_PATH + this.name.replace("trap-slot-", ""))) lore.add(value.replace("{cost}", String.valueOf(n)).replace("{currency}", currencyMsg));
        } else {
            lore.addAll(Language.getList(player, enemyBaseEnterTrap.getLoreMsgPath()));
            lore.addAll(Language.getList(player, Messages.UPGRADES_TRAP_SLOT_ITEM_LORE1_PATH + this.name.replace("trap-slot-", "")));
        }
        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    
    @Override
    public void onClick(final Player player, final ClickType clickType, final ITeam team) {
    }
}
