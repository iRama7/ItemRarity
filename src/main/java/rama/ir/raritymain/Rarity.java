package rama.ir.raritymain;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import rama.ir.ItemRarity;
import rama.ir.NBTMain;
import rama.ir.util.CustomModelDataItem;
import rama.ir.util.Potion;
import rama.ir.util.EnchantedBook;

import java.util.ArrayList;
import java.util.List;

public class Rarity {

    private final String identifier;
    private final String name;
    private final int weight;
    private List<Material> materials = new ArrayList<>();
    private List<ItemStack> customItems = new ArrayList<>();
    private List<EnchantedBook> enchantedBooks = new ArrayList<>();
    private List<Potion> potionItems = new ArrayList<>();
    private List<CustomModelDataItem> customModelDataItems = new ArrayList<>();
    private List<CustomStack> customStacks = new ArrayList<>();

    private final ItemRarity ir;

    public Rarity(String identifier, String name, int weight, ItemRarity ir){
        this.ir = ir;
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
        }else if(new NBTMain().hasCustomModelData(item)){ //CustomModelData
            for(CustomModelDataItem customModelDataItem : customModelDataItems){
                if(customModelDataItem.equals(item)){
                    b = true;
                    break;
                }
            }
        }else if(ir != null && ir.isItemsAdderHook()){
            if(CustomStack.byItemStack(item) != null) {
                for (CustomStack customStack : customStacks) {
                    if (customStack.getNamespace().equals(CustomStack.byItemStack(item).getNamespace())) {
                        b = true;
                        break;
                    }
                }
            }
        }else {
            for (ItemStack i : customItems) {
                if (iEqualsI(i, item)) {
                    b = true;
                }
            }
        }

        if (materials.contains(item.getType())) {
            b = true;
        }

        return b;
    }

    public boolean iEqualsI(ItemStack i, ItemStack i2) {

        ir.logger("Comparing in " + identifier, true);
        ir.logger(i.toString(), true);
        ir.logger(i2.toString(), true);

        boolean b = false;
        boolean equalsDisplayName = false;
        boolean equalsLore = false;

        if (i.getItemMeta().hasDisplayName() && i2.getItemMeta().hasDisplayName()) {
            if (i.getItemMeta().getDisplayName().equals(i2.getItemMeta().getDisplayName())) {
                    equalsDisplayName = true;
            }
        } else if (!i.getItemMeta().hasDisplayName() && !i2.getItemMeta().hasDisplayName()) {
            equalsDisplayName = true;
        }

        if(i.getItemMeta().hasLore() && i2.getItemMeta().hasLore()){
            if(i.getItemMeta().getLore().equals(i2.getItemMeta().getLore())){
                equalsLore = true;
            }
        } else if (!i.getItemMeta().hasLore() && !i2.getItemMeta().hasLore()){
            equalsLore = true;
        }

        b = equalsDisplayName && equalsLore && i.getType().equals(i2.getType());

        return b;

    }

    public void addMaterial(Material material){
        materials.add(material);
    }

    public void addItem(ItemStack item){
        customItems.add(item);
    }

    public void addEnchantedBook(EnchantedBook book){
        enchantedBooks.add(book);
    }

    public void addPotionItem(Potion potion){
        potionItems.add(potion);
    }

    public void addCustomModelDataItem(CustomModelDataItem item){
        customModelDataItems.add(item);
    }

    public void addItemsAdderItem(String namespace){
        CustomStack customItem = CustomStack.getInstance(namespace);
        if(customItem != null){
            customStacks.add(customItem);
        }else{
            ir.logger("&cDidn't found item &e" + namespace + " &cin ItemsAdder.", false);
        }
    }


}
