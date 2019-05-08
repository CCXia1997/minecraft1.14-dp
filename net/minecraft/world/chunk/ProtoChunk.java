package net.minecraft.world.chunk;

import org.apache.logging.log4j.LogManager;
import net.minecraft.world.TickScheduler;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.Collections;
import net.minecraft.entity.Entity;
import java.util.Collection;
import com.google.common.collect.Sets;
import java.util.Set;
import java.util.EnumSet;
import net.minecraft.world.BlockView;
import java.util.Iterator;
import java.util.stream.Stream;
import net.minecraft.fluid.FluidState;
import net.minecraft.block.Blocks;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.registry.Registry;
import java.util.BitSet;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.fluid.Fluid;
import net.minecraft.block.Block;
import net.minecraft.world.ChunkTickScheduler;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.structure.StructureStart;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.Logger;

public class ProtoChunk implements Chunk
{
    private static final Logger LOGGER;
    private final ChunkPos pos;
    private boolean shouldSave;
    private Biome[] biomeArray;
    @Nullable
    private volatile LightingProvider lightingProvider;
    private final Map<Heightmap.Type, Heightmap> heightmaps;
    private volatile ChunkStatus status;
    private final Map<BlockPos, BlockEntity> blockEntities;
    private final Map<BlockPos, CompoundTag> blockEntityTags;
    private final ChunkSection[] sections;
    private final List<CompoundTag> entities;
    private final List<BlockPos> lightSources;
    private final ShortList[] postProcessingLists;
    private final Map<String, StructureStart> structureStarts;
    private final Map<String, LongSet> structureReferences;
    private final UpgradeData upgradeData;
    private final ChunkTickScheduler<Block> blockTickScheduler;
    private final ChunkTickScheduler<Fluid> fluidTickScheduler;
    private long inhabitedTime;
    private final Map<GenerationStep.Carver, BitSet> carvingMasks;
    private volatile boolean isLightOn;
    
    public ProtoChunk(final ChunkPos chunkPos, final UpgradeData upgradeData) {
        this(chunkPos, upgradeData, null, new ChunkTickScheduler<Block>(block -> block == null || block.getDefaultState().isAir(), Registry.BLOCK::getId, Registry.BLOCK::get, chunkPos), new ChunkTickScheduler<Fluid>(fluid -> fluid == null || fluid == Fluids.EMPTY, Registry.FLUID::getId, Registry.FLUID::get, chunkPos));
    }
    
    public ProtoChunk(final ChunkPos chunkPos, final UpgradeData upgradeData, @Nullable final ChunkSection[] arr, final ChunkTickScheduler<Block> chunkTickScheduler4, final ChunkTickScheduler<Fluid> chunkTickScheduler5) {
        this.heightmaps = Maps.newEnumMap(Heightmap.Type.class);
        this.status = ChunkStatus.EMPTY;
        this.blockEntities = Maps.newHashMap();
        this.blockEntityTags = Maps.newHashMap();
        this.sections = new ChunkSection[16];
        this.entities = Lists.newArrayList();
        this.lightSources = Lists.newArrayList();
        this.postProcessingLists = new ShortList[16];
        this.structureStarts = Maps.newHashMap();
        this.structureReferences = Maps.newHashMap();
        this.carvingMasks = Maps.newHashMap();
        this.pos = chunkPos;
        this.upgradeData = upgradeData;
        this.blockTickScheduler = chunkTickScheduler4;
        this.fluidTickScheduler = chunkTickScheduler5;
        if (arr != null) {
            if (this.sections.length == arr.length) {
                System.arraycopy(arr, 0, this.sections, 0, this.sections.length);
            }
            else {
                ProtoChunk.LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", arr.length, this.sections.length);
            }
        }
    }
    
    @Override
    public BlockState getBlockState(final BlockPos pos) {
        final int integer2 = pos.getY();
        if (World.isHeightInvalid(integer2)) {
            return Blocks.kS.getDefaultState();
        }
        final ChunkSection chunkSection3 = this.getSectionArray()[integer2 >> 4];
        if (ChunkSection.isEmpty(chunkSection3)) {
            return Blocks.AIR.getDefaultState();
        }
        return chunkSection3.getBlockState(pos.getX() & 0xF, integer2 & 0xF, pos.getZ() & 0xF);
    }
    
