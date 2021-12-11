package dev.eugenio.nasgarbedwars.arena.compass.menus;

import de.tr7zw.nbtapi.NBTItem;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.GameStatus;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.arena.compass.data.ConfigData;
import dev.eugenio.nasgarbedwars.arena.compass.data.MessagesData;
import dev.eugenio.nasgarbedwars.api.menu.Menu;
import dev.eugenio.nasgarbedwars.api.menu.PlayerMenuUtility;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MainMenu extends Menu {
    final YamlConfiguration yml;

    public MainMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        this.yml = MessagesData.getYml(playerMenuUtility.getPlayer());
    }

    @Override
    public String getMenuName() {
        return yml.getString(MessagesData.MAIN_MENU_TITLE);
    }

    @Override
    public int getSlots() {
        return BedWars.getInstance().getConfigData().getInt(ConfigData.MAIN_MENU_SIZE);
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        final Player player = (Player) e.getWhoClicked();
        final NBTItem nbtItem = new NBTItem(e.getCurrentItem());
        if (!BedWars.getInstance().getApi().getArenaUtil().isPlaying(player)) return;
        final IArena arena = BedWars.getInstance().getApi().getArenaUtil().getArenaByPlayer(player);
        if (!(arena.getStatus().equals(GameStatus.playing) && !arena.isSpectator(player))) return;
        switch (nbtItem.getString("data")) {
            case "tracker-menu":
                new TrackerMenu(playerMenuUtility, arena).open();
                break;
            case "communications-menu":
                new CommunicationsMenu(playerMenuUtility, arena).open();
                break;
        }
    }

    @Override
    public void setMenuItems() {
        final Player player = playerMenuUtility.getPlayer();
        final IArena arena = BedWars.getInstance().getApi().getArenaUtil().getArenaByPlayer(player);
        final ITeam team = arena.getTeam(player);

        if (team.getMembers().size() > 1 && BedWars.getInstance().getConfigData().getBoolean(ConfigData.USE_COMMUNICATIONS)) {
            final NBTItem nbtTracker = new NBTItem(BedWars.getInstance().getConfigData().getItem(player, ConfigData.MAIN_MENU_TRACKER_TEAM, true, "tracker-menu"));
            final NBTItem nbtComm = new NBTItem(BedWars.getInstance().getConfigData().getItem(player, ConfigData.MAIN_MENU_COMMUNICATIONS, true, "communications-menu"));
            inventory.setItem(nbtTracker.getInteger("slot"), nbtTracker.getItem());
            inventory.setItem(nbtComm.getInteger("slot"), nbtComm.getItem());
        } else {
            final NBTItem nbtItem = new NBTItem(BedWars.getInstance().getConfigData().getItem(player, ConfigData.MAIN_MENU_TRACKER, true, "tracker-menu"));
            inventory.setItem(nbtItem.getInteger("slot"), nbtItem.getItem());
        }
    }
}
