package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CombatManager implements Listener {

    int combatTime = 15 * 20;

    public static HashMap<UUID, Integer> taggedPlayers = new HashMap<>();
    public static List<UUID> bannedPlayers = new ArrayList<>();

    static {

        new BukkitRunnable() {
            @Override
            public void run() {

                List<UUID> toRemove = new ArrayList<>();
                for(Map.Entry<UUID, Integer> entry : taggedPlayers.entrySet()) {
                    int time = entry.getValue();
                    time = time - 1;

                    if(time > 0) taggedPlayers.put(entry.getKey(), time);
                    else toRemove.add(entry.getKey());
                }

                for(UUID uuid : toRemove) {
                    taggedPlayers.remove(uuid);
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        if(player.getUniqueId().equals(uuid)) {
                            PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
                            pitPlayer.lastHitUUID = null;
                        }
                    }
                }

            }
        }.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
    }

   @EventHandler
   public void onAttack(AttackEvent.Apply attackEvent) {
        Player attacker = attackEvent.attacker;
        Player defender = attackEvent.defender;

        taggedPlayers.put(attacker.getUniqueId(), combatTime);
        taggedPlayers.put(defender.getUniqueId(), combatTime);

   }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onJoin(PlayerJoinEvent event) {


    }

   @EventHandler
    public static void onLeave(PlayerQuitEvent event) {
        if(NonManager.getNon(event.getPlayer()) != null) return;
       FileConfiguration playerData = APlayerData.getPlayerData(event.getPlayer());
       PitPlayer pitplayer = PitPlayer.getPitPlayer(event.getPlayer());
       playerData.set("level", pitplayer.playerLevel);
       playerData.set("playerkills", pitplayer.playerKills);
       playerData.set("xp", pitplayer.remainingXP);
       APlayerData.savePlayerData(event.getPlayer());

//        Player player = event.getPlayer();
//
//        if(taggedPlayers.containsKey(player.getUniqueId()) && !player.hasPermission("pitsim.combatlog") && !player.isOp()) {
//            player.teleport(Bukkit.getWorld("pit").getSpawnLocation());
//            taggedPlayers.remove(player.getUniqueId());
//
//            bannedPlayers.add(player.getUniqueId());
//            new BukkitRunnable() {
//                @Override
//                public void run() {
//                    bannedPlayers.remove(player.getUniqueId());
//                }
//            }.runTaskLater(PitSim.INSTANCE, 60 * 20);
//        }
   }

   @EventHandler
   public static void onDeath(KillEvent event) {
       taggedPlayers.remove(event.dead.getUniqueId());
   }

   @EventHandler
    public static void onCommandSend(PlayerCommandPreprocessEvent event) {

        List<String> BlockedCommands = new ArrayList<>();
        BlockedCommands.add("/ec");
        BlockedCommands.add("/echest");
        BlockedCommands.add("/enderchest");
        BlockedCommands.add("/perks");
        BlockedCommands.add("/spawn");


        if(taggedPlayers.containsKey(event.getPlayer().getUniqueId())) {
            for(String cmd : BlockedCommands) {
                if(cmd.equals(event.getMessage())) {
                    event.setCancelled(true);
                    AOutput.error(event.getPlayer(), "&c&c&lNOPE! &7You cannot use that while in combat!");
                }
            }
        }


   }
}
