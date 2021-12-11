package dev.eugenio.nasgarbedwars.shop.main;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.shop.IBuyItem;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.language.Language;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BuyCommand implements IBuyItem {
    private final List<String> asPlayer;
    private final List<String> asConsole;
    @Getter
    private final String upgradeIdentifier;

    public BuyCommand(final String s, final YamlConfiguration yamlConfiguration, final String upgradeIdentifier) {
        this.asPlayer = new ArrayList<>();
        this.asConsole = new ArrayList<>();
        BedWars.debug("Cargando BuyCommand: " + s);
        this.upgradeIdentifier = upgradeIdentifier;
        for (String replaceFirst : yamlConfiguration.getStringList(s + ".as-console")) {
            if (replaceFirst.startsWith("/")) {
                replaceFirst = replaceFirst.replaceFirst("/", "");
            }
            this.asConsole.add(replaceFirst);
        }
        for (String string : yamlConfiguration.getStringList(s + ".as-player")) {
            if (!string.startsWith("/")) {
                string = "/" + string;
            }
            this.asPlayer.add(string);
        }
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public void give(final Player player, final IArena arena) {
        BedWars.debug("Dando BuyCMD: " + this.getUpgradeIdentifier() + " a: " + player.getName());
        final String name = player.getName();
        final String string = player.getUniqueId().toString();
        final ITeam team = arena.getTeam(player);
        final String s = (team == null) ? "null" : team.getName();
        final String s2 = (team == null) ? "null" : team.getDisplayName(Language.getPlayerLanguage(player));
        final String s3 = (team == null) ? ChatColor.WHITE.toString() : team.getColor().chat().toString();
        final String arenaName = arena.getArenaName();
        final String worldName = arena.getWorldName();
        final String displayName = arena.getDisplayName();
        final String group = arena.getGroup();
        for (String value : this.asPlayer)
            player.chat(value.replace("{player}", name).replace("{player_uuid}", string).replace("{team}", s).replace("{team_display}", s2).replace("{team_color}", s3).replace("{arena}", arenaName).replace("{arena_world}", worldName).replace("{arena_display}", displayName).replace("{arena_group}", group));
        for (String value : this.asConsole)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), value.replace("{player}", name).replace("{player_uuid}", string).replace("{team}", s).replace("{team_display}", s2).replace("{team_color}", s3).replace("{arena}", arenaName).replace("{arena_world}", worldName).replace("{arena_display}", displayName).replace("{arena_group}", group));
    }

    @Override
    public ItemStack getItemStack() {
        return null;
    }

    @Override
    public void setItemStack(final ItemStack itemStack) {
    }

    @Override
    public boolean isAutoEquip() {
        return false;
    }

    @Override
    public void setAutoEquip(final boolean b) {
    }

    @Override
    public boolean isPermanent() {
        return false;
    }

    @Override
    public void setPermanent(final boolean b) {
    }
}
