package dev.eugenio.nasgarbedwars.upgrades.menu;

import dev.eugenio.nasgarbedwars.upgrades.upgradeaction.*;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.api.upgrades.UpgradeAction;
import dev.eugenio.nasgarbedwars.upgrades.UpgradesManager;
import dev.eugenio.nasgarbedwars.upgrades.upgradeaction.*;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UpgradeTier {
    @Getter private final ItemStack displayItem;
    @Getter private final String name;
    @Getter private final List<UpgradeAction> upgradeActions;
    @Getter private final int cost;
    @Getter private final Material currency;
    
    public UpgradeTier(final String s, final String name, final ItemStack itemStack, final int cost, final Material currency) {
        this.upgradeActions = new ArrayList<>();
        this.displayItem = BedWars.getInstance().getNms().addCustomData(itemStack, "MCONT_" + s);
        this.name = name;
        Language.saveIfNotExists(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("{name}", s.replace("upgrade-", "")).replace("{tier}", name), "&cName not set");
        Language.saveIfNotExists(Messages.UPGRADES_UPGRADE_TIER_ITEM_LORE.replace("{name}", s.replace("upgrade-", "")).replace("{tier}", name), Collections.singletonList("&cLore not set"));
        this.cost = cost;
        this.currency = currency;
        for (String item : UpgradesManager.getConfiguration().getYml().getStringList(s + "." + name + ".receive")) {
            final String[] split = item.trim().split(":");
            if (split.length < 2) continue;
            final String[] split2 = split[1].trim().toLowerCase().split(",");
            UpgradeAction upgradeAction = null;
            final String lowerCase = split[0].trim().toLowerCase();
            switch (lowerCase) {
                case "enchant-item": {
                    if (split2.length < 3) {
                        BedWars.getInstance().getLogger().warning("Inválido " + split[0] + " en upgrades: " + s + "." + name);
                        continue;
                    }
                    final Enchantment byName = Enchantment.getByName(split2[0].toUpperCase());
                    if (byName == null) {
                        BedWars.getInstance().getLogger().warning("Encantamiento inválido " + split2[0].toUpperCase() + " en upgrades: " + s + "." + name);
                        continue;
                    }
                    EnchantItemAction.ApplyType applyType = null;
                    final String lowerCase2 = split2[2].toLowerCase();
                    switch (lowerCase2) {
                        case "sword": {
                            applyType = EnchantItemAction.ApplyType.SWORD;
                            break;
                        }
                        case "armor": {
                            applyType = EnchantItemAction.ApplyType.ARMOR;
                            break;
                        }
                        case "bow": {
                            applyType = EnchantItemAction.ApplyType.BOW;
                            break;
                        }
                    }
                    if (applyType == null) {
                        BedWars.getInstance().getLogger().warning("Inválido apply type " + split2[2] + " en upgrades: " + s + "." + name);
                        continue;
                    }
                    int int1 = 1;
                    try {
                        int1 = Integer.parseInt(split2[1]);
                    } catch (Exception ignored) {
                        // Ignored
                    }
                    upgradeAction = new EnchantItemAction(byName, int1, applyType);
                    break;
                }
                case "player-effect": {
                    if (split2.length < 4) {
                        BedWars.getInstance().getLogger().warning("Inválido " + split[0] + " en upgrades: " + s + "." + name);
                        continue;
                    }
                    final PotionEffectType byName2 = PotionEffectType.getByName(split2[0].toUpperCase());
                    if (byName2 == null) {
                        BedWars.getInstance().getLogger().warning("Efecto de poción inválido " + split2[0] + " en upgrades: " + s + "." + name);
                        continue;
                    }
                    PlayerEffectAction.ApplyType applyType2 = null;
                    final String lowerCase3 = split2[3].toLowerCase();
                    switch (lowerCase3) {
                        case "team": {
                            applyType2 = PlayerEffectAction.ApplyType.TEAM;
                            break;
                        }
                        case "base": {
                            applyType2 = PlayerEffectAction.ApplyType.BASE;
                            break;
                        }
                    }
                    if (applyType2 == null) {
                        BedWars.getInstance().getLogger().warning("Inválido apply type " + split2[3] + " en upgrades: " + s + "." + name);
                        continue;
                    }
                    int int2 = 1;
                    int int3 = 0;
                    try {
                        int2 = Integer.parseInt(split2[1]);
                        int3 = Integer.parseInt(split2[2]);
                    } catch (Exception ignored) {
                        // Ignored
                    }
                    upgradeAction = new PlayerEffectAction(byName2, int2, int3, applyType2);
                    break;
                }
                case "generator-edit": {
                    if (split2.length < 4) {
                        BedWars.getInstance().getLogger().warning("Inválido " + split[0] + " en upgrades: " + s + "." + name);
                        continue;
                    }
                    GeneratorEditAction.ApplyType applyType3 = null;
                    final String lowerCase4 = split2[0].toLowerCase();
                    switch (lowerCase4) {
                        case "gold":
                        case "g": {
                            applyType3 = GeneratorEditAction.ApplyType.GOLD;
                            break;
                        }
                        case "iron":
                        case "i": {
                            applyType3 = GeneratorEditAction.ApplyType.IRON;
                            break;
                        }
                        case "emerald":
                        case "e": {
                            applyType3 = GeneratorEditAction.ApplyType.EMERALD;
                            break;
                        }
                    }
                    if (applyType3 == null) BedWars.getInstance().getLogger().warning("Tipo de generador inválido " + split2[0] + " en upgrades: " + s + "." + name);
                    int int4;
                    int int5;
                    int int6;
                    try {
                        int4 = Integer.parseInt(split2[1]);
                        int5 = Integer.parseInt(split2[2]);
                        int6 = Integer.parseInt(split2[3]);
                    } catch (Exception ex3) {
                        BedWars.getInstance().getLogger().warning("Configuración de generador inválido " + split2[0] + " en upgrades: " + s + "." + name);
                        continue;
                    }
                    upgradeAction = new GeneratorEditAction(applyType3, int5, int4, int6);
                    break;
                }
                case "dragon": {
                    if (split2.length < 1) {
                        BedWars.getInstance().getLogger().warning("Inválido " + split[0] + " en upgrades: " + s + "." + name);
                        continue;
                    }
                    int int7;
                    try {
                        int7 = Integer.parseInt(split2[0]);
                    } catch (Exception ex4) {
                        BedWars.getInstance().getLogger().warning("Invalid dragon amount en upgrades: " + s + "." + name);
                        continue;
                    }
                    upgradeAction = new DragonAction(int7);
                    break;
                }
                case "command": {
                    if (split2.length < 2) {
                        BedWars.getInstance().getLogger().warning("Inválido " + split[0] + " en upgrades: " + s + "." + name);
                        continue;
                    }
                    DispatchCommand.CommandType value;
                    try {
                        value = DispatchCommand.CommandType.valueOf(split2[0].toUpperCase());
                    } catch (Exception ex5) {
                        BedWars.getInstance().getLogger().warning("Invalid command type " + split2[0] + " en upgrades: " + s + "." + name);
                        continue;
                    }
                    upgradeAction = new DispatchCommand(value, split[1].split(",")[1]);
                    break;
                }
            }
            if (upgradeAction == null) continue;
            this.upgradeActions.add(upgradeAction);
        }
    }
}
