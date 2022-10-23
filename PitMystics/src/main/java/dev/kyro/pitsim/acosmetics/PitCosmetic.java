package dev.kyro.pitsim.acosmetics;

import dev.kyro.pitsim.ParticleColor;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public abstract class PitCosmetic implements Listener {
	private String displayName;
	public String refName;
	public CosmeticType cosmeticType;

	public boolean isColorCosmetic;
	public boolean accountForYaw = true;
	public boolean accountForPitch = true;
	public boolean isPermissionRequired = false;

	public boolean preventKillSound = false;
	public final int SOUND_RANGE = 10;
	public final int MID_RANGE = 10;

	public Map<UUID, BukkitTask> runnableMap = new HashMap<>();

	public PitCosmetic(String displayName, String refName, CosmeticType cosmeticType) {
		this.displayName = displayName;
		this.refName = refName;
		this.cosmeticType = cosmeticType;
		this.isColorCosmetic = this instanceof ColorableCosmetic;

		Bukkit.getServer().getPluginManager().registerEvents(this, PitSim.INSTANCE);
	}

	public abstract ItemStack getRawDisplayItem();

	public void onEnable(PitPlayer pitPlayer) {}
	public void onDisable(PitPlayer pitPlayer) {}

	public void enable(PitPlayer pitPlayer) {
		loadColor(pitPlayer);
		onEnable(pitPlayer);
		CosmeticManager.sendEnableMessage(pitPlayer, this, getColor(pitPlayer));
	}

	public void disable(PitPlayer pitPlayer) {
		onDisable(pitPlayer);
		CosmeticManager.sendDisableMessage(pitPlayer, this);
	}

	public void loadColor(PitPlayer pitPlayer) {
		if(!(this instanceof ColorableCosmetic)) return;
		ColorableCosmetic colorableCosmetic = (ColorableCosmetic) this;
		colorableCosmetic.setParticleColor(pitPlayer.player, getColor(pitPlayer));
	}

	public ItemStack getDisplayItem(boolean equipped) {
		ItemStack itemStack = getRawDisplayItem();
		if(equipped) Misc.addEnchantGlint(itemStack);
		return itemStack;
	}

	private boolean hasPermission(PitPlayer pitPlayer, ParticleColor particleColor) {
		String permission = "pitsim.cosmetic." + cosmeticType.refName + "." + refName;
		if(particleColor == null) {
			for(ParticleColor testColor : ParticleColor.values()) {
				if(pitPlayer.player.hasPermission(permission + "." +
						testColor.name().toLowerCase().replaceAll("_", ""))) return true;
			}
			return false;
		}
		permission += "." + particleColor.name().toLowerCase().replaceAll("_", "");
		return pitPlayer.player.hasPermission(permission);
	}

	public boolean isUnlocked(PitPlayer pitPlayer) {
		return isUnlocked(pitPlayer, null);
	}

	public boolean isUnlocked(PitPlayer pitPlayer, ParticleColor particleColor) {
//		TODO: Probably should remove this later (temporary) maybe change to is op
		if(true) return true;
		if(isPermissionRequired) return hasPermission(pitPlayer, particleColor);
		PitPlayer.UnlockedCosmeticData unlockedCosmeticData = pitPlayer.unlockedCosmeticsMap.get(refName);
		if(unlockedCosmeticData == null) return false;
		if(isColorCosmetic && particleColor != null) {
			return unlockedCosmeticData.unlockedColors.contains(particleColor);
		}
		return true;
	}

	public List<ParticleColor> getUnlockedColors(PitPlayer pitPlayer) {
		List<ParticleColor> particleColors = new ArrayList<>();
		if(!isUnlocked(pitPlayer)) return particleColors;

//		TODO: Probably should remove this later (temporary)
		if(true) {
			particleColors.addAll(Arrays.asList(ParticleColor.values()));
			return particleColors;
		}

		if(isPermissionRequired) {
			for(ParticleColor particleColor : ParticleColor.values()) {
				if(hasPermission(pitPlayer, particleColor)) particleColors.add(particleColor);
			}
			return particleColors;
		}

		List<ParticleColor> unorderedColors = pitPlayer.unlockedCosmeticsMap.get(refName).unlockedColors;
		for(ParticleColor particleColor : ParticleColor.values()) {
			if(unorderedColors.contains(particleColor)) particleColors.add(particleColor);
		}
		return particleColors;
	}

//	Returns true if color cosmetic if any color is equipped
	public boolean isEnabled(PitPlayer pitPlayer) {
		if(pitPlayer == null || !isUnlocked(pitPlayer)) return false;
		if(!pitPlayer.equippedCosmeticMap.containsKey(cosmeticType.name())) return false;
		PitPlayer.EquippedCosmeticData cosmeticData = pitPlayer.equippedCosmeticMap.get(cosmeticType.name());
		if(cosmeticData == null) return false;
		return cosmeticData.refName.equals(refName);
	}

	public boolean isEnabled(PitPlayer pitPlayer, ParticleColor particleColor) {
		if(!isEnabled(pitPlayer) || !isUnlocked(pitPlayer, particleColor)) return false;
		PitPlayer.EquippedCosmeticData cosmeticData = pitPlayer.equippedCosmeticMap.get(cosmeticType.name());
		return cosmeticData.particleColor == particleColor;
	}

	public ParticleColor getColor(PitPlayer pitPlayer) {
		if(!isEnabled(pitPlayer)) return null;
		PitPlayer.EquippedCosmeticData cosmeticData = pitPlayer.equippedCosmeticMap.get(cosmeticType.name());
		if(cosmeticData == null) return null;
		return cosmeticData.particleColor;
	}

	public String getDisplayName() {
		return ChatColor.translateAlternateColorCodes('&', displayName);
	}

	public double random(double variance) {
		return Math.random() * variance - variance / 2;
	}

	public String getBountyClaimMessage(String killerName, String deadName, String bounty) {
		return null;
	}

	public boolean nearMid(Player player) {
		return !MapManager.inDarkzone(player) && MapManager.currentMap.getMid(player.getWorld()).distance(player.getLocation()) < MID_RANGE;
	}
}
