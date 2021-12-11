package dev.eugenio.nasgarbedwars.arena.compass.menus;

import de.tr7zw.nbtapi.NBTItem;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.arena.compass.menus.communication.ResourceSelector;
import dev.eugenio.nasgarbedwars.arena.compass.menus.communication.TeamSelector;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.arena.compass.data.ConfigData;
import dev.eugenio.nasgarbedwars.arena.compass.data.MessagesData;
import dev.eugenio.nasgarbedwars.api.menu.Menu;
import dev.eugenio.nasgarbedwars.api.menu.PlayerMenuUtility;
import dev.eugenio.nasgarbedwars.arena.compass.util.MessagingUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CommunicationsMenu extends Menu {
    final YamlConfiguration yml;
    final IArena arena;

    public CommunicationsMenu(PlayerMenuUtility playerMenuUtility, IArena arena) {
        super(playerMenuUtility);
        this.yml = MessagesData.getYml(playerMenuUtility.getPlayer());
        this.arena = arena;
    }

    @Override
    public String getMenuName() {
        return yml.getString(MessagesData.COMMUNICATIONS_MENU_TITLE);
    }

    @Override
    public int getSlots() {
        return BedWars.getInstance().getConfigData().getInt(ConfigData.COMMUNICATIONS_MENU_SIZE);
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        final Player player = (Player) e.getWhoClicked();
        final NBTItem nbti = new NBTItem(e.getCurrentItem());
        if (arena.isSpectator(player)) return;
        final ITeam team = arena.getTeam(player);
        if (team.getMembers().size() <= 1) return;
        if (nbti.getString("data").equals("back-item")) {
            new MainMenu(playerMenuUtility).open();
        } else if (nbti.getString("data").equals("communication-item")) {
            switch (nbti.getString("menuType")) {
                case "NONE":
                    MessagingUtil.simpleMessage(player, team, nbti.getString("path"));
                    break;
                case "TEAM":
                    new TeamSelector(playerMenuUtility, team, nbti.getString("path")).open();
                    break;
                case "RESOURCE":
                    new ResourceSelector(playerMenuUtility, team, nbti.getString("path")).open();
                    break;
            }
        }
    }

    @Override
    public void setMenuItems() {
        Player player = playerMenuUtility.getPlayer();
        for (String s : BedWars.getInstance().getConfigData().getYml().getConfigurationSection(ConfigData.COMMUNICATIONS_MENU_ITEMS).getKeys(false)) {
            NBTItem nbtItem = new NBTItem(BedWars.getInstance().getConfigData().getCommunicationItem(player, ConfigData.COMMUNICATIONS_MENU_ITEMS+"."+s));
            inventory.setItem(nbtItem.getInteger("slot"), nbtItem.getItem());
        }
        NBTItem backItem = new NBTItem(BedWars.getInstance().getConfigData().getItem(player, ConfigData.COMMUNICATIONS_MENU_BACK, true, "back-item"));
        inventory.setItem(backItem.getInteger("slot"), backItem.getItem());
    }

}
