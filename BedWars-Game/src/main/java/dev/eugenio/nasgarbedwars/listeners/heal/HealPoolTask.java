package dev.eugenio.nasgarbedwars.listeners.heal;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.configuration.CachedPath;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HealPoolTask extends BukkitRunnable {

    private final ITeam bwt;
    private final int maxX;
    private final int minX;
    private final int maxY;
    private final int minY;
    private final int maxZ;
    private final int minZ;
    private final IArena arena;
    private final Random r = new Random();

    private static final List<HealPoolTask> healPoolTasks = new ArrayList<>();

    public HealPoolTask(ITeam bwt) {
        this.bwt = bwt;
        int radius = bwt.getArena().getConfig().getInt(CachedPath.ARENA_ISLAND_RADIUS);
        this.maxX = Math.max(bwt.getSpawn().clone().add(radius, 0, 0).getBlockX(), bwt.getSpawn().clone().subtract(radius, 0, 0).getBlockX());
        this.minX = Math.min(bwt.getSpawn().clone().add(radius, 0, 0).getBlockX(), bwt.getSpawn().clone().subtract(radius, 0, 0).getBlockX());
        this.maxY = Math.max(bwt.getSpawn().clone().add(0, radius, 0).getBlockY(), bwt.getSpawn().clone().subtract(0, radius, 0).getBlockY());
        this.minY = Math.min(bwt.getSpawn().clone().add(0, radius, 0).getBlockY(), bwt.getSpawn().clone().subtract(0, radius, 0).getBlockY());
        this.maxZ = Math.max(bwt.getSpawn().clone().add(0, 0, radius).getBlockZ(), bwt.getSpawn().clone().subtract(0, 0, radius).getBlockZ());
        this.minZ = Math.min(bwt.getSpawn().clone().add(0, 0, radius).getBlockZ(), bwt.getSpawn().clone().subtract(0, 0, radius).getBlockZ());
        this.arena = bwt.getArena();
        this.runTaskTimerAsynchronously(BedWars.getInstance(), 0, 100L);
        healPoolTasks.add(this);
    }

    @Override
    public void run() {
        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    Location l = new Location(bwt.getSpawn().getWorld(), x, y, z);
                    if (l.getBlock().getType() != Material.AIR) continue;
                    int chance = r.nextInt(45);
                    if (chance != 0) continue;
                    for (Player p : Bukkit.getOnlinePlayers()) playEffect(p, l);
                }
            }
        }
    }

    public static boolean exists(IArena arena, ITeam bwt) {
        for (HealPoolTask hpt : new ArrayList<>(healPoolTasks)) if (hpt.getArena() == arena && hpt.getBwt() == bwt) return true;
        return false;
    }

    public static void removeForArena(String a) {
        for (HealPoolTask hpt : new ArrayList<>(healPoolTasks)) {
            if (hpt.getArena().getWorldName().equals(a)) {
                healPoolTasks.remove(hpt);
                hpt.cancel();
            }
        }
    }

    public ITeam getBwt() {
        return bwt;
    }

    public IArena getArena() {
        return arena;
    }

    private void playEffect(Player p, Location loc) {
        PacketPlayOutWorldParticles pwp = new PacketPlayOutWorldParticles(EnumParticle.VILLAGER_HAPPY, true, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), (float) 0, (float) 0, (float) 0, (float) 0, 1);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(pwp);
    }
}
