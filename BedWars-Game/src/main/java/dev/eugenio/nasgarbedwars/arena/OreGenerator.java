package dev.eugenio.nasgarbedwars.arena;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.generator.GeneratorType;
import dev.eugenio.nasgarbedwars.api.arena.generator.IGeneratorHolo;
import dev.eugenio.nasgarbedwars.api.arena.generator.IGenerator;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.events.gameplay.GeneratorUpgradeEvent;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.api.region.Cuboid;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class OreGenerator implements IGenerator {
    @Getter private Location location;
    @Getter private int delay;
    private int upgradeStage;
    private int lastSpawn;
    @Getter private int spawnLimit;
    @Getter private int amount;
    @Getter private IArena arena;
    @Getter private ItemStack ore;
    @Getter private GeneratorType type;
    private int rotate;
    private int dropID;
    @Getter private ITeam bwt;
    boolean up;
    private HashMap<String, IGeneratorHolo> armorStands;
    private ArmorStand item;
    public boolean stack;
    private static final ConcurrentLinkedDeque<OreGenerator> rotation;
    
    public OreGenerator(final Location location, final IArena arena, final GeneratorType type, final ITeam bwt) {
        this.delay = 1;
        this.upgradeStage = 1;
        this.spawnLimit = 0;
        this.amount = 1;
        this.rotate = 0;
        this.dropID = 0;
        this.up = true;
        this.armorStands = new HashMap<>();
        this.stack = BedWars.getInstance().getGeneratorConfig().getBoolean("stack-items");
        if (type == GeneratorType.EMERALD || type == GeneratorType.DIAMOND) {
            this.location = new Location(location.getWorld(), location.getBlockX() + 0.5, location.getBlockY() + 1.3, location.getBlockZ() + 0.5);
        } else {
            this.location = location.add(0.0, 1.3, 0.0);
        }
        this.arena = arena;
        this.bwt = bwt;
        this.type = type;
        this.loadDefaults();
        BedWars.debug("Inicializando nuevo generador en: " + location.toString() + " - " + type + " - " + ((bwt == null) ? "NOTEAM" : bwt.getName()));
        final Cuboid cuboid = new Cuboid(location, 1, true);
        cuboid.setMaxY(cuboid.getMaxY() + 5);
        cuboid.setMinY(cuboid.getMinY() - 2);
        arena.getRegionsList().add(cuboid);
    }
    
    @Override
    public void upgrade() {
        switch (this.type) {
            case DIAMOND: {
                ++this.upgradeStage;
                if (this.upgradeStage == 2) {
                    this.delay = BedWars.getInstance().getGeneratorConfig().getInt((BedWars.getInstance().getGeneratorConfig().getYml().get(this.arena.getGroup() + "." + "diamond.tierII.delay") == null) ? "Default.diamond.tierII.delay" : (this.arena.getGroup() + "." + "diamond.tierII.delay"));
                    this.spawnLimit = BedWars.getInstance().getGeneratorConfig().getInt((BedWars.getInstance().getGeneratorConfig().getYml().get(this.arena.getGroup() + "." + "diamond.tierII.spawn-limit") == null) ? "Default.diamond.tierII.spawn-limit" : (this.arena.getGroup() + "." + "diamond.tierII.spawn-limit"));
                } else if (this.upgradeStage == 3) {
                    this.delay = BedWars.getInstance().getGeneratorConfig().getInt((BedWars.getInstance().getGeneratorConfig().getYml().get(this.arena.getGroup() + "." + "diamond.tierIII.delay") == null) ? "Default.diamond.tierIII.delay" : (this.arena.getGroup() + "." + "diamond.tierIII.delay"));
                    this.spawnLimit = BedWars.getInstance().getGeneratorConfig().getInt((BedWars.getInstance().getGeneratorConfig().getYml().get(this.arena.getGroup() + "." + "diamond.tierII.spawn-limit") == null) ? "Default.diamond.tierIII.spawn-limit" : (this.arena.getGroup() + "." + "diamond.tierIII.spawn-limit"));
                }
                this.ore = new ItemStack(Material.DIAMOND);
                for (final IGeneratorHolo genHolo : this.armorStands.values()) genHolo.setTierName(Language.getLang(genHolo.getIso()).m(Messages.GENERATOR_HOLOGRAM_TIER).replace("{tier}", Language.getLang(genHolo.getIso()).m((this.upgradeStage == 2) ? Messages.FORMATTING_GENERATOR_TIER2 : Messages.FORMATTING_GENERATOR_TIER3)));
                break;
            }
            case EMERALD: {
                ++this.upgradeStage;
                if (this.upgradeStage == 2) {
                    this.delay = BedWars.getInstance().getGeneratorConfig().getInt((BedWars.getInstance().getGeneratorConfig().getYml().get(this.arena.getGroup() + "." + "emerald.tierII.delay") == null) ? "Default.emerald.tierII.delay" : (this.arena.getGroup() + "." + "emerald.tierII.delay"));
                    this.spawnLimit = BedWars.getInstance().getGeneratorConfig().getInt((BedWars.getInstance().getGeneratorConfig().getYml().get(this.arena.getGroup() + "." + "emerald.tierII.spawn-limit") == null) ? "Default.emerald.tierII.spawn-limit" : (this.arena.getGroup() + "." + "emerald.tierII.spawn-limit"));
                } else if (this.upgradeStage == 3) {
                    this.delay = BedWars.getInstance().getGeneratorConfig().getInt((BedWars.getInstance().getGeneratorConfig().getYml().get(this.arena.getGroup() + "." + "emerald.tierIII.delay") == null) ? "Default.emerald.tierIII.delay" : (this.arena.getGroup() + "." + "emerald.tierIII.delay"));
                    this.spawnLimit = BedWars.getInstance().getGeneratorConfig().getInt((BedWars.getInstance().getGeneratorConfig().getYml().get(this.arena.getGroup() + "." + "emerald.tierII.spawn-limit") == null) ? "Default.emerald.tierIII.spawn-limit" : (this.arena.getGroup() + "." + "emerald.tierIII.spawn-limit"));
                }
                this.ore = new ItemStack(Material.EMERALD);
                for (final IGeneratorHolo genHolo2 : this.armorStands.values()) genHolo2.setTierName(Language.getLang(genHolo2.getIso()).m(Messages.GENERATOR_HOLOGRAM_TIER).replace("{tier}", Language.getLang(genHolo2.getIso()).m((this.upgradeStage == 2) ? Messages.FORMATTING_GENERATOR_TIER2 : Messages.FORMATTING_GENERATOR_TIER3)));
                break;
            }
        }
        Bukkit.getPluginManager().callEvent(new GeneratorUpgradeEvent(this));
    }
    
    @Override
    public void spawn() {
        if (this.lastSpawn == 0) {
            this.lastSpawn = this.delay;
            if (this.spawnLimit != 0) {
                int n = 0;
                for (final Entity entity2 : this.location.getWorld().getNearbyEntities(this.location, 3.0, 3.0, 3.0)) {
                    if (entity2.getType() == EntityType.DROPPED_ITEM) {
                        if (((Item)entity2).getItemStack().getType() == this.ore.getType()) ++n;
                        if (n >= this.spawnLimit) return;
                    }
                }
                this.lastSpawn = this.delay;
            }
            if (this.bwt == null) {
                this.dropItem(this.location);
                return;
            }
            if (this.bwt.getMembers().size() == 1) {
                this.dropItem(this.location);
                return;
            }
            if (BedWars.getInstance().getMainConfig().getBoolean("enable-gen-split")) {
                // Algo
            } else {
                this.dropItem(this.location);
            }
        }
        --this.lastSpawn;
        for (final IGeneratorHolo genHolo : this.armorStands.values()) genHolo.setTimerName(Language.getLang(genHolo.getIso()).m(Messages.GENERATOR_HOLOGRAM_TIMER).replace("{seconds}", String.valueOf(this.lastSpawn)));
    }
    
    private void dropItem(final Location location, final int n) {
        for (int i = n; i >= 0; --i, --i) {
            final ItemStack itemStack = new ItemStack(this.ore);
            if (!this.stack) {
                final ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName("custom" + this.dropID++);
                itemStack.setItemMeta(itemMeta);
            }
            location.getWorld().dropItem(location, itemStack).setVelocity(new Vector(0, 0, 0));
        }
    }
    
    @Override
    public void dropItem(final Location location) {
        this.dropItem(location, this.amount);
    }
    
    @Override
    public void setOre(final ItemStack ore) {
        BedWars.debug("Cambiando ore generator en " + this.location.toString() + " de " + this.ore + " a " + ore);
        this.ore = ore;
    }
    
    public static ConcurrentLinkedDeque<OreGenerator> getRotation() {
        return OreGenerator.rotation;
    }
    
    @Override
    public HashMap<String, IGeneratorHolo> getLanguageHolograms() {
        return this.armorStands;
    }
    
    private static ArmorStand createArmorStand(final String customName, final Location location) {
        final ArmorStand armorStand = (ArmorStand)location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setGravity(false);
        if (customName != null) {
            armorStand.setCustomName(customName);
            armorStand.setCustomNameVisible(true);
        }
        armorStand.setRemoveWhenFarAway(false);
        armorStand.setVisible(false);
        armorStand.setCanPickupItems(false);
        armorStand.setArms(false);
        armorStand.setBasePlate(false);
        armorStand.setMarker(true);
        return armorStand;
    }
    
    @Override
    public void rotate() {
        if (this.up) {
            if (this.rotate >= 540) this.up = false;
            if (this.rotate > 500) {
                this.item.setHeadPose(new EulerAngle(0.0, Math.toRadians(++this.rotate), 0.0));
            } else if (this.rotate > 470) {
                final ArmorStand item = this.item;
                final double n = 0.0;
                final int rotate = this.rotate + 2;
                this.rotate = rotate;
                item.setHeadPose(new EulerAngle(n, Math.toRadians(rotate), 0.0));
            } else if (this.rotate > 450) {
                final ArmorStand item2 = this.item;
                final double n2 = 0.0;
                final int rotate2 = this.rotate + 3;
                this.rotate = rotate2;
                item2.setHeadPose(new EulerAngle(n2, Math.toRadians(rotate2), 0.0));
            } else {
                final ArmorStand item3 = this.item;
                final double n3 = 0.0;
                final int rotate3 = this.rotate + 4;
                this.rotate = rotate3;
                item3.setHeadPose(new EulerAngle(n3, Math.toRadians(rotate3), 0.0));
            }
        } else {
            if (this.rotate <= 0) this.up = true;
            if (this.rotate > 120) {
                final ArmorStand item4 = this.item;
                final double n4 = 0.0;
                final int rotate4 = this.rotate - 4;
                this.rotate = rotate4;
                item4.setHeadPose(new EulerAngle(n4, Math.toRadians(rotate4), 0.0));
            } else if (this.rotate > 90) {
                final ArmorStand item5 = this.item;
                final double n5 = 0.0;
                final int rotate5 = this.rotate - 3;
                this.rotate = rotate5;
                item5.setHeadPose(new EulerAngle(n5, Math.toRadians(rotate5), 0.0));
            } else if (this.rotate > 70) {
                final ArmorStand item6 = this.item;
                final double n6 = 0.0;
                final int rotate6 = this.rotate - 2;
                this.rotate = rotate6;
                item6.setHeadPose(new EulerAngle(n6, Math.toRadians(rotate6), 0.0));
            } else {
                final ArmorStand item7 = this.item;
                final double n7 = 0.0;
                final int rotate7 = this.rotate - 1;
                this.rotate = rotate7;
                item7.setHeadPose(new EulerAngle(n7, Math.toRadians(rotate7), 0.0));
            }
        }
    }
    
    @Override
    public void setDelay(final int delay) {
        this.delay = delay;
    }
    
    @Override
    public void setAmount(final int amount) {
        this.amount = amount;
    }
    
    @Override
    public void disable() {
        if (this.getOre().getType() == Material.EMERALD || this.getOre().getType() == Material.DIAMOND) {
            OreGenerator.rotation.remove(this);
            for (IGeneratorHolo iGeneratorHolo : this.armorStands.values()) iGeneratorHolo.destroy();
        }
        this.armorStands.clear();
    }
    
    @Override
    public void updateHolograms(final Player player, final String s) {
        for (IGeneratorHolo iGeneratorHolo : this.armorStands.values()) iGeneratorHolo.updateForPlayer(player, s);
    }
    
    @Override
    public void enableRotation() {
        OreGenerator.rotation.add(this);
        for (final Language language : Language.getLanguages()) if (this.armorStands.get(language.getIso()) == null) this.armorStands.put(language.getIso(), new Hologram(language.getIso()));
        for (IGeneratorHolo iGeneratorHolo : this.armorStands.values()) iGeneratorHolo.updateForAll();
        (this.item = createArmorStand(null, this.location.clone().add(0.0, 0.5, 0.0))).setHelmet(new ItemStack((this.type == GeneratorType.DIAMOND) ? Material.DIAMOND_BLOCK : Material.EMERALD_BLOCK));
    }
    
    @Override
    public void setSpawnLimit(final int spawnLimit) {
        this.spawnLimit = spawnLimit;
    }
    
    private void loadDefaults() {
        switch (this.type) {
            case GOLD:
                this.delay = BedWars.getInstance().getGeneratorConfig().getInt((BedWars.getInstance().getGeneratorConfig().getYml().get(this.arena.getGroup() + "." + "gold.delay") == null) ? "Default.gold.delay" : (this.arena.getGroup() + "." + "gold.delay"));
                this.ore = new ItemStack(Material.GOLD_INGOT);
                this.amount = BedWars.getInstance().getGeneratorConfig().getInt((BedWars.getInstance().getGeneratorConfig().getYml().get(this.arena.getGroup() + "." + "gold.amount") == null) ? "Default.gold.amount" : (this.arena.getGroup() + "." + "gold.amount"));
                this.spawnLimit = BedWars.getInstance().getGeneratorConfig().getInt((BedWars.getInstance().getGeneratorConfig().getYml().get(this.arena.getGroup() + "." + "gold.spawn-limit") == null) ? "Default.gold.spawn-limit" : (this.arena.getGroup() + "." + "gold.spawn-limit"));
                break;
            case IRON:
                this.delay = BedWars.getInstance().getGeneratorConfig().getInt((BedWars.getInstance().getGeneratorConfig().getYml().get(this.arena.getGroup() + "." + "iron.delay") == null) ? "Default.iron.delay" : (this.arena.getGroup() + "." + "iron.delay"));
                this.amount = BedWars.getInstance().getGeneratorConfig().getInt((BedWars.getInstance().getGeneratorConfig().getYml().get(this.arena.getGroup() + "." + "iron.amount") == null) ? "Default.iron.amount" : (this.arena.getGroup() + "." + "iron.amount"));
                this.ore = new ItemStack(Material.IRON_INGOT);
                this.spawnLimit = BedWars.getInstance().getGeneratorConfig().getInt((BedWars.getInstance().getGeneratorConfig().getYml().get(this.arena.getGroup() + "." + "iron.spawn-limit") == null) ? "Default.iron.spawn-limit" : (this.arena.getGroup() + "." + "iron.spawn-limit"));
                break;
            case DIAMOND:
                this.delay = BedWars.getInstance().getGeneratorConfig().getInt((BedWars.getInstance().getGeneratorConfig().getYml().get(this.arena.getGroup() + "." + "diamond.tierI.delay") == null) ? "Default.diamond.tierI.delay" : (this.arena.getGroup() + "." + "diamond.tierI.delay"));
                this.spawnLimit = BedWars.getInstance().getGeneratorConfig().getInt((BedWars.getInstance().getGeneratorConfig().getYml().get(this.arena.getGroup() + "." + "diamond.tierI.spawn-limit") == null) ? "Default.diamond.tierI.spawn-limit" : (this.arena.getGroup() + "." + "diamond.tierI.spawn-limit"));
                this.ore = new ItemStack(Material.DIAMOND);
                break;
            case EMERALD:
                this.delay = BedWars.getInstance().getGeneratorConfig().getInt((BedWars.getInstance().getGeneratorConfig().getYml().get(this.arena.getGroup() + "." + "emerald.tierI.delay") == null) ? "Default.emerald.tierI.delay" : (this.arena.getGroup() + "." + "emerald.tierI.delay"));
                this.spawnLimit = BedWars.getInstance().getGeneratorConfig().getInt((BedWars.getInstance().getGeneratorConfig().getYml().get(this.arena.getGroup() + "." + "emerald.tierI.spawn-limit") == null) ? "Default.emerald.tierI.spawn-limit" : (this.arena.getGroup() + "." + "emerald.tierI.spawn-limit"));
                this.ore = new ItemStack(Material.EMERALD);
                break;
        }
        this.lastSpawn = this.delay;
    }
    
    @Override
    public ArmorStand getHologramHolder() {
        return this.item;
    }
    
    @Override
    public int getNextSpawn() {
        return this.lastSpawn;
    }
    
    @Override
    public void setNextSpawn(final int lastSpawn) {
        this.lastSpawn = lastSpawn;
    }
    
    @Override
    public void setStack(final boolean stack) {
        this.stack = stack;
    }
    
    @Override
    public boolean isStack() {
        return this.stack;
    }
    
    @Override
    public void setType(final GeneratorType type) {
        this.type = type;
    }
    
    @Override
    public void destroyData() {
        OreGenerator.rotation.remove(this);
        this.location = null;
        this.arena = null;
        this.ore = null;
        this.bwt = null;
        this.armorStands = null;
        this.item = null;
    }
    
    static {
        rotation = new ConcurrentLinkedDeque<>();
    }
    
    public class Hologram implements IGeneratorHolo {
        @Getter String iso;
        ArmorStand tier;
        ArmorStand timer;
        ArmorStand name;
        
        public Hologram(final String iso) {
            this.iso = iso;
            this.tier = createArmorStand(Language.getLang(iso).m(Messages.GENERATOR_HOLOGRAM_TIER).replace("{tier}", Language.getLang(iso).m(Messages.FORMATTING_GENERATOR_TIER1)), OreGenerator.this.location.clone().add(0.0, 3.0, 0.0));
            this.timer = createArmorStand(Language.getLang(iso).m(Messages.GENERATOR_HOLOGRAM_TIMER).replace("{seconds}", String.valueOf(OreGenerator.this.lastSpawn)), OreGenerator.this.location.clone().add(0.0, 2.4, 0.0));
            this.name = createArmorStand(Language.getLang(iso).m((OreGenerator.this.getOre().getType() == Material.DIAMOND) ? Messages.GENERATOR_HOLOGRAM_TYPE_DIAMOND : Messages.GENERATOR_HOLOGRAM_TYPE_EMERALD), OreGenerator.this.location.clone().add(0.0, 2.7, 0.0));
        }
        
        @Override
        public void updateForAll() {
            for (final Player player : this.timer.getWorld().getPlayers()) {
                if (Language.getPlayerLanguage(player).getIso().equalsIgnoreCase(this.iso)) continue;
                BedWars.getInstance().getNms().hideEntity(this.tier, player);
                BedWars.getInstance().getNms().hideEntity(this.timer, player);
                BedWars.getInstance().getNms().hideEntity(this.name, player);
            }
        }
        
        @Override
        public void updateForPlayer(final Player player, final String s) {
            if (s.equalsIgnoreCase(this.iso)) return;
            BedWars.getInstance().getNms().hideEntity(this.tier, player);
            BedWars.getInstance().getNms().hideEntity(this.timer, player);
            BedWars.getInstance().getNms().hideEntity(this.name, player);
        }
        
        @Override
        public void setTierName(final String customName) {
            if (this.tier.isDead()) return;
            this.tier.setCustomName(customName);
        }
        
        @Override
        public void setTimerName(final String customName) {
            if (this.timer.isDead()) {
                return;
            }
            this.timer.setCustomName(customName);
        }
        
        @Override
        public void destroy() {
            this.tier.remove();
            this.timer.remove();
            this.name.remove();
        }
    }
}
