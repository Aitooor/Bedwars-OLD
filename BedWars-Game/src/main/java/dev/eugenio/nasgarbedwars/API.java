package dev.eugenio.nasgarbedwars;

import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.arena.SetupSession;
import dev.eugenio.nasgarbedwars.commands.bedwars.MainCommand;
import dev.eugenio.nasgarbedwars.stats.StatsAPI;
import dev.eugenio.nasgarbedwars.upgrades.UpgradesManager;
import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.shop.IContentTier;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.configuration.ConfigManager;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerAFKEvent;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.levels.Level;
import dev.eugenio.nasgarbedwars.api.server.ISetupSession;
import dev.eugenio.nasgarbedwars.api.server.NMSUtil;
import dev.eugenio.nasgarbedwars.api.server.RestoreAdapter;
import dev.eugenio.nasgarbedwars.shop.main.CategoryContent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

public class API implements BedWarsAPI {
    private static RestoreAdapter restoreAdapter;
    private final AFKUtil afkSystem;
    private final ArenaUtil arenaUtil;
    private final Configs configs;
    private final ShopUtil shopUtil;
    private final TeamUpgradesUtil teamUpgradesUtil;
    
    public API() {
        this.afkSystem = new AFKUtil() {
            private final HashMap<UUID, Integer> afkPlayers = new HashMap<>();
            
            @Override
            public boolean isPlayerAFK(final Player player) {
                return this.afkPlayers.containsKey(player.getUniqueId());
            }
            
            @Override
            public void setPlayerAFK(final Player player, final boolean b) {
                if (b) {
                    if (!this.afkPlayers.containsKey(player.getUniqueId())) {
                        this.afkPlayers.put(player.getUniqueId(), Arena.afkCheck.get(player.getUniqueId()));
                        Bukkit.getPluginManager().callEvent(new PlayerAFKEvent(player, PlayerAFKEvent.AFKType.START));
                    }
                } else {
                    if (this.afkPlayers.containsKey(player.getUniqueId())) {
                        this.afkPlayers.remove(player.getUniqueId());
                        Bukkit.getPluginManager().callEvent(new PlayerAFKEvent(player, PlayerAFKEvent.AFKType.END));
                    }
                    Arena.afkCheck.remove(player.getUniqueId());
                }
            }
            
            @Override
            public int getPlayerTimeAFK(final Player player) {
                return this.afkPlayers.getOrDefault(player.getUniqueId(), 0);
            }
        };
        this.arenaUtil = new ArenaUtil() {
            
            @Override
            public void addToEnableQueue(final IArena arena) {
                Arena.addToEnableQueue(arena);
            }
            
            @Override
            public void removeFromEnableQueue(final IArena arena) {
                Arena.removeFromEnableQueue(arena);
            }
            
            @Override
            public boolean isPlaying(final Player player) {
                return Arena.isInArena(player);
            }
            
            @Override
            public boolean isSpectating(final Player player) {
                return Arena.isInArena(player) && Arena.getArenaByPlayer(player).isSpectator(player);
            }
            
            @Override
            public void loadArena(final String s, final Player player) {
                new Arena(s, player);
            }
            
            @Override
            public void setGamesBeforeRestart(final int gamesBeforeRestart) {
                Arena.setGamesBeforeRestart(gamesBeforeRestart);
            }
            
            @Override
            public int getGamesBeforeRestart() {
                return Arena.getGamesBeforeRestart();
            }
            
            @Override
            public IArena getArenaByPlayer(final Player player) {
                return Arena.getArenaByPlayer(player);
            }
            
            @Override
            public void setArenaByPlayer(final Player player, final IArena arena) {
                Arena.setArenaByPlayer(player, arena);
            }
            
            @Override
            public void removeArenaByPlayer(final Player player, final IArena arena) {
                Arena.removeArenaByPlayer(player, arena);
            }
            
            @Override
            public IArena getArenaByName(final String s) {
                return Arena.getArenaByName(s);
            }
            
            @Override
            public IArena getArenaByIdentifier(final String s) {
                return Arena.getArenaByIdentifier(s);
            }
            
            @Override
            public void setArenaByName(final IArena arenaByName) {
                Arena.setArenaByName(arenaByName);
            }
            
            @Override
            public void removeArenaByName(final String s) {
                Arena.removeArenaByName(s);
            }
            
            @Override
            public LinkedList<IArena> getArenas() {
                return Arena.getArenas();
            }
            
            @Override
            public int getPlayers(final String s) {
                return Arena.getPlayers(s);
            }
            
            @Override
            public boolean joinRandomArena(final Player player) {
                return Arena.joinRandomArena(player);
            }
            
            @Override
            public boolean joinRandomFromGroup(final Player player, final String s) {
                return Arena.joinRandomFromGroup(player, s);
            }
            
            @Override
            public LinkedList<IArena> getEnableQueue() {
                return Arena.getEnableQueue();
            }
        };
        this.configs = new Configs() {
            @Override
            public ConfigManager getMainConfig() {
                return BedWars.getInstance().getMainConfig();
            }
            
            @Override
            public ConfigManager getGeneratorsConfig() {
                return BedWars.getInstance().getGeneratorConfig();
            }
            
            @Override
            public ConfigManager getShopConfig() {
                return BedWars.getInstance().getShopManager();
            }
            
            @Override
            public ConfigManager getUpgradesConfig() {
                return UpgradesManager.getConfiguration();
            }
        };
        this.shopUtil = new ShopUtil() {
            @Override
            public int calculateMoney(final Player player, final Material material) {
                return CategoryContent.calculateMoney(player, material);
            }
            
            @Override
            public Material getCurrency(final String s) {
                return CategoryContent.getCurrency(s);
            }
            
            @Override
            public ChatColor getCurrencyColor(final Material material) {
                return CategoryContent.getCurrencyColor(material);
            }
            
            @Override
            public String getCurrencyMsgPath(final IContentTier contentTier) {
                return CategoryContent.getCurrencyMsgPath(contentTier);
            }
            
            @Override
            public String getRomanNumber(final int n) {
                return CategoryContent.getRomanNumber(n);
            }
            
            @Override
            public void takeMoney(final Player player, final Material material, final int n) {
                CategoryContent.takeMoney(player, material, n);
            }
        };
        this.teamUpgradesUtil = new TeamUpgradesUtil() {
            @Override
            public boolean isWatchingGUI(final Player player) {
                return UpgradesManager.isWatchingUpgrades(player.getUniqueId());
            }
            
            @Override
            public void setWatchingGUI(final Player player) {
                UpgradesManager.setWatchingUpgrades(player.getUniqueId());
            }
            
            @Override
            public void removeWatchingUpgrades(final UUID uuid) {
                UpgradesManager.removeWatchingUpgrades(uuid);
            }
        };
    }
    
