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
import rama.ir.util.Util;

import java.io.File;
import java.io.IOException;

public final class ItemRarity extends JavaPlugin {


    private File rarityFileFile;
    private FileConfiguration rarityFile;

    private RarityMain rarityMain;

    private Util util;



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
        this.saveDefaultConfig();

        registerCommands();
        initializeRarityMain();
        util = new Util(this);
        registerEvents();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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




    public void reloadRarities() throws IOException, InvalidConfigurationException {
        rarityFile.load(rarityFileFile);
    }

    public void registerEvents(){
        logger("&eLoading events...");
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(util, this);
    }

    public void registerCommands(){
        logger("&eLoading commands...");
        getCommand("ir").setExecutor(new MainCommand(this));
    }

    public void logger(String message){
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[&2ItemRarity&6] " + message));
    }

    public void initializeRarityMain(){
        rarityMain = new RarityMain(this, rarityFile, this);
        rarityMain.startItemStackChecker(this.getConfig().getInt("Config.checkerTime"));
        rarityMain.loadRarities();
    }

    public RarityMain getRarityMain(){
        return rarityMain;
    }


    public Util getUtil() {
        return util;
    }

    public void debug(String m){

    }



}