package dev.eugenio.nasgarbedwars.libs.sidebar;

import org.bukkit.Bukkit;

import java.util.Collection;

public class SidebarManager {
    private final SidebarProvider sidebarProvider;

    public SidebarManager() throws InstantiationException {
        try {
            this.sidebarProvider = (SidebarProvider) Class.forName("dev.eugenio.nasgarbedwars.libs.sidebar.Provider_" + Bukkit.getServer().getClass().getName().split("\\.")[3]).newInstance();
        } catch (ClassNotFoundException | IllegalAccessException ex) {
            throw new InstantiationException("Servidor no soportado.");
        }
    }

    public Sidebar createSidebar(final SidebarLine sidebarLine2, final Collection<SidebarLine> collection, final Collection<PlaceholderProvider> collection2) {
        collection.forEach(sidebarLine -> collection2.forEach(placeholderProvider -> {
            if (sidebarLine.getLine().contains(placeholderProvider.getPlaceholder())) {
                sidebarLine.setHasPlaceholders(true);
            }
        }));
        return this.sidebarProvider.createSidebar(sidebarLine2, collection, collection2);
    }
}
