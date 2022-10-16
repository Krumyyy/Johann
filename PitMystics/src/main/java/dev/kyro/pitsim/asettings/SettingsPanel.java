package dev.kyro.pitsim.asettings;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class SettingsPanel extends AGUIPanel {
	public SettingsGUI settingsGUI;

	public SettingsPanel(AGUI gui) {
		super(gui);
		settingsGUI = (SettingsGUI) gui;

		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 7);

		ItemStack cosmeticItem = new AItemStackBuilder(Material.RED_ROSE)
				.setName("&c&lCosmetics")
				.setLore(new ALoreBuilder(
						"&7Click to open the cosmetics menu"
				))
				.getItemStack();
		getInventory().setItem(10, cosmeticItem);
	}

	@Override
	public String getName() {
		return ChatColor.WHITE + "Settings";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;
		int slot = event.getSlot();

		if(slot == 10) {
			openPanel(settingsGUI.cosmeticPanel);
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}