package rama.ir.raritysystem;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeWrapper;
import rama.ir.ItemRarity;
import rama.ir.api.ApplyRarityEvent;
import rama.ir.itemhandler.Potion;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.Material.ENCHANTED_BOOK;
import static org.bukkit.Material.POTION;


public class RarityMain {

    public ItemRarity plugin;

    String potion_effect_name;

    Object MyPotion(String potion_effect_name) {
        this.potion_effect_name = potion_effect_name;
        return null;
    }


    public RarityMain(ItemRarity plugin){
        this.plugin = plugin;
    }

    public void addRarity(ItemStack i, String rarity, Boolean custom){
        NBTItem nbti = new NBTItem(i);

        if(nbti.hasKey("Rarity")) {
            return;
        }
        nbti.setString("Rarity", rarity);
        if(custom){
            nbti.setString("CustomItem", "true");
        }
        if(plugin.getConfig().getBoolean("Config.enable-glow")){
            for(String rarity_integer : plugin.getRarityFile().getConfigurationSection("Rarities").getKeys(false)){
                if(plugin.getRarityFile().getString("Rarities." + rarity_integer + ".Name").equals(rarity)){
                    String glowColor = plugin.getRarityFile().getString("Rarities." + rarity_integer + ".Glow-color");
                    nbti.setString("Glow", glowColor);
                }
            }
        }
        FileConfiguration rarityFile = plugin.getRarityFile();
        ItemMeta meta = nbti.getItem().getItemMeta();
        List<String> lore_format = plugin.getConfig().getStringList("Config.lore-format");
        List<String> lore = new ArrayList<>();
        for(String line : lore_format){
            if(line.equals("{item-lore}")){
                if(meta.hasLore()){
                    lore.addAll(meta.getLore());
                }
            }else if(line.contains("{rarity-prefix}")){
                for(String rarity_integer : rarityFile.getConfigurationSection("Rarities").getKeys(false)){
                    if(rarityFile.getString("Rarities."+ rarity_integer + ".Name").equals(rarity)){
                        rarity = rarity_integer;
                    }
                }
                if(rarity == null){
                    continue;
                }
                lore.add(line.replace("{rarity-prefix}", rarityFile.getString("Rarities." + rarity + ".Prefix")));
            }else{
                lore.add(line);
            }
        }
        for(String line : lore){
            lore.set(lore.indexOf(line), hex(line));
        }
        meta.setLore(lore);
        i.setItemMeta(meta);
        String finalRarity = rarity;
        Bukkit.getScheduler().runTask(plugin, () ->{
            ApplyRarityEvent event = new ApplyRarityEvent(rarityFile.getString("Rarities." + finalRarity + ".Prefix"), custom, i);
            plugin.triggerApplyRarityEvent(event);
        });
    }

