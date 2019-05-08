package net.minecraft.world.chunk;

import net.minecraft.world.TickScheduler;
import java.util.BitSet;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.registry.Registry;
import net.minecraft.block.Block;
import net.minecraft.world.ChunkTickScheduler;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.biome.Biome;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import net.minecraft.structure.StructureStart;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.block.BlockState;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ReadOnlyChunk extends ProtoChunk
{
    private final WorldChunk wrapped;
    
    public ReadOnlyChunk(final WorldChunk wrapped) {
        super(wrapped.getPos(), UpgradeData.NO_UPGRADE_DATA);
        this.wrapped = wrapped;
    }
    
    @Nullable
    @Override
    public BlockEntity getBlockEntity(final BlockPos pos) {
        return this.wrapped.getBlockEntity(pos);
    }
    
    @Nullable
    @Override
    public BlockState getBlockState(final BlockPos pos) {
        return this.wrapped.getBlockState(pos);
    }
    
    @Override
    public FluidState getFluidState(final BlockPos pos) {
        return this.wrapped.getFluidState(pos);
    }
    
    @Override
    public int getMaxLightLevel() {
        return this.wrapped.getMaxLightLevel();
    }
    
    @Nullable
    @Override
    public BlockState setBlockState(final BlockPos pos, final BlockState state, final boolean boolean3) {
        return null;
    }
    
    @Override
    public void setBlockEntity(final BlockPos pos, final BlockEntity blockEntity) {
    }
    
    @Override
    public void addEntity(final Entity entity) {
    }
    
    @Override
    public void setStatus(final ChunkStatus chunkStatus) {
    }
    
    @Override
    public ChunkSection[] getSectionArray() {
        return this.wrapped.getSectionArray();
    }
    
    @Nullable
    @Override
    public LightingProvider getLightingProvider() {
        return this.wrapped.getLightingProvider();
    }
    
    @Override
    public void setHeightmap(final Heightmap.Type type, final long[] heightmap) {
    }
    
    private Heightmap.Type transformHeightmapType(final Heightmap.Type type) {
        if (type == Heightmap.Type.a) {
            return Heightmap.Type.b;
        }
        if (type == Heightmap.Type.c) {
            return Heightmap.Type.d;
        }
        return type;
    }
    
    @Override
    public int sampleHeightmap(final Heightmap.Type type, final int x, final int z) {
        return this.wrapped.sampleHeightmap(this.transformHeightmapType(type), x, z);
    }
    
    @Override
    public ChunkPos getPos() {
        return this.wrapped.getPos();
    }
    
    @Override
    public void setLastSaveTime(final long lastSaveTime) {
    }
    
    @Nullable
    @Override
    public StructureStart getStructureStart(final String structureId) {
        return this.wrapped.getStructureStart(structureId);
    }
    
    @Override
    public void setStructureStart(final String structureId, final StructureStart structureStart) {
    }
    
    @Override
    public Map<String, StructureStart> getStructureStarts() {
        return this.wrapped.getStructureStarts();
    }
    
    @Override
    public void setStructureStarts(final Map<String, StructureStart> map) {
    }
    
    @Override
    public LongSet getStructureReferences(final String structureId) {
        return this.wrapped.getStructureReferences(structureId);
    }
    
    @Override
    public void addStructureReference(final String structureId, final long reference) {
    }
    
    @Override
    public Map<String, LongSet> getStructureReferences() {
        return this.wrapped.getStructureReferences();
    }
    
    @Override
    public void setStructureReferences(final Map<String, LongSet> structureReferences) {
    }
    
    @Override
    public Biome[] getBiomeArray() {
        return this.wrapped.getBiomeArray();
    }
    
    @Override
    public void setShouldSave(final boolean shouldSave) {
    }
    
    @Override
    public boolean needsSaving() {
        return false;
    }
    
    @Override
    public ChunkStatus getStatus() {
        return this.wrapped.getStatus();
    }
    
    @Override
    public void removeBlockEntity(final BlockPos blockPos) {
    }
    
    @Override
    public void markBlockForPostProcessing(final BlockPos blockPos) {
    }
    
    @Override
    public void addPendingBlockEntityTag(final CompoundTag compoundTag) {
    }
    
    @Nullable
    @Override
    public CompoundTag getBlockEntityTagAt(final BlockPos pos) {
        return this.wrapped.getBlockEntityTagAt(pos);
    }
    
    @Override
    public void setBiomeArray(final Biome[] biomeArray) {
    }
    
    @Override
    public Stream<BlockPos> getLightSourcesStream() {
        return this.wrapped.getLightSourcesStream();
    }
    
    @Override
    public ChunkTickScheduler<Block> getBlockTickScheduler() {
        return new ChunkTickScheduler<Block>(block -> block.getDefaultState().isAir(), Registry.BLOCK::getId, Registry.BLOCK::get, this.getPos());
    }
    
    @Override
    public ChunkTickScheduler<Fluid> getFluidTickScheduler() {
        return new ChunkTickScheduler<Fluid>(fluid -> fluid == Fluids.EMPTY, Registry.FLUID::getId, Registry.FLUID::get, this.getPos());
    }
    
    @Override
    public BitSet getCarvingMask(final GenerationStep.Carver carver) {
        return this.wrapped.getCarvingMask(carver);
    }
    
    public WorldChunk getWrappedChunk() {
        return this.wrapped;
    }
    
    @Override
    public boolean isLightOn() {
        return this.wrapped.isLightOn();
    }
    
    @Override
    public void setLightOn(final boolean lightOn) {
        this.wrapped.setLightOn(lightOn);
    }
}
