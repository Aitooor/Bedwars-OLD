package dev.eugenio.nasgarbedwars.libs.sidebar;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class Sidebar_v1_8_R3 implements Sidebar {
    private final LinkedList<ScoreLine> lines = new LinkedList<>();
    private final LinkedList<String> availableColors = new LinkedList<>();
    private final SidebarObjective sidebarObjective;
    private final ConcurrentHashMap<String, PlayerList_v1_8_R3> teamLists = new ConcurrentHashMap<>();
    protected LinkedList<PlayerConnection> players = new LinkedList<>();
    protected LinkedList<PlaceholderProvider> placeholderProviders = new LinkedList<>();
    protected SidebarObjective healthObjective = null;

    public Sidebar_v1_8_R3(SidebarLine paramSidebarLine, Collection<SidebarLine> paramCollection, Collection<PlaceholderProvider> paramCollection1) {
        for (ChatColor chatColor : ChatColor.values()) this.availableColors.add(chatColor.toString());
        this.sidebarObjective = new SidebarObjective("Sidebar", IScoreboardCriteria.b, 1, paramSidebarLine);
        this.placeholderProviders.addAll(paramCollection1);
        for (SidebarLine sidebarLine : paramCollection) addLine(sidebarLine);
    }

    private static void scoreOffsetIncrease(Collection<ScoreLine> paramCollection) {
        paramCollection.forEach(paramScoreLine -> paramScoreLine.setScore(paramScoreLine.getScore() + 1));
    }

    private static void scoreOffsetDecrease(Collection<ScoreLine> paramCollection) {
        paramCollection.forEach(paramScoreLine -> paramScoreLine.setScore(paramScoreLine.getScore() - 1));
    }

    public void setTitle(SidebarLine paramSidebarLine) {
        this.sidebarObjective.displayName = paramSidebarLine;
        this.sidebarObjective.sendUpdate();
    }

    public void addPlaceholder(PlaceholderProvider paramPlaceholderProvider) {
        this.placeholderProviders.remove(paramPlaceholderProvider);
        this.placeholderProviders.add(paramPlaceholderProvider);
        this.lines.forEach(paramScoreLine -> {
            if (!paramScoreLine.text.isHasPlaceholders())
                if (paramScoreLine.text instanceof SidebarLineAnimated) {
                    for (String str : ((SidebarLineAnimated) paramScoreLine.text).getLines()) {
                        if (str.contains(paramPlaceholderProvider.getPlaceholder())) {
                            paramScoreLine.text.setHasPlaceholders(true);
                            break;
                        }
                    }
                } else if (paramScoreLine.text.getLine().contains(paramPlaceholderProvider.getPlaceholder())) {
                    paramScoreLine.text.setHasPlaceholders(true);
                }
        });
    }

    public void addLine(SidebarLine paramSidebarLine) {
        int i = getAvailableScore();
        if (i == -1) return;
        scoreOffsetIncrease(this.lines);
        String str = this.availableColors.get(0);
        this.availableColors.remove(0);
        ScoreLine scoreLine = new ScoreLine(paramSidebarLine, (i == 0) ? i : (i - 1), str);
        scoreLine.sendCreate();
        this.lines.add(scoreLine);
        order();
    }

    public void setLine(SidebarLine paramSidebarLine, int paramInt) {
        if (paramInt >= 0 && paramInt < this.lines.size()) {
            ScoreLine scoreLine = this.lines.get(paramInt);
            this.placeholderProviders.forEach(paramPlaceholderProvider -> {
                if (paramSidebarLine.getLine().contains(paramPlaceholderProvider.getPlaceholder()))
                    paramSidebarLine.setHasPlaceholders(true);
            });
            scoreLine.setText(paramSidebarLine);
        }
    }

    private int getAvailableScore() {
        if (this.lines.isEmpty()) return 0;
        if (this.lines.size() == 16) return -1;
        return this.lines.getFirst().getScore();
    }

    private void order() {
        Collections.sort(this.lines);
    }

    public void apply(Player paramPlayer) {
        PlayerConnection playerConnection = (((CraftPlayer) paramPlayer).getHandle()).playerConnection;
        this.sidebarObjective.sendCreate(playerConnection);
        this.lines.forEach(paramScoreLine -> paramScoreLine.sendCreate(playerConnection));
        this.players.add(playerConnection);
        if (this.healthObjective != null) {
            this.healthObjective.sendCreate(playerConnection);
            this.teamLists.forEach((paramString, paramPlayerList_v1_8_R3) -> paramPlayerList_v1_8_R3.sendCreate(playerConnection));
        }
    }

    public void refreshPlaceholders() {
        this.lines.forEach(paramScoreLine -> {
            if (paramScoreLine.text.isHasPlaceholders()) {
                String str = paramScoreLine.text.getLine();
                for (PlaceholderProvider placeholderProvider : this.placeholderProviders)
                    if (str.contains(placeholderProvider.getPlaceholder()))
                        str = str.replace(placeholderProvider.getPlaceholder(), placeholderProvider.getReplacement());
                paramScoreLine.setContent(str);
                paramScoreLine.sendUpdate();
            }
        });
    }

    public void refreshTitle() {
        this.sidebarObjective.sendUpdate();
    }

    public void refreshAnimatedLines() {
        this.lines.forEach(paramScoreLine -> {
            if (paramScoreLine.text instanceof SidebarLineAnimated) {
                if (paramScoreLine.text.isHasPlaceholders()) {
                    String str = paramScoreLine.text.getLine();
                    for (PlaceholderProvider placeholderProvider : this.placeholderProviders)
                        if (str.contains(placeholderProvider.getPlaceholder()))
                            str = str.replace(placeholderProvider.getPlaceholder(), placeholderProvider.getReplacement());
                    paramScoreLine.setContent(str);
                } else {
                    paramScoreLine.setContent(paramScoreLine.text.getLine());
                }
                paramScoreLine.sendUpdate();
            }
        });
    }

    public void removeLine(int paramInt) {
        if (paramInt >= 0 && paramInt < this.lines.size()) {
            ScoreLine scoreLine = this.lines.get(paramInt);
            scoreLine.remove();
            scoreOffsetDecrease(this.lines.subList(paramInt, this.lines.size()));
        }
    }

    public int linesAmount() {
        return this.lines.size();
    }

    public void removePlaceholder(String paramString) {
        this.placeholderProviders.removeIf(paramPlaceholderProvider -> paramPlaceholderProvider.getPlaceholder().equalsIgnoreCase(paramString));
    }

    public List<PlaceholderProvider> getPlaceholders() {
        return Collections.unmodifiableList(this.placeholderProviders);
    }

    public void playerListCreate(Player paramPlayer, SidebarLine paramSidebarLine1, SidebarLine paramSidebarLine2, boolean paramBoolean) {
        if (this.teamLists.containsKey(paramPlayer.getName()))
            playerListRemove(paramPlayer.getName());
        PlayerList_v1_8_R3 playerList_v1_8_R3 = new PlayerList_v1_8_R3(this, paramPlayer, paramSidebarLine1, paramSidebarLine2);
        this.players.forEach(playerList_v1_8_R3::sendCreate);
        this.teamLists.put(paramPlayer.getName(), playerList_v1_8_R3);
    }

    public void playerListAddPlaceholders(Player paramPlayer, PlaceholderProvider... paramVarArgs) {
        PlayerList_v1_8_R3 playerList_v1_8_R3 = this.teamLists.getOrDefault(paramPlayer.getName(), null);
        if (playerList_v1_8_R3 == null) return;
        for (PlaceholderProvider placeholderProvider : paramVarArgs)
            playerList_v1_8_R3.addPlaceholderProvider(placeholderProvider);
        playerList_v1_8_R3.sendUpdate();
    }

    public void playerListRemovePlaceholder(Player paramPlayer, String paramString) {
        PlayerList_v1_8_R3 playerList_v1_8_R3 = this.teamLists.getOrDefault(paramPlayer.getName(), null);
        if (playerList_v1_8_R3 == null) return;
        playerList_v1_8_R3.removePlaceholderProvider(paramString);
        playerList_v1_8_R3.sendUpdate();
    }

    public void playerListRemove(String paramString) {
        PlayerList_v1_8_R3 playerList_v1_8_R3 = this.teamLists.getOrDefault(paramString, null);
        if (playerList_v1_8_R3 != null) {
            this.players.forEach(playerList_v1_8_R3::sendRemove);
            this.teamLists.remove(paramString);
        }
    }

    public void playerListClear() {
        this.teamLists.forEach((paramString, paramPlayerList_v1_8_R3) -> this.players.forEach(paramPlayerList_v1_8_R3::sendRemove));
        this.teamLists.clear();
    }

    public void showPlayersHealth(SidebarLine paramSidebarLine, boolean paramBoolean) {
        if (this.healthObjective == null) {
            this.healthObjective = new SidebarObjective(paramBoolean ? "health" : "health2", IScoreboardCriteria.b, 2, paramSidebarLine);
            this.players.forEach(paramPlayerConnection -> this.healthObjective.sendCreate(paramPlayerConnection));
        } else {
            this.healthObjective.sendUpdate();
        }
    }

    public void hidePlayersHealth() {
        if (this.healthObjective != null) {
            this.players.forEach(paramPlayerConnection -> this.healthObjective.sendRemove(paramPlayerConnection));
            this.healthObjective = null;
        }
    }

    public void refreshHealthAnimation() {
        if (this.healthObjective != null &&
                this.healthObjective.displayName instanceof SidebarLineAnimated)
            this.healthObjective.sendUpdate();
    }

    public void refreshHealth(Player paramPlayer, int paramInt) {
        if (paramInt < 0)
            paramInt = 0;
        CustomScore_v1_8_R3.sendScore(this, paramPlayer.getName(), paramInt);
    }

    public void playerListRefreshAnimation() {
        this.teamLists.forEach((paramString, paramPlayerList_v1_8_R3) -> paramPlayerList_v1_8_R3.sendUpdate());
    }

    public void playerListHideNameTag(Player paramPlayer) {
        PlayerList_v1_8_R3 playerList_v1_8_R3 = this.teamLists.get(paramPlayer.getName());
        if (playerList_v1_8_R3 != null) playerList_v1_8_R3.hideNameTag();
    }

    public void playerListRestoreNameTag(Player paramPlayer) {
        PlayerList_v1_8_R3 playerList_v1_8_R3 = this.teamLists.get(paramPlayer.getName());
        if (playerList_v1_8_R3 != null) playerList_v1_8_R3.showNameTag();
    }

    public void remove(UUID paramUUID) {
        this.players.removeIf(paramPlayerConnection -> paramPlayerConnection.player.getUniqueID().equals(paramUUID));
        Player player = Bukkit.getPlayer(paramUUID);
        if (player != null && player.isOnline()) {
            PlayerConnection playerConnection = (((CraftPlayer) player).getHandle()).playerConnection;
            this.sidebarObjective.sendRemove(playerConnection);
            if (this.healthObjective != null) this.healthObjective.sendRemove(playerConnection);
            this.teamLists.forEach((paramString, paramPlayerList_v1_8_R3) -> paramPlayerList_v1_8_R3.sendRemove(playerConnection));
        }
    }

    private class SidebarObjective extends ScoreboardObjective {
        private final int type;

        private final IScoreboardCriteria.EnumScoreboardHealthDisplay health = IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER;

        private SidebarLine displayName;

        public SidebarObjective(String param1String, IScoreboardCriteria param1IScoreboardCriteria, int param1Int, SidebarLine param1SidebarLine) {
            super(null, param1String, param1IScoreboardCriteria);
            this.type = param1Int;
            this.displayName = param1SidebarLine;
        }

        public String getDisplayName() {
            String str = this.displayName.getLine();
            if (str.length() > 16)
                str = str.substring(0, 16);
            return str;
        }

        public void setDisplayName(String param1String) {
        }

        private void sendCreate(PlayerConnection param1PlayerConnection) {
            PacketPlayOutScoreboardObjective packetPlayOutScoreboardObjective = new PacketPlayOutScoreboardObjective(this, 0);
            param1PlayerConnection.sendPacket(packetPlayOutScoreboardObjective);
            PacketPlayOutScoreboardDisplayObjective packetPlayOutScoreboardDisplayObjective = new PacketPlayOutScoreboardDisplayObjective(this.type, this);
            param1PlayerConnection.sendPacket(packetPlayOutScoreboardDisplayObjective);
            if (getName().equalsIgnoreCase("health")) {
                PacketPlayOutScoreboardDisplayObjective packetPlayOutScoreboardDisplayObjective1 = new PacketPlayOutScoreboardDisplayObjective(0, this);
                param1PlayerConnection.sendPacket(packetPlayOutScoreboardDisplayObjective1);
            }
        }

        private void sendUpdate() {
            PacketPlayOutScoreboardObjective packetPlayOutScoreboardObjective = new PacketPlayOutScoreboardObjective(this, 2);
            Sidebar_v1_8_R3.this.players.forEach(param1PlayerConnection -> param1PlayerConnection.sendPacket(packetPlayOutScoreboardObjective));
        }

        public void sendRemove(PlayerConnection param1PlayerConnection) {
            Sidebar_v1_8_R3.this.teamLists.forEach((param1String, param1PlayerList_v1_8_R3) -> Sidebar_v1_8_R3.this.players.forEach(param1PlayerList_v1_8_R3::sendRemove));
            PacketPlayOutScoreboardObjective packetPlayOutScoreboardObjective = new PacketPlayOutScoreboardObjective(this, 1);
            param1PlayerConnection.sendPacket(packetPlayOutScoreboardObjective);
        }

        public IScoreboardCriteria.EnumScoreboardHealthDisplay e() {
            return this.health;
        }
    }

    private class ScoreLine extends ScoreboardScore implements Comparable<ScoreLine> {
        @Getter
        private int score;

        private String prefix = "";

        private String suffix = "";

        private TeamLine team;

        private SidebarLine text;

        public ScoreLine(SidebarLine param1SidebarLine, int param1Int, String param1String) {
            super(null, Sidebar_v1_8_R3.this.sidebarObjective, param1String);
            this.score = param1Int;
            this.text = param1SidebarLine;
            this.team = new TeamLine(param1String);
            if (!param1SidebarLine.isHasPlaceholders()) {
                Sidebar_v1_8_R3.this.placeholderProviders.forEach(param1PlaceholderProvider -> {
                    if (param1SidebarLine.getLine().contains(param1PlaceholderProvider.getPlaceholder()))
                        param1SidebarLine.setHasPlaceholders(true);
                });
            }
            if (param1SidebarLine.isHasPlaceholders()) {
                String str = param1SidebarLine.getLine();
                for (PlaceholderProvider placeholderProvider : Sidebar_v1_8_R3.this.placeholderProviders)
                    if (str.contains(placeholderProvider.getPlaceholder()))
                        str = str.replace(placeholderProvider.getPlaceholder(), placeholderProvider.getReplacement());
                setContent(str);
            } else {
                setContent(param1SidebarLine.getLine());
            }
        }

        private void setText(SidebarLine param1SidebarLine) {
            this.text = param1SidebarLine;
            setContent(param1SidebarLine.getLine());
            sendUpdate();
        }

        private void sendCreate(PlayerConnection param1PlayerConnection) {
            PacketPlayOutScoreboardTeam packetPlayOutScoreboardTeam = new PacketPlayOutScoreboardTeam(this.team, 0);
            param1PlayerConnection.sendPacket(packetPlayOutScoreboardTeam);
            PacketPlayOutScoreboardScore packetPlayOutScoreboardScore = new PacketPlayOutScoreboardScore(this);
            param1PlayerConnection.sendPacket(packetPlayOutScoreboardScore);
        }

        private void sendCreate() {
            PacketPlayOutScoreboardTeam packetPlayOutScoreboardTeam = new PacketPlayOutScoreboardTeam(this.team, 0);
            PacketPlayOutScoreboardScore packetPlayOutScoreboardScore = new PacketPlayOutScoreboardScore(this);
            Sidebar_v1_8_R3.this.players.forEach(param1PlayerConnection -> {
                param1PlayerConnection.sendPacket(packetPlayOutScoreboardTeam);
                param1PlayerConnection.sendPacket(packetPlayOutScoreboardScore);
            });
        }

        private void remove() {
            Sidebar_v1_8_R3.this.lines.remove(this);
            PacketPlayOutScoreboardTeam packetPlayOutScoreboardTeam = new PacketPlayOutScoreboardTeam(this.team, 1);
            PacketPlayOutScoreboardScore packetPlayOutScoreboardScore = new PacketPlayOutScoreboardScore(getPlayerName(), Sidebar_v1_8_R3.this.sidebarObjective);
            Sidebar_v1_8_R3.this.players.forEach(param1PlayerConnection -> {
                param1PlayerConnection.sendPacket(packetPlayOutScoreboardTeam);
                param1PlayerConnection.sendPacket(packetPlayOutScoreboardScore);
            });
            Sidebar_v1_8_R3.this.availableColors.add(getColor());
            this.text = null;
            this.team = null;
            this.prefix = null;
            this.suffix = null;
        }

        private void setContent(String param1String) {
            if (param1String.length() > 16) {
                this.prefix = param1String.substring(0, 16);
                if (this.prefix.charAt(15) == '§') {
                    this.prefix = param1String.substring(0, 15);
                    setSuffix(param1String.substring(15));
                } else {
                    setSuffix(param1String.substring(16));
                }
            } else {
                this.prefix = param1String;
                this.suffix = "";
            }
        }

        public void setSuffix(String param1String) {
            if (param1String.isEmpty()) {
                this.suffix = "";
                return;
            }
            param1String = ChatColor.getLastColors(this.prefix) + param1String;
            this.suffix = (param1String.length() > 16) ? param1String.substring(0, 16) : param1String;
        }

        private void sendUpdate() {
            PacketPlayOutScoreboardTeam packetPlayOutScoreboardTeam = new PacketPlayOutScoreboardTeam(this.team, 2);
            Sidebar_v1_8_R3.this.players.forEach(param1PlayerConnection -> param1PlayerConnection.sendPacket(packetPlayOutScoreboardTeam));
        }

        public int compareTo(ScoreLine param1ScoreLine) {
            return Integer.compare(this.score, param1ScoreLine.score);
        }

        public void setScore(int param1Int) {
            this.score = param1Int;
            PacketPlayOutScoreboardScore packetPlayOutScoreboardScore = new PacketPlayOutScoreboardScore(this);
            Sidebar_v1_8_R3.this.players.forEach(param1PlayerConnection -> param1PlayerConnection.sendPacket(packetPlayOutScoreboardScore));
        }

        public void updateForList(List<EntityHuman> param1List) {
        }

        public void addScore(int param1Int) {
        }

        public void incrementScore() {
        }

        public String getColor() {
            return (this.team.getName().charAt(0) == '§') ? this.team.getName() : ('§' + this.team.getName());
        }

        private class TeamLine extends ScoreboardTeam {
            public TeamLine(String param2String) {
                super(null, param2String);
                getPlayerNameSet().add(param2String);
            }

            public String getPrefix() {
                return Sidebar_v1_8_R3.ScoreLine.this.prefix;
            }

            public void setPrefix(String param2String) {
            }

            public String getSuffix() {
                return Sidebar_v1_8_R3.ScoreLine.this.suffix;
            }

            public void setSuffix(String param2String) {
            }

            public void setAllowFriendlyFire(boolean param2Boolean) {
            }

            public void setCanSeeFriendlyInvisibles(boolean param2Boolean) {
            }

            public void setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility param2EnumNameTagVisibility) {
            }
        }
    }
}
