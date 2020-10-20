package fun.lewisdev.skyblockborder.inventory;

import fun.lewisdev.skyblockborder.SkyblockBorder;
import fun.lewisdev.skyblockborder.inventory.inventories.SettingsGUI;

public class InventoryManager {

    private SkyblockBorder plugin;
    private SettingsGUI settingsGUI;

    public InventoryManager(SkyblockBorder plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new InventoryListener(), plugin);
        load();
    }

    public void load() {
        settingsGUI = new SettingsGUI(plugin);
    }

    public SettingsGUI getSettingsGUI() {
        return settingsGUI;
    }
}
