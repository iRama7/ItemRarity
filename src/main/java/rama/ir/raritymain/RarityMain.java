package rama.ir.raritymain;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionData;
import org.bukkit.scheduler.BukkitScheduler;
import rama.ir.ItemRarity;
import rama.ir.NBTMain;
import rama.ir.util.CustomModelDataItem;
import rama.ir.util.EnchantedBook;
import rama.ir.util.Potion;
import rama.ir.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RarityMain {

    public RarityMain(Plugin plugin, FileConfiguration rarityFile, ItemRarity ir){
        this.plugin = plugin;
        this.rarityFile = rarityFile;
        this.ir = ir;
        rarityList.add(nullRarity);
        util = new Util(ir);
    }

    private final Plugin plugin;
    private final NBTMain NBT = new NBTMain();
    private final List<Rarity> rarityList = new ArrayList<>();
    private final FileConfiguration rarityFile;
    private final ItemRarity ir;
    private static final Rarity nullRarity = new Rarity("null", "", 0);
    private Boolean updating = false;
    private final Util util;


    public Rarity getMostWeightRarity(ItemStack item){
        Rarity mostWeightRarity = new Rarity(null, null, 0);

        for(Rarity r : rarityList){
            if(r.contains(item) && r.getWeight() > mostWeightRarity.getWeight()){
                mostWeightRarity = r;
            }
        }

        if(mostWeightRarity.getIdentifier() == null){
            mostWeightRarity = nullRarity;
            Bukkit.getLogger().info("null rarity for " + item.getType());
        }

        return mostWeightRarity;

    }

    public Rarity getRarity(ItemStack item){

        Rarity rarity = null;

        String identifier = NBT.getNBT(item);
        if(identifier != null){
            for(Rarity r : rarityList){
                if(r.getIdentifier().equals(identifier)){
                    rarity = r;
                    break;
                }
            }
        }

        if(updating){
            rarity = null;
        }

        return rarity;
    }


    public void setRarity(ItemStack item, Rarity rarity){ //TODO


        if(getRarity(item) != null){ // remove if present
            removeRarity(item);
        }

        NBT.addNBT(rarity.getIdentifier(), item);

        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setLore(buildLore(item));

        item.setItemMeta(itemMeta);


        Bukkit.getLogger().info("Applying " + rarity.getIdentifier() + " to " + item.getType().toString());


    }

    public void startItemStackChecker(long checkerTime){
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimerAsynchronously(plugin, () -> {
            for(Player player : Bukkit.getOnlinePlayers()){
                for(ItemStack item : player.getInventory().getContents()){
                    if(item != null && !item.getType().equals(Material.AIR) && getRarity(item) == null && !ir.getUtil().isPlayerExcluded(player)){
                        queryItem(item);
                    }
                }
            }
        }, checkerTime, checkerTime);
    }

    public void queryItem(ItemStack item){
        if(item == null){
            return;
        }
        Rarity r = getMostWeightRarity(item);
        setRarity(item, r);
    }

    public void loadRarities(){
        int count = 0;
        for(String i : rarityFile.getConfigurationSection("Rarities").getKeys(false)){
            String identifier = rarityFile.getString("Rarities." + i + ".identifier");
            String name = rarityFile.getString("Rarities." + i + ".name");
            int weight = rarityFile.getInt("Rarities." + i + ".weight");
            Rarity rarity = new Rarity(identifier, name, weight);
            rarityList.add(rarity);
            loadItems(rarity);
            count++;
        }
        ir.logger("&eLoaded &a" + count + " &erarities");
    }

    public void loadItems(Rarity rarity){
        for(String s : rarityFile.getStringList("Items." + rarity.getIdentifier() + ".list")){
            switch(s.split(":")[0]){
                case "DEFAULT":
                    for(Material material : Material.values()){
                        rarity.addMaterial(material);
                    }
                    break;
                case "ENCHANTED_BOOK":
                    rarity.addEnchantedBook(new EnchantedBook(s));
                    break;
                case "POTION":
                    rarity.addPotionItem(new Potion(s));
                    break;
                default:
                    rarity.addMaterial(Material.getMaterial(s));
                    break;
            }

            if(s.split(":").length == 2 && util.isNumber(s.split(":")[1])){ //Custom model data
                rarity.addCustomModelDataItem(new CustomModelDataItem(s));
            }

        }
        //TODO loadOther();
    }

    public Rarity getRarityByID(String id){ //TODO



        return  null;
    }

    public void removeRarity(ItemStack itemStack){
        ItemStack nullItemStack = new ItemStack(Material.MAP);
        setRarity(nullItemStack, getRarity(itemStack));
        NBT.removeNBT(itemStack);

        List<String> loreToRemove = buildLore(nullItemStack);
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> itemLore = itemMeta.getLore();
        itemLore.removeAll(loreToRemove);
        itemMeta.setLore(itemLore);
        itemStack.setItemMeta(itemMeta);
    }

    public void setUpdating(){
        updating = true;
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskLaterAsynchronously(plugin, () -> {
            updating = false;
        },20L);
    }

    public List<String> getLore_format() {
        return plugin.getConfig().getStringList("Config.lore-format");
    }

    public List<String> buildLore(ItemStack item){
        List<String> lore_format = getLore_format();
        List<String> build = new ArrayList<>();
        Rarity rarity = getRarity(item);

        for (int i = 0; i < lore_format.size(); i++){

            boolean b = false;

            if(lore_format.get(i).contains("{item-lore}")){ //Replacing item lore
                if(item.hasItemMeta() && item.getItemMeta().hasLore()) { //If item has lore
                    for (int n = 0; n < item.getItemMeta().getLore().size(); n++) { //Add all the item lore starting at index i
                        build.add(item.getItemMeta().getLore().get(n));
                        b = true;
                    }
                }else{
                    b = true;
                }
            }

            if(lore_format.get(i).contains("{rarity-prefix}")){
                build.add(colorized(lore_format.get(i).replace("{rarity-prefix}", rarity.getName())));
                b = true;
            }
            if(!b) {
                build.add(colorized(lore_format.get(i)));
            }
        }

        return build;

    }

    public String colorized(String s){
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            String hexCode = s.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder("");
            for (char c : ch) {
                builder.append("&" + c);
            }

            s = s.replace(hexCode, builder.toString());
            matcher = pattern.matcher(s);
        }
        return ChatColor.translateAlternateColorCodes('&', s);
    }

}
