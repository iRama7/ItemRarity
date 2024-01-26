package rama.ir.util;

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import rama.ir.raritymain.RarityMain;

public class ItemsAdderEvent implements Listener {

    private final RarityMain rarityMain;

    public ItemsAdderEvent(RarityMain rarityMain){
        this.rarityMain = rarityMain;
    }

    @EventHandler
    public void event(ItemsAdderLoadDataEvent e){
        rarityMain.loadItemsAdderItems();
    }

}
