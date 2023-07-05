package pt.com.zBlackTPA.comands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pt.com.zBlackTPA.Main;

public class TpcancelCMD implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command c, String arg2, String[] a) {
		if (!(s instanceof Player)) {
			s.sendMessage("§cComando apenas para jogadores!");
			return true;
		}
		Player p = (Player)s;
		if (!Main.cache.playerTPA.containsKey(p)) {
			p.sendMessage(Main.cache.mensagensCFG.get("tpcancel-nao-tem"));
			return true;
		}
		Player target = Main.cache.playerTPA.get(p);
		Main.cache.playerTPA.remove(p);
		List<Player> Pedidos = (Main.cache.allTPA.containsKey(target)) ? Main.cache.allTPA.get(target) : new ArrayList<>();
		Pedidos.remove(p);
		Main.cache.allTPA.put(target, Pedidos);
		p.sendMessage(Main.cache.mensagensCFG.get("tpcancel").replace("%player%", target.getName()));
		target.sendMessage(Main.cache.mensagensCFG.get("tpcancel1").replace("%player%", p.getName()));
		return true;
	}
}
