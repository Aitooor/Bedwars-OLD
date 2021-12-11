package dev.eugenio.nasgarbedwars.upgrades.trapaction;

import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.upgrades.TrapAction;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DisenchantAction implements TrapAction {
    private final Enchantment enchantment;
    private final ApplyType type;
    
    public DisenchantAction(final Enchantment enchantment, final ApplyType type) {
        this.enchantment = enchantment;
        this.type = type;
    }
    
    @Override
    public String getName() {
        return "disenchant-item";
    }
    
    @Override
    public void onTrigger(final Player player, final ITeam team, final ITeam team2) {
        if (this.type == ApplyType.SWORD) {
            for (final ItemStack itemStack : player.getInventory()) {
                if (BedWars.getInstance().getNms().isSword(itemStack)) itemStack.removeEnchantment(this.enchantment);
                player.updateInventory();
            }
        } else if (this.type == ApplyType.ARMOR) {
            for (final ItemStack itemStack2 : player.getInventory()) {
                if (BedWars.getInstance().getNms().isArmor(itemStack2)) itemStack2.removeEnchantment(this.enchantment);
                player.updateInventory();
            }
            for (final ItemStack itemStack3 : player.getInventory().getArmorContents()) {
                if (BedWars.getInstance().getNms().isArmor(itemStack3)) itemStack3.removeEnchantment(this.enchantment);
                player.updateInventory();
            }
        } else if (this.type == ApplyType.BOW) {
            for (final ItemStack itemStack4 : player.getInventory()) {
                if (BedWars.getInstance().getNms().isBow(itemStack4))  itemStack4.removeEnchantment(this.enchantment);
                player.updateInventory();
            }
        }
    }
    
    public enum ApplyType {
        SWORD, 
        ARMOR, 
        BOW
    }
}
