package dev.eugenio.nasgarbedwars.api.arena.generator;

import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public interface IGenerator {
    HashMap<String, IGeneratorHolo> getLanguageHolograms();

    void disable();

    void upgrade();

    void spawn();

    void dropItem(final Location p0);

    IArena getArena();

    void rotate();

    Location getLocation();

    ItemStack getOre();

    void setOre(final ItemStack p0);

    void updateHolograms(final Player p0, final String p1);

    void enableRotation();

    ITeam getBwt();

    ArmorStand getHologramHolder();

    GeneratorType getType();

    void setType(final GeneratorType p0);

    int getAmount();

    void setAmount(final int p0);

    int getDelay();

    void setDelay(final int p0);

    int getNextSpawn();

    void setNextSpawn(final int p0);

    int getSpawnLimit();

    void setSpawnLimit(final int p0);

    boolean isStack();

    void setStack(final boolean p0);

    void destroyData();
}
