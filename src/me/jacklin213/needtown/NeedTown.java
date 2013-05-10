package me.jacklin213.needtown;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import me.jacklin213.needtown.utils.UpdateChecker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class NeedTown extends JavaPlugin {

	public static NeedTown plugin;
	
	public final Logger logger = Logger.getLogger("Minecraft");	
	public String chatPluginName = ChatColor.RED + "[" + ChatColor.AQUA + "Need"+ ChatColor.YELLOW +"Town" + ChatColor.RED + "] ";
	private File colorFile;
	public UpdateChecker updateChecker;
	
	private List<String> cantDoCommand = new ArrayList<String>();

	public void onDisable() {
		logger.info(String.format("[%s] Disabled Version %s", getDescription()
				.getName(), getDescription().getVersion()));
	}

	public void onEnable() {
		/*Boolean updateCheck = Boolean.valueOf(getConfig().getBoolean("UpdateCheck"));
		 
		this.updateChecker = new UpdateChecker(this, "http://dev.bukkit.org/server-mods/needtown/files.rss");

		if ((updateCheck) && (this.updateChecker.updateNeeded())) {
			this.logger.info(String.format("[%s] A new update is avalible, Version: %s", getDescription().getName(), this.updateChecker.getVersion()));
			this.logger.info(String.format("[%s] Get it now from: %s", getDescription().getName(), this.updateChecker.getLink()));
		}*/
		
		logger.info(String.format("[%s] Enabled Version %s by jacklin213",
				getDescription().getName(), getDescription().getVersion()));
		createfiles();
	}

	public void createfiles() {
		// Creates config.yml
		File configfile = new File(getDataFolder() + File.separator
				+ "config.yml");
		this.colorFile = new File(getDataFolder() + File.separator
				+ "colors.yml");
		// If config.yml doesnt exit
		if (!configfile.exists() || !colorFile.exists()){
			if (!configfile.exists()) {
				this.getConfig().options().copyDefaults(true);
				this.saveDefaultConfig();
			}
			if (!colorFile.exists()) {
				try {
					this.logger.info("[NeedTown] Generating colors.yml");
					PrintStream out = new PrintStream(new FileOutputStream(
							this.colorFile));
					out.println("# ======= Color.yml ======= #");
					out.println("# Do not edit any thing in here or else you won't know the colors");
					out.println("# This is a Color.yml for NeedTown");
					out.println("List of colors:");
					out.println("<red> - Color Red");
					out.println("<dark_red> - Color DarkRed");
					out.println("<green> - Color Green");
					out.println("<dark_green> - Color Dark-Green");
					out.println("<aqua> - Color Aqua");
					out.println("<dark_aqua> - Color Dark-Aqua");
					out.println("<blue> - Color Gold");
					out.println("<dark_blue> - Color Dark-Blue");
					out.println("<yellow> - Color Yellow");
					out.println("<gold> - Color Gold");
					out.println("<white> - Color White");
					out.println("<black> - Color Black");
					out.println("<light_purple> - Color Light-Purple");
					out.println("<dark_purple> - Color Dark-Purple");
					out.println("<gray> - Color Gray");
					out.println("<dark_gray> - Color Dark-Grey");
					out.println("# These are the only ones tested so far, feel free too try them yourself");
					out.println();
					out.println("# Copyright BMX_ATVMAN14,jacklin213,LinCraft,LinProdutions 2012");
					out.close();
				} catch (IOException e) {
					this.logger.severe(String.format("[%s] Error in creating file !", getDescription().getName()));
				}
				
			}
			
			this.getLogger().info("Reqired files Generated");
		}
	}

	  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		  boolean useCooldown = getConfig().getBoolean("Cooldowns");
		  int cdTime = (getCooldownTime() * 20);
		  if ((sender instanceof Player)) {
			  Player player = (Player)sender;
			  final String playerName = player.getName();
			  if (player.hasPermission("needtown.use")) {
				  if ((cmd.getName().equalsIgnoreCase("needtown")) || 
						  (cmd.getName().equalsIgnoreCase("nt"))) {
					  if (args.length == 1) {
						  if (player.hasPermission("needtown.reload")) {
							  if (args[0].equalsIgnoreCase("reload")) {
								  reloadConfig();
								  String string = "<green>Config reloaded!";
								  sender.sendMessage(chatPluginName + format(string));
								  return true;
							  }
						  } else {
							  player.sendMessage(ChatColor.RED + "Error: You do not have permission to do that!");
							  return true;
						  }
					  }
					  if (useCooldown){
						  if (!cantDoCommand.contains(playerName)){
							  if (getConfig().getBoolean("CustomNeedTownMessage", true)) {
								  String message = chatPluginName + (getConfig().getString("Message"));
								  message = message.replace("%p", player.getName());
								  Bukkit.broadcastMessage(format(message));
								  cantDoCommand.add(playerName);
								  Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
									  public void run(){
										  cantDoCommand.remove(playerName);
									  }
								  }, cdTime);
								  return true;
							  } else {
								  cantDoCommand.add(playerName);
								  Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
									  public void run(){
										  cantDoCommand.remove(playerName);
									  }
								  }, cdTime);
								  return defaultmessage(player); 
							  }
						  } else {
							  String message = getConfig().getString("Cooldown-Message");
							  player.sendMessage(chatPluginName + format(message));
							  return true;
						  }
					  } else {
						  if (getConfig().getBoolean("CustomNeedTownMessage", true)) {
							  String message = chatPluginName + (getConfig().getString("Message"));
							  message = message.replace("%p", player.getName());
							  Bukkit.broadcastMessage(format(message));
							  return true;
						  } else {
							  return defaultmessage(player); 
						  }
					  }
				  }
			  } else {
				  player.sendMessage(ChatColor.RED + "Error: You do not have permission to do that!");
				  return true;
			  }
		  } else {
			  sender.sendMessage("This is a player only command");
			  return true;
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
		Bukkit.broadcastMessage(chatPluginName + ChatColor.GOLD + player.getDisplayName()
				+ ChatColor.AQUA + " would like to be invited to a town! "
				+ ChatColor.RED + "Town owners" + ChatColor.AQUA
				+ " make sure to invite " + ChatColor.GOLD
				+ player.getDisplayName() + ChatColor.AQUA + "!");

		return true;
	}
	
	public int getCooldownTime(){
		int cdTime;
		try {
			cdTime = Integer.parseInt(getConfig().getString("Cooldown-time"));
			return cdTime;
		} catch (NumberFormatException e){
			this.logger.info(String.format("[%s] Error in loading the Cooldown value in the config", getDescription().getName()));
			this.logger.info(String.format("[%s] Please fix and reload the plugin", getDescription().getName()));
		}	
		return 0;
	}
		

	/*
	 * For String format usage String message =
	 * this.getConfig().getString("Message"); this.getConfig().set("Message",
	 * format(message));
	 */

}
