package dev.eugenio.nasgarbedwars.upgrades.menu;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.api.upgrades.MenuContent;
import dev.eugenio.nasgarbedwars.api.upgrades.UpgradesIndex;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.upgrades.UpgradesManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class InternalMenu implements UpgradesIndex {
    @Getter private final String name;
    private final HashMap<Integer, MenuContent> menuContentBySlot;
    
    public InternalMenu(final String s) {
        this.menuContentBySlot = new HashMap<>();
        this.name = s.toLowerCase();
        Language.saveIfNotExists(Messages.UPGRADES_MENU_GUI_NAME_PATH + s.toLowerCase(), "&8Mejoras y trampas");
    }
    
    @Override
    public void open(final Player player) {
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) return;
        if (!arenaByPlayer.isPlayer(player)) return;
        final ITeam team = arenaByPlayer.getTeam(player);
        if (team == null) return;
        if (!BedWars.getInstance().getApi().getArenaUtil().isPlaying(player)) return;
        final Inventory inventory = Bukkit.createInventory(null, 45, Language.getMsg(player, Messages.UPGRADES_MENU_GUI_NAME_PATH + this.name));
        for (final Map.Entry<Integer, MenuContent> entry : this.menuContentBySlot.entrySet()) inventory.setItem(entry.getKey(), entry.getValue().getDisplayItem(player, team));
        player.openInventory(inventory);
        UpgradesManager.setWatchingUpgrades(player.getUniqueId());
    }
    
    @Override
    public boolean addContent(final MenuContent menuContent, final int n) {
        if (this.menuContentBySlot.get(n) != null) return false;
        this.menuContentBySlot.put(n, menuContent);
        return true;
    }
}
