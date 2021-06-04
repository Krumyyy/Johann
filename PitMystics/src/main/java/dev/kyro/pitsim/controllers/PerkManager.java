package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;

import java.util.ArrayList;
import java.util.List;

public class PerkManager {

	public static List<PitPerk> pitPerks = new ArrayList<>();

	public static void registerUpgrade(PitPerk pitPerk) {

		pitPerks.add(pitPerk);
		PitSim.INSTANCE.getServer().getPluginManager().registerEvents(pitPerk, PitSim.INSTANCE);
	}
}