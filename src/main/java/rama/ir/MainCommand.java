package rama.ir;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import rama.ir.raritymain.Rarity;
import rama.ir.raritymain.RarityMain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainCommand implements TabExecutor {

    private final ItemRarity plugin;
    private final RarityMain rarityMain;

    public MainCommand(ItemRarity plugin, RarityMain rarityMain){
        this.plugin = plugin;
        this.rarityMain = rarityMain;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length == 1 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("ir.admin")){
            plugin.stopRarityMain();
            try {
                plugin.reloadRarities();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            plugin.initializeRarityMain();
            sender.sendMessage(rarityMain.colorized("&aReloaded rarities."));
        }

        if(args.length == 1 && args[0].equalsIgnoreCase("updateItems") && sender.hasPermission("ir.admin")){
            plugin.getRarityMain().setUpdating();
            sender.sendMessage(rarityMain.colorized("&aUpdating items..."));
        }

        if(args.length == 2 && args[0].equalsIgnoreCase("addCustomItem") && sender.hasPermission("ir.admin")){
            String rarity = args[1];
            ItemStack item = plugin.getRarityMain().removeRarity(((Player) sender).getInventory().getItemInMainHand());
            if(item != null && item.getType() != Material.AIR){
                try {
                    plugin.getRarityMain().addCustomItemStack(item, plugin.getRarityMain().getRarityByID(rarity));
                } catch (IOException | InvalidConfigurationException e) {
                    throw new RuntimeException(e);
                }
            }
            sender.sendMessage(rarityMain.colorized("&aAdded custom item."));
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
                commands.add("updateItems");
                commands.add("addCustomItem");
                StringUtil.copyPartialMatches(args[0], commands, completions);
            }
            if(args.length == 2 && args[0].equalsIgnoreCase("addCustomItem")){
                for(Rarity r : rarityMain.getRarities()){
                    if(r.getIdentifier().equals("null")){
                        continue;
                    }
                    commands.add(r.getIdentifier());
                }
                StringUtil.copyPartialMatches(args[1], commands, completions);
            }
        }
        Collections.sort(completions);
        return completions;
    }

}
