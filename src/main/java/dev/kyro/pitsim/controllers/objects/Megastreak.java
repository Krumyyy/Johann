package dev.kyro.pitsim.controllers.objects;

import com.google.cloud.firestore.annotation.Exclude;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.upgrades.TheWay;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class Megastreak implements Listener {
	public PitPlayer pitPlayer;

	public Megastreak() {
	}

	public Megastreak(PitPlayer pitPlayer) {
		this.pitPlayer = pitPlayer;
		Bukkit.getServer().getPluginManager().registerEvents(this, PitSim.INSTANCE);
	}

	public abstract String getName();

	public abstract String getRawName();

	public abstract String getPrefix();

	public abstract List<String> getRefNames();

	public abstract int getRequiredKills();

	public abstract ItemStack guiItem();

	public abstract int guiSlot();

	public abstract int prestigeReq();

	public abstract int initialLevelReq();

	public abstract void stop();

	public abstract void proc();

	public abstract void reset();

	public void kill() {}

	@Exclude
	public boolean isOnMega() {
		return pitPlayer.getKills() >= getRequiredKills();
	}

	@Exclude
	public boolean playerIsOnMega(KillEvent killEvent) {
		if(!killEvent.isKillerPlayer()) return false;
		return killEvent.getKillerPitPlayer().getKills() >= getRequiredKills();
	}

	public int getFinalLevelReq(Player player) {
		return Math.max(initialLevelReq() - TheWay.INSTANCE.getLevelReduction(player), 0);
	}
}