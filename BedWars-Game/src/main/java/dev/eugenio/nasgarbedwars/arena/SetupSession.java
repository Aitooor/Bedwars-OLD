package dev.eugenio.nasgarbedwars.arena;

import dev.eugenio.nasgarbedwars.commands.bedwars.MainCommand;
import dev.eugenio.nasgarbedwars.configuration.ArenaConfig;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.team.TeamColor;
import dev.eugenio.nasgarbedwars.api.events.server.SetupSessionCloseEvent;
import dev.eugenio.nasgarbedwars.api.events.server.SetupSessionStartEvent;
import dev.eugenio.nasgarbedwars.api.server.ISetupSession;
import dev.eugenio.nasgarbedwars.api.server.SetupType;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class SetupSession implements ISetupSession {
    private static List<SetupSession> setupSessions;
    @Getter private final Player player;
    @Getter private final String worldName;
    @Getter private SetupType setupType;
    private ArenaConfig cm;
    private boolean started;
    private boolean autoCreatedEmerald;
    private boolean autoCreatedDiamond;
    private final List<Location> skipAutoCreateGen;
    
    public SetupSession(final Player player, final String worldName) {
        this.started = false;
        this.autoCreatedEmerald = false;
        this.autoCreatedDiamond = false;
        this.skipAutoCreateGen = new ArrayList<>();
        this.player = player;
        this.worldName = worldName;
        getSetupSessions().add(this);
        openGUI(player);
    }
    
    public void setSetupType(final SetupType setupType) {
        this.setupType = setupType;
    }
    
    public static List<SetupSession> getSetupSessions() {
        return SetupSession.setupSessions;
    }
    
    public static String getInvName() {
        return "§8Elige un método de setup";
    }
    
    public static int getAdvancedSlot() {
        return 5;
    }
    
    public static int getAssistedSlot() {
        return 3;
    }
    
    public boolean isStarted() {
        return this.started;
    }
    
    public boolean startSetup() {
        this.getPlayer().sendMessage("§6 ♦ §aCargando " + this.getWorldName());
        this.cm = new ArenaConfig(BedWars.getInstance(), this.getWorldName(), BedWars.getInstance().getDataFolder().getPath() + "/Arenas");
        BedWars.getInstance().getApi().getRestoreAdapter().onSetupSessionStart(this);
        return true;
    }
    
    private static void openGUI(final Player player) {
        final Inventory inventory = Bukkit.createInventory(null, 9, getInvName());
        final ItemStack itemStack = new ItemStack(Material.GLOWSTONE_DUST);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§e§lINSTALACIÓN DE ARENA ASISTIDA");
        itemMeta.setLore(Arrays.asList("", "§aInstalación fácil y rápida, pero no perfecta.", "§7Para gente vaga como Eugenio.", "", "§3Opciones reducidas. Cosas no exactas."));
        itemStack.setItemMeta(itemMeta);
        inventory.setItem(getAssistedSlot(), itemStack);
        final ItemStack itemStack2 = new ItemStack(Material.REDSTONE);
        final ItemMeta itemMeta2 = itemStack2.getItemMeta();
        itemMeta2.setDisplayName("§e§lINSTALACIÓN DE ARENA MANUAL/AVANZADA");
        itemMeta2.setLore(Arrays.asList("", "§aInstalación más exacta, avanzada y manual.", "§7Para gente más trabajadora como Mag.", "", "§e¡Más exacto, todo más amplificado!"));
        itemStack2.setItemMeta(itemMeta2);
        inventory.setItem(getAdvancedSlot(), itemStack2);
        player.openInventory(inventory);
    }
    
    public void cancel() {
        getSetupSessions().remove(this);
        if (this.isStarted()) {
            this.player.sendMessage("§6 ♦ §eSetup de mundo/arena §a" + this.getWorldName() + " §ccancelada.");
            this.done();
        }
    }
    
    public void done() {
        BedWars.getInstance().getApi().getRestoreAdapter().onSetupSessionClose(this);
        getSetupSessions().remove(this);
        this.getPlayer().removePotionEffect(PotionEffectType.SPEED);
        Bukkit.getPluginManager().callEvent(new SetupSessionCloseEvent(this));
    }
    
    public static boolean isInSetupSession(final UUID uuid) {
        for (SetupSession setupSession : getSetupSessions()) if (setupSession.getPlayer().getUniqueId().equals(uuid)) return true;
        return false;
    }
    
    public static SetupSession getSession(final UUID uuid) {
        for (final SetupSession setupSession : getSetupSessions()) if (setupSession.getPlayer().getUniqueId().equals(uuid)) return setupSession;
        return null;
    }
    
    public void setStarted(final boolean started) {
        this.started = started;
    }
    
    @Override
    public ArenaConfig getConfig() {
        return this.cm;
    }
    
    @Override
    public void teleportPlayer() {
        this.player.getInventory().clear();
        this.player.teleport(Bukkit.getWorld(this.getWorldName()).getSpawnLocation());
        this.player.setGameMode(GameMode.CREATIVE);
        Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
            this.player.setAllowFlight(true);
            this.player.setFlying(true);
        }, 5L);
        this.player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
        this.player.sendMessage("\n" + ChatColor.WHITE + "\n");
        for (int i = 0; i < 10; ++i) this.getPlayer().sendMessage(" ");
        this.player.sendMessage(ChatColor.GREEN + "Has sido teletransportado al spawn de la arena " + ChatColor.YELLOW + this.getWorldName() + ChatColor.GREEN + ".");
        if (this.getSetupType() == SetupType.ASSISTED && this.getConfig().getYml().get("waiting.Loc") == null) {
            this.player.sendMessage("");
            this.player.sendMessage(ChatColor.YELLOW + "Por favor, establece el lugar del lobby/limbo/waiting lobby de la partida." + ChatColor.GREEN + " Será el lugar donde los jugadores spawnearán al iniciar la partida.");
            this.player.spigot().sendMessage(Misc.msgHoverClick(ChatColor.BLUE + "     ♦     " + ChatColor.GOLD + "CLICK AQUÍ PARA ESTABLECER EL WAITING LOBBY    " + ChatColor.BLUE + " ♦", ChatColor.LIGHT_PURPLE + "Click para establecer el limbo.", "/bw" + " setWaitingSpawn", ClickEvent.Action.RUN_COMMAND));
            this.player.spigot().sendMessage(MainCommand.createTC(ChatColor.YELLOW + "O también puedes escribir en el chat " + ChatColor.GRAY + "/bw" + " para ver la lista de comandos.", "/bw" + "", ChatColor.WHITE + "Enseña la lista de comandos."));
        } else {
            Bukkit.dispatchCommand(this.player, "bw" + " cmds");
        }
        final World world = Bukkit.getWorld(this.getWorldName());
        Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> world.getEntities().stream().filter(entity -> entity.getType() != EntityType.PLAYER).filter(entity -> entity.getType() != EntityType.PAINTING).filter(entity -> entity.getType() != EntityType.ITEM_FRAME).forEach(Entity::remove), 30L);
        world.setAutoSave(false);
        world.setGameRuleValue("doMobSpawning", "false");
        Bukkit.getPluginManager().callEvent(new SetupSessionStartEvent(this));
        setStarted(true);
        Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
            for (String string : this.getTeams()) {
                for (String string2 : new String[]{"Iron", "Gold", "Emerald"}) {
                    if (this.getConfig().getYml().get("Team." + string + "." + string2) != null) for (String string3 : this.getConfig().getList("Team." + string + ".Iron")) dev.eugenio.nasgarbedwars.commands.Misc.createArmorStand("§eGenerador de §a" + string2 + " §eañadido al equipo: " + this.getTeamColor(string) + string, this.getConfig().convertStringToArenaLocation(string3), string3);
                    if (this.getConfig().getYml().get("Team." + string + ".Spawn") != null) dev.eugenio.nasgarbedwars.commands.Misc.createArmorStand(this.getTeamColor(string) + string + " " + ChatColor.GOLD + "SPAWN SETEADO", this.getConfig().getArenaLoc("Team." + string + ".Spawn"), this.getConfig().getString("Team." + string + ".Spawn"));
                    if (this.getConfig().getYml().get("Team." + string + ".Bed") == null) continue;
                    dev.eugenio.nasgarbedwars.commands.Misc.createArmorStand(this.getTeamColor(string) + string + " " + ChatColor.GOLD + "BED SETEADO", this.getConfig().getArenaLoc("Team." + string + ".Bed"), this.getConfig().getString("Team." + string + ".Bed"));
                }
                if (this.getConfig().getYml().get("Team." + string + ".Shop") != null) dev.eugenio.nasgarbedwars.commands.Misc.createArmorStand(this.getTeamColor(string) + string + " " + ChatColor.GOLD + "SHOP SETEADO", this.getConfig().getArenaLoc("Team." + string + ".Shop"), null);
                if (this.getConfig().getYml().get("Team." + string + ".Upgrade") != null) dev.eugenio.nasgarbedwars.commands.Misc.createArmorStand(this.getTeamColor(string) + string + " " + ChatColor.GOLD + "MEJORAS SETEADAS", this.getConfig().getArenaLoc("Team." + string + ".Upgrade"), null);
                if (this.getConfig().getYml().get("Team." + string + "." + "kill-drops-loc") == null) continue;
                dev.eugenio.nasgarbedwars.commands.Misc.createArmorStand(ChatColor.GOLD + "Kill drops " + string, this.getConfig().getArenaLoc("Team." + string + "." + "kill-drops-loc"), null);
            }
            for (String string : new String[]{"Emerald", "Diamond"}) {
                if (this.getConfig().getYml().get("generator." + string) == null) continue;
                for (String string2 : this.getConfig().getList("generator." + string)) dev.eugenio.nasgarbedwars.commands.Misc.createArmorStand(ChatColor.GOLD + string + " SET", this.getConfig().convertStringToArenaLocation(string2), string2);
            }
        }, 90L);
    }
    
    @Override
    public void close() {
        this.cancel();
    }
    
    public List<Location> getSkipAutoCreateGen() {
        return new ArrayList<>(this.skipAutoCreateGen);
    }
    
    public void addSkipAutoCreateGen(final Location location) {
        this.skipAutoCreateGen.add(location);
    }
    
    public void setAutoCreatedEmerald(final boolean autoCreatedEmerald) {
        this.autoCreatedEmerald = autoCreatedEmerald;
    }
    
    public boolean isAutoCreatedEmerald() {
        return this.autoCreatedEmerald;
    }
    
    public void setAutoCreatedDiamond(final boolean autoCreatedDiamond) {
        this.autoCreatedDiamond = autoCreatedDiamond;
    }
    
    public boolean isAutoCreatedDiamond() {
        return this.autoCreatedDiamond;
    }
    
    public String getPrefix() {
        return ChatColor.GREEN + "[" + this.getWorldName() + ChatColor.GREEN + "] " + ChatColor.GOLD;
    }
    
    public ChatColor getTeamColor(final String s) {
        return TeamColor.getChatColor(this.getConfig().getString("Team." + s + ".Color"));
    }
    
    public void displayAvailableTeams() {
        if (this.getConfig().getYml().get("Team") != null) {
            this.getPlayer().sendMessage(this.getPrefix() + "Available teams: ");
            for (final String s : Objects.requireNonNull(this.getConfig().getYml().getConfigurationSection("Team")).getKeys(false)) {
                this.getPlayer().sendMessage(this.getPrefix() + TeamColor.getChatColor(Objects.requireNonNull(this.getConfig().getYml().getString("Team." + s + ".Color"))) + s);
            }
        }
    }
    
    public String getNearestTeam() {
        String s = "";
        final ConfigurationSection configurationSection = this.getConfig().getYml().getConfigurationSection("Team");
        if (configurationSection == null) {
            return s;
        }
        double n = 100.0;
        for (final String s2 : configurationSection.getKeys(false)) {
            if (this.getConfig().getYml().get("Team." + s2 + ".Spawn") == null) {
                continue;
            }
            final double distance = this.getConfig().getArenaLoc("Team." + s2 + ".Spawn").distance(this.getPlayer().getLocation());
            if (distance > this.getConfig().getInt("island-radius") || distance >= n) {
                continue;
            }
            n = distance;
            s = s2;
        }
        return s;
    }

    public String getFarNearestTeam() {
        String s = "";
        final ConfigurationSection configurationSection = this.getConfig().getYml().getConfigurationSection("Team");
        if (configurationSection == null) {
            return s;
        }
        double n = 150.0;
        for (final String s2 : configurationSection.getKeys(false)) {
            if (this.getConfig().getYml().get("Team." + s2 + ".Spawn") == null) {
                continue;
            }
            final double distance = this.getConfig().getArenaLoc("Team." + s2 + ".Spawn").distance(this.getPlayer().getLocation());
            if (distance > 25 || distance >= n) {
                continue;
            }
            n = distance;
            s = s2;
        }
        return s;
    }
    
    public String dot() {
        return ChatColor.BLUE + " " + '♦' + " " + ChatColor.GRAY + "/bw" + " ";
    }
    
    public List<String> getTeams() {
        if (this.getConfig().getYml().get("Team") == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(this.getConfig().getYml().getConfigurationSection("Team").getKeys(false));
    }
    
    static {
        SetupSession.setupSessions = new ArrayList<>();
    }
}
