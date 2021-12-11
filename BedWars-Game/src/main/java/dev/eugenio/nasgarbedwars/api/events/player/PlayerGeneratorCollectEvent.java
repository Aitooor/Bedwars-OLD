package dev.eugenio.nasgarbedwars.api.events.player;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import lombok.Getter;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PlayerGeneratorCollectEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    @Getter
    private final Player player;
    @Getter
    private final Item item;
    @Getter
    private final IArena arena;
    private boolean cancelled;

    public PlayerGeneratorCollectEvent(final Player player, final Item item, final IArena arena) {
        this.cancelled = false;
        this.player = player;
        this.item = item;
        this.arena = arena;
    }

    public static HandlerList getHandlerList() {
        return PlayerGeneratorCollectEvent.HANDLERS;
    }

    public ItemStack getItemStack() {
        return this.item.getItemStack();
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return PlayerGeneratorCollectEvent.HANDLERS;
    }
}
