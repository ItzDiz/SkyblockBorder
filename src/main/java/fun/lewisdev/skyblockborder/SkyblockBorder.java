package fun.lewisdev.skyblockborder;

import java.util.logging.Level;

import fun.lewisdev.skyblockborder.border.Border;
import fun.lewisdev.skyblockborder.border.hooks.ASkyBlockHook;
import fun.lewisdev.skyblockborder.command.SkyblockBorderCommand;
import fun.lewisdev.skyblockborder.config.ConfigManager;
import fun.lewisdev.skyblockborder.cooldown.CooldownManager;
import fun.lewisdev.skyblockborder.inventory.InventoryManager;
import fun.lewisdev.skyblockborder.border.PlayerManager;
import fun.lewisdev.skyblockborder.listeners.PlayerQuitListener;
import fun.lewisdev.skyblockborder.placeholders.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyblockBorder extends JavaPlugin {

    private InventoryManager inventoryManager;
    private PlayerManager playerManager;
    private ConfigManager guiConfig, langConfig;
    private CooldownManager cooldownManager;
    private Border pluginBorder;

    public void onEnable() {
        getLogger().log(Level.INFO, "");
        getLogger().log(Level.INFO, " __ _  _     " + getDescription().getName()  +" v" + getDescription().getVersion());
        getLogger().log(Level.INFO, "(_ |_)|_)    Author: " + getDescription().getAuthors().get(0));
        getLogger().log(Level.INFO, "__)|_)|_)    (c) Lewis D 2020. All rights reserved.");
        getLogger().log(Level.INFO, "");

        saveDefaultConfig();
        guiConfig = new ConfigManager(this, this.getDataFolder(), "gui.yml", true);
        langConfig = new ConfigManager(this, this.getDataFolder(), "lang.yml", true);

        guiConfig.reload();
        langConfig.reload();

        PluginManager pluginManager = getServer().getPluginManager();

        // ASkyBlock Hook
        if (pluginManager.isPluginEnabled(pluginManager.getPlugin("ASkyBlock"))) {
            getLogger().log(Level.INFO, "Hooked into ASkyBlock");
            pluginBorder = new ASkyBlockHook(this);
        }

        new PlayerQuitListener(this);

        inventoryManager = new InventoryManager(this);
        cooldownManager = new CooldownManager(getConfig());
        playerManager = new PlayerManager(this);

        getCommand("skyblockborder").setExecutor(new SkyblockBorderCommand(this));
        new SkyblockBorderCommand(this);

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) new PlaceholderAPI(this).register();
    }

    public void reload() {
        HandlerList.unregisterAll(this);
        reloadConfig();
        guiConfig.reload();
        langConfig.reload();
        cooldownManager.load(getConfig());
        new SkyblockBorderCommand(this);
        inventoryManager = new InventoryManager(this);
    }

    public void onDisable() {
        HandlerList.unregisterAll(this);
        playerManager.onDisable();
    }

    public FileConfiguration getGuiConfig() {
        return guiConfig.getConfig();
    }

    public FileConfiguration getLangConfig() {
        return langConfig.getConfig();
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public Border getPluginBorder() {
        return pluginBorder;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
}