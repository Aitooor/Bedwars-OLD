package dev.eugenio.nasgarbedwars.api.arena.shop;

import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ShopHologram {
    private static List<ShopHologram> shopHologram;
    private static BedWarsAPI api;

    static {
        ShopHologram.shopHologram = new ArrayList<>();
        ShopHologram.api = null;
    }

    private final Location l;
    @Getter
    private String iso;
    private ArmorStand a1;
    private ArmorStand a2;
    @Getter
    private IArena a;

    public ShopHologram(final String iso, final ArmorStand a1, final ArmorStand a2, final Location l, final IArena a3) {
        this.l = l;
        for (final ShopHologram shopHologram : getShopHologram()) {
            if (shopHologram.l == l && shopHologram.iso.equalsIgnoreCase(iso)) {
                if (a1 != null) a1.remove();
                if (a2 != null) a2.remove();
                return;
            }
        }
        this.a1 = a1;
        this.a2 = a2;
        this.iso = iso;
        this.a = a3;
        if (a1 != null) a1.setMarker(true);
        if (a2 != null) a2.setMarker(true);
        ShopHologram.shopHologram.add(this);
        if (ShopHologram.api == null) {
            ShopHologram.api = Bukkit.getServer().getServicesManager().getRegistration(BedWarsAPI.class).getProvider();
        }
    }

    public static void clearForArena(final IArena arena) {
        for (final ShopHologram shopHologram : new ArrayList<>(getShopHologram())) if (shopHologram.a == arena) ShopHologram.shopHologram.remove(shopHologram);
    }

    public static List<ShopHologram> getShopHologram() {
        return ShopHologram.shopHologram;
    }

    public void update() {
        if (this.l == null) Bukkit.broadcastMessage("LOCATION ES NULL");
        for (final Player player : this.l.getWorld().getPlayers()) {
            if (Language.getPlayerLanguage(player).getIso().equalsIgnoreCase(this.iso)) continue;
            if (this.a1 != null) ShopHologram.api.getVersionSupport().hideEntity(this.a1, player);
            if (this.a2 == null) continue;
            ShopHologram.api.getVersionSupport().hideEntity(this.a2, player);
        }
    }

    public void updateForPlayer(final Player player, final String s) {
        if (s.equalsIgnoreCase(this.iso)) return;
        if (this.a1 != null) ShopHologram.api.getVersionSupport().hideEntity(this.a1, player);
        if (this.a2 != null) ShopHologram.api.getVersionSupport().hideEntity(this.a2, player);
    }
}
