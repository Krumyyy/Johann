package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Wasp extends PitEnchant {

	public Wasp() {
		super("Wasp", false, ApplyType.BOWS,
				"wasp");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(MapManager.inDarkzone(attackEvent.getAttacker())) return;

		Misc.applyPotionEffect(attackEvent.getDefender(), PotionEffectType.WEAKNESS, getDuration(enchantLvl) * 20, enchantLvl, true, false);

		PitPlayer pitPlayer = attackEvent.getAttackerPitPlayer();
		pitPlayer.stats.wasp++;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Apply &cWeakness " + AUtil.toRoman(enchantLvl + 1) + " &7(" +
				getDuration(enchantLvl) + "s) on hit"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that effects your " +
				"opponents with &cWeakness &7when hit";
	}

	public int getDuration(int enchantLvl) {
		return enchantLvl * 5 + 1;
	}
}
