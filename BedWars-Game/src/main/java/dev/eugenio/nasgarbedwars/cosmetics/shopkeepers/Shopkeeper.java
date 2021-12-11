package dev.eugenio.nasgarbedwars.cosmetics.shopkeepers;

import lombok.Getter;

@Getter
public class Shopkeeper {

    private final String shopkeeperName;
    private final String shopkeeperEntity;
    private final String shopkeeperSkin;

    public Shopkeeper(String shopkeeperName, String shopkeeperEntity, String shopkeeperSkin) {
        this.shopkeeperName = shopkeeperName;
        this.shopkeeperEntity = shopkeeperEntity;
        this.shopkeeperSkin = shopkeeperSkin;
    }

}
