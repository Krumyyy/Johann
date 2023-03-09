package dev.kyro.pitsim.enchants.tainted.uncommon;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Desperate extends PitEnchant {
	public static Desperate INSTANCE;

	public Desperate() {
		super("Emboldened", false, ApplyType.SCYTHES,
				"emboldened");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(attackEvent.getAttacker().getHealth() >= attackEvent.getDefender().getHealth()) return;

		attackEvent.increasePercent += getDamageIncrease(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Deal &c+" + getDamageIncrease(enchantLvl) + "% more damage when you have less " +
						"health than your target"
		).getLore();
	}

	public int getDamageIncrease(int enchantLvl) {
		return enchantLvl * 8 + 4;
	}
}