package dev.eugenio.nasgarbedwars.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class InvisibleEffect implements Listener {
    @EventHandler
    public void onInvisibleWalk(PlayerMoveEvent event) {
        if (!event.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY)) return;
        final Player player = event.getPlayer();
        final Random random = new Random();
        final int randomResult = random.nextInt(20 - 1 + 1) + 1;
        if (randomResult == 8) {
            Bukkit.getWorld(player.getWorld().getName()).playEffect(player.getLocation(), Effect.FOOTSTEP, 30);
            Bukkit.getWorld(player.getWorld().getName()).playEffect(player.getLocation(), Effect.FOOTSTEP, 30);
        }
    }

    @EventHandler
    public void onInvisibleSprint(PlayerMoveEvent event) {
        if (!event.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY)) return;
        final Player player = event.getPlayer();
        final Random random = new Random();
        final int randomResult = random.nextInt(10 - 1 + 1) + 1;
        if (randomResult == 8) {
            Bukkit.getWorld(player.getWorld().getName()).playEffect(player.getLocation(), Effect.FOOTSTEP, 40);
            Bukkit.getWorld(player.getWorld().getName()).playEffect(player.getLocation(), Effect.FOOTSTEP, 40);
        }
    }
}
