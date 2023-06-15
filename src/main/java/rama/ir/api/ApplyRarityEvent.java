package rama.ir.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ApplyRarityEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final String rarity;
    private final Boolean isCustom;
    private final ItemStack item;
    public ApplyRarityEvent(String rarity, Boolean isCustom, ItemStack item){
        this.isCustom = isCustom;
        this.item = item;
        this.rarity = rarity;
    }

    public String getRarity(){
        return rarity;
    }

    public Boolean isCustom(){
        return isCustom;
    }

    public ItemStack getItem(){
        return item;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
