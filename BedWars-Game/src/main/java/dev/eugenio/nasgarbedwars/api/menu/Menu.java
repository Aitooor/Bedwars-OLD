package dev.eugenio.nasgarbedwars.api.menu;

import dev.eugenio.nasgarbedwars.arena.compass.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class Menu implements InventoryHolder {
    protected PlayerMenuUtility playerMenuUtility;
    protected Inventory inventory;

    public Menu(PlayerMenuUtility playerMenuUtility) {
        this.playerMenuUtility = playerMenuUtility;
    }

    public abstract String getMenuName();
    public abstract int getSlots();
    public abstract void handleMenu(InventoryClickEvent e);
    public abstract void setMenuItems();

    public void open() {
        inventory = Bukkit.createInventory(this, getSlots(), TextUtil.colorize(getMenuName()));
        this.setMenuItems();
        playerMenuUtility.getPlayer().openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
