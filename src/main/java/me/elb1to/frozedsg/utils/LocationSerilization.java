package me.elb1to.frozedsg.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationSerilization {

    public static String serialize(final Location location) {
        return location.getWorld().getName() + ":" + location.getX() + ":" + location.getY() + ":" + location.getZ() + ":" + location.getYaw() + ":" + location.getPitch();
    }

    public static Location deserialize(final String source) {
        String[] split = source.split(":");
        return new Location(Bukkit.getServer().getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
    }

    public static String getWorldNameFor(String source) {
        String[] split = source.split(":");

        return split[0];
    }
}
