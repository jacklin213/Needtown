package me.BMX_ATVMAN14.bukkit.NeedTown;

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
                logger.info((new StringBuilder(String.valueOf(pdfFile.getName()))).append("Has Been Disabled!").toString());
            }

            public void onEnable() {
               PluginDescriptionFile pdfFile = getDescription();
                logger.info((new StringBuilder(String.valueOf(pdfFile.getName()))).append("Version").append(pdfFile.getVersion()).append("Has Been Enabled!").toString());
                getConfig().options().copyDefaults(true);
                saveConfig();
            }

            public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]) {
                Player player = (Player)sender;
                if (player.hasPermission("needtown.use")){
                  if (cmd.getName().equalsIgnoreCase("needtown")) {
                    Bukkit.broadcastMessage((new StringBuilder()).append(ChatColor.GOLD).append(player.getDisplayName()).append(ChatColor.AQUA).append(" would like to be invited to a town! ").append(ChatColor.RED).append("Town owners").append(ChatColor.AQUA).append(" make sure to invite ").append(ChatColor.GOLD).append(player.getDisplayName()).append(ChatColor.AQUA).append("!").toString());
return true;                  
}            
}
              else
              {
                  player.sendMessage(ChatColor.RED + "Error: You do not have permission to do that!");
return true;
              }
              return false;
              }
           
}
				