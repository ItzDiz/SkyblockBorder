package fun.lewisdev.skyblockborder.utility;

import org.bukkit.Bukkit;

public class NMSUtil {

    private final static String NMS_PACKAGE = "net.minecraft.server.";
    private final static String CRAFTBUKKIT_PACKAGE = "org.bukkit.craftbukkit.";

    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().substring(23);
    }

    public static int getVersionNumber() {
        return Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].replace(".","#").split("#")[1]);
    }

    public static Class<?> getNMSClass(String className) {
        String fullClass = NMS_PACKAGE + getVersion() + "." + className;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(fullClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clazz;
    }

    public static Class<?> getCraftClass(String className) {
        String fullClass = CRAFTBUKKIT_PACKAGE + getVersion() + "." + className;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(fullClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clazz;
    }

}
