package dev.eugenio.nasgarbedwars.listeners;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerInvisibilityDrinkEvent;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.sidebar.BedWarsScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InvisibilityPotionListener implements Listener {
    @EventHandler
    public void onPotion(final PlayerInvisibilityDrinkEvent event) {
        if (event.getTeam() == null) return;
        if (event.getType() == PlayerInvisibilityDrinkEvent.Type.ADDED) {
            for (final BedWarsScoreboard bedWarsScoreboard : BedWarsScoreboard.getScoreboards().values()) {
                if (bedWarsScoreboard.getArena() == null) continue;
                if (!bedWarsScoreboard.getArena().equals(event.getArena())) continue;
                bedWarsScoreboard.invisibilityPotion(event.getTeam(), event.getPlayer(), true);
            }
        } else {
            for (final BedWarsScoreboard bedWarsScoreboard2 : BedWarsScoreboard.getScoreboards().values()) {
                if (bedWarsScoreboard2.getArena() == null) continue;
                if (!bedWarsScoreboard2.getArena().equals(event.getArena())) continue;
                bedWarsScoreboard2.invisibilityPotion(event.getTeam(), event.getPlayer(), false);
            }
        }
    }

    @EventHandler
    public void onDrink(PlayerItemConsumeEvent event) {
        IArena iArena = Arena.getArenaByPlayer(event.getPlayer());
        if (iArena == null) return;
        if (event.getItem().getType() != Material.POTION) return;
        Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> BedWars.getInstance().getNms().minusAmount(event.getPlayer(), new ItemStack(Material.GLASS_BOTTLE), 1), 5L);
        PotionMeta potionMeta = (PotionMeta) event.getItem().getItemMeta();
        if (potionMeta.hasCustomEffects() && potionMeta.hasCustomEffect(PotionEffectType.INVISIBILITY))
            Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
                for (PotionEffect potionEffect : event.getPlayer().getActivePotionEffects()) {
                    if (potionEffect.getType().toString().contains("INVISIBILITY")) {
                        if (iArena.getShowTime().containsKey(event.getPlayer())) {
                            ITeam iTeam1 = iArena.getTeam(event.getPlayer());
                            iArena.getShowTime().replace(event.getPlayer(), potionEffect.getDuration() / 20);
                            Bukkit.getPluginManager().callEvent(new PlayerInvisibilityDrinkEvent(PlayerInvisibilityDrinkEvent.Type.ADDED, iTeam1, event.getPlayer(), iTeam1.getArena()));
                            break;
                        }
                        ITeam iTeam = iArena.getTeam(event.getPlayer());
                        iArena.getShowTime().put(event.getPlayer(), potionEffect.getDuration() / 20);
                        for (Player player : event.getPlayer().getWorld().getPlayers()) {
                            if (iArena.isSpectator(player)) {
                                BedWars.getInstance().getNms().hideArmor(event.getPlayer(), player);
                                continue;
                            }
                            if (iTeam != iArena.getTeam(player)) BedWars.getInstance().getNms().hideArmor(event.getPlayer(), player);
                        }
                        Bukkit.getPluginManager().callEvent(new PlayerInvisibilityDrinkEvent(PlayerInvisibilityDrinkEvent.Type.ADDED, iTeam, event.getPlayer(), iTeam.getArena()));
                        break;
                    }
                }
            }, 5L);
    }
}
