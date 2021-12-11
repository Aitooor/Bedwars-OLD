package dev.eugenio.nasgarbedwars.shop.listeners;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.arena.Misc;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SpecialsListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onSpecialInteract(final PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        final Player player = event.getPlayer();
        final ItemStack item = event.getItem();
        if (item == null) return;
        if (item.getType() == Material.AIR) return;
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) return;
        if (arenaByPlayer.getRespawnSessions().containsKey(event.getPlayer())) return;
        if (!arenaByPlayer.isPlayer(player)) return;
        if (BedWars.getInstance().getShopManager().getYml().getBoolean("shop-specials.silverfish.enable") && !Misc.isProjectile(Material.valueOf(BedWars.getInstance().getShopManager().getYml().getString("shop-specials.silverfish.material"))) && item.getType() == Material.valueOf(BedWars.getInstance().getShopManager().getYml().getString("shop-specials.silverfish.material")) && BedWars.getInstance().getNms().itemStackDataCompare(item, (short) BedWars.getInstance().getShopManager().getYml().getInt("shop-specials.silverfish.data"))) {
            event.setCancelled(true);
            BedWars.getInstance().getNms().spawnSilverfish(player.getLocation().add(0.0, 1.0, 0.0), arenaByPlayer.getTeam(player), BedWars.getInstance().getShopManager().getYml().getDouble("shop-specials.silverfish.speed"), BedWars.getInstance().getShopManager().getYml().getDouble("shop-specials.silverfish.health"), BedWars.getInstance().getShopManager().getInt("shop-specials.silverfish.despawn"), BedWars.getInstance().getShopManager().getYml().getDouble("shop-specials.silverfish.damage"));
            if (!BedWars.getInstance().getNms().isProjectile(item)) {
                BedWars.getInstance().getNms().minusAmount(player, item, 1);
                player.updateInventory();
            }
        }
        if (BedWars.getInstance().getShopManager().getYml().getBoolean("shop-specials.iron-golem.enable") && !Misc.isProjectile(Material.valueOf(BedWars.getInstance().getShopManager().getYml().getString("shop-specials.iron-golem.material"))) && item.getType() == Material.valueOf(BedWars.getInstance().getShopManager().getYml().getString("shop-specials.iron-golem.material")) && BedWars.getInstance().getNms().itemStackDataCompare(item, (short) BedWars.getInstance().getShopManager().getYml().getInt("shop-specials.iron-golem.data"))) {
            event.setCancelled(true);
            BedWars.getInstance().getNms().spawnIronGolem(player.getLocation().add(0.0, 1.0, 0.0), arenaByPlayer.getTeam(player), BedWars.getInstance().getShopManager().getYml().getDouble("shop-specials.iron-golem.speed"), BedWars.getInstance().getShopManager().getYml().getDouble("shop-specials.iron-golem.health"), BedWars.getInstance().getShopManager().getInt("shop-specials.iron-golem.despawn"));
            if (!BedWars.getInstance().getNms().isProjectile(item)) {
                BedWars.getInstance().getNms().minusAmount(player, item, 1);
                player.updateInventory();
            }
        }
    }
}
