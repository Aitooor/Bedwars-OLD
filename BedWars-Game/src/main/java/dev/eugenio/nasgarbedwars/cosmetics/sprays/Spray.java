package dev.eugenio.nasgarbedwars.cosmetics.sprays;

import lombok.Getter;
import org.bukkit.Sound;

@Getter
public class Spray {

    private final String sprayName;
    private final String sprayFile;

    public Spray(String sprayName, String sprayFile) {
        this.sprayName = sprayName;
        this.sprayFile = sprayFile;
    }

}
