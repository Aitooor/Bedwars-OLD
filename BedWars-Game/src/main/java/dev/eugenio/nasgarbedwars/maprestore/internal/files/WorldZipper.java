package dev.eugenio.nasgarbedwars.maprestore.internal.files;

import dev.eugenio.nasgarbedwars.api.util.ZipFileUtil;
import dev.eugenio.nasgarbedwars.maprestore.internal.InternalAdapter;
import org.bukkit.Bukkit;

import java.io.File;

public class WorldZipper {
    private final String worldName;
    private final boolean replace;
    
    public WorldZipper(final String worldName, final boolean replace) {
        this.worldName = worldName;
        this.replace = replace;
        this.execute();
    }
    
    private void execute() {
        if (this.exists()) if (!this.replace) return;
        try {
            this.zipWorldFolder();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void zipWorldFolder() {
        ZipFileUtil.zipDirectory(this.getWorldFolder(), this.getBackupFile());
    }
    
    private File getWorldFolder() {
        return new File(Bukkit.getWorldContainer(), this.worldName);
    }
    
    private File getBackupFile() {
        return new File(InternalAdapter.backupFolder, this.worldName + ".zip");
    }
    
    private boolean exists() {
        return this.getWorldFolder().isDirectory();
    }
}
