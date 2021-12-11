package dev.eugenio.nasgarbedwars.upgrades.upgradeaction;

import dev.eugenio.nasgarbedwars.api.arena.generator.GeneratorType;
import dev.eugenio.nasgarbedwars.api.arena.generator.IGenerator;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.upgrades.UpgradeAction;
import dev.eugenio.nasgarbedwars.arena.OreGenerator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GeneratorEditAction implements UpgradeAction {
    private final int amount;
    private final int delay;
    private final int limit;
    private final ApplyType type;
    
    public GeneratorEditAction(final ApplyType type, final int amount, final int delay, final int limit) {
        this.type = type;
        this.amount = amount;
        this.delay = delay;
        this.limit = limit;
    }
    
    @Override
    public void onBuy(Player player, ITeam iTeam) {
        List<IGenerator> list = new ArrayList<>();
        if (this.type == ApplyType.IRON) {
            list = iTeam.getGenerators().stream().filter(paramIGenerator -> (paramIGenerator.getType() == GeneratorType.IRON)).collect(Collectors.toList());
        } else if (this.type == ApplyType.GOLD) {
            list = iTeam.getGenerators().stream().filter(paramIGenerator -> (paramIGenerator.getType() == GeneratorType.GOLD)).collect(Collectors.toList());
        } else if (this.type == ApplyType.EMERALD) {
            if (!iTeam.getArena().getConfig().getArenaLocations("Team." + iTeam.getName() + ".Emerald").isEmpty()) {
                for (Location location : iTeam.getArena().getConfig().getArenaLocations("Team." + iTeam.getName() + ".Emerald")) {
                    OreGenerator oreGenerator = new OreGenerator(location, iTeam.getArena(), GeneratorType.CUSTOM, iTeam);
                    oreGenerator.setOre(new ItemStack(Material.EMERALD));
                    oreGenerator.setType(GeneratorType.EMERALD);
                    iTeam.getGenerators().add(oreGenerator);
                    list.add(oreGenerator);
                }
            } else {
                OreGenerator oreGenerator = new OreGenerator(iTeam.getGenerators().get(0).getLocation().clone(), iTeam.getArena(), GeneratorType.CUSTOM, iTeam);
                oreGenerator.setOre(new ItemStack(Material.EMERALD));
                oreGenerator.setType(GeneratorType.EMERALD);
                iTeam.getGenerators().add(oreGenerator);
                list.add(oreGenerator);
            }
        }

        for (IGenerator o : list) {
            IGenerator iGen = o;
            iGen.setAmount(this.amount);
            iGen.setDelay(this.delay);
            iGen.setSpawnLimit(this.limit);
        }

    }
    
    public enum ApplyType {
        IRON, 
        GOLD, 
        EMERALD
    }
}
