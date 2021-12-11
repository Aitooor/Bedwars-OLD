package dev.eugenio.nasgarbedwars.shop.quickbuy;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.shop.ShopManager;
import dev.eugenio.nasgarbedwars.shop.main.CategoryContent;
import dev.eugenio.nasgarbedwars.shop.main.ShopCategory;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class QuickBuyTask extends BukkitRunnable {
    private final UUID uuid;

    public QuickBuyTask(final UUID uuid) {
        this.uuid = uuid;
        this.runTaskLaterAsynchronously(BedWars.getInstance(), 140L);
    }

    public void run() {
        if (Bukkit.getPlayer(this.uuid) == null) {
            this.cancel();
            return;
        }
        if (Bukkit.getPlayer(this.uuid).isOnline()) {
            final PlayerQuickBuyCache quickBuyCache = PlayerQuickBuyCache.getQuickBuyCache(this.uuid);
            if (quickBuyCache == null) {
                this.cancel();
                return;
            }
            if (!BedWars.getInstance().getMySQLDatabase().hasQuickBuy(this.uuid)) {
                if (BedWars.getInstance().getShopManager().getYml().get("quick-buy-defaults") != null) {
                    for (final String s : BedWars.getInstance().getShopManager().getYml().getConfigurationSection("quick-buy-defaults").getKeys(false)) {
                        if (BedWars.getInstance().getShopManager().getYml().get("quick-buy-defaults." + s + ".path") != null) {
                            if (BedWars.getInstance().getShopManager().getYml().get("quick-buy-defaults." + s + ".slot") == null) {
                                continue;
                            }
                            try {
                                Integer.valueOf(BedWars.getInstance().getShopManager().getYml().getString("quick-buy-defaults." + s + ".slot"));
                            } catch (Exception ex) {
                                BedWars.debug(BedWars.getInstance().getShopManager().getYml().getString("quick-buy-defaults." + s + ".slot") + " must be an integer!");
                                continue;
                            }
                            for (ShopCategory shopCategory : ShopManager.getShop().getCategoryList()) {
                                for (final CategoryContent categoryContent : shopCategory.getCategoryContentList())
                                    if (categoryContent.getIdentifier().equals(BedWars.getInstance().getShopManager().getYml().getString("quick-buy-defaults." + s + ".path")))
                                        quickBuyCache.setElement(Integer.parseInt(BedWars.getInstance().getShopManager().getYml().getString("quick-buy-defaults." + s + ".slot")), categoryContent);
                            }
                        }
                    }
                }
            } else {
                for (final int n : PlayerQuickBuyCache.quickSlots) {
                    final String quickBuySlots = BedWars.getInstance().getMySQLDatabase().getQuickBuySlots(this.uuid, n);
                    if (!quickBuySlots.isEmpty()) {
                        if (!quickBuySlots.equals(" ")) {
                            final QuickBuyElement quickBuyElement = new QuickBuyElement(quickBuySlots, n);
                            if (quickBuyElement.isLoaded()) quickBuyCache.addQuickElement(quickBuyElement);
                        }
                    }
                }
            }
        }
    }

    public synchronized void cancel() {
        super.cancel();
    }
}
