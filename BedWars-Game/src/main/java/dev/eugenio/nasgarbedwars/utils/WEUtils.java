package dev.eugenio.nasgarbedwars.utils;

//import com.boydti.fawe.FaweAPI;

public class WEUtils {
    /*public static void pasteSchem(String schemName, Location loc, String orientation) {
        AffineTransform transform = new AffineTransform();
        try {
            switch (orientation) {
                case "south":
                    transform = transform.rotateY(450.0);
                    break;
                case "east":
                    transform = transform.rotateY(540.0);
                    break;
                case "north":
                    transform = transform.rotateY(270.0);
                    break;
            }

            FaweAPI.load(new File(BedWars.getInstance().getDataFolder() + "/toppers/" + schemName + ".schematic"))
                    .paste(FaweAPI.getWorld(loc.getWorld().getName()), new com.sk89q.worldedit.Vector(loc.getX(), loc.getY(), loc.getZ()), false, false, transform);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String determineFacing(String team, YamlConfiguration config) {
        if (config.getInt("Team." + team + "." + "TopperFace.yaw") == 90 && config.getInt("Team." + team + "." + "TopperFace.pitch") == 0) return "west";
        if (config.getInt("Team." + team + "." + "TopperFace.yaw") == 0 && config.getInt("Team." + team + "." + "TopperFace.pitch") == 0) return "south";
        if (config.getInt("Team." + team + "." + "TopperFace.yaw") == -90 && config.getInt("Team." + team + "." + "TopperFace.pitch") == 0) return "east";
        if (config.getInt("Team." + team + "." + "TopperFace.yaw") == -180 && config.getInt("Team." + team + "." + "TopperFace.pitch") == 0) return "north";
        return "";
    }*/
}