package dev.eugenio.nasgarbedwars.arena.mapreset.slime;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.IntTag;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;
import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.*;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.server.ISetupSession;
import dev.eugenio.nasgarbedwars.api.server.RestoreAdapter;
import dev.eugenio.nasgarbedwars.api.util.FileUtil;
import dev.eugenio.nasgarbedwars.api.util.ZipFileUtil;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class SlimeAdapter extends RestoreAdapter {
    private final SlimePlugin slime;
    private final BedWarsAPI api;

    public SlimeAdapter(final Plugin plugin) {
        super(plugin);
        this.slime = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        this.api = Bukkit.getServer().getServicesManager().getRegistration(BedWarsAPI.class).getProvider();
    }

    @Override
    public void onEnable(final IArena arena) {
        Bukkit.getScheduler().runTaskAsynchronously(getOwner(), () -> {
            if (Bukkit.getWorld(arena.getWorldName()) != null) {
                Bukkit.getScheduler().runTask(getOwner(), () -> {
                    World world = Bukkit.getWorld(arena.getWorldName());
                    arena.init(world);
                });
                return;
            }
            SlimeLoader slimeLoader = this.slime.getLoader("file");
            String[] arrayOfString = arena.getConfig().getString("waiting.Loc").split(",");
            SlimePropertyMap slimePropertyMap = new SlimePropertyMap();
            slimePropertyMap.setString(SlimeProperties.WORLD_TYPE, "flat");
            slimePropertyMap.setInt(SlimeProperties.SPAWN_X, (int) Double.parseDouble(arrayOfString[0]));
            slimePropertyMap.setInt(SlimeProperties.SPAWN_Y, (int) Double.parseDouble(arrayOfString[1]));
            slimePropertyMap.setInt(SlimeProperties.SPAWN_Z, (int) Double.parseDouble(arrayOfString[2]));
            slimePropertyMap.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
            slimePropertyMap.setBoolean(SlimeProperties.ALLOW_MONSTERS, false);
            slimePropertyMap.setString(SlimeProperties.DIFFICULTY, "easy");
            slimePropertyMap.setBoolean(SlimeProperties.PVP, true);
            try {
                SlimeWorld slimeWorld = this.slime.loadWorld(slimeLoader, arena.getArenaName(), true, slimePropertyMap);
                Bukkit.getScheduler().runTaskAsynchronously(this.getOwner(), () -> this.slime.generateWorld(slimeWorld));
            } catch (UnknownWorldException | IOException | com.grinderwolf.swm.api.exceptions.CorruptedWorldException | com.grinderwolf.swm.api.exceptions.NewerFormatException | com.grinderwolf.swm.api.exceptions.WorldInUseException unknownWorldException) {
                this.api.getArenaUtil().removeFromEnableQueue(arena);
                unknownWorldException.printStackTrace();
            } catch (ConcurrentModificationException concurrentModificationException) {
                concurrentModificationException.printStackTrace();
                this.api.getArenaUtil().removeFromEnableQueue(arena);
                getOwner().getLogger().severe("Intentando cargar otra vez arena: " + arena.getArenaName());
                onEnable(arena);
            }
        });
    }

    @Override
    public void onRestart(final IArena arena) {
        if (this.api.getArenaUtil().getGamesBeforeRestart() == 0) {
            if (this.api.getArenaUtil().getArenas().isEmpty()) {
                this.getOwner().getLogger().info("Haciendo comando: " + this.api.getConfigs().getMainConfig().getString("bungee-settings.restart-cmd"));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.api.getConfigs().getMainConfig().getString("bungee-settings.restart-cmd"));
            }
        } else {
            if (this.api.getArenaUtil().getGamesBeforeRestart() != -1) {
                this.api.getArenaUtil().setGamesBeforeRestart(this.api.getArenaUtil().getGamesBeforeRestart() - 1);
            }
            Bukkit.getScheduler().runTaskAsynchronously(this.getOwner(), () -> Bukkit.unloadWorld(arena.getWorldName(), false));
        }
    }

    @Override
    public void onDisable(final IArena arena) {
        Bukkit.getScheduler().runTask(this.getOwner(), () -> Bukkit.unloadWorld(arena.getWorldName(), false));
    }

    @Override
    public void onSetupSessionStart(ISetupSession iSetupSession) {
        Bukkit.getScheduler().runTaskAsynchronously(this.getOwner(), () -> {
            SlimeLoader slimeLoader = this.slime.getLoader("file");
            String[] stringArray = new String[]{"0", "50", "0"};
            if (iSetupSession.getConfig().getYml().getString("waiting.Loc") != null) {
                stringArray = iSetupSession.getConfig().getString("waiting.Loc").split(",");
            }
            SlimePropertyMap slimePropertyMap = new SlimePropertyMap();
            slimePropertyMap.setString(SlimeProperties.WORLD_TYPE, "flat");
            slimePropertyMap.setInt(SlimeProperties.SPAWN_X, (int) Double.parseDouble(stringArray[0]));
            slimePropertyMap.setInt(SlimeProperties.SPAWN_Y, (int) Double.parseDouble(stringArray[1]));
            slimePropertyMap.setInt(SlimeProperties.SPAWN_Z, (int) Double.parseDouble(stringArray[2]));
            slimePropertyMap.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
            slimePropertyMap.setBoolean(SlimeProperties.ALLOW_MONSTERS, false);
            slimePropertyMap.setString(SlimeProperties.DIFFICULTY, "easy");
            slimePropertyMap.setBoolean(SlimeProperties.PVP, true);
            try {
                SlimeWorld slimeWorld;
                if (Bukkit.getWorld(iSetupSession.getWorldName()) != null) {
                    Bukkit.getScheduler().runTask(this.getOwner(), () -> Bukkit.unloadWorld(iSetupSession.getWorldName(), false));
                }
                if (slimeLoader.worldExists(iSetupSession.getWorldName())) {
                    slimeWorld = this.slime.loadWorld(slimeLoader, iSetupSession.getWorldName(), false, slimePropertyMap);
                    Bukkit.getScheduler().runTask(this.getOwner(), () -> iSetupSession.getPlayer().sendMessage(ChatColor.GREEN + "Convirtiendo mundo a un contenedor de SWM."));
                } else if (new File(Bukkit.getWorldContainer(), iSetupSession.getWorldName() + "/level.dat").exists()) {
                    Bukkit.getScheduler().runTask(this.getOwner(), () -> iSetupSession.getPlayer().sendMessage(ChatColor.GREEN + "Importando mundo a SWM."));
                    this.slime.importWorld(new File(Bukkit.getWorldContainer(), iSetupSession.getWorldName()), iSetupSession.getWorldName().toLowerCase(), slimeLoader);
                    slimeWorld = this.slime.loadWorld(slimeLoader, iSetupSession.getWorldName(), false, slimePropertyMap);
                } else {
                    Bukkit.getScheduler().runTask(this.getOwner(), () -> iSetupSession.getPlayer().sendMessage(ChatColor.GREEN + "Creando un nuevo mapa vacío."));
                    slimeWorld = this.slime.createEmptyWorld(slimeLoader, iSetupSession.getWorldName(), false, slimePropertyMap);
                }
                SlimeWorld slimeWorld2 = slimeWorld;
                Bukkit.getScheduler().runTask(this.getOwner(), () -> {
                    this.slime.generateWorld(slimeWorld2);
                    iSetupSession.teleportPlayer();
                });
            } catch (CorruptedWorldException | InvalidWorldException | NewerFormatException | UnknownWorldException | WorldAlreadyExistsException | WorldInUseException | WorldLoadedException | WorldTooBigException | IOException throwable) {
                iSetupSession.getPlayer().sendMessage(ChatColor.RED + "Ha ocurrido un error. Mira la consola.");
                throwable.printStackTrace();
                iSetupSession.close();
            }
        });
    }

    @Override
    public void onSetupSessionClose(final ISetupSession setupSession) {
        Bukkit.getWorld(setupSession.getWorldName()).save();
        Bukkit.getScheduler().runTaskAsynchronously(this.getOwner(), () -> Bukkit.unloadWorld(setupSession.getWorldName(), true));
    }

    @Override
    public void onLobbyRemoval(IArena iArena) {
        Location location = iArena.getConfig().getArenaLoc("waiting.Pos1");
        Location location2 = iArena.getConfig().getArenaLoc("waiting.Pos2");
        if (location == null || location2 == null) {
            return;
        }
        Bukkit.getScheduler().runTask(this.getOwner(), () -> {
            int n = Math.min(location.getBlockX(), location2.getBlockX());
            int n2 = Math.max(location.getBlockX(), location2.getBlockX());
            int n3 = Math.min(location.getBlockY(), location2.getBlockY());
            int n4 = Math.max(location.getBlockY(), location2.getBlockY());
            int n5 = Math.min(location.getBlockZ(), location2.getBlockZ());
            int n6 = Math.max(location.getBlockZ(), location2.getBlockZ());
            for (int i = n; i < n2; ++i) {
                for (int j = n3; j < n4; ++j) {
                    for (int k = n5; k < n6; ++k) {
                        location.getWorld().getBlockAt(i, j, k).setType(Material.AIR);
                    }
                }
            }
            Bukkit.getScheduler().runTaskLater(this.getOwner(), () -> location.getWorld().getEntities().forEach(entity -> {
                if (entity instanceof Item) entity.remove();
            }), 15L);
        });
    }

    @Override
    public boolean isWorld(final String s) {
        try {
            return this.slime.getLoader("file").worldExists(s);
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public void deleteWorld(String string) {
        Bukkit.getScheduler().runTaskAsynchronously(this.getOwner(), () -> {
            try {
                this.slime.getLoader("file").deleteWorld(string);
            } catch (UnknownWorldException | IOException throwable) {
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public void cloneArena(String string, String string2) {
        Bukkit.getScheduler().runTaskAsynchronously(this.getOwner(), () -> {
            SlimePropertyMap slimePropertyMap = new SlimePropertyMap();
            slimePropertyMap.setString(SlimeProperties.WORLD_TYPE, "flat");
            slimePropertyMap.setInt(SlimeProperties.SPAWN_X, 0);
            slimePropertyMap.setInt(SlimeProperties.SPAWN_Y, 118);
            slimePropertyMap.setInt(SlimeProperties.SPAWN_Z, 0);
            slimePropertyMap.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
            slimePropertyMap.setBoolean(SlimeProperties.ALLOW_MONSTERS, false);
            slimePropertyMap.setString(SlimeProperties.DIFFICULTY, "easy");
            slimePropertyMap.setBoolean(SlimeProperties.PVP, true);
            try {
                SlimeWorld slimeWorld = this.slime.loadWorld(this.slime.getLoader("file"), string, true, slimePropertyMap);
                slimeWorld.clone(string2, this.slime.getLoader("file"));
            } catch (CorruptedWorldException | NewerFormatException | UnknownWorldException | WorldAlreadyExistsException | WorldInUseException | IOException throwable) {
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public List<String> getWorldsList() {
        try {
            return this.slime.getLoader("file").listWorlds();
        } catch (IOException ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void convertWorlds() {
        File[] fileArray;
        File file = new File(this.getOwner().getDataFolder(), "/Arenas");
        SlimeLoader slimeLoader = this.slime.getLoader("file");
        if (file.exists() && (fileArray = file.listFiles()) != null) {
            for (File file2 : fileArray) {
                if (!file2.isFile() || !file2.getName().endsWith(".yml")) continue;
                String string = file2.getName().replace(".yml", "").toLowerCase();
                File file3 = new File(Bukkit.getWorldContainer(), file2.getName().replace(".yml", ""));
                try {
                    if (slimeLoader.worldExists(string)) continue;
                    if (!file2.getName().equals(string) && !file2.renameTo(new File(file, string + ".yml"))) {
                        this.getOwner().getLogger().log(Level.WARNING, "Could not rename " + file2.getName() + ".yml to " + string + ".yml");
                    }
                    File file4 = new File(this.getOwner().getDataFolder() + "/Cache", file3.getName() + ".zip");
                    if (file3.exists() && file4.exists()) {
                        FileUtil.delete(file3);
                        ZipFileUtil.unzipFileIntoDirectory(file4, new File(Bukkit.getWorldContainer(), string));
                    }
                    this.deleteWorldTrash(string);
                    this.handleLevelDat(string);
                    this.convertWorld(string, null);
                } catch (IOException iOException) {
                    iOException.printStackTrace();
                }
            }
        }
        Bukkit.getScheduler().runTaskAsynchronously(getOwner(), () -> {
            File[] arrayOfFile = Bukkit.getWorldContainer().listFiles();
            if (arrayOfFile != null)
                for (File file1 : arrayOfFile) {
                    if (file1 != null && file1.isDirectory() && file1.getName().contains("bw_temp_"))
                        try {
                            FileUtils.deleteDirectory(file);
                        } catch (IOException iOException) {
                            iOException.printStackTrace();
                        }
                }
        });
    }


    private void convertWorld(String string, Player player) {
        SlimeLoader slimeLoader = this.slime.getLoader("file");
        try {
            this.getOwner().getLogger().log(Level.INFO, "Convirtiendo " + string + " a formato Slime.");
            this.slime.importWorld(new File(Bukkit.getWorldContainer(), string), string, slimeLoader);
        } catch (InvalidWorldException | WorldAlreadyExistsException | WorldLoadedException | WorldTooBigException | IOException throwable) {
            if (player != null) {
                player.sendMessage(ChatColor.RED + "No se ha podido convertir " + string + " a formato Slime.\nMira la consola para ver más detalles.");
                ISetupSession iSetupSession = this.api.getSetupSession(player.getUniqueId());
                if (iSetupSession != null) iSetupSession.close();
            }
            this.getOwner().getLogger().log(Level.WARNING, "No se ha podido convertir " + string + " a formato Slime.");
            throwable.printStackTrace();
        }
    }

    private void deleteWorldTrash(final String s) {
        for (final File file : new File[]{new File(Bukkit.getWorldContainer(), s + "/level.dat_mcr"), new File(Bukkit.getWorldContainer(), s + "/level.dat_old"), new File(Bukkit.getWorldContainer(), s + "/session.lock"), new File(Bukkit.getWorldContainer(), s + "/uid.dat")}) {
            if (file.exists() && !file.delete()) this.getOwner().getLogger().warning("No se ha podido eliminar: " + file.getPath());
        }
    }

    private void handleLevelDat(String string2) {
        File file;
        File file2 = new File(Bukkit.getWorldContainer(), string2 + "/level.dat");
        try {
            if (!file2.exists() && file2.createNewFile() && (file = new File(Bukkit.getWorldContainer(), "world/region")).exists() && ((String[]) Objects.requireNonNull((Object) file.list())).length > 0 && Arrays.stream(Objects.requireNonNull(file.list())).filter(string -> string.endsWith(".mca")).toArray().length > 0) {
                Optional<CompoundTag> optional;
                File file3 = new File(Bukkit.getWorldContainer(), string2 + "/" + Arrays.stream(Objects.requireNonNull(file.list())).filter(string -> string.endsWith(".mca")).toArray()[0]);
                NBTInputStream nBTInputStream = new NBTInputStream(new FileInputStream(file3));
                Optional<CompoundTag> optional2 = nBTInputStream.readTag().getAsCompoundTag();
                nBTInputStream.close();
                if (optional2.isPresent() && (optional = (optional2.get()).getAsCompoundTag("Chunk")).isPresent()) {
                    int n = (optional.get()).getIntValue("DataVersion").orElse(-1);
                    NBTOutputStream nBTOutputStream = new NBTOutputStream(new FileOutputStream(file2));
                    CompoundMap compoundMap = new CompoundMap();
                    compoundMap.put(new IntTag("SpawnX", 0));
                    compoundMap.put(new IntTag("SpawnY", 255));
                    compoundMap.put(new IntTag("SpawnZ", 0));
                    if (n != -1) compoundMap.put(new IntTag("DataVersion", n));
                    CompoundTag compoundTag = new CompoundTag("Data", compoundMap);
                    nBTOutputStream.writeTag(compoundTag);
                    nBTOutputStream.flush();
                    nBTOutputStream.close();
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
