package dev.eugenio.nasgarbedwars.libs.sidebar;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface Sidebar {
    void addPlaceholder(final PlaceholderProvider p0);

    void addLine(final SidebarLine p0);

    void setLine(final SidebarLine p0, final int p1);

    void setTitle(final SidebarLine p0);

    void refreshPlaceholders();

    void refreshTitle();

    void remove(final UUID p0);

    void apply(final Player p0);

    void refreshAnimatedLines();

    void removeLine(final int p0);

    int linesAmount();

    void removePlaceholder(final String p0);

    List<PlaceholderProvider> getPlaceholders();

    default void playerListCreate(final Player player, final SidebarLine prefix, final SidebarLine suffix) {
        this.playerListCreate(player, prefix, suffix, true);
    }

    void playerListCreate(final Player p0, final SidebarLine p1, final SidebarLine p2, final boolean p3);

    void playerListAddPlaceholders(final Player p0, final PlaceholderProvider... p1);

    void playerListRemovePlaceholder(final Player p0, final String p1);

    void playerListRemove(final String p0);

    void playerListClear();

    void showPlayersHealth(final SidebarLine p0, final boolean p1);

    void hidePlayersHealth();

    void refreshHealthAnimation();

    void refreshHealth(final Player p0, final int p1);

    void playerListRefreshAnimation();

    void playerListHideNameTag(final Player p0);

    void playerListRestoreNameTag(final Player p0);
}
