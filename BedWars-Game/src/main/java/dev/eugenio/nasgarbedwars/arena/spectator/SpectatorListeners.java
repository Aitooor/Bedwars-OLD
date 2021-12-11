package dev.eugenio.nasgarbedwars.arena.spectator;

import dev.eugenio.nasgarbedwars.configuration.Sounds;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerKillEvent;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerLeaveArenaEvent;
import dev.eugenio.nasgarbedwars.api.events.spectator.SpectatorFirstPersonEnterEvent;
import dev.eugenio.nasgarbedwars.api.events.spectator.SpectatorFirstPersonLeaveEvent;
import dev.eugenio.nasgarbedwars.api.events.spectator.SpectatorTeleportToPlayerEvent;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

public class SpectatorListeners implements Listener {
    @EventHandler
    public void onSpectatorItemInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack itemInHand = BedWars.getInstance().getNms().getItemInHand(player);
        if (itemInHand == null) return;
        if (itemInHand.getType() == Material.AIR) return;
        if (!BedWars.getInstance().getNms().isCustomBedWarsItem(itemInHand)) return;
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) return;
        if (!arenaByPlayer.isSpectator(player)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onSpectatorBlockInteract(final PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (!BedWars.getInstance().getApi().getArenaUtil().isSpectating(event.getPlayer())) return;
        if (event.getClickedBlock().getType().toString().contains("DOOR")) event.setCancelled(true);
    }
    
    @EventHandler
    public void onSpectatorInventoryClose(final InventoryCloseEvent event) {
        TeleporterGUI.closeGUI((Player)event.getPlayer());
    }
    
    @EventHandler
    public void onSpectatorClick(final InventoryClickEvent event) {
        if (event.getWhoClicked().getGameMode() == GameMode.SPECTATOR) {
            event.setCancelled(true);
            return;
        }
        if (event.getCurrentItem() == null) return;
        final ItemStack currentItem = event.getCurrentItem();
        if (currentItem.getType() == Material.AIR) return;
        final Player player = (Player)event.getWhoClicked();
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) return;
        if (!arenaByPlayer.isSpectator(player)) return;
        if (BedWars.getInstance().getNms().isPlayerHead(currentItem.getType().toString(), 3) && BedWars.getInstance().getNms().itemStackDataCompare(currentItem, (short)3) && BedWars.getInstance().getNms().isCustomBedWarsItem(currentItem)) {
            event.setCancelled(true);
            final String customData = BedWars.getInstance().getNms().getCustomData(currentItem);
            if (customData.contains("spectatorTeleporterGUIhead_")) {
                final Player player2 = Bukkit.getPlayer(customData.replace("spectatorTeleporterGUIhead_", ""));
                if (player2 == null) return;
                if (!player2.isOnline()) return;
                if (player2.isDead()) return;
                final SpectatorTeleportToPlayerEvent spectatorTeleportToPlayerEvent = new SpectatorTeleportToPlayerEvent(player, player2, arenaByPlayer);
                Bukkit.getPluginManager().callEvent(spectatorTeleportToPlayerEvent);
                if (!spectatorTeleportToPlayerEvent.isCancelled()) player.teleport(player2);
                Sounds.playSound("spectator-gui-click", player);
                player.closeInventory();
            }
        }
    }
    
    @EventHandler
    public void onHealthChange(final EntityRegainHealthEvent event) {
        if (event.getEntity().getType() != EntityType.PLAYER) return;
        final Player player = (Player) event.getEntity();
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) return;
        if (arenaByPlayer.isPlayer(player)) TeleporterGUI.refreshAllGUIs();
    }
    
    @EventHandler
    public void onFoodChange(final FoodLevelChangeEvent event) {
        if (event.getEntity().getType() != EntityType.PLAYER) return;
        final Player player = (Player)event.getEntity();
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) return;
        if (arenaByPlayer.isPlayer(player)) {
            TeleporterGUI.refreshAllGUIs();
        }
    }
    
    @EventHandler
    public void onPlayerLeave(final PlayerLeaveArenaEvent event) {
        if (event.getArena().isPlayer(event.getPlayer())) TeleporterGUI.refreshAllGUIs();
    }
    
    @EventHandler
    public void onSpectatorInteractPlayer(final PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() != EntityType.PLAYER) return;
        final Player player5 = event.getPlayer();
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player5);
        if (arenaByPlayer == null) return;
        if (arenaByPlayer.isPlayer(player5)) return;
        event.setCancelled(true);
        final Player spectatorTarget = (Player)event.getRightClicked();
        if (arenaByPlayer.isPlayer(spectatorTarget)) {
            if (player5.getSpectatorTarget() != null) Bukkit.getPluginManager().callEvent(new SpectatorFirstPersonLeaveEvent(player5, arenaByPlayer, player -> Language.getMsg(player, Messages.ARENA_SPECTATOR_FIRST_PERSON_LEAVE_TITLE), player2 -> Language.getMsg(player2, Messages.ARENA_SPECTATOR_FIRST_PERSON_LEAVE_SUBTITLE)));
            final SpectatorFirstPersonEnterEvent spectatorFirstPersonEnterEvent = new SpectatorFirstPersonEnterEvent(player5, spectatorTarget, arenaByPlayer, player3 -> Language.getMsg(player3, Messages.ARENA_SPECTATOR_FIRST_PERSON_ENTER_TITLE), player4 -> Language.getMsg(player4, Messages.ARENA_SPECTATOR_FIRST_PERSON_ENTER_SUBTITLE));
            Bukkit.getPluginManager().callEvent(spectatorFirstPersonEnterEvent);
            if (spectatorFirstPersonEnterEvent.isCancelled()) return;
            player5.getInventory().setHeldItemSlot(5);
            player5.setGameMode(GameMode.SPECTATOR);
            player5.setSpectatorTarget(spectatorTarget);
            BedWars.getInstance().getNms().sendTitle(player5, spectatorFirstPersonEnterEvent.getTitle().apply(player5).replace("{player}", spectatorTarget.getDisplayName()), spectatorFirstPersonEnterEvent.getSubTitle().apply(player5).replace("{player}", spectatorTarget.getDisplayName()), spectatorFirstPersonEnterEvent.getFadeIn(), spectatorFirstPersonEnterEvent.getStay(), spectatorFirstPersonEnterEvent.getFadeOut());
        }
    }
    
    @EventHandler
    public void onSneak(final PlayerToggleSneakEvent event) {
        final Player player3 = event.getPlayer();
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player3);
        if (arenaByPlayer == null) return;
        if (arenaByPlayer.isSpectator(player3) && player3.getSpectatorTarget() != null) {
            player3.setGameMode(GameMode.ADVENTURE);
            player3.setAllowFlight(true);
            player3.setFlying(true);
            final SpectatorFirstPersonLeaveEvent spectatorFirstPersonLeaveEvent = new SpectatorFirstPersonLeaveEvent(player3, arenaByPlayer, player -> Language.getMsg(player, Messages.ARENA_SPECTATOR_FIRST_PERSON_LEAVE_TITLE), player2 -> Language.getMsg(player2, Messages.ARENA_SPECTATOR_FIRST_PERSON_LEAVE_SUBTITLE));
            Bukkit.getPluginManager().callEvent(spectatorFirstPersonLeaveEvent);
            BedWars.getInstance().getNms().sendTitle(player3, spectatorFirstPersonLeaveEvent.getTitle().apply(player3), spectatorFirstPersonLeaveEvent.getSubTitle().apply(player3), spectatorFirstPersonLeaveEvent.getFadeIn(), spectatorFirstPersonLeaveEvent.getStay(), spectatorFirstPersonLeaveEvent.getFadeOut());
        }
    }
    
    @EventHandler
    public void onTeleport(final PlayerTeleportEvent event) {
        final IArena arenaByPlayer = Arena.getArenaByPlayer(event.getPlayer());
        if (arenaByPlayer == null) return;
        if (arenaByPlayer.isSpectator(event.getPlayer()) && !event.getTo().getWorld().equals(event.getPlayer().getWorld()) && event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) {
            final Player player3 = event.getPlayer();
            event.setCancelled(true);
            player3.setGameMode(GameMode.ADVENTURE);
            player3.setAllowFlight(true);
            player3.setFlying(true);
            final SpectatorFirstPersonLeaveEvent spectatorFirstPersonLeaveEvent = new SpectatorFirstPersonLeaveEvent(player3, Arena.getArenaByPlayer(player3), player -> Language.getMsg(player, Messages.ARENA_SPECTATOR_FIRST_PERSON_LEAVE_TITLE), player2 -> Language.getMsg(player2, Messages.ARENA_SPECTATOR_FIRST_PERSON_LEAVE_SUBTITLE));
            Bukkit.getPluginManager().callEvent(spectatorFirstPersonLeaveEvent);
            BedWars.getInstance().getNms().sendTitle(player3, spectatorFirstPersonLeaveEvent.getTitle().apply(player3), spectatorFirstPersonLeaveEvent.getSubTitle().apply(player3), spectatorFirstPersonLeaveEvent.getFadeIn(), spectatorFirstPersonLeaveEvent.getStay(), spectatorFirstPersonLeaveEvent.getFadeOut());
        }
    }
    
    @EventHandler
    public void onTargetDeath(final PlayerKillEvent event) {
        for (final Player player3 : event.getArena().getSpectators()) {
            if (player3.getSpectatorTarget() == null) continue;
            if (player3.getSpectatorTarget() != event.getVictim()) continue;
            player3.setGameMode(GameMode.ADVENTURE);
            player3.setAllowFlight(true);
            player3.setFlying(true);
            final SpectatorFirstPersonLeaveEvent spectatorFirstPersonLeaveEvent = new SpectatorFirstPersonLeaveEvent(player3, event.getArena(), player -> Language.getMsg(player, Messages.ARENA_SPECTATOR_FIRST_PERSON_LEAVE_TITLE), player2 -> Language.getMsg(player2, Messages.ARENA_SPECTATOR_FIRST_PERSON_LEAVE_SUBTITLE));
            Bukkit.getPluginManager().callEvent(spectatorFirstPersonLeaveEvent);
            BedWars.getInstance().getNms().sendTitle(player3, spectatorFirstPersonLeaveEvent.getTitle().apply(player3), spectatorFirstPersonLeaveEvent.getSubTitle().apply(player3), spectatorFirstPersonLeaveEvent.getFadeIn(), spectatorFirstPersonLeaveEvent.getStay(), spectatorFirstPersonLeaveEvent.getFadeOut());
        }
    }
    
    @EventHandler
    public void onDamageByEntity(final EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        final IArena arenaByIdentifier = Arena.getArenaByIdentifier(event.getEntity().getWorld().getName());
        if (arenaByIdentifier == null) return;
        Player player = null;
        if (event.getDamager() instanceof Projectile) {
            final ProjectileSource shooter = ((Projectile)event.getDamager()).getShooter();
            if (shooter instanceof Player) player = (Player)shooter;
        } else if (event.getDamager() instanceof Player) {
            player = (Player) event.getDamager();
            if (arenaByIdentifier.getRespawnSessions().containsKey(player)) {
                event.setCancelled(true);
                return;
            }
        } else if (event.getDamager() instanceof TNTPrimed) {
            final TNTPrimed tntPrimed = (TNTPrimed) event.getDamager();
            if (tntPrimed.getSource() instanceof Player) player = (Player)tntPrimed.getSource();
        }
        if (player == null) return;
        if (arenaByIdentifier.isSpectator(player)) event.setCancelled(true);
    }
}
