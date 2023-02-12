package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BottomlessQuiver extends PitEnchant {

	public BottomlessQuiver() {
		super("Bottomless Quiver", false, ApplyType.BOWS,
				"bq", "bottomless-quiver", "bottom", "quiver");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(attackEvent.getAttacker().equals(attackEvent.getDefender())) return;

		ItemStack arrows = new ItemStack(Material.ARROW);
		arrows.setAmount(getArrowAmount(enchantLvl));
		attackEvent.getAttackerPlayer().getInventory().addItem(arrows);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {

		if(getArrowAmount(enchantLvl) == 1)
			return new PitLoreBuilder(
					"&7Get &f" + getArrowAmount(enchantLvl) + " arrow &7on arrow hit"
			).getLore();

		else return new PitLoreBuilder(
				"&7Get &f" + getArrowAmount(enchantLvl) + " arrows &7on arrow hit"
		).getLore();
	}

	public int getArrowAmount(int enchantLvl) {

		return (int) (Math.pow(enchantLvl, 1.9));
	}
}
