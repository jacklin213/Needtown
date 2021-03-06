package me.jacklin213.needtown;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import me.jacklin213.needtown.utils.Updater;
import me.jacklin213.needtown.utils.Updater.UpdateResult;
import me.jacklin213.needtown.utils.Updater.UpdateType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class NeedTown extends JavaPlugin {

	public static NeedTown plugin;
	
	public Logger log;
	public String chatPluginPrefix;
	public Updater updater;
	private Towny towny = null;
	private File colorFile, usersFile;
	public PluginManager pm;
	
	private List<String> cantDoCommand = new ArrayList<String>();
	private List<UUID> users = new ArrayList<UUID>();

	@Override
	public void onDisable() {
		saveUsers();
		super.onDisable();
	}
	
	public void onEnable() {
		log = getLogger();
		pm = getServer().getPluginManager();
		
		Boolean useTowny = getConfig().getBoolean("TownyIntegration");
		
		//Update Check
		Boolean updateCheck = Boolean.valueOf(getConfig().getBoolean("UpdateCheck"));
		Boolean autoUpdate = Boolean.valueOf(getConfig().getBoolean("AutoUpdate"));
		this.updateCheck(updateCheck, autoUpdate, 41673);

		// Checks for towny
		if (useTowny) {
			checkPlugins();
			if ((towny == null) || (getServer().getScheduler().scheduleSyncDelayedTask(this, new onLoadedTask(this),1) == -1)) {
				/*
				 *  We either failed to find Towny
				 *  or the Scheduler failed to register the task.
				 */
				log.severe("Could not find Towny, disabling plugin...");
				pm.disablePlugin(this);
				return;
			}
		}
		
		log.info(String.format("Enabled Version %s by jacklin213", getDescription().getVersion()));
		
		// Creates Config.yml + Colors.yml
		createfiles();
		
		if (usersFile.exists())
			for (String uuid : YamlConfiguration.loadConfiguration(usersFile).getKeys(false))
				users.add(UUID.fromString(uuid));
		
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				saveUsers();
			}
		}, 0, getConfig().getInt("Save-Period") * 60 * 20);
	}
	
	private void saveUsers() {
		try {
			PrintStream out = new PrintStream(new FileOutputStream(usersFile));
			for (UUID uuid : users) 
				out.println(uuid.toString());
			out.close();
			log.info("Saving users...");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void createfiles() {
		try {
			// Creates config.yml
			File configfile = new File(getDataFolder() + File.separator	+ "config.yml");
			this.colorFile = new File(getDataFolder() + File.separator + "colors.yml");
			this.usersFile = new File(getDataFolder() + File.separator + "users.yml");
			// If config.yml doesnt exit
			if (!configfile.exists() || !colorFile.exists() || !usersFile.exists()) {
				if (!configfile.exists()) {
					this.getConfig().options().copyDefaults(true);
					this.saveDefaultConfig();
				}
				if (!usersFile.exists()) 
					usersFile.createNewFile();
				if (!colorFile.exists()) {
					log.info("[NeedTown] Generating colors.yml");
					PrintStream out = new PrintStream(new FileOutputStream(this.colorFile));
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
					out.println("# Copyright BMX_ATVMAN14,jacklin213,LinCraft,LinProdutions 2015");
					out.close();
				}
				this.log.info("Reqired files Generated");
			}
		} catch (IOException e) {
			this.log.severe("Error in creating file !");
		}
	}

	  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		  // Gets things needed from Config
		  boolean useCooldown = getConfig().getBoolean("Cooldowns");
		  boolean useTowny = getConfig().getBoolean("TownyIntegration");
		  int cdTime = (getCooldownTime() * 20);
		  chatPluginPrefix = format(getConfig().getString("PluginPrefix")) + " ";
		  // Check if sender is a player
		  if (commandLabel.equalsIgnoreCase("needtownr") || commandLabel.equalsIgnoreCase("ntr")) {
			  if (sender.hasPermission("needtown.reload")) {
				  if (sender instanceof Player) {
					  sender.sendMessage(chatPluginPrefix + "Console only command!"); 
					  return true;
				  }
				  reloadNTConsole(sender);
				  return true;
			  }
		  }
		  if (sender instanceof Player) {
			  Player player = (Player)sender;
			  final String playerName = player.getName();
			  if (player.hasPermission("needtown.use")) {
				  if ((cmd.getName().equalsIgnoreCase("needtown")) || (cmd.getName().equalsIgnoreCase("nt"))) {
					  
					  if (args.length == 1) {
						  if (player.hasPermission("needtown.reload")) {
							  if (args[0].equalsIgnoreCase("reload")) {
								  reloadNT(sender);
								  return true;
							  }
						  } else if (player.hasPermission("needtown.users")) {
							  player.sendMessage(ChatColor.YELLOW + "Players who need a town: ");
							  if (users.size() == 0) 
								  player.sendMessage("None");
							  else
								  for (UUID uuid : users)
									  player.sendMessage(Bukkit.getPlayer(uuid).getName());
						  } else {
							  player.sendMessage(ChatColor.RED + "Error: You do not have permission to do that!");
							  return true;
						  }
					  }
					  
					  // Using Towny (begin)
					  if (useTowny) {
						 if (this.hasTown(player)) {
							  player.sendMessage(chatPluginPrefix + "You cannot use this command because you are already in a town!"); 
							  return true;
						 }
					  } 
					  
					  if (useCooldown) {
						  runCommandWithCD(playerName, player, cdTime);
						  return true;
					  } else {
						 runCommand(player);
						 return true;
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
		  
	/**
	 * Gets message from config and formats it (Grabs colors).
	 * 
	 * @param string The message to format
	 * @return Formatted Message
	 */
	public static String format(String string) {
		String s = string;
		for (ChatColor color : ChatColor.values()) {
			s = s.replaceAll("(?i)<" + color.name() + ">", "" + color);
		}
		return s;
	}

	/**
	 * Broadcasts the default NeedTown message if none is specified in the config 
	 * or if the config is broken.
	 * 
	 * @param player Who issued the command
	 */
	public void broadcastDefaultmessage(Player player) {
		chatPluginPrefix = format(getConfig().getString("PluginPrefix")) + " ";
		Bukkit.broadcastMessage(chatPluginPrefix + ChatColor.GOLD + player.getDisplayName()
				+ ChatColor.AQUA + " would like to be invited to a town! "
				+ ChatColor.RED + "Town owners" + ChatColor.AQUA
				+ " make sure to invite " + ChatColor.GOLD
				+ player.getDisplayName() + ChatColor.AQUA + "!");
	}
	
	/**
	 * Gets the cooldown time from config.
	 * 
	 * @return cooldown time
	 */
	
	public int getCooldownTime() {
		int cdTime;
		try {
			cdTime = Integer.parseInt(getConfig().getString("Cooldown-Time"));
			return cdTime;
		} catch (NumberFormatException e) {
			this.log.info("Error in loading the Cooldown value in the config");
			this.log.info("Please fix and reload the plugin");
		}	
		return 0;
	}
	
	
	/**
	 * Runs the NeedTown command with cooldown.
	 * 
	 * @param playerName Name of Player that issued the command
	 * @param player Who issued the command 
	 * @param cdTime Cooldown time obtained from config
	 * @return the success boolean outcome of the command
	 */
	public void runCommandWithCD(final String playerName, Player player, int cdTime) {
		chatPluginPrefix = format(getConfig().getString("PluginPrefix")) + " ";
		if (!cantDoCommand.contains(playerName)) {
			  if (getConfig().getBoolean("CustomNeedTownMessage", true)) {
				  String message = chatPluginPrefix + (getConfig().getString("Message"));
				  message = message.replace("%p", player.getName());
				  Bukkit.broadcastMessage(format(message));
				  cantDoCommand.add(playerName);
				  Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					  public void run() {
						  cantDoCommand.remove(playerName);
					  }
				  }, cdTime);
			  } else {
				  cantDoCommand.add(playerName);
				  Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					  public void run(){
						  cantDoCommand.remove(playerName);
					  }
				  }, cdTime);
				  broadcastDefaultmessage(player); 
			  }
			  users.add(player.getUniqueId());
		  } else {
			  String message = getConfig().getString("Cooldown-Message");
			  player.sendMessage(chatPluginPrefix + format(message));
		  }
	}
	
	/**
	 * Runs the Needtown command with no cooldown
	 * 
	 * @param player who issued the command
	 */
	
	public void runCommand(Player player) {
		chatPluginPrefix = format(getConfig().getString("PluginPrefix")) + " ";
		if (getConfig().getBoolean("CustomNeedTownMessage", true)) {
			String message = chatPluginPrefix + (getConfig().getString("Message"));
			message = message.replace("%p", player.getName());
			Bukkit.broadcastMessage(format(message));
		} else {
			broadcastDefaultmessage(player); 
		}
		users.add(player.getUniqueId());
	}
	
	private void checkPlugins() {
        Plugin test;
        
        pm = getServer().getPluginManager();
        test = pm.getPlugin("Towny");
        if (test != null && test instanceof Towny)
        	towny = (Towny)test;

	}
	
	protected Towny getTowny() {
		return towny;
	}
	
	/**
	 * Runs the reload for player
	 * 
	 * @param useTowny - After config is reloaded, check if they enabled towny
	 * @param sender - CommandSender (Should be player)
	 */
	
	public void reloadNT(CommandSender sender) {
		chatPluginPrefix = format(getConfig().getString("PluginPrefix")) + " ";
		reloadConfig();
		boolean useTowny = getConfig().getBoolean("TownyIntegration");
		  String string = "<green>Config reloaded!";
		  if (useTowny) {
				checkPlugins();
				if ((towny == null) || (getServer().getScheduler().scheduleSyncDelayedTask(this, new onLoadedTask(this),1) == -1)) {
					/*
					 *  We either failed to find Towny
					 *  or the Scheduler failed to register the task.
					 */
					log.severe("Could not find Towny, disabling plugin...");
					pm.disablePlugin(this);
				}
			}
		  sender.sendMessage(chatPluginPrefix + format(string));
	}
	
	/**
	 * Runs the reload for console
	 * 
	 * @param useTowny - After config is reloaded, check if they enabled towny
	 * @param sender - CommandSender (Should be console)
	 */
	public void reloadNTConsole(CommandSender sender) {
		chatPluginPrefix = format(getConfig().getString("PluginPrefix")) + " ";
		reloadConfig();
		boolean useTowny = getConfig().getBoolean("TownyIntegration");
		  if (useTowny) {
				checkPlugins();
				if ((towny == null) || (getServer().getScheduler().scheduleSyncDelayedTask(this, new onLoadedTask(this),1) == -1)) {
					/*
					 *  We either failed to find Towny
					 *  or the Scheduler failed to register the task.
					 */
					log.severe("Could not find Towny, disabling plugin...");
					pm.disablePlugin(this);
				}
			}
		  log.info("Config Reloaded!");
	}
	
	@SuppressWarnings("deprecation")
	private boolean hasTown(Player player) {
		List<Resident> noTown = TownyUniverse.getDataSource().getResidentsWithoutTown();
		for (Resident resident : noTown) {
			if (Bukkit.getPlayer(resident.getName()).getUniqueId() == player.getUniqueId()) {
				return false;
			}
		}
		return true;
	}

	/*
	 * For String format usage String message =
	 * this.getConfig().getString("Message"); this.getConfig().set("Message", format(message));
	 */

	private void updateCheck(boolean updateCheck, boolean autoUpdate, int ID) {
		if(updateCheck && (autoUpdate == false)) {
			updater = new Updater(this, ID, this.getFile(), UpdateType.NO_DOWNLOAD, true);
			if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
			    log.info("New version available! " + updater.getLatestName());
			}
			if (updater.getResult() == UpdateResult.NO_UPDATE) {
				log.info(String.format("You are running the latest version of %s", getDescription().getName()));
			}
		}
		if(autoUpdate && (updateCheck == false)) {
			updater = new Updater(this, ID, this.getFile(), UpdateType.NO_VERSION_CHECK, true);
		} 
		if(autoUpdate && updateCheck) {
			updater = new Updater(this, ID, this.getFile(), UpdateType.DEFAULT, true);
			if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
			    log.info("New version available! " + updater.getLatestName());
			}
			if (updater.getResult() == UpdateResult.NO_UPDATE) {
				log.info(String.format("You are running the latest version of %s", getDescription().getName()));
			}
		}
	}
}
