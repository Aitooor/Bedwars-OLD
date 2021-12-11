package dev.eugenio.nasgarbedwars.api.events.player;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.function.Function;

public class PlayerKillEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    @Getter
    private final IArena arena;
    @Getter
    private final Player victim;
    @Getter
    private final Player killer;
    @Getter
    private final PlayerKillCause cause;
    @Getter
    private Function<Player, String> message;

    public PlayerKillEvent(final IArena arena, final Player victim, final Player killer, final Function<Player, String> message, final PlayerKillCause cause) {
        this.arena = arena;
        this.victim = victim;
        this.killer = killer;
        this.message = message;
        this.cause = cause;
    }

    public static HandlerList getHandlerList() {
        return PlayerKillEvent.HANDLERS;
    }

    public void setMessage(final Function<Player, String> message) {
        this.message = message;
    }

    public HandlerList getHandlers() {
        return PlayerKillEvent.HANDLERS;
    }

    public enum PlayerKillCause {
        UNKNOWN(false, false, false),
        UNKNOWN_FINAL_KILL(true, false, false),
        EXPLOSION(false, false, false),
        EXPLOSION_FINAL_KILL(true, false, false),
        VOID(false, false, false),
        VOID_FINAL_KILL(true, false, false),
        PVP(false, false, false),
        PVP_FINAL_KILL(true, false, false),
        PLAYER_SHOOT(false, false, false),
        PLAYER_SHOOT_FINAL_KILL(true, false, false),
        SILVERFISH(false, true, false),
        SILVERFISH_FINAL_KILL(true, true, false),
        IRON_GOLEM(false, true, false),
        IRON_GOLEM_FINAL_KILL(true, true, false),
        PLAYER_PUSH(false, false, false),
        PLAYER_PUSH_FINAL(true, false, false),
        PLAYER_DISCONNECT(false, false, true),
        PLAYER_DISCONNECT_FINAL(true, false, true);

        private final boolean finalKill;
        private final boolean despawnable;
        private final boolean pvpLogOut;

        PlayerKillCause(final boolean finalKill, final boolean despawnable, final boolean pvpLogOut) {
            this.finalKill = finalKill;
            this.despawnable = despawnable;
            this.pvpLogOut = pvpLogOut;
        }

        public boolean isFinalKill() {
            return this.finalKill;
        }

        public boolean isDespawnable() {
            return this.despawnable;
        }

        public boolean isPvpLogOut() {
            return this.pvpLogOut;
        }
    }
}
