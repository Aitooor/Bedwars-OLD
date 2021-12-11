package dev.eugenio.nasgarbedwars.libs.sidebar;

interface PlayerList {
    void setPrefix(final SidebarLine p0);

    void setSuffix(final SidebarLine p0);

    void addPlayer(final String p0);

    void removePlayer(final String p0);

    void refreshAnimations();

    void addPlaceholderProvider(final PlaceholderProvider p0);

    void removePlaceholderProvider(final String p0);

    void hideNameTag();

    void showNameTag();
}
