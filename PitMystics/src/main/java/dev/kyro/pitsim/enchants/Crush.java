package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Crush extends PitEnchant {

	public Crush() {
		super("Crush", false, ApplyType.SWORDS,
				"crush");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canAttack(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(attackEvent.attacker, 2 * 20);
		if(cooldown.isOnCooldown()) return; else cooldown.reset();

		Misc.applyPotionEffect(attackEvent.defender, PotionEffectType.WEAKNESS, getDuration(enchantLvl), enchantLvl + 3, true, false);
		attackEvent.attacker.playSound(attackEvent.attacker.getLocation(), Sound.GLASS, 1, 0.80F);
		attackEvent.defender.playSound(attackEvent.defender.getLocation(), Sound.GLASS, 1, 0.80F);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Strikes apply &cWeakness " + AUtil.toRoman(enchantLvl + 4), "&7(lasts " + (getDuration(enchantLvl) / 20D) +
				"s, 2s cooldown)").getLore();
	}

	public int getDuration(int enchantLvl) {

		return (int) (Math.floor(Math.pow(enchantLvl, 0.7) * 2 + enchantLvl / 2D) * 2);
	}
}
