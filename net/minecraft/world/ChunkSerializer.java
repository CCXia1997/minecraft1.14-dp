package net.minecraft.world;

import java.util.AbstractList;
import org.apache.logging.log4j.LogManager;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import net.minecraft.nbt.ShortTag;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.structure.StructureFeatures;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.structure.StructureStart;
import net.minecraft.entity.EntityType;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.LongArrayTag;
import java.util.Map;
import net.minecraft.server.world.SimpleTickScheduler;
import net.minecraft.server.world.ServerTickScheduler;
import java.util.Collection;
import net.minecraft.entity.Entity;
import java.util.Arrays;
import net.minecraft.nbt.Tag;
import net.minecraft.SharedConstants;
import java.util.Iterator;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import java.util.BitSet;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.chunk.ReadOnlyChunk;
import java.util.Set;
import java.util.EnumSet;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.block.Block;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import java.util.Objects;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.structure.StructureManager;
import org.apache.logging.log4j.Logger;

public class ChunkSerializer
{
    private static final Logger LOGGER;
    
    public static ProtoChunk deserialize(final World world, final StructureManager structureManager, final PointOfInterestStorage pointOfInterestStorage, final ChunkPos chunkPos, final CompoundTag compoundTag) {
        final ChunkGenerator<?> chunkGenerator6 = world.getChunkManager().getChunkGenerator();
        final BiomeSource biomeSource7 = chunkGenerator6.getBiomeSource();
        final CompoundTag compoundTag2 = compoundTag.getCompound("Level");
        final ChunkPos chunkPos2 = new ChunkPos(compoundTag2.getInt("xPos"), compoundTag2.getInt("zPos"));
        if (!Objects.equals(chunkPos, chunkPos2)) {
            ChunkSerializer.LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", chunkPos, chunkPos, chunkPos2);
        }
        final Biome[] arr10 = new Biome[256];
        final BlockPos.Mutable mutable11 = new BlockPos.Mutable();
        if (compoundTag2.containsKey("Biomes", 11)) {
            final int[] arr11 = compoundTag2.getIntArray("Biomes");
            for (int integer13 = 0; integer13 < arr11.length; ++integer13) {
                arr10[integer13] = Registry.BIOME.get(arr11[integer13]);
                if (arr10[integer13] == null) {
                    arr10[integer13] = biomeSource7.getBiome(mutable11.set((integer13 & 0xF) + chunkPos.getStartX(), 0, (integer13 >> 4 & 0xF) + chunkPos.getStartZ()));
                }
            }
        }
        else {
            for (int integer14 = 0; integer14 < arr10.length; ++integer14) {
                arr10[integer14] = biomeSource7.getBiome(mutable11.set((integer14 & 0xF) + chunkPos.getStartX(), 0, (integer14 >> 4 & 0xF) + chunkPos.getStartZ()));
            }
        }
        final UpgradeData upgradeData12 = compoundTag2.containsKey("UpgradeData", 10) ? new UpgradeData(compoundTag2.getCompound("UpgradeData")) : UpgradeData.NO_UPGRADE_DATA;
        final ChunkTickScheduler<Block> chunkTickScheduler13 = new ChunkTickScheduler<Block>(block -> block == null || block.getDefaultState().isAir(), Registry.BLOCK::getId, Registry.BLOCK::get, chunkPos, compoundTag2.getList("ToBeTicked", 9));
        final ChunkTickScheduler<Fluid> chunkTickScheduler14 = new ChunkTickScheduler<Fluid>(fluid -> fluid == null || fluid == Fluids.EMPTY, Registry.FLUID::getId, Registry.FLUID::get, chunkPos, compoundTag2.getList("LiquidsToBeTicked", 9));
        final boolean boolean15 = compoundTag2.getBoolean("isLightOn");
        final ListTag listTag16 = compoundTag2.getList("Sections", 10);
        final int integer15 = 16;
        final ChunkSection[] arr12 = new ChunkSection[16];
        final boolean boolean16 = world.getDimension().hasSkyLight();
        final ChunkManager chunkManager20 = world.getChunkManager();
        final LightingProvider lightingProvider21 = chunkManager20.getLightingProvider();
        for (int integer16 = 0; integer16 < listTag16.size(); ++integer16) {
            final CompoundTag compoundTag3 = listTag16.getCompoundTag(integer16);
            final int integer17 = compoundTag3.getByte("Y");
            if (compoundTag3.containsKey("Palette", 9) && compoundTag3.containsKey("BlockStates", 12)) {
                final ChunkSection chunkSection25 = new ChunkSection(integer17 << 4);
                chunkSection25.getContainer().read(compoundTag3.getList("Palette", 10), compoundTag3.getLongArray("BlockStates"));
                chunkSection25.calculateCounts();
                if (!chunkSection25.isEmpty()) {
                    arr12[integer17] = chunkSection25;
                }
                pointOfInterestStorage.initForPalette(chunkPos, chunkSection25);
            }
            if (boolean15) {
                if (compoundTag3.containsKey("BlockLight", 7)) {
                    lightingProvider21.queueData(LightType.BLOCK, ChunkSectionPos.from(chunkPos, integer17), new ChunkNibbleArray(compoundTag3.getByteArray("BlockLight")));
                }
                if (boolean16 && compoundTag3.containsKey("SkyLight", 7)) {
                    lightingProvider21.queueData(LightType.SKY, ChunkSectionPos.from(chunkPos, integer17), new ChunkNibbleArray(compoundTag3.getByteArray("SkyLight")));
                }
            }
        }
        final long long22 = compoundTag2.getLong("InhabitedTime");
        final ChunkStatus.ChunkType chunkType24 = getChunkType(compoundTag);
        Chunk chunk25;
        if (chunkType24 == ChunkStatus.ChunkType.LEVELCHUNK) {
            chunk25 = new WorldChunk(world.getWorld(), chunkPos, arr10, upgradeData12, chunkTickScheduler13, chunkTickScheduler14, long22, arr12, worldChunk -> writeEntities(compoundTag2, worldChunk));
        }
        else {
            final ProtoChunk protoChunk26 = (ProtoChunk)(chunk25 = new ProtoChunk(chunkPos, upgradeData12, arr12, chunkTickScheduler13, chunkTickScheduler14));
            chunk25.setBiomeArray(arr10);
            chunk25.setInhabitedTime(long22);
            protoChunk26.setStatus(ChunkStatus.get(compoundTag2.getString("Status")));
            if (chunk25.getStatus().isAtLeast(ChunkStatus.FEATURES)) {
                protoChunk26.setLightingProvider(lightingProvider21);
            }
            if (!boolean15 && chunk25.getStatus().isAtLeast(ChunkStatus.LIGHT)) {
                for (final BlockPos blockPos28 : BlockPos.iterate(chunkPos.getStartX(), 0, chunkPos.getStartZ(), chunkPos.getEndX(), 255, chunkPos.getEndZ())) {
                    if (chunk25.getBlockState(blockPos28).getLuminance() != 0) {
                        protoChunk26.addLightSource(blockPos28);
                    }
                }
            }
        }
        chunk25.setLightOn(boolean15);
        final CompoundTag compoundTag4 = compoundTag2.getCompound("Heightmaps");
        final EnumSet<Heightmap.Type> enumSet27 = EnumSet.<Heightmap.Type>noneOf(Heightmap.Type.class);
        for (final Heightmap.Type type29 : chunk25.getStatus().isSurfaceGenerated()) {
            final String string30 = type29.getName();
            if (compoundTag4.containsKey(string30, 12)) {
                chunk25.setHeightmap(type29, compoundTag4.getLongArray(string30));
            }
            else {
                enumSet27.add(type29);
            }
        }
        Heightmap.populateHeightmaps(chunk25, enumSet27);
        final CompoundTag compoundTag5 = compoundTag2.getCompound("Structures");
        chunk25.setStructureStarts(readStructureStarts(chunkGenerator6, structureManager, biomeSource7, compoundTag5));
        chunk25.setStructureReferences(readStructureReferences(compoundTag5));
        if (compoundTag2.getBoolean("shouldSave")) {
            chunk25.setShouldSave(true);
        }
        final ListTag listTag17 = compoundTag2.getList("PostProcessing", 9);
        for (int integer18 = 0; integer18 < listTag17.size(); ++integer18) {
            final ListTag listTag18 = listTag17.getListTag(integer18);
            for (int integer19 = 0; integer19 < listTag18.size(); ++integer19) {
                chunk25.markBlockForPostProcessing(listTag18.getShort(integer19), integer18);
            }
        }
        if (chunkType24 == ChunkStatus.ChunkType.LEVELCHUNK) {
            return new ReadOnlyChunk((WorldChunk)chunk25);
        }
        final ProtoChunk protoChunk27 = (ProtoChunk)chunk25;
        final ListTag listTag18 = compoundTag2.getList("Entities", 10);
        for (int integer19 = 0; integer19 < listTag18.size(); ++integer19) {
            protoChunk27.addEntity(listTag18.getCompoundTag(integer19));
        }
        final ListTag listTag19 = compoundTag2.getList("TileEntities", 10);
        for (int integer20 = 0; integer20 < listTag19.size(); ++integer20) {
            final CompoundTag compoundTag6 = listTag19.getCompoundTag(integer20);
            chunk25.addPendingBlockEntityTag(compoundTag6);
        }
        final ListTag listTag20 = compoundTag2.getList("Lights", 9);
        for (int integer21 = 0; integer21 < listTag20.size(); ++integer21) {
            final ListTag listTag21 = listTag20.getListTag(integer21);
            for (int integer22 = 0; integer22 < listTag21.size(); ++integer22) {
                protoChunk27.addLightSource(listTag21.getShort(integer22), integer21);
            }
        }
        final CompoundTag compoundTag6 = compoundTag2.getCompound("CarvingMasks");
        for (final String string31 : compoundTag6.getKeys()) {
            final GenerationStep.Carver carver37 = GenerationStep.Carver.valueOf(string31);
            protoChunk27.setCarvingMask(carver37, BitSet.valueOf(compoundTag6.getByteArray(string31)));
        }
        return protoChunk27;
    }
    
