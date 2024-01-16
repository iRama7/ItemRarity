package rama.ir;

import de.tr7zw.changeme.nbtapi.NBTItem;
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

}
