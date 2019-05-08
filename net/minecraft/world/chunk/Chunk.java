package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import java.util.BitSet;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.fluid.Fluid;
import net.minecraft.block.Block;
import net.minecraft.world.TickScheduler;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import it.unimi.dsi.fastutil.shorts.ShortList;
import org.apache.logging.log4j.LogManager;
import net.minecraft.world.biome.Biome;
import net.minecraft.structure.StructureStart;
import net.minecraft.world.Heightmap;
import java.util.Map;
import java.util.Collection;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.light.LightingProvider;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.block.entity.BlockEntity;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockViewWithStructures;

public interface Chunk extends BlockViewWithStructures
{
    @Nullable
    BlockState setBlockState(final BlockPos arg1, final BlockState arg2, final boolean arg3);
    
    void setBlockEntity(final BlockPos arg1, final BlockEntity arg2);
    
    void addEntity(final Entity arg1);
    
    @Nullable
    default ChunkSection getHighestNonEmptySection() {
        final ChunkSection[] arr1 = this.getSectionArray();
        for (int integer2 = arr1.length - 1; integer2 >= 0; --integer2) {
            final ChunkSection chunkSection3 = arr1[integer2];
            if (!ChunkSection.isEmpty(chunkSection3)) {
                return chunkSection3;
            }
        }
        return null;
    }
    
    default int getHighestNonEmptySectionYOffset() {
        final ChunkSection chunkSection1 = this.getHighestNonEmptySection();
        return (chunkSection1 == null) ? 0 : chunkSection1.getYOffset();
    }
    
    Set<BlockPos> getBlockEntityPositions();
    
    ChunkSection[] getSectionArray();
    
    @Nullable
    LightingProvider getLightingProvider();
    
    default int getLightLevel(final BlockPos pos, final int darkness, final boolean includeSkyLight) {
        final LightingProvider lightingProvider4 = this.getLightingProvider();
        if (lightingProvider4 == null || !this.getStatus().isAtLeast(ChunkStatus.LIGHT)) {
            return 0;
        }
        final int integer5 = includeSkyLight ? (lightingProvider4.get(LightType.SKY).getLightLevel(pos) - darkness) : 0;
        final int integer6 = lightingProvider4.get(LightType.BLOCK).getLightLevel(pos);
        return Math.max(integer6, integer5);
    }
    
    Collection<Map.Entry<Heightmap.Type, Heightmap>> getHeightmaps();
    
    void setHeightmap(final Heightmap.Type arg1, final long[] arg2);
    
    Heightmap getHeightmap(final Heightmap.Type arg1);
    
    int sampleHeightmap(final Heightmap.Type arg1, final int arg2, final int arg3);
    
    ChunkPos getPos();
    
    void setLastSaveTime(final long arg1);
    
    Map<String, StructureStart> getStructureStarts();
    
    void setStructureStarts(final Map<String, StructureStart> arg1);
    
    default Biome getBiome(final BlockPos pos) {
        final int integer2 = pos.getX() & 0xF;
        final int integer3 = pos.getZ() & 0xF;
        return this.getBiomeArray()[integer3 << 4 | integer2];
    }
    
    default boolean a(int integer1, int integer2) {
        if (integer1 < 0) {
            integer1 = 0;
        }
        if (integer2 >= 256) {
            integer2 = 255;
        }
        for (int integer3 = integer1; integer3 <= integer2; integer3 += 16) {
            if (!ChunkSection.isEmpty(this.getSectionArray()[integer3 >> 4])) {
                return false;
            }
        }
        return true;
    }
    
    Biome[] getBiomeArray();
    
    void setShouldSave(final boolean arg1);
    
    boolean needsSaving();
    
    ChunkStatus getStatus();
    
    void removeBlockEntity(final BlockPos arg1);
    
    void setLightingProvider(final LightingProvider arg1);
    
    default void markBlockForPostProcessing(final BlockPos blockPos) {
        LogManager.getLogger().warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", blockPos);
    }
    
    ShortList[] getPostProcessingLists();
    
    default void markBlockForPostProcessing(final short short1, final int integer) {
        getList(this.getPostProcessingLists(), integer).add(short1);
    }
    
    default void addPendingBlockEntityTag(final CompoundTag compoundTag) {
        LogManager.getLogger().warn("Trying to set a BlockEntity, but this operation is not supported.");
    }
    
    @Nullable
    default CompoundTag getBlockEntityTagAt(final BlockPos pos) {
        throw new UnsupportedOperationException();
    }
    
    default void setBiomeArray(final Biome[] biomeArray) {
        throw new UnsupportedOperationException();
    }
    
    Stream<BlockPos> getLightSourcesStream();
    
    TickScheduler<Block> getBlockTickScheduler();
    
    TickScheduler<Fluid> getFluidTickScheduler();
    
    default BitSet getCarvingMask(final GenerationStep.Carver carver) {
        throw new RuntimeException("Meaningless in this context");
    }
    
    UpgradeData getUpgradeData();
    
    void setInhabitedTime(final long arg1);
    
    long getInhabitedTime();
    
    default ShortList getList(final ShortList[] lists, final int index) {
        if (lists[index] == null) {
            lists[index] = (ShortList)new ShortArrayList();
        }
        return lists[index];
    }
    
    boolean isLightOn();
    
    void setLightOn(final boolean arg1);
}
