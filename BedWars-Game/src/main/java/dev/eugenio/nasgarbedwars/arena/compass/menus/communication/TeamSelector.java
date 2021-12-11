package dev.eugenio.nasgarbedwars.arena.compass.menus.communication;

import de.tr7zw.nbtapi.NBTItem;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.menu.Menu;
import dev.eugenio.nasgarbedwars.api.menu.PlayerMenuUtility;
import dev.eugenio.nasgarbedwars.arena.compass.data.ConfigData;
import dev.eugenio.nasgarbedwars.arena.compass.data.MessagesData;
import dev.eugenio.nasgarbedwars.arena.compass.menus.CommunicationsMenu;
import dev.eugenio.nasgarbedwars.arena.compass.util.MessagingUtil;
import dev.eugenio.nasgarbedwars.arena.compass.util.TextUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeamSelector extends Menu {
    final Language lang;
    final YamlConfiguration yml;
    final List<Integer> slots;
    final IArena arena;
    final ITeam team;
    final String path;
    final HashMap<Integer, ITeam> teamMap = new HashMap<>();

    public TeamSelector(PlayerMenuUtility playerMenuUtility, ITeam team, String path) {
        super(playerMenuUtility);
        this.lang = MessagesData.getLang(playerMenuUtility.getPlayer());
        this.yml = lang.getYml();
        this.slots = new ArrayList<>();
        this.team = team;
        this.arena = team.getArena();
        this.path = path;
        for (String s : BedWars.getInstance().getConfigData().getYml().getString(ConfigData.COMMUNICATIONS_MENU_TEAMS+".slots").split(",")) {
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
        return yml.getString(MessagesData.COMMUNICATIONS_MENU_TEAMS_TITLE);
    }

    @Override
    public int getSlots() {
        return BedWars.getInstance().getConfigData().getInt(ConfigData.COMMUNICATIONS_MENU_TEAMS+".size");
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        final NBTItem nbti = new NBTItem(e.getCurrentItem());
        final Player player = (Player) e.getWhoClicked();
        if (!BedWars.getInstance().getApi().getArenaUtil().isPlaying(player)) return;
        final IArena a = BedWars.getInstance().getApi().getArenaUtil().getArenaByPlayer(player);
        if (a.isSpectator(player)) return;
        if (team.getMembers().size() <= 1) return;
        switch (nbti.getString("data")) {
            case "back-item":
                new CommunicationsMenu(playerMenuUtility, arena).open();
                break;
            case "team-item":
                MessagingUtil.teamMessage(player, team, nbti.getString("path"), teamMap.get(e.getSlot()));
                break;
        }
    }

    @Override
    public void setMenuItems() {
        Player player = playerMenuUtility.getPlayer();
        int index = 0;
        for (ITeam t : arena.getTeams()) {
            if (t.getMembers().isEmpty()) continue;
            if (t.equals(team)) continue;
            if (slots.size() <= index) continue;
            inventory.setItem(slots.get(index), getTeamItem(BedWars.getInstance().getConfigData().getItem(player, ConfigData.TRACKER_MENU_TEAM_ITEM, false, null), t));
            teamMap.put(slots.get(index), t);
            index++;
        }
        NBTItem nbtBack = new NBTItem(BedWars.getInstance().getConfigData().getItem(player, ConfigData.COMMUNICATIONS_MENU_TEAMS+".back-item", true, "back-item"));
        inventory.setItem(nbtBack.getInteger("slot"), nbtBack.getItem());
    }

    private ItemStack getTeamItem(ItemStack itemStack, ITeam team) {
        String displayName = yml.getString(path).replace("{team}", team.getColor().chat() + "§l" + team.getDisplayName(lang));
        itemStack = BedWars.getInstance().getApi().getVersionSupport().colourItem(itemStack, team);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(TextUtil.colorize(displayName));
        List<String> newLore = new ArrayList<>();
        yml.getStringList(MessagesData.COMMUNICATIONS_MENU_LORE).forEach(s -> newLore.add(TextUtil.colorize(s.replace("{message}", displayName))));
        itemMeta.setLore(newLore);
        itemStack.setItemMeta(itemMeta);
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setString("path", path);
        nbtItem.setString("data", "team-item");
        return nbtItem.getItem();
    }

}
