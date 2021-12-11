package dev.eugenio.nasgarbedwars.listeners.split;

import java.util.List;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerGeneratorCollectEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class MaterialSplit implements Listener {
    @EventHandler
    public void onGoldPickup(PlayerGeneratorCollectEvent e) {
        if (!e.isCancelled() && (e.getItemStack().getType() == Material.GOLD_INGOT || e.getItemStack().getType() == Material.IRON_INGOT)) {
            Location pl = e.getPlayer().getLocation();
            Player p = e.getPlayer();
            List<Entity> nearbyEntities2 = (List<Entity>)pl.getWorld().getNearbyEntities(pl, 3.0D, 3.0D, 3.0D);
            for (Entity en : pl.getWorld().getEntities()) {
                if (nearbyEntities2.contains(en) && en instanceof Player) {
                    Player r = (Player)en;
                    if (r.getUniqueId() != p.getUniqueId()) {
                        ITeam pt = BedWars.getInstance().getApi().getArenaUtil().getArenaByPlayer(r).getTeam(p);
                        ITeam rt = BedWars.getInstance().getApi().getArenaUtil().getArenaByPlayer(r).getTeam(r);
                        if (pt == rt) {
                            ItemStack gold = new ItemStack(e.getItemStack().getType());
                            gold.setAmount(e.getItemStack().getAmount());
                            r.getInventory().addItem(gold);
                            if (Bukkit.getServer().getClass().getPackage().getName().contains("v1_8")) {
                                r.playSound(r.getLocation(), Sound.valueOf("ITEM_PICKUP"), 0.8F, 1.0F);
                                continue;
                            }
                            r.playSound(r.getLocation(), Sound.valueOf("ENTITY_ITEM_PICKUP"), 0.8F, 1.0F);
                        }
                    }
                }
            }
        }
    }
}
