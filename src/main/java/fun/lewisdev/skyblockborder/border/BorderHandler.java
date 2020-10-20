package fun.lewisdev.skyblockborder.border;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import fun.lewisdev.skyblockborder.utility.NMSUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BorderHandler {

    private static Method sendPacket, getHandle;
    private static Field playerConnection;

    static {
        try {
            sendPacket = NMSUtil.getNMSClass("PlayerConnection").getMethod("sendPacket", NMSUtil.getNMSClass("Packet"));
            getHandle = NMSUtil.getCraftClass("entity.CraftPlayer").getMethod("getHandle");
            playerConnection = NMSUtil.getNMSClass("EntityPlayer").getField("playerConnection");
        } catch (NoSuchMethodException | SecurityException | IllegalArgumentException | NoSuchFieldException ex) {
            ex.printStackTrace();
        }
    }

    public static void sendWorldBorder(Player p, Location center, double size, BorderColor color) {
        try {
            Object world = NMSUtil.getCraftClass("CraftWorld").getMethod("getHandle").invoke(p.getWorld());
            Class<?> worldBorderClass = NMSUtil.getNMSClass("WorldBorder");
            Object worldBorder = NMSUtil.getNMSClass("WorldBorder").newInstance();

            // Set the senter X and Z location
            worldBorderClass.getMethod("setCenter", double.class, double.class).invoke(worldBorder, (double) center.getBlockX(), (double) center.getBlockZ());

            // Set size of border (radius)
            worldBorderClass.getMethod("setSize", double.class).invoke(worldBorder, size);

            // Set warning time of border
            worldBorderClass.getMethod("setWarningTime", int.class).invoke(worldBorder, 0);

            // Set the world
            worldBorderClass.getField("world").set(worldBorder, world);

            // Set border color
            Method transitionSizeBetween = worldBorderClass.getMethod("transitionSizeBetween", double.class, double.class, long.class);
            if (color == BorderColor.GREEN)
                transitionSizeBetween.invoke(worldBorder, size - 0.1D, size, 20000000L);
            else if (color == BorderColor.RED)
                transitionSizeBetween.invoke(worldBorder, size, size - 1.0D, 20000000L);

            // Creation of packet
            Class<?> packetPlayOutWorldBorder = NMSUtil.getNMSClass("PacketPlayOutWorldBorder");
            Class<?> enumBorder;
            if(NMSUtil.getVersionNumber() > 10) enumBorder = packetPlayOutWorldBorder.getDeclaredClasses()[0];
            else enumBorder = packetPlayOutWorldBorder.getDeclaredClasses()[1];

            Object worldborderPacket = packetPlayOutWorldBorder.getConstructor(worldBorderClass, enumBorder).newInstance(worldBorder, Enum.valueOf((Class<Enum>) enumBorder, "INITIALIZE"));

            // Send packet
            sendPacket.invoke(playerConnection.get(getHandle.invoke(p)), worldborderPacket);
        }catch(Exception e) {
            e.printStackTrace();
        }

    }
}