package pt.com.zBlackTPA.comands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pt.com.zBlackTPA.Main;

public class TpdenyCMD implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender s, Command c, String arg2, String[] a) {
		if (!(s instanceof Player)) {
			s.sendMessage("§cComando apenas para jogadores!");
			return true;
		}
		Player p = (Player)s;
		if(a.length != 1) {
			p.sendMessage(Main.cache.mensagensCFG.get("tpdeny-incorreto"));
			return true;
		}
		OfflinePlayer ofp = Bukkit.getOfflinePlayer(a[0]);
		if (!ofp.isOnline() || !Main.cache.allTPA.containsKey(p) || !Main.cache.allTPA.get(p).contains(ofp.getPlayer())) {
			p.sendMessage(Main.cache.mensagensCFG.get("nao-solicitado"));
			return true;
		}
		
		Player target = ofp.getPlayer();
		
		Main.cache.playerTPA.remove(target);
		List<Player> pPedidos = (Main.cache.allTPA.containsKey(p)) ? Main.cache.allTPA.get(p) : new ArrayList<>();
		pPedidos.remove(target);
		Main.cache.allTPA.put(p, pPedidos);
		
		p.sendMessage(Main.cache.mensagensCFG.get("tpdeny-sucesso").replace("%player%", target.getName()));
		target.sendMessage(Main.cache.mensagensCFG.get("tpdeny-sucesso1").replace("%player%", p.getName()));
		return true;
	}
}
