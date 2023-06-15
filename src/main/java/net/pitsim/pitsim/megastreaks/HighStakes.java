package net.pitsim.pitsim.megastreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.pitsim.aitems.misc.GoldPickup;
import net.pitsim.pitsim.battlepass.quests.daily.DailyMegastreakQuest;
import net.pitsim.pitsim.controllers.DamageManager;
import net.pitsim.pitsim.controllers.ItemFactory;
import net.pitsim.pitsim.controllers.objects.Megastreak;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.events.KillEvent;
import net.pitsim.pitsim.misc.Formatter;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import net.pitsim.pitsim.misc.Sounds;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class HighStakes extends Megastreak {
	public static HighStakes INSTANCE;

	public HighStakes() {
		super("&2High Stakes", "highstakes", 50, 8, 50);
		INSTANCE = this;
	}

	public static void spawnIngot(Player player, double multiplier) {
		Location spawnLoc = player.getLocation().add(Misc.randomOffset(1), Misc.randomOffsetPositive(1), Misc.randomOffset(1));
		Item item = spawnLoc.getWorld().dropItem(spawnLoc, ItemFactory.getItem(GoldPickup.class).getItem(GoldPickup.getPickupGold()));
		item.setVelocity(new Vector(Misc.randomOffset(1), Misc.randomOffsetPositive(1.5), Misc.randomOffset(1)).multiply(multiplier));
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!hasMegastreak(killEvent.getKillerPlayer())) return;
		if(!killEvent.getKillerPitPlayer().isOnMega() || killEvent.getKillerPitPlayer().getKills() % getKillInterval() != 0) return;

		for(int i = 0; i < getIngotCount(); i++) spawnIngot(killEvent.getDeadPlayer(), 0.7);
	}

	@EventHandler
	public void kill(KillEvent killEvent) {
		if(!hasMegastreak(killEvent.getKillerPlayer())) return;
		PitPlayer pitPlayer = killEvent.getKillerPitPlayer();
		if(!pitPlayer.isOnMega()) return;
		killEvent.goldMultipliers.add(1 + (getGoldIncrease() / 100.0));
		killEvent.xpMultipliers.add(0.5);

		if(Math.random() > 1.0 / getChanceToKill()) return;
		for(int i = 0; i < getDeathIngotCount(); i++) spawnIngot(killEvent.getKillerPlayer(), 1);
		DamageManager.killPlayer(killEvent.getKillerPlayer());
	}

	@Override
	public void proc(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		Sounds.MEGA_GENERAL.play(player.getLocation());
		pitPlayer.stats.timesOnHighStakes++;
		DailyMegastreakQuest.INSTANCE.onMegastreakComplete(pitPlayer);
	}

	@Override
	public void reset(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!pitPlayer.isOnMega()) return;
	}

	@Override
	public String getPrefix(Player player) {
		return "&2&lSTAKE";
	}

	@Override
	public ItemStack getBaseDisplayStack(Player player) {
		return new AItemStackBuilder(Material.DIAMOND)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, PitPlayer pitPlayer) {
		loreBuilder.addLore(
				"&7On Trigger:",
				"&a\u25a0 &7Earn &6+" + getGoldIncrease() + "% gold &7from kills",
				"&a\u25a0 &7Every &c" + getKillInterval() + " kills&7, spawn",
				"   &6" + getIngotCount() + " gold ingots&7. Picking them up",
				"   &7grants &cRegen " + AUtil.toRoman(GoldPickup.getRegenAmplifier() + 1) +
						" &7(" + GoldPickup.getRegenSeconds() + "s) and &6" + GoldPickup.getPickupGold() + "g",
				"",
				"&7BUT:",
				"&c\u25a0 &7Have a &f1 &7in &f" + Formatter.commaFormat.format(getChanceToKill()) + " &7chance to &cDIE!",
				"   &7on each bot kill",
				"&c\u25a0 &7Earn &c-50% &7xp from kills",
				"",
				"&7On Death:",
				"&e\u25a0 &7If &cbad luck &7killed you, explode",
				"   &7into a bunch of &6gold ingots"
		);
	}

	@Override
	public String getSummary() {
		return getCapsDisplayName() + "&7 is a Megastreak that";
	}

	public static int getGoldIncrease() {
		return 75;
	}

	public static int getIngotCount() {
		return 5;
	}

	public static int getDeathIngotCount() {
		return 20;
	}

	public static int getKillInterval() {
		return 17;
	}

	public static int getChanceToKill() {
		return 1000;
	}
}