    @Override
    public FluidState getFluidState(final BlockPos pos) {
        final int integer2 = pos.getY();
        if (World.isHeightInvalid(integer2)) {
            return Fluids.EMPTY.getDefaultState();
        }
        final ChunkSection chunkSection3 = this.getSectionArray()[integer2 >> 4];
        if (ChunkSection.isEmpty(chunkSection3)) {
            return Fluids.EMPTY.getDefaultState();
        }
        return chunkSection3.getFluidState(pos.getX() & 0xF, integer2 & 0xF, pos.getZ() & 0xF);
    }
    
    @Override
    public Stream<BlockPos> getLightSourcesStream() {
        return this.lightSources.stream();
    }
    
    public ShortList[] getLightSourcesBySection() {
        final ShortList[] arr1 = new ShortList[16];
        for (final BlockPos blockPos3 : this.lightSources) {
            Chunk.getList(arr1, blockPos3.getY() >> 4).add(getPackedSectionRelative(blockPos3));
        }
        return arr1;
    }
    
    public void addLightSource(final short chunkSliceRel, final int sectionY) {
        this.addLightSource(joinBlockPos(chunkSliceRel, sectionY, this.pos));
    }
    
    public void addLightSource(final BlockPos pos) {
        this.lightSources.add(pos.toImmutable());
    }
    
    @Nullable
    @Override
    public BlockState setBlockState(final BlockPos pos, final BlockState state, final boolean boolean3) {
        final int integer4 = pos.getX();
        final int integer5 = pos.getY();
        final int integer6 = pos.getZ();
        if (integer5 < 0 || integer5 >= 256) {
            return Blocks.kS.getDefaultState();
        }
        if (this.sections[integer5 >> 4] == WorldChunk.EMPTY_SECTION && state.getBlock() == Blocks.AIR) {
            return state;
        }
        if (state.getLuminance() > 0) {
            this.lightSources.add(new BlockPos((integer4 & 0xF) + this.getPos().getStartX(), integer5, (integer6 & 0xF) + this.getPos().getStartZ()));
        }
        final ChunkSection chunkSection7 = this.getSection(integer5 >> 4);
        final BlockState blockState8 = chunkSection7.setBlockState(integer4 & 0xF, integer5 & 0xF, integer6 & 0xF, state);
        if (this.status.isAtLeast(ChunkStatus.FEATURES) && state != blockState8 && (state.getLightSubtracted(this, pos) != blockState8.getLightSubtracted(this, pos) || state.getLuminance() != blockState8.getLuminance() || state.g() || blockState8.g())) {
            final LightingProvider lightingProvider9 = this.getLightingProvider();
            lightingProvider9.enqueueLightUpdate(pos);
        }
        final EnumSet<Heightmap.Type> enumSet9 = this.getStatus().isSurfaceGenerated();
        EnumSet<Heightmap.Type> enumSet10 = null;
        for (final Heightmap.Type type12 : enumSet9) {
            final Heightmap heightmap13 = this.heightmaps.get(type12);
            if (heightmap13 == null) {
                if (enumSet10 == null) {
                    enumSet10 = EnumSet.<Heightmap.Type>noneOf(Heightmap.Type.class);
                }
                enumSet10.add(type12);
            }
        }
        if (enumSet10 != null) {
            Heightmap.populateHeightmaps(this, enumSet10);
        }
        for (final Heightmap.Type type12 : enumSet9) {
            this.heightmaps.get(type12).trackUpdate(integer4 & 0xF, integer5, integer6 & 0xF, state);
        }
        return blockState8;
    }
    
    public ChunkSection getSection(final int y) {
        if (this.sections[y] == WorldChunk.EMPTY_SECTION) {
            this.sections[y] = new ChunkSection(y << 4);
        }
        return this.sections[y];
    }
    
