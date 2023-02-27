package dev.kyro.pitsim.enums;

import dev.kyro.pitsim.aitems.*;
import dev.kyro.pitsim.aitems.misc.AncientGemShard;
import dev.kyro.pitsim.aitems.misc.ChunkOfVile;
import dev.kyro.pitsim.aitems.misc.CorruptedFeather;
import dev.kyro.pitsim.aitems.misc.FunkyFeather;
import dev.kyro.pitsim.commands.admin.JewelCommand;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public enum ItemType {
	FEATHERS_3(1, ItemFactory.getItem(FunkyFeather.class).getItem(3), ChatColor.DARK_AQUA + "3x Funky Feather", 50, 10),
	FEATHERS_5(2, ItemFactory.getItem(FunkyFeather.class).getItem(5), ChatColor.DARK_AQUA + "5x Funky Feather", 25, 25),
	VILE_3(3, ItemFactory.getItem(ChunkOfVile.class).getItem(3), ChatColor.DARK_PURPLE + "3x Chunk of Vile", 50, 10),
	VILE_5(4, ItemFactory.getItem(ChunkOfVile.class).getItem(5), ChatColor.DARK_PURPLE + "5x Chunk of Vile", 25, 25),
	COMP_JEWEL_SWORD(5, MysticFactory.getFreshItem(MysticType.SWORD, PantColor.BLUE), ChatColor.YELLOW + "Completed Hidden Jewel Sword", 25, 25),
	COMP_JEWEL_BOW(6, MysticFactory.getFreshItem(MysticType.BOW, PantColor.BLUE), ChatColor.AQUA + "Completed Hidden Jewel Bow", 25, 25),
	COMP_JEWEL_PANTS(7, MysticFactory.getFreshItem(MysticType.PANTS, PantColor.JEWEL), ChatColor.DARK_AQUA + "Completed Hidden Jewel Pants", 25, 25),
	JEWEL_SWORD(8, JewelCommand.getJewel(MysticType.SWORD, null, 0), ChatColor.YELLOW + "Hidden Jewel Sword", 25, 25),
	JEWEL_BOW(9, JewelCommand.getJewel(MysticType.BOW, null, 0), ChatColor.AQUA + "Hidden Jewel Bow", 25, 25),
	JEWEL_PANTS(10, JewelCommand.getJewel(MysticType.PANTS, null, 0), ChatColor.DARK_AQUA + "Hidden Jewel Pants", 25, 25),
	GEM_SHARD_10(11, ItemFactory.getItem(AncientGemShard.class).getItem(5), ChatColor.GREEN + "5x Ancient Gem Shard", 10, 50),
	GEM_SHARD_25(12, ItemFactory.getItem(AncientGemShard.class).getItem(10), ChatColor.GREEN + "10x Ancient Gem Shard", 5, 100),
	CORRUPTED_FEATHERS_3(13, ItemFactory.getItem(CorruptedFeather.class).getItem(3), ChatColor.DARK_PURPLE + "3x Corrupted Feather", 50, 10),
	CORRUPTED_FEATHERS_5(14, ItemFactory.getItem(CorruptedFeather.class).getItem(5), ChatColor.DARK_PURPLE + "5x Corrupted Feather", 25, 25);

	public final int id;
	public final ItemStack item;
	public final String itemName;
	public final double chance;
	public final int startingBid;

	ItemType(int id, ItemStack item, String itemName, double chance, int startingBid) {
		this.id = id;
		this.item = item;
		this.itemName = itemName;
		this.chance = chance;
		this.startingBid = startingBid;
	}

	public static ItemType getItemType(int id) {
		for(ItemType itemType : values()) {
			if(itemType.id == id) return itemType;
		}
		return null;
	}

	public static int generateJewelData(ItemStack item) {
		MysticType mysticType = MysticType.getMysticType(item);
		if(mysticType == null) return 0;

		return new Random().nextInt(EnchantManager.getEnchants(mysticType).size() - 1);
	}

	public static String jewelDataToEnchant(MysticType mysticType, int data) {
		if(mysticType == null) return null;

		return EnchantManager.getEnchants(mysticType).get(data).refNames.get(0);
	}

	public static ItemStack getJewelItem(int id, int data) {
		MysticType mysticType = null;

		switch(id) {
			case 5:
				mysticType = MysticType.SWORD;
				break;
			case 6:
				mysticType = MysticType.BOW;
				break;
			case 7:
				mysticType = MysticType.PANTS;
				break;
		}

		return JewelCommand.getJewel(mysticType, jewelDataToEnchant(mysticType, data), 0);
	}
}
