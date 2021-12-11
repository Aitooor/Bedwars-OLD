package dev.eugenio.nasgarbedwars.api.events.spectator;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class SpectatorFirstPersonEnterEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS;
    private static List<UUID> spectatingInFirstPerson;

    static {
        HANDLERS = new HandlerList();
        SpectatorFirstPersonEnterEvent.spectatingInFirstPerson = new ArrayList<UUID>();
    }

    @Getter
    private final Player spectator;
    @Getter
    private final Player target;
    @Getter
    private final IArena arena;
    private boolean cancelled;
    private Function<Player, String> title;
    private Function<Player, String> subTitle;
    @Getter
    private int fadeIn;
    @Getter
    private int stay;
    @Getter
    private int fadeOut;

    public SpectatorFirstPersonEnterEvent(final Player spectator, final Player target, final IArena arena, final Function<Player, String> title, final Function<Player, String> subTitle) {
        this.cancelled = false;
        this.fadeIn = 0;
        this.stay = 30;
        this.fadeOut = 0;
        this.spectator = spectator;
        this.target = target;
        this.arena = arena;
        this.title = title;
        this.subTitle = subTitle;
        if (!SpectatorFirstPersonEnterEvent.spectatingInFirstPerson.contains(spectator.getUniqueId())) {
            SpectatorFirstPersonEnterEvent.spectatingInFirstPerson.add(spectator.getUniqueId());
        }
    }

    public static HandlerList getHandlerList() {
        return SpectatorFirstPersonEnterEvent.HANDLERS;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
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

    public void setStay(final int stay) {
        if (stay < 0) {
            return;
        }
        this.stay = stay;
    }

    public void setFadeOut(final int fadeOut) {
        if (fadeOut < 0) {
            return;
        }
        this.fadeOut = fadeOut;
    }

    public void setFadeIn(final int fadeIn) {
        if (fadeIn < 0) {
            return;
        }
        this.fadeIn = fadeIn;
    }

    public HandlerList getHandlers() {
        return SpectatorFirstPersonEnterEvent.HANDLERS;
    }
}
