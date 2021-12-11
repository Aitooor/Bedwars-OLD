package dev.eugenio.nasgarbedwars.api.events.server;

import dev.eugenio.nasgarbedwars.api.server.ISetupSession;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SetupSessionStartEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    @Getter
    private final ISetupSession setupSession;

    public SetupSessionStartEvent(final ISetupSession setupSession) {
        this.setupSession = setupSession;
    }

    public static HandlerList getHandlerList() {
        return SetupSessionStartEvent.HANDLERS;
    }

    public HandlerList getHandlers() {
        return SetupSessionStartEvent.HANDLERS;
    }
}
