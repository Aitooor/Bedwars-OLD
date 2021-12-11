package dev.eugenio.nasgarbedwars.sidebar;

public class SidebarListRefresh implements Runnable {
    @Override
    public void run() {
        for (BedWarsScoreboard bedWarsScoreboard : BedWarsScoreboard.getScoreboards().values()) bedWarsScoreboard.getHandle().playerListRefreshAnimation();
    }
}
