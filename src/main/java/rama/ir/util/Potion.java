package rama.ir.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class Potion {

    PotionType type;
    boolean extended;
    boolean upgraded;

    public Potion(String s){
        //POTION:(effect_name) (extended:upgraded) POTION:invisibility true:false

        String[] parts = s.split(" ");

        type = PotionType.valueOf(parts[0].split(":")[1].toUpperCase()); //invisibility

        extended = Boolean.parseBoolean(parts[1].split(":")[0]); //true

        upgraded = Boolean.parseBoolean(parts[1].split(":")[1]); //false
    }

    public Boolean equals(ItemStack potion){
        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
        PotionData potionData = potionMeta.getBasePotionData();
        return type == potionData.getType() &&
                extended == potionData.isExtended() &&
                upgraded == potionData.isUpgraded();
    }

    public PotionType getType(){
        return type;
    }

    public boolean isExtended(){
        return extended;
    }

    public boolean isUpgraded(){
        return upgraded;
    }



}