    public static CompoundTag serialize(final World world, final Chunk chunk) {
        final ChunkPos chunkPos3 = chunk.getPos();
        final CompoundTag compoundTag4 = new CompoundTag();
        final CompoundTag compoundTag5 = new CompoundTag();
        compoundTag4.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        compoundTag4.put("Level", compoundTag5);
        compoundTag5.putInt("xPos", chunkPos3.x);
        compoundTag5.putInt("zPos", chunkPos3.z);
        compoundTag5.putLong("LastUpdate", world.getTime());
        compoundTag5.putLong("InhabitedTime", chunk.getInhabitedTime());
        compoundTag5.putString("Status", chunk.getStatus().getName());
        final UpgradeData upgradeData6 = chunk.getUpgradeData();
        if (!upgradeData6.a()) {
            compoundTag5.put("UpgradeData", upgradeData6.toTag());
        }
        final ChunkSection[] arr7 = chunk.getSectionArray();
        final ListTag listTag8 = new ListTag();
        final LightingProvider lightingProvider9 = world.getChunkManager().getLightingProvider();
        for (int integer10 = -1; integer10 < 17; ++integer10) {
            final int integer11 = integer10;
            final int n;
            final ChunkSection chunkSection2 = Arrays.<ChunkSection>stream(arr7).filter(chunkSection -> chunkSection != null && chunkSection.getYOffset() >> 4 == n).findFirst().orElse(WorldChunk.EMPTY_SECTION);
            final ChunkNibbleArray chunkNibbleArray13 = lightingProvider9.get(LightType.BLOCK).getChunkLightArray(ChunkSectionPos.from(chunkPos3, integer11));
            final ChunkNibbleArray chunkNibbleArray14 = lightingProvider9.get(LightType.SKY).getChunkLightArray(ChunkSectionPos.from(chunkPos3, integer11));
            if (chunkSection2 != WorldChunk.EMPTY_SECTION || chunkNibbleArray13 != null || chunkNibbleArray14 != null) {
                final CompoundTag compoundTag6 = new CompoundTag();
                compoundTag6.putByte("Y", (byte)(integer11 & 0xFF));
                if (chunkSection2 != WorldChunk.EMPTY_SECTION) {
                    chunkSection2.getContainer().write(compoundTag6, "Palette", "BlockStates");
                }
                if (chunkNibbleArray13 != null && !chunkNibbleArray13.isUninitialized()) {
                    compoundTag6.putByteArray("BlockLight", chunkNibbleArray13.asByteArray());
                }
                if (chunkNibbleArray14 != null && !chunkNibbleArray14.isUninitialized()) {
                    compoundTag6.putByteArray("SkyLight", chunkNibbleArray14.asByteArray());
                }
                ((AbstractList<CompoundTag>)listTag8).add(compoundTag6);
            }
        }
        compoundTag5.put("Sections", listTag8);
        if (chunk.isLightOn()) {
            compoundTag5.putBoolean("isLightOn", true);
        }
        final Biome[] arr8 = chunk.getBiomeArray();
        final int[] arr9 = (arr8 != null) ? new int[arr8.length] : new int[0];
        if (arr8 != null) {
            for (int integer12 = 0; integer12 < arr8.length; ++integer12) {
                arr9[integer12] = Registry.BIOME.getRawId(arr8[integer12]);
            }
        }
        compoundTag5.putIntArray("Biomes", arr9);
        final ListTag listTag9 = new ListTag();
        for (final BlockPos blockPos14 : chunk.getBlockEntityPositions()) {
            final BlockEntity blockEntity15 = chunk.getBlockEntity(blockPos14);
            if (blockEntity15 != null) {
                final CompoundTag compoundTag7 = new CompoundTag();
                blockEntity15.toTag(compoundTag7);
                if (chunk.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
                    compoundTag7.putBoolean("keepPacked", false);
                }
                ((AbstractList<CompoundTag>)listTag9).add(compoundTag7);
            }
            else {
                final CompoundTag compoundTag7 = chunk.getBlockEntityTagAt(blockPos14);
                if (compoundTag7 == null) {
                    continue;
                }
                if (chunk.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
                    compoundTag7.putBoolean("keepPacked", true);
                }
                ((AbstractList<CompoundTag>)listTag9).add(compoundTag7);
            }
        }
        compoundTag5.put("TileEntities", listTag9);
        final ListTag listTag10 = new ListTag();
        if (chunk.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
            final WorldChunk worldChunk14 = (WorldChunk)chunk;
            worldChunk14.d(false);
            for (int integer13 = 0; integer13 < worldChunk14.getEntitySectionArray().length; ++integer13) {
                for (final Entity entity17 : worldChunk14.getEntitySectionArray()[integer13]) {
                    final CompoundTag compoundTag8 = new CompoundTag();
                    if (entity17.saveToTag(compoundTag8)) {
                        worldChunk14.d(true);
                        ((AbstractList<CompoundTag>)listTag10).add(compoundTag8);
                    }
                }
            }
        }
        else {
            final ProtoChunk protoChunk14 = (ProtoChunk)chunk;
            listTag10.addAll(protoChunk14.getEntities());
            for (final BlockPos blockPos15 : chunk.getBlockEntityPositions()) {
                final BlockEntity blockEntity16 = chunk.getBlockEntity(blockPos15);
                if (blockEntity16 != null) {
                    final CompoundTag compoundTag8 = new CompoundTag();
                    blockEntity16.toTag(compoundTag8);
                    ((AbstractList<CompoundTag>)listTag9).add(compoundTag8);
                }
                else {
                    ((AbstractList<CompoundTag>)listTag9).add(chunk.getBlockEntityTagAt(blockPos15));
                }
            }
            compoundTag5.put("Lights", toNbt(protoChunk14.getLightSourcesBySection()));
            final CompoundTag compoundTag6 = new CompoundTag();
            for (final GenerationStep.Carver carver19 : GenerationStep.Carver.values()) {
                compoundTag6.putByteArray(carver19.toString(), chunk.getCarvingMask(carver19).toByteArray());
            }
            compoundTag5.put("CarvingMasks", compoundTag6);
        }
        compoundTag5.put("Entities", listTag10);
        if (world.getBlockTickScheduler() instanceof ServerTickScheduler) {
            compoundTag5.put("TileTicks", ((ServerTickScheduler)world.getBlockTickScheduler()).toTag(chunkPos3));
        }
        if (chunk.getBlockTickScheduler() instanceof ChunkTickScheduler) {
            compoundTag5.put("ToBeTicked", ((ChunkTickScheduler)chunk.getBlockTickScheduler()).toNbt());
        }
        if (chunk.getBlockTickScheduler() instanceof SimpleTickScheduler) {
            compoundTag5.put("TileTicks", ((SimpleTickScheduler)chunk.getBlockTickScheduler()).toTag(world.getTime()));
        }
        if (world.getFluidTickScheduler() instanceof ServerTickScheduler) {
            compoundTag5.put("LiquidTicks", ((ServerTickScheduler)world.getFluidTickScheduler()).toTag(chunkPos3));
        }
        if (chunk.getFluidTickScheduler() instanceof ChunkTickScheduler) {
            compoundTag5.put("LiquidsToBeTicked", ((ChunkTickScheduler)chunk.getFluidTickScheduler()).toNbt());
        }
        if (chunk.getFluidTickScheduler() instanceof SimpleTickScheduler) {
            compoundTag5.put("LiquidTicks", ((SimpleTickScheduler)chunk.getFluidTickScheduler()).toTag(world.getTime()));
        }
        compoundTag5.put("PostProcessing", toNbt(chunk.getPostProcessingLists()));
        final CompoundTag compoundTag9 = new CompoundTag();
        for (final Map.Entry<Heightmap.Type, Heightmap> entry16 : chunk.getHeightmaps()) {
            if (chunk.getStatus().isSurfaceGenerated().contains(entry16.getKey())) {
                compoundTag9.put(entry16.getKey().getName(), new LongArrayTag(entry16.getValue().asLongArray()));
            }
        }
        compoundTag5.put("Heightmaps", compoundTag9);
        compoundTag5.put("Structures", writeStructures(chunkPos3, chunk.getStructureStarts(), chunk.getStructureReferences()));
        return compoundTag4;
    }
    
