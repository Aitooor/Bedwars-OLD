package dev.eugenio.nasgarbedwars.levels.internal;

import dev.eugenio.nasgarbedwars.configuration.LevelsConfig;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerLevelUpEvent;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerXpGainEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerLevel {
    @Getter
    private final UUID uuid;
    @Getter
    private int level;
    @Getter
    private int nextLevelCost;
    @Getter
    private String levelName;
    @Getter
    private int currentXp;
    private String progressBar;
    private String requiredXp;
    @Getter
    private String formattedCurrentXp;
    private boolean modified;
    private static ConcurrentHashMap<UUID, PlayerLevel> levelByPlayer;
    
    public PlayerLevel(final UUID uuid, int n, int currentXp) {
        this.modified = false;
        this.uuid = uuid;
        this.setLevelName(n);
        this.setNextLevelCost(n, true);
        if (n < 1) n = 1;
        if (currentXp < 0) currentXp = 0;
        this.level = n;
        this.currentXp = currentXp;
        this.updateProgressBar();
        if (!PlayerLevel.levelByPlayer.containsKey(uuid)) PlayerLevel.levelByPlayer.put(uuid, this);
    }
    
    public void setLevelName(final int n) {
        this.levelName = ChatColor.translateAlternateColorCodes('&', LevelsConfig.getLevelName(n)).replace("{number}", String.valueOf(n));
    }
    
    public void setNextLevelCost(final int n, final boolean b) {
        if (!b) this.modified = true;
        this.nextLevelCost = LevelsConfig.getNextCost(n);
    }
    
    public void lazyLoad(int n, int currentXp) {
        this.modified = false;
        if (n < 1) n = 1;
        if (currentXp < 0) currentXp = 0;
        this.setLevelName(n);
        this.setNextLevelCost(n, true);
        this.level = n;
        this.currentXp = currentXp;
        this.updateProgressBar();
        this.modified = false;
    }
    
    private void updateProgressBar() {
        int n = (int)((this.nextLevelCost - this.currentXp) / this.nextLevelCost * 10.0);
        int n2 = 10 - n;
        if (n < 0 || n2 < 0) {
            n = 10;
            n2 = 0;
        }
        this.progressBar = ChatColor.translateAlternateColorCodes('&', LevelsConfig.levels.getString("progress-bar.format").replace("{progress}", LevelsConfig.levels.getString("progress-bar.unlocked-color") + String.valueOf(new char[n2]).replace("\u0000", LevelsConfig.levels.getString("progress-bar.symbol")) + LevelsConfig.levels.getString("progress-bar.locked-color") + String.valueOf(new char[n]).replace("\u0000", LevelsConfig.levels.getString("progress-bar.symbol"))));
        this.requiredXp = ((this.nextLevelCost >= 1000) ? ((this.nextLevelCost % 1000 == 0) ? (this.nextLevelCost / 1000 + "k") : (this.nextLevelCost / 1000.0 + "k")) : String.valueOf(this.nextLevelCost));
        this.formattedCurrentXp = ((this.currentXp >= 1000) ? ((this.currentXp % 1000 == 0) ? (this.currentXp / 1000 + "k") : (this.currentXp / 1000.0 + "k")) : String.valueOf(this.currentXp));
    }
    
    public static PlayerLevel getLevelByPlayer(final UUID uuid) {
        return PlayerLevel.levelByPlayer.getOrDefault(uuid, new PlayerLevel(uuid, 1, 0));
    }
    
    public String getProgress() {
        return this.progressBar;
    }
    
    public String getFormattedRequiredXp() {
        return this.requiredXp;
    }
    
    public void addXp(final int n, final PlayerXpGainEvent.XpSource xpSource) {
        if (n < 0) return;
        this.currentXp += n;
        this.upgradeLevel();
        this.updateProgressBar();
        Bukkit.getPluginManager().callEvent(new PlayerXpGainEvent(Bukkit.getPlayer(this.uuid), n, xpSource));
        this.modified = true;
    }
    
    public void setXp(int currentXp) {
        if (currentXp <= 0) currentXp = 0;
        this.currentXp = currentXp;
        this.upgradeLevel();
        this.updateProgressBar();
        this.modified = true;
    }
    
    public void setLevel(final int level) {
        this.level = level;
        this.nextLevelCost = LevelsConfig.getNextCost(level);
        this.levelName = ChatColor.translateAlternateColorCodes('&', LevelsConfig.getLevelName(level)).replace("{number}", String.valueOf(level));
        this.requiredXp = ((this.nextLevelCost >= 1000) ? ((this.nextLevelCost % 1000 == 0) ? (this.nextLevelCost / 1000 + "k") : (this.nextLevelCost / 1000.0 + "k")) : String.valueOf(this.nextLevelCost));
        this.updateProgressBar();
        this.modified = true;
    }
    
    public void upgradeLevel() {
        if (this.currentXp >= this.nextLevelCost) {
            this.currentXp -= this.nextLevelCost;
            ++this.level;
            this.nextLevelCost = LevelsConfig.getNextCost(this.level);
            this.levelName = ChatColor.translateAlternateColorCodes('&', LevelsConfig.getLevelName(this.level)).replace("{number}", String.valueOf(this.level));
            this.requiredXp = ((this.nextLevelCost >= 1000) ? ((this.nextLevelCost % 1000 == 0) ? (this.nextLevelCost / 1000 + "k") : (this.nextLevelCost / 1000.0 + "k")) : String.valueOf(this.nextLevelCost));
            this.formattedCurrentXp = ((this.currentXp >= 1000) ? ((this.currentXp % 1000 == 0) ? (this.currentXp / 1000 + "k") : (this.currentXp / 1000.0 + "k")) : String.valueOf(this.currentXp));
            Bukkit.getPluginManager().callEvent(new PlayerLevelUpEvent(Bukkit.getPlayer(this.getUuid()), this.level, this.nextLevelCost));
            this.modified = true;
        }
    }
    
    public int getPlayerLevel() {
        return this.level;
    }
    
    public void destroy() {
        PlayerLevel.levelByPlayer.remove(this.uuid);
        this.updateDatabase();
    }
    
    public void updateDatabase() {
        if (this.modified) {
            Bukkit.getScheduler().runTaskAsynchronously(BedWars.getInstance(), () -> BedWars.getInstance().getMySQLDatabase().setLevelData(this.uuid, this.level, this.currentXp, LevelsConfig.getLevelName(this.level), this.nextLevelCost));
            this.modified = false;
        }
    }
    
    static {
        PlayerLevel.levelByPlayer = new ConcurrentHashMap<>();
    }
}
