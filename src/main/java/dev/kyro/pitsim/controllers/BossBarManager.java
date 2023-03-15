package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.BossManager;
import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.adarkzone.SubLevel;
import dev.kyro.pitsim.controllers.objects.PitBossBar;
import dev.kyro.pitsim.enums.PitEntityType;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BossBarManager {

	public static List<UUID> overriddenPlayers = new ArrayList<>();

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				BossManager.pitBosses.forEach(boss -> {
					List<Player> players = new ArrayList<>();
					SubLevel subLevel = boss.getSubLevel();
					int radius = subLevel.spawnRadius;
					for(Entity entity : MapManager.getDarkzone().getNearbyEntities(subLevel.getMiddle(), radius, radius, radius)) {
						if(!Misc.isEntity(entity, PitEntityType.REAL_PLAYER)) continue;
						if(overriddenPlayers.contains(entity.getUniqueId())) continue;
						players.add((Player) entity);
					}

					boss.bossBar.updatePlayers(players);
				});
			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 20);
	}

	public static void init() {

	}

	public static void showBossBar(Player player, PitBossBar bossBar, int ticks) {
		List<Player> players = new ArrayList<>(bossBar.players);
		players.add(player);
		bossBar.updatePlayers(players);
		overriddenPlayers.add(player.getUniqueId());

		new BukkitRunnable() {
			@Override
			public void run() {
				List<Player> updatedPlayers = new ArrayList<>(bossBar.players);
				updatedPlayers.remove(player);
				bossBar.updatePlayers(updatedPlayers);
				overriddenPlayers.remove(player.getUniqueId());
			}
		}.runTaskLater(PitSim.INSTANCE, ticks);
	}
}