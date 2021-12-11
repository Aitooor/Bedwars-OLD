package dev.eugenio.nasgarbedwars.upgrades.upgradeaction;

import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.upgrades.UpgradeAction;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class EnchantItemAction implements UpgradeAction {
    private final Enchantment enchantment;
    private final int amplifier;
    private final ApplyType type;
    
    public EnchantItemAction(final Enchantment enchantment, final int amplifier, final ApplyType type) {
        this.enchantment = enchantment;
        this.amplifier = amplifier;
        this.type = type;
    }
    
    @Override
    public void onBuy(final Player player, final ITeam team) {
        if (this.type == ApplyType.ARMOR) {
            team.addArmorEnchantment(this.enchantment, this.amplifier);
        }
        else if (this.type == ApplyType.SWORD) {
            team.addSwordEnchantment(this.enchantment, this.amplifier);
        }
        else if (this.type == ApplyType.BOW) {
            team.addBowEnchantment(this.enchantment, this.amplifier);
        }
    }
    
    public enum ApplyType {
        SWORD, 
        ARMOR, 
        BOW
    }
}
