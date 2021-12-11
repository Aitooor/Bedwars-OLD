package dev.eugenio.nasgarbedwars.api.entity;

import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerKillEvent;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;

public class Despawnable {
    private static BedWarsAPI api;
    private final LivingEntity e;
    @Getter
    private ITeam team;
    @Getter
    private int despawn;
    private String namePath;
    private PlayerKillEvent.PlayerKillCause deathRegularCause;
    private PlayerKillEvent.PlayerKillCause deathFinalCause;
    private UUID uuid;

    public Despawnable(final LivingEntity e, final ITeam team, final int despawn, final String namePath, final PlayerKillEvent.PlayerKillCause deathFinalCause, final PlayerKillEvent.PlayerKillCause deathRegularCause) {
        this.despawn = 250;
        this.e = e;
        if (e == null) return;
        this.uuid = e.getUniqueId();
        this.team = team;
        this.deathFinalCause = deathFinalCause;
        this.deathRegularCause = deathRegularCause;
        if (despawn != 0) this.despawn = despawn;
        this.namePath = namePath;
        if (Despawnable.api == null) {
            Despawnable.api = Bukkit.getServer().getServicesManager().getRegistration(BedWarsAPI.class).getProvider();
        }
        Despawnable.api.getVersionSupport().getDespawnablesList().put(this.uuid, this);
        this.setName();
    }

    public void refresh() {
        if (this.e.isDead() || this.team == null || this.team.getArena() == null) {
            Despawnable.api.getVersionSupport().getDespawnablesList().remove(this.uuid);
            if (this.team.getArena() == null) this.e.damage(this.e.getHealth() + 100.0);
            return;
        }
        this.setName();
        --this.despawn;
        if (this.despawn == 0) {
            this.e.damage(this.e.getHealth() + 100.0);
            Despawnable.api.getVersionSupport().getDespawnablesList().remove(this.e.getUniqueId());
        }
    }

    private void setName() {
        final int n = (int) (this.e.getHealth() * 100.0 / this.e.getMaxHealth() / 10.0);
        String customName = Despawnable.api.getDefaultLang().m(this.namePath).replace("{despawn}", String.valueOf(this.despawn)).replace("{health}", new String(new char[n]).replace("\u0000", Despawnable.api.getDefaultLang().m(Messages.FORMATTING_DESPAWNABLE_UTILITY_NPC_HEALTH)) + new String(new char[10 - n]).replace("\u0000", "�7" + Despawnable.api.getDefaultLang().m(Messages.FORMATTING_DESPAWNABLE_UTILITY_NPC_HEALTH)));
        if (this.team != null)
            customName = customName.replace("{TeamColor}", this.team.getColor().chat().toString()).replace("{TeamName}", this.team.getDisplayName(Despawnable.api.getDefaultLang()));
        this.e.setCustomName(customName);
    }

    public LivingEntity getEntity() {
        return this.e;
    }

    public PlayerKillEvent.PlayerKillCause getDeathFinalCause() {
        return this.deathFinalCause;
    }

    public PlayerKillEvent.PlayerKillCause getDeathRegularCause() {
        return this.deathRegularCause;
    }

    public void destroy() {
        if (this.getEntity() != null) this.getEntity().damage(2.147483647E9);
        this.team = null;
        Despawnable.api.getVersionSupport().getDespawnablesList().remove(this.uuid);
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof LivingEntity && ((LivingEntity) o).getUniqueId().equals(this.e.getUniqueId());
    }
}
