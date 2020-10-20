package fun.lewisdev.skyblockborder.config;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;

public class ConfigManager {

    private JavaPlugin plugin;

    private File folder;
    private String fileName;
    private boolean lookForDefault;

    public FileConfiguration config;
    private File configFile;

    public ConfigManager(JavaPlugin plugin, File directory, String name, boolean isDefault) {

        if(!name.endsWith(".yml")) name += ".yml";

        this.plugin = plugin;
        this.folder = directory;
        this.fileName = name;
        this.lookForDefault = isDefault;

        reload();
    }

    public FileConfiguration getConfig() {
        if(config == null) reload();
        return config;
    }

    public Location getLocation(String path) {

        World w = Bukkit.getServer().getWorld(getConfig().getString(path + ".world"));
        double x = getConfig().getDouble(path + ".x");
        double y = getConfig().getDouble(path + ".y");
        double z = getConfig().getDouble(path + ".z");
        float pitch = getConfig().getInt(path + ".pitch", 0);
        float yaw = getConfig().getInt(path + ".yaw", 0);

        return new Location(w, x, y, z, yaw, pitch);
    }

    public boolean reload() {

        if(!folder.exists()) {
            try {
                if(!folder.mkdir())
                   Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUnable to create folder " + folder.getName()));
            } catch(Exception exception) {
                Bukkit.getConsoleSender().sendMessage( "Failed to reload " + fileName + " config.");
                return false;
            }
        }

        this.configFile = new File(folder, fileName);
        this.config = YamlConfiguration.loadConfiguration(configFile);

        if(lookForDefault) {
            try {
                Reader defaultConfigStream = new InputStreamReader(plugin.getResource(fileName), "UTF8");
                if(defaultConfigStream != null) {
                    YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defaultConfigStream);
                    config.setDefaults(defaultConfig);
                    saveDefaultConfig();
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Failed to load default " + fileName + " config.");
            }
        } else {
            if(!configFile.exists()) {
                try
                {
                    configFile.createNewFile();
                } catch(Exception exception) {
                    plugin.getLogger().log(Level.WARNING, "Failed to reload " + fileName + " config.");
                    return false;
                }
            }
        }

        return true;
    }

    public boolean save() {

        if (config == null || configFile == null) return false;

        try {
            getConfig().save(configFile);
            return true;
        } catch (IOException ex) {
            plugin.getLogger().log(Level.WARNING, "Failed to save config to " + configFile.getName(), ex);
            return false;
        }
    }

    public void saveDefaultConfig() {
        if (configFile == null)  configFile = new File(plugin.getDataFolder(), fileName);

        if (!configFile.exists()) plugin.saveResource(fileName, false);
    }


}