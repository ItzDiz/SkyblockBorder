package fun.lewisdev.skyblockborder.cooldown;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class CooldownManager {

    private Table<String, CooldownType, Long> cooldowns = HashBasedTable.create();

    private boolean changeColorCooldown, toggleCooldown;
    private int changeColorCooldownTime, toggleCooldownTime;

    public CooldownManager(FileConfiguration config) {
        load(config);
    }
	
    public void load(FileConfiguration config) {
        this.changeColorCooldown = config.getBoolean("settings.cooldowns.change_color.enabled");
        this.toggleCooldown = config.getBoolean("settings.cooldowns.toggle_border.enabled");
        this.changeColorCooldownTime = config.getInt("settings.cooldowns.change_color.time");
        this.toggleCooldownTime = config.getInt("settings.cooldowns.toggle_border.time");
    }

    public long getCooldown(UUID uuid, CooldownType key) {
        return calculateRemainder(cooldowns.get(uuid.toString(), key));
    }

    public long setCooldown(UUID uuid, CooldownType key) {
        if(key == CooldownType.BORDER_COLOR)
            return calculateRemainder(cooldowns.put(uuid.toString(), key, System.currentTimeMillis() + (changeColorCooldownTime*1000)));
        else
            return calculateRemainder(cooldowns.put(uuid.toString(), key, System.currentTimeMillis() + (toggleCooldownTime*1000)));
    }

    public boolean tryCooldown(UUID uuid, CooldownType key) {
        if (getCooldown(uuid, key) / 1000 > 0) return false;

        if(key == CooldownType.BORDER_TOGGLE && !toggleCooldown) return true;
        else if(key == CooldownType.BORDER_COLOR && !changeColorCooldown) return true;

        setCooldown(uuid, key);
        return true;
    }

    public void removeCooldowns(UUID uuid) {
        cooldowns.row(uuid.toString()).clear();
    }
    
    private long calculateRemainder(Long expireTime) {
        return expireTime != null ? expireTime - System.currentTimeMillis() : Long.MIN_VALUE;
    }
}