    @Override
    public void setBlockEntity(final BlockPos pos, final BlockEntity blockEntity) {
        blockEntity.setPos(pos);
        this.blockEntities.put(pos, blockEntity);
    }
    
    @Override
    public Set<BlockPos> getBlockEntityPositions() {
        final Set<BlockPos> set1 = Sets.newHashSet(this.blockEntityTags.keySet());
        set1.addAll(this.blockEntities.keySet());
        return set1;
    }
    
    @Nullable
    @Override
    public BlockEntity getBlockEntity(final BlockPos pos) {
        return this.blockEntities.get(pos);
    }
    
    public Map<BlockPos, BlockEntity> getBlockEntities() {
        return this.blockEntities;
    }
    
    public void addEntity(final CompoundTag entityTag) {
        this.entities.add(entityTag);
    }
    
    @Override
    public void addEntity(final Entity entity) {
        final CompoundTag compoundTag2 = new CompoundTag();
        entity.saveToTag(compoundTag2);
        this.addEntity(compoundTag2);
    }
    
    public List<CompoundTag> getEntities() {
        return this.entities;
    }
    
    @Override
    public void setBiomeArray(final Biome[] biomeArray) {
        this.biomeArray = biomeArray;
    }
    
    @Override
    public Biome[] getBiomeArray() {
        return this.biomeArray;
    }
    
    @Override
    public void setShouldSave(final boolean shouldSave) {
        this.shouldSave = shouldSave;
    }
    
    @Override
    public boolean needsSaving() {
        return this.shouldSave;
    }
    
    @Override
    public ChunkStatus getStatus() {
        return this.status;
    }
    
    public void setStatus(final ChunkStatus chunkStatus) {
        this.status = chunkStatus;
        this.setShouldSave(true);
    }
    
    @Override
    public ChunkSection[] getSectionArray() {
        return this.sections;
    }
    
    @Nullable
    @Override
    public LightingProvider getLightingProvider() {
        return this.lightingProvider;
    }
    
    @Override
    public Collection<Map.Entry<Heightmap.Type, Heightmap>> getHeightmaps() {
        return Collections.unmodifiableSet(this.heightmaps.entrySet());
    }
    
    @Override
    public void setHeightmap(final Heightmap.Type type, final long[] heightmap) {
        this.getHeightmap(type).setTo(heightmap);
    }
    
    @Override
    public Heightmap getHeightmap(final Heightmap.Type type) {
        return this.heightmaps.computeIfAbsent(type, type -> new Heightmap(this, type));
    }
    
    @Override
    public int sampleHeightmap(final Heightmap.Type type, final int x, final int z) {
        Heightmap heightmap4 = this.heightmaps.get(type);
        if (heightmap4 == null) {
            Heightmap.populateHeightmaps(this, EnumSet.<Heightmap.Type>of(type));
            heightmap4 = this.heightmaps.get(type);
        }
        return heightmap4.get(x & 0xF, z & 0xF) - 1;
    }
    
    @Override
    public ChunkPos getPos() {
        return this.pos;
    }
    
    @Override
    public void setLastSaveTime(final long lastSaveTime) {
    }
    
    @Nullable
    @Override
    public StructureStart getStructureStart(final String structureId) {
        return this.structureStarts.get(structureId);
    }
    
    @Override
    public void setStructureStart(final String structureId, final StructureStart structureStart) {
        this.structureStarts.put(structureId, structureStart);
        this.shouldSave = true;
    }
    
    @Override
    public Map<String, StructureStart> getStructureStarts() {
        return Collections.<String, StructureStart>unmodifiableMap(this.structureStarts);
    }
    
    @Override
    public void setStructureStarts(final Map<String, StructureStart> map) {
        this.structureStarts.clear();
        this.structureStarts.putAll(map);
        this.shouldSave = true;
    }
    
    @Override
    public LongSet getStructureReferences(final String structureId) {
        return this.structureReferences.computeIfAbsent(structureId, string -> new LongOpenHashSet());
    }
    
