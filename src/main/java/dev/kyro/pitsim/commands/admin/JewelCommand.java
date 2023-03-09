package dev.kyro.pitsim.commands.admin;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.commands.ACommand;
import dev.kyro.arcticapi.commands.AMultiCommand;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.aitems.MysticFactory;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.TemporaryItem;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.misc.Constant;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class JewelCommand extends ACommand {
	public JewelCommand(AMultiCommand base, String executor) {
		super(base, executor);
	}

	@Override
	public void execute(CommandSender sender, Command command, String alias, List<String> args) {
		if(!(sender instanceof Player)) return;
		if(args.size() < 1) {
			AOutput.error(sender, "Usage: /jewel <sword|bow|pants> [enchant] [max-lives]");
			return;
		}

		MysticType mysticType = null;
		String type = args.get(0).toLowerCase();
		switch(type) {
			case "sword":
				mysticType = MysticType.SWORD;
				break;
			case "bow":
				mysticType = MysticType.BOW;
				break;
			case "pants":
			case "pant":
			case "fresh":
				mysticType = MysticType.PANTS;
				break;
		}
		if(mysticType == null) {

			AOutput.error(sender, "Usage: /fresh <sword|bow|pants>");
			return;
		}

		AUtil.giveItemSafely((Player) sender, getJewel(mysticType, args.get(1), Integer.parseInt(args.get(2))));
	}

	public static ItemStack getJewel(MysticType mysticType, String pitEnchant, int maxLives) {
		ItemStack jewel = MysticFactory.getJewelItem(mysticType);
		NBTItem nbtItem = new NBTItem(jewel);

		PitItem pitItem = ItemFactory.getItem(jewel);
		assert pitItem != null;
		TemporaryItem temporaryItem = pitItem.getAsTemporaryItem();

		if(pitEnchant != null) {
			nbtItem.setInteger(NBTTag.JEWEL_KILLS.getRef(), Constant.JEWEL_KILLS);

			PitEnchant jewelEnchant = EnchantManager.getEnchant(pitEnchant);
			if(maxLives <= 0) maxLives = EnchantManager.getRandomMaxLives();

			nbtItem = new NBTItem(PantColor.setPantColor(nbtItem.getItem(), PantColor.getNormalRandom()));
			try {
				jewel = EnchantManager.addEnchant(nbtItem.getItem(), jewelEnchant, 3, false, true, -1);
			} catch(Exception ignored) {}
			jewel = temporaryItem.addMaxLives(jewel, maxLives);
		}

		pitItem.updateItem(jewel);
		return jewel;
	}

	@Override
	public List<String> getTabComplete(Player player, String current, List<String> args) {
		return null;
	}
}