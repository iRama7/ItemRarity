package rama.ir.itemhandler;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rama.ir.ItemRarity;
import rama.ir.raritysystem.RarityMain;

public class RecipeBookFix implements Listener {

    public ItemRarity plugin;

    public RecipeBookFix(ItemRarity plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void CraftEvent(PrepareItemCraftEvent e) {
        for (ItemStack is : e.getInventory().getHolder().getInventory().getContents()) {
            if (is != null && is.getType() != Material.AIR) {
                NBTItem nbti = new NBTItem(is);
                if (nbti.hasKey("Rarity") && !nbti.hasKey("CustomItem")) {
                    nbti.removeKey("Rarity");
                    is.setItemMeta(nbti.getItem().getItemMeta());
                    ItemMeta im = is.getItemMeta();
                    im.setLore(null);
                    is.setItemMeta(im);
                }
                if(e.getInventory().getResult() != null) {
                    RarityMain rarityMain = new RarityMain(plugin);
                    rarityMain.getRarity(e.getInventory().getResult());
                }
            }

        }
    }
}
