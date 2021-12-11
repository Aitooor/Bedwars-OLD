package dev.eugenio.nasgarbedwars.database;

import dev.eugenio.nasgarbedwars.stats.PlayerStats;

import java.util.UUID;

public interface Database {
    void init();
    
    boolean hasStats(final UUID p0);

    void saveStats(final PlayerStats p0);

    PlayerStats fetchStats(final UUID p0);
    
    void close();
    
    void setQuickBuySlot(final UUID p0, final String p1, final int p2);
    
    String getQuickBuySlots(final UUID p0, final int p1);
    
    boolean hasQuickBuy(final UUID p0);
    
    int getColumn(final UUID p0, final String p1);
    
    Object[] getLevelData(final UUID p0);
    
    void setLevelData(final UUID p0, final int p1, final int p2, final String p3, final int p4);
    
    void setLanguage(final UUID p0, final String p1);
    
    String getLanguage(final UUID p0);
}
