package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enchants.PitBlob;
import dev.kyro.pitsim.enchants.Regularity;
import dev.kyro.pitsim.enchants.Telebow;
import dev.kyro.pitsim.enchants.WolfPack;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.NonTrait;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.FunkyFeather;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.ProtArmor;
import dev.kyro.pitsim.perks.AssistantToTheStreaker;
import dev.kyro.pitsim.pitevents.CaptureTheFlag;
import dev.kyro.pitsim.upgrades.DivineIntervention;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.DecimalFormat;
import java.util.*;

public class DamageManager implements Listener {

	public static List<Player> hitCooldownList = new ArrayList<>();
	public static List<Player> nonHitCooldownList = new ArrayList<>();
	public static Map<EntityShootBowEvent, Map<PitEnchant, Integer>> arrowMap = new HashMap<>();

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				List<EntityShootBowEvent> toRemove = new ArrayList<>();
				for(Map.Entry<EntityShootBowEvent, Map<PitEnchant, Integer>> entry : arrowMap.entrySet()) {

					if(entry.getKey().getProjectile().isDead()) toRemove.add(entry.getKey());
				}
				for(EntityShootBowEvent remove : toRemove) {
					arrowMap.remove(remove);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBowShoot(EntityShootBowEvent event) {

		if(!(event.getEntity() instanceof Player) || !(event.getProjectile() instanceof Arrow)) return;
		Player shooter = (Player) event.getEntity();
		Arrow arrow = (Arrow) event.getProjectile();
		arrowMap.put(event, EnchantManager.getEnchantsOnPlayer(shooter));
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onAttack(EntityDamageByEntityEvent event) {

		if(!(event.getEntity() instanceof Player)) return;
		Player attacker = getAttacker(event.getDamager());
		Player defender = (Player) event.getEntity();

		Map<PitEnchant, Integer> defenderEnchantMap = EnchantManager.getEnchantsOnPlayer(defender);
		boolean fakeHit = false;

		Non attackingNon = NonManager.getNon(attacker);
		Non defendingNon = NonManager.getNon(defender);
//		Hit on non or by non
		if((attackingNon != null && nonHitCooldownList.contains(defender)) ||
				(attackingNon == null && defendingNon != null && hitCooldownList.contains(defender)) && !Regularity.toReg.contains(defender.getUniqueId())) {
			event.setCancelled(true);
			return;
		}
//		Regular player to player hit
		if(attackingNon == null && !Regularity.toReg.contains(defender.getUniqueId())) {
			fakeHit = hitCooldownList.contains(defender);
		}

		if(Regularity.regCooldown.contains(defender.getUniqueId()) && !Regularity.toReg.contains(defender.getUniqueId())) {
			event.setCancelled(true);
			return;
		}

		if(!fakeHit) {
//			if(attackingNon == null) attacker.setHealth(Math.min(attacker.getHealth() + 1, attacker.getMaxHealth()));
			hitCooldownList.add(defender);
			nonHitCooldownList.add(defender);
			new BukkitRunnable() {
				int count = 0;

				@Override
				public void run() {
					if(++count == 15) cancel();

					if(count == 8) DamageManager.hitCooldownList.remove(defender);
					if(count == 15) DamageManager.nonHitCooldownList.remove(defender);
				}
			}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
		}

		if(attackingNon != null) {
			event.setDamage(attackingNon.traits.contains(NonTrait.IRON_STREAKER) ? 9 : 6);
			if(Misc.isCritical(attacker)) event.setDamage(event.getDamage() * 1.5);
		}

		AttackEvent.Pre preEvent = null;
		if(event.getDamager() instanceof Player) {

			preEvent = new AttackEvent.Pre(event, EnchantManager.getEnchantsOnPlayer(attacker), defenderEnchantMap, fakeHit);
		} else if(event.getDamager() instanceof Arrow) {

			for(Map.Entry<EntityShootBowEvent, Map<PitEnchant, Integer>> entry : arrowMap.entrySet()) {

				if(!entry.getKey().getProjectile().equals(event.getDamager())) continue;
				preEvent = new AttackEvent.Pre(event, arrowMap.get(entry.getKey()), defenderEnchantMap, fakeHit);
			}
		} else if(event.getDamager() instanceof Slime) {

			preEvent = new AttackEvent.Pre(event, new HashMap<>(), defenderEnchantMap, fakeHit);
		} else if(event.getDamager() instanceof Wolf) {

			preEvent = new AttackEvent.Pre(event, new HashMap<>(), defenderEnchantMap, fakeHit);
		}
		if(preEvent == null) return;

		Bukkit.getServer().getPluginManager().callEvent(preEvent);
		if(preEvent.isCancelled()) {
			event.setCancelled(true);
			return;
		}
		AttackEvent.Apply applyEvent = new AttackEvent.Apply(preEvent);
		Bukkit.getServer().getPluginManager().callEvent(applyEvent);
//		if(applyEvent.fakeHit) {
//			applyEvent.event.setDamage(0);
//			return;
//		}
		handleAttack(applyEvent);
		Bukkit.getServer().getPluginManager().callEvent(new AttackEvent.Post(applyEvent));
	}

	public static void handleAttack(AttackEvent.Apply attackEvent) {
//		AOutput.send(attackEvent.attacker, "Initial Damage: " + attackEvent.event.getDamage());

//		As strong as iron
		if(attackEvent.defender.getInventory().getLeggings() != null && attackEvent.defender.getInventory().getLeggings().getType() == Material.LEATHER_LEGGINGS) {
			NBTItem pants = new NBTItem(attackEvent.defender.getInventory().getLeggings());
			if(pants.hasKey(NBTTag.ITEM_UUID.getRef())) {
				attackEvent.multiplier.add(0.86956521);
			}
		}

		double damage = attackEvent.getFinalDamage();
		attackEvent.event.setDamage(damage);

		if(attackEvent.trueDamage != 0 || attackEvent.veryTrueDamage != 0) {
			double finalHealth = attackEvent.defender.getHealth() - attackEvent.trueDamage - attackEvent.veryTrueDamage;
			if(finalHealth <= 0) {
				attackEvent.event.setCancelled(true);
				kill(attackEvent, attackEvent.attacker, attackEvent.defender, false);
				return;
			} else {
				attackEvent.defender.setHealth(Math.max(finalHealth, 0));
			}
		}

		if(attackEvent.selfTrueDamage != 0 || attackEvent.selfVeryTrueDamage != 0) {
			double finalHealth = attackEvent.attacker.getHealth() - attackEvent.selfTrueDamage - attackEvent.selfVeryTrueDamage;
			if(finalHealth <= 0) {
				attackEvent.event.setCancelled(true);
				kill(attackEvent, attackEvent.defender, attackEvent.attacker, false);
				return;
			} else {
				attackEvent.attacker.setHealth(Math.max(finalHealth, 0));
				attackEvent.attacker.damage(0);
			}
		}

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.defender);
		pitPlayer.addDamage(attackEvent.attacker, attackEvent.event.getFinalDamage() + attackEvent.trueDamage);

//		AOutput.send(attackEvent.attacker, "Final Damage: " + attackEvent.event.getDamage());
//		AOutput.send(attackEvent.attacker, "Final Damage: " + attackEvent.event.getFinalDamage());

		if(attackEvent.event.getFinalDamage() >= attackEvent.defender.getHealth()) {

			attackEvent.event.setCancelled(true);
			kill(attackEvent, attackEvent.attacker, attackEvent.defender, false);
		} else if(attackEvent.event.getFinalDamage() + attackEvent.executeUnder >= attackEvent.defender.getHealth()) {

			attackEvent.event.setCancelled(true);
			kill(attackEvent, attackEvent.attacker, attackEvent.defender, true);
		}

		DamageIndicator.onAttack(attackEvent);
	}

	public static Player getAttacker(Entity damager) {

		if(damager instanceof Player) return (Player) damager;
		if(damager instanceof Arrow) return (Player) ((Arrow) damager).getShooter();
		if(damager instanceof Slime) return PitBlob.getOwner((Slime) damager);
		if(damager instanceof Wolf) return WolfPack.getOwner((Wolf) damager);

		return null;
	}

	public static void kill(AttackEvent attackEvent, Player killer, Player dead, boolean exeDeath) {

		KillEvent killEvent = new KillEvent(attackEvent, killer, dead, exeDeath);
		Bukkit.getServer().getPluginManager().callEvent(killEvent);

		EnchantManager.incrementKills(killer, dead);

		PitPlayer pitAttacker = PitPlayer.getPitPlayer(killer);
		PitPlayer pitDefender = PitPlayer.getPitPlayer(dead);


		Telebow.teleShots.removeIf(teleShot -> teleShot.getShooter().equals(dead));

		dead.setHealth(dead.getMaxHealth());
		dead.playEffect(EntityEffect.HURT);
		dead.playSound(dead.getLocation(), Sound.FALL_BIG, 1000, 1F);
		dead.playSound(dead.getLocation(), Sound.FALL_BIG, 1000, 1F);
		Regularity.toReg.remove(dead.getUniqueId());
		if(NonManager.getNon(dead) == null) {
			FileConfiguration playerData = APlayerData.getPlayerData(dead);
			playerData.set("level", pitDefender.playerLevel);
			playerData.set("playerkills", pitDefender.playerKills);
			playerData.set("xp", pitDefender.remainingXP);
			APlayerData.savePlayerData(dead);
		}
		Non attackingNon = NonManager.getNon(killer);
		if(attackingNon == null) {

			pitAttacker.incrementKills();
		}

		Misc.multiKill(killer);

		Non defendingNon = NonManager.getNon(dead);
		if(defendingNon == null) {
			Location spawnLoc = MapManager.getPlayerSpawn();
			if(PitEventManager.activeEvent != null) {
				if(PitEventManager.activeEvent.getClass() != CaptureTheFlag.class) dead.teleport(spawnLoc);
			} else dead.teleport(spawnLoc);
			if(attackingNon == null) {
				FileConfiguration playerData = APlayerData.getPlayerData(killer);
				if(killer != dead && !isNaked(dead)) {
					if(killEvent.isLuckyKill) pitAttacker.playerKills = pitAttacker.playerKills + 3;
					else pitAttacker.playerKills = pitAttacker.playerKills + 1;
				}

				playerData.set("playerkills", pitAttacker.playerKills);
				APlayerData.savePlayerData(killer);
			}
		} else {
			defendingNon.respawn();
		}

		pitDefender.endKillstreak();
		pitDefender.bounty = 0;
		for(PotionEffect potionEffect : dead.getActivePotionEffects()) {
			dead.removePotionEffect(potionEffect.getType());
		}

		Non killingNon = NonManager.getNon(killer);
		if(killingNon != null) {
			killingNon.rewardKill();
		} else {
//			Disabled auto-tenacity
//			pitAttacker.heal(2);
		}


		if(pitAttacker.remainingXP - killEvent.getFinalXp() >= 0)
			pitAttacker.remainingXP = pitAttacker.remainingXP - killEvent.getFinalXp();
		else pitAttacker.remainingXP = 0;
		LevelManager.incrementLevel(killer);
		PitSim.VAULT.depositPlayer(killEvent.killer, killEvent.getFinalGold());

		DecimalFormat df = new DecimalFormat("##0.00");
		String kill = "&a&lKILL!&7 on %luckperms_prefix%" + (defendingNon == null ? "%player_name%" : defendingNon.displayName)
				+ " &b+" + killEvent.getFinalXp() + "XP" + " &6+" + df.format(killEvent.getFinalGold()) + "g";
		String death = "&c&lDEATH! &7by %luckperms_prefix%" + (killingNon == null ? "%player_name%" : killingNon.displayName);
		String killActionBar = "&7%luckperms_prefix%" + (defendingNon == null ? "%player_name%" : defendingNon.displayName) + " &a&lKILL!";

		PitPlayer pitKiller = PitPlayer.getPitPlayer(killer);
		if(!pitKiller.disabledKillFeed)
			AOutput.send(killEvent.killer, PlaceholderAPI.setPlaceholders(killEvent.dead, kill));
		PitPlayer pitDead = PitPlayer.getPitPlayer(dead);
		if(!pitDead.disabledKillFeed)
			AOutput.send(killEvent.dead, PlaceholderAPI.setPlaceholders(killEvent.killer, death));
		String actionBarPlaceholder = PlaceholderAPI.setPlaceholders(killEvent.dead, killActionBar);
		new BukkitRunnable() {
			@Override
			public void run() {

				Misc.sendActionBar(killEvent.killer, actionBarPlaceholder);
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);

		double finalDamage = 0;
		for(Map.Entry<UUID, Double> entry : pitDefender.recentDamageMap.entrySet()) {

			finalDamage += entry.getValue();
		}

		for(Map.Entry<UUID, Double> entry : pitDefender.recentDamageMap.entrySet()) {

			if(entry.getKey().equals(killEvent.killer.getUniqueId())) continue;

			Player assistPlayer = Bukkit.getPlayer(entry.getKey());
			if(assistPlayer == null) continue;
			double assistPercent = entry.getValue() / finalDamage;

			PitPlayer pitPlayer = PitPlayer.getPitPlayer(assistPlayer);
			if(pitPlayer.hasPerk(AssistantToTheStreaker.INSTANCE)) {
				pitPlayer.incrementAssist(assistPercent);
			}

			int xp = (int) Math.ceil(20 * assistPercent);
			double gold = 20 * assistPercent;

			PitPlayer assistPitPlayer = PitPlayer.getPitPlayer(assistPlayer);
			if(assistPitPlayer.remainingXP - xp < 0) assistPitPlayer.remainingXP = 0;
			else assistPitPlayer.remainingXP = assistPitPlayer.remainingXP - xp;
			LevelManager.incrementLevel(assistPlayer);

			if(killEvent.getFinalGold() > 10) {
				PitSim.VAULT.depositPlayer(assistPlayer, 10);
			} else {
				PitSim.VAULT.depositPlayer(assistPlayer, gold);
			}


			ASound.play(assistPlayer, Sound.ORB_PICKUP, 1F, 1.7301587F);
			String assist = "&a&lASSIST!&7 " + Math.round(assistPercent * 100) + "% on %luckperms_prefix%" +
					(defendingNon == null ? "%player_name%" : defendingNon.displayName) + " &b+" + xp + "XP" + " &6+" + df.format(gold) + "g";

			if(!assistPitPlayer.disabledKillFeed)
				AOutput.send(assistPlayer, PlaceholderAPI.setPlaceholders(killEvent.dead, assist));
		}

		pitDefender.assistRemove.forEach(BukkitTask::cancel);
		pitDefender.assistRemove.clear();

		pitDefender.recentDamageMap.clear();

		String message = "%luckperms_prefix%";
		pitDead.prefix = "&7[&e" + pitDead.playerLevel + "&7] &7" + PlaceholderAPI.setPlaceholders(pitDead.player, message);

		if(PitEventManager.majorEvent && UpgradeManager.hasUpgrade(dead, "LIFE_INSURANCE")) {
			AOutput.send(dead, "&2&lLIFE INSURANCE! &7Inventory protected.");
		} else {

			boolean divine = DivineIntervention.INSTANCE.isDivine(dead);
			boolean feather = FunkyFeather.useFeather(dead, divine);

			for(int i = 0; i < dead.getInventory().getSize(); i++) {
				ItemStack itemStack = dead.getInventory().getItem(i);
				if(Misc.isAirOrNull(itemStack)) continue;
				NBTItem nbtItem = new NBTItem(itemStack);
				if(nbtItem.hasKey(NBTTag.MAX_LIVES.getRef())) {
					int lives = nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef());
					if(feather || divine) return;
					if(lives - 1 == 0) {
						dead.getInventory().remove(itemStack);
					} else {
						nbtItem.setInteger(NBTTag.CURRENT_LIVES.getRef(), nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef()) - 1);
						EnchantManager.setItemLore(nbtItem.getItem());
						dead.getInventory().setItem(i, nbtItem.getItem());
					}
				}
			}
			if(!feather && !divine) ProtArmor.deleteArmor(dead);
			if(!Misc.isAirOrNull(dead.getInventory().getLeggings())) {
				ItemStack pants = dead.getInventory().getLeggings();
				NBTItem nbtItem = new NBTItem(pants);
				if(nbtItem.hasKey(NBTTag.MAX_LIVES.getRef())) {
					int lives = nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef());
					if(!feather && !divine) {
						if(lives - 1 == 0) {
							dead.getInventory().remove(pants);
						} else {
							nbtItem.setInteger(NBTTag.CURRENT_LIVES.getRef(), nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef()) - 1);
							EnchantManager.setItemLore(nbtItem.getItem());
							dead.getInventory().setLeggings(nbtItem.getItem());
						}
					}

				}
			}
		}
	}

	public static void Death(Player dead) {
		Telebow.teleShots.removeIf(teleShot -> teleShot.getShooter().equals(dead));

		dead.setHealth(dead.getMaxHealth());
		dead.playEffect(EntityEffect.HURT);
		dead.playSound(dead.getLocation(), Sound.FALL_BIG, 1000, 1F);
		dead.playSound(dead.getLocation(), Sound.FALL_BIG, 1000, 1F);
		Regularity.toReg.remove(dead.getUniqueId());
		CombatManager.taggedPlayers.remove(dead.getUniqueId());

		FileConfiguration playerData = APlayerData.getPlayerData(dead);
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(dead);
		playerData.set("level", pitPlayer.playerLevel);
		playerData.set("playerkills", pitPlayer.playerKills);
		playerData.set("xp", pitPlayer.remainingXP);
		APlayerData.savePlayerData(dead);

		Location spawnLoc = MapManager.getPlayerSpawn();
		if(PitEventManager.activeEvent != null) {
			if(PitEventManager.activeEvent.getClass() != CaptureTheFlag.class) dead.teleport(spawnLoc);
		} else dead.teleport(spawnLoc);


//		for(ItemStack itemStack : dead.getInventory()) {
//
//			if(Misc.isAirOrNull(itemStack)) continue;
//			NBTItem nbtItem = new NBTItem(itemStack);
//			if(nbtItem.hasKey(NBTTag.MAX_LIVES.getRef())) {
//				Bukkit.broadcastMessage(itemStack.getItemMeta().getDisplayName());
//				int lives = nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef());
//				nbtItem.setInteger(NBTTag.CURRENT_LIVES.getRef(), nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef()) - 1);
//				nbtItem.getItem();
//				EnchantManager.setItemLore(itemStack);
//				Bukkit.broadcastMessage(String.valueOf(nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef())));
//			}
//		}
		PitPlayer pitDefender = PitPlayer.getPitPlayer(dead);
		pitDefender.endKillstreak();
		pitDefender.bounty = 0;
		for(PotionEffect potionEffect : dead.getActivePotionEffects()) {
			dead.removePotionEffect(potionEffect.getType());
		}
		AOutput.send(dead, "&c&lDEATH!");
		String message = "%luckperms_prefix%";
		pitDefender.prefix = "&7[&e" + pitDefender.playerLevel + "&7] &7" + PlaceholderAPI.setPlaceholders(pitDefender.player, message);

		if(PitEventManager.majorEvent && UpgradeManager.hasUpgrade(dead, "LIFE_INSURANCE")) {
			AOutput.send(dead, "&2&lLIFE INSURANCE! &7Inventory protected.");
		} else {

			boolean divine = DivineIntervention.INSTANCE.isDivine(dead);
			boolean feather = FunkyFeather.useFeather(dead, divine);

			for(int i = 0; i < dead.getInventory().getSize(); i++) {
				ItemStack itemStack = dead.getInventory().getItem(i);
				if(Misc.isAirOrNull(itemStack)) continue;
				NBTItem nbtItem = new NBTItem(itemStack);
				if(nbtItem.hasKey(NBTTag.MAX_LIVES.getRef())) {
					int lives = nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef());
					if(feather) return;
					if(lives - 1 == 0) {
						dead.getInventory().remove(itemStack);
					} else {
						nbtItem.setInteger(NBTTag.CURRENT_LIVES.getRef(), nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef()) - 1);
						EnchantManager.setItemLore(nbtItem.getItem());
						dead.getInventory().setItem(i, nbtItem.getItem());
					}
				}
			}

			if(!feather) ProtArmor.deleteArmor(dead);

			if(!Misc.isAirOrNull(dead.getInventory().getLeggings())) {
				ItemStack pants = dead.getInventory().getLeggings();
				NBTItem nbtItem = new NBTItem(pants);
				if(nbtItem.hasKey(NBTTag.MAX_LIVES.getRef())) {
					int lives = nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef());
					if(!feather) {
						if(lives - 1 == 0) {
							dead.getInventory().remove(pants);
						} else {
							nbtItem.setInteger(NBTTag.CURRENT_LIVES.getRef(), nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef()) - 1);
							EnchantManager.setItemLore(nbtItem.getItem());
							dead.getInventory().setLeggings(nbtItem.getItem());
						}
					}

				}
			}
		}
	}

	public static boolean isNaked(Player player) {
		if(!Misc.isAirOrNull(player.getInventory().getHelmet())) return false;
		if(!Misc.isAirOrNull(player.getInventory().getChestplate())) return false;
		if(!Misc.isAirOrNull(player.getInventory().getLeggings())) return false;
		return Misc.isAirOrNull(player.getInventory().getBoots());
	}
}