    public static ChunkStatus.ChunkType getChunkType(@Nullable final CompoundTag tag) {
        if (tag != null) {
            final ChunkStatus chunkStatus2 = ChunkStatus.get(tag.getCompound("Level").getString("Status"));
            if (chunkStatus2 != null) {
                return chunkStatus2.getChunkType();
            }
        }
        return ChunkStatus.ChunkType.PROTOCHUNK;
    }
    
    private static void writeEntities(final CompoundTag tag, final WorldChunk chunk) {
        final ListTag listTag3 = tag.getList("Entities", 10);
        final World world4 = chunk.getWorld();
        for (int integer5 = 0; integer5 < listTag3.size(); ++integer5) {
            final CompoundTag compoundTag6 = listTag3.getCompoundTag(integer5);
            EntityType.loadEntityWithPassengers(compoundTag6, world4, entity -> {
                chunk.addEntity(entity);
                return entity;
            });
            chunk.d(true);
        }
        final ListTag listTag4 = tag.getList("TileEntities", 10);
        for (int integer6 = 0; integer6 < listTag4.size(); ++integer6) {
            final CompoundTag compoundTag7 = listTag4.getCompoundTag(integer6);
            final boolean boolean8 = compoundTag7.getBoolean("keepPacked");
            if (boolean8) {
                chunk.addPendingBlockEntityTag(compoundTag7);
            }
            else {
                final BlockEntity blockEntity9 = BlockEntity.createFromTag(compoundTag7);
                if (blockEntity9 != null) {
                    chunk.addBlockEntity(blockEntity9);
                }
            }
        }
        if (tag.containsKey("TileTicks", 9) && world4.getBlockTickScheduler() instanceof ServerTickScheduler) {
            ((ServerTickScheduler)world4.getBlockTickScheduler()).fromTag(tag.getList("TileTicks", 10));
        }
        if (tag.containsKey("LiquidTicks", 9) && world4.getFluidTickScheduler() instanceof ServerTickScheduler) {
            ((ServerTickScheduler)world4.getFluidTickScheduler()).fromTag(tag.getList("LiquidTicks", 10));
        }
    }
    
