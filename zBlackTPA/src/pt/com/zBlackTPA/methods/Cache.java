package pt.com.zBlackTPA.methods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import pt.com.zBlackTPA.Main;
import pt.com.zBlackTPA.utils.UltimateFancy;
import pt.com.zBlackTPA.utils.zBUtils;

public class Cache {
	public List<String> tpaDesativado;
	public Map<Player, List<Player>> allTPA;
	public Map<Player, Player> playerTPA;
	
	public Map<String, String> mensagensCFG;
	
	public Integer pedidoTime;
	
	private List<String> pedidoMsg;
	private List<String> enviadoMsg;
	
	private String accText;
	private String recText;
	private String canText;
	private String accHover;
	private String recHover;
	private String canHover;
	
	public Cache() {
		tpaDesativado = new ArrayList<>();
		allTPA = new HashMap<>();
		playerTPA = new HashMap<>();
		mensagensCFG = new HashMap<>();
		pedidoTime = Main.getInstance().getConfig().getInt("tempo-pedido");
		loadMessages();
		loadBotoes();
	}
	
	private void loadMessages() {
		FileConfiguration cfg = Main.getInstance().getConfig();
		for(String key : cfg.getConfigurationSection("Mensagens").getKeys(false)) {
			if(key.equals("tpa-pedido")) {
				pedidoMsg = zBUtils.replaceList(cfg.getStringList("Mensagens." + key), "&", "§");
				continue;
			}
			if(key.equals("tpa-enviado")) {
				enviadoMsg = zBUtils.replaceList(cfg.getStringList("Mensagens." + key), "&", "§");
				continue;
			}
			mensagensCFG.put(key, cfg.getString("Mensagens." + key).replace("&", "§"));
		}
	}
	
	private void loadBotoes() {
		FileConfiguration cfg = Main.getInstance().getConfig();
		accText = cfg.getString("Botoes.Aceitar.texto").replace("&", "§");
		recText = cfg.getString("Botoes.Recusar.texto").replace("&", "§");
		canText = cfg.getString("Botoes.Cancelar.texto").replace("&", "§");
		accHover = cfg.getString("Botoes.Aceitar.hover").replace("&", "§");
		recHover = cfg.getString("Botoes.Recusar.hover").replace("&", "§");
		canHover = cfg.getString("Botoes.Cancelar.hover").replace("&", "§");
	}
	
	public void tpaPedidoMsg(Player p, Player target) {
		for(String s : pedidoMsg) {
			UltimateFancy msg = new UltimateFancy();
			if(s.equals("")) {
				target.sendMessage("");
				continue;
			}
			String[] s1 = s.split(" ");
			for(String s2 : s1) {
				if(!s2.contains("%aceitar%") && !s2.contains("%recusar%")) {
					msg.text(s2.replace("%player%", p.getName()) + " ");
					continue;
				}
				msg.next();
				if(s2.contains("%aceitar%")) msg.text(accText + " ").hoverShowText(accHover).clickRunCmd("/tpaccept " + p.getName()).next();
				if(s2.contains("%recusar%")) msg.text(recText + " ").hoverShowText(recHover).clickRunCmd("/tpdeny " + p.getName()).next();
			}
			msg.send(target);
		}
	}
	
	public void tpaEnviadoMsg(Player p, Player target) {
		for(String s : enviadoMsg) {
			UltimateFancy msg = new UltimateFancy();
			if(s.equals("")) {
				p.sendMessage("");
				continue;
			}
			String[] s1 = s.split(" ");
			for(String s2 : s1) {
				if(!s2.contains("%cancelar%")) {
					msg.text(s2.replace("%player%", target.getName()) + " ");
					continue;
				}
				msg.next();
				if(s2.contains("%cancelar%")) msg.text(canText + " ").hoverShowText(canHover).clickRunCmd("/tpcancel").next();
			}
			msg.send(p);
		}
	}
}
