package rama.ir.commands;

import de.tr7zw.changeme.nbtapi.NBTItem;
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
                for(ItemStack i : player.getInventory().getContents()){
                    if(i != null){
                        extractLore(i);
                        removeNBTTags(i);
                    }
                }
            }
        }
        if (!all) {
            for(ItemStack i : p.getInventory().getContents()){
                if(i != null){
                    extractLore(i);
                    removeNBTTags(i);
                }
            }
        }
    }

    public List<String> loreToRemove(ItemStack i) {
        String rarity = null;
        NBTItem nbtItem = new NBTItem(i);
        if (nbtItem.hasKey("Rarity")) {
            rarity = nbtItem.getString("Rarity");
        }
        FileConfiguration rarityFile = plugin.getRarityFile();
        List<String> lore_format = plugin.getConfig().getStringList("Config.lore-format");
        List<String> loreToRemove = new ArrayList<>();
        for (String line : lore_format) {
            if (line.equals("{item-lore}")) {
                continue;
            }
            if (line.contains("{rarity-prefix}")) {
                for (String rarity_integer : rarityFile.getConfigurationSection("Rarities").getKeys(false)) {
                    if (rarityFile.getString("Rarities." + rarity_integer + ".Name").equals(rarity)) {
                        rarity = rarity_integer;
                    }
                }
                String rarityPrefix = rarityFile.getString("Rarities." + rarity + ".Prefix");
                if (rarityPrefix != null) {
                    loreToRemove.add(ChatColor.translateAlternateColorCodes('&', line.replace("{rarity-prefix}", rarityPrefix)));
                }
            } else {
                loreToRemove.add(line);
            }
        }
        if(plugin.getConfig().getBoolean("debug-mode")) {
            Bukkit.broadcastMessage("LORE TO REMOVE");
            Bukkit.broadcastMessage("---------------");
            for (String line : loreToRemove) {
                Bukkit.broadcastMessage(line);
            }
            Bukkit.broadcastMessage("---------------");
        }
        return loreToRemove;
    }

    public void extractLore(ItemStack i) {
        if (i.getItemMeta().hasLore()) {
            List<String> item_lore = i.getItemMeta().getLore();
            item_lore.removeAll(loreToRemove(i));
            ItemMeta item_meta = i.getItemMeta();
            item_meta.setLore(item_lore);
            i.setItemMeta(item_meta);
        }
    }

    public void removeNBTTags(ItemStack i){
        NBTItem nbtItem = new NBTItem(i);
        if (nbtItem.hasKey("Rarity")) {
            nbtItem.removeKey("Rarity");
            i.setItemMeta(nbtItem.getItem().getItemMeta());
        }
        if (nbtItem.hasKey("CustomItem")) {
            nbtItem.removeKey("CustomItem");
            i.setItemMeta(nbtItem.getItem().getItemMeta());
        }
    }
}

    /*private void removeRarity(FileConfiguration rarityFile, Player player) {
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
                if(i.getItemMeta().getLore() != null) {
                    List<String> itemLore = i.getItemMeta().getLore();
                    itemLore.removeAll(loreToRemove);
                    ItemMeta im = i.getItemMeta();
                    im.setLore(itemLore);
                    i.setItemMeta(im);
                }
            }
        }

    */
