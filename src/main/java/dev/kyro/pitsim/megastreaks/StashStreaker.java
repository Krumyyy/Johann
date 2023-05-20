package dev.kyro.pitsim.megastreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.battlepass.quests.daily.DailyMegastreakQuest;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StashStreaker extends Megastreak {
	public static StashStreaker INSTANCE;

	public StashStreaker() {
		super("&8Stash Streaker", "stashstreaker", 100, 13, 50);
		INSTANCE = this;
	}

	public static boolean isActive(PitPlayer pitPlayer) {
		if(pitPlayer == null) return false;
		return pitPlayer.getMegastreak() instanceof StashStreaker && pitPlayer.isOnMega();
	}

	@Override
	public void proc(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		Sounds.MEGA_GENERAL.play(player.getLocation());
		pitPlayer.stats.timesOnStashStreaker++;
		DailyMegastreakQuest.INSTANCE.onMegastreakComplete(pitPlayer);
	}

	@Override
	public void reset(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!pitPlayer.isOnMega()) return;

//		TODO: On death effect
	}

	@Override
	public String getPrefix(Player player) {
		return "&8&lSTASH";
	}

	@Override
	public ItemStack getBaseDisplayStack(Player player) {
		return new AItemStackBuilder(Material.CHAINMAIL_LEGGINGS)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, PitPlayer pitPlayer) {
		loreBuilder.addLore(
				"&7On Trigger:",
				"&a\u25a0 &7Gain access to /ec while not",
				"   &7in combat",
				"",
				"&7BUT:",
				"&c\u25a0 &7You cannot attack bots",
				"",
				"&7On Death:",
				"&e\u25a0 &7Protects your inventory"
		);
	}

	@Override
	public String getSummary() {
		return getCapsDisplayName() + "&7 is a Megastreak that";
	}
}
