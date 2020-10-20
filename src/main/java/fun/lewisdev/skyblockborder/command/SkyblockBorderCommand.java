package fun.lewisdev.skyblockborder.command;

import fun.lewisdev.skyblockborder.SkyblockBorder;
import fun.lewisdev.skyblockborder.border.BorderColor;
import fun.lewisdev.skyblockborder.border.BorderPlayer;
import fun.lewisdev.skyblockborder.border.PlayerManager;
import fun.lewisdev.skyblockborder.utility.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class SkyblockBorderCommand implements CommandExecutor {

    private SkyblockBorder plugin;

    public SkyblockBorderCommand() {}

    public SkyblockBorderCommand(SkyblockBorder plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        boolean guiEnabled = plugin.getConfig().getBoolean("settings.gui_enabled");

        if(args.length == 0) {
            if(sender instanceof Player) {
                if(guiEnabled) {
                    plugin.getInventoryManager().getSettingsGUI().openInventory((Player) sender);
                }else{
                    ((Player) sender).performCommand("skyblockborder about");
                }
            }else{
                sender.sendMessage("SBB: You must to be a player to open this inventory.");
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("about")) {
            sender.sendMessage(ChatUtils.color("&bServer is running &fSkyblockBorder v" + plugin.getDescription().getVersion() + " &bby &fItsLewizzz"));
            sender.sendMessage(ChatUtils.color("&4Registered to: &chttps://www.mc-market.org/members/%%__USER__%%/"));
            return true;
        }

        if(args[0].equalsIgnoreCase("help") && sender.hasPermission("skyblockborder.command.help")) {
            sender.sendMessage("");
            sender.sendMessage(ChatUtils.color("&3&lSkyblockBorder " + "&fv" + plugin.getDescription().getVersion()));
            sender.sendMessage(ChatUtils.color("&7Author: &fItsLewizzz"));
            sender.sendMessage("");
            sender.sendMessage(ChatUtils.color(" &b/" + label));
            sender.sendMessage(ChatUtils.color("   &7&oOpen Island Border GUI (if enabled)"));
            sender.sendMessage(ChatUtils.color(" &b/" + label + "&b help"));
            sender.sendMessage(ChatUtils.color("   &7&oDisplay help message"));
            sender.sendMessage(ChatUtils.color(" &b/" + label + "&b info <player>"));
            sender.sendMessage(ChatUtils.color("   &7&oSee island border information about a player"));
            sender.sendMessage(ChatUtils.color(" &b/" + label + "&b toggle [player] <true/false>"));
            sender.sendMessage(ChatUtils.color("   &7&oSet visibility of island borders for a player"));
            sender.sendMessage(ChatUtils.color(" &b/" + label + "&b color [player] <color>"));
            sender.sendMessage(ChatUtils.color("   &7&oSet a border color for a player"));
            sender.sendMessage(ChatUtils.color(" &b/" + label + "&b reload"));
            sender.sendMessage(ChatUtils.color("   &7&oReload the configuration"));
            sender.sendMessage("");
            return true;
        }

        if(args[0].equalsIgnoreCase("info") && sender.hasPermission("skyblockborder.command.info")) {

            if(args.length != 2 ) {
                sender.sendMessage(ChatUtils.color("&cUsage: /skyblockborder info <player>"));
                return true;
            }

            Player player = Bukkit.getPlayer(args[1]);

            if(player == null) {
                sender.sendMessage(ChatUtils.color(plugin.getLangConfig().getString("messages.invalid_player").replace("%player%", args[1])));
                return true;
            }

            BorderPlayer borderPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());

            for(String line : plugin.getLangConfig().getStringList("messages.border_info_player")) {
                sender.sendMessage(ChatUtils.color(line.replace("%player%", player.getName()).replace("%enabled%", (borderPlayer.isEnabled() ? "Yes" : "No")).replace("%color%", borderPlayer.getColor().toString())));
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("toggle")) {
            if(args.length == 2 && sender.hasPermission("skyblockborder.command.toggle") && sender instanceof Player) {
                Player player = (Player) sender;

                if(!toggleBorder(player.getUniqueId(), args[1])) {
                    player.sendMessage(ChatUtils.color(plugin.getLangConfig().getString("messages.invalid_toggle_value").replace("%value%", args[1])));
                }
                return true;
            }
            else if(args.length == 3 && sender.hasPermission("skyblockborder.command.color.toggle")) {
                Player player = Bukkit.getPlayer(args[1]);

                if(player == null) {
                    sender.sendMessage(ChatUtils.color(plugin.getLangConfig().getString("messages.invalid_player").replace("%player%", args[1])));
                    return true;
                }
                if(!toggleBorder(player.getUniqueId(), args[2])) {
                    player.sendMessage(ChatUtils.color(plugin.getLangConfig().getString("messages.invalid_toggle_value").replace("%value%", args[2])));
                }else{
                    sender.sendMessage(ChatUtils.color(plugin.getLangConfig().getString("messages.other_player_border_toggled").replace("%value%", (Boolean.parseBoolean(args[2]) ? "enabled" : "disabled")).replace("%player%", player.getName())));
                }
                return true;
            }else if((args.length != 2 && sender.hasPermission("skyblockborder.command.toggle")) || (args.length != 3 && sender.hasPermission("skyblockborder.command.toggle.others"))) {
                sender.sendMessage(ChatUtils.color("&cUsage: /skyblockborder toggle [player] <true/false>"));
                return true;
            }
        }

        if(args[0].equalsIgnoreCase("color")) {
            if(args.length == 2 && sender.hasPermission("skyblockborder.command.color") && sender instanceof Player) {
                Player player = (Player) sender;

                if(!setColor(player, args[1])) {
                    player.sendMessage(ChatUtils.color(plugin.getLangConfig().getString("messages.invalid_color").replace("%color%", args[1])));
                }
                return true;
            }
            else if(args.length == 3 && sender.hasPermission("skyblockborder.command.color.others")) {
                Player player = Bukkit.getPlayer(args[1]);

                if(player == null) {
                    sender.sendMessage(ChatUtils.color(plugin.getLangConfig().getString("messages.invalid_player").replace("%player%", args[1])));
                    return true;
                }
                if(!setColor(player, args[2])) {
                    sender.sendMessage(ChatUtils.color(plugin.getLangConfig().getString("messages.invalid_color").replace("%color%", args[2])));
                }else{
                    sender.sendMessage(ChatUtils.color(plugin.getLangConfig().getString("messages.other_player_color_set").replace("%color%", args[2]).replace("%player%", player.getName())));
                }
                return true;
            }else if((args.length != 2 && sender.hasPermission("skyblockborder.command.color")) || (args.length != 3 && sender.hasPermission("skyblockborder.command.color.others"))) {
                sender.sendMessage(ChatUtils.color("&cUsage: /skyblockborder color [player] <color>"));
                return true;
            }
        }

        if(args[0].equalsIgnoreCase("reload") && sender.hasPermission("skyblockborder.command.reload")) {
            long startTime = System.currentTimeMillis();
            plugin.reload();
            sender.sendMessage(ChatUtils.color(plugin.getLangConfig().getString("messages.config_reload").replace("%time%", String.valueOf(System.currentTimeMillis() - startTime))));
            return true;
        }

        if(sender instanceof Player && guiEnabled)
            plugin.getInventoryManager().getSettingsGUI().openInventory((Player) sender);
        else ((Player) sender).performCommand("skyblockborder about");

        return false;
    }

    private boolean setColor(Player player, String color) {
        if(color.equalsIgnoreCase("RED") || color.equalsIgnoreCase("GREEN") || color.equalsIgnoreCase("BLUE")) {
            plugin.getPlayerManager().setBorderColor(player.getUniqueId(), BorderColor.valueOf(color.toUpperCase()));
            return true;
        }
        return false;
    }

    private boolean toggleBorder(UUID uuid, String value) {
        if(!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) return false;
        plugin.getPlayerManager().setBorderEnabled(uuid, Boolean.parseBoolean(value));
        return true;
    }

}
