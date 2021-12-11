package dev.eugenio.nasgarbedwars.api.arena.team;

import org.bukkit.enchantments.Enchantment;

public interface TeamEnchantment {
    Enchantment getEnchantment();

    int getAmplifier();
}
