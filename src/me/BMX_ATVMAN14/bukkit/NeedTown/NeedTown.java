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

	}

	public void createconfig() {
		// Creates config.yml
		File configfile = new File(getDataFolder() + File.separator
				+ "config.yml");
		// If config.yml doesnt exit
		if (!configfile.exists()) {
			this.getConfig().set("NeedTownMessage", true);
			this.getConfig().options().copyDefaults(true);
			this.saveDefaultConfig();
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String args[]) {
		Player player = (Player) sender;
		if (player.hasPermission("needtown.use")) {
			if (cmd.getName().equalsIgnoreCase("needtown")) {
				if (args.length > 1) {
					if (!(sender.hasPermission("needtown.setmessage"))) {
						if (args[0].equalsIgnoreCase("setmessage")) {
							StringBuilder sb = new StringBuilder();
							for (int i = 0; i < args.length; i++) {
								if (i != 0) {
									sb.append(' ');
								}
								sb.append(args);
							}
							sender.sendMessage(plugin.getName()
									+ " The message has been changed to: "
									+ sb.toString().replaceAll("(&([a-f0-9]))",
											"\u00A7$2"));
							plugin.getConfig().set(
									"Message",
									sb.toString().replaceAll("(&([a-f0-9]))",
											"\u00A7$2"));
							plugin.saveConfig();
							return true;
						}
					} else {
						player.sendMessage(ChatColor.RED
								+ "Error: You do not have permission to do that!");
						return true;
					}
				}
				
				if (this.getConfig().getBoolean("NeedTownMessage", true)) {
					String message = this.getConfig().getString("Message");
					message = message.replace("%p", player.getName());
					message.replaceAll("(&([a-f0-9]))", "\u00A7$2");
					sender.sendMessage(message);
					return true;
				} else {
					return defaultmessage(player);
				}
			}
		} else {
			player.sendMessage(ChatColor.RED
					+ "Error: You do not have permission to do that!");
			return true;
		}
		return false;
	}

	public boolean defaultmessage(Player player) {
		Bukkit.broadcastMessage(ChatColor.GOLD + player.getDisplayName()
				+ ChatColor.AQUA + " would like to be invited to a town! "
				+ ChatColor.RED + "Town owners" + ChatColor.AQUA
				+ " make sure to invite " + ChatColor.GOLD
				+ player.getDisplayName() + ChatColor.AQUA + "!");

		return true;
	}

}
