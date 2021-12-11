package dev.eugenio.nasgarbedwars.api.events.shop;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ShopOpenEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    @Getter
    private final Player player;
    private boolean cancelled;

    public ShopOpenEvent(final Player player) {
        this.cancelled = false;
        this.player = player;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return ShopOpenEvent.HANDLERS;
    }
}
