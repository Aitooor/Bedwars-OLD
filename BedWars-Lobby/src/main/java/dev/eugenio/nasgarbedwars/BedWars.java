package dev.eugenio.nasgarbedwars;

import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.levels.Level;
import dev.eugenio.nasgarbedwars.api.server.NMSUtil;
import dev.eugenio.nasgarbedwars.commands.bedwars.MainCommand;
import dev.eugenio.nasgarbedwars.configuration.LevelsConfig;
import dev.eugenio.nasgarbedwars.database.Database;
import dev.eugenio.nasgarbedwars.database.MySQL;
import dev.eugenio.nasgarbedwars.language.English;
import dev.eugenio.nasgarbedwars.language.Spanish;
import dev.eugenio.nasgarbedwars.levels.internal.InternalLevel;
import dev.eugenio.nasgarbedwars.levels.internal.LevelListeners;
import dev.eugenio.nasgarbedwars.listeners.lang.LangChangeListener;
import dev.eugenio.nasgarbedwars.npc.NPCManager;
import dev.eugenio.nasgarbedwars.npc.listeners.NPCDestroyEvent;
import dev.eugenio.nasgarbedwars.npc.listeners.NPCSpawnEvent;
import dev.eugenio.nasgarbedwars.stats.StatsManager;
import dev.eugenio.nasgarbedwars.api.configuration.ConfigManager;
import dev.eugenio.nasgarbedwars.configuration.MainConfig;
import lombok.Getter;
import net.jitse.npclib.NPCLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Arrays;

@Getter
public class BedWars extends JavaPlugin {
    @Getter
    private static BedWars instance;

    public static boolean debug;

    private ConfigManager mainConfig;

    private StatsManager statsManager;
    protected Level levels;

    private Database mySQLDatabase;

    private BedWarsAPI api;
    private NMSUtil nms;

    private NPCManager npcManager;
    private NPCLib npcLib;

    private final String version = Bukkit.getServer().getClass().getName().split("\\.")[3];

    public void onLoad() {
        Class<?> clazz;
        try {
            clazz = Class.forName("dev.eugenio.nasgarbedwars.support.version." + version + "." + version);
        } catch (ClassNotFoundException classNotFoundException) {
            BedWars.debug(ChatColor.RED + "BedWars no puede ser usado en tu versión debido al NMS: " + version + ". Apagando servidor. 1");
            Bukkit.shutdown();
            return;
        }

        try {
            nms = (NMSUtil) clazz.getConstructor(Class.forName("org.bukkit.plugin.Plugin"), String.class).newInstance(this, version);
        } catch (InstantiationException | NoSuchMethodException | java.lang.reflect.InvocationTargetException | IllegalAccessException | ClassNotFoundException instantiationException) {
            instantiationException.printStackTrace();
            BedWars.debug(ChatColor.RED + "BedWars no puede ser usado en tu versión debido al NMS: " + version + ". Apagando servidor. 2");
            Bukkit.shutdown();
        }
    }

    public void onEnable() {
        instance = this;

        debug = true;

        new English();
        new Spanish();

        mainConfig = new MainConfig(instance, "config");

        api = new API();

        statsManager = new StatsManager();

        Bukkit.getServicesManager().register(BedWarsAPI.class, api, this, ServicePriority.Highest);

        registerEvents(new LangChangeListener());

        nms.registerCommand("bedwars", new MainCommand("bedwars"));

        setLevelAdapter(new InternalLevel());

        final MySQL mySQL = new MySQL();
        final long currentTimeMillis = System.currentTimeMillis();

        try {
            if (!mySQL.connect()) {
                this.getLogger().severe(ChatColor.RED + "No se ha podido conectar a la database de MySQL. Asegúrate que los detalles son correctos en la configuración y que la IP está whitelisteada por el firewall. Apagando servidor.");
                this.getLogger().severe(ChatColor.RED + "No se ha podido conectar a la database de MySQL. Asegúrate que los detalles son correctos en la configuración y que la IP está whitelisteada por el firewall. Apagando servidor.");
                this.getLogger().severe(ChatColor.RED + "No se ha podido conectar a la database de MySQL. Asegúrate que los detalles son correctos en la configuración y que la IP está whitelisteada por el firewall. Apagando servidor.");
                Bukkit.getServer().shutdown();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        mySQLDatabase = mySQL;
        if (System.currentTimeMillis() - currentTimeMillis >= 5000L) this.getLogger().severe(ChatColor.YELLOW + "Se ha tardado " + (System.currentTimeMillis() - currentTimeMillis) / 1000L + " ms para establecer una conexión a MySQL.\nUsar esta conexión no es muy recomendable. También esto podría indicar que el MySQL está sobrecargado.");
        mySQLDatabase.init();

        Language.setupCustomStatsMessages();

        LevelsConfig.init();

        npcLib = new NPCLib(this);
        npcManager = new NPCManager();

        registerEvents(new NPCSpawnEvent(), new NPCDestroyEvent());
    }

    public void onDisable() {
        if (mySQLDatabase != null) mySQLDatabase.close();

        Bukkit.getScheduler().cancelTasks(this);
        NPCManager.destroyAll();
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
}