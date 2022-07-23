package dev.kyro.pitsim.misc;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import com.sk89q.worldedit.world.DataException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Bat;

import java.io.File;
import java.io.IOException;

public class SchematicPaste {
	public static void loadSchematic(File file, Location location) {
		WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
		EditSession session = worldEditPlugin.getWorldEdit().getEditSessionFactory().getEditSession(new BukkitWorld(location.getWorld()), 100000);
		try {
			CuboidClipboard clipboard = MCEditSchematicFormat.getFormat(file).load(file);
			clipboard.paste(session, new com.sk89q.worldedit.Vector(location.getX(), location.getY(), location.getZ()), false);
		} catch(MaxChangedBlocksException | DataException | IOException e) {
			e.printStackTrace();
		}
		Bat bat;
	}

	public static EditSession loadSchematicAir(File file, Location location) {
		WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
		EditSession session = worldEditPlugin.getWorldEdit().getEditSessionFactory().getEditSession(new BukkitWorld(location.getWorld()), 100000);
		try {
			CuboidClipboard clipboard = MCEditSchematicFormat.getFormat(file).load(file);
			clipboard.paste(session, new com.sk89q.worldedit.Vector(location.getX(), location.getY(), location.getZ()), true);
		} catch(MaxChangedBlocksException | DataException | IOException e) {
			e.printStackTrace();
		}
		return session;
	}

	public static void loadTutorialSchematic(File file, Location location) {
		WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
		EditSession session = worldEditPlugin.getWorldEdit().getEditSessionFactory().getEditSession(new BukkitWorld(location.getWorld()), 100000);
		try {
			CuboidClipboard clipboard = MCEditSchematicFormat.getFormat(file).load(file);
			clipboard.rotate2D(90);
			clipboard.paste(session, new com.sk89q.worldedit.Vector(location.getX(), location.getY(), location.getZ()), false);
		} catch(MaxChangedBlocksException | DataException | IOException e) {
			e.printStackTrace();
		}
	}
}
