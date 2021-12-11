package dev.eugenio.nasgarbedwars.listeners;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.shop.ShopCache;
import dev.eugenio.nasgarbedwars.shop.listeners.InventoryListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Openable;
import org.bukkit.metadata.FixedMetadataValue;

public class Interact implements Listener {
    @EventHandler
    public void onItemCommand(final PlayerInteractEvent event) {
        if (event == null) return;
        final Player player = event.getPlayer();
        if (player == null) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            final ItemStack itemInHand = BedWars.getInstance().getNms().getItemInHand(player);
            if (!BedWars.getInstance().getNms().isCustomBedWarsItem(itemInHand)) return;
            final String[] split = BedWars.getInstance().getNms().getCustomData(itemInHand).split("_");
            if (split.length >= 2 && split[0].equals("RUNCOMMAND")) {
                event.setCancelled(true);
                Bukkit.dispatchCommand(player, split[1]);
            }
        }
    }
    
    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        if (event == null) return;
        final Player player = event.getPlayer();
        if (player == null) return;
        Arena.afkCheck.remove(player.getUniqueId());
        if (BedWars.getInstance().getApi().getAFKUtil().isPlayerAFK(event.getPlayer())) BedWars.getInstance().getApi().getAFKUtil().setPlayerAFK(event.getPlayer(), false);
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null) return;
            if (clickedBlock.getType() == Material.AIR) return;
            final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
            if (arenaByPlayer != null) {
                if (arenaByPlayer.getRespawnSessions().containsKey(event.getPlayer())) {
                    event.setCancelled(true);
                    return;
                }
                if (BedWars.getInstance().getNms().isBed(clickedBlock.getType())) {
                    if (player.isSneaking()) {
                        final ItemStack itemInHand = BedWars.getInstance().getNms().getItemInHand(player);
                        if (itemInHand == null) {
                            event.setCancelled(true);
                        } else if (itemInHand.getType() == Material.AIR) {
                            event.setCancelled(true);
                        }
                    } else {
                        event.setCancelled(true);
                    }
                    return;
                }
                if (clickedBlock.getType() == Material.CHEST) {
                    if (arenaByPlayer.isSpectator(player) || arenaByPlayer.getRespawnSessions().containsKey(player)) {
                        event.setCancelled(true);
                        return;
                    }
                    ITeam team = null;
                    final int int1 = arenaByPlayer.getConfig().getInt("island-radius");
                    for (final ITeam team2 : arenaByPlayer.getTeams()) if (team2.getSpawn().distance(event.getClickedBlock().getLocation()) <= int1) team = team2;
                    if (team != null && !team.isMember(player) && (!team.getMembers().isEmpty() || !team.isBedDestroyed())) {
                        event.setCancelled(true);
                        player.sendMessage(Language.getMsg(player, Messages.INTERACT_CHEST_CANT_OPEN_TEAM_ELIMINATED));
                    }
                }
                if (arenaByPlayer.isSpectator(player) || arenaByPlayer.getRespawnSessions().containsKey(player)) {
                    switch (clickedBlock.getType()) {
                        case CHEST:
                        case ENDER_CHEST:
                        case ANVIL:
                        case WORKBENCH:
                        case HOPPER:
                        case TRAPPED_CHEST:
                            event.setCancelled(true);
                            break;
                    }
                    if (clickedBlock.getState() instanceof Openable) event.setCancelled(true);
                }
            }
        }
        final ItemStack item = event.getItem();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (item == null) return;
            final IArena arenaByPlayer2 = Arena.getArenaByPlayer(player);
            if (arenaByPlayer2 != null && arenaByPlayer2.isPlayer(player) && item.getType() == BedWars.getInstance().getNms().materialFireball()) {
                event.setCancelled(true);
                final Fireball setFireballDirection = BedWars.getInstance().getNms().setFireballDirection(player.launchProjectile(Fireball.class), player.getEyeLocation().getDirection());
                setFireballDirection.setVelocity(setFireballDirection.getDirection().multiply(3));
                setFireballDirection.setIsIncendiary(false);
                setFireballDirection.setMetadata("zcbw", new FixedMetadataValue(BedWars.getInstance(), "ceva"));
                BedWars.getInstance().getNms().minusAmount(player, item, 1);
            }
        }
    }
    
    @EventHandler
    public void disableItemFrameRotation(final PlayerInteractEntityEvent event) {
        if (event == null) return;
        if (event.getRightClicked().getType() == EntityType.ITEM_FRAME) {
            if (((ItemFrame)event.getRightClicked()).getItem().getType().equals(Material.AIR)) {
                final ItemStack itemInHand = BedWars.getInstance().getNms().getItemInHand(event.getPlayer());
                if (itemInHand != null && itemInHand.getType() != Material.AIR) {
                    final ShopCache shopCache = ShopCache.getShopCache(event.getPlayer().getUniqueId());
                    if (shopCache != null && InventoryListener.isUpgradable(itemInHand, shopCache)) event.setCancelled(true);
                }
                return;
            }
            if (Arena.getArenaByIdentifier(event.getPlayer().getWorld().getName()) != null) event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onEntityInteract(final PlayerInteractEntityEvent event) {
        if (event == null) return;
        final IArena arenaByPlayer = Arena.getArenaByPlayer(event.getPlayer());
        if (arenaByPlayer == null) return;
        final Location location = event.getRightClicked().getLocation();
        for (final ITeam team : arenaByPlayer.getTeams()) {
            final Location shop = team.getShop();
            final Location teamUpgrades = team.getTeamUpgrades();
            if (location.getBlockX() == shop.getBlockX() && location.getBlockY() == shop.getBlockY() && location.getBlockZ() == shop.getBlockZ()) {
                event.setCancelled(true);
            } else {
                if (location.getBlockX() != teamUpgrades.getBlockX() || location.getBlockY() != teamUpgrades.getBlockY() || location.getBlockZ() != teamUpgrades.getBlockZ()) continue;
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onBedEnter(final PlayerBedEnterEvent event) {
        if (event == null) return;
        if (Arena.getArenaByPlayer(event.getPlayer()) != null) event.setCancelled(true);
    }
    
    @EventHandler
    public void onArmorManipulate(final PlayerArmorStandManipulateEvent event) {
        if (event == null) return;
        if (event.isCancelled()) return;
        if (Arena.getArenaByPlayer(event.getPlayer()) != null) event.setCancelled(true);
    }
    
    @EventHandler
    public void onCrafting(final PrepareItemCraftEvent event) {
        if (event == null) return;
        event.getInventory().setResult(new ItemStack(Material.AIR));
    }
}
