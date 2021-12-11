package dev.eugenio.nasgarbedwars;

import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.redis.RedisManager;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.configuration.ConfigManager;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.levels.Level;
import dev.eugenio.nasgarbedwars.api.menu.PlayerMenuUtility;
import dev.eugenio.nasgarbedwars.api.server.NMSUtil;
import dev.eugenio.nasgarbedwars.api.server.RestoreAdapter;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.arena.VoidGenerator;
import dev.eugenio.nasgarbedwars.arena.compass.command.CompassMenuCommand;
import dev.eugenio.nasgarbedwars.arena.compass.data.ConfigData;
import dev.eugenio.nasgarbedwars.arena.compass.data.MessagesData;
import dev.eugenio.nasgarbedwars.arena.compass.listeners.GameListener;
import dev.eugenio.nasgarbedwars.arena.compass.listeners.MenuListener;
import dev.eugenio.nasgarbedwars.arena.despawnables.TargetListener;
import dev.eugenio.nasgarbedwars.arena.feature.ParticleTNTPlayerSpoil;
import dev.eugenio.nasgarbedwars.arena.spectator.SpectatorListeners;
import dev.eugenio.nasgarbedwars.arena.tasks.Refresh;
import dev.eugenio.nasgarbedwars.arena.tasks.RotateGenerators;
import dev.eugenio.nasgarbedwars.arena.upgrades.BaseListener;
import dev.eugenio.nasgarbedwars.commands.bedwars.MainCommand;
import dev.eugenio.nasgarbedwars.commands.leave.LeaveCommand;
import dev.eugenio.nasgarbedwars.commands.rejoin.RejoinCommand;
import dev.eugenio.nasgarbedwars.commands.shout.ShoutCommand;
import dev.eugenio.nasgarbedwars.configuration.GeneratorsConfig;
import dev.eugenio.nasgarbedwars.configuration.LevelsConfig;
import dev.eugenio.nasgarbedwars.configuration.MainConfig;
import dev.eugenio.nasgarbedwars.configuration.Sounds;
import dev.eugenio.nasgarbedwars.cosmetics.beddestroys.BedDestroysManager;
import dev.eugenio.nasgarbedwars.cosmetics.deathcries.DeathCriesManager;
import dev.eugenio.nasgarbedwars.cosmetics.finalkills.FinalKillsManager;
import dev.eugenio.nasgarbedwars.cosmetics.killmessages.KillMessagesManager;
import dev.eugenio.nasgarbedwars.cosmetics.shopkeepers.ShopkeeperManager;
import dev.eugenio.nasgarbedwars.cosmetics.sprays.SpraysManager;
import dev.eugenio.nasgarbedwars.cosmetics.toppers.ToppersManager;
import dev.eugenio.nasgarbedwars.cosmetics.woodskins.WoodSkinsManager;
import dev.eugenio.nasgarbedwars.database.Database;
import dev.eugenio.nasgarbedwars.database.MySQL;
import dev.eugenio.nasgarbedwars.language.English;
import dev.eugenio.nasgarbedwars.language.LangListener;
import dev.eugenio.nasgarbedwars.language.Spanish;
import dev.eugenio.nasgarbedwars.levels.internal.InternalLevel;
import dev.eugenio.nasgarbedwars.levels.internal.LevelListeners;
import dev.eugenio.nasgarbedwars.listeners.*;
import dev.eugenio.nasgarbedwars.listeners.heal.HealPoolParticles;
import dev.eugenio.nasgarbedwars.listeners.joinhandler.JoinListenerBungee;
import dev.eugenio.nasgarbedwars.listeners.physics.FireballTNTPhysics;
import dev.eugenio.nasgarbedwars.listeners.split.MaterialSplit;
import dev.eugenio.nasgarbedwars.listeners.split.ThrownItems;
import dev.eugenio.nasgarbedwars.listeners.sworddupe.AntiSwordDupe;
import dev.eugenio.nasgarbedwars.listeners.tower.ChestPlace;
import dev.eugenio.nasgarbedwars.shop.ShopManager;
import dev.eugenio.nasgarbedwars.sidebar.ScoreboardListener;
import dev.eugenio.nasgarbedwars.sidebar.SidebarLifeRefresh;
import dev.eugenio.nasgarbedwars.sidebar.SidebarListRefresh;
import dev.eugenio.nasgarbedwars.sidebar.SidebarPlaceholderRefresh;
import dev.eugenio.nasgarbedwars.stats.StatsManager;
import dev.eugenio.nasgarbedwars.support.ecochat.Chat;
import dev.eugenio.nasgarbedwars.support.ecochat.ChatFormat;
import dev.eugenio.nasgarbedwars.support.ecochat.Eco;
import dev.eugenio.nasgarbedwars.support.ecochat.Economy;
import dev.eugenio.nasgarbedwars.upgrades.UpgradesManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;

