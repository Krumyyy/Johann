package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.DamageEvent;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.Misc;

import java.util.List;

public class Prick extends PitEnchant {

	public Prick() {
		super("Prick", false, ApplyType.PANTS,
				"prick", "thorns");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = EnchantManager.getEnchantLevel(damageEvent.defender, this);
		if(enchantLvl == 0) return damageEvent;

		damageEvent.selfTrueDamage += getDamage(enchantLvl);

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Enemies hitting you receive", "&c" + Misc.getHearts(getDamage(enchantLvl)) + " &7true damage").getLore();
	}

	public double getDamage(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 0.5;
			case 2:
				return 0.75;
			case 3:
				return 1.0;

		}

		return 0;
	}
}