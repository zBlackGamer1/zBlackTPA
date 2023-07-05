package pt.com.zBlackTPA.methods;

import org.bukkit.entity.Player;

import pt.com.zBlackTPA.Main;

public class TpaActive {
	public static void set(Player player, Boolean bool) {
		set(player.getName(), bool);
	}
	
	public static void set(String playerName, Boolean bool) {
		if(bool) Main.cache.tpaDesativado.add(playerName);
		else if(Main.cache.tpaDesativado.contains(playerName)) Main.cache.tpaDesativado.remove(playerName);
	}
	
	public static Boolean isAtivo(Player p) {
		return !Main.cache.tpaDesativado.contains(p.getName());
	}
}
