package dev.eugenio.nasgarbedwars.maprestore.internal;

import dev.eugenio.nasgarbedwars.maprestore.internal.files.WorldZipper;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.server.ISetupSession;
import dev.eugenio.nasgarbedwars.api.server.RestoreAdapter;
import dev.eugenio.nasgarbedwars.api.util.FileUtil;
import dev.eugenio.nasgarbedwars.api.util.ZipFileUtil;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.arena.VoidGenerator;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InternalAdapter extends RestoreAdapter {
    public static File backupFolder;
    
    public InternalAdapter(final Plugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable(IArena iArena) {
        Bukkit.getScheduler().runTask(this.getOwner(), () -> {
            if (Bukkit.getWorld(iArena.getWorldName()) != null) {
                Bukkit.getScheduler().runTask(this.getOwner(), () -> {
                    World world = Bukkit.getWorld(iArena.getWorldName());
                    iArena.init(world);
                });
                return;
            }
            Bukkit.getScheduler().runTaskAsynchronously(BedWars.getInstance(), () -> {
                File file = new File(backupFolder, iArena.getArenaName() + ".zip");
                File file2 = new File(Bukkit.getWorldContainer(), iArena.getArenaName());
                if (file.exists()) FileUtil.delete(file2);
                if (!file.exists()) {
                    new WorldZipper(iArena.getArenaName(), true);
                } else {
                    try {
                        ZipFileUtil.unzipFileIntoDirectory(file, new File(Bukkit.getWorldContainer(), iArena.getWorldName()));
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                this.deleteWorldTrash(iArena.getWorldName());
                Bukkit.getScheduler().runTask(BedWars.getInstance(), () -> {
                    WorldCreator worldCreator = new WorldCreator(iArena.getWorldName());
                    worldCreator.generateStructures(false);
                    worldCreator.generator(new VoidGenerator());
                    World world = Bukkit.createWorld(worldCreator);
                    world.setKeepSpawnInMemory(true);
                    world.setAutoSave(false);
                });
            });
        });
    }
    
    @Override
    public void onRestart(final IArena arena) {
        Bukkit.getScheduler().runTask(this.getOwner(), () -> {
                if (Arena.getGamesBeforeRestart() == 0) {
                    if (Arena.getArenas().isEmpty()) {
                        BedWars.getInstance().getLogger().info("Reiniciando servidor");
                        Bukkit.getServer().shutdown();
                    }
                } else {
                    if (Arena.getGamesBeforeRestart() != -1) Arena.setGamesBeforeRestart(Arena.getGamesBeforeRestart() - 1);
                    Bukkit.unloadWorld(arena.getWorldName(), false);
                }
            if (!arena.getWorldName().equals(arena.getArenaName())) {
                this.deleteWorld(arena.getWorldName());
            }
        });
    }
    
    @Override
    public void onDisable(final IArena arena) {
        Bukkit.getScheduler().runTask(this.getOwner(), () -> Bukkit.unloadWorld(arena.getWorldName(), false));
    }
    
    @Override
    public void onSetupSessionStart(final ISetupSession setupSession) {
        Bukkit.getScheduler().runTaskAsynchronously(this.getOwner(), () -> {
            File file = new File(backupFolder, setupSession.getWorldName() + ".zip");
            File file2 = new File(Bukkit.getWorldContainer(), setupSession.getWorldName());
            if (file.exists()) {
                FileUtil.delete(file2);
                try {
                    ZipFileUtil.unzipFileIntoDirectory(file, new File(Bukkit.getWorldContainer(), setupSession.getWorldName()));
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            WorldCreator worldCreator = new WorldCreator(setupSession.getWorldName());
            worldCreator.generator(new VoidGenerator());
            worldCreator.generateStructures(false);
            Bukkit.getScheduler().runTask(this.getOwner(), () -> {
                try {
                    File file3 = new File(Bukkit.getWorldContainer(), setupSession.getWorldName() + "/region");
                    if (!file3.exists()) {
                        try {
                            setupSession.getPlayer().sendMessage(ChatColor.GREEN + "Creando un void map: " + setupSession.getWorldName());
                            World world = Bukkit.createWorld(worldCreator);
                            world.setKeepSpawnInMemory(true);
                            Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), setupSession::teleportPlayer, 20L);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            setupSession.close();
                        }
                        return;
                    }
                    setupSession.getPlayer().sendMessage(ChatColor.GREEN + "Cargando " + setupSession.getWorldName() + " de el container de Bukkit worlds.");
                    this.deleteWorldTrash(setupSession.getWorldName());
                    World world = Bukkit.createWorld(worldCreator);
                    world.setKeepSpawnInMemory(true);
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                    setupSession.close();
                    return;
                }
                Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), setupSession::teleportPlayer, 20L);
            });
        });
    }
    
    @Override
    public void onSetupSessionClose(final ISetupSession setupSession) {
        Bukkit.getScheduler().runTask(this.getOwner(), () -> {
            Bukkit.getWorld(setupSession.getWorldName()).save();
            Bukkit.unloadWorld(setupSession.getWorldName(), true);
            Bukkit.getScheduler().runTaskAsynchronously(BedWars.getInstance(), () -> new WorldZipper(setupSession.getWorldName(), true));
        });
    }
    
    @Override
    public void onLobbyRemoval(final IArena arena) {
        Location location = arena.getConfig().getArenaLoc("waiting.Pos1");
        Location location2 = arena.getConfig().getArenaLoc("waiting.Pos2");
        if (location == null || location2 == null) return;
        Bukkit.getScheduler().runTask(BedWars.getInstance(), () -> {
            int n = Math.min(location.getBlockX(), location2.getBlockX());
            int n2 = Math.max(location.getBlockX(), location2.getBlockX());
            int n3 = Math.min(location.getBlockY(), location2.getBlockY());
            int n4 = Math.max(location.getBlockY(), location2.getBlockY());
            int n5 = Math.min(location.getBlockZ(), location2.getBlockZ());
            int n6 = Math.max(location.getBlockZ(), location2.getBlockZ());
            for (int i = n; i < n2; ++i) for (int j = n3; j < n4; ++j) for (int k = n5; k < n6; ++k) location.getWorld().getBlockAt(i, j, k).setType(Material.AIR);
            Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> location.getWorld().getEntities().forEach(entity -> {
                if (entity instanceof Item) entity.remove();
            }), 15L);
        });
    }
    
    @Override
    public boolean isWorld(final String s) {
        return new File(Bukkit.getWorldContainer(), s + "/region").exists();
    }
    
    @Override
    public void deleteWorld(final String s) {
        Bukkit.getScheduler().runTaskAsynchronously(this.getOwner(), () -> {
            try {
                FileUtils.deleteDirectory(new File(Bukkit.getWorldContainer(), s));
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
    
    @Override
    public void cloneArena(final String s, final String s2) {
        Bukkit.getScheduler().runTaskAsynchronously(BedWars.getInstance(), () -> {
            try {
                FileUtils.copyDirectory(new File(Bukkit.getWorldContainer(), s), new File(Bukkit.getWorldContainer(), s2));
                this.deleteWorldTrash(s2);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
    
    @Override
    public List<String> getWorldsList() {
        final ArrayList<String> list = new ArrayList<>();
        final File worldContainer = Bukkit.getWorldContainer();
        if (worldContainer.exists()) for (final File file : Objects.requireNonNull(worldContainer.listFiles())) if (file.isDirectory() && new File(file.getName() + "/region").exists() && !file.getName().startsWith("bw_temp")) list.add(file.getName());
        return list;
    }
    
    @Override
    public void convertWorlds() {
        final File file = new File(BedWars.getInstance().getDataFolder(), "/Arenas");
        if (file.exists()) {
            final ArrayList<File> list = new ArrayList<>();
            for (final File file2 : Objects.requireNonNull(file.listFiles())) if (file2.isFile() && file2.getName().contains(".yml")) list.add(file2);
            final ArrayList<File> list2 = new ArrayList<>();
            final ArrayList<File> list3 = new ArrayList<>();
            for (final File file3 : list) {
                if (!file3.getName().equals(file3.getName().toLowerCase())) {
                    final File file4 = new File(file.getPath() + "/" + file3.getName().toLowerCase());
                    if (!file3.renameTo(file4)) {
                        list2.add(file3);
                        BedWars.getInstance().getLogger().severe("No se ha podido renombrar " + file3.getName() + " a " + file3.getName().toLowerCase() + "; hazlo manualmente.");
                    } else {
                        list3.add(file4);
                        list2.add(file3);
                    }
                    final File file5 = new File(BedWars.getInstance().getServer().getWorldContainer(), file3.getName().replace(".yml", ""));
                    if (file5.exists() && !file5.getName().equals(file5.getName().toLowerCase()) && !file5.renameTo(new File(BedWars.getInstance().getServer().getWorldContainer().getPath() + "/" + file5.getName().toLowerCase()))) {
                        BedWars.getInstance().getLogger().severe("No se ha podido renombrar " + file5.getName() + " carpeta a " + file5.getName().toLowerCase() + "; hazlo manualmente.");
                        list2.add(file3);
                        return;
                    }
                }
            }
            for (File value : list2) list.remove(value);
            list.addAll(list3);
        }
        Bukkit.getScheduler().runTaskAsynchronously(this.getOwner(), () -> {
            File[] arrayOfFile = Bukkit.getWorldContainer().listFiles();
            if (arrayOfFile != null) for (File file5 : arrayOfFile) if (file5 != null && file5.isDirectory() && file5.getName().contains("bw_temp_")) deleteWorld(file5.getName());
        });
    }
    
    private void deleteWorldTrash(final String s) {
        for (final File file : new File[] { new File(Bukkit.getWorldContainer(), s + "/level.dat"), new File(Bukkit.getWorldContainer(), s + "/level.dat_mcr"), new File(Bukkit.getWorldContainer(), s + "/level.dat_old"), new File(Bukkit.getWorldContainer(), s + "/session.lock"), new File(Bukkit.getWorldContainer(), s + "/uid.dat") }) {
            if (file.exists() && !file.delete()) {
                this.getOwner().getLogger().warning("No se ha podido eliminar: " + file.getPath());
                this.getOwner().getLogger().warning("Esto puede causar problemas.");
            }
        }
    }
    
    static {
        InternalAdapter.backupFolder = new File(BedWars.getInstance().getDataFolder() + "/Cache");
    }
}