    @Override
    public IStats getStatsUtil() {
        return StatsAPI.getInstance();
    }
    
    @Override
    public AFKUtil getAFKUtil() {
        return this.afkSystem;
    }
    
    @Override
    public ArenaUtil getArenaUtil() {
        return this.arenaUtil;
    }
    
    @Override
    public Configs getConfigs() {
        return this.configs;
    }
    
    @Override
    public ShopUtil getShopUtil() {
        return this.shopUtil;
    }
    
    @Override
    public TeamUpgradesUtil getTeamUpgradesUtil() {
        return this.teamUpgradesUtil;
    }
    
    @Override
    public Level getLevelsUtil() {
        return BedWars.getInstance().getLevels();
    }
    
    @Override
    public ISetupSession getSetupSession(final UUID uuid) {
        return SetupSession.getSession(uuid);
    }
    
    @Override
    public boolean isInSetupSession(final UUID uuid) {
        return SetupSession.isInSetupSession(uuid);
    }
    
    @Override
    public ParentCommand getBedWarsCommand() {
        return MainCommand.getInstance();
    }
    
    @Override
    public RestoreAdapter getRestoreAdapter() {
        return API.restoreAdapter;
    }
    
    @Override
    public void setRestoreAdapter(final RestoreAdapter restoreAdapter) {
        if (!Arena.getArenas().isEmpty()) {
            throw new IllegalAccessError("Las arenas deben de ser descargadas cambiando el adaptador.");
        }
        API.restoreAdapter = restoreAdapter;
        if (restoreAdapter.getOwner() != null && restoreAdapter.getOwner() != BedWars.getInstance()) {
            BedWars.getInstance().getLogger().log(java.util.logging.Level.WARNING, restoreAdapter.getOwner().getName() + " cambió el sistema de restauración a su propio adaptador.");
        }
    }
    
    @Override
    public NMSUtil getVersionSupport() {
        return BedWars.getInstance().getNms();
    }
    
    @Override
    public Language getDefaultLang() {
        return Language.getDefaultLanguage();
    }
    
    @Override
    public String getForCurrentVersion(final String s, final String s2, final String s3) {
        return BedWars.getInstance().getForCurrentVersion(s, s2, s3);
    }
    
    @Override
    public void setLevelAdapter(final Level levelAdapter) {
        BedWars.getInstance().setLevelAdapter(levelAdapter);
    }
    
    @Override
    public Language getLanguageByIso(final String s) {
        return Language.getLang(s);
    }
    
    @Override
    public Language getPlayerLanguage(final Player player) {
        return Language.getPlayerLanguage(player);
    }
    
    @Override
    public String getLangIso(final Player player) {
        return Language.getPlayerLanguage(player).getIso();
    }
}
