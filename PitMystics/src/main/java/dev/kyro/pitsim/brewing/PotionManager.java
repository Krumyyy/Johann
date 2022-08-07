package dev.kyro.pitsim.brewing;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.data.APlayer;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.brewing.ingredients.SpiderEye;
import dev.kyro.pitsim.brewing.objects.BrewingIngredient;
import dev.kyro.pitsim.brewing.objects.PotionEffect;
import dev.kyro.pitsim.controllers.BossManager;
import dev.kyro.pitsim.enums.NBTTag;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

public class PotionManager implements Listener {

    public static List<PotionEffect> potionEffectList = new ArrayList<>();
    public static Map<Player, Integer> playerIndex = new HashMap<>();
    public static Map<Player, BossBar> bossBars = new HashMap<>();
    public static List<Entity> potions = new ArrayList<>();
    public static int i = 0;

    static {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (PotionEffect potionEffect : potionEffectList) {
                    potionEffect.tick();
                }
            }
        }.runTaskTimer(PitSim.INSTANCE, 1, 1);


        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    List<PotionEffect> effects = getPotionEffects(player);
                    if(effects.size() == 0) continue;
                    if(BossManager.activePlayers.contains(player)) {
                        hideActiveBossBar(PitSim.adventure.player(player), player);
                        continue;
                    }

                    playerIndex.putIfAbsent(player, 0);
                    int index = playerIndex.get(player);

                    while(index >= effects.size()) {
                        index--;
                    }

                    StringBuilder builder = new StringBuilder();
                    for (PotionEffect effect : effects) {
                        if(effect == effects.get(index)) builder.append(effect.potionType.color + "" + ChatColor.BOLD + effect.potionType.name.toUpperCase(Locale.ROOT)).append(" ").append(AUtil.toRoman(effect.potency.tier));
                        else builder.append(effect.potionType.color + effect.potionType.name).append(" ").append(AUtil.toRoman(effect.potency.tier));
                        if(effect != effects.get(effects.size() - 1)) builder.append(ChatColor.GRAY + ", ");
                    }
                    float progress = (float) effects.get(index).getTimeLeft() / (float) effects.get(index).getOriginalTime();

                    int maxI = effects.size() - 1;
                    if(i == 60) {
                        if(playerIndex.get(player) + 1 > maxI) {
                            playerIndex.put(player, 0);
                        } else playerIndex.put(player, playerIndex.get(player) + 1);
                    }

                    if(!bossBars.containsKey(player)) showMyBossBar(PitSim.adventure.player(player), player, builder.toString(), progress);
                    else {
                        bossBars.get(player).name(Component.text(builder.toString()));
                        bossBars.get(player).progress(progress);
                    }
                    i++;
                    if(i > 20 *3) i = 0;
                }
            }
        }.runTaskTimer(PitSim.INSTANCE, 2, 2);
    }
    
    @EventHandler
    public void onPotionDrink(PlayerItemConsumeEvent event) {
       if(event.getItem().getType() != Material.POTION) return;
       Player player = event.getPlayer();

       event.setCancelled(true);

       ItemStack potion = event.getItem();
        NBTItem nbtItem = new NBTItem(potion);
        if(!nbtItem.hasKey(NBTTag.POTION_IDENTIFIER.getRef())) return;

        BrewingIngredient identifier = BrewingIngredient.getIngredientFromTier(nbtItem.getInteger(NBTTag.POTION_IDENTIFIER.getRef()));
        BrewingIngredient potency = BrewingIngredient.getIngredientFromTier(nbtItem.getInteger(NBTTag.POTION_POTENCY.getRef()));
        BrewingIngredient duration = BrewingIngredient.getIngredientFromTier(nbtItem.getInteger(NBTTag.POTION_DURATION.getRef()));

        if(hasLesserEffect(player, identifier, potency)) {
            AOutput.send(player, "&5&lPOTION! &7You already have a stonger tier of this effect!");
            return;
        }
        replaceLesserEffects(player, identifier, potency);
        player.setItemInHand(new ItemStack(Material.AIR));

        assert identifier != null;
        assert potency != null;

        if(identifier instanceof SpiderEye) {
            identifier.administerEffect(player, potency, 0);
        } else {
            potionEffectList.add(new PotionEffect(player, identifier, potency, duration));
        }
    }


    @EventHandler
    public void onSplash(PotionSplashEvent event) {

        ItemStack potion = event.getPotion().getItem();
        NBTItem nbtItem = new NBTItem(potion);
        if(!nbtItem.hasKey(NBTTag.POTION_IDENTIFIER.getRef())) return;

        BrewingIngredient identifier = BrewingIngredient.getIngredientFromTier(nbtItem.getInteger(NBTTag.POTION_IDENTIFIER.getRef()));
        BrewingIngredient potency = BrewingIngredient.getIngredientFromTier(nbtItem.getInteger(NBTTag.POTION_POTENCY.getRef()));
        BrewingIngredient duration = BrewingIngredient.getIngredientFromTier(nbtItem.getInteger(NBTTag.POTION_DURATION.getRef()));

        int durationTime = identifier.getDuration(duration);

        for (LivingEntity affectedEntity : event.getAffectedEntities()) {
            if(!(affectedEntity instanceof Player)) continue;

            Player player = (Player) affectedEntity;

            if(hasLesserEffect(player, identifier, potency)) {
                AOutput.send(player, "&5&lPOTION! &7You already have a stonger tier of this effect!");
                continue;
            }
            replaceLesserEffects(player, identifier, potency);

            assert identifier != null;
            assert potency != null;

            if(identifier instanceof SpiderEye) {
                identifier.administerEffect(player, potency, 0);
            } else {
                potionEffectList.add(new PotionEffect(player, identifier, potency, Math.max(1, durationTime / event.getAffectedEntities().size())));
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        List<PotionEffect> toExpire = new ArrayList<>();
        for (PotionEffect potionEffect : potionEffectList) {
            if(potionEffect.player == player) toExpire.add(potionEffect);
        }

        APlayer aPlayer = APlayerData.getPlayerData(player);
        FileConfiguration data = aPlayer.playerData;

        for (PotionEffect potionEffect : toExpire) {

            potionEffect.onExpire(true);

            String time = String.valueOf(System.currentTimeMillis());
            data.set("potions." + potionEffect.potionType.name, potionEffect.potency.tier + ":" + potionEffect.getTimeLeft() + ":" + time);

        }
        aPlayer.save();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        APlayer aPlayer = APlayerData.getPlayerData(player);
        FileConfiguration data = aPlayer.playerData;
        if(!data.contains("potions")) return;
        for (String key : data.getConfigurationSection("potions").getKeys(false)) {
            String[] split = data.getString("potions." + key).split(":");
            if(split.length != 3) continue;

            int tier = Integer.parseInt(split[0]);
            int timeLeft = Integer.parseInt(split[1]);
            long time = Long.parseLong(split[2]);

            long passedTicks = ((System.currentTimeMillis() - time) / 1000) * 20;
            if(passedTicks > timeLeft) continue;

            PotionEffect potionEffect = new PotionEffect(player, BrewingIngredient.getIngredientFromName(key), BrewingIngredient.getIngredientFromTier(tier), (int) (timeLeft - passedTicks));

            potionEffectList.add(potionEffect);
        }

        data.set("potions", null);
        aPlayer.save();
    }

    public boolean hasLesserEffect(Player player, BrewingIngredient identifier, BrewingIngredient potency) {
        for (PotionEffect potionEffect : potionEffectList) {
            if(potionEffect.player != player) continue;
            if(potionEffect.potionType != identifier) continue;

            if(potionEffect.potency.tier > potency.tier) {
                return true;
            }
        }
        return false;
    }

    public void replaceLesserEffects(Player player, BrewingIngredient identifier, BrewingIngredient potency) {
        for (PotionEffect potionEffect : potionEffectList) {
            if(potionEffect.player != player) continue;
            if(potionEffect.potionType != identifier) continue;

            if(potionEffect.potency.tier <= potency.tier) {
                potionEffect.onExpire(false);
                return;
            }
        }
    }


    public static List<PotionEffect> getPotionEffects(Player player) {
        List<PotionEffect> effects = new ArrayList<>();
        for (PotionEffect potionEffect : potionEffectList) {
            if(potionEffect.player == player) effects.add(potionEffect);
        }
        return effects;
    }

    public static PotionEffect getEffect(LivingEntity player, BrewingIngredient type) {
        if(!(player instanceof Player)) return null;
        for (PotionEffect potionEffect : potionEffectList) {
            if(potionEffect.player == player && potionEffect.potionType == type) return potionEffect;
        }
        return null;
    }


    public static void showMyBossBar(final @NonNull Audience player, Player realPlayer, String text, float progress) {
        final Component name = Component.text(text);
        final BossBar fullBar = BossBar.bossBar(name, progress, BossBar.Color.PINK, BossBar.Overlay.PROGRESS);

        player.showBossBar(fullBar);
        bossBars.put(realPlayer, fullBar);
    }

    public static void hideActiveBossBar(final @NonNull Audience player, Player realPlayer) {
        player.hideBossBar(bossBars.get(realPlayer));
        bossBars.remove(realPlayer);
    }
}