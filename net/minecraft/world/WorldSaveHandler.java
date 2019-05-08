package net.minecraft.world;

import org.apache.logging.log4j.LogManager;
import net.minecraft.util.TagHelper;
import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.level.storage.LevelStorage;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.OutputStream;
import net.minecraft.nbt.NbtIo;
import java.io.FileOutputStream;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.util.SystemUtil;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import com.mojang.datafixers.DataFixer;
import net.minecraft.structure.StructureManager;
import java.io.File;
import org.apache.logging.log4j.Logger;

public class WorldSaveHandler implements PlayerSaveHandler
{
    private static final Logger LOGGER;
    private final File worldDir;
    private final File playerDataDir;
    private final long saveStartTime;
    private final String worldName;
    private final StructureManager structureManager;
    protected final DataFixer dataFixer;
    
    public WorldSaveHandler(final File worldsDirectory, final String worldName, @Nullable final MinecraftServer server, final DataFixer dataFixer) {
        this.saveStartTime = SystemUtil.getMeasuringTimeMs();
        this.dataFixer = dataFixer;
        (this.worldDir = new File(worldsDirectory, worldName)).mkdirs();
        this.playerDataDir = new File(this.worldDir, "playerdata");
        this.worldName = worldName;
        if (server != null) {
            this.playerDataDir.mkdirs();
            this.structureManager = new StructureManager(server, this.worldDir, dataFixer);
        }
        else {
            this.structureManager = null;
        }
        this.writeSessionLock();
    }
    
    public void saveWorld(final LevelProperties levelProperties, @Nullable final CompoundTag compoundTag) {
        levelProperties.setVersion(19133);
        final CompoundTag compoundTag2 = levelProperties.cloneWorldTag(compoundTag);
        final CompoundTag compoundTag3 = new CompoundTag();
        compoundTag3.put("Data", compoundTag2);
        try {
            final File file5 = new File(this.worldDir, "level.dat_new");
            final File file6 = new File(this.worldDir, "level.dat_old");
            final File file7 = new File(this.worldDir, "level.dat");
            NbtIo.writeCompressed(compoundTag3, new FileOutputStream(file5));
            if (file6.exists()) {
                file6.delete();
            }
            file7.renameTo(file6);
            if (file7.exists()) {
                file7.delete();
            }
            file5.renameTo(file7);
            if (file5.exists()) {
                file5.delete();
            }
        }
        catch (Exception exception5) {
            exception5.printStackTrace();
        }
    }
    
    private void writeSessionLock() {
        try {
            final File file1 = new File(this.worldDir, "session.lock");
            final DataOutputStream dataOutputStream2 = new DataOutputStream(new FileOutputStream(file1));
            try {
                dataOutputStream2.writeLong(this.saveStartTime);
            }
            finally {
                dataOutputStream2.close();
            }
        }
        catch (IOException iOException1) {
            iOException1.printStackTrace();
            throw new RuntimeException("Failed to check session lock, aborting");
        }
    }
    
    public File getWorldDir() {
        return this.worldDir;
    }
    
    public void checkSessionLock() throws SessionLockException {
        try {
            final File file1 = new File(this.worldDir, "session.lock");
            final DataInputStream dataInputStream2 = new DataInputStream(new FileInputStream(file1));
            try {
                if (dataInputStream2.readLong() != this.saveStartTime) {
                    throw new SessionLockException("The save is being accessed from another location, aborting");
                }
            }
            finally {
                dataInputStream2.close();
            }
        }
        catch (IOException iOException1) {
            throw new SessionLockException("Failed to check session lock, aborting");
        }
    }
    
    @Nullable
    public LevelProperties readProperties() {
        File file1 = new File(this.worldDir, "level.dat");
        if (file1.exists()) {
            final LevelProperties levelProperties2 = LevelStorage.readLevelProperties(file1, this.dataFixer);
            if (levelProperties2 != null) {
                return levelProperties2;
            }
        }
        file1 = new File(this.worldDir, "level.dat_old");
        if (file1.exists()) {
            return LevelStorage.readLevelProperties(file1, this.dataFixer);
        }
        return null;
    }
    
    public void saveWorld(final LevelProperties levelProperties) {
        this.saveWorld(levelProperties, null);
    }
    
    @Override
    public void savePlayerData(final PlayerEntity playerEntity) {
        try {
            final CompoundTag compoundTag2 = playerEntity.toTag(new CompoundTag());
            final File file3 = new File(this.playerDataDir, playerEntity.getUuidAsString() + ".dat.tmp");
            final File file4 = new File(this.playerDataDir, playerEntity.getUuidAsString() + ".dat");
            NbtIo.writeCompressed(compoundTag2, new FileOutputStream(file3));
            if (file4.exists()) {
                file4.delete();
            }
            file3.renameTo(file4);
        }
        catch (Exception exception2) {
            WorldSaveHandler.LOGGER.warn("Failed to save player data for {}", playerEntity.getName().getString());
        }
    }
    
    @Nullable
    @Override
    public CompoundTag loadPlayerData(final PlayerEntity playerEntity) {
        CompoundTag compoundTag2 = null;
        try {
            final File file3 = new File(this.playerDataDir, playerEntity.getUuidAsString() + ".dat");
            if (file3.exists() && file3.isFile()) {
                compoundTag2 = NbtIo.readCompressed(new FileInputStream(file3));
            }
        }
        catch (Exception exception3) {
            WorldSaveHandler.LOGGER.warn("Failed to load player data for {}", playerEntity.getName().getString());
        }
        if (compoundTag2 != null) {
            final int integer3 = compoundTag2.containsKey("DataVersion", 3) ? compoundTag2.getInt("DataVersion") : -1;
            playerEntity.fromTag(TagHelper.update(this.dataFixer, DataFixTypes.b, compoundTag2, integer3));
        }
        return compoundTag2;
    }
    
    public String[] getSavedPlayerIds() {
        String[] arr1 = this.playerDataDir.list();
        if (arr1 == null) {
            arr1 = new String[0];
        }
        for (int integer2 = 0; integer2 < arr1.length; ++integer2) {
            if (arr1[integer2].endsWith(".dat")) {
                arr1[integer2] = arr1[integer2].substring(0, arr1[integer2].length() - 4);
            }
        }
        return arr1;
    }
    
    public StructureManager getStructureManager() {
        return this.structureManager;
    }
    
    public DataFixer getDataFixer() {
        return this.dataFixer;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
