package dev.eugenio.nasgarbedwars.listeners.tower;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.team.TeamColor;
import dev.eugenio.nasgarbedwars.api.region.Region;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlaceBlock {
    public PlaceBlock(Block b, int x, int y, int z, TeamColor color, Player p, boolean ladder, int ladderdata) {
        if (b.getRelative(x, y, z).getType().equals(Material.AIR)) {
            for (Region r : BedWars.getInstance().getApi().getArenaUtil().getArenaByPlayer(p).getRegionsList()) {
                if (!r.isInRegion(b.getRelative(x, y, z).getLocation())) continue;
                return;
            }
            if (!ladder) {
                if (BedWars.getInstance().getApi().getVersionSupport().getVersion() >= 7) {
                    b.getRelative(x, y, z).setType(color.woolMaterial());
                    BedWars.getInstance().getApi().getArenaUtil().getArenaByPlayer(p).addPlacedBlock(b.getRelative(x, y, z));
                } else {
                    b.getRelative(x, y, z).setType(Material.WOOL);
                    BedWars.getInstance().getApi().getVersionSupport().setBlockTeamColor(b.getRelative(x, y, z), color);
                    BedWars.getInstance().getApi().getArenaUtil().getArenaByPlayer(p).addPlacedBlock(b.getRelative(x, y, z));
                }
            } else {
                b.getRelative(x, y, z).setType(Material.LADDER);
                b.getRelative(x, y, z).setData((byte)ladderdata);
                BedWars.getInstance().getApi().getArenaUtil().getArenaByPlayer(p).addPlacedBlock(b.getRelative(x, y, z));
            }
        }
    }
}