    private static CompoundTag writeStructures(final ChunkPos pos, final Map<String, StructureStart> structureStarts, final Map<String, LongSet> structureReferences) {
        final CompoundTag compoundTag4 = new CompoundTag();
        final CompoundTag compoundTag5 = new CompoundTag();
        for (final Map.Entry<String, StructureStart> entry7 : structureStarts.entrySet()) {
            compoundTag5.put(entry7.getKey(), entry7.getValue().toTag(pos.x, pos.z));
        }
        compoundTag4.put("Starts", compoundTag5);
        final CompoundTag compoundTag6 = new CompoundTag();
        for (final Map.Entry<String, LongSet> entry8 : structureReferences.entrySet()) {
            compoundTag6.put(entry8.getKey(), new LongArrayTag(entry8.getValue()));
        }
        compoundTag4.put("References", compoundTag6);
        return compoundTag4;
    }
    
    private static Map<String, StructureStart> readStructureStarts(final ChunkGenerator<?> chunkGenerator, final StructureManager structureManager, final BiomeSource biomeSource, final CompoundTag tag) {
        final Map<String, StructureStart> map5 = Maps.newHashMap();
        final CompoundTag compoundTag6 = tag.getCompound("Starts");
        for (final String string8 : compoundTag6.getKeys()) {
            map5.put(string8, StructureFeatures.readStructureStart(chunkGenerator, structureManager, biomeSource, compoundTag6.getCompound(string8)));
        }
        return map5;
    }
    
    private static Map<String, LongSet> readStructureReferences(final CompoundTag tag) {
        final Map<String, LongSet> map2 = Maps.newHashMap();
        final CompoundTag compoundTag3 = tag.getCompound("References");
        for (final String string5 : compoundTag3.getKeys()) {
            map2.put(string5, (LongSet)new LongOpenHashSet(compoundTag3.getLongArray(string5)));
        }
        return map2;
    }
    
    public static ListTag toNbt(final ShortList[] lists) {
        final ListTag listTag2 = new ListTag();
        for (final ShortList shortList6 : lists) {
            final ListTag listTag3 = new ListTag();
            if (shortList6 != null) {
                for (final Short short9 : shortList6) {
                    ((AbstractList<ShortTag>)listTag3).add(new ShortTag(short9));
                }
            }
            ((AbstractList<ListTag>)listTag2).add(listTag3);
        }
        return listTag2;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
