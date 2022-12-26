package dev.kyro.pitsim.battlepass.quests;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.battlepass.PassQuest;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GrindGoldQuest extends PassQuest {
	public static GrindGoldQuest INSTANCE;

	public GrindGoldQuest() {
		super("&6&lRich", "grindgold", QuestType.WEEKLY);
		INSTANCE = this;
	}

	public void gainGold(PitPlayer pitPlayer, long gold) {
		progressQuest(pitPlayer, (double) gold);
	}

	@Override
	public ItemStack getDisplayItem(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.GOLD_INGOT)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Grind &6" + Misc.formatLarge(questLevel.getRequirement(pitPlayer)) + "g",
						"",
						"&7Progress: &3" + Misc.formatLarge(progress) + "&7/&3" + Misc.formatLarge(questLevel.getRequirement(pitPlayer)) + " &8[" +
								AUtil.createProgressBar("|", ChatColor.AQUA, ChatColor.GRAY, 20, progress / questLevel.getRequirement(pitPlayer)) + "&8]",
						"&7Reward: &3" + questLevel.rewardPoints + " &7Quest Points"
				))
				.getItemStack();
		return itemStack;
	}

	@Override
	public QuestLevel getDailyState() {
		return null;
	}

	@Override
	public void createWeeklyPossibleStates() {
		questLevels.add(new QuestLevel(0.75, 100));
		questLevels.add(new QuestLevel(1, 150));
		questLevels.add(new QuestLevel(1.25, 200));
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return PrestigeValues.getPrestigeInfo(pitPlayer.prestige).goldReq;
	}
}
