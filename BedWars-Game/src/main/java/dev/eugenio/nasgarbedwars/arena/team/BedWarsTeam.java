package dev.eugenio.nasgarbedwars.arena.team;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.arena.OreGenerator;
import dev.eugenio.nasgarbedwars.utils.WEUtils;
import dev.eugenio.nasgarbedwars.api.arena.generator.GeneratorType;
import dev.eugenio.nasgarbedwars.api.arena.generator.IGenerator;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.arena.team.TeamColor;
import dev.eugenio.nasgarbedwars.api.arena.team.TeamEnchantment;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerFirstSpawnEvent;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerSpawnEvent;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.api.region.Cuboid;
import dev.eugenio.nasgarbedwars.api.upgrades.EnemyBaseEnterTrap;
import dev.eugenio.nasgarbedwars.shop.ShopCache;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.Bed;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BedWarsTeam implements ITeam {
    @Getter private List<Player> members;
    @Getter private TeamColor color;
    @Getter private Location spawn;
    @Getter private Location bed;
    @Getter private Location shop;
    @Getter private Location teamUpgrades;
    @Getter private String name;
    @Getter private Arena arena;
    private boolean bedDestroyed;
    private Vector killDropsLoc;
    @Getter private final List<IGenerator> generators;
    private final ConcurrentHashMap<String, Integer> teamUpgradeList;
    @Getter private List<PotionEffect> teamEffects;
    private List<PotionEffect> base;
    private List<TeamEnchantment> bowsEnchantments;
    private List<TeamEnchantment> swordsEnchantemnts;
    private List<TeamEnchantment> armorsEnchantemnts;
    @Getter private final HashMap<UUID, UUID> beds;
    private final LinkedList<EnemyBaseEnterTrap> enemyBaseEnterTraps;
    @Getter private int dragons;
    private List<Player> membersCache;
    public static HashMap<UUID, Long> reSpawnInvulnerability;

    public BedWarsTeam(final String name, final TeamColor color, final Location spawn, final Location bed, final Location shop, final Location teamUpgrades, final Arena arena) {
        this.members = new ArrayList<>();
        this.bedDestroyed = false;
        this.killDropsLoc = null;
        this.generators = new ArrayList<>();
        this.teamUpgradeList = new ConcurrentHashMap<>();
        this.teamEffects = new ArrayList<>();
        this.base = new ArrayList<>();
        this.bowsEnchantments = new ArrayList<>();
        this.swordsEnchantemnts = new ArrayList<>();
        this.armorsEnchantemnts = new ArrayList<>();
        this.beds = new HashMap<>();
        this.enemyBaseEnterTraps = new LinkedList<>();
        this.dragons = 1;
        this.membersCache = new ArrayList<>();
        if (arena == null) return;
        this.name = name;
        this.color = color;
        this.spawn = spawn;
        this.bed = bed;
        this.arena = arena;
        this.shop = shop;
        this.teamUpgrades = teamUpgrades;
        Language.saveIfNotExists("team-name-{arena}-{team}".replace("{arena}", this.getArena().getArenaName()).replace("{team}", this.getName()), name);
        arena.getRegionsList().add(new Cuboid(spawn, arena.getConfig().getInt("spawn-protection"), true));
        final Location arenaLoc = this.getArena().getConfig().getArenaLoc("Team." + this.getName() + "." + "kill-drops-loc");
        if (arenaLoc != null) this.setKillDropsLocation(arenaLoc);
    }
    
    @Override
    public int getSize() {
        return this.members.size();
    }
    
    @Override
    public void addPlayers(final Player... array) {
        if (array == null) return;
        for (final Player player3 : array) {
            if (player3 != null) {
                this.members.removeIf(player -> player.getUniqueId().equals(player3.getUniqueId()));
                this.members.add(player3);
                this.membersCache.removeIf(player2 -> player2.getUniqueId().equals(player3.getUniqueId()));
                this.membersCache.add(player3);
            }
        }
    }
    
    @Override
    public void firstSpawn(final Player player) {
        if (player == null) return;
        player.teleport(this.spawn, PlayerTeleportEvent.TeleportCause.PLUGIN);
        player.setGameMode(GameMode.SURVIVAL);
        this.sendDefaultInventory(player, true);
        Bukkit.getPluginManager().callEvent(new PlayerFirstSpawnEvent(player, this.getArena(), this));
    }
    
    @Override
    public void spawnNPCs() {
        if (this.getMembers().isEmpty() && this.getArena().getConfig().getBoolean("disable-npcs-for-empty-teams")) return;
        Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
            BedWars.getInstance().getNms().colorBed(this);
            BedWars.getInstance().getNms().spawnShop(this.getArena().getConfig().getArenaLoc("Team." + this.getName() + ".Upgrade"), (this.getArena().getMaxInTeam() > 1) ? Messages.NPC_NAME_TEAM_UPGRADES : Messages.NPC_NAME_SOLO_UPGRADES, this.getArena().getPlayers(), this.getArena());
            BedWars.getInstance().getNms().spawnShop(this.getArena().getConfig().getArenaLoc("Team." + this.getName() + ".Shop"), (this.getArena().getMaxInTeam() > 1) ? Messages.NPC_NAME_TEAM_SHOP : Messages.NPC_NAME_SOLO_SHOP, this.getArena().getPlayers(), this.getArena());
        }, 20L);
        final Cuboid cuboid = new Cuboid(this.getArena().getConfig().getArenaLoc("Team." + this.getName() + ".Upgrade"), 1, true);
        cuboid.setMinY(cuboid.getMinY() - 1);
        cuboid.setMaxY(cuboid.getMaxY() + 4);
        this.getArena().getRegionsList().add(cuboid);
        final Cuboid cuboid2 = new Cuboid(this.getArena().getConfig().getArenaLoc("Team." + this.getName() + ".Shop"), 1, true);
        cuboid2.setMinY(cuboid2.getMinY() - 1);
        cuboid2.setMaxY(cuboid2.getMaxY() + 4);
        this.getArena().getRegionsList().add(cuboid2);
    }
    
    @Override
    public void reJoin(final Player player) {
        this.addPlayers(player);
        this.arena.startReSpawnSession(player, BedWars.getInstance().getMainConfig().getInt("countdowns.player-re-spawn"));
    }
    
    @Override
    public void sendDefaultInventory(final Player player, final boolean b) {
        if (b) player.getInventory().clear();
        for (final String s : BedWars.getInstance().getMainConfig().getYml().getStringList((BedWars.getInstance().getMainConfig().getYml().get("start-items-per-group." + this.arena.getGroup()) == null) ? "start-items-per-group.Default" : ("start-items-per-group." + this.arena.getGroup()))) {
            final String[] split = s.split(",");
            if (split.length != 0) {
                try {
                    ItemStack itemStack;
                    if (split.length > 1) {
                        try {
                            Integer.parseInt(split[1]);
                        } catch (Exception ex) {
                            BedWars.getInstance().getLogger().severe(split[1] + " is not an integer at: " + s + " (config)");
                            continue;
                        }
                        itemStack = new ItemStack(Material.valueOf(split[0]), Integer.parseInt(split[1]));
                    } else {
                        itemStack = new ItemStack(Material.valueOf(split[0]));
                    }
                    if (split.length > 2) {
                        try {
                            Integer.parseInt(split[2]);
                        } catch (Exception ex2) {
                            BedWars.getInstance().getLogger().severe(split[2] + " is not an integer at: " + s + " (config)");
                            continue;
                        }
                        itemStack.setAmount(Integer.parseInt(split[2]));
                    }
                    final ItemMeta itemMeta = itemStack.getItemMeta();
                    if (split.length > 3) itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', split[3]));
                    BedWars.getInstance().getNms().setUnbreakable(itemMeta);
                    itemStack.setItemMeta(itemMeta);
                    final ItemStack addCustomData = BedWars.getInstance().getNms().addCustomData(itemStack, "DEFAULT_ITEM");
                    if (BedWars.getInstance().getNms().isSword(addCustomData)) {
                        boolean b2 = false;
                        for (final ItemStack itemStack2 : player.getInventory().getContents()) {
                            if (itemStack2 != null) {
                                if (itemStack2.getType() != Material.AIR) {
                                    if (BedWars.getInstance().getNms().isSword(itemStack2)) {
                                        b2 = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (b2) continue;
                        player.getInventory().addItem(addCustomData);
                    } else if (BedWars.getInstance().getNms().isBow(addCustomData)) {
                        boolean b3 = false;
                        for (final ItemStack itemStack3 : player.getInventory().getContents()) {
                            if (itemStack3 != null) {
                                if (itemStack3.getType() != Material.AIR) {
                                    if (BedWars.getInstance().getNms().isBow(itemStack3)) {
                                        b3 = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (b3) continue;
                        player.getInventory().addItem(addCustomData);
                    } else {
                        player.getInventory().addItem(addCustomData);
                    }
                } catch (Exception ignored) {
                    // Ignored
                }
            }
        }
        this.sendArmor(player);
    }
    
    @Override
    public void defaultSword(final Player player, final boolean b) {
        if (!b) {
            return;
        }
        for (final String s : BedWars.getInstance().getMainConfig().getYml().getStringList((BedWars.getInstance().getMainConfig().getYml().get("start-items-per-group." + this.arena.getGroup()) == null) ? "start-items-per-group.Default" : ("start-items-per-group." + this.arena.getGroup()))) {
            final String[] split = s.split(",");
            if (split.length != 0) {
                try {
                    ItemStack itemStack;
                    if (split.length > 1) {
                        try {
                            Integer.parseInt(split[1]);
                        } catch (Exception ex) {
                            BedWars.getInstance().getLogger().severe(split[1] + " is not an integer at: " + s + " (config)");
                            continue;
                        }
                        itemStack = new ItemStack(Material.valueOf(split[0]), Integer.parseInt(split[1]));
                    } else {
                        itemStack = new ItemStack(Material.valueOf(split[0]));
                    }
                    if (split.length > 2) {
                        try {
                            Integer.parseInt(split[2]);
                        } catch (Exception ex2) {
                            BedWars.getInstance().getLogger().severe(split[2] + " is not an integer at: " + s + " (config)");
                            continue;
                        }
                        itemStack.setAmount(Integer.parseInt(split[2]));
                    }
                    final ItemMeta itemMeta = itemStack.getItemMeta();
                    if (split.length > 3) {
                        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', split[3]));
                    }
                    BedWars.getInstance().getNms().setUnbreakable(itemMeta);
                    itemStack.setItemMeta(itemMeta);
                    final ItemStack addCustomData = BedWars.getInstance().getNms().addCustomData(itemStack, "DEFAULT_ITEM");
                    if (BedWars.getInstance().getNms().isSword(addCustomData)) {
                        player.getInventory().addItem(addCustomData);
                        break;
                    }
                } catch (Exception ignored) {
                    // Ignored
                }
            }
        }
    }
    
    public void spawnGenerators() {
        for (final String s : new String[] { "Iron", "Gold" }) {
            final GeneratorType value = GeneratorType.valueOf(s.toUpperCase());
            List<Location> arenaLocations = new ArrayList<>();
            if (this.getArena().getConfig().getYml().get("Team." + this.getName() + "." + s) instanceof String) {
                arenaLocations.add(this.getArena().getConfig().getArenaLoc("Team." + this.getName() + "." + s));
            } else {
                arenaLocations = this.getArena().getConfig().getArenaLocations("Team." + this.getName() + "." + s);
            }
            for (Location arenaLocation : arenaLocations) {
                this.generators.add(new OreGenerator(arenaLocation, this.getArena(), value, this));
            }
        }
    }

    @Override
    public void respawnMember(final Player player) {
        if (BedWarsTeam.reSpawnInvulnerability.containsKey(player.getUniqueId())) {
            BedWarsTeam.reSpawnInvulnerability.replace(player.getUniqueId(), System.currentTimeMillis() + BedWars.getInstance().getMainConfig().getInt("re-spawn-invulnerability"));
        } else {
            BedWarsTeam.reSpawnInvulnerability.put(player.getUniqueId(), System.currentTimeMillis() + BedWars.getInstance().getMainConfig().getInt("re-spawn-invulnerability"));
        }
        player.teleport(this.getSpawn(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        player.setVelocity(new Vector(0, 0, 0));
        this.getArena().getRespawnSessions().remove(player);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        BedWars.getInstance().getNms().setCollide(player, this.arena, true);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setHealth(20.0);
        Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
            for (Player player2 : this.arena.getPlayers()) {
                if (player2.equals(player)) continue;
                BedWars.getInstance().getNms().spigotShowPlayer(player, player2);
                BedWars.getInstance().getNms().spigotShowPlayer(player2, player);
            }
            for (Player player2 : this.arena.getSpectators()) BedWars.getInstance().getNms().spigotShowPlayer(player, player2);
        }, 8L);
        BedWars.getInstance().getNms().sendTitle(player, Language.getMsg(player, Messages.PLAYER_DIE_RESPAWNED_TITLE), "", 0, 20, 0);
        this.sendDefaultInventory(player, false);
        final ShopCache shopCache = ShopCache.getShopCache(player.getUniqueId());
        if (shopCache != null) shopCache.managePermanentsAndDowngradables(this.getArena());
        player.setHealth(20.0);
        if (!this.getBaseEffects().isEmpty()) for (PotionEffect potionEffect : this.getBaseEffects()) player.addPotionEffect(potionEffect, true);
        if (!this.getTeamEffects().isEmpty()) for (PotionEffect potionEffect : this.getTeamEffects()) player.addPotionEffect(potionEffect, true);
        if (!this.getBowsEnchantments().isEmpty()) {
            for (final ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack != null) {
                    if (itemStack.getType() == Material.BOW) {
                        final ItemMeta itemMeta = itemStack.getItemMeta();
                        for (final TeamEnchantment teamEnchantment : this.getBowsEnchantments()) itemMeta.addEnchant(teamEnchantment.getEnchantment(), teamEnchantment.getAmplifier(), true);
                        itemStack.setItemMeta(itemMeta);
                    }
                    player.updateInventory();
                }
            }
        }
        if (!this.getSwordsEnchantments().isEmpty()) {
            for (final ItemStack itemStack2 : player.getInventory().getContents()) {
                if (itemStack2 != null) {
                    if (BedWars.getInstance().getNms().isSword(itemStack2)) {
                        final ItemMeta itemMeta2 = itemStack2.getItemMeta();
                        for (final TeamEnchantment teamEnchantment2 : this.getSwordsEnchantments()) itemMeta2.addEnchant(teamEnchantment2.getEnchantment(), teamEnchantment2.getAmplifier(), true);
                        itemStack2.setItemMeta(itemMeta2);
                    }
                    player.updateInventory();
                }
            }
        }
        if (!this.getArmorsEnchantments().isEmpty()) {
            for (final ItemStack itemStack3 : player.getInventory().getArmorContents()) {
                if (itemStack3 != null) {
                    if (BedWars.getInstance().getNms().isArmor(itemStack3)) {
                        final ItemMeta itemMeta3 = itemStack3.getItemMeta();
                        for (final TeamEnchantment teamEnchantment3 : this.getArmorsEnchantments()) itemMeta3.addEnchant(teamEnchantment3.getEnchantment(), teamEnchantment3.getAmplifier(), true);
                        itemStack3.setItemMeta(itemMeta3);
                    }
                    player.updateInventory();
                }
            }
        }
        Bukkit.getPluginManager().callEvent(new PlayerSpawnEvent(player, this.getArena(), this));
        BedWars.getInstance().getNms().sendPlayerSpawnPackets(player, this.getArena());
        Bukkit.getScheduler().runTaskLaterAsynchronously(BedWars.getInstance(), () -> {
            if (this.getArena() != null) {
                BedWars.getInstance().getNms().sendPlayerSpawnPackets(player, this.getArena());
                for (Player player2 : this.getArena().getShowTime().keySet()) BedWars.getInstance().getNms().hideArmor(player2, player);
            }
        }, 10L);
    }
    
    private ItemStack createArmor(final Material material) {
        final ItemStack itemStack = new ItemStack(material);
        final LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta)itemStack.getItemMeta();
        leatherArmorMeta.setColor(this.color.bukkitColor());
        BedWars.getInstance().getNms().setUnbreakable(leatherArmorMeta);
        itemStack.setItemMeta(leatherArmorMeta);
        return itemStack;
    }
    
    @Override
    public void sendArmor(final Player player) {
        if (player.getInventory().getHelmet() == null) player.getInventory().setHelmet(this.createArmor(Material.LEATHER_HELMET));
        if (player.getInventory().getChestplate() == null) player.getInventory().setChestplate(this.createArmor(Material.LEATHER_CHESTPLATE));
        if (player.getInventory().getLeggings() == null) player.getInventory().setLeggings(this.createArmor(Material.LEATHER_LEGGINGS));
        if (player.getInventory().getBoots() == null) player.getInventory().setBoots(this.createArmor(Material.LEATHER_BOOTS));
    }
    
    @Override
    public void addTeamEffect(final PotionEffectType potionEffectType, final int n, final int n2) {
        this.getTeamEffects().add(new PotionEffect(potionEffectType, n2, n));
        for (Player player : this.getMembers()) player.addPotionEffect(new PotionEffect(potionEffectType, n2, n), true);
    }
    
    @Override
    public void addBaseEffect(final PotionEffectType potionEffectType, final int n, final int n2) {
        this.getBaseEffects().add(new PotionEffect(potionEffectType, n2, n));
        for (final Player player : new ArrayList<>(this.getMembers())) if (player.getLocation().distance(this.getBed()) <= this.getArena().getIslandRadius()) for (PotionEffect potionEffect : this.getBaseEffects()) player.addPotionEffect(potionEffect, true);
    }
    
    @Override
    public void addBowEnchantment(final org.bukkit.enchantments.Enchantment enchantment, final int n) {
        this.getBowsEnchantments().add(new Enchantment(enchantment, n));
        for (final Player player : this.getMembers()) {
            for (final ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack != null) {
                    if (itemStack.getType() == Material.BOW) {
                        final ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.addEnchant(enchantment, n, true);
                        itemStack.setItemMeta(itemMeta);
                    }
                }
            }
            player.updateInventory();
        }
    }
    
    @Override
    public void addSwordEnchantment(final org.bukkit.enchantments.Enchantment enchantment, final int n) {
        this.getSwordsEnchantments().add(new Enchantment(enchantment, n));
        for (final Player player : this.getMembers()) {
            for (final ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack != null) {
                    if (BedWars.getInstance().getNms().isSword(itemStack)) {
                        final ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.addEnchant(enchantment, n, true);
                        itemStack.setItemMeta(itemMeta);
                    }
                }
            }
            player.updateInventory();
        }
    }
    
    @Override
    public void addArmorEnchantment(org.bukkit.enchantments.Enchantment enchantment, int n) {
        this.getArmorsEnchantments().add(new Enchantment(enchantment, n));
        for (Player player : this.getMembers()) {
            for (ItemStack itemStack : player.getInventory().getArmorContents()) {
                if (itemStack == null || !BedWars.getInstance().getNms().isArmor(itemStack)) continue;
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.addEnchant(enchantment, n, true);
                itemStack.setItemMeta(itemMeta);
            }
            player.updateInventory();
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(BedWars.getInstance(), () -> {
            for (Player player : this.getMembers()) {
                if (!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) continue;
                for (Player player2 : this.getArena().getPlayers()) BedWars.getInstance().getNms().hideArmor(player, player2);
                for (Player player2 : this.getArena().getSpectators()) BedWars.getInstance().getNms().hideArmor(player, player2);
            }
        }, 20L);
    }

    @Override
    public boolean isBedDestroyed() {
        return this.bedDestroyed;
    }

    @Override
    public boolean isMember(final Player player) {
        return player != null && this.members.contains(player);
    }
    
    @Override
    public boolean wasMember(final UUID uuid) {
        if (uuid == null) return false;
        for (Player player : this.membersCache) if (player.getUniqueId().equals(uuid)) return true;
        return false;
    }
    
    @Override
    public String getDisplayName(final Language language) {
        final String m = language.m("team-name-{arena}-{team}".replace("{arena}", this.getArena().getArenaName()).replace("{team}", this.getName()));
        return (m == null) ? this.getName() : m;
    }

    @Override
    public ConcurrentHashMap<String, Integer> getTeamUpgradeTiers() {
        return this.teamUpgradeList;
    }
    
    @Override
    public void setBedDestroyed(final boolean bedDestroyed) {
        if (!(this.bedDestroyed = bedDestroyed)) {
            if (!this.getBed().getBlock().getType().toString().contains("BED")) {
                BedWars.getInstance().getLogger().severe("Cama no seteada para el equipo: " + this.getName() + " en arena: " + this.getArena().getArenaName());
                return;
            }
            BedWars.getInstance().getNms().colorBed(this);
        } else {
            breakBlockBed(this.bed.getBlock());

            if (this.getArena().getConfig().getBoolean("disable-generator-for-empty-teams")) for (IGenerator iGenerator : this.getGenerators()) iGenerator.disable();
        }
    }
    
    @Deprecated
    @Override
    public IGenerator getIronGenerator() {
        final IGenerator[] array = (IGenerator[])this.generators.stream().filter(generator -> generator.getType() == GeneratorType.IRON).toArray();
        if (array.length == 0) return null;
        return array[0];
    }
    
    @Deprecated
    @Override
    public IGenerator getGoldGenerator() {
        final IGenerator[] array = (IGenerator[])this.generators.stream().filter(generator -> generator.getType() == GeneratorType.GOLD).toArray();
        if (array.length == 0) return null;
        return array[0];
    }
    
    @Deprecated
    @Override
    public IGenerator getEmeraldGenerator() {
        final IGenerator[] array = (IGenerator[])this.generators.stream().filter(generator -> generator.getType() == GeneratorType.EMERALD).toArray();
        if (array.length == 0) return null;
        return array[0];
    }
    
    @Deprecated
    @Override
    public void setEmeraldGenerator(final IGenerator generator) {
        this.generators.add(generator);
    }
    
    @Override
    public List<PotionEffect> getBaseEffects() {
        return this.base;
    }
    
    @Override
    public void setDragons(final int dragons) {
        this.dragons = dragons;
    }
    
    @Override
    public List<Player> getMembersCache() {
        return this.membersCache;
    }

    @Override
    public List<TeamEnchantment> getBowsEnchantments() {
        return this.bowsEnchantments;
    }

    @Override
    public List<TeamEnchantment> getSwordsEnchantments() {
        return this.swordsEnchantemnts;
    }

    @Override
    public List<TeamEnchantment> getArmorsEnchantments() {
        return this.armorsEnchantemnts;
    }
    
    @Override
    public void destroyData() {
        this.members = null;
        this.spawn = null;
        this.bed = null;
        this.shop = null;
        this.teamUpgrades = null;
        for (IGenerator iGenerator : new ArrayList<>(this.generators)) iGenerator.destroyData();
        this.arena = null;
        this.teamEffects = null;
        this.base = null;
        this.bowsEnchantments = null;
        this.swordsEnchantemnts = null;
        this.armorsEnchantemnts = null;
        this.enemyBaseEnterTraps.clear();
        this.membersCache = null;
    }
    
    @Override
    public LinkedList<EnemyBaseEnterTrap> getActiveTraps() {
        return this.enemyBaseEnterTraps;
    }
    
    @Override
    public Vector getKillDropsLocation() {
        if (this.killDropsLoc == null) {
            List<IGenerator> list = this.generators.stream().filter(paramIGenerator -> (paramIGenerator.getType() == GeneratorType.IRON || paramIGenerator.getType() == GeneratorType.GOLD)).collect(Collectors.toList());
            if (list.isEmpty()) return new Vector(getSpawn().getX(), getSpawn().getY(), getSpawn().getZ());
            return new Vector((list.get(0)).getLocation().getX(), list.get(0).getLocation().getY(), list.get(0).getLocation().getZ());
        }
        return this.killDropsLoc;
    }

    public void setKillDropsLocation(final Vector vector) {
        if (vector == null) {
            this.killDropsLoc = null;
            return;
        }
        this.killDropsLoc = new Vector(vector.getBlockX() + 0.5, vector.getBlockY(), vector.getBlockZ() + 0.5);
    }
    
    public void setKillDropsLocation(final Location location) {
        if (location == null) {
            this.killDropsLoc = null;
            return;
        }
        this.killDropsLoc = new Vector(location.getBlockX() + 0.5, location.getBlockY(), location.getBlockZ() + 0.5);
    }
    
    static {
        BedWarsTeam.reSpawnInvulnerability = new HashMap<>();
    }
    
    public static class Enchantment implements TeamEnchantment {
        org.bukkit.enchantments.Enchantment enchantment;
        int amplifier;
        
        public Enchantment(final org.bukkit.enchantments.Enchantment enchantment, final int amplifier) {
            this.enchantment = enchantment;
            this.amplifier = amplifier;
        }
        
        @Override
        public org.bukkit.enchantments.Enchantment getEnchantment() {
            return this.enchantment;
        }
        
        @Override
        public int getAmplifier() {
            return this.amplifier;
        }
    }

    private void breakBlockBed(Block targetBlock) {
        if (targetBlock.getType().equals(Material.BED_BLOCK)) {
            Block bedHead;
            Block bedFeet;
            Bed bedBlock = (Bed) targetBlock.getState().getData();

            if (!bedBlock.isHeadOfBed()) {
                bedFeet = targetBlock;
                bedHead = getBedNeighbor(bedFeet);
            } else {
                bedHead = targetBlock;
                bedFeet = getBedNeighbor(bedHead);
            }
            bedFeet.setType(Material.AIR);
            bedHead.setType(Material.AIR);
        }
    }

    private Block getBedNeighbor(Block head) {
        if (isBedBlock(head.getRelative(BlockFace.EAST))) {
            return head.getRelative(BlockFace.EAST);
        } else if (isBedBlock(head.getRelative(BlockFace.WEST))) {
            return head.getRelative(BlockFace.WEST);
        } else if (isBedBlock(head.getRelative(BlockFace.SOUTH))) {
            return head.getRelative(BlockFace.SOUTH);
        } else {
            return head.getRelative(BlockFace.NORTH);
        }
    }

    private boolean isBedBlock(Block isBed) {
        if (isBed == null) return false;

        return (isBed.getType() == Material.BED || isBed.getType() == Material.BED_BLOCK);
    }

}
