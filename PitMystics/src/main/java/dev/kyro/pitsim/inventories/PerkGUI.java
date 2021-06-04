package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.builders.AInventoryBuilder;
import dev.kyro.arcticapi.gui.AInventoryGUI;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.PitPerk;
import dev.kyro.pitsim.controllers.PitPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class PerkGUI extends AInventoryGUI {

	public AInventoryBuilder builder;
	public Player player;
	public boolean inSubGUI = false;

	public PerkGUI(Player player) {
		super("Perk GUI", 6);
		this.player = player;

		builder = new AInventoryBuilder(baseGUI)
				.createBorder(Material.STAINED_GLASS_PANE, 8);

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		for(int i = 0; i < pitPlayer.pitPerks.length; i++) {
			PitPerk pitPerk = pitPlayer.pitPerks[i];
			if(pitPerk == null) continue;
			baseGUI.setItem(10 + i * 2, pitPerk.getDisplayItem());
		}
	}

	@Override
	public void onClick(InventoryClickEvent event) {

		int slot = event.getSlot();
		ItemStack clickedItem = event.getCurrentItem();
		if(event.getClickedInventory().getHolder() == this) {

			if(slot == 10 || slot == 12 || slot == 14 || slot == 16) {

				inSubGUI = true;
				player.openInventory(new ApplyPerkGUI(this, getPerkNum(slot)).getInventory());
				return;
			}
		}
		updateGUI();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

		inSubGUI = false;
		new BukkitRunnable() {
			@Override
			public void run() {
				updateGUI();
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);
	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}

	public void updateGUI() {

		for(int i = 0; i < baseGUI.getSize(); i++) {
			player.getOpenInventory().setItem(i, baseGUI.getItem(i));
		}
	}

	public int getSlot(int perkNum) {

		return perkNum * 2 + 8;
	}

	public int getPerkNum(int slot) {

		return (slot - 8) / 2;
	}

	public PitPerk getActivePerk(int perkNum) {

		return getActivePerks()[perkNum];
	}

	public PitPerk[] getActivePerks() {

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		return pitPlayer.pitPerks;
	}

	public void setPerk(PitPerk pitPerk, int perkNum) {

		getActivePerks()[perkNum] = pitPerk;
	}
}