package dev.kyro.pitsim.commands;

import com.google.gson.Gson;
import dev.kyro.arcticapi.misc.AOutput;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;

public class ShowCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!(sender instanceof Player)) return false;

        Player player = (Player) sender;
        ItemStack item = player.getItemInHand();
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();

        if(!player.hasPermission("pitsim.show") && !player.isOp()) {
            AOutput.error(player, "&cInsufficient Permissions");
            return false;
        }

       if(!item.hasItemMeta()) {
           AOutput.error(player, "&cThis item does not have lore!");
           return false;
       }

        StringBuilder builder = new StringBuilder();


        builder.append(meta.getDisplayName() + "\n");

            for(String s : lore) {
                builder.append(s).append("\n");
            }



        String playername = "%luckperms_prefix%%essentials_nickname%";
        String playernamecolor = PlaceholderAPI.setPlaceholders(player, playername);


        BaseComponent[] hoverEventComponents = new BaseComponent[]{
                new TextComponent(String.valueOf(builder))
        };

        TextComponent nonhover = new TextComponent(ChatColor.translateAlternateColorCodes('&',"&6&lSHOWOFF! " + playernamecolor + " &7shows off their "));
        TextComponent hover = new TextComponent(ChatColor.translateAlternateColorCodes('&', meta.getDisplayName()));
        hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverEventComponents));

        nonhover.addExtra(hover);

        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.spigot().sendMessage(nonhover);
        }

        return false;
    }



}
