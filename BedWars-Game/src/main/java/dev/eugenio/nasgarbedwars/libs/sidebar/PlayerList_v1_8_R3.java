package dev.eugenio.nasgarbedwars.libs.sidebar;

import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.ScoreboardTeam;
import net.minecraft.server.v1_8_R3.ScoreboardTeamBase;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.LinkedList;

public class PlayerList_v1_8_R3 extends ScoreboardTeam implements PlayerList {
    private final Sidebar_v1_8_R3 sidebar;
    private final LinkedList<PlaceholderProvider> placeholderProviders;
    private SidebarLine prefix;
    private SidebarLine suffix;
    private ScoreboardTeamBase.EnumNameTagVisibility nameTagVisibility;

    public PlayerList_v1_8_R3(final Sidebar_v1_8_R3 sidebar, final Player player, final SidebarLine prefix, final SidebarLine suffix) {
        super(null, player.getName());
        this.placeholderProviders = new LinkedList<>();
        this.nameTagVisibility = ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS;
        this.suffix = suffix;
        this.prefix = prefix;
        this.sidebar = sidebar;
        this.getPlayerNameSet().add(player.getName());
    }

    public String getPrefix() {
        String s = this.prefix.getLine();
        for (final PlaceholderProvider placeholderProvider : this.placeholderProviders)
            if (s.contains(placeholderProvider.getPlaceholder()))
                s = s.replace(placeholderProvider.getPlaceholder(), placeholderProvider.getReplacement());
        return s;
    }

    public void setPrefix(final String s) {
    }

    public void setPrefix(final SidebarLine prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        String s = this.suffix.getLine();
        for (final PlaceholderProvider placeholderProvider : this.placeholderProviders)
            if (s.contains(placeholderProvider.getPlaceholder()))
                s = s.replace(placeholderProvider.getPlaceholder(), placeholderProvider.getReplacement());
        return s;
    }

    public void setSuffix(final String s) {
    }

    public void setSuffix(final SidebarLine suffix) {
        this.suffix = suffix;
    }

    public void setAllowFriendlyFire(final boolean b) {
    }

    public void setCanSeeFriendlyInvisibles(final boolean b) {
    }

    public void addPlayer(final String s) {
        this.getPlayerNameSet().add(s);
        CustomScore_v1_8_R3.sendScore(this.sidebar, s, 20);
        this.sidebar.players.forEach(playerConnection -> playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(this, Collections.singleton(s), 3)));
    }

    public void removePlayer(final String s) {
        this.getPlayerNameSet().remove(s);
        this.sidebar.players.forEach(playerConnection -> playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(this, Collections.singleton(s), 4)));
    }

    public void refreshAnimations() {
    }

    public void addPlaceholderProvider(final PlaceholderProvider placeholderProvider) {
        this.placeholderProviders.remove(placeholderProvider);
        this.placeholderProviders.add(placeholderProvider);
        this.placeholderProviders.forEach(placeholderProvider2 -> {
            if (this.prefix.getLine().contains(placeholderProvider2.getPlaceholder()))
                this.prefix.setHasPlaceholders(true);
            if (this.suffix.getLine().contains(placeholderProvider2.getPlaceholder()))
                this.suffix.setHasPlaceholders(true);
        });
    }

    public void removePlaceholderProvider(final String s) {
        this.placeholderProviders.removeIf(placeholderProvider -> placeholderProvider.getPlaceholder().equalsIgnoreCase(s));
    }

    public void hideNameTag() {
        this.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
        this.sendUpdate();
    }

    public void showNameTag() {
        this.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS);
        this.sendUpdate();
    }

    public ScoreboardTeamBase.EnumNameTagVisibility getNameTagVisibility() {
        return this.nameTagVisibility;
    }

    public void setNameTagVisibility(final ScoreboardTeamBase.EnumNameTagVisibility nameTagVisibility) {
        this.nameTagVisibility = nameTagVisibility;
    }

    public void sendCreate(final PlayerConnection playerConnection) {
        playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(this, 0));
    }

    public void sendUpdate() {
        this.sidebar.players.forEach(playerConnection -> playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(this, 2)));
    }

    public void sendRemove(final PlayerConnection playerConnection) {
        playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(this, 1));
    }

    public boolean equals(final Object o) {
        return this == o || o == null || (o instanceof PlayerList_v1_8_R3 && ((PlayerList_v1_8_R3) o).getName().equals(this.getName()));
    }
}
