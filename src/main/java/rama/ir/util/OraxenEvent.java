package rama.ir.util;

import io.th0rgal.oraxen.api.events.OraxenItemsLoadedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import rama.ir.raritymain.RarityMain;

public class OraxenEvent implements Listener {
    private final RarityMain rarityMain;

    public OraxenEvent(RarityMain rarityMain){
        this.rarityMain = rarityMain;
    }

    @EventHandler
    public void event(OraxenItemsLoadedEvent e){
        rarityMain.loadOraxenItems();
    }

}
