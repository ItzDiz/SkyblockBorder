package fun.lewisdev.skyblockborder.inventory.inventories;

import fun.lewisdev.skyblockborder.SkyblockBorder;
import fun.lewisdev.skyblockborder.border.BorderColor;
import fun.lewisdev.skyblockborder.inventory.ClickAction;
import fun.lewisdev.skyblockborder.inventory.InventoryBuilder;
import fun.lewisdev.skyblockborder.inventory.InventoryItem;
import fun.lewisdev.skyblockborder.border.BorderPlayer;
import fun.lewisdev.skyblockborder.utility.ChatUtils;
import fun.lewisdev.skyblockborder.utility.ItemStackBuilder;
import fun.lewisdev.skyblockborder.utility.universal.XMaterial;
import fun.lewisdev.skyblockborder.utility.universal.XSound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SettingsGUI {

    private SkyblockBorder plugin;
    private FileConfiguration config;

    public SettingsGUI(SkyblockBorder plugin) {
        this.plugin = plugin;
        this.config = plugin.getGuiConfig();
    }

    public void openInventory(Player player) {
        InventoryBuilder inventoryBuilder = new InventoryBuilder(config.getInt("inventories.settings.size"), ChatUtils.color(config.getString("inventories.settings.title")));

        // Filler items
        for(String entry : config.getConfigurationSection("inventories.settings.filler_items").getKeys(false)) {
            ItemStack itemStack = new ItemStackBuilder(XMaterial.matchXMaterial(config.getString("inventories.settings.filler_items." + entry + ".material")).get().parseItem())
                    .withName(config.getString("inventories.settings.filler_items." + entry + ".display_name"))
                    .withLore(config.getStringList("inventories.settings.filler_items." + entry + ".lore"))
                    .withAmount(config.getInt("inventories.settings.filler_items." + entry + ".amount"))
                    .build();
            InventoryItem item = new InventoryItem(itemStack);
            for(String slot : config.getStringList("inventories.settings.filler_items." + entry + ".slots")) {
                inventoryBuilder.setItem(Integer.parseInt(slot), item);
            }
        }

        BorderPlayer playerData = plugin.getPlayerManager().getPlayer(player.getUniqueId());

        // Border toggle item
        InventoryItem toggleItem = null;
        if(!player.hasPermission("skyblockborder.border.toggle")) {
            toggleItem = new InventoryItem(new ItemStackBuilder(XMaterial.matchXMaterial(config.getString("inventories.settings.toggle_button.locked.material")).get().parseItem())
                    .withName(config.getString("inventories.settings.toggle_button.locked.display_name"))
                    .withLore(config.getStringList("inventories.settings.toggle_button.locked.lore"))
                    .withAmount(config.getInt("inventories.settings.toggle_button.locked.amount"))
                    .build());
        } else if(playerData.isEnabled()) {
            toggleItem = new InventoryItem(new ItemStackBuilder(XMaterial.matchXMaterial(config.getString("inventories.settings.toggle_button.enabled.material")).get().parseItem())
                    .withName(config.getString("inventories.settings.toggle_button.enabled.display_name"))
                    .withLore(config.getStringList("inventories.settings.toggle_button.enabled.lore"))
                    .withAmount(config.getInt("inventories.settings.toggle_button.enabled.amount"))
                    .build());

            toggleItem.addClickAction(new ClickAction() {
                @Override
                public void execute(Player p) {
                    plugin.getPlayerManager().setBorderEnabled(player.getUniqueId(), false);
                    if(plugin.getGuiConfig().getBoolean("gui_sounds.border_toggled.enabled"))
                        player.playSound(player.getLocation(), XSound.matchXSound(plugin.getGuiConfig().getString("gui_sounds.border_toggled.sound")).get().parseSound(), 5, 1);
                    player.closeInventory();
                }
            });
        }else{
            toggleItem = new InventoryItem(new ItemStackBuilder(XMaterial.matchXMaterial(config.getString("inventories.settings.toggle_button.disabled.material")).get().parseItem())
                    .withName(config.getString("inventories.settings.toggle_button.disabled.display_name"))
                    .withLore(config.getStringList("inventories.settings.toggle_button.disabled.lore"))
                    .withAmount(config.getInt("inventories.settings.toggle_button.disabled.amount"))
                    .build());

            toggleItem.addClickAction(new ClickAction() {
                @Override
                public void execute(Player p) {
                    plugin.getPlayerManager().setBorderEnabled(player.getUniqueId(), true);
                    if(plugin.getGuiConfig().getBoolean("gui_sounds.border_toggled.enabled"))
                        player.playSound(player.getLocation(), XSound.matchXSound(plugin.getGuiConfig().getString("gui_sounds.border_toggled.sound")).get().parseSound(), 5, 1);
                    player.closeInventory();
                }
            });
        }
        inventoryBuilder.setItem(config.getInt("inventories.settings.toggle_button.slot"), toggleItem);

        // Red border item
        InventoryItem redBorderItem = null;
        if(player.hasPermission("skyblockborder.border.red")) {
            ItemStackBuilder redItem = new ItemStackBuilder(XMaterial.matchXMaterial(config.getString("inventories.settings.red_border_button.unlocked.material")).get().parseItem())
                    .withName(config.getString("inventories.settings.red_border_button.unlocked.display_name"))
                    .withLore(config.getStringList("inventories.settings.red_border_button.unlocked.lore"))
                    .withAmount(config.getInt("inventories.settings.red_border_button.unlocked.amount"));

            if(playerData.getColor() == BorderColor.RED) redItem.withGlow();
            redBorderItem = new InventoryItem(redItem.build());

            redBorderItem.addClickAction(new ClickAction() {
                @Override
                public void execute(Player p) {
                    plugin.getPlayerManager().setBorderColor(player.getUniqueId(), BorderColor.RED);
                    if(plugin.getGuiConfig().getBoolean("gui_sounds.color_selected.enabled"))
                        player.playSound(player.getLocation(), XSound.matchXSound(plugin.getGuiConfig().getString("gui_sounds.color_selected.sound")).get().parseSound(), 5, 1);
                    player.closeInventory();
                }
            });
        }else{
            redBorderItem = new InventoryItem(new ItemStackBuilder(XMaterial.matchXMaterial(config.getString("inventories.settings.red_border_button.locked.material")).get().parseItem())
                    .withName(config.getString("inventories.settings.red_border_button.locked.display_name"))
                    .withLore(config.getStringList("inventories.settings.red_border_button.locked.lore"))
                    .withAmount(config.getInt("inventories.settings.red_border_button.locked.amount"))
                    .build());
        }
        inventoryBuilder.setItem(config.getInt("inventories.settings.red_border_button.slot"), redBorderItem);

        // Green border item
        InventoryItem greenBorderItem = null;
        if(player.hasPermission("skyblockborder.border.green")) {
            ItemStackBuilder greenItem = new ItemStackBuilder(XMaterial.matchXMaterial(config.getString("inventories.settings.green_border_button.unlocked.material")).get().parseItem())
                    .withName(config.getString("inventories.settings.green_border_button.unlocked.display_name"))
                    .withLore(config.getStringList("inventories.settings.green_border_button.unlocked.lore"))
                    .withAmount(config.getInt("inventories.settings.green_border_button.unlocked.amount"));

            if(playerData.getColor() == BorderColor.GREEN) greenItem.withGlow();
            greenBorderItem = new InventoryItem(greenItem.build());

            greenBorderItem.addClickAction(new ClickAction() {
                @Override
                public void execute(Player p) {
                    plugin.getPlayerManager().setBorderColor(player.getUniqueId(), BorderColor.GREEN);
                    if(plugin.getGuiConfig().getBoolean("gui_sounds.color_selected.enabled"))
                        player.playSound(player.getLocation(), XSound.matchXSound(plugin.getGuiConfig().getString("gui_sounds.color_selected.sound")).get().parseSound(), 5, 1);
                    player.closeInventory();
                }
            });
        }else{
            greenBorderItem = new InventoryItem(new ItemStackBuilder(XMaterial.matchXMaterial(config.getString("inventories.settings.green_border_button.locked.material")).get().parseItem())
                    .withName(config.getString("inventories.settings.green_border_button.locked.display_name"))
                    .withLore(config.getStringList("inventories.settings.green_border_button.locked.lore"))
                    .withAmount(config.getInt("inventories.settings.green_border_button.locked.amount"))
                    .build());
        }
        inventoryBuilder.setItem(config.getInt("inventories.settings.green_border_button.slot"), greenBorderItem);

        // Blue border item
        InventoryItem blueBorderItem = null;
        if(player.hasPermission("skyblockborder.border.blue") || playerData.getColor() == BorderColor.BLUE) {
            ItemStackBuilder blueItem = new ItemStackBuilder(XMaterial.matchXMaterial(config.getString("inventories.settings.blue_border_button.unlocked.material")).get().parseItem())
                    .withName(config.getString("inventories.settings.blue_border_button.unlocked.display_name"))
                    .withLore(config.getStringList("inventories.settings.blue_border_button.unlocked.lore"))
                    .withAmount(config.getInt("inventories.settings.blue_border_button.unlocked.amount"));

            if(playerData.getColor() == BorderColor.BLUE) blueItem.withGlow();
            blueBorderItem = new InventoryItem(blueItem.build());

            blueBorderItem.addClickAction(new ClickAction() {
                @Override
                public void execute(Player p) {
                    plugin.getPlayerManager().setBorderColor(player.getUniqueId(), BorderColor.BLUE);
                    if(plugin.getGuiConfig().getBoolean("gui_sounds.color_selected.enabled"))
                        player.playSound(player.getLocation(), XSound.matchXSound(plugin.getGuiConfig().getString("gui_sounds.color_selected.sound")).get().parseSound(), 5, 1);
                    player.closeInventory();
                }
            });
        }else{
            blueBorderItem = new InventoryItem(new ItemStackBuilder(XMaterial.matchXMaterial(config.getString("inventories.settings.green_border_button.locked.material")).get().parseItem())
                    .withName(config.getString("inventories.settings.blue_border_button.locked.display_name"))
                    .withLore(config.getStringList("inventories.settings.blue_border_button.locked.lore"))
                    .withAmount(config.getInt("inventories.settings.blue_border_button.locked.amount"))
                    .build());
        }
        inventoryBuilder.setItem(config.getInt("inventories.settings.blue_border_button.slot"), blueBorderItem);

        if(plugin.getGuiConfig().getBoolean("gui_sounds.inventory_open.enabled"))
            player.playSound(player.getLocation(), XSound.matchXSound(plugin.getGuiConfig().getString("gui_sounds.inventory_open.sound")).get().parseSound(), 5, 1);

        player.openInventory(inventoryBuilder.getInventory());

    }

}
