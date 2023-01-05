package dev.kyro.pitsim.misc;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.GuildIntegrationManager;
import dev.kyro.pitsim.controllers.ItemManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class FunkyFeather {
	public static void giveFeather(Player player, int amount) {
		ItemStack feather = new ItemStack(Material.FEATHER);
		ItemMeta meta = feather.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_AQUA + "Funky Feather");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.YELLOW + "Special item");
		lore.add(ChatColor.GRAY + "protects your inventory but");
		lore.add(ChatColor.GRAY + "gets consumed on death if");
		lore.add(ChatColor.GRAY + "in your hotbar.");
		meta.setLore(lore);
		feather.setItemMeta(meta);
		feather.setAmount(amount);

		feather = ItemManager.enableDropConfirm(feather);

		NBTItem nbtItem = new NBTItem(feather);
		nbtItem.setBoolean(NBTTag.IS_FEATHER.getRef(), true);
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(onlinePlayer == player) {
				new BukkitRunnable() {
					@Override
					public void run() {
						AUtil.giveItemSafely(player, nbtItem.getItem(), true);
					}
				}.runTaskLater(PitSim.INSTANCE, 10L);
			}
		}
	}

	public static ItemStack getFeather(int amount) {
		ItemStack feather = new ItemStack(Material.FEATHER);
		ItemMeta meta = feather.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_AQUA + "Funky Feather");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.YELLOW + "Special item");
		lore.add(ChatColor.GRAY + "protects your inventory but");
		lore.add(ChatColor.GRAY + "gets consumed on death if");
		lore.add(ChatColor.GRAY + "in your hotbar.");
		meta.setLore(lore);
		feather.setItemMeta(meta);
		feather.setAmount(amount);

		feather = ItemManager.enableDropConfirm(feather);

		NBTItem nbtItem = new NBTItem(feather);
		nbtItem.setBoolean(NBTTag.IS_FEATHER.getRef(), true);
		return nbtItem.getItem();
	}

	public static boolean useFeather(LivingEntity killer, Player dead, boolean isDivine) {
		if(isDivine) return false;

		for(int i = 0; i < 9; i++) {
			ItemStack itemStack = dead.getInventory().getItem(i);
			if(Misc.isAirOrNull(itemStack)) continue;
			NBTItem nbtItem = new NBTItem(itemStack);
			if(nbtItem.hasKey(NBTTag.IS_FEATHER.getRef())) {
				AOutput.send(dead, "&3&lFUNKY FEATHER!&7 Inventory protected.");
				if(itemStack.getAmount() > 1) itemStack.setAmount(itemStack.getAmount() - 1);
				else dead.getInventory().setItem(i, null);
				Sounds.FUNKY_FEATHER.play(dead);

				GuildIntegrationManager.handleFeather(killer, dead);

				PitPlayer pitPlayer = PitPlayer.getPitPlayer(dead);
				if(pitPlayer.stats != null) pitPlayer.stats.feathersLost++;
				return true;
			}
		}
		return false;
	}
}
