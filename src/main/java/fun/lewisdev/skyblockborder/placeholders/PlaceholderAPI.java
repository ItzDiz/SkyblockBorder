package fun.lewisdev.skyblockborder.placeholders;

import fun.lewisdev.skyblockborder.SkyblockBorder;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlaceholderAPI extends PlaceholderExpansion {

    private SkyblockBorder plugin;

    public PlaceholderAPI(SkyblockBorder plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier(){
        return "skyblockborder";
    }

    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier){
        if(player == null) return "";

        UUID uuid = player.getUniqueId();

        if(identifier.equals("enabled")) return (plugin.getPlayerManager().getPlayer(uuid).isEnabled() ? "Yes" : "No");

        if(identifier.equals("color")) return plugin.getPlayerManager().getPlayer(uuid).getColor().toString();

        return null;
    }
}
