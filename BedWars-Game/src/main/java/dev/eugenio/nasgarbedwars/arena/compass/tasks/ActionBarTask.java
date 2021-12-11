package dev.eugenio.nasgarbedwars.arena.compass.tasks;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.arena.compass.data.MessagesData;
import dev.eugenio.nasgarbedwars.arena.compass.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ActionBarTask extends BukkitRunnable {
    final IArena arena;

    public ActionBarTask(IArena arena) {
        this.arena = arena;
    }

    @Override
    public void run() {
        if (arena.getPlayers().isEmpty() || arena.getPlayers().size() == 1 || !BedWars.getInstance().getTrackingArenaMap().containsKey(arena) || BedWars.getInstance().getTrackingTeamMap(arena) == null) {
            cancel();
            return;
        }
        for (Map.Entry<UUID, ITeam> teamMap : BedWars.getInstance().getTrackingTeamMap(arena).entrySet()) {
            if (teamMap.getValue() == null) continue;
            if (Bukkit.getPlayer(teamMap.getKey()) == null) continue;
            final Player player = Bukkit.getPlayer(teamMap.getKey());
            if (getPlayer(player, teamMap.getValue()) == null) continue;
            player.setCompassTarget(getPlayer(player, teamMap.getValue()).getLocation());
            BedWars.getInstance().getApi().getVersionSupport().playAction(player, TextUtil.colorize(MessagesData.getYml(player).getString(MessagesData.ACTION_BAR_TRACKING).replace("{target}", getPlayer(player, teamMap.getValue()).getDisplayName()).replace("{distance}", String.valueOf(getMeters(player, teamMap.getValue())))).replace("{teamColor}", "§"+teamMap.getValue().getColor().chat().getChar()));
        }
    }

    public int getMeters(Player player, ITeam team) {
        if (getSorted(player, team).isEmpty()) return 0;
        return getSorted(player, team).get(0).getValue();
    }

    public Player getPlayer(Player player, ITeam team) {
        if (getSorted(player, team).isEmpty()) return null;
        return getSorted(player, team).get(0).getKey();
    }

    public List<Map.Entry<Player, Integer>> getSorted(Player player, ITeam team) {
        HashMap<Player, Integer> playerDistanceMap = new HashMap<>();
        team.getMembers().forEach(p -> playerDistanceMap.put(p, (int) player.getLocation().distance(p.getLocation())));
        List<Map.Entry<Player, Integer>> list = new ArrayList<>(playerDistanceMap.entrySet());
        list.sort(Map.Entry.comparingByValue());
        return list;
    }
}