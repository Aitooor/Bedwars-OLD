package dev.eugenio.nasgarbedwars.npc;

import dev.eugenio.nasgarbedwars.utils.LocationUtils;
import dev.eugenio.nasgarbedwars.BedWars;
import lombok.Getter;
import net.jitse.npclib.api.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class NPCManager {
    @Getter
    private final HashMap<String, NPC> npcs = new HashMap<>();

    @Getter
    private Location location = null;

    public NPCManager() {
        try {
            if (BedWars.getInstance().getMainConfig().getYml().isSet("npcs.location")) {
                location = LocationUtils.locFromString(BedWars.getInstance().getMainConfig().getString("npcs.location"));
            } else {
                System.out.println("&cLa localización de los NPCs no ha sido inicializados debido a un error. ¿Está en la config?");
            }
        } catch (Exception ex) {
            System.out.println("&cLos NPCs o la localización de ellos no han sido inicializados debido a un error. Stacktrace: &f" + ex);
        }
    }

    public void addPlayerAndNPC(Player player, NPC npc) {
        BedWars.getInstance().getNpcManager().getNpcs().put(player.getUniqueId().toString(), npc);
        Bukkit.getScheduler().runTask(BedWars.getInstance(), () -> npc.show(player));
    }

    public void destroyPlayerAndNPC(Player player) {
        NPC npc = BedWars.getInstance().getNpcManager().getNpcs().get(player.getUniqueId().toString());
        npc.destroy();
        BedWars.getInstance().getNpcManager().getNpcs().remove(player.getUniqueId().toString());
    }

    public static void destroyAll() {
        if (!BedWars.getInstance().getNpcManager().getNpcs().isEmpty()) {
            BedWars.getInstance().getNpcManager().getNpcs().values().forEach(NPC::destroy);
            BedWars.getInstance().getNpcManager().getNpcs().clear();
        }
    }
}
