package dev.eugenio.nasgarbedwars.api.events.player;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLangChangeEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    @Getter
    private final Player player;
    @Getter
    private final String oldLang;
    @Getter
    private final String newLang;
    private boolean cancelled;

    public PlayerLangChangeEvent(final Player player, final String oldLang, final String newLang) {
        this.cancelled = false;
        this.player = player;
        this.oldLang = oldLang;
        this.newLang = newLang;
    }

    public static HandlerList getHandlerList() {
        return PlayerLangChangeEvent.HANDLERS;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return PlayerLangChangeEvent.HANDLERS;
    }
}
