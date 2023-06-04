package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;

import java.util.List;

public class GottaGoFast extends PitEnchant {
	public static GottaGoFast INSTANCE;

	public GottaGoFast() {
		super("Gotta go fast", false, ApplyType.PANTS,
				"gottagofast", "gotta-go-fast", "gtgf", "gotta", "fast");
		INSTANCE = this;
		isUncommonEnchant = true;
	}

	public static float getWalkSpeedIncrease(PitPlayer pitPlayer) {
		if(!INSTANCE.isEnabled()) return 0;

		int enchantLvl = EnchantManager.getEnchantLevel(pitPlayer.player, INSTANCE);
		if(enchantLvl == 0) return 0;

		return getWalkSpeedIncrease(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Move &e" + Misc.roundString(getWalkSpeedIncrease(enchantLvl)) + "&e% faster &7at all times"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that increases " +
				"your movement speed";
	}

	public static int getWalkSpeedIncrease(int enchantLvl) {
		return enchantLvl * 5 + 5;
	}
}
