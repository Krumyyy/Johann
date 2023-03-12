package dev.kyro.pitsim.adarkzone.altar;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.PitEntityType;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AltarVoucherReward {

	public Player player;
	public int amount;
	public List<BukkitTask> bukkitTaskList = new ArrayList<>();
	public List<EntityItem> items = new ArrayList<>();

	public AltarVoucherReward(Player player, int amount) {
		this.player = player;
		this.amount = amount;
	}

	public void spawn(Location location) {
		Random random = new Random();
		World world = ((CraftWorld) location.getWorld()).getHandle();

		for(int i = 0; i < amount; i++) {
			double offsetX = (random.nextInt(20) - 10) * 0.1;
			double offsetZ = (random.nextInt(20) - 10) * 0.1;

			EntityItem entityItem = new EntityItem(world);
			Location spawnLocation = new Location(location.getWorld(), location.getX() + offsetX, location.getY(), location.getZ() + offsetZ);
			entityItem.setPosition(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ());
			entityItem.setItemStack(CraftItemStack.asNMSCopy(new ItemStack(Material.EMPTY_MAP)));
			items.add(entityItem);

			PacketPlayOutSpawnEntity spawn = new PacketPlayOutSpawnEntity(entityItem, 2, 395);
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(spawn);

			PacketPlayOutEntityMetadata meta = new PacketPlayOutEntityMetadata(entityItem.getId(), entityItem.getDataWatcher(), true);
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(meta);


			bukkitTaskList.add(new BukkitRunnable() {
				@Override
				public void run() {
					for(Entity entity : spawnLocation.getWorld().getNearbyEntities(spawnLocation, 1, 1, 1)) {
						if(!Misc.isEntity(entity, PitEntityType.REAL_PLAYER)) continue;
						if(entity.getUniqueId() != player.getUniqueId()) continue;

						PacketPlayOutCollect packet = new PacketPlayOutCollect(entityItem.getId(), player.getEntityId());
						((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

						Sounds.ITEM_PICKUP.play(player);

						reward();
						cancel();
					}
				}
			}.runTaskTimer(PitSim.INSTANCE, 0, 5));
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				for(EntityItem item : items) despawn(item);
				for(BukkitTask task : bukkitTaskList) task.cancel();
				reward();
			}
		}.runTaskLater(PitSim.INSTANCE, 30 * 20);
	}

	public void despawn(EntityItem item) {
		PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(item.getId());
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroy);
	}

	public void reward() {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer == null) return;
		pitPlayer.renown += 1;

		AOutput.send(player, "&4&lALTAR! &7Gained &4+1 Demonic Voucher");
	}

}