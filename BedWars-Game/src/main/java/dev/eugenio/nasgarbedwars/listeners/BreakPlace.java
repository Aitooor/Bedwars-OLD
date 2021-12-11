package dev.eugenio.nasgarbedwars.listeners;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.sensitive.setup.AutoCreateTeams;
import dev.eugenio.nasgarbedwars.configuration.Sounds;
import dev.eugenio.nasgarbedwars.api.arena.GameStatus;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.NextEvent;
import dev.eugenio.nasgarbedwars.api.arena.generator.IGenerator;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerBedBreakEvent;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.api.region.Region;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import java.util.ArrayList;
import java.util.List;

public class BreakPlace implements Listener {
    private static final List<Player> buildSession;
    
    @EventHandler
    public void onIceMelt(final BlockFadeEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onCactus(final BlockPhysicsEvent event) {
        if (event.getBlock().getType() == Material.CACTUS) event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBurn(final BlockBurnEvent event) {
        final IArena arenaByIdentifier = Arena.getArenaByIdentifier(event.getBlock().getWorld().getName());
        if (arenaByIdentifier == null) return;
        if (!arenaByIdentifier.getConfig().getBoolean("allow-map-break")) {
            event.setCancelled(true);
            return;
        }
        if (BedWars.getInstance().getNms().isBed(event.getBlock().getType())) {
            for (final ITeam team : arenaByIdentifier.getTeams()) {
                for (int i = event.getBlock().getX() - 2; i < event.getBlock().getX() + 2; ++i) {
                    for (int j = event.getBlock().getY() - 2; j < event.getBlock().getY() + 2; ++j) {
                        for (int k = event.getBlock().getZ() - 2; k < event.getBlock().getZ() + 2; ++k) {
                            if (team.getBed().getBlockX() == i && team.getBed().getBlockY() == j && team.getBed().getBlockZ() == k && !team.isBedDestroyed()) {
                                event.setCancelled(true);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        final IArena arenaByIdentifier = Arena.getArenaByIdentifier(event.getBlock().getWorld().getName());
        if (arenaByIdentifier != null && arenaByIdentifier.getStatus() != GameStatus.playing) {
            event.setCancelled(true);
            return;
        }
        final Player player = event.getPlayer();
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) return;
        if (arenaByPlayer.isSpectator(player)) {
            event.setCancelled(true);
            return;
        }
        if (arenaByPlayer.getRespawnSessions().containsKey(player)) {
            event.setCancelled(true);
            return;
        }
        if (arenaByPlayer.getStatus() != GameStatus.playing) {
            event.setCancelled(true);
            return;
        }
        if (event.getBlockPlaced().getLocation().getBlockY() >= arenaByPlayer.getConfig().getInt("max-build-y")) {
            event.setCancelled(true);
            return;
        }
        for (final Region region : arenaByPlayer.getRegionsList()) {
            if (region.isInRegion(event.getBlock().getLocation()) && region.isProtected()) {
                event.setCancelled(true);
                player.sendMessage(Language.getMsg(player, Messages.INTERACT_CANNOT_PLACE_BLOCK));
                return;
            }
        }
        if (event.getBlockPlaced().getType().toString().contains("STRIPPED_") && event.getBlock().getType().toString().contains("_WOOD") && !arenaByPlayer.getConfig().getBoolean("allow-map-break")) {
            event.setCancelled(true);
            return;
        }
        arenaByPlayer.addPlacedBlock(event.getBlock());
        if (event.getBlock().getType() == Material.TNT) {
            event.getBlockPlaced().setType(Material.AIR);
            final TNTPrimed tntPrimed = event.getBlock().getLocation().getWorld().spawn(event.getBlock().getLocation().add(0.5, 0.5, 0.5), TNTPrimed.class);
            tntPrimed.setFuseTicks(45);
            BedWars.getInstance().getNms().setSource(tntPrimed, player);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreakMonitor(final BlockBreakEvent event) {
        final IArena arenaByPlayer = Arena.getArenaByPlayer(event.getPlayer());
        if (arenaByPlayer != null) arenaByPlayer.removePlacedBlock(event.getBlock());
    }
    
    @EventHandler
    public void onBlockDrop(final ItemSpawnEvent event) {
        if (Arena.getArenaByIdentifier(event.getEntity().getWorld().getName()) == null) return;
        final String string = event.getEntity().getItemStack().getType().toString();
        if (string.equals("SEEDS") || string.equals("WHEAT_SEEDS")) event.setCancelled(true);
    }
    
    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        if (event.isCancelled()) return;
        final Player player = event.getPlayer();
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer != null) {
            if (!arenaByPlayer.isPlayer(player)) {
                event.setCancelled(true);
                return;
            }
            if (arenaByPlayer.getRespawnSessions().containsKey(player)) {
                event.setCancelled(true);
                return;
            }
            if (arenaByPlayer.getStatus() != GameStatus.playing) {
                event.setCancelled(true);
                return;
            }
            final String string = event.getBlock().getType().toString();
            switch (string) {
                case "LONG_GRASS":
                case "TALL_GRASS":
                case "TALL_SEAGRASS":
                case "SEAGRASS":
                case "SUGAR_CANE":
                case "SUGAR_CANE_BLOCK":
                case "GRASS_PATH":
                case "DOUBLE_PLANT":
                    if (event.isCancelled()) event.setCancelled(false);
                default:
                    if (BedWars.getInstance().getNms().isBed(event.getBlock().getType())) {
                        for (final ITeam team : arenaByPlayer.getTeams()) {
                            for (int i = event.getBlock().getX() - 2; i < event.getBlock().getX() + 2; ++i) {
                                for (int j = event.getBlock().getY() - 2; j < event.getBlock().getY() + 2; ++j) {
                                    for (int k = event.getBlock().getZ() - 2; k < event.getBlock().getZ() + 2; ++k) {
                                        if (team.getBed().getBlockX() == i && team.getBed().getBlockY() == j && team.getBed().getBlockZ() == k && !team.isBedDestroyed()) {
                                            if (team.isMember(player)) {
                                                player.sendMessage(Language.getMsg(player, Messages.INTERACT_CANNOT_BREAK_OWN_BED));
                                                event.setCancelled(true);
                                            } else {
                                                event.setCancelled(false);
                                                team.setBedDestroyed(true);
                                                arenaByPlayer.addPlayerBedDestroyed(player);
                                                Bukkit.getPluginManager().callEvent(new PlayerBedBreakEvent(event.getPlayer(), arenaByPlayer.getTeam(player), team, arenaByPlayer));
                                                for (final Player player2 : arenaByPlayer.getWorld().getPlayers()) {
                                                    if (team.isMember(player2)) {
                                                        player2.sendMessage(Language.getMsg(player2, Messages.INTERACT_BED_DESTROY_CHAT_ANNOUNCEMENT_TO_VICTIM).replace("{TeamColor}", team.getColor().chat().toString()).replace("{TeamName}", team.getDisplayName(Language.getPlayerLanguage(player2))).replace("{PlayerColor}", arenaByPlayer.getTeam(player).getColor().chat().toString()).replace("{PlayerName}", player.getDisplayName()));
                                                        BedWars.getInstance().getNms().sendTitle(player2, Language.getMsg(player2, Messages.INTERACT_BED_DESTROY_TITLE_ANNOUNCEMENT), Language.getMsg(player2, Messages.INTERACT_BED_DESTROY_SUBTITLE_ANNOUNCEMENT), 0, 25, 0);
                                                    } else {
                                                        player2.sendMessage(Language.getMsg(player2, Messages.INTERACT_BED_DESTROY_CHAT_ANNOUNCEMENT).replace("{TeamColor}", team.getColor().chat().toString()).replace("{TeamName}", team.getDisplayName(Language.getPlayerLanguage(player2))).replace("{PlayerColor}", arenaByPlayer.getTeam(player).getColor().chat().toString()).replace("{PlayerName}", player.getDisplayName()));
                                                    }
                                                    Sounds.playSound("bed-destroy", player2);
                                                }
                                            }
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    for (final Region region : arenaByPlayer.getRegionsList()) {
                        if (region.isInRegion(event.getBlock().getLocation()) && region.isProtected()) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                    if (!arenaByPlayer.getConfig().getBoolean("allow-map-break") && !arenaByPlayer.isBlockPlaced(event.getBlock())) {
                        event.setCancelled(true);
                        break;
                    }
                    break;
            }
        }
    }
    
    @EventHandler
    public void onBucketFill(final PlayerBucketFillEvent event) {
        if (event.isCancelled()) return;
        final IArena arenaByPlayer = Arena.getArenaByPlayer(event.getPlayer());
        if (arenaByPlayer != null && (arenaByPlayer.isSpectator(event.getPlayer()) || arenaByPlayer.getStatus() != GameStatus.playing || arenaByPlayer.getRespawnSessions().containsKey(event.getPlayer()))) event.setCancelled(true);
    }
    
    @EventHandler
    public void onBucketEmpty(final PlayerBucketEmptyEvent event) {
        if (event.isCancelled()) return;
        final IArena arenaByIdentifier = Arena.getArenaByIdentifier(event.getBlockClicked().getWorld().getName());
        if (arenaByIdentifier != null && arenaByIdentifier.getStatus() != GameStatus.playing) {
            event.setCancelled(true);
            return;
        }
        final Player player = event.getPlayer();
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer != null) {
            if (arenaByPlayer.isSpectator(player)) {
                event.setCancelled(true);
                return;
            }
            if (arenaByPlayer.getRespawnSessions().containsKey(player)) {
                event.setCancelled(true);
                return;
            }
            if (arenaByPlayer.getStatus() != GameStatus.playing) {
                event.setCancelled(true);
                return;
            }
            if (event.getBlockClicked().getLocation().getBlockY() >= arenaByPlayer.getConfig().getInt("max-build-y")) {
                event.setCancelled(true);
                return;
            }
            try {
                for (final ITeam team : arenaByPlayer.getTeams()) {
                    if (team.getSpawn().distance(event.getBlockClicked().getLocation()) <= arenaByPlayer.getConfig().getInt("spawn-protection")) {
                        event.setCancelled(true);
                        player.sendMessage(Language.getMsg(player, Messages.INTERACT_CANNOT_PLACE_BLOCK));
                        return;
                    }
                    if (team.getShop().distance(event.getBlockClicked().getLocation()) <= arenaByPlayer.getConfig().getInt("shop-protection")) {
                        event.setCancelled(true);
                        player.sendMessage(Language.getMsg(player, Messages.INTERACT_CANNOT_PLACE_BLOCK));
                        return;
                    }
                    if (team.getTeamUpgrades().distance(event.getBlockClicked().getLocation()) <= arenaByPlayer.getConfig().getInt("upgrades-protection")) {
                        event.setCancelled(true);
                        player.sendMessage(Language.getMsg(player, Messages.INTERACT_CANNOT_PLACE_BLOCK));
                        return;
                    }
                    for (IGenerator iGenerator : team.getGenerators()) {
                        if (iGenerator.getLocation().distance(event.getBlockClicked().getLocation()) <= 1.0) {
                            event.setCancelled(true);
                            player.sendMessage(Language.getMsg(player, Messages.INTERACT_CANNOT_PLACE_BLOCK));
                            return;
                        }
                    }
                }
                for (IGenerator iGenerator : arenaByPlayer.getOreGenerators()) {
                    if (iGenerator.getLocation().distance(event.getBlockClicked().getLocation()) <= 1.0) {
                        event.setCancelled(true);
                        player.sendMessage(Language.getMsg(player, Messages.INTERACT_CANNOT_PLACE_BLOCK));
                        return;
                    }
                }
            } catch (Exception ignored) {
                // Ignored
            }
            Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> BedWars.getInstance().getNms().minusAmount(event.getPlayer(), event.getItemStack(), 1), 3L);
        }
    }
    
    @EventHandler
    public void onBlow(final EntityExplodeEvent event) {
        if (event.isCancelled()) return;
        if (event.blockList().isEmpty()) return;
        final IArena arenaByIdentifier = Arena.getArenaByIdentifier(event.blockList().get(0).getWorld().getName());
        if (arenaByIdentifier != null && arenaByIdentifier.getNextEvent() != NextEvent.GAME_END) {
            for (final Block block : new ArrayList<>(event.blockList())) {
                if (!arenaByIdentifier.isBlockPlaced(block)) {
                    event.blockList().remove(block);
                } else {
                    if (!AutoCreateTeams.is13Higher() || !block.getType().toString().contains("_GLASS")) continue;
                    event.blockList().remove(block);
                }
            }
        }
    }
    
    @EventHandler
    public void onBlockExplode(final BlockExplodeEvent event) {
        if (event.isCancelled()) return;
        if (event.blockList().isEmpty()) return;
        final IArena arenaByIdentifier = Arena.getArenaByIdentifier(event.blockList().get(0).getWorld().getName());
        if (arenaByIdentifier != null && arenaByIdentifier.getNextEvent() != NextEvent.GAME_END) {
            for (final Block block : new ArrayList<>(event.blockList())) {
                if (!arenaByIdentifier.isBlockPlaced(block)) {
                    event.blockList().remove(block);
                } else {
                    if (!AutoCreateTeams.is13Higher() || !block.getType().toString().contains("_GLASS")) continue;
                    event.blockList().remove(block);
                }
            }
        }
    }
    
    @EventHandler
    public void onPaintingRemove(final HangingBreakByEntityEvent event) {
        if (event.getEntity().getType() == EntityType.PAINTING || event.getEntity().getType() == EntityType.ITEM_FRAME) event.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onBlockCanBuildEvent(final BlockCanBuildEvent event) {
        if (event.isBuildable()) return;
        final IArena arenaByIdentifier = Arena.getArenaByIdentifier(event.getBlock().getWorld().getName());
        if (arenaByIdentifier != null) {
            boolean b = false;
            for (final ITeam team : arenaByIdentifier.getTeams()) {
                for (int i = event.getBlock().getX() - 1; i < event.getBlock().getX() + 1; ++i) {
                    for (int j = event.getBlock().getZ() - 1; j < event.getBlock().getZ() + 1; ++j) {
                        if (team.getBed().getBlockX() == i && team.getBed().getBlockY() == event.getBlock().getY() && team.getBed().getBlockZ() == j) {
                            event.setBuildable(false);
                            b = true;
                            break;
                        }
                    }
                }
                if (team.getBed().getBlockX() == event.getBlock().getX() && team.getBed().getBlockY() + 1 == event.getBlock().getY() && team.getBed().getBlockZ() == event.getBlock().getZ() && !b) {
                    event.setBuildable(true);
                    break;
                }
            }
        }
    }
    
    @EventHandler
    public void soilChangeEntity(final EntityChangeBlockEvent event) {
        if (event.getTo() == Material.DIRT && (event.getBlock().getType().toString().equals("FARMLAND") || event.getBlock().getType().toString().equals("SOIL")) && (Arena.getArenaByIdentifier(event.getBlock().getWorld().getName()) != null)) event.setCancelled(true);
    }
    
    public static boolean isBuildSession(final Player player) {
        return BreakPlace.buildSession.contains(player);
    }
    
    public static void addBuildSession(final Player player) {
        BreakPlace.buildSession.add(player);
    }
    
    public static void removeBuildSession(final Player player) {
        BreakPlace.buildSession.remove(player);
    }
    
    static {
        buildSession = new ArrayList<>();
    }
}
