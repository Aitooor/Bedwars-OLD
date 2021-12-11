package dev.eugenio.nasgarbedwars.cosmetics.woodskins;

import lombok.Getter;

@Getter
public class WoodSkin {

    private final String name;
    private final int data;

    public WoodSkin(String name, int data) {
        this.name = name;
        this.data = data;
    }

}
