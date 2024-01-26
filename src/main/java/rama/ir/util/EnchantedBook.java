package rama.ir.util;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Map;

public class EnchantedBook {

    Enchantment enchantment;
    int level;

    public EnchantedBook(String s){ // ENCHANTED_BOOK:efficiency 5
        String[] parts = s.split(":")[1].split(" ");
        enchantment = Enchantment.getByKey(NamespacedKey.minecraft(parts[0]));
        level = Integer.parseInt(parts[1]);
    }

    public int getLevel() {
        return level;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public boolean equals(ItemStack book){
        boolean b = false;

        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();

        if(meta.getStoredEnchantLevel(enchantment) == level){
            b = true;
        }

        return b;
    }
}
