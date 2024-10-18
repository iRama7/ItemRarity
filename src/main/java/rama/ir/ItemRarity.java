package rama.ir;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import rama.ir.api.ApplyRarityEvent;
import rama.ir.raritymain.RarityMain;
import rama.ir.util.ItemsAdderEvent;
import rama.ir.util.Util;

import java.io.File;
import java.io.IOException;

public final class ItemRarity extends JavaPlugin {


    private File rarityFileFile;
    private FileConfiguration rarityFile;

    private boolean irgHook;

    private RarityMain rarityMain;

    private Util util;

    private boolean itemsAdderHook;

    @Override
    public void onEnable() {
        new UpdateChecker(this, 105483).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                logger("&eYou are using the latest version.", false);
            } else {
                logger("&eThere is a new update available!", false);
                logger("&eYour current version: "+"&c"+this.getDescription().getVersion(), false);
                logger("&eLatest version: "+"&a"+version, false);
            }
        });
        createRarityFile();
        this.saveDefaultConfig();
        initializeRarityMain();
        hookItemsAdder();
        initIRGHook();
        registerCommands();
        util = new Util(this);
        registerEvents();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public boolean isItemsAdderHook(){
        return itemsAdderHook;
    }

    public void hookItemsAdder(){
        if(Bukkit.getPluginManager().getPlugin("ItemsAdder") != null){
            logger("&eEnabling &dItemsAdder &ehook!", false);
            getServer().getPluginManager().registerEvents(new ItemsAdderEvent(rarityMain), this);
            itemsAdderHook = true;
        }else{
            logger("&dItemsAdder &enot found.", false);
            itemsAdderHook = false;
        }
    }

    private void initIRGHook() {
        if(Bukkit.getPluginManager().getPlugin("ItemRarityGlow") != null){
            logger("&eEnabling &dItemRarityGlow &ehook!", false);
            irgHook = true;
        }else{
            logger("&dItemRarityGlow &enot found.", false);
            irgHook = false;
        }


    }

    public void triggerApplyRarityEvent(ApplyRarityEvent event){
        Bukkit.getPluginManager().callEvent(event);
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




    public void reloadRarities() throws IOException {
        createRarityFile();
    }

    public void registerEvents(){
        logger("&eLoading events...", false);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(util, this);
    }

    public void registerCommands(){
        logger("&eLoading commands...", false);
        getCommand("ir").setExecutor(new MainCommand(this, rarityMain));
    }

    public void logger(String message, Boolean debug){

        boolean debugMode = getConfig().getBoolean("Config.debug-mode");

        if(debug){
            if(debugMode){
                getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[&2ItemRarity&6] &e[&cDEBUG&e] " + message));
            }
        }else{
            getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[&2ItemRarity&6] " + message));
        }


    }

    public void initializeRarityMain() {
        rarityMain = new RarityMain(this, rarityFile, this);
        rarityMain.startItemStackChecker(this.getConfig().getInt("Config.checkerTime"));
        rarityMain.loadRarities();
    }

    public void stopRarityMain(){
        rarityMain.stopChecker();
        rarityMain = null;
    }

    public RarityMain getRarityMain(){
        return rarityMain;
    }

    public boolean isIrgHook() {
        return irgHook;
    }

    public Util getUtil() {
        return util;
    }

    public void debug(String m){

    }





}