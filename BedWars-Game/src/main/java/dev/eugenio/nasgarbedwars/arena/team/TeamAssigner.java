package dev.eugenio.nasgarbedwars.arena.team;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeamAssigner;
import dev.eugenio.nasgarbedwars.api.events.gameplay.TeamAssignEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class  TeamAssigner implements ITeamAssigner {
    private final LinkedList<Player> skip;

    public TeamAssigner() {
        this.skip = new LinkedList<>();
    }

    @Override
    public void assignTeams(IArena iArena) {
        if (iArena.getPlayers().size() > iArena.getMaxInTeam() && iArena.getMaxInTeam() > 1) {
            LinkedList<List<?>> linkedList = new LinkedList<>();
            if (!linkedList.isEmpty())
                for (ITeam iTeam : iArena.getTeams()) {
                    linkedList.sort(Comparator.comparingInt(List::size));
                    if ((linkedList.get(0)).isEmpty())
                        break;
                    for (int b = 0; b < iArena.getMaxInTeam() && iTeam.getMembers().size() < iArena.getMaxInTeam() && (
                            linkedList.get(0)).size() > b; b++) {
                        Player player = ((List<Player>)linkedList.get(0)).remove(0);
                        TeamAssignEvent teamAssignEvent = new TeamAssignEvent(player, iTeam, iArena);
                        Bukkit.getPluginManager().callEvent(teamAssignEvent);
                        if (!teamAssignEvent.isCancelled()) {
                            player.closeInventory();
                            iTeam.addPlayers(player);
                            this.skip.add(player);
                        }
                    }
                }
        }
        for (Player player : iArena.getPlayers()) {
            if (this.skip.contains(player)) continue;
            for (ITeam iTeam : iArena.getTeams()) {
                if (iTeam.getMembers().size() < iArena.getMaxInTeam()) {
                    TeamAssignEvent teamAssignEvent = new TeamAssignEvent(player, iTeam, iArena);
                    Bukkit.getPluginManager().callEvent(teamAssignEvent);
                    if (!teamAssignEvent.isCancelled()) {
                        player.closeInventory();
                        iTeam.addPlayers(player);
                        break;
                    }
                }
            }
        }
    }
}