package dev.eugenio.nasgarbedwars.sidebar;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.GameStatus;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.arena.Misc;
import dev.eugenio.nasgarbedwars.libs.sidebar.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.*;

public class BedWarsScoreboard {
    private static SidebarManager sidebarManager;
    @Getter
    private static final HashMap<UUID, BedWarsScoreboard> scoreboards = new HashMap<>();
    @Getter
    private final Player player;
    @Getter
    private IArena arena;
    @Getter
    private Sidebar handle;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat nextEventDateFormat;
    
    private BedWarsScoreboard(final Player player, final List<String> strings, @Nullable final IArena arena) {
        this.arena = arena;
        this.player = player;
        if (strings.isEmpty()) return;
        final BedWarsScoreboard bedWarsScoreboard = scoreboards.get(player.getUniqueId());
        if (bedWarsScoreboard != null) bedWarsScoreboard.remove();
        if (!player.isOnline()) return;
        this.nextEventDateFormat = new SimpleDateFormat(Language.getMsg(player, Messages.FORMATTING_SCOREBOARD_NEXEVENT_TIMER));
        this.dateFormat = new SimpleDateFormat(Language.getMsg(player, Messages.FORMATTING_SCOREBOARD_DATE));
        List<PlaceholderProvider> list2 = Arrays.asList(new PlaceholderProvider("{on}", () -> String.valueOf(this.getArena() == null ? Bukkit.getOnlinePlayers().size() : this.getArena().getPlayers().size())), new PlaceholderProvider("{max}", () -> String.valueOf(this.getArena() == null ? Bukkit.getMaxPlayers() : this.getArena().getMaxPlayers())), new PlaceholderProvider("{time}", () -> {
            if (this.arena == null) return this.dateFormat.format(new Date(System.currentTimeMillis()));
            if (this.arena.getStatus() == GameStatus.playing || this.arena.getStatus() == GameStatus.restarting) return this.getNextEventTime();
            if (this.arena.getStatus() == GameStatus.starting && this.getArena().getStartingTask() != null) return String.valueOf(this.getArena().getStartingTask().getCountdown() + 1);
            return this.dateFormat.format(new Date(System.currentTimeMillis()));
        }), new PlaceholderProvider("{nextEvent}", this::getNextEventName), new PlaceholderProvider("{date}", () -> this.dateFormat.format(new Date(System.currentTimeMillis()))), new PlaceholderProvider("{kills}", () -> String.valueOf(this.getArena() == null ? BedWars.getInstance().getStatsManager().get(this.getPlayer().getUniqueId()).getKills() : this.getArena().getPlayerKills(this.getPlayer(), false))), new PlaceholderProvider("{finalKills}", () -> String.valueOf(this.getArena() == null ? BedWars.getInstance().getStatsManager().get(this.getPlayer().getUniqueId()).getFinalKills() : this.getArena().getPlayerKills(this.getPlayer(), true))), new PlaceholderProvider("{beds}", () -> String.valueOf(this.getArena() == null ? BedWars.getInstance().getStatsManager().get(this.getPlayer().getUniqueId()).getBedsDestroyed() : this.getArena().getPlayerBedsDestroyed(this.getPlayer()))), new PlaceholderProvider("{deaths}", () -> String.valueOf(this.getArena() == null ? BedWars.getInstance().getStatsManager().get(this.getPlayer().getUniqueId()).getDeaths() : this.getArena().getPlayerDeaths(this.getPlayer(), false))), new PlaceholderProvider("{progress}", () -> BedWars.getInstance().getLevels().getProgressBar(this.getPlayer())), new PlaceholderProvider("{level}", () -> BedWars.getInstance().getLevels().getLevel(this.getPlayer())), new PlaceholderProvider("{currentXp}", () -> BedWars.getInstance().getLevels().getCurrentXpFormatted(this.getPlayer())), new PlaceholderProvider("{requiredXp}", () -> BedWars.getInstance().getLevels().getRequiredXpFormatted(this.getPlayer())));
        if (sidebarManager == null) {
            try {
                sidebarManager = new SidebarManager();
            } catch (InstantiationException instantiationException) {
                throw new IllegalStateException(instantiationException);
            }
        }
        this.handle = BedWarsScoreboard.sidebarManager.createSidebar(null, Collections.emptyList(), list2);
        scoreboards.put(player.getUniqueId(), this);
        this.setStrings(strings);
        this.handle.apply(player);
        this.handlePlayerList();
    }

