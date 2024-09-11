package rama.ir;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableItemNBT;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

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
        int customModelData = 0;
        if(MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4)){
            customModelData = NBT.modifyComponents(item, (Function<ReadWriteNBT, Integer>) nbt ->
                    nbt.getInteger("minecraft:custom_model_data"));
        }else{
            customModelData = NBT.get(item, (Function<ReadableItemNBT, Integer>) nbt ->
                    nbt.getInteger("CustomModelData"));
        }

        Bukkit.getLogger().info("Custom model data for item " + item.getType() + " is " + customModelData);

        return customModelData;
    }

    public boolean hasCustomModelData(ItemStack item){
        int customModelData = 0;
        if(MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4)){
            customModelData = NBT.modifyComponents(item, (Function<ReadWriteNBT, Integer>) nbt ->
                    nbt.getInteger("minecraft:custom_model_data"));
        }else{
            customModelData = NBT.get(item, (Function<ReadableItemNBT, Integer>) nbt ->
                    nbt.getInteger("CustomModelData"));
        }

        return customModelData != 0;
    }

}
