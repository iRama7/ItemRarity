package rama.ir.commands;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rama.ir.ItemRarity;

import java.util.ArrayList;
import java.util.List;

public class UpdateRarity {

    public ItemRarity plugin;

    public UpdateRarity(ItemRarity plugin) {
        this.plugin = plugin;
    }

    public void updateRarity(Boolean all, Player p) {
        FileConfiguration rarityFile = plugin.getRarityFile();
        if (all) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                removeRarity(rarityFile, player);
            }
        }
        if (!all) {
            removeRarity(rarityFile, p);
        }
    }

    private void removeRarity(FileConfiguration rarityFile, Player player) {
        String rarity = null;
        for (ItemStack i : player.getInventory().getContents()) {
            if (i != null) {
                NBTItem nbtItem = new NBTItem(i);
                if (nbtItem.hasKey("Rarity")) {
                    rarity = nbtItem.getString("Rarity");
                    nbtItem.removeKey("Rarity");
                    i.setItemMeta(nbtItem.getItem().getItemMeta());
                }
                if (nbtItem.hasKey("CustomItem")) {
                    nbtItem.removeKey("CustomItem");
                    i.setItemMeta(nbtItem.getItem().getItemMeta());
                }
                List<String> lore_format = plugin.getConfig().getStringList("Config.lore-format");
                List<String> loreToRemove = new ArrayList<>();
                for(String line : lore_format){
                    if(!line.equals("{item-lore}")){
                        if(line.contains("{rarity-prefix}")){

                            for (String rarity_integer : rarityFile.getConfigurationSection("Rarities").getKeys(false)) {
                                if (rarityFile.getString("Rarities." + rarity_integer + ".Name").equals(rarity)) {
                                    rarity = rarity_integer;
                                }
                            }
                            String rarityPrefix = rarityFile.getString("Rarities." + rarity + ".Prefix");
                            if(rarityPrefix != null){
                                loreToRemove.add(ChatColor.translateAlternateColorCodes('&', line.replace("{rarity-prefix}", rarityPrefix)));
                            }
                        }else{
                            loreToRemove.add(line);
                        }
                    }
                }
                List<String> itemLore = i.getItemMeta().getLore();
                itemLore.removeAll(loreToRemove);
                ItemMeta im = i.getItemMeta();
                im.setLore(itemLore);
                i.setItemMeta(im);
            }
        }

    }
}
