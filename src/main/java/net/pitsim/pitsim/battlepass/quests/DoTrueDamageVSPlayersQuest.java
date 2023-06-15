package net.pitsim.pitsim.battlepass.quests;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.pitsim.battlepass.PassQuest;
import net.pitsim.pitsim.controllers.PlayerManager;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.events.AttackEvent;
import net.pitsim.pitsim.misc.Formatter;
import net.pitsim.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

public class DoTrueDamageVSPlayersQuest extends PassQuest {

	public DoTrueDamageVSPlayersQuest() {
		super("&9&lWhen Players Fly", "truedamageplayers", QuestType.WEEKLY);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!PlayerManager.isRealPlayer(attackEvent.getAttackerPlayer()) || !canProgressQuest(attackEvent.getAttackerPitPlayer())
				|| !PlayerManager.isRealPlayer(attackEvent.getDefenderPlayer()) || attackEvent.getDefenderPlayer().isOnGround())
			return;
		progressQuest(attackEvent.getAttackerPitPlayer(), attackEvent.trueDamage);
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.RED_ROSE, 1, 1)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Deal &9" + Misc.getHearts(questLevel.getRequirement(pitPlayer)) + " &7of true damage to",
						"&7other players that are not",
						"&7on the ground",
						"",
						"&7Progress: &3" + Formatter.formatLarge(progress / 2) + "&7/&3" + Formatter.formatLarge(questLevel.getRequirement(pitPlayer) / 2) + " &8[" +
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
	public void createPossibleStates() {
		questLevels.add(new QuestLevel(3_000.0, 100));
		questLevels.add(new QuestLevel(4_500.0, 150));
		questLevels.add(new QuestLevel(6_000.0, 200));
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}