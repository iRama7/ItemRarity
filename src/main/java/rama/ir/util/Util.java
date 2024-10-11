package rama.ir.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import rama.ir.ItemRarity;
import java.util.ArrayList;
import java.util.List;

public class Util implements Listener {

    private final List<Player> excludedPlayers = new ArrayList<>();
    private final ItemRarity main;

    public Util(ItemRarity plugin){
        main = plugin;
    }



    @EventHandler
    public void inventoryOpenEvent(InventoryOpenEvent e){
        if(isBlacklisted(e.getView().getTitle()) || isBlacklisted(e.getInventory().getType())){
            Bukkit.getLogger().info(e.getView().getTitle());
            excludedPlayers.add((Player) e.getPlayer());
            for(ItemStack item : e.getPlayer().getInventory().getContents().clone()){
                if(item != null && !item.getType().equals(Material.AIR) && main.getRarityMain().getRarity(item) != null){
                    main.getRarityMain().removeRarity(item);
                }
            }
        }
    }

    @EventHandler
    public void inventoryCloseEvent(InventoryCloseEvent e){
        Player p = (Player) e.getPlayer();
        excludedPlayers.remove(p);
    }

    public Boolean isPlayerExcluded(Player p){
        return excludedPlayers.contains(p);
    }


    @EventHandler
    public void itemSpawnEvent(ItemSpawnEvent e){
        ItemStack item = e.getEntity().getItemStack();
        if(item != null && !item.getType().equals(Material.AIR) && main.getRarityMain().getRarity(item) == null){
            main.getRarityMain().queryItem(item);
        }
    }

    public boolean isNumber(String s){
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isBlacklisted(String title){
        for(String t : main.getConfig().getStringList("Config.inventory-blacklist.titles")){
            if(title.equals(t)){
                return true;
            }
        }
        return false;
    }

    public boolean isBlacklisted(InventoryType type){

        boolean b = false;

        for(String stringEnum : main.getConfig().getStringList("Config.inventory-blacklist.types")){
            try{
                InventoryType inventoryType = InventoryType.valueOf(stringEnum);

                if(inventoryType.equals(type)){
                    b = true;
                }
            }catch (IllegalArgumentException e){
                main.logger(e.getMessage(), false);
            }
        }

        return b;

    }


}
