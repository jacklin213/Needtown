package me.jacklin213.needtown;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class onLoadedTask implements Runnable {
	
	public static NeedTown plugin;
	Towny towny;
	
	public onLoadedTask(NeedTown instance) {
        super();
        plugin = instance;
        this.towny = plugin.getTowny();
	}
	
	@Override
	public void run() {

		plugin.getLogger().info("-*****************************************************-");
		for (Resident resident: TownyUniverse.getDataSource().getResidents()) {
			plugin.getLogger().info("Resident: " + resident.getName());
			plugin.getLogger().info("Total Money: " + resident.getHoldingFormattedBalance());
		}
		plugin.getLogger().info("-*****************************************************-");
		
	}
	
	/*	How to hook into Towny for need town.
	 *  List<Resident> list = TownyUniverse.getDataSource().getResidents();
		if (list.contains(player)){
			
		}*/
	
}