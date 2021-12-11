package dev.eugenio.nasgarbedwars.api.events.player;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerAFKEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    @Getter
    private final Player player;
    @Getter
    private final AFKType afkType;

    public PlayerAFKEvent(final Player player, final AFKType afkType) {
        this.afkType = afkType;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return PlayerAFKEvent.HANDLERS;
    }

    public HandlerList getHandlers() {
        return PlayerAFKEvent.HANDLERS;
    }

    public enum AFKType {
        START,
        END
    }
}
