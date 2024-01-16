package rama.ir.raritymain;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import rama.ir.util.Potion;
import rama.ir.util.EnchantedBook;

import java.util.ArrayList;
import java.util.List;

public class Rarity {

    private final String identifier;
    private final String name;
    private final int weight;
    private List<Material> materials = new ArrayList<>();
    private List<ItemStack> items = new ArrayList<>();
    private List<EnchantedBook> enchantedBooks = new ArrayList<>();
    private List<Potion> potionItems = new ArrayList<>();

    public Rarity(String identifier, String name, int weight){
        this.identifier = identifier;
        this.name = name;
        this.weight = weight;
    }

    public String getIdentifier(){
        return  identifier;
    }

    public String getName(){
        return name;
    }

    public int getWeight(){
        return weight;
    }

    public boolean contains(ItemStack item){
        boolean b = false;

        Material itemMaterial = item.getType();

        if(itemMaterial.equals(Material.ENCHANTED_BOOK)){ // Enchanted book
            for(EnchantedBook book : enchantedBooks){
                if(book.equals(item)){
                    b = true;
                    break;
                }
            }

        }else if(itemMaterial.equals(Material.POTION)){ // Potion
                for(Potion potion : potionItems){
                    if(potion.equals(item)){
                        b = true;
                        break;
                    }
                }
        }
        if (materials.contains(item.getType())) {
            b = true;
        }


            /*
            for (ItemStack i : items) {
                if (iEqualsI(i, item)) {
                    b = true;
                }
            }
             */

        return b;
    }

    public boolean iEqualsI(ItemStack i, ItemStack i2){
        boolean b = false;
        boolean equalsDisplayName = false;
        boolean equalsLore = false;

        if(i.getItemMeta().hasDisplayName() && i2.getItemMeta().hasDisplayName()){
            if(i.getItemMeta().getDisplayName().equals(i2.getItemMeta().getDisplayName())){
                equalsDisplayName = true;
            }
        }

        if(i.getItemMeta().hasLore() && i2.getItemMeta().hasLore()){
            if(i.getItemMeta().getLore().equals(i2.getItemMeta().getLore())){
                equalsLore = true;
            }
        }

        b = equalsDisplayName == equalsLore;

        return b;

    }

    public void addMaterial(Material material){
        materials.add(material);
    }

    public void addItem(ItemStack item){
        items.add(item);
    }

    public void addEnchantedBook(EnchantedBook book){
        enchantedBooks.add(book);
    }

    public void addPotionItem(Potion potion){
        potionItems.add(potion);
    }

}
