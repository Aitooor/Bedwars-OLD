package dev.eugenio.nasgarbedwars.api.events.player;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerXpGainEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    @Getter
    private final Player player;
    @Getter
    private final int amount;
    @Getter
    private final XpSource xpSource;

    public PlayerXpGainEvent(final Player player, final int amount, final XpSource xpSource) {
        this.player = player;
        this.amount = amount;
        this.xpSource = xpSource;
    }

    public static HandlerList getHandlerList() {
        return PlayerXpGainEvent.HANDLERS;
    }

    public HandlerList getHandlers() {
        return PlayerXpGainEvent.HANDLERS;
    }

    public enum XpSource {
        PER_MINUTE,
        PER_TEAMMATE,
        GAME_WIN,
        OTHER
    }
}
