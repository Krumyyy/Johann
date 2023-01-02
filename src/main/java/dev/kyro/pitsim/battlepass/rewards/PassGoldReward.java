package dev.kyro.pitsim.battlepass.rewards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.battlepass.PassReward;
import dev.kyro.pitsim.controllers.LevelManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PassGoldReward extends PassReward {
	public int gold;

	public PassGoldReward(int gold) {
		this.gold = gold;
	}

	@Override
	public boolean giveReward(PitPlayer pitPlayer) {
		LevelManager.addGold(pitPlayer.player, (int) (gold * getMultiplier(pitPlayer)));
		return true;
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		if(pitPlayer.prestige == 0) return 1;
		return pitPlayer.prestige;
	}

	@Override
	public ItemStack getDisplayItem(PitPlayer pitPlayer, boolean hasClaimed) {
		ItemStack itemStack = new AItemStackBuilder(Material.GOLD_INGOT)
				.setName("&6&lGold Reward")
				.setLore(new ALoreBuilder(
						"&7Reward: &6" + Misc.formatLarge((int) (gold * getMultiplier(pitPlayer))) + "g"
				)).getItemStack();
		return itemStack;
	}
}