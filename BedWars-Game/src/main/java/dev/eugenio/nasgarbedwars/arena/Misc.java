package dev.eugenio.nasgarbedwars.arena;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.stats.PlayerStats;
import dev.eugenio.nasgarbedwars.api.arena.GameStatus;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.generator.IGenerator;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.exceptions.InvalidMaterialException;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;

public class Misc {
    public static void moveToLobbyOrKick(final Player player, @Nullable final IArena arena, final boolean b) {
        forceKick(player, arena, b);
    }

    private static void forceKick(final Player player, @Nullable final IArena arena, final boolean b) {
        final ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
        dataOutput.writeUTF("Connect");
        dataOutput.writeUTF(BedWars.getInstance().getMainConfig().getYml().getString("lobbyServer"));
        player.sendPluginMessage(BedWars.getInstance(), "BungeeCord", dataOutput.toByteArray());
        if (arena != null && !b && arena.getStatus() == GameStatus.playing && BedWars.getInstance().getMainConfig().getBoolean("mark-leave-as-abandon")) arena.abandonGame(player);
            Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
                if (player.isOnline()) {
                    player.kickPlayer(Language.getMsg(player, Messages.ARENA_RESTART_PLAYER_KICK));
                    if (arena != null && !b && arena.getStatus() == GameStatus.playing && BedWars.getInstance().getMainConfig().getBoolean("mark-leave-as-abandon")) arena.abandonGame(player);
                }
            }, 30L);
    }
    
    static ItemStack createItem(final Material material, final byte b, final boolean b2, final String displayName, final List<String> lore, final Player player, final String s, final String s2) {
        ItemStack itemStack = new ItemStack(material, 1, b);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lore);
        if (b2) {
            itemMeta.addEnchant(Enchantment.LUCK, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itemStack.setItemMeta(itemMeta);
        if (!s2.isEmpty() && !s.isEmpty()) itemStack = BedWars.getInstance().getNms().addCustomData(itemStack, s + "_" + s2);
        return itemStack;
    }
    
    public static ItemStack createItemStack(final String s, final int n, final String displayName, final List<String> lore, final boolean b, final String s2) throws InvalidMaterialException {
        Material value;
        try {
            value = Material.valueOf(s);
        } catch (Exception ex) {
            throw new InvalidMaterialException(s);
        }
        ItemStack addCustomData = new ItemStack(value, 1, (short)n);
        final ItemMeta itemMeta = addCustomData.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lore);
        if (b) {
            itemMeta.addEnchant(Enchantment.LUCK, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        addCustomData.setItemMeta(itemMeta);
        if (!s2.isEmpty())addCustomData = BedWars.getInstance().getNms().addCustomData(addCustomData, s2);
        return addCustomData;
    }
    
    public static boolean isProjectile(final Material material) {
        return Material.EGG == material || BedWars.getInstance().getNms().materialFireball() == material || BedWars.getInstance().getNms().materialSnowball() == material || Material.ARROW == material;
    }
    
    public static TextComponent msgHoverClick(final String s, final String s2, final String s3, final ClickEvent.Action action) {
        final TextComponent textComponent = new TextComponent(s);
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(s2).create()));
        textComponent.setClickEvent(new ClickEvent(action, s3));
        return textComponent;
    }
    
    public static String replaceStatsPlaceholders(final Player player, String s, final boolean b) {
        final PlayerStats value = BedWars.getInstance().getStatsManager().get(player.getUniqueId());
        if (s.contains("{kills}")) s = s.replace("{kills}", String.valueOf(value.getKills()));
        if (s.contains("{deaths}")) s = s.replace("{deaths}", String.valueOf(value.getDeaths()));
        if (s.contains("{losses}")) s = s.replace("{losses}", String.valueOf(value.getLosses()));
        if (s.contains("{wins}")) s = s.replace("{wins}", String.valueOf(value.getWins()));
        if (s.contains("{finalKills}")) s = s.replace("{finalKills}", String.valueOf(value.getFinalKills()));
        if (s.contains("{finalDeaths}")) s = s.replace("{finalDeaths}", String.valueOf(value.getFinalDeaths()));
        if (s.contains("{bedsDestroyed}")) s = s.replace("{bedsDestroyed}", String.valueOf(value.getBedsDestroyed()));
        if (s.contains("{gamesPlayed}")) s = s.replace("{gamesPlayed}", String.valueOf(value.getGamesPlayed()));
        if (s.contains("{firstPlay}")) s = s.replace("{firstPlay}", new SimpleDateFormat(Language.getMsg(player, Messages.FORMATTING_STATS_DATE_FORMAT)).format((value.getFirstPlay() != null) ? Timestamp.from(value.getFirstPlay()) : Timestamp.from(Instant.now())));
        if (s.contains("{lastPlay}")) s = s.replace("{lastPlay}", new SimpleDateFormat(Language.getMsg(player, Messages.FORMATTING_STATS_DATE_FORMAT)).format((value.getLastPlay() != null) ? Timestamp.from(value.getLastPlay()) : Timestamp.from(Instant.now())));
        if (s.contains("{player}")) s = s.replace("{player}", player.getDisplayName());
        if (s.contains("{prefix}")) s = s.replace("{prefix}", BedWars.getInstance().getChat().getPrefix(player));
        return b ? s : s;
    }
    
    public static boolean isNumber(final String s) {
        try {
            Double.parseDouble(s);
        } catch (Exception ex) {
            try {
                Integer.parseInt(s);
            } catch (Exception ex2) {
                try {
                    Long.parseLong(s);
                } catch (Exception ex3) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static boolean isOutsideOfBorder(final Location location) {
        final WorldBorder worldBorder = location.getWorld().getWorldBorder();
        return worldBorder.getCenter().distance(location) >= worldBorder.getSize() / 2.0 + worldBorder.getWarningDistance();
    }
    
    public static boolean isBuildProtected(final Location location, final IArena arena) {
        for (final ITeam team : arena.getTeams()) {
            if (team.getSpawn().distance(location) <= arena.getConfig().getInt("spawn-protection")) return true;
            if (team.getShop().distance(location) <= arena.getConfig().getInt("shop-protection")) return true;
            if (team.getTeamUpgrades().distance(location) <= arena.getConfig().getInt("upgrades-protection")) return true;
            for (IGenerator iGenerator : team.getGenerators()) if (iGenerator.getLocation().distance(location) <= 1.0) return true;
        }
        for (IGenerator iGenerator : arena.getOreGenerators()) if (iGenerator.getLocation().distance(location) <= 1.0) return true;
        return isOutsideOfBorder(location);
    }
}
