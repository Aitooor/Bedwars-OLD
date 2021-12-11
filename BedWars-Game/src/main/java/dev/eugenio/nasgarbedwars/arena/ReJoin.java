package dev.eugenio.nasgarbedwars.arena;

import com.google.gson.JsonObject;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.arena.tasks.ReJoinTask;
import dev.eugenio.nasgarbedwars.configuration.Sounds;
import dev.eugenio.nasgarbedwars.api.arena.GameStatus;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.shop.ShopCache;
import lombok.Getter;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ReJoin {
    @Getter private UUID player;
    @Getter private IArena arena;
    @Getter private ITeam bwt;
    @Getter private ReJoinTask task;
    private final ArrayList<ShopCache.CachedItem> permanentsAndNonDowngradables;
    private static final List<ReJoin> reJoinList;
    
    public ReJoin(final Player player, final IArena arena, final ITeam bwt, final List<ShopCache.CachedItem> list) {
        this.task = null;
        this.permanentsAndNonDowngradables = new ArrayList<>();
        final ReJoin player2 = getPlayer(player);
        if (player2 != null) player2.destroy(true);
        if (bwt == null) return;
        if (bwt.isBedDestroyed()) return;
        this.bwt = bwt;
        this.player = player.getUniqueId();
        this.arena = arena;
        ReJoin.reJoinList.add(this);
        BedWars.debug("Creado ReJoin para " + player.getName() + " " + player.getUniqueId() + " en " + arena.getArenaName());
        if (bwt.getMembers().isEmpty()) this.task = new ReJoinTask(arena, bwt);
        this.permanentsAndNonDowngradables.addAll(list);
    }
    
    public static boolean exists(final Player player) {
        BedWars.debug("ReJoin check existe en " + player.getUniqueId());
        for (final ReJoin reJoin : getReJoinList()) {
            BedWars.debug("ReJoin check existe en list scroll: " + reJoin.getPl().toString());
            if (reJoin.getPl().equals(player.getUniqueId())) return true;
        }
        return false;
    }
    
    @Nullable
    public static ReJoin getPlayer(final Player player) {
        BedWars.debug("ReJoin getPlayer " + player.getUniqueId());
        for (final ReJoin reJoin : getReJoinList()) if (reJoin.getPl().equals(player.getUniqueId())) return reJoin;
        return null;
    }
    
    public boolean canReJoin() {
        BedWars.debug("ReJoin canReJoin check.");
        if (this.arena == null) {
            BedWars.debug("ReJoin canReJoin arena es null " + this.player.toString());
            this.destroy(true);
            return false;
        }
        if (this.arena.getStatus() == GameStatus.restarting) {
            BedWars.debug("ReJoin canReJoin estado está reiniciandose " + this.player.toString());
            this.destroy(true);
            return false;
        }
        if (this.bwt == null) {
            BedWars.debug("ReJoin canReJoin bwt es null " + this.player.toString());
            this.destroy(true);
            return false;
        }
        if (this.bwt.isBedDestroyed()) {
            BedWars.debug("ReJoin canReJoin la cama está destruida " + this.player.toString());
            this.destroy(false);
            return false;
        }
        return true;
    }
    
    public boolean reJoin(final Player player) {
        Sounds.playSound("rejoin-allowed", player);
        player.sendMessage(Language.getMsg(player, Messages.REJOIN_ALLOWED).replace("{arena}", this.getArena().getDisplayName()));
        return this.arena.reJoin(player);
    }
    
    public void destroy(final boolean b) {
        BedWars.debug("ReJoin destruido para " + this.player.toString());
        ReJoin.reJoinList.remove(this);
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "RD");
        jsonObject.addProperty("uuid", this.player.toString());
        jsonObject.addProperty("server", BedWars.getInstance().getMainConfig().getString("bungee-settings.server-id"));
        if (this.bwt != null && b && this.bwt.getMembers().isEmpty()) {
            this.bwt.setBedDestroyed(true);
            if (this.bwt != null) {
                for (final Player player : this.arena.getPlayers()) player.sendMessage(Language.getMsg(player, Messages.TEAM_ELIMINATED_CHAT).replace("{TeamColor}", this.bwt.getColor().chat().toString()).replace("{TeamName}", this.bwt.getDisplayName(Language.getPlayerLanguage(player))));
                for (final Player player2 : this.arena.getSpectators()) player2.sendMessage(Language.getMsg(player2, Messages.TEAM_ELIMINATED_CHAT).replace("{TeamColor}", this.bwt.getColor().chat().toString()).replace("{TeamName}", this.bwt.getDisplayName(Language.getPlayerLanguage(player2))));
            }
            this.arena.checkWinner();
        }
    }
    
    public UUID getPl() {
        return this.player;
    }
    
    public List<ShopCache.CachedItem> getPermanentsAndNonDowngradables() {
        return this.permanentsAndNonDowngradables;
    }
    
    public static List<ReJoin> getReJoinList() {
        return Collections.unmodifiableList(ReJoin.reJoinList);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) return false;
        if (!(o instanceof ReJoin)) return false;
        ReJoin reJoin = (ReJoin) o;
        return reJoin.getPl().equals(getPl());
    }
    
    static {
        reJoinList = new ArrayList<>();
    }
}
