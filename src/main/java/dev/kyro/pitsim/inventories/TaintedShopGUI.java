package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class TaintedShopGUI extends AGUI {

	ShopHomePanel homePanel;
	TaintedShredPanel shredPanel;
	TaintedShopPanel shopPanel;

	public TaintedShopGUI(Player player) {
		super(player);

		homePanel = new ShopHomePanel(this);
		shredPanel = new TaintedShredPanel(this);
		shopPanel = new TaintedShopPanel(this);

		setHomePanel(homePanel);
	}
}
