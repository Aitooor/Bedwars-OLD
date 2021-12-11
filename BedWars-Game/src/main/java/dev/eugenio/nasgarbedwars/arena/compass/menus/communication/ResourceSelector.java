package dev.eugenio.nasgarbedwars.arena.compass.menus.communication;

import de.tr7zw.nbtapi.NBTItem;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.menu.Menu;
import dev.eugenio.nasgarbedwars.api.menu.PlayerMenuUtility;
import dev.eugenio.nasgarbedwars.arena.compass.data.ConfigData;
import dev.eugenio.nasgarbedwars.arena.compass.data.MessagesData;
import dev.eugenio.nasgarbedwars.arena.compass.menus.CommunicationsMenu;
import dev.eugenio.nasgarbedwars.arena.compass.util.MessagingUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;

public class ResourceSelector extends Menu {
    final YamlConfiguration yml;
    final String path;
    final ITeam team;
    final String[] resources = {"iron", "gold", "diamond", "emerald"};
    final HashMap<Integer, String> resourceMap = new HashMap<>();

    public ResourceSelector(PlayerMenuUtility playerMenuUtility, ITeam team, String path) {
        super(playerMenuUtility);
        this.team = team;
        this.path = path;
        this.yml = MessagesData.getYml(playerMenuUtility.getPlayer());
    }

    @Override
    public String getMenuName() {
        return yml.getString(MessagesData.COMMUNICATIONS_MENU_RESOURCES_TITLE);
    }

    @Override
    public int getSlots() {
        return BedWars.getInstance().getConfigData().getInt(ConfigData.COMMUNICATIONS_MENU_RESOURCES + ".size");
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        final NBTItem nbtItem = new NBTItem(e.getCurrentItem());
        final Player player = (Player) e.getWhoClicked();
        if (!BedWars.getInstance().getApi().getArenaUtil().isPlaying(player)) return;
        final IArena arena = BedWars.getInstance().getApi().getArenaUtil().getArenaByPlayer(player);
        if (arena.isSpectator(player)) return;
        switch (nbtItem.getString("data")) {
            case "back-item":
                new CommunicationsMenu(playerMenuUtility, team.getArena()).open();
                break;
            case "resource-item":
                MessagingUtil.resourceMessage(playerMenuUtility.getPlayer(), team, nbtItem.getString("path"), resourceMap.get(e.getSlot()));
                break;
        }
    }

    @Override
    public void setMenuItems() {
        final Player player = playerMenuUtility.getPlayer();
        for (String s : resources) {
            NBTItem nbtItem = new NBTItem(BedWars.getInstance().getConfigData().getResourceItem(playerMenuUtility.getPlayer(), s, path));
            inventory.setItem(nbtItem.getInteger("slot"), nbtItem.getItem());
            resourceMap.put(nbtItem.getInteger("slot"), s);
        }
        final NBTItem nbtItem = new NBTItem(BedWars.getInstance().getConfigData().getItem(player, ConfigData.COMMUNICATIONS_MENU_RESOURCES+".back-item", true, "back-item"));
        inventory.setItem(nbtItem.getInteger("slot"), nbtItem.getItem());
    }

}
