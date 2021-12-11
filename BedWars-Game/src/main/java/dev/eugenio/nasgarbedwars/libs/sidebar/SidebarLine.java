package dev.eugenio.nasgarbedwars.libs.sidebar;


public abstract class SidebarLine {
    private boolean hasPlaceholders;

    public SidebarLine() {
        this.hasPlaceholders = false;
    }

    public abstract String getLine();

    public boolean isHasPlaceholders() {
        return this.hasPlaceholders;
    }

    public void setHasPlaceholders(final boolean hasPlaceholders) {
        this.hasPlaceholders = hasPlaceholders;
    }
}
