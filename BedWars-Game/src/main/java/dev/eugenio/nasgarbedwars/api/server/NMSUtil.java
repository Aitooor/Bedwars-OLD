package dev.eugenio.nasgarbedwars.api.server;

import dev.eugenio.nasgarbedwars.api.exceptions.InvalidEffectException;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.arena.team.TeamColor;
import dev.eugenio.nasgarbedwars.api.entity.Despawnable;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class NMSUtil {
    private static String name2;
    private static ConcurrentHashMap<UUID, Despawnable> despawnables;

    static {
        NMSUtil.despawnables = new ConcurrentHashMap<>();
    }

    private final Plugin plugin;
    private Effect eggBridge;

    public NMSUtil(final Plugin plugin, final String name2) {
        NMSUtil.name2 = name2;
        this.plugin = plugin;
    }

    public static String getName() {
        return NMSUtil.name2;
    }

    public abstract void registerCommand(final String p0, final Command p1);

    public abstract void sendTitle(final Player p0, final String p1, final String p2, final int p3, final int p4, final int p5);

    public abstract void playAction(final Player p0, final String p1);

    public abstract ItemStack getItemInHand(final Player p0);

    public abstract void hideEntity(final Entity p0, final Player p1);

    public abstract boolean isArmor(final ItemStack p0);

    public abstract boolean isTool(final ItemStack p0);

    public abstract boolean isSword(final ItemStack p0);

    public abstract boolean isAxe(final ItemStack p0);

    public abstract boolean isBow(final ItemStack p0);

    public abstract boolean isProjectile(final ItemStack p0);

    public abstract void registerEntities();

    public abstract void spawnShop(final Location p0, final String p1, final List<Player> p2, final IArena p3);

    public abstract double getDamage(final ItemStack p0);

    public abstract void spawnSilverfish(final Location p0, final ITeam p1, final double p2, final double p3, final int p4, final double p5);

    public abstract void spawnIronGolem(final Location p0, final ITeam p1, final double p2, final double p3, final int p4);

    public boolean isDespawnable(final Entity entity) {
        return NMSUtil.despawnables.get(entity.getUniqueId()) != null;
    }

    public abstract void minusAmount(final Player p0, final ItemStack p1, final int p2);

    public abstract void setSource(final TNTPrimed p0, final Player p1);

    public abstract void voidKill(final Player p0);

    public abstract void hideArmor(final Player p0, final Player p1);

    public abstract void showArmor(final Player p0, final Player p1);

    public abstract void spawnDragon(final Location p0, final ITeam p1);

    public abstract void colorBed(final ITeam p0);

    public abstract void registerTntWhitelist();

    public Effect eggBridge() {
        return this.eggBridge;
    }

    public void setEggBridgeEffect(final String s) throws InvalidEffectException {
        try {
            this.eggBridge = Effect.valueOf(s);
        } catch (Exception ex) {
            throw new InvalidEffectException(s);
        }
    }

    public abstract void setBlockTeamColor(final Block p0, final TeamColor p1);

    public abstract void setCollide(final Player p0, final IArena p1, final boolean p2);

    public abstract ItemStack addCustomData(final ItemStack p0, final String p1);

    public abstract ItemStack setTag(final ItemStack p0, final String p1, final String p2);

    public abstract boolean isCustomBedWarsItem(final ItemStack p0);

    public abstract String getCustomData(final ItemStack p0);

    public abstract ItemStack setSkullOwner(final ItemStack p0, final Player p1);

    public abstract ItemStack colourItem(final ItemStack p0, final ITeam p1);

    public abstract ItemStack createItemStack(final String p0, final int p1, final short p2);

    public boolean isPlayerHead(final String s, final int n) {
        return s.equalsIgnoreCase("PLAYER_HEAD");
    }

    public abstract Material materialFireball();

    public abstract Material materialPlayerHead();

    public abstract Material materialSnowball();

    public abstract Material materialGoldenHelmet();

    public abstract Material materialGoldenChestPlate();

    public abstract Material materialGoldenLeggings();

    public abstract Material materialCake();

    public boolean isBed(final Material material) {
        return material.toString().contains("_BED");
    }

    public boolean itemStackDataCompare(final ItemStack itemStack, final short n) {
        return true;
    }

    public abstract Material woolMaterial();

    public abstract String getShopUpgradeIdentifier(final ItemStack p0);

    public abstract ItemStack setShopUpgradeIdentifier(final ItemStack p0, final String p1);

    public abstract void sendPlayerSpawnPackets(final Player p0, final IArena p1);

    public abstract String getInventoryName(final InventoryEvent p0);

    public abstract void setUnbreakable(final ItemMeta p0);

    public ConcurrentHashMap<UUID, Despawnable> getDespawnablesList() {
        return NMSUtil.despawnables;
    }

    public abstract int getVersion();

    public Plugin getPlugin() {
        return this.plugin;
    }

    public abstract void registerVersionListeners();

    public byte getCompressedAngle(final float n) {
        return (byte) (n * 256.0f / 360.0f);
    }

    public void spigotShowPlayer(final Player player, final Player player2) {
        player2.showPlayer(player);
    }

    public void spigotHidePlayer(final Player player, final Player player2) {
        player2.hidePlayer(player);
    }

    public abstract Fireball setFireballDirection(final Fireball p0, final Vector p1);

    public abstract void playRedStoneDot(final Player p0);
}
