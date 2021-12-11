package dev.eugenio.nasgarbedwars.api.events.gameplay;

import dev.eugenio.nasgarbedwars.api.arena.generator.IGenerator;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GeneratorUpgradeEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    @Getter
    private final IGenerator generator;

    public GeneratorUpgradeEvent(final IGenerator generator) {
        this.generator = generator;
    }

    public static HandlerList getHandlerList() {
        return GeneratorUpgradeEvent.HANDLERS;
    }

    public HandlerList getHandlers() {
        return GeneratorUpgradeEvent.HANDLERS;
    }
}
