package net.pitsim.pitsim.adarkzone.mobs;

import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.adarkzone.*;
import net.pitsim.pitsim.adarkzone.notdarkzone.PitEquipment;
import net.pitsim.pitsim.aitems.mobdrops.Charcoal;
import net.pitsim.pitsim.controllers.ItemFactory;
import net.pitsim.pitsim.enums.MobStatus;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Skeleton;
import org.bukkit.scheduler.BukkitRunnable;

public class PitWitherSkeleton extends PitMob {

	public PitWitherSkeleton(Location spawnLocation, MobStatus mobStatus) {
		super(spawnLocation, mobStatus);
	}

	@Override
	public SubLevelType getSubLevelType() {
		return SubLevelType.WITHER_SKELETON;
	}

	@Override
	public Creature createMob(Location spawnLocation) {
		Skeleton witherSkeleton = spawnLocation.getWorld().spawn(spawnLocation, Skeleton.class);
		witherSkeleton.setCustomNameVisible(false);
		witherSkeleton.setRemoveWhenFarAway(false);
		witherSkeleton.setCanPickupItems(false);

		witherSkeleton.setSkeletonType(Skeleton.SkeletonType.WITHER);

		new BukkitRunnable() {
			@Override
			public void run() {
				new PitEquipment().setEquipment(witherSkeleton);
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);

		return witherSkeleton;
	}

	@Override
	public String getRawDisplayName() {
		return isMinion() ? "Minion Wither Skeleton" : "Wither Skeleton";
	}

	@Override
	public ChatColor getChatColor() {
		return ChatColor.DARK_GRAY;
	}

	@Override
	public int getMaxHealth() {
		return DarkzoneBalancing.getAttributeAsInt(getSubLevelType(), DarkzoneBalancing.Attribute.MOB_HEALTH);
	}

	@Override
	public double getDamage() {
		return DarkzoneBalancing.getAttribute(getSubLevelType(), DarkzoneBalancing.Attribute.MOB_DAMAGE);
	}

	@Override
	public int getSpeedAmplifier() {
		return 1;
	}

	@Override
	public int getDroppedSouls() {
		return DarkzoneBalancing.getAttributeAsRandomInt(getSubLevelType(), DarkzoneBalancing.Attribute.MOB_SOULS);
	}

	@Override
	public DropPool createDropPool() {
		return new DropPool()
				.addRareItem(() -> ItemFactory.getItem(Charcoal.class).getItem(), DarkzoneBalancing.MOB_ITEM_DROP_PERCENT);
	}

	@Override
	public PitNameTag createNameTag() {
		return new PitNameTag(this, PitNameTag.NameTagType.NAME_AND_HEALTH)
				.addMob(PitNameTag.RidingType.SMALL_MAGMA_CUBE);
	}
}