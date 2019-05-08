package net.minecraft.world.updater;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.regex.Matcher;
import net.minecraft.world.storage.RegionFile;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.nbt.CompoundTag;
import java.util.List;
import java.util.Iterator;
import net.minecraft.util.crash.CrashException;
import java.io.IOException;
import net.minecraft.SharedConstants;
import net.minecraft.world.VersionedChunkStorage;
import net.minecraft.world.chunk.ChunkPos;
import java.util.ListIterator;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableTextComponent;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import net.minecraft.util.SystemUtil;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.PersistentStateManager;
import java.util.regex.Pattern;
import net.minecraft.text.TextComponent;
import net.minecraft.world.dimension.DimensionType;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import java.io.File;
import net.minecraft.world.WorldSaveHandler;
import java.util.concurrent.ThreadFactory;
import org.apache.logging.log4j.Logger;

public class WorldUpdater
{
    private static final Logger LOGGER;
    private static final ThreadFactory UPDATE_THREAD_FACTORY;
    private final String levelName;
    private final boolean eraseCache;
    private final WorldSaveHandler worldSaveHandler;
    private final Thread updateThread;
    private final File worldDirectory;
    private volatile boolean keepUpgradingChunks;
    private volatile boolean isDone;
    private volatile float progress;
    private volatile int totalChunkCount;
    private volatile int upgradedChunkCount;
    private volatile int skippedChunkCount;
    private final Object2FloatMap<DimensionType> dimensionProgress;
    private volatile TextComponent status;
    private static final Pattern REGION_FILE_PATTERN;
    private final PersistentStateManager persistentStateManager;
    
    public WorldUpdater(final String string, final LevelStorage levelStorage, final LevelProperties levelProperties, final boolean eraseCache) {
        this.keepUpgradingChunks = true;
        this.dimensionProgress = (Object2FloatMap<DimensionType>)Object2FloatMaps.synchronize((Object2FloatMap)new Object2FloatOpenCustomHashMap((Hash.Strategy)SystemUtil.identityHashStrategy()));
        this.status = new TranslatableTextComponent("optimizeWorld.stage.counting", new Object[0]);
        this.levelName = levelProperties.getLevelName();
        this.eraseCache = eraseCache;
        (this.worldSaveHandler = levelStorage.createSaveHandler(string, null)).saveWorld(levelProperties);
        this.persistentStateManager = new PersistentStateManager(new File(DimensionType.a.getFile(this.worldSaveHandler.getWorldDir()), "data"), this.worldSaveHandler.getDataFixer());
        this.worldDirectory = this.worldSaveHandler.getWorldDir();
        (this.updateThread = WorldUpdater.UPDATE_THREAD_FACTORY.newThread(this::updateWorld)).setUncaughtExceptionHandler((thread, throwable) -> {
            WorldUpdater.LOGGER.error("Error upgrading world", throwable);
            this.status = new TranslatableTextComponent("optimizeWorld.stage.failed", new Object[0]);
            return;
        });
        this.updateThread.start();
    }
    
    public void cancel() {
        this.keepUpgradingChunks = false;
        try {
            this.updateThread.join();
        }
        catch (InterruptedException ex) {}
    }
    
