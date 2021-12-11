package dev.eugenio.nasgarbedwars.arena;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.redis.RedisManager;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerKillEvent;
import dev.eugenio.nasgarbedwars.arena.tasks.GamePlayingTask;
import dev.eugenio.nasgarbedwars.arena.tasks.GameRestartingTask;
import dev.eugenio.nasgarbedwars.arena.tasks.GameStartingTask;
import dev.eugenio.nasgarbedwars.arena.tasks.ReJoinTask;
import dev.eugenio.nasgarbedwars.arena.team.TeamAssigner;
import dev.eugenio.nasgarbedwars.arena.upgrades.BaseListener;
import dev.eugenio.nasgarbedwars.configuration.ArenaConfig;
import dev.eugenio.nasgarbedwars.configuration.Sounds;
import dev.eugenio.nasgarbedwars.levels.internal.InternalLevel;
import dev.eugenio.nasgarbedwars.levels.internal.PerMinuteTask;
import dev.eugenio.nasgarbedwars.listeners.dropshandler.PlayerDrops;
import dev.eugenio.nasgarbedwars.sidebar.BedWarsScoreboard;
import dev.eugenio.nasgarbedwars.api.arena.GameStatus;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.NextEvent;
import dev.eugenio.nasgarbedwars.api.arena.generator.GeneratorType;
import dev.eugenio.nasgarbedwars.api.arena.generator.IGenerator;
import dev.eugenio.nasgarbedwars.api.arena.shop.ShopHologram;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeamAssigner;
import dev.eugenio.nasgarbedwars.api.arena.team.TeamColor;
import dev.eugenio.nasgarbedwars.api.entity.Despawnable;
import dev.eugenio.nasgarbedwars.api.events.gameplay.GameEndEvent;
import dev.eugenio.nasgarbedwars.api.events.gameplay.GameStatusChangeEvent;
import dev.eugenio.nasgarbedwars.api.events.gameplay.EventChangeEvent;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerJoinArenaEvent;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerLeaveArenaEvent;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerReJoinEvent;
import dev.eugenio.nasgarbedwars.api.events.server.ArenaDisableEvent;
import dev.eugenio.nasgarbedwars.api.events.server.ArenaEnableEvent;
import dev.eugenio.nasgarbedwars.api.events.server.ArenaRestartEvent;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.api.region.Region;
import dev.eugenio.nasgarbedwars.api.tasks.PlayingTask;
import dev.eugenio.nasgarbedwars.api.tasks.RestartingTask;
import dev.eugenio.nasgarbedwars.api.tasks.StartingTask;
import dev.eugenio.nasgarbedwars.arena.team.BedWarsTeam;
import dev.eugenio.nasgarbedwars.shop.ShopCache;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class Arena implements IArena {
    private static final HashMap<String, IArena> arenaByName;
    private static final HashMap<Player, IArena> arenaByPlayer;
    private static final HashMap<String, IArena> arenaByIdentifier;
    @Getter
    private static final LinkedList<IArena> arenas;
    private static int gamesBeforeRestart;
    public static HashMap<UUID, Integer> afkCheck;
    public static HashMap<UUID, Integer> magicMilk;
    @Getter
    private List<Player> players;
    @Getter
    private List<Player> spectators;
    @Getter
    private GameStatus status;
    private YamlConfiguration yml;
    @Getter
    private ArenaConfig cm;
    private int minPlayers;
    @Getter
    private int maxPlayers;
    @Getter
    private int maxInTeam;
    @Getter
    private int islandRadius;
    @Getter
    public int upgradeDiamondsCount;
    @Getter
    public int upgradeEmeraldsCount;
    public boolean allowSpectate;
    @Getter
    private World world;
    @Getter
    private String group;
    @Getter
    private String arenaName;
    @Getter
    private String worldName;
    @Getter
    private List<ITeam> teams;
    @Getter
    private LinkedList<org.bukkit.util.Vector> placed;
    private List<String> nextEvents;
    @Getter
    private List<Region> regionsList;
    @Getter
    private int renderDistance;
    @Getter
    private NextEvent nextEvent;
    private int diamondTier;
    private int emeraldTier;
    @Getter
    private ConcurrentHashMap<Player, Integer> respawnSessions;
    @Getter
    private ConcurrentHashMap<Player, Integer> showTime;
    @Getter
    private static final HashMap<Player, Location> playerLocation;
    private HashMap<String, Integer> playerKills;
    private HashMap<Player, Integer> playerBedsDestroyed;
    private HashMap<Player, Integer> playerFinalKills;
    private HashMap<Player, Integer> playerDeaths;
    private HashMap<Player, Integer> playerFinalKillDeaths;
    @Getter
    private StartingTask startingTask;
    @Getter
    private PlayingTask playingTask;
    @Getter
    private RestartingTask restartingTask;
    @Getter
    private List<IGenerator> oreGenerators;
    private PerMinuteTask perMinuteTask;
    private static final LinkedList<IArena> enableQueue;
    private Location respawnLocation;
    @Getter
    private Location spectatorLocation;
    @Getter
    private Location waitingLocation;
    @Getter
    private int yKillHeight;
    @Getter
    private Instant startTime;
    @Getter
    private ITeamAssigner teamAssigner;

    RedisManager redisManager = BedWars.getInstance().getRedisManager();

    public Arena(final String arenaName, final Player player) {
        for (IArena iArena : enableQueue) {
            if (!iArena.getArenaName().equalsIgnoreCase(arenaName)) continue;
            BedWars.getInstance().getLogger().severe("Intentado cargar arena " + arenaName + ", pero ya está en la fila de activación.");
            if (player != null)
                player.sendMessage(ChatColor.RED + "Intentado cargar arena " + arenaName + ", pero ya está en la fila de activación.");
            return;
        }
        if (Arena.getArenaByName(arenaName) != null) {
            BedWars.getInstance().getLogger().severe("Intentado cargar arena " + arenaName + ", pero ya está activado.");
            if (player != null)
                player.sendMessage(ChatColor.RED + "Intentado cargar arena " + arenaName + ", pero ya está activado.");
            return;
        }
        this.players = new ArrayList<>();
        this.spectators = new ArrayList<>();
        this.status = GameStatus.restarting;
        this.minPlayers = 2;
        this.maxPlayers = 10;
        this.maxInTeam = 1;
        this.islandRadius = 10;
        this.upgradeDiamondsCount = 0;
        this.upgradeEmeraldsCount = 0;
        this.allowSpectate = true;
        this.group = "Default";
        this.teams = new ArrayList<>();
        this.placed = new LinkedList<>();
        this.nextEvents = new ArrayList<>();
        this.regionsList = new ArrayList<>();
        this.nextEvent = NextEvent.DIAMOND_GENERATOR_TIER_II;
        this.diamondTier = 1;
        this.emeraldTier = 1;
        this.respawnSessions = new ConcurrentHashMap<>();
        this.showTime = new ConcurrentHashMap<>();
        this.playerKills = new HashMap<>();
        this.playerBedsDestroyed = new HashMap<>();
        this.playerFinalKills = new HashMap<>();
        this.playerDeaths = new HashMap<>();
        this.playerFinalKillDeaths = new HashMap<>();
        this.startingTask = null;
        this.playingTask = null;
        this.restartingTask = null;
        this.oreGenerators = new ArrayList<>();
        this.teamAssigner = new TeamAssigner();
        for (IArena iArena : Arena.enableQueue) {
            if (iArena.getArenaName().equalsIgnoreCase(arenaName)) {
                BedWars.getInstance().getLogger().severe("Intentando cargar arena " + arenaName + " pero ya está habilitada en la cola.");
                if (player != null)
                    player.sendMessage(ChatColor.RED + "Intentando cargar arena " + arenaName + " pero ya está habilitada en la cola.");
                return;
            }
        }
        if (getArenaByName(arenaName) != null) {
            BedWars.getInstance().getLogger().severe("Intentando cargar arena " + arenaName + " pero ya está habilitada.");
            if (player != null)
                player.sendMessage(ChatColor.RED + "Intentando cargar arena " + arenaName + " pero ya está habilitada.");
            return;
        }
        this.arenaName = arenaName;
        this.worldName = this.arenaName;
        this.cm = new ArenaConfig(BedWars.getInstance(), arenaName, BedWars.getInstance().getDataFolder().getPath() + "/Arenas");
        this.yml = this.cm.getYml();
        if (this.yml.get("Team") == null) {
            if (player != null) player.sendMessage("No has especificado ningún equipo para la arena: " + arenaName);
            BedWars.getInstance().getLogger().severe("No has especificado ningún equipo para la arena: " + arenaName);
            return;
        }
        if (this.yml.getConfigurationSection("Team").getKeys(false).size() < 2) {
            if (player != null)
                player.sendMessage(ChatColor.RED + "Necesitas establecer por lo menos 2 equipos en la arena: " + arenaName);
            BedWars.getInstance().getLogger().severe("Necesitas establecer por lo menos 2 equipos en la arena: " + arenaName);
            return;
        }
        this.maxInTeam = this.yml.getInt("maxInTeam");
        this.maxPlayers = this.yml.getConfigurationSection("Team").getKeys(false).size() * this.maxInTeam;
        this.minPlayers = this.yml.getInt("minPlayers");
        this.allowSpectate = this.yml.getBoolean("allowSpectate");
        this.islandRadius = this.yml.getInt("island-radius");
        if (BedWars.getInstance().getMainConfig().getYml().get("arenaGroups") != null && BedWars.getInstance().getMainConfig().getYml().getStringList("arenaGroups").contains(this.yml.getString("group")))
            this.group = this.yml.getString("group");
        if (!BedWars.getInstance().getApi().getRestoreAdapter().isWorld(arenaName)) {
            if (player != null) player.sendMessage(ChatColor.RED + "No hay ningún mapa llamado " + arenaName);
            BedWars.getInstance().getLogger().log(Level.WARNING, "No hay ningún mapa llamado " + arenaName);
            return;
        }
        boolean b = false;
        for (final String s : this.yml.getConfigurationSection("Team").getKeys(false)) {
            final String string = this.yml.getString("Team." + s + ".Color");
            if (string == null) continue;
            final String upperCase = string.toUpperCase();
            try {
                TeamColor.valueOf(upperCase);
            } catch (Exception ex) {
                if (player != null)
                    player.sendMessage(ChatColor.RED + "Color inválido en el equipo: " + s + " en arena: " + arenaName);
                BedWars.getInstance().getLogger().severe("Color inválido en el equipo: " + s + " en arena: " + arenaName);
                b = true;
            }
            for (final String s2 : Arrays.asList("Color", "Spawn", "Bed", "Shop", "Upgrade", "Iron", "Gold")) {
                if (this.yml.get("Team." + s + "." + s2) == null) {
                    if (player != null)
                        player.sendMessage(ChatColor.RED + s2 + " no seteado para el equipo " + s + " en: " + arenaName);
                    BedWars.getInstance().getLogger().severe(s2 + " no seteado para el equipo " + s + " en: " + arenaName);
                    b = true;
                }
            }
        }
        if (this.yml.get("generator.Diamond") == null) {
            if (player != null) player.sendMessage("No hay ningún generador de diamante en la arena: " + arenaName);
            BedWars.getInstance().getLogger().severe("No hay ningún generador de diamante en la arena: " + arenaName);
        }
        if (this.yml.get("generator.Emerald") == null) {
            if (player != null) player.sendMessage("No hay ningún generador de esmeralda en la arena: " + arenaName);
            BedWars.getInstance().getLogger().severe("No hay ningún generador de esmeralda en la arena: " + arenaName);
        }
        if (this.yml.get("waiting.Loc") == null) {
            if (player != null) player.sendMessage("No hay ningún punto de espera en la arena: " + arenaName);
            BedWars.getInstance().getLogger().severe("No hay ningún punto de espera en la arena: " + arenaName);
            return;
        }
        if (b) return;
        this.yKillHeight = this.yml.getInt("y-kill-height");
        addToEnableQueue(this);
        Language.saveIfNotExists(Messages.ARENA_DISPLAY_GROUP_PATH + this.getGroup().toLowerCase(), String.valueOf(this.getGroup().charAt(0)).toUpperCase() + this.group.substring(1).toLowerCase());
    }

    @Override
    public void init(final World world) {
        removeFromEnableQueue(this);
        BedWars.debug("Inicializando arena " + this.getArenaName() + " con mapa " + world.getName());
        this.world = world;
        this.worldName = world.getName();
        this.getConfig().setName(this.worldName);
        world.getEntities().stream().filter(entity -> entity.getType() != EntityType.PLAYER).filter(entity2 -> entity2.getType() != EntityType.PAINTING).filter(entity3 -> entity3.getType() != EntityType.ITEM_FRAME).forEach(Entity::remove);
        for (String value : getConfig().getList("game-rules")) {
            String[] arrayOfString = value.split(":");
            if (arrayOfString.length == 2) world.setGameRuleValue(arrayOfString[0], arrayOfString[1]);
        }
        world.setAutoSave(false);
        for (Entity entity : world.getEntities()) if (entity.getType() == EntityType.ARMOR_STAND && !((ArmorStand)entity).isVisible()) entity.remove();
        for (final String s : this.yml.getConfigurationSection("Team").getKeys(false)) {
            if (this.getTeam(s) != null) {
                BedWars.getInstance().getLogger().severe("Un equipo con el nombre: " + s + " ya ha sido cargado para la arena: " + this.getArenaName());
            } else {
                final BedWarsTeam bedWarsTeam = new BedWarsTeam(s, TeamColor.valueOf(this.yml.getString("Team." + s + ".Color").toUpperCase()), this.cm.getArenaLoc("Team." + s + ".Spawn"), this.cm.getArenaLoc("Team." + s + ".Bed"), this.cm.getArenaLoc("Team." + s + ".Shop"), this.cm.getArenaLoc("Team." + s + ".Upgrade"), this);
                this.teams.add(bedWarsTeam);
                bedWarsTeam.spawnGenerators();
            }
        }
        for (final String s2 : Arrays.asList("Diamond", "Emerald")) {
            if (this.yml.get("generator." + s2) != null) {
                for (final String s3 : this.yml.getStringList("generator." + s2)) {
                    final Location convertStringToArenaLocation = this.cm.convertStringToArenaLocation(s3);
                    if (convertStringToArenaLocation == null) {
                        BedWars.getInstance().getLogger().severe("Ubicación " + s2 + " inválida para el generador: " + s3);
                    } else {
                        this.oreGenerators.add(new OreGenerator(convertStringToArenaLocation, this, GeneratorType.valueOf(s2.toUpperCase()), null));
                    }
                }
            }
        }
        Arena.arenas.add(this);
        Arena.arenaByName.put(this.getArenaName(), this);
        Arena.arenaByIdentifier.put(this.worldName, this);
        world.getWorldBorder().setCenter(this.cm.getArenaLoc("waiting.Loc"));
        world.getWorldBorder().setSize(this.yml.getInt("worldBorder"));
        if (!this.getConfig().getYml().isSet("waiting.Pos1") && this.getConfig().getYml().isSet("waiting.Pos2")) BedWars.getInstance().getLogger().severe(ChatColor.YELLOW + "La posición 1 del lobby no está establecida. Sin esta no se podrá hacer el cuboide para eliminar el prelobby/limbo de arriba. Por favor, no te olvides de establecerlo");
        if (this.getConfig().getYml().isSet("waiting.Pos1") && !this.getConfig().getYml().isSet("waiting.Pos2")) BedWars.getInstance().getLogger().severe(ChatColor.YELLOW + "La posición 2 del lobby no está establecida. Sin esta no se podrá hacer el cuboide para eliminar el prelobby/limbo de arriba. Por favor, no te olvides de establecerlo");
        Bukkit.getPluginManager().callEvent(new ArenaEnableEvent(this));
        this.respawnLocation = this.cm.getArenaLoc("spectator-loc");
        if (this.respawnLocation == null) this.respawnLocation = this.cm.getArenaLoc("waiting.Loc");
        if (this.respawnLocation == null) this.respawnLocation = world.getSpawnLocation();
        this.spectatorLocation = this.cm.getArenaLoc("spectator-loc");
        if (this.spectatorLocation == null) this.spectatorLocation = this.cm.getArenaLoc("waiting.Loc");
        if (this.spectatorLocation == null) this.spectatorLocation = world.getSpawnLocation();
        this.waitingLocation = this.cm.getArenaLoc("waiting.Loc");
        if (this.waitingLocation == null) this.waitingLocation = world.getSpawnLocation();
        this.changeStatus(GameStatus.waiting);
        final NextEvent[] values = NextEvent.values();
        for (int length = values.length, i = 0; i < length; ++i) this.nextEvents.add(values[i].toString());
        this.upgradeDiamondsCount = BedWars.getInstance().getGeneratorConfig().getInt((BedWars.getInstance().getGeneratorConfig().getYml().get(this.getGroup() + "." + "diamond.tierII.start") == null) ? "Default.diamond.tierII.start" : (this.getGroup() + "." + "diamond.tierII.start"));
        this.upgradeEmeraldsCount = BedWars.getInstance().getGeneratorConfig().getInt((BedWars.getInstance().getGeneratorConfig().getYml().get(this.getGroup() + "." + "emerald.tierII.start") == null) ? "Default.emerald.tierII.start" : (this.getGroup() + "." + "emerald.tierII.start"));
        BedWars.getInstance().getLogger().info("Load done: " + this.getArenaName());
        final YamlConfiguration loadConfiguration = YamlConfiguration.loadConfiguration(new File("spigot.yml"));
        this.renderDistance = ((loadConfiguration.get("world-settings." + this.getWorldName() + ".entity-tracking-range.players") == null) ? loadConfiguration.getInt("world-settings.default.entity-tracking-range.players") : loadConfiguration.getInt("world-settings." + this.getWorldName() + ".entity-tracking-range.players"));

        redisManager.getPool().getResource().publish("BWArenaStatus", this.getArenaName() + ";" + "waiting");
    }

    @Override
    public boolean addPlayer(Player player, boolean b) {
        if (player == null) return false;
        BedWars.debug("Jugador " + player.getName() + " añadido a la arena: " + getArenaName());
        BaseListener.isOnABase.remove(player);
        if (getArenaByPlayer(player) != null) return false;
        if (this.status == GameStatus.waiting || (this.status == GameStatus.starting && this.startingTask != null && this.startingTask.getCountdown() > 1)) {
            if (this.players.size() >= this.maxPlayers) {
                TextComponent textComponent = new TextComponent(Language.getMsg(player, Messages.COMMAND_JOIN_DENIED_IS_FULL));
                player.spigot().sendMessage(textComponent);
                return false;
            }
            PlayerJoinArenaEvent playerJoinArenaEvent = new PlayerJoinArenaEvent(this, player, false);
            Bukkit.getPluginManager().callEvent(playerJoinArenaEvent);
            if (playerJoinArenaEvent.isCancelled()) return false;
            ReJoin reJoin = ReJoin.getPlayer(player);
            if (reJoin != null) reJoin.destroy(true);
            player.closeInventory();
            this.players.add(player);
            player.setFlying(false);
            player.setAllowFlight(false);
            for (Player player3 : this.players)
                player3.sendMessage(Language.getMsg(player, Messages.COMMAND_JOIN_PLAYER_JOIN_MSG).replace("{player}", player.getDisplayName()).replace("{on}", String.valueOf(getPlayers().size())).replace("{max}", String.valueOf(getMaxPlayers())));
            setArenaByPlayer(player, this);
            if (this.status == GameStatus.waiting) {
                byte b1 = 0, b2 = 0;
                if (this.players.size() >= this.minPlayers) {
                    changeStatus(GameStatus.starting);
                }
            }
            if (this.players.size() >= getMaxPlayers() / 2 && this.players.size() > this.minPlayers && this.startingTask != null && Bukkit.getScheduler().isCurrentlyRunning(this.startingTask.getTask()) && this.startingTask.getCountdown() > getConfig().getInt("countdowns.game-start-half-arena"))
                this.startingTask.setCountdown(BedWars.getInstance().getMainConfig().getInt("countdowns.game-start-half-arena"));
            player.teleport(getWaitingLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            BedWarsScoreboard.giveScoreboard(player, this, false);
            sendPreGameCommandItems(player);
            for (PotionEffect potionEffect : player.getActivePotionEffects())
                player.removePotionEffect(potionEffect.getType());
        } else if (this.status == GameStatus.playing) {
            addSpectator(player, false, null);
            return false;
        }
        player.getInventory().setArmorContents(null);
        Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
            BedWars.getInstance().getNms().sendPlayerSpawnPackets(player, this);
            for (Player player2 : Bukkit.getOnlinePlayers()) {
                if (isPlayer(player2)) {
                    BedWars.getInstance().getNms().spigotShowPlayer(player2, player2);
                    BedWars.getInstance().getNms().spigotShowPlayer(player2, player2);
                    continue;
                }
                BedWars.getInstance().getNms().spigotHidePlayer(player2, player2);
                BedWars.getInstance().getNms().spigotHidePlayer(player2, player2);
            }
            BedWars.getInstance().getNms().sendPlayerSpawnPackets(player, this);
        }, 17L);
        player.getEnderChest().clear();
        if (getPlayers().size() >= getMaxPlayers() &&
                this.startingTask != null &&
                Bukkit.getScheduler().isCurrentlyRunning(this.startingTask.getTask()) &&
                this.startingTask.getCountdown() > BedWars.getInstance().getMainConfig().getInt("countdowns.game-start-shortened"))
            this.startingTask.setCountdown(BedWars.getInstance().getMainConfig().getInt("countdowns.game-start-shortened"));
        //JoinNPC.updateNPCs(getGroup());
        return true;
    }

    @Override
    public boolean addSpectator(final Player player, final boolean b, final Location location) {
        if (this.allowSpectate || b || location != null) {
            BedWars.debug("Espectador " + player.getName() + " añadido a la arena: " + this.getArenaName());
            if (!b) {
                final PlayerJoinArenaEvent playerJoinArenaEvent = new PlayerJoinArenaEvent(this, player, true);
                Bukkit.getPluginManager().callEvent(playerJoinArenaEvent);
                if (playerJoinArenaEvent.isCancelled()) return false;
            }
            final ReJoin player2 = ReJoin.getPlayer(player);
            if (player2 != null) player2.destroy(true);
            player.closeInventory();
            this.spectators.add(player);
            this.players.remove(player);
            this.updateSpectatorCollideRule(player, false);
            if (!b) setArenaByPlayer(player, this);
            BedWarsScoreboard.giveScoreboard(player, this, false);
            BedWars.getInstance().getNms().setCollide(player, this, false);
            if (!b) {
                if (location == null) {
                    player.teleport(this.getSpectatorLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                } else {
                    player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
            }
            player.setGameMode(GameMode.ADVENTURE);
            Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
                player.setAllowFlight(true);
                player.setFlying(true);
            }, 5L);
            if (player.getPassenger() != null && player.getPassenger().getType() == EntityType.ARMOR_STAND) {
                player.getPassenger().remove();
            }
            Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
                for (Player player3 : Bukkit.getOnlinePlayers()) {
                    if (player3 == player) continue;
                    if (this.getSpectators().contains(player3)) {
                        BedWars.getInstance().getNms().spigotShowPlayer(player, player3);
                        continue;
                    }
                    if (this.getPlayers().contains(player3)) {
                        BedWars.getInstance().getNms().spigotHidePlayer(player, player3);
                        continue;
                    }
                    BedWars.getInstance().getNms().spigotHidePlayer(player, player3);
                    BedWars.getInstance().getNms().spigotHidePlayer(player3, player);
                }
                if (!b) {
                    if (location == null) {
                        player.teleport(this.getSpectatorLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    } else {
                        player.teleport(location);
                    }
                } else {
                    player.teleport(this.getSpectatorLocation());
                }
                player.setAllowFlight(true);
                player.setFlying(true);
                this.sendSpectatorCommandItems(player);
                player.getInventory().setArmorContents(null);
            }, 25L);
            player.sendMessage(Language.getMsg(player, Messages.COMMAND_JOIN_SPECTATOR_MSG).replace("{arena}", this.getDisplayName()));
            final String iso = Language.getPlayerLanguage(player).getIso();
            for (IGenerator iGenerator : this.getOreGenerators()) iGenerator.updateHolograms(player, iso);
            for (ITeam iTeam : this.getTeams()) for (IGenerator iGenerator : iTeam.getGenerators()) iGenerator.updateHolograms(player, iso);
            for (final ShopHologram shopHologram : ShopHologram.getShopHologram()) if (shopHologram.getA() == this) shopHologram.updateForPlayer(player, iso);
            this.showTime.remove(player);
            //JoinNPC.updateNPCs(this.getGroup());
            return true;
        }
        player.sendMessage(Language.getMsg(player, Messages.COMMAND_JOIN_SPECTATOR_DENIED_MSG));
        return false;
    }

    @Override
    public void removePlayer(Player paramPlayer, boolean paramBoolean) {
        BedWars.debug("Jugador " + paramPlayer.getName() + " removido de la arena: " + getArenaName());
        this.respawnSessions.remove(paramPlayer);
        ITeam iTeam = null;
        if (getStatus() == GameStatus.playing)
            for (ITeam iTeam1 : getTeams()) {
                if (iTeam1.isMember(paramPlayer)) {
                    iTeam = iTeam1;
                    iTeam1.getMembers().remove(paramPlayer);
                }
            }
        List<ShopCache.CachedItem> list = new ArrayList<>();
        if (ShopCache.getShopCache(paramPlayer.getUniqueId()) != null)
            list = ShopCache.getShopCache(paramPlayer.getUniqueId()).getCachedPermanents();
        LastHit lastHit1 = LastHit.getLastHit(paramPlayer);
        Player player = (lastHit1 == null) ? null : ((lastHit1.getDamager() instanceof Player) ? (Player) lastHit1.getDamager() : null);
        if (lastHit1 != null) if (lastHit1.getTime() > System.currentTimeMillis() - 13000L) player = null;
        Bukkit.getPluginManager().callEvent(new PlayerLeaveArenaEvent(paramPlayer, this, player));
        this.players.remove(paramPlayer);
        removeArenaByPlayer(paramPlayer, this);
        for (PotionEffect potionEffect : paramPlayer.getActivePotionEffects())
            paramPlayer.removePotionEffect(potionEffect.getType());
        if (paramPlayer.getPassenger() != null && paramPlayer.getPassenger().getType() == EntityType.ARMOR_STAND)
            paramPlayer.getPassenger().remove();
        boolean bool = false;
        if (this.status == GameStatus.starting && ((this.maxInTeam > this.players.size() && bool) || (this.players.size() < this.minPlayers && !bool))) {
            changeStatus(GameStatus.waiting);
            for (Player player1 : this.players) {
                player1.sendMessage(Language.getMsg(player1, Messages.ARENA_START_COUNTDOWN_STOPPED_INSUFF_PLAYERS_CHAT));
                player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
            }
        } else if (this.status == GameStatus.playing) {
            int b = 0;
            for (ITeam iTeam1 : getTeams()) if (iTeam1 != null && !iTeam1.getMembers().isEmpty()) b++;
            if (b == 1) {
                checkWinner();
                Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> changeStatus(GameStatus.restarting), 10L);
                if (iTeam != null && !iTeam.isBedDestroyed()) {
                    for (Player player1 : getPlayers())
                        player1.sendMessage(Language.getMsg(player1, Messages.TEAM_ELIMINATED_CHAT).replace("{TeamColor}", iTeam.getColor().chat().toString()).replace("{TeamName}", iTeam.getDisplayName(Language.getPlayerLanguage(player1))));
                    for (Player player1 : getSpectators())
                        player1.sendMessage(Language.getMsg(player1, Messages.TEAM_ELIMINATED_CHAT).replace("{TeamColor}", iTeam.getColor().chat().toString()).replace("{TeamName}", iTeam.getDisplayName(Language.getPlayerLanguage(player1))));
                }
            } else if (b == 0) {
                Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> changeStatus(GameStatus.restarting), 10L);
            } else {
                new ReJoin(paramPlayer, this, iTeam, list);
                if (iTeam != null) {
                    ITeam iTeam1 = getTeam(player);
                    if (player != null && isPlayer(player) && iTeam1 != null) {
                        String string;
                        PlayerKillEvent.PlayerKillCause playerKillCause;
                        if (iTeam.isBedDestroyed()) {
                            playerKillCause = PlayerKillEvent.PlayerKillCause.PLAYER_DISCONNECT_FINAL;
                            string = Messages.PLAYER_DIE_PVP_LOG_OUT_FINAL;
                        } else {
                            string = Messages.PLAYER_DIE_PVP_LOG_OUT_REGULAR;
                            playerKillCause = PlayerKillEvent.PlayerKillCause.PLAYER_DISCONNECT;
                        }
                        PlayerKillEvent playerKillEvent = new PlayerKillEvent(this, paramPlayer, player, playerParam -> Language.getMsg(playerParam, string), playerKillCause);
                        for (Player player1 : getPlayers()) {
                            Language language = Language.getPlayerLanguage(player1);
                            player1.sendMessage(playerKillEvent.getMessage().apply(player1)
                                    .replace("{PlayerTeamName}", iTeam.getDisplayName(language))
                                    .replace("{PlayerColor}", iTeam.getColor().chat().toString()).replace("{PlayerName}", paramPlayer.getDisplayName())
                                    .replace("{KillerColor}", iTeam1.getColor().chat().toString())
                                    .replace("{KillerName}", player.getDisplayName())
                                    .replace("{KillerTeamName}", iTeam1.getDisplayName(language)));
                        }
                        for (Player player1 : getSpectators()) {
                            Language language = Language.getPlayerLanguage(player1);
                            player1.sendMessage(playerKillEvent.getMessage().apply(player1)
                                    .replace("{PlayerTeamName}", iTeam.getDisplayName(language))
                                    .replace("{PlayerColor}", iTeam.getColor().chat().toString()).replace("{PlayerName}", paramPlayer.getDisplayName())
                                    .replace("{KillerColor}", iTeam1.getColor().chat().toString())
                                    .replace("{KillerName}", player.getDisplayName())
                                    .replace("{KillerTeamName}", iTeam1.getDisplayName(language)));
                        }
                        PlayerDrops.handlePlayerDrops(this, paramPlayer, player, iTeam, iTeam1, playerKillCause, new ArrayList<>(Arrays.asList(paramPlayer.getInventory().getContents())));
                    }
                }
            }
        }
        if (this.status == GameStatus.starting || this.status == GameStatus.waiting)
            for (Player player1 : this.players)
                player1.sendMessage(Language.getMsg(player1, Messages.COMMAND_LEAVE_MSG).replace("{player}", paramPlayer.getDisplayName()));
        Misc.moveToLobbyOrKick(paramPlayer, this, true);
        playerLocation.remove(paramPlayer);
        for (PotionEffect potionEffect : paramPlayer.getActivePotionEffects())
            paramPlayer.removePotionEffect(potionEffect.getType());
        Bukkit.getScheduler().scheduleSyncDelayedTask(BedWars.getInstance(), () -> {
            for (Player player3 : Bukkit.getOnlinePlayers()) {
                if (player3.equals(paramPlayer))
                    continue;
                if (getArenaByPlayer(player3) == null) {
                    BedWars.getInstance().getNms().spigotShowPlayer(paramPlayer, player3);
                    BedWars.getInstance().getNms().spigotShowPlayer(player3, paramPlayer);
                    continue;
                }
                BedWars.getInstance().getNms().spigotHidePlayer(paramPlayer, player3);
                BedWars.getInstance().getNms().spigotHidePlayer(player3, paramPlayer);
            }
            if (!paramBoolean) BedWarsScoreboard.giveScoreboard(paramPlayer, null, false);
        }, 5L);
        paramPlayer.setFlying(false);
        paramPlayer.setAllowFlight(false);
        if (this.status == GameStatus.restarting && ReJoin.exists(paramPlayer))
            if (ReJoin.getPlayer(paramPlayer).getArena() == this) ReJoin.getPlayer(paramPlayer).destroy(false);
        if (magicMilk.containsKey(paramPlayer.getUniqueId())) {
            int i = magicMilk.remove(paramPlayer.getUniqueId());
            if (i > 0) Bukkit.getScheduler().cancelTask(i);
        }
        this.showTime.remove(paramPlayer);
        //JoinNPC.updateNPCs(getGroup());
        LastHit lastHit2 = LastHit.getLastHit(paramPlayer);
        if (lastHit2 != null) lastHit2.remove();
    }

    @Override
    public void removeSpectator(Player paramPlayer, boolean paramBoolean) {
        BedWars.debug("Espectador " + paramPlayer.getName() + " removido en arena: " + getArenaName());
        Bukkit.getPluginManager().callEvent(new PlayerLeaveArenaEvent(paramPlayer, this, null));
        this.spectators.remove(paramPlayer);
        removeArenaByPlayer(paramPlayer, this);
        paramPlayer.getInventory().clear();
        paramPlayer.getInventory().setArmorContents(null);
        BedWars.getInstance().getNms().setCollide(paramPlayer, this, true);
        Misc.moveToLobbyOrKick(paramPlayer, this, true);
        playerLocation.remove(paramPlayer);
        Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.equals(paramPlayer)) continue;
                if (getArenaByPlayer(player) == null) {
                    BedWars.getInstance().getNms().spigotShowPlayer(paramPlayer, player);
                    BedWars.getInstance().getNms().spigotShowPlayer(player, paramPlayer);
                    continue;
                }
                BedWars.getInstance().getNms().spigotHidePlayer(paramPlayer, player);
                BedWars.getInstance().getNms().spigotHidePlayer(player, paramPlayer);
            }
            if (!paramBoolean) BedWarsScoreboard.giveScoreboard(paramPlayer, null, true);
        }, 10L);
        paramPlayer.setFlying(false);
        paramPlayer.setAllowFlight(false);
        if (ReJoin.exists(paramPlayer))
            if (ReJoin.getPlayer(paramPlayer).getArena() == this) ReJoin.getPlayer(paramPlayer).destroy(false);
        if (magicMilk.containsKey(paramPlayer.getUniqueId())) if (magicMilk.get(paramPlayer.getUniqueId()) > 0)
            Bukkit.getScheduler().cancelTask(magicMilk.get(paramPlayer.getUniqueId()));
        //JoinNPC.updateNPCs(getGroup());
    }

    @Override
    public boolean reJoin(final Player player) {
        final ReJoin player2 = ReJoin.getPlayer(player);
        if (player2 == null) return false;
        if (player2.getArena() != this) return false;
        if (!player2.canReJoin()) return false;
        if (player2.getTask() != null) player2.getTask().destroy();
        final PlayerReJoinEvent playerReJoinEvent = new PlayerReJoinEvent(player, this);
        Bukkit.getPluginManager().callEvent(playerReJoinEvent);
        if (playerReJoinEvent.isCancelled()) return false;
        for (final Player player3 : Bukkit.getOnlinePlayers()) {
            if (player3.equals(player)) continue;
            if (isInArena(player3)) continue;
            BedWars.getInstance().getNms().spigotHidePlayer(player3, player);
            BedWars.getInstance().getNms().spigotHidePlayer(player, player3);
        }
        player.closeInventory();
        this.players.add(player);
        for (final Player player4 : this.players)
            player4.sendMessage(Language.getMsg(player4, Messages.COMMAND_REJOIN_PLAYER_RECONNECTED).replace("{player}", player.getDisplayName()).replace("{on}", String.valueOf(this.getPlayers().size())).replace("{max}", String.valueOf(this.getMaxPlayers())));
        for (final Player player5 : this.spectators)
            player5.sendMessage(Language.getMsg(player5, Messages.COMMAND_REJOIN_PLAYER_RECONNECTED).replace("{player}", player.getDisplayName()).replace("{on}", String.valueOf(this.getPlayers().size())).replace("{max}", String.valueOf(this.getMaxPlayers())));
        setArenaByPlayer(player, this);
        Arena.playerLocation.put(player, player.getLocation());
        player.teleport(this.getSpectatorLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        player.getInventory().clear();
        final ShopCache shopCache = ShopCache.getShopCache(player.getUniqueId());
        if (shopCache != null) shopCache.destroy();
        final ShopCache shopCache2 = new ShopCache(player.getUniqueId());
        for (ShopCache.CachedItem cachedItem : player2.getPermanentsAndNonDowngradables())
            shopCache2.getCachedItems().add(cachedItem);
        player2.getBwt().reJoin(player);
        player2.destroy(false);
        BedWarsScoreboard.giveScoreboard(player, this, true);
        return true;
    }

    @Override
    public void disable() {
        for (Player player : new ArrayList<>(this.players)) this.removePlayer(player, false);
        for (Player player : new ArrayList<>(this.spectators)) this.removeSpectator(player, false);
        if (this.getRestartingTask() != null) this.getRestartingTask().cancel();
        if (this.getStartingTask() != null) this.getStartingTask().cancel();
        if (this.getPlayingTask() != null) this.getPlayingTask().cancel();
        BedWars.getInstance().getLogger().log(Level.WARNING, "Deshabilitando arena: " + this.getArenaName());
        for (Player player : this.getWorld().getPlayers())
            player.kickPlayer("No parece que debas de estar aquí. Quizá es un bug; solo vuélvete a unir y prueba a jugar de nuevo.");
        BedWars.getInstance().getApi().getRestoreAdapter().onDisable(this);
        Bukkit.getPluginManager().callEvent(new ArenaDisableEvent(this.getArenaName(), this.getWorldName()));
        this.destroyData();
    }

    @Override
    public void restart() {
        if (this.getRestartingTask() != null) this.getRestartingTask().cancel();
        if (this.getStartingTask() != null) this.getStartingTask().cancel();
        if (this.getPlayingTask() != null) this.getPlayingTask().cancel();
        BedWars.getInstance().getLogger().log(Level.FINE, "Reiniciando arena: " + this.getArenaName());
        Bukkit.getPluginManager().callEvent(new ArenaRestartEvent(this.getArenaName(), this.getWorldName()));
        for (Player player : this.getWorld().getPlayers())
            player.kickPlayer("No parece que debas de estar aquí. Quizá es un bug; solo vuélvete a unir y prueba a jugar de nuevo.");
        BedWars.getInstance().getApi().getRestoreAdapter().onRestart(this);
        this.destroyData();
    }

    public static IArena getArenaByName(final String s) {
        return Arena.arenaByName.get(s);
    }

    public static IArena getArenaByIdentifier(final String s) {
        return Arena.arenaByIdentifier.get(s);
    }

    public static IArena getArenaByPlayer(final Player player) {
        return Arena.arenaByPlayer.get(player);
    }

    @Override
    public String getDisplayStatus(final Language language) {
        String s = "";
        switch (this.status) {
            case waiting: {
                s = language.m(Messages.ARENA_STATUS_WAITING_NAME);
                break;
            }
            case starting: {
                s = language.m(Messages.ARENA_STATUS_STARTING_NAME);
                break;
            }
            case restarting: {
                s = language.m(Messages.ARENA_STATUS_RESTARTING_NAME);
                break;
            }
            case playing: {
                s = language.m(Messages.ARENA_STATUS_PLAYING_NAME);
                break;
            }
        }
        return s.replace("{full}", (this.getPlayers().size() == this.getMaxPlayers()) ? language.m(Messages.MEANING_FULL) : "");
    }

    @Override
    public String getDisplayGroup(final Player player) {
        return Language.getPlayerLanguage(player).m(Messages.ARENA_DISPLAY_GROUP_PATH + this.getGroup().toLowerCase());
    }

    @Override
    public String getDisplayGroup(final Language language) {
        return language.m(Messages.ARENA_DISPLAY_GROUP_PATH + this.getGroup().toLowerCase());
    }

    @Override
    public String getDisplayName() {
        return this.getConfig().getYml().getString("display-name", (Character.toUpperCase(this.arenaName.charAt(0)) + this.arenaName.substring(1)).replace("_", " ").replace("-", " ")).trim().isEmpty() ? (Character.toUpperCase(this.arenaName.charAt(0)) + this.arenaName.substring(1)).replace("_", " ").replace("-", " ") : this.getConfig().getString("display-name");
    }

    @Override
    public void setWorldName(final String worldName) {
        this.worldName = worldName;
    }

    @Override
    public void addPlacedBlock(final Block block) {
        if (block == null) return;
        this.placed.add(new org.bukkit.util.Vector(block.getX(), block.getY(), block.getZ()));
    }

    @Override
    public void removePlacedBlock(final Block block) {
        if (block == null) return;
        if (!this.isBlockPlaced(block)) return;
        this.placed.remove(new org.bukkit.util.Vector(block.getX(), block.getY(), block.getZ()));
    }

    @Override
    public boolean isBlockPlaced(final Block block) {
        for (final org.bukkit.util.Vector vector : this.getPlaced())
            if (vector.getX() == block.getX() && vector.getY() == block.getY() && vector.getZ() == block.getZ())
                return true;
        return false;
    }

    @Override
    public int getPlayerKills(final Player player, final boolean b) {
        if (b) return this.playerFinalKills.getOrDefault(player, 0);
        return this.playerKills.getOrDefault(player.getName(), 0);
    }

    @Override
    public int getPlayerBedsDestroyed(final Player player) {
        if (this.playerBedsDestroyed.containsKey(player)) return this.playerBedsDestroyed.get(player);
        return 0;
    }

    @Override
    public void setGroup(final String group) {
        this.group = group;
    }

    public static void setArenaByPlayer(final Player player, final IArena arena) {
        Arena.arenaByPlayer.put(player, arena);
        //JoinNPC.updateNPCs(arena.getGroup());
    }

    public static void setArenaByName(final IArena arena) {
        Arena.arenaByName.put(arena.getArenaName(), arena);
    }

    public static void removeArenaByName(final String s) {
        Arena.arenaByName.remove(s.replace("_clone", ""));
    }

    public static void removeArenaByPlayer(final Player player, final IArena arena) {
        Arena.arenaByPlayer.remove(player);
        //JoinNPC.updateNPCs(arena.getGroup());
    }

    @Override
    public void setStatus(final GameStatus status) {
        if (this.status != GameStatus.playing && status == GameStatus.playing) this.startTime = Instant.now();
        if (this.status == GameStatus.starting && status == GameStatus.waiting) {
            for (final Player player : this.getPlayers()) {
                final Language playerLanguage = Language.getPlayerLanguage(player);
                BedWars.getInstance().getNms().sendTitle(player, playerLanguage.m(Messages.ARENA_STATUS_START_COUNTDOWN_CANCELLED_TITLE), playerLanguage.m(Messages.ARENA_STATUS_START_COUNTDOWN_CANCELLED_SUB_TITLE), 0, 40, 0);
            }
        }
        this.status = status;
    }

    @Override
    public void changeStatus(final GameStatus status) {
        this.status = status;
        Bukkit.getPluginManager().callEvent(new GameStatusChangeEvent(this, status, status));
        final BukkitScheduler scheduler = Bukkit.getScheduler();
        if (this.startingTask != null && (scheduler.isCurrentlyRunning(this.startingTask.getTask()) || scheduler.isQueued(this.startingTask.getTask())))
            this.startingTask.cancel();
        this.startingTask = null;
        if (this.playingTask != null && (scheduler.isCurrentlyRunning(this.playingTask.getTask()) || scheduler.isQueued(this.playingTask.getTask())))
            this.playingTask.cancel();
        this.playingTask = null;
        if (this.restartingTask != null && (scheduler.isCurrentlyRunning(this.restartingTask.getTask()) || scheduler.isQueued(this.restartingTask.getTask())))
            this.restartingTask.cancel();
        this.restartingTask = null;
        this.players.forEach(player -> BedWarsScoreboard.giveScoreboard(player, this, false));
        this.spectators.forEach(player2 -> BedWarsScoreboard.giveScoreboard(player2, this, false));
        switch (status) {
            case starting:
                this.startingTask = new GameStartingTask(this);
                redisManager.getPool().getResource().publish("BWArenaStatus", this.getArenaName() + ";" + "starting");
                break;
            case playing:
                if (BedWars.getInstance().getLevels() instanceof InternalLevel)
                    this.perMinuteTask = new PerMinuteTask(this);
                this.playingTask = new GamePlayingTask(this);
                redisManager.getPool().getResource().publish("BWArenaStatus", this.getArenaName() + ";" + "ingame");
                break;
            case restarting:
                this.restartingTask = new GameRestartingTask(this);
                if (this.perMinuteTask != null) {
                    this.perMinuteTask.cancel();
                }
                redisManager.getPool().getResource().publish("BWArenaStatus", this.getArenaName() + ";" + "restarting");
                break;
        }
    }

    @Override
    public boolean isPlayer(final Player player) {
        return this.players.contains(player);
    }

    @Override
    public boolean isSpectator(final Player player) {
        return this.spectators.contains(player);
    }

    @Override
    public Location getReSpawnLocation() {
        return this.respawnLocation;
    }

    @Override
    public boolean isSpectator(final UUID uuid) {
        for (Player player : this.getSpectators()) if (player.getUniqueId().equals(uuid)) return true;
        return false;
    }

    @Override
    public boolean isReSpawning(final UUID uuid) {
        if (uuid == null) return false;
        for (Player player : this.respawnSessions.keySet()) if (player.getUniqueId().equals(uuid)) return true;
        return false;
    }

    @Override
    public void setAllowSpectate(final boolean allowSpectate) {
        this.allowSpectate = allowSpectate;
    }

    @Override
    public void addPlayerKill(final Player player, final boolean b, final Player player2) {
        if (player == null) return;
        if (this.playerKills.containsKey(player.getName())) {
            this.playerKills.replace(player.getName(), this.playerKills.get(player.getName()) + 1);
        } else {
            this.playerKills.put(player.getName(), 1);
        }
        if (b) {
            if (this.playerFinalKills.containsKey(player)) {
                this.playerFinalKills.replace(player, this.playerFinalKills.get(player) + 1);
            } else {
                this.playerFinalKills.put(player, 1);
            }
            this.playerFinalKillDeaths.put(player2, 1);
        }
    }

    @Override
    public void addPlayerBedDestroyed(final Player player) {
        if (this.playerBedsDestroyed.containsKey(player)) {
            this.playerBedsDestroyed.replace(player, this.playerBedsDestroyed.get(player) + 1);
            return;
        }
        this.playerBedsDestroyed.put(player, 1);
    }

    @Override
    public ArenaConfig getConfig() {
        return this.cm;
    }

    @Override
    public void sendPreGameCommandItems(final Player player) {
        if (BedWars.getInstance().getMainConfig().getYml().get("pre-game-items") == null) return;
        player.getInventory().clear();
        for (final String s : BedWars.getInstance().getMainConfig().getYml().getConfigurationSection("pre-game-items").getKeys(false)) {
            if (BedWars.getInstance().getMainConfig().getYml().get("pre-game-items.%path%.material".replace("%path%", s)) == null) {
                BedWars.getInstance().getLogger().severe("pre-game-items.%path%.material".replace("%path%", s) + " is not set!");
            } else if (BedWars.getInstance().getMainConfig().getYml().get("pre-game-items.%path%.data".replace("%path%", s)) == null) {
                BedWars.getInstance().getLogger().severe("pre-game-items.%path%.data".replace("%path%", s) + " is not set!");
            } else if (BedWars.getInstance().getMainConfig().getYml().get("pre-game-items.%path%.slot".replace("%path%", s)) == null) {
                BedWars.getInstance().getLogger().severe("pre-game-items.%path%.slot".replace("%path%", s) + " is not set!");
            } else if (BedWars.getInstance().getMainConfig().getYml().get("pre-game-items.%path%.enchanted".replace("%path%", s)) == null) {
                BedWars.getInstance().getLogger().severe("pre-game-items.%path%.enchanted".replace("%path%", s) + " is not set!");
            } else if (BedWars.getInstance().getMainConfig().getYml().get("pre-game-items.%path%.command".replace("%path%", s)) == null) {
                BedWars.getInstance().getLogger().severe("pre-game-items.%path%.command".replace("%path%", s) + " is not set!");
            } else {
                player.getInventory().setItem(BedWars.getInstance().getMainConfig().getInt("pre-game-items.%path%.slot".replace("%path%", s)), Misc.createItem(Material.valueOf(BedWars.getInstance().getMainConfig().getYml().getString("pre-game-items.%path%.material".replace("%path%", s))), (byte) BedWars.getInstance().getMainConfig().getInt("pre-game-items.%path%.data".replace("%path%", s)), BedWars.getInstance().getMainConfig().getBoolean("pre-game-items.%path%.enchanted".replace("%path%", s)), Language.getMsg(player, "pre-game-items-%path%-name".replace("%path%", s)), Language.getList(player, "pre-game-items-%path%-lore".replace("%path%", s)), player, "RUNCOMMAND", BedWars.getInstance().getMainConfig().getYml().getString("pre-game-items.%path%.command".replace("%path%", s))));
            }
        }
    }

    @Override
    public void sendSpectatorCommandItems(final Player player) {
        if (BedWars.getInstance().getMainConfig().getYml().get("spectator-items") == null) return;
        player.getInventory().clear();
        for (final String s : BedWars.getInstance().getMainConfig().getYml().getConfigurationSection("spectator-items").getKeys(false)) {
            if (BedWars.getInstance().getMainConfig().getYml().get("spectator-items.%path%.material".replace("%path%", s)) == null) {
                BedWars.getInstance().getLogger().severe("spectator-items.%path%.material".replace("%path%", s) + " is not set!");
            } else if (BedWars.getInstance().getMainConfig().getYml().get("spectator-items.%path%.data".replace("%path%", s)) == null) {
                BedWars.getInstance().getLogger().severe("spectator-items.%path%.data".replace("%path%", s) + " is not set!");
            } else if (BedWars.getInstance().getMainConfig().getYml().get("spectator-items.%path%.slot".replace("%path%", s)) == null) {
                BedWars.getInstance().getLogger().severe("spectator-items.%path%.slot".replace("%path%", s) + " is not set!");
            } else if (BedWars.getInstance().getMainConfig().getYml().get("spectator-items.%path%.enchanted".replace("%path%", s)) == null) {
                BedWars.getInstance().getLogger().severe("spectator-items.%path%.enchanted".replace("%path%", s) + " is not set!");
            } else if (BedWars.getInstance().getMainConfig().getYml().get("spectator-items.%path%.command".replace("%path%", s)) == null) {
                BedWars.getInstance().getLogger().severe("spectator-items.%path%.command".replace("%path%", s) + " is not set!");
            } else {
                player.getInventory().setItem(BedWars.getInstance().getMainConfig().getInt("spectator-items.%path%.slot".replace("%path%", s)), Misc.createItem(Material.valueOf(BedWars.getInstance().getMainConfig().getYml().getString("spectator-items.%path%.material".replace("%path%", s))), (byte) BedWars.getInstance().getMainConfig().getInt("spectator-items.%path%.data".replace("%path%", s)), BedWars.getInstance().getMainConfig().getBoolean("spectator-items.%path%.enchanted".replace("%path%", s)), Language.getMsg(player, "spectator-items-%path%-name".replace("%path%", s)), Language.getList(player, "spectator-items-%path%-lore".replace("%path%", s)), player, "RUNCOMMAND", BedWars.getInstance().getMainConfig().getYml().getString("spectator-items.%path%.command".replace("%path%", s))));
            }
        }
    }

    public static boolean isInArena(final Player player) {
        return Arena.arenaByPlayer.containsKey(player);
    }

    @Override
    public ITeam getTeam(final Player player) {
        for (final ITeam team : this.getTeams()) if (team.isMember(player)) return team;
        return null;
    }

    @Override
    public ITeam getExTeam(final UUID uuid) {
        for (final ITeam team : this.getTeams()) if (team.wasMember(uuid)) return team;
        return null;
    }

    @Deprecated
    @Override
    public ITeam getPlayerTeam(final String s) {
        for (final ITeam team : this.getTeams()) {
            for (Player player : team.getMembersCache()) if (player.getName().equals(s)) return team;
        }
        return null;
    }

    @Override
    public void checkWinner() {
        if (getStatus() != GameStatus.restarting) {
            int i = getTeams().size();
            int b = 0;
            ITeam iTeam = null;
            for (ITeam iTeam1 : getTeams()) {
                if (iTeam1.getMembers().isEmpty()) {
                    b++;
                    continue;
                }
                iTeam = iTeam1;
            }
            if (i - b == 1) {
                if (iTeam != null) {
                    if (!iTeam.getMembers().isEmpty())
                        for (Player player : iTeam.getMembers()) {
                            if (!player.isOnline()) continue;
                            player.getInventory().clear();
                        }
                    String str1 = "";
                    String str2 = "";
                    String str3 = "";
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Player player : iTeam.getMembersCache()) {
                        if (player.getWorld().equals(getWorld()))
                            BedWars.getInstance().getNms().sendTitle(player, Language.getMsg(player, Messages.GAME_END_VICTORY_PLAYER_TITLE), null, 0, 70, 0);
                        if (!stringBuilder.toString().contains(player.getDisplayName()))
                            stringBuilder.append(player.getDisplayName()).append(" ");
                    }
                    if (stringBuilder.toString().endsWith(" "))
                        stringBuilder = new StringBuilder(stringBuilder.substring(0, stringBuilder.length() - 1));
                    int j = 0, k = 0, m = 0;
                    if (!this.playerKills.isEmpty()) {
                        LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>();
                        this.playerKills.entrySet()
                                .stream()
                                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                .forEachOrdered(paramEntry -> linkedHashMap.put(paramEntry.getKey(), paramEntry.getValue()));
                        int b1 = 0;
                        for (Map.Entry<Object, Object> entry : linkedHashMap.entrySet()) {
                            if (b1 == 0) {
                                str1 = (String) entry.getKey();
                                Player player = Bukkit.getPlayerExact((String) entry.getKey());
                                if (player != null) str1 = player.getDisplayName();
                                j = (Integer) entry.getValue();
                            } else if (b1 == 1) {
                                str2 = (String) entry.getKey();
                                Player player = Bukkit.getPlayerExact((String) entry.getKey());
                                if (player != null) str2 = player.getDisplayName();
                                k = (Integer) entry.getValue();
                            } else if (b1 == 2) {
                                str3 = (String) entry.getKey();
                                Player player = Bukkit.getPlayerExact((String) entry.getKey());
                                if (player != null) str3 = player.getDisplayName();
                                m = (Integer) entry.getValue();
                                break;
                            }
                            b1++;
                        }
                    }
                    for (Player player : this.world.getPlayers()) {
                        player.sendMessage(Language.getMsg(player, Messages.GAME_END_TEAM_WON_CHAT).replace("{TeamColor}", iTeam.getColor().chat().toString()).replace("{TeamName}", iTeam.getDisplayName(Language.getPlayerLanguage(player))));
                        if (!iTeam.getMembers().contains(player))
                            BedWars.getInstance().getNms().sendTitle(player, Language.getMsg(player, Messages.GAME_END_GAME_OVER_PLAYER_TITLE), null, 0, 70, 0);
                        for (String str4 : Language.getList(player, Messages.GAME_END_TOP_PLAYER_CHAT)) {
                            String str5 = str4.replace("{firstName}", str1.isEmpty() ? Language.getMsg(player, Messages.MEANING_NOBODY) : str1).replace("{firstKills}", String.valueOf(j)).replace("{secondName}", str2.isEmpty() ? Language.getMsg(player, Messages.MEANING_NOBODY) : str2).replace("{secondKills}", String.valueOf(k)).replace("{thirdName}", str3.isEmpty() ? Language.getMsg(player, Messages.MEANING_NOBODY) : str3).replace("{thirdKills}", String.valueOf(m)).replace("{winnerFormat}", (getMaxInTeam() > 1) ? Language.getMsg(player, Messages.FORMATTING_TEAM_WINNER_FORMAT).replace("{members}", stringBuilder.toString()) : Language.getMsg(player, Messages.FORMATTING_SOLO_WINNER_FORMAT).replace("{members}", stringBuilder.toString())).replace("{TeamColor}", iTeam.getColor().chat().toString()).replace("{TeamName}", iTeam.getDisplayName(Language.getPlayerLanguage(player)));
                            player.sendMessage(str5);
                        }
                    }
                }
                changeStatus(GameStatus.restarting);
                ArrayList<UUID> arrayList1 = new ArrayList<>(), arrayList2 = new ArrayList<>(), arrayList3 = new ArrayList<>();
                for (Player player : getPlayers()) arrayList3.add(player.getUniqueId());
                if (iTeam != null) for (Player player : iTeam.getMembersCache()) arrayList1.add(player.getUniqueId());
                for (ITeam iTeam1 : getTeams()) {
                    if (iTeam != null && iTeam1 == iTeam) continue;
                    for (Player player : iTeam1.getMembersCache()) arrayList2.add(player.getUniqueId());
                }
                Bukkit.getPluginManager().callEvent(new GameEndEvent(this, arrayList1, arrayList2, iTeam, arrayList3));
            }
            if (this.players.size() == 0 && getStatus() != GameStatus.restarting) changeStatus(GameStatus.restarting);
            if (this.players.size() <= 1) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(BedWars.getInstance(), () -> {
                    for (Player player : Bukkit.getOnlinePlayers()) player.kickPlayer("§eReiniciando");
                    if ((Bukkit.getOnlinePlayers().size()) >= 1) for (Player player : Bukkit.getOnlinePlayers()) player.kickPlayer(Messages.ARENA_STATUS_RESTARTING_NAME);
                    Bukkit.shutdown();
                }, 200L);
            }
        }
    }

    @Override
    public boolean isAllowSpectate() {
        return this.allowSpectate;
    }

    @Override
    public void addPlayerDeath(final Player player) {
        if (this.playerDeaths.containsKey(player)) {
            this.playerDeaths.replace(player, this.playerDeaths.get(player) + 1);
        } else {
            this.playerDeaths.put(player, 1);
        }
    }

    @Override
    public void setNextEvent(final NextEvent nextEvent) {
        if (this.nextEvent != null) {
            Sounds.playSound(this.nextEvent.getSoundPath(), this.getPlayers());
            Sounds.playSound(this.nextEvent.getSoundPath(), this.getSpectators());
        }
        Bukkit.getPluginManager().callEvent(new EventChangeEvent(this, nextEvent, this.nextEvent));
        this.nextEvent = nextEvent;
    }

    @Override
    public void updateNextEvent() {
        BedWars.debug("---");
        BedWars.debug("updateNextEvent called");
        if (this.nextEvent == NextEvent.EMERALD_GENERATOR_TIER_II && this.upgradeEmeraldsCount == 0) {
            final int int1 = BedWars.getInstance().getGeneratorConfig().getInt((BedWars.getInstance().getGeneratorConfig().getYml().get(this.getGroup() + "." + "emerald.tierIII.start") == null) ? "Default.emerald.tierIII.start" : (this.getGroup() + "." + "emerald.tierIII.start"));
            if (this.upgradeDiamondsCount < int1 && this.diamondTier == 1) {
                this.setNextEvent(NextEvent.DIAMOND_GENERATOR_TIER_II);
            } else if (this.upgradeDiamondsCount < int1 && this.diamondTier == 2) {
                this.setNextEvent(NextEvent.DIAMOND_GENERATOR_TIER_III);
            } else {
                this.setNextEvent(NextEvent.EMERALD_GENERATOR_TIER_III);
            }
            this.upgradeEmeraldsCount = int1;
            this.emeraldTier = 2;
            this.sendEmeraldsUpgradeMessages();
            for (final IGenerator generator : this.getOreGenerators()) {
                if (generator.getType() == GeneratorType.EMERALD && generator.getBwt() == null) generator.upgrade();
            }
        } else if (this.nextEvent == NextEvent.DIAMOND_GENERATOR_TIER_II && this.upgradeDiamondsCount == 0) {
            final int int2 = BedWars.getInstance().getGeneratorConfig().getInt((BedWars.getInstance().getGeneratorConfig().getYml().get(this.getGroup() + "." + "diamond.tierIII.start") == null) ? "Default.diamond.tierIII.start" : (this.getGroup() + "." + "diamond.tierIII.start"));
            if (this.upgradeEmeraldsCount < int2 && this.emeraldTier == 1) {
                this.setNextEvent(NextEvent.EMERALD_GENERATOR_TIER_II);
            } else if (this.upgradeEmeraldsCount < int2 && this.emeraldTier == 2) {
                this.setNextEvent(NextEvent.EMERALD_GENERATOR_TIER_III);
            } else {
                this.setNextEvent(NextEvent.DIAMOND_GENERATOR_TIER_III);
            }
            this.upgradeDiamondsCount = int2;
            this.diamondTier = 2;
            this.sendDiamondsUpgradeMessages();
            for (final IGenerator generator2 : this.getOreGenerators())
                if (generator2.getType() == GeneratorType.DIAMOND && generator2.getBwt() == null) generator2.upgrade();
        } else if (this.nextEvent == NextEvent.EMERALD_GENERATOR_TIER_III && this.upgradeEmeraldsCount == 0) {
            this.emeraldTier = 3;
            this.sendEmeraldsUpgradeMessages();
            if (this.diamondTier == 1 && this.upgradeDiamondsCount > 0) {
                this.setNextEvent(NextEvent.DIAMOND_GENERATOR_TIER_II);
            } else if (this.diamondTier == 2 && this.upgradeDiamondsCount > 0) {
                this.setNextEvent(NextEvent.DIAMOND_GENERATOR_TIER_III);
            } else {
                this.setNextEvent(NextEvent.BEDS_DESTROY);
            }
            for (final IGenerator generator3 : this.getOreGenerators())
                if (generator3.getType() == GeneratorType.EMERALD && generator3.getBwt() == null) generator3.upgrade();
        } else if (this.nextEvent == NextEvent.DIAMOND_GENERATOR_TIER_III && this.upgradeDiamondsCount == 0) {
            this.diamondTier = 3;
            this.sendDiamondsUpgradeMessages();
            if (this.emeraldTier == 1 && this.upgradeEmeraldsCount > 0) {
                this.setNextEvent(NextEvent.EMERALD_GENERATOR_TIER_II);
            } else if (this.emeraldTier == 2 && this.upgradeEmeraldsCount > 0) {
                this.setNextEvent(NextEvent.EMERALD_GENERATOR_TIER_III);
            } else {
                this.setNextEvent(NextEvent.BEDS_DESTROY);
            }
            for (final IGenerator generator4 : this.getOreGenerators())
                if (generator4.getType() == GeneratorType.DIAMOND && generator4.getBwt() == null) generator4.upgrade();
        } else if (this.nextEvent == NextEvent.BEDS_DESTROY && this.getPlayingTask().getBedsDestroyCountdown() == 0) {
            this.setNextEvent(NextEvent.ENDER_DRAGON);
        } else if (this.nextEvent == NextEvent.ENDER_DRAGON && this.getPlayingTask().getDragonSpawnCountdown() == 0) {
            this.setNextEvent(NextEvent.GAME_END);
        }
        BedWars.debug("---");
        BedWars.debug(this.nextEvent.toString());
    }

    public static HashMap<Player, IArena> getArenaByPlayer() {
        return Arena.arenaByPlayer;
    }

    public static int getPlayers(final String s) {
        int n = 0;
        for (final String s2 : s.split("\\+"))
            for (final IArena arena : getArenas())
                if (arena.getGroup().equalsIgnoreCase(s2)) n += arena.getPlayers().size();
        return n;
    }

    @Override
    public ITeam getTeam(final String s) {
        for (final ITeam team : this.getTeams()) if (team.getName().equals(s)) return team;
        return null;
    }

    @Override
    public void updateSpectatorCollideRule(final Player player, final boolean b) {
        if (!this.isSpectator(player)) return;
        for (final BedWarsScoreboard bedWarsScoreboard : BedWarsScoreboard.getScoreboards().values())
            if (bedWarsScoreboard.getArena() == this) bedWarsScoreboard.updateSpectator(player, b);
    }

    public static boolean joinRandomArena(Player paramPlayer) {
        List<IArena> list = getSorted(getArenas());
        int b = 1;
        for (IArena iArena : list)
            if (iArena.getPlayers().size() != iArena.getMaxPlayers() && iArena.getMaxPlayers() - iArena.getPlayers().size() >= b && iArena.addPlayer(paramPlayer, false))
                break;
        return true;
    }

    public static List<IArena> getSorted(List<IArena> paramList) {
        ArrayList<IArena> arrayList = new ArrayList<>(paramList);
        arrayList.sort(new Comparator<IArena>() {
            public int compare(IArena param1IArena1, IArena param1IArena2) {
                if (param1IArena1.getStatus() == GameStatus.starting && param1IArena2.getStatus() == GameStatus.starting)
                    return Integer.compare(param1IArena2.getPlayers().size(), param1IArena1.getPlayers().size());
                if (param1IArena1.getStatus() == GameStatus.starting && param1IArena2.getStatus() != GameStatus.starting)
                    return -1;
                if (param1IArena2.getStatus() == GameStatus.starting && param1IArena1.getStatus() != GameStatus.starting)
                    return 1;
                if (param1IArena1.getStatus() == GameStatus.waiting && param1IArena2.getStatus() == GameStatus.waiting)
                    return Integer.compare(param1IArena2.getPlayers().size(), param1IArena1.getPlayers().size());
                if (param1IArena1.getStatus() == GameStatus.waiting && param1IArena2.getStatus() != GameStatus.waiting)
                    return -1;
                if (param1IArena2.getStatus() == GameStatus.waiting && param1IArena1.getStatus() != GameStatus.waiting)
                    return 1;
                if (param1IArena1.getStatus() == GameStatus.playing && param1IArena2.getStatus() == GameStatus.playing)
                    return 0;
                if (param1IArena1.getStatus() == GameStatus.playing && param1IArena2.getStatus() != GameStatus.playing)
                    return -1;
                return 1;
            }

            public boolean equals(Object object) {
                return object instanceof IArena;
            }
        });
        return arrayList;
    }

    public static boolean joinRandomFromGroup(Player paramPlayer, String paramString) {
        List<IArena> list = getSorted(getArenas());
        int b = 1;
        String[] arrayOfString = paramString.split("\\+");
        for (IArena iArena : list) {
            if (iArena.getPlayers().size() == iArena.getMaxPlayers()) continue;
            for (String str : arrayOfString) {
                if (iArena.getGroup().equalsIgnoreCase(str) && iArena.getMaxPlayers() - iArena.getPlayers().size() >= b && iArena.addPlayer(paramPlayer, false))
                    return true;
            }
        }
        return false;
    }

    @Override
    public List<String> getNextEvents() {
        return new ArrayList<>(this.nextEvents);
    }

    @Override
    public int getPlayerDeaths(final Player player, final boolean b) {
        if (b) return this.playerFinalKillDeaths.getOrDefault(player, 0);
        return this.playerDeaths.getOrDefault(player, 0);
    }

    @Override
    public void sendDiamondsUpgradeMessages() {
        for (final Player player : this.getPlayers())
            player.sendMessage(Language.getMsg(player, Messages.GENERATOR_UPGRADE_CHAT_ANNOUNCEMENT).replace("{generatorType}", Language.getMsg(player, Messages.GENERATOR_HOLOGRAM_TYPE_DIAMOND)).replace("{tier}", Language.getMsg(player, (this.diamondTier == 2) ? Messages.FORMATTING_GENERATOR_TIER2 : Messages.FORMATTING_GENERATOR_TIER3)));
        for (final Player player2 : this.getSpectators())
            player2.sendMessage(Language.getMsg(player2, Messages.GENERATOR_UPGRADE_CHAT_ANNOUNCEMENT).replace("{generatorType}", Language.getMsg(player2, Messages.GENERATOR_HOLOGRAM_TYPE_DIAMOND)).replace("{tier}", Language.getMsg(player2, (this.diamondTier == 2) ? Messages.FORMATTING_GENERATOR_TIER2 : Messages.FORMATTING_GENERATOR_TIER3)));
    }

    @Override
    public void sendEmeraldsUpgradeMessages() {
        for (final Player player : this.getPlayers())
            player.sendMessage(Language.getMsg(player, Messages.GENERATOR_UPGRADE_CHAT_ANNOUNCEMENT).replace("{generatorType}", Language.getMsg(player, Messages.GENERATOR_HOLOGRAM_TYPE_EMERALD)).replace("{tier}", Language.getMsg(player, (this.emeraldTier == 2) ? Messages.FORMATTING_GENERATOR_TIER2 : Messages.FORMATTING_GENERATOR_TIER3)));
        for (final Player player2 : this.getSpectators())
            player2.sendMessage(Language.getMsg(player2, Messages.GENERATOR_UPGRADE_CHAT_ANNOUNCEMENT).replace("{generatorType}", Language.getMsg(player2, Messages.GENERATOR_HOLOGRAM_TYPE_EMERALD)).replace("{tier}", Language.getMsg(player2, (this.emeraldTier == 2) ? Messages.FORMATTING_GENERATOR_TIER2 : Messages.FORMATTING_GENERATOR_TIER3)));
    }

    public static int getGamesBeforeRestart() {
        return Arena.gamesBeforeRestart;
    }

    public static void setGamesBeforeRestart(final int gamesBeforeRestart) {
        Arena.gamesBeforeRestart = gamesBeforeRestart;
    }

    public static LinkedList<IArena> getEnableQueue() {
        return Arena.enableQueue;
    }

    @Override
    public void destroyData() {
        this.destroyReJoins();
        if (this.worldName != null) Arena.arenaByIdentifier.remove(this.worldName);
        Arena.arenas.remove(this);
        for (final ReJoinTask reJoinTask : ReJoinTask.getReJoinTasks())
            if (reJoinTask.getArena() == this) reJoinTask.destroy();
        for (final Despawnable despawnable : new ArrayList<>(BedWars.getInstance().getNms().getDespawnablesList().values()))
            if (despawnable.getTeam().getArena() == this) despawnable.destroy();
        Arena.arenaByName.remove(this.arenaName);
        Arena.arenaByPlayer.entrySet().removeIf(entry -> entry.getValue() == this);
        this.players = null;
        this.spectators = null;
        this.yml = null;
        this.cm = null;
        this.world = null;
        for (IGenerator oreGenerator : this.oreGenerators) oreGenerator.destroyData();
        for (ITeam team : this.teams) team.destroyData();
        Arena.playerLocation.entrySet().removeIf(entry2 -> entry2.getValue().getWorld().getName().equalsIgnoreCase(this.worldName));
        this.teams = null;
        this.placed = null;
        this.nextEvents = null;
        this.regionsList = null;
        this.respawnSessions = null;
        this.showTime = null;
        this.playerKills = null;
        this.playerBedsDestroyed = null;
        this.playerFinalKills = null;
        this.playerDeaths = null;
        this.playerFinalKillDeaths = null;
        this.startingTask = null;
        this.playingTask = null;
        this.restartingTask = null;
        this.oreGenerators = null;
        this.perMinuteTask = null;
    }

    public static void removeFromEnableQueue(final IArena arena) {
        Arena.enableQueue.remove(arena);
        if (!Arena.enableQueue.isEmpty()) {
            BedWars.getInstance().getApi().getRestoreAdapter().onEnable(Arena.enableQueue.get(0));
            BedWars.getInstance().getLogger().info("Cargando arena: " + Arena.enableQueue.get(0).getWorldName());
        }
    }

    public static void addToEnableQueue(final IArena arena) {
        Arena.enableQueue.add(arena);
        BedWars.getInstance().getLogger().info("Arena " + arena.getWorldName() + " añadida a la cola.");
        if (Arena.enableQueue.size() == 1) {
            BedWars.getInstance().getApi().getRestoreAdapter().onEnable(arena);
            BedWars.getInstance().getLogger().info("Cargando arena: " + arena.getWorldName());
        }
    }

    @Override
    public boolean startReSpawnSession(Player paramPlayer, int paramInt) {
        if (this.respawnSessions.get(paramPlayer) == null) {
            IArena iArena = getArenaByPlayer(paramPlayer);
            if (iArena == null) return false;
            if (!iArena.isPlayer(paramPlayer)) return false;
            paramPlayer.getInventory().clear();
            if (paramInt > 1) {
                for (Player player : iArena.getPlayers()) {
                    if (player.equals(paramPlayer)) continue;
                    BedWars.getInstance().getNms().spigotHidePlayer(paramPlayer, player);
                }
                paramPlayer.teleport(getReSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                paramPlayer.setAllowFlight(true);
                paramPlayer.setFlying(true);
                this.respawnSessions.put(paramPlayer, paramInt);
                Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
                    paramPlayer.setAllowFlight(true);
                    paramPlayer.setFlying(true);
                    BedWars.getInstance().getNms().setCollide(paramPlayer, this, false);
                    for (Player player : getShowTime().keySet())
                        BedWars.getInstance().getNms().hideArmor(player, paramPlayer);
                    updateSpectatorCollideRule(paramPlayer, false);
                    paramPlayer.teleport(getReSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                }, 10L);
            } else {
                ITeam iTeam = getTeam(paramPlayer);
                iTeam.respawnMember(paramPlayer);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isReSpawning(final Player player) {
        return this.respawnSessions.containsKey(player);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null) return false;
        if (o instanceof IArena) return ((IArena) o).getWorldName().equals(getWorldName());
        return false;
    }

    private void destroyReJoins() {
        for (final ReJoin reJoin : new ArrayList<>(ReJoin.getReJoinList()))
            if (reJoin.getArena() == this) reJoin.destroy(true);
    }

    @Override
    public boolean isProtected(final Location location) {
        return Misc.isBuildProtected(location, this);
    }

    @Override
    public void abandonGame(final Player player) {
        if (player == null) return;
        this.playerBedsDestroyed.remove(player);
        this.playerFinalKills.remove(player);
        this.playerDeaths.remove(player);
        this.playerFinalKillDeaths.remove(player);
        final ITeam team2 = this.getTeams().stream().filter(team -> team.wasMember(player.getUniqueId())).findFirst().orElse(null);
        if (team2 != null) {
            team2.getMembersCache().removeIf(player2 -> player2.getUniqueId().equals(player.getUniqueId()));
            final ReJoin player3 = ReJoin.getPlayer(player);
            if (player3 != null) player3.destroy(team2.getMembers().isEmpty());
        }
    }

    @Override
    public void setTeamAssigner(final ITeamAssigner teamAssigner) {
        if (teamAssigner == null) {
            this.teamAssigner = new TeamAssigner();
            BedWars.getInstance().getLogger().info("Usando asignador de teams default en arena: " + this.getArenaName());
        } else {
            this.teamAssigner = teamAssigner;
            BedWars.getInstance().getLogger().warning("Usando asignador de teams " + teamAssigner.getClass().getSimpleName() + " en arena: " + this.getArenaName());
        }
    }

    static {
        arenaByName = new HashMap<>();
        arenaByPlayer = new HashMap<>();
        arenaByIdentifier = new HashMap<>();
        arenas = new LinkedList<>();
        Arena.gamesBeforeRestart = 15;
        Arena.afkCheck = new HashMap<>();
        Arena.magicMilk = new HashMap<>();
        playerLocation = new HashMap<>();
        enableQueue = new LinkedList<>();
    }
}