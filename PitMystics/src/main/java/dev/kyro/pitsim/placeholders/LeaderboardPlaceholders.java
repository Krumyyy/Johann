package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.LeaderboardManager;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.controllers.objects.Leaderboard;
import dev.kyro.pitsim.controllers.objects.LeaderboardData;
import dev.kyro.pitsim.controllers.objects.LeaderboardPosition;
import dev.kyro.pitsim.misc.Misc;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LeaderboardPlaceholders extends PlaceholderExpansion {

	@Override
	public String onPlaceholderRequest(Player player, @NotNull String identifier) {
		if(player == null) return "";

		for(int i = 1; i <= 10; i++) {
			String testString = "leader" + i;
			if(!identifier.equalsIgnoreCase(testString)) continue;
			LeaderboardPosition position = LeaderboardManager.leaderboards.get(0).orderedLeaderboard.get(i - 1);
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(position.uuid);
			LeaderboardData.PlayerData data = LeaderboardData.getLeaderboardData(LeaderboardManager.leaderboards.get(0)).getValue(offlinePlayer.getUniqueId());
			String rankColor = Leaderboard.getRankColor(offlinePlayer.getUniqueId());

			return "&7" + i + ". " + rankColor + offlinePlayer.getName() + "&7 - " + data.prefix;
		}
		return null;
	}

	@Override
	public @NotNull String getIdentifier() {
		return "pitsimlb";
	}

	@Override
	public @NotNull String getAuthor() {
		return "KyroKrypt";
	}

	@Override
	public @NotNull String getVersion() {
		return "1.0";
	}
}
