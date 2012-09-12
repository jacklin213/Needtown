package me.BMX_ATVMAN14.bukkit.NeedTown;

import java.io.File;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class NeedTown extends JavaPlugin {

	public final Logger logger = Logger.getLogger("Minecraft");
	public static NeedTown plugin;
	PluginDescriptionFile pdfFile;

	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info((new StringBuilder(String.valueOf(pdfFile.getName())))
				.append(" Has Been Disabled!").toString());
	}

	public void onEnable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger.info((new StringBuilder(String.valueOf(pdfFile.getName())))
				.append(" Version").append(pdfFile.getVersion())
				.append(" Has Been Enabled!").toString());
		createfiles();
	}

	public void createfiles() {
		// Creates config.yml
		File configfile = new File(getDataFolder() + File.separator
				+ "config.yml");
		File colorfile = new File(getDataFolder() + File.separator
				+ "colors.txt");
		// If config.yml doesnt exit
		if (!configfile.exists()) {
			this.getConfig().getBoolean("NeedTownMessage", true);
			this.getConfig().options().copyDefaults(true);
			this.saveDefaultConfig();
		}
		if (!colorfile.exists()) {
			this.getFile().mkdirs();
			this.getLogger().info("Generating reqired files....");
			this.getLogger().info("Reqired files Generated");
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String args[]) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (player.hasPermission("needtown.use")) {
				if (cmd.getName().equalsIgnoreCase("needtown")
						|| cmd.getName().equalsIgnoreCase("nt")) {
					if (args.length == 1) {
						if (player.hasPermission("needtown.reload")) {
							if (args[0].equalsIgnoreCase("reload")) {
								this.reloadConfig();
								String string = "<red>[ <aqua>Need <yellow>Town <red>] <green>Config reloaded!";
								sender.sendMessage(format(string));
								return true;
							}
						} else {
							player.sendMessage(ChatColor.RED
									+ "Error: You do not have permission to do that!");
							return true;
						}
					} else {
						if (this.getConfig().getBoolean(
								"CustomNeedTownMessage", true)) {
							String message = this.getConfig().getString(
									"Message");
							message = message.replace("%p", player.getName());
							player.sendMessage(format(message));
							return true;
						} else if (this.getConfig().getBoolean(
								"CustomNeedTownMessage", false)) {
							return defaultmessage(player);
						}

					}
				}
			} else {
				player.sendMessage(ChatColor.RED
						+ "Error: You do not have permission to do that!");
				return true;
			}
		} else {
			sender.sendMessage("This is a player only command");
		}

		return false;
	}

	public static String format(String string) {
		String s = string;
		for (ChatColor color : ChatColor.values()) {
			s = s.replaceAll("(?i)<" + color.name() + ">", "" + color);
		}
		return s;
	}

	public boolean defaultmessage(Player player) {
		Bukkit.broadcastMessage(ChatColor.GOLD + player.getDisplayName()
				+ ChatColor.AQUA + " would like to be invited to a town! "
				+ ChatColor.RED + "Town owners" + ChatColor.AQUA
				+ " make sure to invite " + ChatColor.GOLD
				+ player.getDisplayName() + ChatColor.AQUA + "!");

		return true;
	}

	/*
	 * For String format usage String message =
	 * this.getConfig().getString("Message"); this.getConfig().set("Message",
	 * format(message));
	 */

}
