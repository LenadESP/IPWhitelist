package net.craftminecraft.bukkit.ipwhitelist;

import java.io.IOException;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.ChatPaginator;
import org.bukkit.util.ChatPaginator.ChatPage;

import com.google.common.reflect.ClassPath;

public class IPWhitelist extends JavaPlugin {
	public void onEnable() {
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
	}

	public void onDisable() { }

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!command.getName().equalsIgnoreCase("ipwhitelist")) {
			return false;
		}
		if (!sender.hasPermission("ipwhitelist.setup")) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to perform this action.");
			return true;
		}
		if (args.length == 0) {
			sender.sendMessage(getTag() + ChatColor.AQUA + "Commands : ");
			sender.sendMessage(ChatColor.AQUA + "/ipwhitelist list [page] - List whitelisted IPs");
			sender.sendMessage(ChatColor.AQUA + "/ipwhitelist addip <ip> - Add IP to whitelist");
			sender.sendMessage(ChatColor.AQUA + "/ipwhitelist remip <ip> - Removes IP to whitelist");
			sender.sendMessage(ChatColor.AQUA + "/ipwhitelist reload - Reload whitelist");
			return true;
		}

		if (args[0].equalsIgnoreCase("list")) {
			sender.sendMessage(getTag() + ChatColor.AQUA + "Whitelisted IPs :");
			StringBuffer iplistbuff = new StringBuffer();
			for (String ip : getConfig().getStringList("whitelist")) {
				iplistbuff.append(ChatColor.AQUA + ip + "\n");
			}
			// Delete last newline if there is one.
			if (iplistbuff.length() > 0) {
				iplistbuff.deleteCharAt(iplistbuff.length()-1);
			}
			String iplist = iplistbuff.toString();
			ChatPage page;
			if (args.length > 1) {
				page = ChatPaginator.paginate(iplist, Integer.parseInt(args[1]), ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH, ChatPaginator.CLOSED_CHAT_PAGE_HEIGHT-2);
				sender.sendMessage(page.getLines());
			} else {
				page = ChatPaginator.paginate(iplist, 1, ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH, ChatPaginator.CLOSED_CHAT_PAGE_HEIGHT-2);
				sender.sendMessage(page.getLines());
			}
			sender.sendMessage(ChatColor.AQUA + "Page " + page.getPageNumber() + "/" + page.getTotalPages() + ".");
			return true;
		}
		if (args[0].equalsIgnoreCase("addip")) {
			if (args.length < 2) {
				sender.sendMessage(getTag() + ChatColor.AQUA + "Command usage : ");
				sender.sendMessage(ChatColor.AQUA + "/ipwhitelist addip <ip>");
				return true;
			}
			List<String> whitelist = getConfig().getStringList("whitelist");
			if (!whitelist.contains(args[1])) {
				whitelist.add(args[1]);
				getConfig().set("whitelist", whitelist);
				this.saveConfig();
				sender.sendMessage(getTag() + ChatColor.AQUA + "Successfully whitelisted IP " + args[1] + "!");
				return true;
			}
			sender.sendMessage(getTag() + ChatColor.AQUA + "IP " + args[1] + " was already whitelisted!");
			return true;
		}
		if (args[0].equalsIgnoreCase("remip")) {
			if (args.length < 2) {
				sender.sendMessage(getTag() + ChatColor.AQUA + "Command usage : ");
				sender.sendMessage(ChatColor.AQUA + "/ipwhitelist remip <ip>");
				return true;
			}
			List<String> whitelist = getConfig().getStringList("whitelist");
			if (whitelist.remove(args[1])) {
				getConfig().set("whitelist", whitelist);
				this.saveConfig();
				sender.sendMessage(getTag() + ChatColor.AQUA + "Successfully unwhitelisted IP " + args[1] + "!");
				return true;
			}
			sender.sendMessage(getTag() + ChatColor.AQUA + "IP " + args[1] + " was not whitelisted!");
			return true;
		}
		if (args[0].equalsIgnoreCase("reload")) {
			this.reloadConfig();
			sender.sendMessage(getTag() + ChatColor.AQUA + "Successfully reloaded config!");
			return true;
		}
		sender.sendMessage(getTag() + ChatColor.AQUA + "Commands : ");
		sender.sendMessage(ChatColor.AQUA + "/ipwhitelist list [page] - List whitelisted IPs");
		sender.sendMessage(ChatColor.AQUA + "/ipwhitelist addip <ip> - Add IP to whitelist");
		sender.sendMessage(ChatColor.AQUA + "/ipwhitelist remip <ip> - Removes IP to whitelist");
		sender.sendMessage(ChatColor.AQUA + "/ipwhitelist reload - Reload whitelist");
		return true;
	}
	
	public String getTag() {
		return ChatColor.ITALIC.toString() + ChatColor.GREEN + "[" + ChatColor.AQUA + this.getName() + ChatColor.GREEN + "] " + ChatColor.RESET;
	}
}