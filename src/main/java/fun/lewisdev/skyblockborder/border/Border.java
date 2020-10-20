package fun.lewisdev.skyblockborder.border;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class Border {

    public abstract void sendWorldBorder(Player player, BorderColor color);

    public void removeWorldBorder(Player player) {
        BorderHandler.sendWorldBorder(player, new Location(player.getLocation().getWorld(), 0, 0, 0), 1.4999992E7, BorderColor.BLUE);
    }

}