    @Override
    public void addStructureReference(final String structureId, final long reference) {
        this.structureReferences.computeIfAbsent(structureId, string -> new LongOpenHashSet()).add(reference);
        this.shouldSave = true;
    }
    
    @Override
    public Map<String, LongSet> getStructureReferences() {
        return Collections.<String, LongSet>unmodifiableMap(this.structureReferences);
    }
    
    @Override
    public void setStructureReferences(final Map<String, LongSet> structureReferences) {
        this.structureReferences.clear();
        this.structureReferences.putAll(structureReferences);
        this.shouldSave = true;
    }
    
    public static short getPackedSectionRelative(final BlockPos pos) {
        final int integer2 = pos.getX();
        final int integer3 = pos.getY();
        final int integer4 = pos.getZ();
        final int integer5 = integer2 & 0xF;
        final int integer6 = integer3 & 0xF;
        final int integer7 = integer4 & 0xF;
        return (short)(integer5 | integer6 << 4 | integer7 << 8);
    }
    
    public static BlockPos joinBlockPos(final short sectionRel, final int sectionY, final ChunkPos chunkPos) {
        final int integer4 = (sectionRel & 0xF) + (chunkPos.x << 4);
        final int integer5 = (sectionRel >>> 4 & 0xF) + (sectionY << 4);
        final int integer6 = (sectionRel >>> 8 & 0xF) + (chunkPos.z << 4);
        return new BlockPos(integer4, integer5, integer6);
    }
    
    @Override
    public void markBlockForPostProcessing(final BlockPos blockPos) {
        if (!World.isHeightInvalid(blockPos)) {
            Chunk.getList(this.postProcessingLists, blockPos.getY() >> 4).add(getPackedSectionRelative(blockPos));
        }
    }
    
    @Override
    public ShortList[] getPostProcessingLists() {
        return this.postProcessingLists;
    }
    
    @Override
    public void markBlockForPostProcessing(final short short1, final int integer) {
        Chunk.getList(this.postProcessingLists, integer).add(short1);
    }
    
    @Override
    public ChunkTickScheduler<Block> getBlockTickScheduler() {
        return this.blockTickScheduler;
    }
    
    @Override
    public ChunkTickScheduler<Fluid> getFluidTickScheduler() {
        return this.fluidTickScheduler;
    }
    
    @Override
    public UpgradeData getUpgradeData() {
        return this.upgradeData;
    }
    
    @Override
    public void setInhabitedTime(final long inhabitedTime) {
        this.inhabitedTime = inhabitedTime;
    }
    
    @Override
    public long getInhabitedTime() {
        return this.inhabitedTime;
    }
    
    @Override
    public void addPendingBlockEntityTag(final CompoundTag compoundTag) {
        this.blockEntityTags.put(new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z")), compoundTag);
    }
    
    public Map<BlockPos, CompoundTag> getBlockEntityTags() {
        return Collections.<BlockPos, CompoundTag>unmodifiableMap(this.blockEntityTags);
    }
    
    @Override
    public CompoundTag getBlockEntityTagAt(final BlockPos pos) {
        return this.blockEntityTags.get(pos);
    }
    
    @Override
    public void removeBlockEntity(final BlockPos blockPos) {
        this.blockEntities.remove(blockPos);
        this.blockEntityTags.remove(blockPos);
    }
    
    @Override
    public BitSet getCarvingMask(final GenerationStep.Carver carver) {
        return this.carvingMasks.computeIfAbsent(carver, carver -> new BitSet(65536));
    }
    
    public void setCarvingMask(final GenerationStep.Carver carver, final BitSet mask) {
        this.carvingMasks.put(carver, mask);
    }
    
    @Override
    public void setLightingProvider(final LightingProvider lightingProvider) {
        this.lightingProvider = lightingProvider;
    }
    
    @Override
    public boolean isLightOn() {
        return this.isLightOn;
    }
    
    @Override
    public void setLightOn(final boolean lightOn) {
        this.isLightOn = lightOn;
        this.setShouldSave(true);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