@Getter
public class BedWars extends JavaPlugin {
    @Getter
    private static BedWars instance;

    public static boolean debug;

    private ConfigManager mainConfig;
    private ConfigManager generatorConfig;

    private ShopManager shopManager;
    private StatsManager statsManager;
    protected Level levels;
    private Economy economy;
    private Chat chat;

    private Database mySQLDatabase;
    private final RedisManager redisManager = new RedisManager();

    private BedWarsAPI api;
    private NMSUtil nms;

    private ConfigData configData;
    private final HashMap<IArena, HashMap<UUID, ITeam>> trackingArenaMap = new HashMap<>();
    private final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

    private DeathCriesManager deathCriesManager;
    private SpraysManager spraysManager;
    private WoodSkinsManager woodSkinsManager;
    private KillMessagesManager killMessagesManager;
    private FinalKillsManager finalKillsManager;
    private ToppersManager toppersManager;
    private ShopkeeperManager shopkeeperManager;
    private BedDestroysManager bedDestroysManager;

    private final String version = Bukkit.getServer().getClass().getName().split("\\.")[3];

    public void onLoad() {
        Class<?> clazz;
        try {
            clazz = Class.forName("dev.eugenio.nasgarbedwars.support.version." + version + "." + version);
        } catch (ClassNotFoundException classNotFoundException) {
            getLogger().severe(ChatColor.RED + "BedWars no puede ser usado en tu versión debido al NMS: " + version + ". Apagando servidor.");
            Bukkit.shutdown();
            return;
        }

        try {
            nms = (NMSUtil) clazz.getConstructor(Class.forName("org.bukkit.plugin.Plugin"), String.class).newInstance(this, version);
        } catch (InstantiationException | NoSuchMethodException | java.lang.reflect.InvocationTargetException | IllegalAccessException | ClassNotFoundException instantiationException) {
            instantiationException.printStackTrace();
            getLogger().severe(ChatColor.RED + "BedWars no puede ser usado en tu versión debido al NMS: " + version + ". Apagando servidor.");
            Bukkit.shutdown();
        }
    }

