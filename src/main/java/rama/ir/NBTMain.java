package rama.ir;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class NBTMain {

    private static final String key = "Rarity";

    public void addNBT(String value, ItemStack item){
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString(key, value);
        item.setItemMeta(nbtItem.getItem().getItemMeta());
    }

    public String getNBT(ItemStack item){
        NBTItem nbtItem = new NBTItem(item);
        return nbtItem.getString(key);

    }

    public void removeNBT(ItemStack item){
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.removeKey(key);
        item.setItemMeta(nbtItem.getItem().getItemMeta());
    }

    public int getCustomModelData(ItemStack item){
        NBTItem nbtItem = new NBTItem(item);
        Bukkit.getLogger().info("Custom model data for item " + item.getType() + " is " + nbtItem.getInteger("CustomModelData"));
        return nbtItem.getInteger("CustomModelData");
    }

    public boolean hasCustomModelData(ItemStack item){
        NBTItem nbtItem = new NBTItem(item);
        return nbtItem.getInteger("CustomModelData") != 0;
    }

}