    public String getRarity(ItemStack is){
        FileConfiguration rarityFile = plugin.getRarityFile();
        ItemMeta meta = is.getItemMeta();
        if(meta.hasDisplayName() || meta.hasLore()){ //Joining the Other case
            if(meta.hasDisplayName() && meta.hasLore()){
                for(String i : rarityFile.getConfigurationSection("Items.Other").getKeys(false)){
                    String raritiesName = ChatColor.translateAlternateColorCodes('&', rarityFile.getString("Items.Other." + i + ".Name"));
                    String raritiesLore = ChatColor.translateAlternateColorCodes('&', rarityFile.getString("Items.Other." + i + ".Lore"));
                    Material raritiesMaterial = Material.getMaterial(rarityFile.getString("Items.Other." + i + ".Material"));
                    if(meta.getDisplayName().equals(raritiesName) && meta.getLore().contains(raritiesLore) && is.getType().equals(raritiesMaterial)){
                        String rarity = rarityFile.getString("Items.Other." + i + ".Rarity");
                        addRarity(is, rarity, true);
                        return rarity;
                    }
                }
            }else if(meta.hasDisplayName()){
                for(String i : rarityFile.getConfigurationSection("Items.Other").getKeys(false)){
                    String raritiesName = ChatColor.translateAlternateColorCodes('&', rarityFile.getString("Items.Other." + i + ".Name"));
                    Material raritiesMaterial = Material.getMaterial(rarityFile.getString("Items.Other." + i + ".Material"));
                    if(meta.getDisplayName().equals(raritiesName) && is.getType().equals(raritiesMaterial)){
                        String rarity = rarityFile.getString("Items.Other." + i + ".Rarity");
                        addRarity(is, rarity, true);
                        return rarity;
                    }
                }
            }else if(meta.hasLore()){
                for(String i : rarityFile.getConfigurationSection("Items.Other").getKeys(false)){
                    String raritiesLore = ChatColor.translateAlternateColorCodes('&', rarityFile.getString("Items.Other." + i + ".Lore"));
                    Material raritiesMaterial = Material.getMaterial(rarityFile.getString("Items.Other." + i + ".Material"));
                    if(meta.getLore().contains(raritiesLore) && is.getType().equals(raritiesMaterial)){
                        String rarity = rarityFile.getString("Items.Other." + i + ".Rarity");
                        addRarity(is, rarity, true);
                        return rarity;
                    }
                }
            }
        }else{ //Joining the default case
            List<String> valid_rarities = new ArrayList<>();
            for(String rarity : rarityFile.getConfigurationSection("Items").getKeys(false)) {
                if (rarity.equals("Other")) continue;

                List<String> materials = rarityFile.getStringList("Items." + rarity + ".list");
                //search for custom model data or enchantment
                for(String m : materials){
                    if(m.contains(":") && !m.contains("ENCHANTED_BOOK") && !m.contains("POTION")){
                        String[] parts = m.split(":");
                        String material_name = parts[0];
                        if(Character.isDigit(parts[1].toCharArray()[0])) {
                            int custom_model_data = Integer.parseInt(parts[1]);
                            NBTItem nbtItem = new NBTItem(is);
                            if (is.getType().toString().equals(material_name) && nbtItem.getInteger("CustomModelData") == custom_model_data) {
                                valid_rarities.add(rarity);
                            }
                        }else{
                            String[] parts1 = parts[1].split(" ");
                            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(parts1[0]));
                            int level = Integer.parseInt(parts1[1]);
                            if(is.getType().toString().equals(material_name) && is.getEnchantmentLevel(enchantment) == level){
                                valid_rarities.add(rarity);
                            }
                        }
                    }
                }
                //search for potions
                for(String m : materials){
                    if(m.contains("POTION")){
                        String[] parts1 = m.split("POTION:");
                        String potion_string = parts1[1];
                        String[] parts2 = potion_string.split(" ");
                        String potion_effect_name = parts2[0];
                        String[] parts3 = parts2[1].split(":");
                        Boolean extended = Boolean.valueOf(parts3[0]);
                        Boolean upgraded = Boolean.valueOf(parts3[1]);
                        Potion configPotion = new Potion();
                        configPotion.setPotion_effect_name(potion_effect_name);
                        configPotion.setExtended(extended);
                        configPotion.setUpgraded(upgraded);
                        if(is.getType().equals(POTION)) {
                            Potion potion = parsePotion(is);
                            if(potionsAreEqual(configPotion, potion)){
                                valid_rarities.add(rarity);
                            }
                        }
                    }
                }

                //search for enchanted_book
                for(String m : materials){
                    if(m.contains("ENCHANTED_BOOK")){
                        String[] parts1 = m.split("ENCHANTED_BOOK:");
                        String Enchantment_string = parts1[1];
                        String[] parts2 = Enchantment_string.split(" ");
                        String Enchantment_name = parts2[0];
                        int Enchantment_level = Integer.parseInt(parts2[1]);
                        org.bukkit.enchantments.Enchantment Bukkit_Ench = new EnchantmentWrapper(Enchantment_name);
                        if(is.getType().equals(ENCHANTED_BOOK)){
                            EnchantmentStorageMeta im = (EnchantmentStorageMeta) is.getItemMeta();
                            if(im.getStoredEnchants().containsKey(Bukkit_Ench) && im.getStoredEnchantLevel(Bukkit_Ench) == Enchantment_level){
                                valid_rarities.add(rarity);
                            }
                        }
                    }
                }
                if (materials.contains(is.getType().toString()) || materials.contains("DEFAULT")) { //create a list with valid rarities for the item
                    valid_rarities.add(rarity);
                }
            }


                String mostPriorityRarity = null;
                int previousPriority = 0;
                int current_priority;
                if(valid_rarities.size() == 0){
                    return null;
                }
                if(valid_rarities.size() == 1){
                    mostPriorityRarity = valid_rarities.get(0);
                }else {
                    for (String valid_rarity : valid_rarities) { //get rarity with the highest priority
                        current_priority = rarityFile.getInt("Items." + valid_rarity + ".priority");
                        if (previousPriority == 0) {
                            previousPriority = current_priority;
                        } else {
                            if (current_priority < previousPriority) {
                                mostPriorityRarity = valid_rarity;
                                previousPriority = current_priority;
                            }
                        }
                    }
                }

                if(plugin.getConfig().getBoolean("Config.debug-mode")){
                    plugin.getLogger().info("Valid Rarities: " + valid_rarities);
                    plugin.getLogger().info("Most Priority Rarity: " + mostPriorityRarity);
                }

                addRarity(is, mostPriorityRarity, false);
                if(plugin.getConfig().getBoolean("Config.debug-mode")) {
                    plugin.getLogger().warning("[IR-DEBUG] Adding " + mostPriorityRarity + " rarity to " + is.getType().toString() + " item.");
                }
                return mostPriorityRarity;
            }
            return null;
        }

    public static String hex(String message) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder("");
            for (char c : ch) {
                builder.append("&" + c);
            }

            message = message.replace(hexCode, builder.toString());
            matcher = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public Potion parsePotion(ItemStack is){
        Potion potion = new Potion();
        PotionMeta pm = (PotionMeta) is.getItemMeta();
        PotionData potionData = pm.getBasePotionData();
        String potion_effect_name = potionData.getType().name();
        potion.setExtended(false);
        potion.setUpgraded(false);
        potion.setPotion_effect_name(potion_effect_name);
        if(potionData.isExtended()){
            potion.setExtended(true);
        }
        if(potionData.isUpgraded()){
            potion.setUpgraded(true);
        }
        return potion;
    }

    public Boolean potionsAreEqual(Potion p1, Potion p2){
        if(p1.getExtended().equals(p2.getExtended()) && p1.getUpgraded().equals(p2.getUpgraded()) && p1.getPotion_effect_name().equalsIgnoreCase(p2.getPotion_effect_name())) {
            return true;
        }else{
            return false;
        }
    }
}
