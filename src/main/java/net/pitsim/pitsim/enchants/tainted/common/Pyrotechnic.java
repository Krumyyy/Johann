package net.pitsim.pitsim.enchants.tainted.common;

import net.pitsim.pitsim.adarkzone.PitMob;
import net.pitsim.pitsim.adarkzone.mobs.PitBlaze;
import net.pitsim.pitsim.adarkzone.mobs.PitWitherSkeleton;
import net.pitsim.pitsim.adarkzone.mobs.PitZombiePigman;
import net.pitsim.pitsim.controllers.objects.BasicDarkzoneEnchant;
import net.pitsim.pitsim.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class Pyrotechnic extends BasicDarkzoneEnchant {
	public static Pyrotechnic INSTANCE;

	public Pyrotechnic() {
		super("Pyrotechnic", false, ApplyType.CHESTPLATES,
				"pyrotechnic", "pyro");
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public int getBaseStatPercent(int enchantLvl) {
		return enchantLvl * 4 + 4;
	}

	@Override
	public boolean isOffensive() {
		return false;
	}

	@Override
	public List<Class<? extends PitMob>> getApplicableMobs() {
		return Arrays.asList(PitBlaze.class, PitZombiePigman.class, PitWitherSkeleton.class);
	}
}