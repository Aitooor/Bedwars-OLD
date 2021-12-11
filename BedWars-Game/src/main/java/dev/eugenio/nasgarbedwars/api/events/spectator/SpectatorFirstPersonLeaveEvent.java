package dev.eugenio.nasgarbedwars.api.events.spectator;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.function.Function;

public class SpectatorFirstPersonLeaveEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    @Getter
    private final Player spectator;
    @Getter
    private final IArena arena;
    private Function<Player, String> title;
    private Function<Player, String> subTitle;
    @Getter
    private int fadeIn;
    @Getter
    private int stay;
    @Getter
    private int fadeOut;

    public SpectatorFirstPersonLeaveEvent(final Player spectator, final IArena arena, final Function<Player, String> title, final Function<Player, String> subTitle) {
        this.fadeIn = 0;
        this.stay = 30;
        this.fadeOut = 0;
        this.spectator = spectator;
        this.arena = arena;
        this.title = title;
        this.subTitle = subTitle;
    }

    public static HandlerList getHandlerList() {
        return SpectatorFirstPersonLeaveEvent.HANDLERS;
    }

    public Function<Player, String> getSubTitle() {
        return this.subTitle;
    }

    public void setSubTitle(final Function<Player, String> subTitle) {
        this.subTitle = subTitle;
    }

    public Function<Player, String> getTitle() {
        return this.title;
    }

    public void setTitle(final Function<Player, String> title) {
        this.title = title;
    }

    public void setFadeIn(final int fadeIn) {
        this.fadeIn = fadeIn;
    }

    public void setFadeOut(final int fadeOut) {
        this.fadeOut = fadeOut;
    }

    public void setStay(final int stay) {
        this.stay = stay;
    }

    public HandlerList getHandlers() {
        return SpectatorFirstPersonLeaveEvent.HANDLERS;
    }
}
