package net.minecraft.world.level.storage;

import org.apache.logging.log4j.LogManager;
import java.util.Collections;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.DataOutput;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.storage.RegionFile;
import java.util.Iterator;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.level.LevelProperties;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSourceConfig;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.FixedBiomeSourceConfig;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.biome.source.BiomeSourceType;
import java.util.Collection;
import net.minecraft.world.dimension.DimensionType;
import java.io.File;
import com.google.common.collect.Lists;
import net.minecraft.util.ProgressListener;
import com.mojang.datafixers.DataFixer;
import java.nio.file.Path;
import org.apache.logging.log4j.Logger;

public class AnvilLevelStorage
{
    private static final Logger LOGGER;
    
    static boolean convertLevel(final Path path, final DataFixer dataFixer, final String string, final ProgressListener progressListener) {
        progressListener.progressStagePercentage(0);
        final List<File> list5 = Lists.newArrayList();
        final List<File> list6 = Lists.newArrayList();
        final List<File> list7 = Lists.newArrayList();
        final File file8 = new File(path.toFile(), string);
        final File file9 = DimensionType.b.getFile(file8);
        final File file10 = DimensionType.c.getFile(file8);
        AnvilLevelStorage.LOGGER.info("Scanning folders...");
        addRegionFiles(file8, list5);
        if (file9.exists()) {
            addRegionFiles(file9, list6);
        }
        if (file10.exists()) {
            addRegionFiles(file10, list7);
        }
        final int integer11 = list5.size() + list6.size() + list7.size();
        AnvilLevelStorage.LOGGER.info("Total conversion count is {}", integer11);
        final LevelProperties levelProperties12 = LevelStorage.getLevelProperties(path, dataFixer, string);
        final BiomeSourceType<FixedBiomeSourceConfig, FixedBiomeSource> biomeSourceType14 = BiomeSourceType.FIXED;
        final BiomeSourceType<VanillaLayeredBiomeSourceConfig, VanillaLayeredBiomeSource> biomeSourceType15 = BiomeSourceType.VANILLA_LAYERED;
        BiomeSource biomeSource13;
        if (levelProperties12 != null && levelProperties12.getGeneratorType() == LevelGeneratorType.FLAT) {
            biomeSource13 = biomeSourceType14.applyConfig(biomeSourceType14.getConfig().setBiome(Biomes.c));
        }
        else {
            biomeSource13 = biomeSourceType15.applyConfig(biomeSourceType15.getConfig().setLevelProperties(levelProperties12).setGeneratorSettings(ChunkGeneratorType.a.createSettings()));
        }
        convertRegions(new File(file8, "region"), list5, biomeSource13, 0, integer11, progressListener);
        convertRegions(new File(file9, "region"), list6, biomeSourceType14.applyConfig(biomeSourceType14.getConfig().setBiome(Biomes.j)), list5.size(), integer11, progressListener);
        convertRegions(new File(file10, "region"), list7, biomeSourceType14.applyConfig(biomeSourceType14.getConfig().setBiome(Biomes.k)), list5.size() + list6.size(), integer11, progressListener);
        levelProperties12.setVersion(19133);
        if (levelProperties12.getGeneratorType() == LevelGeneratorType.DEFAULT_1_1) {
            levelProperties12.setGeneratorType(LevelGeneratorType.DEFAULT);
        }
        makeMcrLevelDatBackup(path, string);
        final WorldSaveHandler worldSaveHandler16 = LevelStorage.createSaveHandler(path, dataFixer, string, null);
        worldSaveHandler16.saveWorld(levelProperties12);
        return true;
    }
    
    private static void makeMcrLevelDatBackup(final Path path, final String string) {
        final File file3 = new File(path.toFile(), string);
        if (!file3.exists()) {
            AnvilLevelStorage.LOGGER.warn("Unable to create level.dat_mcr backup");
            return;
        }
        final File file4 = new File(file3, "level.dat");
        if (!file4.exists()) {
            AnvilLevelStorage.LOGGER.warn("Unable to create level.dat_mcr backup");
            return;
        }
        final File file5 = new File(file3, "level.dat_mcr");
        if (!file4.renameTo(file5)) {
            AnvilLevelStorage.LOGGER.warn("Unable to create level.dat_mcr backup");
        }
    }
    
    private static void convertRegions(final File file, final Iterable<File> iterable, final BiomeSource biomeSource, int integer4, final int currentCount, final ProgressListener progressListener) {
        for (final File file2 : iterable) {
            convertRegion(file, file2, biomeSource, integer4, currentCount, progressListener);
            ++integer4;
            final int integer5 = (int)Math.round(100.0 * integer4 / currentCount);
            progressListener.progressStagePercentage(integer5);
        }
    }
    
    private static void convertRegion(final File file1, final File baseFolder, final BiomeSource biomeSource, final int integer4, final int progressStart, final ProgressListener progressListener) {
        try {
            final String string7 = baseFolder.getName();
            try (final RegionFile regionFile8 = new RegionFile(baseFolder);
                 final RegionFile regionFile9 = new RegionFile(new File(file1, string7.substring(0, string7.length() - ".mcr".length()) + ".mca"))) {
                for (int integer5 = 0; integer5 < 32; ++integer5) {
                    for (int integer6 = 0; integer6 < 32; ++integer6) {
                        final ChunkPos chunkPos14 = new ChunkPos(integer5, integer6);
                        if (regionFile8.hasChunk(chunkPos14) && !regionFile9.hasChunk(chunkPos14)) {
                            CompoundTag compoundTag15;
                            try (final DataInputStream dataInputStream16 = regionFile8.getChunkDataInputStream(chunkPos14)) {
                                if (dataInputStream16 == null) {
                                    AnvilLevelStorage.LOGGER.warn("Failed to fetch input stream");
                                    continue;
                                }
                                compoundTag15 = NbtIo.read(dataInputStream16);
                            }
                            final CompoundTag compoundTag16 = compoundTag15.getCompound("Level");
                            final AlphaChunkIo.AlphaChunk alphaChunk17 = AlphaChunkIo.readAlphaChunk(compoundTag16);
                            final CompoundTag compoundTag17 = new CompoundTag();
                            final CompoundTag compoundTag18 = new CompoundTag();
                            compoundTag17.put("Level", compoundTag18);
                            AlphaChunkIo.convertAlphaChunk(alphaChunk17, compoundTag18, biomeSource);
                            try (final DataOutputStream dataOutputStream20 = regionFile9.getChunkDataOutputStream(chunkPos14)) {
                                NbtIo.write(compoundTag17, dataOutputStream20);
                            }
                        }
                    }
                    int integer6 = (int)Math.round(100.0 * (integer4 * 1024) / (progressStart * 1024));
                    final int integer7 = (int)Math.round(100.0 * ((integer5 + 1) * 32 + integer4 * 1024) / (progressStart * 1024));
                    if (integer7 > integer6) {
                        progressListener.progressStagePercentage(integer7);
                    }
                }
            }
        }
        catch (IOException iOException7) {
            iOException7.printStackTrace();
        }
    }
    
    private static void addRegionFiles(final File file, final Collection<File> collection) {
        final File file2 = new File(file, "region");
        final File[] arr4 = file2.listFiles((file, string) -> string.endsWith(".mcr"));
        if (arr4 != null) {
            Collections.<File>addAll(collection, arr4);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
