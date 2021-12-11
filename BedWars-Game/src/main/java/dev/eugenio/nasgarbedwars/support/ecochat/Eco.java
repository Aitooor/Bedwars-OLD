package dev.eugenio.nasgarbedwars.support.ecochat;

import org.bukkit.entity.Player;

public class Eco implements Economy {
    @Override
    public boolean isEconomy() {
        return false;
    }
    
    @Override
    public double getMoney(final Player player) {
        return 0.0;
    }
    
    @Override
    public void buyAction(final Player player, final double n) {
        player.sendMessage("intentaste comprar pero esto no está hecho todavia xd");
    }
}
