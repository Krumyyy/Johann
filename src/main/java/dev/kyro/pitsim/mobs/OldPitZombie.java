package dev.kyro.pitsim.mobs;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.brewing.ingredients.RottenFlesh;
import dev.kyro.pitsim.adarkzone.aaold.OldMobManager;
import dev.kyro.pitsim.adarkzone.aaold.OldPitMob;
import dev.kyro.pitsim.enums.MobType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class OldPitZombie extends OldPitMob {
	public static OldPitZombie INSTANCE;

	public OldPitZombie(Location spawnLoc) {
		super(MobType.ZOMBIE, spawnLoc, 1, MobValues.zombieDamage, "&cZombie", MobValues.zombieSpeed);
		INSTANCE = this;
	}

	@Override
	public LivingEntity spawnMob(Location spawnLoc) {
		Zombie zombie = (Zombie) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.ZOMBIE);

		zombie.setMaxHealth(MobValues.zombieHealth);
		zombie.setHealth(MobValues.zombieHealth);
//		zombie.setCustomName(displayName);
		zombie.setCustomNameVisible(false);
		zombie.setRemoveWhenFarAway(false);
		zombie.setBaby(false);
		zombie.setVillager(false);
		zombie.setCanPickupItems(false);
		OldMobManager.makeTag(zombie, displayName);
		new BukkitRunnable() {
			@Override
			public void run() {
				zombie.getEquipment().clear();
			}
		}.runTaskLater(PitSim.INSTANCE, 2);

		return zombie;
	}

	@Override
	public Map<ItemStack, Integer> getDrops() {
		Map<ItemStack, Integer> drops = new HashMap<>();
		drops.put(RottenFlesh.INSTANCE.getItem(), 50);

		return drops;
	}
}
