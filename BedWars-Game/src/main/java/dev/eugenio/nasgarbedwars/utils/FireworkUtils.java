package dev.eugenio.nasgarbedwars.utils;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Objects;
import java.util.Random;

public class FireworkUtils {
    public static void spawnFireworks(Location location) {
        final Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        final FireworkMeta fwm = fw.getFireworkMeta();

        final Random random = new Random();

        fwm.setPower(2);
        final FireworkEffect effect = FireworkEffect.builder().withColor(Objects.requireNonNull(getColorRandomness(random.nextInt(17) + 1))).build();
        fwm.addEffect(effect);

        fw.setFireworkMeta(fwm);
        fw.detonate();
    }

    private static Color getColorRandomness(int i) {
        switch (i) {
            case 1:
                return Color.FUCHSIA;
            case 2:
                return Color.BLUE;
            case 3:
                return Color.BLACK;
            case 4:
                return Color.AQUA;
            case 5:
                return Color.GRAY;
            case 6:
                return Color.LIME;
            case 7:
                return Color.GREEN;
            case 8:
                return Color.WHITE;
            case 9:
                return Color.SILVER;
            case 10:
                return Color.ORANGE;
            case 11:
                return Color.OLIVE;
            case 12:
                return Color.PURPLE;
            case 13:
                return Color.RED;
            case 14:
                return Color.NAVY;
            case 15:
                return Color.YELLOW;
            case 16:
                return Color.MAROON;
            case 17:
                return Color.TEAL;
        }
        return Color.WHITE;
    }
}