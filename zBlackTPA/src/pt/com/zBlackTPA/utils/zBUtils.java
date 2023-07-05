package pt.com.zBlackTPA.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

public class zBUtils {
	
	public static void sendSound(Player p, Sound s) {
		p.playSound(p.getLocation(), s, 0.75F, 1.0F);
	}
	
	public static void sendTitle(Player p, String line1, String line2) {
        PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\"" + line1 + "\"}"), 20, 40, 20);
        PacketPlayOutTitle subtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, ChatSerializer.a("{\"text\":\"" + line2 + "\"}"), 20, 40, 20);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(title);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(subtitle);
	}
	
	public static void sendActionBar(Player player, String message) {
	    CraftPlayer p = (CraftPlayer) player;
	    IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
	    PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
	    p.getHandle().playerConnection.sendPacket(ppoc);
	}
	
	public static void ConsoleMsg(String msg) {
		Bukkit.getConsoleSender().sendMessage(msg.replace("&", "§"));
	}
	
	public static String Formatar(Double valor) {
		DecimalFormat d = new DecimalFormat("###,###");
		return d.format(valor);
	}
	
	public static String getPorcentagem(Double atual, Double maximo) {
		DecimalFormat d = new DecimalFormat("###.##");
		return d.format((atual/maximo)*100);
	}
	
	public static Integer getEmptySlots(Player p) {
        PlayerInventory inventory = p.getInventory();
        ItemStack[] cont = inventory.getContents();
        int i = 0;
        for (ItemStack item : cont)
          if (item != null && item.getType() != Material.AIR) {
            i+=64;
          }
        return 2304 - i;
    }
	
	public static Boolean isDouble(String s) {
		String[] a = s.split(" ");
		if(a.length > 1) return false;
		try {
			Double.parseDouble(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	public static List<String> replaceList(List<String> list, String oldChar, String newChar) {
		List<String> out = new ArrayList<>();
		for(String s : list) {
			if(s.contains(oldChar)) out.add(s.replace(oldChar, newChar));
			else out.add(s);
		}
		return out;
	}
	
	@SuppressWarnings("deprecation")
	public static String getItemID(ItemStack item) {
		if (item.getDurability() != 0) {
			return item.getTypeId() + ":" + item.getDurability();
		} else return item.getTypeId() + "";
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack getItemByID(String ID) {
		if (ID.contains(":")) {
			return new ItemStack(Integer.parseInt(ID.split(":")[0]), 1, (short) Integer.parseInt(ID.split(":")[1]));
		} else return new ItemStack(Integer.parseInt(ID));
	}
	
	public static String FormatarChances(Double valor) {
		String valst = valor.toString().replace(".", "!");
		String[] sptt = valst.split("!");
		if(sptt[0].equalsIgnoreCase("0")) return valor.toString();
		if(!sptt[1].equalsIgnoreCase("0")) return valor.toString();
		DecimalFormat d = new DecimalFormat("####");
		return d.format(valor);
	}
}
