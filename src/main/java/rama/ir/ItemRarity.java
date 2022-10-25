package rama.ir;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import rama.ir.commands.MainCommand;
import rama.ir.itemhandler.ItemCreationListener;
import rama.ir.itemhandler.RecipeBookFix;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public final class ItemRarity extends JavaPlugin {


    private File rarityFileFile;
    private FileConfiguration rarityFile;

    public HashMap<String, String> rarities = new HashMap<>();


    @Override
    public void onEnable() {
        new UpdateChecker(this, 105483).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                logger("&eYou are using the latest version.");
            } else {
                logger("&eThere is a new update available!");
                logger("&eYour current version: "+"&c"+this.getDescription().getVersion());
                logger("&eLatest version: "+"&a"+version);
            }
        });
        createRarityFile();
        registerEvents();
        loadRarities();
        this.saveDefaultConfig();
        registerCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public FileConfiguration getRarityFile() {
        return rarityFile;
    }

    private void createRarityFile() {
        rarityFileFile = new File(getDataFolder(), "rarities.yml");
        if (!rarityFileFile.exists()) {
            rarityFileFile.getParentFile().mkdirs();
            saveResource("rarities.yml", false);
        }
        rarityFile = new YamlConfiguration();
        try {
            rarityFile.load(rarityFileFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }


    public void loadRarities(){
       for(String i : rarityFile.getConfigurationSection("Rarities").getKeys(false)){
           String Name = rarityFile.getString("Rarities." + i + ".Name");
           String Prefix = rarityFile.getString("Rarities." + i + ".Prefix");
           rarities.put(Name, Prefix);
       }
       logger("&eLoaded &a" + rarities.size() + " &erarities");
    }

    public void reloadRarities() throws IOException, InvalidConfigurationException {
        rarityFile.load(rarityFileFile);
    }

    public void registerEvents(){
        logger("&eLoading events...");
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new ItemCreationListener(this), this);
        pm.registerEvents(new RecipeBookFix(this), this);
    }

    public void registerCommands(){
        logger("&eLoading commands...");
        getCommand("ir").setExecutor(new MainCommand(this));
    }

    public void logger(String message){
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[&2ItemRarity&6] " + message));
    }


}