package dev.eugenio.nasgarbedwars.arena.compass.menus;

import de.tr7zw.nbtapi.NBTItem;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.arena.compass.tasks.ActionBarTask;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.arena.compass.data.ConfigData;
import dev.eugenio.nasgarbedwars.arena.compass.data.MessagesData;
import dev.eugenio.nasgarbedwars.api.menu.Menu;
import dev.eugenio.nasgarbedwars.api.menu.PlayerMenuUtility;
import dev.eugenio.nasgarbedwars.arena.compass.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TrackerMenu extends Menu {
    final YamlConfiguration yml;
    final IArena arena;
    final List<Integer> slots = new ArrayList<>();
    final HashMap<Integer, ITeam> teamSlotMap;

    public TrackerMenu(PlayerMenuUtility playerMenuUtility, IArena arena) {
        super(playerMenuUtility);
        this.arena = arena;
        this.teamSlotMap = new HashMap<>();
        this.yml = MessagesData.getYml(playerMenuUtility.getPlayer());
        for (String s : BedWars.getInstance().getConfigData().getYml().getString(ConfigData.TRACKER_MENU_SLOTS).split(",")) {
            int i;
            try {
                i = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                continue;
            }
            slots.add(i);
        }
    }

    @Override
    public String getMenuName() {
        return yml.getString(MessagesData.TRACKER_MENU_TITLE);
    }

    @Override
    public int getSlots() {
        return BedWars.getInstance().getConfigData().getInt(ConfigData.TRACKER_MENU_SIZE);
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        final Player player = (Player) e.getWhoClicked();
        final UUID uuid = player.getUniqueId();
        final NBTItem nbtItem = new NBTItem(e.getCurrentItem());

        if (nbtItem.getString("data").equals("main-menu")) new MainMenu(playerMenuUtility).open();
        if (!slots.contains(e.getSlot())) return;

        if (!isAllBedsDestroyed(arena.getTeam(player))) {
            player.closeInventory();
            player.sendMessage(TextUtil.colorize(yml.getString(MessagesData.NOT_ALL_BEDS_DESTROYED)));
            return;
        }

        if (!player.getInventory().contains(Material.valueOf(BedWars.getInstance().getConfigData().getString(ConfigData.PLAYER_TRACK_RESOURCE)), BedWars.getInstance().getConfigData().getInt(ConfigData.PLAYER_TRACK_COST))) {
            player.closeInventory();
            player.sendMessage(TextUtil.colorize(yml.getString(MessagesData.NOT_ENOUGH_RESOURCE)));
            return;
        }

        player.closeInventory();

        if (BedWars.getInstance().isTracking(arena, uuid)) {
            if (BedWars.getInstance().getTrackingTeam(arena, uuid).equals(teamSlotMap.get(e.getSlot()))) {
                player.sendMessage(TextUtil.colorize(yml.getString(MessagesData.ALREADY_TRACKING)));
                return;
            }
        }

        if (!BedWars.getInstance().getTrackingArenaMap().containsKey(arena))
            new ActionBarTask(arena).runTaskTimer(BedWars.getInstance(), 0, 1);

        BedWars.getInstance().setTrackingTeam(arena, uuid, teamSlotMap.get(e.getSlot()));
        BedWars.getInstance().getApi().getShopUtil().takeMoney(player, Material.valueOf(BedWars.getInstance().getConfigData().getString(ConfigData.PLAYER_TRACK_RESOURCE)), BedWars.getInstance().getConfigData().getInt(ConfigData.PLAYER_TRACK_COST));
        player.sendMessage(TextUtil.colorize(yml.getString(MessagesData.PURCHASED)));

    }

    @Override
    public void setMenuItems() {
        final Player player = playerMenuUtility.getPlayer();
        final NBTItem nbtItem = new NBTItem(BedWars.getInstance().getConfigData().getItem(player, ConfigData.TRACKER_MENU_BACK_ITEM, true, "main-menu"));
        int index = 0;
        for (ITeam team : arena.getTeams()) {
            if (team.getMembers().isEmpty()) continue;
            if (arena.getTeam(playerMenuUtility.getPlayer()).equals(team)) continue;
            if (slots.size() <= index) continue;
            NBTItem teamItem = new NBTItem(BedWars.getInstance().getConfigData().getItem(player, ConfigData.TRACKER_MENU_TEAM_ITEM, false, null));
            teamSlotMap.put(slots.get(index), team);
            inventory.setItem(slots.get(index), getTeamItem(teamItem.getItem(), team, playerMenuUtility.getPlayer()));
            index++;
        }
        inventory.setItem(nbtItem.getInteger("slot"), nbtItem.getItem());
    }

    public ItemStack getTeamItem(ItemStack itemStack, ITeam team, Player player) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemStack = BedWars.getInstance().getApi().getVersionSupport().colourItem(itemStack, team);
        itemMeta.setDisplayName(TextUtil.colorize(itemMeta.getDisplayName().replace("{team}", team.getDisplayName(BedWars.getInstance().getApi().getPlayerLanguage(player)))));
        final List<String> newLore = new ArrayList<>();
        itemMeta.getLore().forEach(s -> newLore.add(TextUtil.colorize(s.replace("{status}", getStatus(player, arena)))));
        itemMeta.setLore(newLore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public String getStatus(Player player, IArena arena) {
        if (!isAllBedsDestroyed(arena.getTeam(player))) {
            return yml.getString(MessagesData.STATUS_LOCKED);
        } else if (!player.getInventory().contains(Material.valueOf(BedWars.getInstance().getConfigData().getString(ConfigData.PLAYER_TRACK_RESOURCE)), BedWars.getInstance().getConfigData().getInt(ConfigData.PLAYER_TRACK_COST))) {
            return yml.getString(MessagesData.STATUS_NOT_ENOUGH);
        } else {
            return yml.getString(MessagesData.STATUS_UNLOCKED);
        }
    }

    public boolean isAllBedsDestroyed(ITeam t) {
        boolean bool = true;
        for (ITeam team : arena.getTeams()) {
            if (team.equals(t)) continue;
            if (team.isBedDestroyed()) continue;
            bool = false;
            break;
        }
        return bool;
    }
}