    private void updateWorld() {
        final File file1 = this.worldSaveHandler.getWorldDir();
        this.totalChunkCount = 0;
        final ImmutableMap.Builder<DimensionType, ListIterator<ChunkPos>> builder2 = ImmutableMap.<DimensionType, ListIterator<ChunkPos>>builder();
        for (final DimensionType dimensionType4 : DimensionType.getAll()) {
            final List<ChunkPos> list5 = this.getChunkPositions(dimensionType4);
            builder2.put(dimensionType4, list5.listIterator());
            this.totalChunkCount += list5.size();
        }
        if (this.totalChunkCount == 0) {
            this.isDone = true;
            return;
        }
        final float float3 = (float)this.totalChunkCount;
        final ImmutableMap<DimensionType, ListIterator<ChunkPos>> immutableMap4 = builder2.build();
        final ImmutableMap.Builder<DimensionType, VersionedChunkStorage> builder3 = ImmutableMap.<DimensionType, VersionedChunkStorage>builder();
        for (final DimensionType dimensionType5 : DimensionType.getAll()) {
            final File file2 = dimensionType5.getFile(file1);
            builder3.put(dimensionType5, new VersionedChunkStorage(new File(file2, "region"), this.worldSaveHandler.getDataFixer()));
        }
        final ImmutableMap<DimensionType, VersionedChunkStorage> immutableMap5 = builder3.build();
        long long7 = SystemUtil.getMeasuringTimeMs();
        this.status = new TranslatableTextComponent("optimizeWorld.stage.upgrading", new Object[0]);
        while (this.keepUpgradingChunks) {
            boolean boolean9 = false;
            float float4 = 0.0f;
            for (final DimensionType dimensionType6 : DimensionType.getAll()) {
                final ListIterator<ChunkPos> listIterator13 = immutableMap4.get(dimensionType6);
                final VersionedChunkStorage versionedChunkStorage14 = immutableMap5.get(dimensionType6);
                if (listIterator13.hasNext()) {
                    final ChunkPos chunkPos15 = listIterator13.next();
                    boolean boolean10 = false;
                    try {
                        final CompoundTag compoundTag17 = versionedChunkStorage14.getTagAt(chunkPos15);
                        if (compoundTag17 != null) {
                            final int integer18 = VersionedChunkStorage.getDataVersion(compoundTag17);
                            final CompoundTag compoundTag18 = versionedChunkStorage14.updateChunkTag(dimensionType6, () -> this.persistentStateManager, compoundTag17);
                            boolean boolean11 = integer18 < SharedConstants.getGameVersion().getWorldVersion();
                            if (this.eraseCache) {
                                final CompoundTag compoundTag19 = compoundTag18.getCompound("Level");
                                boolean11 = (boolean11 || compoundTag19.containsKey("Heightmaps"));
                                compoundTag19.remove("Heightmaps");
                                boolean11 = (boolean11 || compoundTag19.containsKey("isLightOn"));
                                compoundTag19.remove("isLightOn");
                            }
                            if (boolean11) {
                                versionedChunkStorage14.setTagAt(chunkPos15, compoundTag18);
                                boolean10 = true;
                            }
                        }
                    }
                    catch (CrashException crashException17) {
                        final Throwable throwable18 = crashException17.getCause();
                        if (!(throwable18 instanceof IOException)) {
                            throw crashException17;
                        }
                        WorldUpdater.LOGGER.error("Error upgrading chunk {}", chunkPos15, throwable18);
                    }
                    catch (IOException iOException17) {
                        WorldUpdater.LOGGER.error("Error upgrading chunk {}", chunkPos15, iOException17);
                    }
                    if (boolean10) {
                        ++this.upgradedChunkCount;
                    }
                    else {
                        ++this.skippedChunkCount;
                    }
                    boolean9 = true;
                }
                final float float5 = listIterator13.nextIndex() / float3;
                this.dimensionProgress.put(dimensionType6, float5);
                float4 += float5;
            }
            this.progress = float4;
            if (!boolean9) {
                this.keepUpgradingChunks = false;
            }
        }
        this.status = new TranslatableTextComponent("optimizeWorld.stage.finished", new Object[0]);
        for (final VersionedChunkStorage versionedChunkStorage15 : immutableMap5.values()) {
            try {
                versionedChunkStorage15.close();
            }
            catch (IOException iOException18) {
                WorldUpdater.LOGGER.error("Error upgrading chunk", (Throwable)iOException18);
            }
        }
        this.persistentStateManager.save();
        long7 = SystemUtil.getMeasuringTimeMs() - long7;
        WorldUpdater.LOGGER.info("World optimizaton finished after {} ms", long7);
        this.isDone = true;
    }
    
    private List<ChunkPos> getChunkPositions(final DimensionType dimensionType) {
        final File file2 = dimensionType.getFile(this.worldDirectory);
        final File file3 = new File(file2, "region");
        final File[] arr4 = file3.listFiles((file, string) -> string.endsWith(".mca"));
        if (arr4 == null) {
            return ImmutableList.of();
        }
        final List<ChunkPos> list5 = Lists.newArrayList();
        for (final File file4 : arr4) {
            final Matcher matcher10 = WorldUpdater.REGION_FILE_PATTERN.matcher(file4.getName());
            if (matcher10.matches()) {
                final int integer11 = Integer.parseInt(matcher10.group(1)) << 5;
                final int integer12 = Integer.parseInt(matcher10.group(2)) << 5;
                try (final RegionFile regionFile13 = new RegionFile(file4)) {
                    for (int integer13 = 0; integer13 < 32; ++integer13) {
                        for (int integer14 = 0; integer14 < 32; ++integer14) {
                            final ChunkPos chunkPos17 = new ChunkPos(integer13 + integer11, integer14 + integer12);
                            if (regionFile13.isChunkPresent(chunkPos17)) {
                                list5.add(chunkPos17);
                            }
                        }
                    }
                }
                catch (Throwable t4) {}
            }
        }
        return list5;
    }
    
    public boolean isDone() {
        return this.isDone;
    }
    
    @Environment(EnvType.CLIENT)
    public float getProgress(final DimensionType dimensionType) {
        return this.dimensionProgress.getFloat(dimensionType);
    }
    
    @Environment(EnvType.CLIENT)
    public float getProgress() {
        return this.progress;
    }
    
    public int getTotalChunkCount() {
        return this.totalChunkCount;
    }
    
    public int getUpgradedChunkCount() {
        return this.upgradedChunkCount;
    }
    
    public int getSkippedChunkCount() {
        return this.skippedChunkCount;
    }
    
    public TextComponent getStatus() {
        return this.status;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        UPDATE_THREAD_FACTORY = new ThreadFactoryBuilder().setDaemon(true).build();
        REGION_FILE_PATTERN = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
    }
}
