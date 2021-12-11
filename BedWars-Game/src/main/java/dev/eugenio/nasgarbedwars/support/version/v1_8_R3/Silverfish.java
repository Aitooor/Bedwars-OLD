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

public class Silverfish extends EntitySilverfish {
    @Getter
    private ITeam team;
    
    public Silverfish(final World world, final ITeam team) {
        super(world);
        if (team == null) return;
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
        this.team = team;
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.9, false));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true));
        this.goalSelector.a(3, new PathfinderGoalRandomStroll(this, 2.0));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, 20, true, false, o -> o != null && ((EntityHuman)o).isAlive() && !this.team.wasMember(((EntityHuman)o).getUniqueID()) && !this.team.getArena().isReSpawning(((EntityHuman)o).getUniqueID()) && !this.team.getArena().isSpectator(((EntityHuman)o).getUniqueID())));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, IGolem.class, 20, true, false, o -> o != null && ((IGolem)o).getTeam() != this.team));
        this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget(this, Silverfish.class, 20, true, false, o -> o != null && ((Silverfish)o).getTeam() != this.team));
    }
    
    public static LivingEntity spawn(final Location location, final ITeam team, final double value, final double value2, final int n, final double value3) {
        final WorldServer handle = ((CraftWorld)location.getWorld()).getHandle();
        final Silverfish silverfish = new Silverfish(handle, team);
        silverfish.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        silverfish.getAttributeInstance(GenericAttributes.maxHealth).setValue(value2);
        silverfish.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(value);
        silverfish.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(value3);
        ((CraftLivingEntity)silverfish.getBukkitEntity()).setRemoveWhenFarAway(false);
        silverfish.setCustomName(Language.getDefaultLanguage().m(Messages.SHOP_UTILITY_NPC_IRON_GOLEM_NAME).replace("{despawn}", String.valueOf(n).replace("{health}", StringUtils.repeat(Language.getDefaultLanguage().m(Messages.FORMATTING_DESPAWNABLE_UTILITY_NPC_HEALTH) + " ", 10)).replace("{TeamColor}", team.getColor().chat().toString())));
        silverfish.setCustomNameVisible(true);
        handle.addEntity(silverfish, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return (LivingEntity)silverfish.getBukkitEntity();
    }
    
    public void die(final DamageSource damageSource) {
        super.die(damageSource);
        this.team = null;
        BedWars.getInstance().getApi().getVersionSupport().getDespawnablesList().remove(this.getUniqueID());
    }
    
    public void die() {
        super.die();
        this.team = null;
        BedWars.getInstance().getApi().getVersionSupport().getDespawnablesList().remove(this.getUniqueID());
    }
}
