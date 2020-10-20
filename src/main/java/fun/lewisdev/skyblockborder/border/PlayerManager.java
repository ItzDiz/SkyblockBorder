package fun.lewisdev.skyblockborder.border;

import fun.lewisdev.skyblockborder.SkyblockBorder;
import fun.lewisdev.skyblockborder.cooldown.CooldownManager;
import fun.lewisdev.skyblockborder.cooldown.CooldownType;
import fun.lewisdev.skyblockborder.utility.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private SkyblockBorder plugin;
    private CooldownManager cooldownManager;
    private Map<UUID, BorderPlayer> playerData;

    public PlayerManager(SkyblockBorder plugin) {
        this.plugin = plugin;
        this.playerData = new HashMap<>();
        this.cooldownManager = plugin.getCooldownManager();
    }

    public BorderPlayer getPlayer(UUID uuid) {
        if(!playerData.containsKey(uuid)) loadPlayerStorage(uuid);
        return playerData.get(uuid);
    }

    public void setBorderEnabled(UUID uuid, boolean enabled) {
        BorderPlayer borderPlayer = getPlayer(uuid);
        Player player = Bukkit.getPlayer(uuid);

        if(!player.hasPermission("skyblockborder.cooldown.bypass") && !cooldownManager.tryCooldown(player.getUniqueId(), CooldownType.BORDER_TOGGLE)) {
            player.sendMessage(ChatUtils.color(plugin.getLangConfig().getString("messages.cooldown_active").replace("%time%", String.valueOf(cooldownManager.getCooldown(player.getUniqueId(), CooldownType.BORDER_COLOR) / 1000))));
            return;
        }

        if(enabled) {
            borderPlayer.setEnabled(true);
            plugin.getPluginBorder().sendWorldBorder(Bukkit.getPlayer(uuid), borderPlayer.getColor());
            player.sendMessage(ChatUtils.color(plugin.getLangConfig().getString("messages.border_toggle_on")));
        }else{
            borderPlayer.setEnabled(false);
            plugin.getPluginBorder().removeWorldBorder(Bukkit.getPlayer(uuid));
            player.sendMessage(ChatUtils.color(plugin.getLangConfig().getString("messages.border_toggle_off")));
        }
    }

    public void setBorderColor(UUID uuid, BorderColor color) {
        BorderPlayer borderPlayer = getPlayer(uuid);
        Player player = Bukkit.getPlayer(uuid);

        if(borderPlayer.getColor() == color) {
            player.sendMessage(ChatUtils.color(plugin.getLangConfig().getString("messages.border_already_selected")));
            return;
        }
        if(!borderPlayer.isEnabled()) {
            player.sendMessage(ChatUtils.color(plugin.getLangConfig().getString("messages.border_not_enabled")));
            return;
        }

        if(!player.hasPermission("skyblockborder.cooldown.bypass") && !cooldownManager.tryCooldown(player.getUniqueId(), CooldownType.BORDER_COLOR)) {
            player.sendMessage(ChatUtils.color(plugin.getLangConfig().getString("messages.cooldown_active").replace("%time%", String.valueOf(cooldownManager.getCooldown(player.getUniqueId(), CooldownType.BORDER_COLOR) / 1000))));
            return;
        }

        borderPlayer.setColor(color);
        plugin.getPluginBorder().sendWorldBorder(player, color);

        switch (color.toString().toUpperCase()) {
            case "RED":
                player.sendMessage(ChatUtils.color(plugin.getLangConfig().getString("messages.red_border_selected")));
                break;
            case "GREEN":
                player.sendMessage(ChatUtils.color(plugin.getLangConfig().getString("messages.green_border_selected")));
                break;
            case "BLUE":
                player.sendMessage(ChatUtils.color(plugin.getLangConfig().getString("messages.blue_border_selected")));
                break;
        }
    }

    public void removePlayerCache(UUID uuid) {
        if(playerData.containsKey(uuid)) {
            savePlayerData(uuid);
            playerData.remove(uuid);
        }
    }

    private void loadPlayerStorage(UUID uuid) {
        File playerData = new File(plugin.getDataFolder() + File.separator + "data", uuid.toString() + ".yml");
        if (!playerData.exists()) {
            try {
                playerData.createNewFile();
            } catch (IOException ex) {
                // ignore
            }

            FileConfiguration playerDataConfig = new YamlConfiguration();
            try {
                playerDataConfig.addDefault("border.enabled", true);
                playerDataConfig.addDefault("border.color", "BLUE");
                playerDataConfig.options().copyDefaults(true);
                playerDataConfig.save(playerData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.playerData.put(uuid, new BorderPlayer(true, BorderColor.BLUE));
        }else{
            FileConfiguration playerDataConfig = YamlConfiguration.loadConfiguration(playerData);

            boolean enabled = playerDataConfig.getBoolean("border.enabled");
            String color = playerDataConfig.getString("border.color").toUpperCase();

            if(!color.equalsIgnoreCase("RED") && !color.equalsIgnoreCase("GREEN") && !color.equalsIgnoreCase("BLUE")) color = "BLUE";

            this.playerData.put(uuid, new BorderPlayer(enabled, BorderColor.valueOf(color)));
        }
    }

    public void onDisable() {
        for(UUID uuid : playerData.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if(player != null && player.isOnline()) {
                plugin.getPluginBorder().removeWorldBorder(player);
                savePlayerData(uuid);
            }
        }
    }

    private void savePlayerData(UUID uuid) {
        if(uuid == null) return;

        BorderPlayer player = getPlayer(uuid);

        File playerFile = new File(plugin.getDataFolder() + File.separator + "data", uuid + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

        config.set("border.enabled", player.isEnabled());
        config.set("border.color", player.getColor().toString());

        try {
            config.save(playerFile);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
