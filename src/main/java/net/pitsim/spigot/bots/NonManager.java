package net.pitsim.spigot.bots;

import net.pitsim.spigot.PitSim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class NonManager {

	public static final Location SPAWN_LOCATION = new Location(Bukkit.getWorld("world"), 0.5, 100, 0.5);
	public static final Location MID_LOCATION = new Location(Bukkit.getWorld("world"), 0.5, 82, 0.5);
	public static final int SPAWN_Y_LEVEL = 82;

	public static List<MinecraftSkin> botSkins = new ArrayList<>();
	public static boolean defaultNons = true;
//	public static List<String> skinLoadedBotIGNS = new ArrayList<>();
	public static List<Non> nons = new ArrayList<>();

	static {
		botSkins.add(MinecraftSkin.getSkin("KyroKrypt"));
		botSkins.add(MinecraftSkin.getSkin("BHunter"));
		botSkins.add(MinecraftSkin.getSkin("PayForTruce"));
		botSkins.add(MinecraftSkin.getSkin("Fishduper"));
		botSkins.add(MinecraftSkin.getSkin("wiji1"));
		botSkins.add(MinecraftSkin.getSkin("Muruseni"));
		botSkins.add(MinecraftSkin.getSkin("ObvEndyy"));
	}

	public static final int MAX_DISTANCE_FROM_MID = 10;

	public static void init() {
		new BukkitRunnable() {
			@Override
			public void run() {

				if(botSkins.isEmpty()) return;
				for(int i = 0; i < 3; i++) {
					if(nons.size() >= getMaxNons()) break;

					Non non = new Non(botSkins.get((int) (Math.random() * botSkins.size())));
					new BukkitRunnable() {
						@Override
						public void run() {
							non.remove();
						}
					}.runTaskLater(PitSim.INSTANCE, (long) (20 * 60 * (Math.random() * 10 + 5)));
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 40L, 3L);

		new BukkitRunnable() {
			@Override
			public void run() {
				for(Non non : nons) non.tick();
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	public static int getMaxNons() {
		int base = 30;
		int max = 50;

		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.hasPermission("pitsim.chaos")) {
				base = 60;
				max = 75;
			}
		}

		Location mid = NonManager.MID_LOCATION;
		int playersNearMid = 0;
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(onlinePlayer.getWorld() != NonManager.MID_LOCATION.getWorld()) continue;
			double distance = mid.distance(onlinePlayer.getLocation());
			if(distance < 15) playersNearMid++;
		}
		return Math.min(playersNearMid * 4 + base, max);
	}

	public static void updateNons(List<MinecraftSkin> newBotSkins) {
		MinecraftSkin kyro = MinecraftSkin.getSkin("KyroKrypt");
		if(!newBotSkins.contains(kyro)) newBotSkins.add(kyro);

		if(defaultNons) {
			defaultNons = false;
			botSkins.clear();
		}
//
		newLoop:
		for(MinecraftSkin newSkin : newBotSkins) {
			for(MinecraftSkin botSkin : new ArrayList<>(botSkins)) {
				if(botSkin.equals(newSkin)) continue newLoop;
			}
			botSkins.add(newSkin);
		}

		List<MinecraftSkin> remove = new ArrayList<>();
		newLoop:
		for(MinecraftSkin skin : botSkins) {
			for(MinecraftSkin newBotSkin : newBotSkins) {
				if(skin.equals(newBotSkin)) continue newLoop;
			}
			remove.add(skin);
		}
		remove.forEach(skin -> botSkins.remove(skin));
	}

	public static Non getNon(LivingEntity entity) {
		if(entity == null) return null;
		try {
			for(Non non : nons) {
				if(non == null) continue;
				if(non.non.getUniqueId().equals(entity.getUniqueId())) return non;
			}
		} catch(Exception exception) {
			return null;
		}
		return null;
	}


}
