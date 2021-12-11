package dev.eugenio.nasgarbedwars.api.events.shop;

import dev.eugenio.nasgarbedwars.api.arena.shop.ICategoryContent;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ShopBuyEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    @Getter
    private final Player buyer;
    @Getter
    private final ICategoryContent categoryContent;

    public ShopBuyEvent(final Player buyer, final ICategoryContent categoryContent) {
        this.categoryContent = categoryContent;
        this.buyer = buyer;
    }

    public static HandlerList getHandlerList() {
        return ShopBuyEvent.HANDLERS;
    }

    public HandlerList getHandlers() {
        return ShopBuyEvent.HANDLERS;
    }
}
