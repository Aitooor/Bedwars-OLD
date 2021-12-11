package dev.eugenio.nasgarbedwars.support.version.v1_8_R3;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.lang.reflect.Field;

public class IGolem extends EntityIronGolem {
    @Getter
    private ITeam team;
    
    private IGolem(final World world, final ITeam team) {
        super(world);
        this.team = team;
        try {
            final Field declaredField = PathfinderGoalSelector.class.getDeclaredField("b");
            declaredField.setAccessible(true);
            final Field declaredField2 = PathfinderGoalSelector.class.getDeclaredField("c");
            declaredField2.setAccessible(true);
            declaredField.set(this.goalSelector, new UnsafeList<>());
            declaredField.set(this.targetSelector, new UnsafeList<>());
            declaredField2.set(this.goalSelector, new UnsafeList<>());
            declaredField2.set(this.targetSelector, new UnsafeList<>());
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            ex.printStackTrace();
        }
        this.setSize(1.4f, 2.9f);
        ((Navigation)this.getNavigation()).a(true);
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.5, false));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true));
        this.goalSelector.a(3, new PathfinderGoalRandomStroll(this, 1.0));
        this.goalSelector.a(4, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, 20, true, false, o -> o != null && ((EntityHuman)o).isAlive() && !team.wasMember(((EntityHuman)o).getUniqueID()) && !team.getArena().isReSpawning(((EntityHuman)o).getUniqueID()) && !team.getArena().isSpectator(((EntityHuman)o).getUniqueID())));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, IGolem.class, 20, true, false, o -> o != null && ((IGolem)o).getTeam() != team));
        this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(this, Silverfish.class, 20, true, false, o -> o != null && ((Silverfish)o).getTeam() != team));
    }
    
    public static LivingEntity spawn(final Location location, final ITeam team, final double value, final double value2, final int n) {
        final WorldServer handle = ((CraftWorld)location.getWorld()).getHandle();
        final IGolem golem = new IGolem(handle, team);
        golem.getAttributeInstance(GenericAttributes.maxHealth).setValue(value2);
        golem.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(value);
        golem.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        ((CraftLivingEntity)golem.getBukkitEntity()).setRemoveWhenFarAway(false);
        golem.setCustomNameVisible(true);
        golem.setCustomName(Language.getDefaultLanguage().m(Messages.SHOP_UTILITY_NPC_IRON_GOLEM_NAME).replace("{despawn}", String.valueOf(n)).replace("{health}", StringUtils.repeat(Language.getDefaultLanguage().m(Messages.FORMATTING_DESPAWNABLE_UTILITY_NPC_HEALTH) + " ", 10)).replace("{TeamColor}", team.getColor().chat().toString()));
        handle.addEntity(golem, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return (LivingEntity)golem.getBukkitEntity();
    }
    
    protected void dropDeathLoot(final boolean b, final int n) {
    }
    
    public void die() {
        super.die();
        this.team = null;
        BedWars.getInstance().getApi().getVersionSupport().getDespawnablesList().remove(this.getUniqueID());
    }
    
    public void die(final DamageSource damageSource) {
        super.die(damageSource);
        this.team = null;
        BedWars.getInstance().getApi().getVersionSupport().getDespawnablesList().remove(this.getUniqueID());
    }
}
