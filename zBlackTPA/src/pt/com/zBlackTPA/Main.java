package pt.com.zBlackTPA;

import org.bukkit.plugin.java.JavaPlugin;

import pt.com.zBlackTPA.comands.TpaCMD;
import pt.com.zBlackTPA.comands.TpacceptCMD;
import pt.com.zBlackTPA.comands.TpcancelCMD;
import pt.com.zBlackTPA.comands.TpdenyCMD;
import pt.com.zBlackTPA.methods.Cache;
import pt.com.zBlackTPA.utils.zBUtils;

public class Main extends JavaPlugin {
	private static Main instance;
	public static Cache cache;
	@Override
	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		cache = new Cache();
		loadCmds();
		zBUtils.ConsoleMsg("&7[&bzBlackTPA&7] &aO plugin foi iniciado.");
	}
	
	@Override
	public void onDisable() {
		zBUtils.ConsoleMsg("&7[&bzBlackTPA&7] &cO plugin foi encerrado.");
	}
	
	private void loadCmds() {
 		getCommand("tpa").setExecutor(new TpaCMD());	
 		getCommand("tpaccept").setExecutor(new TpacceptCMD());	
 		getCommand("tpdeny").setExecutor(new TpdenyCMD());	
 		getCommand("tpcancel").setExecutor(new TpcancelCMD());	
	}
	
	public static Main getInstance() {
		return instance;
	}
}
