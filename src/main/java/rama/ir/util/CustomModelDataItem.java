package rama.ir.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import rama.ir.NBTMain;

public class CustomModelDataItem {

    int number;
    Material material;

    private static final NBTMain nbtMain = new NBTMain();

    public CustomModelDataItem(String s){
        this.number = Integer.parseInt(s.split(":")[1]);
        this.material = Material.getMaterial(s.split(":")[0]);
    }

    public Material getMaterial() {
        return material;
    }

    public int getNumber(){
        return number;
    }

    public boolean equals(ItemStack item){
        return number == nbtMain.getCustomModelData(item) &&
                material == item.getType();
    }
}
