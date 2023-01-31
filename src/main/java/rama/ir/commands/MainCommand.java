package rama.ir.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import rama.ir.ItemRarity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainCommand implements TabExecutor {

    public ItemRarity plugin;

    public MainCommand(ItemRarity plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //RELOAD
        if (args.length == 1 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("ir.admin")) {
            plugin.reloadConfig();
            try {
                plugin.reloadRarities();
            } catch (IOException | InvalidConfigurationException e) {
                throw new RuntimeException(e);
            }
            sender.sendMessage(ChatColor.GREEN + "ItemRarity has been reloaded!");
        }
        //RELOAD
        else if(sender instanceof Player){
            if(args.length == 1) {
                if (args[0].equalsIgnoreCase("update")) {
                    Player player = (Player) sender;
                    if (player.hasPermission("ir.admin")) {
                        player.sendMessage("Correct usage: /ir update <all|player name>");
                    }
                }
            }else if(args.length == 2){
                if(args[0].equalsIgnoreCase("update")){
                    Player player = (Player) sender;
                    if(player.hasPermission("ir.admin")){
                        if(args[1].equalsIgnoreCase("all")){
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eUpdating rarities for all players..."));
                            UpdateRarity updateRarity = new UpdateRarity(plugin);
                            updateRarity.updateRarity(true, null);
                        }else{
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eUpdating rarities for player &7" + args[1] + "&e..."));
                            UpdateRarity updateRarity = new UpdateRarity(plugin);
                            updateRarity.updateRarity(false, Bukkit.getPlayer(args[1]));
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        if (sender.hasPermission("ir.admin")) {
            if (args.length == 1) {
                commands.add("reload");
                commands.add("update <all|player name>");
                StringUtil.copyPartialMatches(args[0], commands, completions);
            } else if (args.length == 2) {
                if (args[0].equals("update")) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        commands.add(p.getName());
                        commands.add("all");
                        StringUtil.copyPartialMatches(args[1], commands, completions);
                    }
                }
            }
        }
            Collections.sort(completions);
            return completions;
    }
}
