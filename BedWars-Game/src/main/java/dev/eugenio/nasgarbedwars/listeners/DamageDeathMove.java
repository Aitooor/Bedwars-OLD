package dev.eugenio.nasgarbedwars.listeners;

import dev.eugenio.nasgarbedwars.listeners.dropshandler.PlayerDrops;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.GameStatus;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.generator.IGenerator;
import dev.eugenio.nasgarbedwars.api.arena.shop.ShopHologram;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.entity.Despawnable;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerInvisibilityDrinkEvent;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerKillEvent;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.arena.LastHit;
import dev.eugenio.nasgarbedwars.arena.SetupSession;
import dev.eugenio.nasgarbedwars.arena.team.BedWarsTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Map;

public class DamageDeathMove implements Listener {
    @EventHandler
    public void onDamage(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player)event.getEntity();
            final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
            if (arenaByPlayer != null) {
                if (arenaByPlayer.isSpectator(player)) {
                    event.setCancelled(true);
                    return;
                }
                if (arenaByPlayer.isReSpawning(player)) {
                    event.setCancelled(true);
                    return;
                }
                if (arenaByPlayer.getStatus() != GameStatus.playing) {
                    event.setCancelled(true);
                    return;
                }
                if (BedWarsTeam.reSpawnInvulnerability.containsKey(player.getUniqueId())) {
                    if (BedWarsTeam.reSpawnInvulnerability.get(player.getUniqueId()) > System.currentTimeMillis()) {
                        event.setCancelled(true);
                    } else {
                        BedWarsTeam.reSpawnInvulnerability.remove(player.getUniqueId());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player)event.getEntity();
            IArena iArena = Arena.getArenaByPlayer(player);
            if (iArena != null) {
                if (iArena.getStatus() != GameStatus.playing) {
                    event.setCancelled(true);
                    return;
                }
                if (iArena.isSpectator(player) || iArena.isReSpawning(player)) {
                    event.setCancelled(true);
                    return;
                }
                Player player1 = null;
                boolean bool = false;
                if (event.getDamager() instanceof Player) {
                    player1 = (Player)event.getDamager();
                } else if (event.getDamager() instanceof Projectile) {
                    ProjectileSource projectileSource = ((Projectile)event.getDamager()).getShooter();
                    if (projectileSource instanceof Player) {
                        player1 = (Player)projectileSource;
                    } else {
                        return;
                    }
                    bool = true;
                } else if (event.getDamager() instanceof Player) {
                    player1 = (Player)event.getDamager();
                    if (iArena.isReSpawning(player1)) {
                        event.setCancelled(true);
                        return;
                    }
                } else if (event.getDamager() instanceof org.bukkit.entity.Silverfish || event.getDamager() instanceof org.bukkit.entity.IronGolem) {
                    LastHit lastHit = LastHit.getLastHit(player);
                    if (lastHit != null) {
                        lastHit.setDamager(event.getDamager());
                        lastHit.setTime(System.currentTimeMillis());
                    } else {
                        new LastHit(player, event.getDamager(), System.currentTimeMillis());
                    }
                }
                if (player1 != null) {
                    if (iArena.isSpectator(player1) || iArena.isReSpawning(player1.getUniqueId())) {
                        event.setCancelled(true);
                        return;
                    }
                    if (iArena.getTeam(player).equals(iArena.getTeam(player1))) {
                        if (!(event.getDamager() instanceof TNTPrimed))
                            event.setCancelled(true);
                        return;
                    }
                    if (BedWarsTeam.reSpawnInvulnerability.containsKey(player.getUniqueId())) {
                        if (BedWarsTeam.reSpawnInvulnerability.get(player.getUniqueId()) > System.currentTimeMillis()) {
                            event.setCancelled(true);
                            return;
                        }
                        BedWarsTeam.reSpawnInvulnerability.remove(player.getUniqueId());
                    }
                    BedWarsTeam.reSpawnInvulnerability.remove(player1.getUniqueId());
                    LastHit lastHit = LastHit.getLastHit(player);
                    if (lastHit != null) {
                        lastHit.setDamager(player1);
                        lastHit.setTime(System.currentTimeMillis());
                    } else {
                        new LastHit(player, player1, System.currentTimeMillis());
                    }
                    if (bool) {
                        ITeam iTeam = iArena.getTeam(player);
                        int resultHealth = (int) player.getHealth() - (int) event.getDamage();
                        Language language = Language.getPlayerLanguage(player1);
                        String str = language.m(Messages.PLAYER_HIT_BOW).replace("{amount}", "" + resultHealth).replace("{TeamColor}", iTeam.getColor().chat().toString()).replace("{TeamName}", iTeam.getDisplayName(language)).replace("{PlayerName}", ChatColor.stripColor(player.getDisplayName()));
                        player1.sendMessage(str);
                    }
                    if (iArena.getShowTime().containsKey(player))
                        Bukkit.getScheduler().runTask(BedWars.getInstance(), () -> {
                            for (Player player2 : iArena.getWorld().getPlayers())
                                BedWars.getInstance().getNms().showArmor(player, player2);
                            iArena.getShowTime().remove(player);
                            player.removePotionEffect(PotionEffectType.INVISIBILITY);
                            ITeam iTeam = iArena.getTeam(player);
                            Bukkit.getPluginManager().callEvent(new PlayerInvisibilityDrinkEvent(PlayerInvisibilityDrinkEvent.Type.REMOVED, iTeam, player, iArena));
                        });
                }
            }
        } else if (BedWars.getInstance().getNms().isDespawnable(event.getEntity())) {
            Player player;
            if (event.getDamager() instanceof Player) {
                player = (Player)event.getDamager();
            } else if (event.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile)event.getDamager();
                player = (Player)projectile.getShooter();
            } else if (event.getDamager() instanceof TNTPrimed) {
                TNTPrimed tNTPrimed = (TNTPrimed)event.getDamager();
                if (tNTPrimed.getSource() instanceof Player) {
                    player = (Player)tNTPrimed.getSource();
                } else {
                    return;
                }
            } else {
                return;
            }
            IArena iArena = Arena.getArenaByPlayer(player);
            if (iArena != null)
                if (iArena.isPlayer(player)) {
                    if (iArena.getTeam(player) == BedWars.getInstance().getNms().getDespawnablesList().get(event.getEntity().getUniqueId()).getTeam())
                        event.setCancelled(true);
                } else {
                    event.setCancelled(true);
                }
        }
    }
    
    @EventHandler
    public void onDeath(final PlayerDeathEvent event) {
        event.setDeathMessage(null);
        final Player entity = event.getEntity();
        Player killer = event.getEntity().getKiller();
        ITeam team = null;
        final IArena arenaByPlayer = Arena.getArenaByPlayer(entity);
        if (arenaByPlayer != null) {
            if (arenaByPlayer.isSpectator(entity)) {
                entity.spigot().respawn();
                return;
            }
            if (arenaByPlayer.getStatus() != GameStatus.playing) {
                entity.spigot().respawn();
                return;
            }
            final EntityDamageEvent lastDamageCause = event.getEntity().getLastDamageCause();
            final ITeam team2 = arenaByPlayer.getTeam(entity);
            if (arenaByPlayer.getStatus() != GameStatus.playing) {
                entity.spigot().respawn();
                return;
            }
            if (team2 == null) {
                entity.spigot().respawn();
                return;
            }
            String s = team2.isBedDestroyed() ? Messages.PLAYER_DIE_UNKNOWN_REASON_FINAL_KILL : Messages.PLAYER_DIE_UNKNOWN_REASON_REGULAR;
            PlayerKillEvent.PlayerKillCause playerKillCause = team2.isBedDestroyed() ? PlayerKillEvent.PlayerKillCause.UNKNOWN_FINAL_KILL : PlayerKillEvent.PlayerKillCause.UNKNOWN;
            if (lastDamageCause != null) {
                if (lastDamageCause.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
                    final LastHit lastHit = LastHit.getLastHit(entity);
                    if (lastHit != null && lastHit.getTime() >= System.currentTimeMillis() - 15000L) {
                        if (lastHit.getDamager() instanceof Player) killer = (Player)lastHit.getDamager();
                        if (killer != null && killer.getUniqueId().equals(entity.getUniqueId())) killer = null;
                    }
                    if (killer == null) {
                        s = (team2.isBedDestroyed() ? Messages.PLAYER_DIE_EXPLOSION_WITHOUT_SOURCE_FINAL_KILL : Messages.PLAYER_DIE_EXPLOSION_WITHOUT_SOURCE_REGULAR);
                    } else if (killer != entity) {
                        s = (team2.isBedDestroyed() ? Messages.PLAYER_DIE_EXPLOSION_WITH_SOURCE_FINAL_KILL : Messages.PLAYER_DIE_EXPLOSION_WITH_SOURCE_REGULAR_KILL);
                    } else {
                        s = (team2.isBedDestroyed() ? Messages.PLAYER_DIE_EXPLOSION_WITHOUT_SOURCE_FINAL_KILL : Messages.PLAYER_DIE_EXPLOSION_WITHOUT_SOURCE_REGULAR);
                    }
                    playerKillCause = (team2.isBedDestroyed() ? PlayerKillEvent.PlayerKillCause.EXPLOSION_FINAL_KILL : PlayerKillEvent.PlayerKillCause.EXPLOSION);
                } else if (lastDamageCause.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    final LastHit lastHit2 = LastHit.getLastHit(entity);
                    if (lastHit2 != null && lastHit2.getTime() >= System.currentTimeMillis() - 15000L) {
                        if (lastHit2.getDamager() instanceof Player) killer = (Player)lastHit2.getDamager();
                        if (killer != null && killer.getUniqueId().equals(entity.getUniqueId())) killer = null;
                    }
                    if (killer == null) {
                        s = (team2.isBedDestroyed() ? Messages.PLAYER_DIE_VOID_FALL_FINAL_KILL : Messages.PLAYER_DIE_VOID_FALL_REGULAR_KILL);
                    } else if (killer != entity) {
                        s = (team2.isBedDestroyed() ? Messages.PLAYER_DIE_KNOCKED_IN_VOID_FINAL_KILL : Messages.PLAYER_DIE_KNOCKED_IN_VOID_REGULAR_KILL);
                    } else {
                        s = (team2.isBedDestroyed() ? Messages.PLAYER_DIE_VOID_FALL_FINAL_KILL : Messages.PLAYER_DIE_VOID_FALL_REGULAR_KILL);
                    }
                    playerKillCause = (team2.isBedDestroyed() ? PlayerKillEvent.PlayerKillCause.VOID_FINAL_KILL : PlayerKillEvent.PlayerKillCause.VOID);
                }
                else if (lastDamageCause.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                    if (killer == null) {
                        final LastHit lastHit3 = LastHit.getLastHit(entity);
                        if (lastHit3 != null && lastHit3.getTime() >= System.currentTimeMillis() - 15000L && BedWars.getInstance().getNms().isDespawnable(lastHit3.getDamager())) {
                            final Despawnable despawnable = BedWars.getInstance().getNms().getDespawnablesList().get(lastHit3.getDamager().getUniqueId());
                            team = despawnable.getTeam();
                            s = ((despawnable.getEntity().getType() == EntityType.IRON_GOLEM) ? (team2.isBedDestroyed() ? Messages.PLAYER_DIE_IRON_GOLEM_FINAL_KILL : Messages.PLAYER_DIE_IRON_GOLEM_REGULAR) : (team2.isBedDestroyed() ? Messages.PLAYER_DIE_DEBUG_FINAL_KILL : Messages.PLAYER_DIE_DEBUG_REGULAR));
                            playerKillCause = (team2.isBedDestroyed() ? despawnable.getDeathFinalCause() : despawnable.getDeathRegularCause());
                            killer = null;
                        }
                    } else {
                        s = (team2.isBedDestroyed() ? Messages.PLAYER_DIE_PVP_FINAL_KILL : Messages.PLAYER_DIE_PVP_REGULAR_KILL);
                        playerKillCause = (team2.isBedDestroyed() ? PlayerKillEvent.PlayerKillCause.PVP_FINAL_KILL : PlayerKillEvent.PlayerKillCause.PVP);
                    }
                } else if (lastDamageCause.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                    if (killer != null) {
                        s = (team2.isBedDestroyed() ? Messages.PLAYER_DIE_SHOOT_FINAL_KILL : Messages.PLAYER_DIE_SHOOT_REGULAR);
                        playerKillCause = (team2.isBedDestroyed() ? PlayerKillEvent.PlayerKillCause.PLAYER_SHOOT_FINAL_KILL : PlayerKillEvent.PlayerKillCause.PLAYER_SHOOT);
                    }
                } else if (lastDamageCause.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    final LastHit lastHit4 = LastHit.getLastHit(entity);
                    if (lastHit4 != null && lastHit4.getTime() >= System.currentTimeMillis() - 10000L) {
                        if (lastHit4.getDamager() instanceof Player) killer = (Player)lastHit4.getDamager();
                        if (killer != null && killer.getUniqueId().equals(entity.getUniqueId())) killer = null;
                        if (killer != null) {
                            if (killer != entity) {
                                s = (team2.isBedDestroyed() ? Messages.PLAYER_DIE_KNOCKED_BY_FINAL_KILL : Messages.PLAYER_DIE_KNOCKED_BY_REGULAR_KILL);
                            } else {
                                s = (team2.isBedDestroyed() ? Messages.PLAYER_DIE_VOID_FALL_FINAL_KILL : Messages.PLAYER_DIE_VOID_FALL_REGULAR_KILL);
                            }
                        }
                        playerKillCause = (team2.isBedDestroyed() ? PlayerKillEvent.PlayerKillCause.PLAYER_PUSH_FINAL : PlayerKillEvent.PlayerKillCause.PLAYER_PUSH);
                    }
                }
            }
            if (killer != null) team = arenaByPlayer.getTeam(killer);
            String finalS = s;
            final PlayerKillEvent playerKillEvent = new PlayerKillEvent(arenaByPlayer, entity, killer, player -> Language.getMsg(player, finalS), playerKillCause);
            Bukkit.getPluginManager().callEvent(playerKillEvent);
            for (final Player player2 : arenaByPlayer.getPlayers()) {
                final Language playerLanguage = Language.getPlayerLanguage(player2);
                player2.sendMessage(playerKillEvent.getMessage().apply(player2).replace("{PlayerColor}", team2.getColor().chat().toString()).replace("{PlayerName}", entity.getDisplayName()).replace("{PlayerTeamName}", team2.getDisplayName(playerLanguage)).replace("{KillerColor}", (team == null) ? "" : team.getColor().chat().toString()).replace("{KillerName}", (killer == null) ? "" : killer.getDisplayName()).replace("{KillerTeamName}", (team == null) ? "" : team.getDisplayName(playerLanguage)));
            }
            for (final Player player3 : arenaByPlayer.getSpectators()) {
                final Language playerLanguage2 = Language.getPlayerLanguage(player3);
                player3.sendMessage(playerKillEvent.getMessage().apply(player3).replace("{PlayerColor}", team2.getColor().chat().toString()).replace("{PlayerName}", entity.getDisplayName()).replace("{KillerColor}", (team == null) ? "" : team.getColor().chat().toString()).replace("{PlayerTeamName}", team2.getDisplayName(playerLanguage2)).replace("{KillerName}", (killer == null) ? "" : killer.getDisplayName()).replace("{KillerTeamName}", (team == null) ? "" : team.getDisplayName(playerLanguage2)));
            }
            if (killer != null && !team2.equals(team) && !entity.equals(killer)) arenaByPlayer.addPlayerKill(killer, playerKillCause.isFinalKill(), entity);
            if (PlayerDrops.handlePlayerDrops(arenaByPlayer, entity, killer, team2, team, playerKillCause, event.getDrops())) event.getDrops().clear();
            Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> entity.spigot().respawn(), 3L);
            arenaByPlayer.addPlayerDeath(entity);
            final LastHit lastHit5 = LastHit.getLastHit(entity);
            if (lastHit5 != null) lastHit5.setDamager(null);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(final PlayerRespawnEvent playerRespawnEvent) {
        final IArena arenaByPlayer = Arena.getArenaByPlayer(playerRespawnEvent.getPlayer());
        if (arenaByPlayer == null) {
            if (SetupSession.getSession(playerRespawnEvent.getPlayer().getUniqueId()) != null) playerRespawnEvent.setRespawnLocation(playerRespawnEvent.getPlayer().getWorld().getSpawnLocation());
        } else {
            if (arenaByPlayer.isSpectator(playerRespawnEvent.getPlayer())) {
                playerRespawnEvent.setRespawnLocation(arenaByPlayer.getSpectatorLocation());
                final String iso = Language.getPlayerLanguage(playerRespawnEvent.getPlayer()).getIso();
                for (IGenerator iGenerator : arenaByPlayer.getOreGenerators()) iGenerator.updateHolograms(playerRespawnEvent.getPlayer(), iso);
                for (ITeam iTeam : arenaByPlayer.getTeams()) for (IGenerator iGenerator : iTeam.getGenerators()) iGenerator.updateHolograms(playerRespawnEvent.getPlayer(), iso);
                for (final ShopHologram shopHologram : ShopHologram.getShopHologram()) if (shopHologram.getA() == arenaByPlayer) shopHologram.updateForPlayer(playerRespawnEvent.getPlayer(), iso);
                arenaByPlayer.sendSpectatorCommandItems(playerRespawnEvent.getPlayer());
                return;
            }
            final ITeam team = arenaByPlayer.getTeam(playerRespawnEvent.getPlayer());
            if (team == null) {
                playerRespawnEvent.setRespawnLocation(arenaByPlayer.getReSpawnLocation());
                BedWars.getInstance().getLogger().severe(playerRespawnEvent.getPlayer().getName() + " re-spawn error en " + arenaByPlayer.getArenaName() + "[" + arenaByPlayer.getWorldName() + "] porque el team era NULL y no estaba especteando");
                BedWars.getInstance().getLogger().severe("Esto puede ser que sea causado por plugins externos.");
                arenaByPlayer.removePlayer(playerRespawnEvent.getPlayer(), false);
                arenaByPlayer.removeSpectator(playerRespawnEvent.getPlayer(), false);
                return;
            }
            if (team.isBedDestroyed()) {
                playerRespawnEvent.setRespawnLocation(arenaByPlayer.getSpectatorLocation());
                arenaByPlayer.addSpectator(playerRespawnEvent.getPlayer(), true, null);
                team.getMembers().remove(playerRespawnEvent.getPlayer());
                playerRespawnEvent.getPlayer().sendMessage(Language.getMsg(playerRespawnEvent.getPlayer(), Messages.PLAYER_DIE_ELIMINATED_CHAT));
                if (team.getMembers().isEmpty()) {
                    for (final Player player : arenaByPlayer.getWorld().getPlayers()) player.sendMessage(Language.getMsg(player, Messages.TEAM_ELIMINATED_CHAT).replace("{TeamColor}", team.getColor().chat().toString()).replace("{TeamName}", team.getDisplayName(Language.getPlayerLanguage(player))));
                    Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), arenaByPlayer::checkWinner, 40L);
                }
            } else {
                final int int1 = BedWars.getInstance().getMainConfig().getInt("countdowns.player-re-spawn");
                if (int1 > 1) {
                    playerRespawnEvent.setRespawnLocation(arenaByPlayer.getReSpawnLocation());
                    arenaByPlayer.startReSpawnSession(playerRespawnEvent.getPlayer(), int1);
                } else {
                    playerRespawnEvent.setRespawnLocation(team.getSpawn());
                    team.respawnMember(playerRespawnEvent.getPlayer());
                }
            }
        }
    }
    
    @EventHandler
    public void onMove(final PlayerMoveEvent event) {
        if (Arena.isInArena(event.getPlayer())) {
            final IArena arenaByPlayer = Arena.getArenaByPlayer(event.getPlayer());
            if (event.getFrom().getChunk() != event.getTo().getChunk()) {
                final String iso = Language.getPlayerLanguage(event.getPlayer()).getIso();
                for (IGenerator iGenerator : arenaByPlayer.getOreGenerators())
                    iGenerator.updateHolograms(event.getPlayer(), iso);
                for (ITeam iTeam : arenaByPlayer.getTeams())
                    for (IGenerator iGenerator : iTeam.getGenerators())
                        iGenerator.updateHolograms(event.getPlayer(), iso);
                for (final ShopHologram shopHologram : ShopHologram.getShopHologram())
                    if (shopHologram.getA() == arenaByPlayer) shopHologram.updateForPlayer(event.getPlayer(), iso);
                if (!arenaByPlayer.getShowTime().isEmpty()) {
                    for (final Map.Entry<Player, Integer> entry : arenaByPlayer.getShowTime().entrySet())
                        if (entry.getValue() > 1)
                            BedWars.getInstance().getNms().hideArmor(entry.getKey(), event.getPlayer());
                    if (arenaByPlayer.getShowTime().containsKey(event.getPlayer()))
                        for (Player player : arenaByPlayer.getPlayers())
                            BedWars.getInstance().getNms().hideArmor(event.getPlayer(), player);
                    if (arenaByPlayer.getShowTime().containsKey(event.getPlayer()))
                        for (Player player : arenaByPlayer.getSpectators())
                            BedWars.getInstance().getNms().hideArmor(event.getPlayer(), player);
                }
            }
            if (arenaByPlayer.isSpectator(event.getPlayer()) || arenaByPlayer.isReSpawning(event.getPlayer())) {
                if (event.getTo().getY() < 0.0) {
                    event.getPlayer().teleport(arenaByPlayer.isSpectator(event.getPlayer()) ? arenaByPlayer.getSpectatorLocation() : arenaByPlayer.getReSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    event.getPlayer().setAllowFlight(true);
                    event.getPlayer().setFlying(true);
                }
            } else if (arenaByPlayer.getStatus() == GameStatus.playing) {
                if (event.getPlayer().getLocation().getBlockY() <= arenaByPlayer.getYKillHeight())
                    BedWars.getInstance().getNms().voidKill(event.getPlayer());
                if (event.getFrom() != event.getTo()) {
                    Arena.afkCheck.remove(event.getPlayer().getUniqueId());
                    if (BedWars.getInstance().getApi().getAFKUtil().isPlayerAFK(event.getPlayer()))
                        BedWars.getInstance().getApi().getAFKUtil().setPlayerAFK(event.getPlayer(), false);
                }
            } else if (event.getPlayer().getLocation().getBlockY() <= 0) {
                final ITeam team2 = arenaByPlayer.getTeam(event.getPlayer());
                if (team2 != null) {
                    event.getPlayer().teleport(team2.getSpawn());
                } else {
                    event.getPlayer().teleport(arenaByPlayer.getSpectatorLocation());
                }
            }
        }
    }
    
    @EventHandler
    public void onProjHit(final ProjectileHitEvent event) {
        final Projectile entity = event.getEntity();
        if (entity == null) return;
        if (event.getEntity().getShooter() instanceof Player) {
            final IArena arenaByPlayer = Arena.getArenaByPlayer((Player)event.getEntity().getShooter());
            if (arenaByPlayer != null) {
                if (!arenaByPlayer.isPlayer((Player)event.getEntity().getShooter())) return;
                if (event.getEntity() instanceof Fireball) {
                    final Location location = event.getEntity().getLocation();
                    if (location == null) return;
                    event.getEntity().getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), 3.0f, false, true);
                } else {
                    String s = "";
                    if (entity instanceof Snowball) s = "silverfish";
                    if (!s.isEmpty()) spawnUtility(s, event.getEntity().getLocation(), arenaByPlayer.getTeam((Player)event.getEntity().getShooter()), (Player)event.getEntity().getShooter());
                }
            }
        }
    }
    
    @EventHandler
    public void onItemFrameDamage(final EntityDamageByEntityEvent event) {
        if (event.getEntity().getType() == EntityType.ITEM_FRAME) if (Arena.getArenaByIdentifier(event.getEntity().getWorld().getName()) != null) event.setCancelled(true);
    }
    
    @EventHandler
    public void onEntityDeath(final EntityDeathEvent event) {
        if (Arena.getArenaByIdentifier(event.getEntity().getLocation().getWorld().getName()) != null && (event.getEntityType() == EntityType.IRON_GOLEM || event.getEntityType() == EntityType.SILVERFISH)) {
            event.getDrops().clear();
            event.setDroppedExp(0);
        }
    }
    
    @EventHandler
    public void onEat(final PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == BedWars.getInstance().getNms().materialCake() && Arena.getArenaByIdentifier(event.getPlayer().getWorld().getName()) != null) event.setCancelled(true);
    }
    
    private static void spawnUtility(final String s, final Location location, final ITeam team, final Player player) {
        if ("silverfish".equalsIgnoreCase(s)) BedWars.getInstance().getNms().spawnSilverfish(location, team, BedWars.getInstance().getShopManager().getYml().getDouble("shop-specials.silverfish.speed"), BedWars.getInstance().getShopManager().getYml().getDouble("shop-specials.silverfish.health"), BedWars.getInstance().getShopManager().getInt("shop-specials.silverfish.despawn"), BedWars.getInstance().getShopManager().getYml().getDouble("shop-specials.silverfish.damage"));
    }
}