    public static void giveScoreboard(final Player player, final IArena arena, final boolean b) {
        if (!player.isOnline()) return;
        final BedWarsScoreboard sBoard = getSBoard(player.getUniqueId());
        List<String> strings = null;
        if (arena != null) {
            if (!BedWars.getInstance().getMainConfig().getBoolean("scoreboard-settings.sidebar.enable-game-sidebar")) {
                if (sBoard != null) sBoard.remove();
                return;
            }
            if (arena.getStatus() == GameStatus.waiting) {
                strings = Language.getScoreboard(player, "scoreboard." + arena.getGroup() + ".waiting", Messages.SCOREBOARD_DEFAULT_WAITING);
            } else if (arena.getStatus() == GameStatus.starting) {
                strings = Language.getScoreboard(player, "scoreboard." + arena.getGroup() + ".starting", Messages.SCOREBOARD_DEFAULT_STARTING);
            } else if (arena.getStatus() == GameStatus.playing || arena.getStatus() == GameStatus.restarting) {
                strings = Language.getScoreboard(player, "scoreboard." + arena.getGroup() + ".playing", Messages.SCOREBOARD_DEFAULT_PLAYING);
            }
        }
        if (strings == null || strings.isEmpty()) {
            if (sBoard != null) sBoard.remove();
            return;
        }
        if (sBoard == null) {
            if (b) {
                List<String> finalStrings = strings;
                Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> new BedWarsScoreboard(player, finalStrings, arena), 5L);
            } else {
                new BedWarsScoreboard(player, strings, arena);
            }
        } else {
            sBoard.setArena(arena);
            sBoard.setStrings(strings);
        }
    }
    
    public void setArena(final IArena arena) {
        this.arena = arena;
    }

    public void handlePlayerList() {
        this.handle.playerListClear();
        handleHealthIcon();
        if (this.arena != null) {
            for (ITeam iTeam : this.arena.getTeams()) {
                this.handle.addPlaceholder(new PlaceholderProvider("{Team" + iTeam.getName() + "Status}", () -> {
                    String str;
                    if (iTeam.isBedDestroyed()) {
                        if (iTeam.getSize() > 0) {
                            str = Language.getMsg(getPlayer(), Messages.FORMATTING_SCOREBOARD_BED_DESTROYED).replace("{remainingPlayers}", String.valueOf(iTeam.getSize()));
                        } else {
                            str = Language.getMsg(getPlayer(), Messages.FORMATTING_SCOREBOARD_TEAM_ELIMINATED);
                        }
                    } else {
                        str = Language.getMsg(getPlayer(), Messages.FORMATTING_SCOREBOARD_TEAM_ALIVE);
                    }
                    if (iTeam.isMember(getPlayer()))
                        str = str + Language.getMsg(getPlayer(), Messages.FORMATTING_SCOREBOARD_YOUR_TEAM);
                    return str;
                }));
            }
            if ((this.arena.getStatus() == GameStatus.playing && BedWars.getInstance().getMainConfig().getBoolean("scoreboard-settings.player-list.format-playing-list")) || (this.arena
                    .getStatus() == GameStatus.restarting && BedWars.getInstance().getMainConfig().getBoolean("scoreboard-settings.player-list.format-restarting-list"))) {
                String str1 = (this.arena.getStatus() == GameStatus.playing) ? Messages.FORMATTING_SCOREBOARD_TAB_PREFIX_PLAYING : Messages.FORMATTING_SCOREBOARD_TAB_PREFIX_PRESTARTING;
                String str2 = (this.arena.getStatus() == GameStatus.playing) ? Messages.FORMATTING_SCOREBOARD_TAB_SUFFIX_PLAYING : Messages.FORMATTING_SCOREBOARD_TAB_SUFFIX_PRESTARTING;
                for (ITeam iTeam : this.arena.getTeams()) {
                    iTeam.getMembers().forEach(paramPlayer -> {
                        addToTabList(paramPlayer, str1, str2);
                        BedWarsScoreboard bedWarsScoreboard = getSBoard(paramPlayer.getUniqueId());
                        if (bedWarsScoreboard != null)
                            bedWarsScoreboard.addToTabList(getPlayer(), str1, str2);
                    });
                    this.handle.playerListRefreshAnimation();
                }
                if (this.arena.isSpectator(getPlayer()))
                    this.arena.getSpectators().forEach(paramPlayer -> {
                        addToTabList(paramPlayer, str1, str2);
                        BedWarsScoreboard bedWarsScoreboard = getSBoard(paramPlayer.getUniqueId());
                        if (bedWarsScoreboard != null) bedWarsScoreboard.addToTabList(getPlayer(), str1, str2);
                        this.handle.playerListRefreshAnimation();
                    });
            } else if ((this.arena.getStatus() == GameStatus.waiting && BedWars.getInstance().getConfig().getBoolean("scoreboard-settings.player-list.format-waiting-list")) || (this.arena
                    .getStatus() == GameStatus.starting && BedWars.getInstance().getConfig().getBoolean("scoreboard-settings.player-list.format-starting-list"))) {
                String str1 = (this.arena.getStatus() == GameStatus.waiting) ? Messages.FORMATTING_SCOREBOARD_TAB_PREFIX_WAITING : Messages.FORMATTING_SCOREBOARD_TAB_PREFIX_STARTING;
                String str2 = (this.arena.getStatus() == GameStatus.waiting) ? Messages.FORMATTING_SCOREBOARD_TAB_SUFFIX_WAITING : Messages.FORMATTING_SCOREBOARD_TAB_SUFFIX_STARTING;
                this.arena.getPlayers().forEach(paramPlayer -> {
                    addToTabList(paramPlayer, str1, str2);
                    BedWarsScoreboard bedWarsScoreboard = getSBoard(paramPlayer.getUniqueId());
                    if (bedWarsScoreboard != null) bedWarsScoreboard.addToTabList(getPlayer(), str1, str2);
                });
                this.handle.playerListRefreshAnimation();
            }
        }
    }
    
    private void setStrings(List<String> paramList) {
        while (this.handle.linesAmount() > 0) this.handle.removeLine(0);
        LinkedList linkedList = new LinkedList();
        this.handle.getPlaceholders().forEach(placeholderProvider -> {
            if (placeholderProvider.getPlaceholder().startsWith("{Team")) linkedList.add(placeholderProvider.getPlaceholder());
        });
        linkedList.forEach(string -> this.handle.removePlaceholder((String) string));
        final String[] arrayOfString = paramList.remove(0).split("[\\n,]");
        if (arrayOfString.length == 1) {
            this.handle.setTitle(new SidebarLine() {
                @Override
                public String getLine() {
                    return arrayOfString[0];
                }
            });
        } else {
            this.handle.setTitle(new SidebarLineAnimated(arrayOfString));
        }
        handlePlayerList();
        int b = 0;
        Language language = Language.getPlayerLanguage(this.player);
        String str = language.m(Messages.FORMATTING_SCOREBOARD_TEAM_GENERIC);
        for (String str1 : paramList) {
            if (this.arena != null && str1.trim().equals("{team}")) {
                if (this.arena.getTeams().size() > b) {
                    ITeam iTeam = this.arena.getTeams().get(b++);
                    String str3 = iTeam.getDisplayName(language);
                    str1 = str.replace("{TeamLetter}", String.valueOf((str3.length() != 0) ? Character.valueOf(str3.charAt(0)) : "")).replace("{TeamColor}", iTeam.getColor().toString()).replace("{TeamName}", str3).replace("{TeamStatus}", "{Team" + iTeam.getName() + "Status}");
                } else {
                    continue;
                }
            }
            str1 = str1.replace("{server_ip}", BedWars.getInstance().getDescription().getVersion()).replace("{server}", BedWars.getInstance().getMainConfig().getString("bungee-settings.server-id")).replace("{player}", this.player.getDisplayName()).replace("{money}", String.valueOf(BedWars.getInstance().getEconomy().getMoney(this.player)));
            if (this.arena == null) {
                str1 = Misc.replaceStatsPlaceholders(getPlayer(), str1, true);
            } else {
                str1 = str1.replace("{map}", this.arena.getDisplayName()).replace("{version}", BedWars.getInstance().getVersion()).replace("{group}", this.arena.getDisplayGroup(this.player));
                for (ITeam iTeam : this.arena.getTeams()) {
                    ChatColor chatColor = iTeam.getColor().chat();
                    str1 = str1.replace("{Team" + iTeam.getName() + "Color}", chatColor.toString()).replace("{Team" + iTeam.getName() + "Name}", iTeam.getDisplayName(Language.getPlayerLanguage(getPlayer())));
                }
            }
            final String finalTemp = str1;
            SidebarLine sidebarLine = new SidebarLine() {
                @Override
                public String getLine() {
                    return finalTemp;
                }
            };
            this.handle.addLine(sidebarLine);
        }
    }

    public void handleHealthIcon() {
        if (this.arena == null) {
            this.handle.hidePlayersHealth();
            return;
        }
        if (this.arena.getStatus() != GameStatus.playing) {
            this.handle.hidePlayersHealth();
            return;
        }
        if (this.handle != null) {
            SidebarLine sidebarLine;
            List<String> list = Language.getList(this.player, Messages.FORMATTING_SCOREBOARD_HEALTH);
            if (list.isEmpty()) return;
            if (list.size() > 1) {
                String[] stringArray = new String[list.size()];
                for (int i = 0; i < list.size(); ++i) stringArray[i] = list.get(i);
                sidebarLine = new SidebarLineAnimated(stringArray);
            } else {
                final String string = list.get(0);
                sidebarLine = new SidebarLine() {
                    @Override
                    public String getLine() {
                        return string;
                    }
                };
            }
            this.handle.showPlayersHealth(sidebarLine, BedWars.getInstance().getMainConfig().getBoolean("scoreboard-settings.health.display-in-tab"));
            Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
                if (this.arena != null && this.handle != null) {
                    this.arena.getPlayers().forEach(player -> this.handle.refreshHealth(player, (int)player.getHealth()));
                    if (this.arena.isSpectator(this.getPlayer())) this.arena.getSpectators().forEach(player -> this.handle.refreshHealth(player, (int)player.getHealth()));
                }
            }, 20L);
        }
    }
    
    public void remove() {
        BedWarsScoreboard.scoreboards.remove(this.getPlayer().getUniqueId());
        if (this.handle != null) {
            this.handle.remove(this.player.getUniqueId());
            this.handle = null;
            getScoreboards().values().forEach(bedWarsScoreboard -> bedWarsScoreboard.handle.playerListRemove(this.getPlayer().getName()));
        }
    }
    
    public static BedWarsScoreboard getSBoard(final UUID uuid) {
        return BedWarsScoreboard.scoreboards.get(uuid);
    }

    private String getNextEventName() {
        if (!(this.arena instanceof Arena)) return "-";
        final Arena arena = (Arena)this.arena;
        String s = "-";
        switch (arena.getNextEvent()) {
            case EMERALD_GENERATOR_TIER_II:
                s = Language.getMsg(this.getPlayer(), Messages.NEXT_EVENT_EMERALD_UPGRADE_II);
                break;
            case EMERALD_GENERATOR_TIER_III:
                s = Language.getMsg(this.getPlayer(), Messages.NEXT_EVENT_EMERALD_UPGRADE_III);
                break;
            case DIAMOND_GENERATOR_TIER_II:
                s = Language.getMsg(this.getPlayer(), Messages.NEXT_EVENT_DIAMOND_UPGRADE_II);
                break;
            case DIAMOND_GENERATOR_TIER_III:
                s = Language.getMsg(this.getPlayer(), Messages.NEXT_EVENT_DIAMOND_UPGRADE_III);
                break;
            case GAME_END:
                s = Language.getMsg(this.getPlayer(), Messages.NEXT_EVENT_GAME_END);
                break;
            case BEDS_DESTROY:
                s = Language.getMsg(this.getPlayer(), Messages.NEXT_EVENT_BEDS_DESTROY);
                break;
            case ENDER_DRAGON:
                s = Language.getMsg(this.getPlayer(), Messages.NEXT_EVENT_DRAGON_SPAWN);
                break;
        }
        return s;
    }

    private String getNextEventTime() {
        if (!(this.arena instanceof Arena)) return this.nextEventDateFormat.format(0L);
        final Arena arena = (Arena)this.arena;
        long n = 0L;
        switch (arena.getNextEvent()) {
            case EMERALD_GENERATOR_TIER_II:
            case EMERALD_GENERATOR_TIER_III:
                n = arena.upgradeEmeraldsCount * 1000L;
                break;
            case DIAMOND_GENERATOR_TIER_II:
            case DIAMOND_GENERATOR_TIER_III:
                n = arena.upgradeDiamondsCount * 1000L;
                break;
            case GAME_END:
                n = arena.getPlayingTask().getGameEndCountdown() * 1000L;
                break;
            case BEDS_DESTROY:
                n = arena.getPlayingTask().getBedsDestroyCountdown() * 1000L;
                break;
            case ENDER_DRAGON:
                n = arena.getPlayingTask().getDragonSpawnCountdown() * 1000L;
        }
        return n == 0L ? "0" : this.nextEventDateFormat.format(new Date(n));
    }
    
    public void updateSpectator(final Player player, final boolean b) {
    }
    
    public void invisibilityPotion(final ITeam team, final Player player, final boolean b) {
        if (b) {
            this.handle.playerListHideNameTag(player);
        } else {
            this.handle.playerListRestoreNameTag(player);
        }
    }
    
    public void addToTabList(final Player player, final String s, final String s2) {
        this.handle.playerListCreate(player, this.getTeamListText(s, player), this.getTeamListText(s2, player));
        if (this.arena != null) {
            this.handle.playerListAddPlaceholders(player, new PlaceholderProvider("{team}", () -> {
                if (this.arena.isSpectator(player))
                    return Language.getMsg(this.getPlayer(), Messages.FORMATTING_SPECTATOR_COLOR) + Language.getMsg(this.getPlayer(), Messages.FORMATTING_SPECTATOR_TEAM);
                ITeam iTeam = this.arena.getTeam(player);
                if (iTeam == null) iTeam = this.arena.getExTeam(player.getUniqueId());
                return (iTeam == null) ? "" : (iTeam.getColor().chat() + iTeam.getDisplayName(Language.getPlayerLanguage(getPlayer())));
            }), new PlaceholderProvider("{tL}", () -> {
                if (this.arena.isSpectator(player))
                    return Language.getMsg(this.getPlayer(), Messages.FORMATTING_SPECTATOR_TEAM).substring(0, 1);
                ITeam iTeam = this.arena.getTeam(player);
                if (iTeam == null) iTeam = this.arena.getExTeam(player.getUniqueId());
                return (iTeam == null) ? "" : iTeam.getDisplayName(Language.getPlayerLanguage(getPlayer())).substring(0, 1);
            }), new PlaceholderProvider("{tN}", () -> {
                if (this.arena.isSpectator(player))
                    return Language.getMsg(getPlayer(), Messages.FORMATTING_SPECTATOR_TEAM);
                ITeam iTeam = this.arena.getTeam(player);
                if (iTeam == null) iTeam = this.arena.getExTeam(player.getUniqueId());
                return (iTeam == null) ? "" : iTeam.getDisplayName(Language.getPlayerLanguage(getPlayer()));
            }), new PlaceholderProvider("{tC}", () -> {
                if (this.arena.isSpectator(player))
                    return Language.getMsg(getPlayer(), Messages.FORMATTING_SPECTATOR_COLOR);
                ITeam iTeam = this.arena.getTeam(player);
                if (iTeam == null) iTeam = this.arena.getExTeam(player.getUniqueId());
                return (iTeam == null) ? "" : iTeam.getColor().chat().toString();
            }));
        }
    }

    private SidebarLine getTeamListText(String paramString, Player paramPlayer) {
        List<String> list = Language.getList(getPlayer(), paramString);
        if (list.isEmpty())
            return new SidebarLine() {
                public String getLine() {
                    return "";
                }
            };
        list = new ArrayList<>();
        for (String str : Language.getList(getPlayer(), paramString))
            list.add(str.replace("{vPrefix}", BedWars.getInstance().getChat().getPrefix(paramPlayer)).replace("{vSuffix}", BedWars.getInstance().getChat().getSuffix(paramPlayer)));
        if (list.size() == 1) {
            final String line = list.get(0);
            return new SidebarLine() {
                public String getLine() {
                    return line;
                }
            };
        }
        String[] arrayOfString = new String[list.size()];
        for (int b = 0; b < arrayOfString.length; b++) arrayOfString[b] = list.get(b);
        return new SidebarLineAnimated(arrayOfString);
    }
    
    static {
        BedWarsScoreboard.sidebarManager = null;
    }
}
