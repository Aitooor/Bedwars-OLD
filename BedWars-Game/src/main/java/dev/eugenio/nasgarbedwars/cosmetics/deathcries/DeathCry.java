package dev.eugenio.nasgarbedwars.cosmetics.deathcries;

import lombok.Getter;
import org.bukkit.Sound;

@Getter
public class DeathCry {

    private final String name;
    private final Sound sound;
    private final Float pitch;

    public DeathCry(String name, Sound sound, Float pitch) {
        this.name = name;
        this.sound = sound;
        this.pitch = pitch;
    }

}
