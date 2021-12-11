package dev.eugenio.nasgarbedwars.api.events.player;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLevelUpEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    @Getter
    private final Player player;
    @Getter
    private final int newXpTarget;
    @Getter
    private final int newLevel;

    public PlayerLevelUpEvent(final Player player, final int newLevel, final int newXpTarget) {
        this.player = player;
        this.newLevel = newLevel;
        this.newXpTarget = newXpTarget;
    }

    public static HandlerList getHandlerList() {
        return PlayerLevelUpEvent.HANDLERS;
    }

    public HandlerList getHandlers() {
        return PlayerLevelUpEvent.HANDLERS;
    }
}
