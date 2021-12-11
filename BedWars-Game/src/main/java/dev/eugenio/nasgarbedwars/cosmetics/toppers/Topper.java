package dev.eugenio.nasgarbedwars.cosmetics.toppers;

import lombok.Getter;
import org.bukkit.Sound;

@Getter
public class Topper {

    private final String topperName;
    private final String topperSchem;

    public Topper(String topperName, String topperSchem) {
        this.topperName = topperName;
        this.topperSchem = topperSchem;
    }

}
