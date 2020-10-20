package fun.lewisdev.skyblockborder.border.hooks;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.Island;
import com.wasteofplastic.askyblock.events.IslandEnterEvent;
import com.wasteofplastic.askyblock.events.IslandExitEvent;
import fun.lewisdev.skyblockborder.SkyblockBorder;
import fun.lewisdev.skyblockborder.border.Border;
import fun.lewisdev.skyblockborder.border.BorderColor;
import fun.lewisdev.skyblockborder.border.BorderHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ASkyBlockHook extends Border implements Listener {

    private ASkyBlockAPI api;
    private SkyblockBorder plugin;

    public ASkyBlockHook(SkyblockBorder plugin) {
        api = ASkyBlockAPI.getInstance();
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void sendWorldBorder(Player player, BorderColor color) {
        if (player.isOnline() && player.getWorld().equals(api.getIslandWorld()) && api.islandAtLocation(player.getLocation())) {
            Island island = api.getIslandAt(player.getLocation());
            BorderHandler.sendWorldBorder(player, island.getCenter(), island.getProtectionSize(), color);
        }
    }

    @EventHandler
    public void onIslandEnter(IslandEnterEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayer());
        if(!plugin.getPlayerManager().getPlayer(player.getUniqueId()).isEnabled()) return;
        new BukkitRunnable() {
            public void run() {
                sendWorldBorder(player, plugin.getPlayerManager().getPlayer(player.getUniqueId()).getColor());
            }
        }.runTaskLater(plugin, 20L);
    }

    @EventHandler
    public void onIslandExit(IslandExitEvent event) {
        removeWorldBorder(Bukkit.getPlayer(event.getPlayer()));
    }
}
