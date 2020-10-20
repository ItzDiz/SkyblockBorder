package fun.lewisdev.skyblockborder.listeners;

import fun.lewisdev.skyblockborder.SkyblockBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private SkyblockBorder plugin;

    public PlayerQuitListener(SkyblockBorder plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getPluginBorder().removeWorldBorder(player);
        plugin.getPlayerManager().removePlayerCache(player.getUniqueId());
    }
}
