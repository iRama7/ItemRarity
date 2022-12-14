package rama.ir.itemhandler;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import rama.ir.ItemRarity;
import rama.ir.raritysystem.RarityMain;

public class ItemCreationListener implements Listener {

    public ItemRarity plugin;

    public ItemCreationListener(ItemRarity plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void itemSpawnEvent(ItemSpawnEvent e) {
        RarityMain rarityMain = new RarityMain(plugin);
        rarityMain.getRarity(e.getEntity().getItemStack());

    }

    @EventHandler
    public void onEnable(PluginEnableEvent e) {
        if (e.getPlugin().getName().equals("ItemRarity")) {
            BukkitScheduler scheduler = Bukkit.getScheduler();
            scheduler.runTaskTimerAsynchronously(plugin, () -> {
                if (!Bukkit.getOnlinePlayers().isEmpty()) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ItemStack[] playerItems = player.getInventory().getContents();
                        for (ItemStack itemStack : playerItems) {
                            if (itemStack != null) {
                                NBTItem nbti = new NBTItem(itemStack);
                                if (!nbti.hasKey("Rarity")) {
                                    RarityMain rarityMain = new RarityMain(plugin);
                                    rarityMain.getRarity(itemStack);
                                }
                            }
                        }
                    }
                }
            }, 1L, 1L);
        }
    }
}
