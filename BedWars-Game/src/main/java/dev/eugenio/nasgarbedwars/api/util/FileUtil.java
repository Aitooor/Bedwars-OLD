package dev.eugenio.nasgarbedwars.api.util;

import dev.eugenio.nasgarbedwars.api.server.NMSUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class FileUtil {
    public static void delete(final File file) {
        if (file.isDirectory()) {
            final File[] listFiles = file.listFiles();
            for (int length = listFiles.length, i = 0; i < length; ++i) {
                delete(listFiles[i]);
            }
        } else {
            file.delete();
        }
    }

    public static void setMainLevel(final String s, final NMSUtil NMSUtil) {
        final Properties properties = new Properties();
        try (final FileInputStream fileInputStream = new FileInputStream("server.properties")) {
            properties.load(fileInputStream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        properties.setProperty("level-name", s);
        properties.setProperty("generator-settings", (NMSUtil.getVersion() > 5) ? "minecraft:air;minecraft:air;minecraft:air" : "1;0;1");
        properties.setProperty("allow-nether", "false");
        properties.setProperty("level-type", "flat");
        properties.setProperty("generate-structures", "false");
        properties.setProperty("spawn-monsters", "false");
        properties.setProperty("max-world-size", "1000");
        properties.setProperty("spawn-animals", "false");
        try (final FileOutputStream fileOutputStream = new FileOutputStream("server.properties")) {
            properties.store(fileOutputStream, null);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
