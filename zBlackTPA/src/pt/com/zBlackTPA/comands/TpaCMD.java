package pt.com.zBlackTPA.comands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import pt.com.zBlackTPA.Main;
import pt.com.zBlackTPA.methods.TpaActive;

public class TpaCMD implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender s, Command c, String arg2, String[] a) {
		if (!(s instanceof Player)) {
			s.sendMessage("§cComando apenas para jogadores!");
			return true;
		}
		Player p = (Player)s;
		if (a.length != 1) {
			p.sendMessage(Main.cache.mensagensCFG.get("tpa-incorreto"));
			return true;
		}
		if (a[0].equalsIgnoreCase(p.getName())) {
			p.sendMessage(Main.cache.mensagensCFG.get("player-voce"));
			return true;
		}
		OfflinePlayer ofp = Bukkit.getOfflinePlayer(a[0]);
		if(!ofp.isOnline()) {
			p.sendMessage(Main.cache.mensagensCFG.get("player-offline"));
			return true;
		}
		
		if (Main.cache.playerTPA.containsKey(p)) {
			p.sendMessage(Main.cache.mensagensCFG.get("tpa-pendente"));
			return true;
		}
		
		Player target = ofp.getPlayer();
		if (!TpaActive.isAtivo(target)) {
			p.sendMessage(Main.cache.mensagensCFG.get("tpa-desativado"));
			return true;
		}
		Main.cache.playerTPA.put(p, target);
		List<Player> targetPedidos = (Main.cache.allTPA.containsKey(target)) ? Main.cache.allTPA.get(target) : new ArrayList<>();
		targetPedidos.add(p);
		Main.cache.allTPA.put(target, targetPedidos);
		Main.cache.tpaPedidoMsg(p, target);
		Main.cache.tpaEnviadoMsg(p, target);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if(Main.cache.playerTPA.containsKey(p)) {
					Main.cache.playerTPA.remove(p);
					List<Player> targetPedidos = (Main.cache.allTPA.containsKey(target)) ? Main.cache.allTPA.get(target) : new ArrayList<>();
					targetPedidos.remove(p);
					Main.cache.allTPA.put(target, targetPedidos);
					
					p.sendMessage(Main.cache.mensagensCFG.get("tpa-expirou").replace("%target%", target.getName()));
				}
				cancel();
			}
		}.runTaskTimer(Main.getInstance(), 20L * Main.cache.pedidoTime, 20L * Main.cache.pedidoTime);
		
		return true;
	}
}