    public void onEnable() {
        instance = this;

        debug = true;

        new English();
        new Spanish();

        mainConfig = new MainConfig(instance, "config");
        generatorConfig = new GeneratorsConfig(instance, "generators", getDataFolder().getPath());

        api = new API();

        statsManager = new StatsManager();
        shopManager = new ShopManager();

        economy = new Eco();
        chat = new ChatFormat();

        Bukkit.getServicesManager().register(BedWarsAPI.class, api, this, ServicePriority.Highest);

        nms.registerVersionListeners();

        if (Bukkit.getPluginManager().getPlugin("SlimeWorldManager") == null && !Bukkit.getPluginManager().getPlugin("SlimeWorldManager").isEnabled()) {
            this.getLogger().severe(ChatColor.RED + "¡SlimeWorldManager no está presente! BedWars depende en esto para manejar mundos. Apagando servidor.");
            Bukkit.getServer().shutdown();
        }

        try {
            final Constructor<?> constructor2 = Class.forName("dev.eugenio.nasgarbedwars.arena.mapreset.slime.SlimeAdapter").getConstructor(Plugin.class);
            try {
                api.setRestoreAdapter((RestoreAdapter) constructor2.newInstance(this));
                this.getLogger().info("¡SlimeWorldManager detectado! Hookeado correctamente.");
            } catch (InstantiationException exception) {
                exception.printStackTrace();
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "¡Fallo al hookear con SWM! BedWars depende en esto para manejar mundos. Apagando servidor.");
                Bukkit.getServer().shutdown();
            }
        } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "¡Fallo al hookear con SWM! BedWars depende en esto para manejar mundos. Apagando servidor.");
            Bukkit.getServer().shutdown();
        }

        nms.registerCommand("bedwars", new MainCommand("bedwars"));
        nms.registerCommand("shout", new ShoutCommand("shout"));
        nms.registerCommand("rejoin", new RejoinCommand("rejoin"));
        nms.registerCommand("leave", new LeaveCommand("leave"));

        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        registerEvents(new QuitAndTeleportListener(), new BreakPlace(), new DamageDeathMove(), new Inventory(), new Interact(), new HungerWeatherSpawn(), new CmdProcess(), new EggBridge(), new SpectatorListeners(), new BaseListener(), new TargetListener(), new LangListener(), new ServerPingListener(), new JoinListenerBungee(), new WorldLoadListener(), new ChunkLoad(), new PlayerChat(), new DenyRageDrop(), new HealPoolParticles(), new ChestPlace(), new RemoveEmptyGlass(), new FireballTNTPhysics(), new InvisibleEffect(), new AntiSwordDupe());
        loadArenasAndSigns();

        setLevelAdapter(new InternalLevel());

        Bukkit.getScheduler().runTaskTimer(this, new Refresh(), 20L, 20L);
        Bukkit.getScheduler().runTaskTimer(this, new RotateGenerators(), 120L, 1L);

        nms.registerEntities();

        final MySQL mySQL = new MySQL();
        final long currentTimeMillis = System.currentTimeMillis();

        try {
            if (!mySQL.connect()) {
                this.getLogger().severe(ChatColor.RED + "No se ha podido conectar a la database de MySQL. Asegúrate que los detalles son correctos en la configuración y que la IP está whitelisteada por el firewall. Apagando servidor.");
                Bukkit.getServer().shutdown();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        mySQLDatabase = mySQL;

        if (System.currentTimeMillis() - currentTimeMillis >= 5000L) this.getLogger().severe(ChatColor.YELLOW + "Se ha tardado " + (System.currentTimeMillis() - currentTimeMillis) / 1000L + " ms para establecer una conexión a MySQL.\nUsar esta conexión no es muy recomendable. También esto podría indicar que el MySQL está sobrecargado.");

        mySQLDatabase.init();

        redisManager.initialize();

        Language.setupCustomStatsMessages();

        nms.registerTntWhitelist();

        Sounds.init();

        for (final Language language : Language.getLanguages()) {
            language.setupUnSetCategories();
            Language.addDefaultMessagesCommandItems(language);
        }

        LevelsConfig.init();

        UpgradesManager.init();

        final int nameRefreshRate = mainConfig.getInt("scoreboard-settings.player-list.names-refresh-interval");
        if (nameRefreshRate < 1) {
            this.getLogger().warning(ChatColor.YELLOW + "El player-list está deshabilitado porque está puesto a " + nameRefreshRate + ". Cámbialo para poder usarlo.");
        } else {
            if (nameRefreshRate < 20) Bukkit.getLogger().warning(ChatColor.YELLOW + "El refresh del playerlist (scoreboard-temas) está puesta a " + nameRefreshRate + "\nNo es recomendado poner un valor más debajo de 20 ticks.\nSi tienes problemas de rendimiento, aumenta el valor.");
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, new SidebarListRefresh(), 23L, nameRefreshRate);
        }

        final int placeholderRefreshRate = mainConfig.getInt("scoreboard-settings.sidebar.placeholders-refresh-interval");
        if (placeholderRefreshRate < 1) {
            Bukkit.getLogger().warning(ChatColor.YELLOW + "El refresh de placeholders está desactivado. (Está puesto a " + placeholderRefreshRate + ").");
        } else {
            if (placeholderRefreshRate < 20) Bukkit.getLogger().warning(ChatColor.YELLOW + "El refresh del placeholders está puesto a " + placeholderRefreshRate + "\nNo es recomendado poner un valor más debajo de 20 ticks.\nSi tienes problemas de rendimiento, aumenta el valor.");
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, new SidebarPlaceholderRefresh(), 28L, placeholderRefreshRate);
        }

        final int healthRefreshRate = mainConfig.getInt("scoreboard-settings.health.animation-refresh-interval");
        if (healthRefreshRate < 1) {
            Bukkit.getLogger().warning(ChatColor.YELLOW + "El refresh de la salud está desactivada. (Está puesto a " + healthRefreshRate + ").");
        } else {
            if (healthRefreshRate < 20) Bukkit.getLogger().warning(ChatColor.YELLOW + "El refresh de salud está puesto a " + placeholderRefreshRate + "\nNo es recomendado poner un valor más debajo de 20 ticks.\nSi tienes problemas de rendimiento, aumenta el valor.");
            Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new SidebarLifeRefresh(), 40L, healthRefreshRate);
        }

        registerEvents(new ScoreboardListener());
        ParticleTNTPlayerSpoil.init();

        // Compass registration
        new CompassMenuCommand(getApi().getBedWarsCommand(), "compass");

        configData = new ConfigData(this, "compass", getDataFolder().getPath());
        configData.reload();

        new MessagesData();

        registerEvents(new MenuListener(), new GameListener());

        registerEvents(new MaterialSplit(), new ThrownItems());

        deathCriesManager = new DeathCriesManager();
        spraysManager = new SpraysManager();
        woodSkinsManager = new WoodSkinsManager();
        killMessagesManager = new KillMessagesManager();
        finalKillsManager = new FinalKillsManager();
        toppersManager = new ToppersManager();
        shopkeeperManager = new ShopkeeperManager();
        bedDestroysManager = new BedDestroysManager();
    }

    public void onDisable() {
        try {
            for (IArena iArena : Arena.getArenas()) iArena.disable();
        } catch (Exception ignored) {
            // Ignored
        }
        if (mySQLDatabase != null) mySQLDatabase.close();
        redisManager.shutdown();

        // Compass
        Bukkit.getScheduler().cancelTasks(this);
        trackingArenaMap.clear();
        playerMenuUtilityMap.clear();
    }

    public void loadArenasAndSigns() {
        api.getRestoreAdapter().convertWorlds();

        final File file = new File(instance.getDataFolder(), "/Arenas");
        if (file.exists()) {
            final ArrayList<File> list = new ArrayList<>();

            for (final File file2 : Objects.requireNonNull(file.listFiles())) if (file2.isFile() && file2.getName().endsWith(".yml")) list.add(file2);

            if (list.isEmpty()) {
                this.getLogger().log(java.util.logging.Level.WARNING, "¡No se ha podido encontrar ninguna arena!");
                return;
            }
            new Arena(list.get(new Random().nextInt(list.size())).getName().replace(".yml", ""), null);
        }
    }

    public void registerEvents(final Listener... array) {
        Arrays.stream(array).forEach(listener -> instance.getServer().getPluginManager().registerEvents(listener, instance));
    }

    public static void debug(final String s) {
        if (debug) Bukkit.getLogger().info("DEBUG: " + s);
    }

    public String getForCurrentVersion(final String s, final String s2, final String s3) {
        switch (version) {
            case "v1_8_R3":
                return s;
            case "v1_9_R1":
            case "v1_9_R2":
            case "v1_10_R1":
            case "v1_11_R1":
            case "v1_12_R1":
                return s2;
            default:
                return s3;
        }
    }

    public void setLevelAdapter(final Level levelAdapter) {
        if (levelAdapter instanceof InternalLevel) {
            if (LevelListeners.instance == null) Bukkit.getPluginManager().registerEvents(new LevelListeners(), instance);
        } else if (LevelListeners.instance != null) {
            PlayerJoinEvent.getHandlerList().unregister(LevelListeners.instance);
            PlayerQuitEvent.getHandlerList().unregister(LevelListeners.instance);
            LevelListeners.instance = null;
        }
        levels = levelAdapter;
    }

    public ChunkGenerator getDefaultWorldGenerator(final String s, final String s2) {
        return new VoidGenerator();
    }

    public PlayerMenuUtility getPlayerMenuUtility(Player p) {
        final PlayerMenuUtility playerMenuUtility;
        if (!(playerMenuUtilityMap.containsKey(p))) {
            playerMenuUtility = new PlayerMenuUtility(p);
            playerMenuUtilityMap.put(p, playerMenuUtility);
            return playerMenuUtility;
        } else {
            return playerMenuUtilityMap.get(p);
        }
    }

    public void setTrackingTeam(IArena arena, UUID uuid, ITeam team) {
        if (trackingArenaMap.get(arena) != null) {
            getTrackingTeamMap(arena).put(uuid, team);
            return;
        }
        final HashMap<UUID, ITeam> map = new HashMap<>();
        map.put(uuid, team);
        trackingArenaMap.put(arena, map);
    }

    public boolean isTracking(IArena arena, UUID uuid) {
        if (trackingArenaMap.containsKey(arena)) return trackingArenaMap.get(arena).containsKey(uuid);
        return false;
    }

    public HashMap<UUID, ITeam> getTrackingTeamMap(IArena arena) {
        return trackingArenaMap.get(arena);
    }

    public ITeam getTrackingTeam(IArena arena, UUID uuid) {
        return trackingArenaMap.get(arena).get(uuid);
    }

    public void removeTrackingTeam(IArena arena, UUID uuid) {
        trackingArenaMap.get(arena).remove(uuid);
    }

    public void removeTrackingArena(IArena arena) {
        trackingArenaMap.remove(arena);
    }
}