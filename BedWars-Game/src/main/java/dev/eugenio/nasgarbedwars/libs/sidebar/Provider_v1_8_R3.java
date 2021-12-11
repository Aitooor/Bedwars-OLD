package dev.eugenio.nasgarbedwars.libs.sidebar;

import java.util.Collection;

class Provider_v1_8_R3 extends SidebarProvider {
    @Override
    public Sidebar createSidebar(SidebarLine sidebarLine, Collection<SidebarLine> collection, Collection<PlaceholderProvider> collection2) {
        return new Sidebar_v1_8_R3(sidebarLine, collection, collection2);
    }
}