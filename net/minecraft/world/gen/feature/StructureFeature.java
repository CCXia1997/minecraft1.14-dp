package net.minecraft.world.gen.feature;

import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.LogManager;
import net.minecraft.world.BlockViewWithStructures;
import net.minecraft.world.chunk.Chunk;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.World;
import java.util.Iterator;
import java.util.List;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.Vec3i;
import it.unimi.dsi.fastutil.longs.LongIterator;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.structure.StructureStart;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import org.apache.logging.log4j.Logger;

public abstract class StructureFeature<C extends FeatureConfig> extends Feature<C>
{
    private static final Logger LOGGER;
    
    public StructureFeature(final Function<Dynamic<?>, ? extends C> configDeserializer) {
        super(configDeserializer, false);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final C config) {
        if (!world.getLevelProperties().hasStructures()) {
            return false;
        }
        final int integer6 = pos.getX() >> 4;
        final int integer7 = pos.getZ() >> 4;
        final int integer8 = integer6 << 4;
        final int integer9 = integer7 << 4;
        boolean boolean10 = false;
        for (final Long long12 : world.getChunk(integer6, integer7).getStructureReferences(this.getName())) {
            final ChunkPos chunkPos13 = new ChunkPos(long12);
            final StructureStart structureStart14 = world.getChunk(chunkPos13.x, chunkPos13.z).getStructureStart(this.getName());
            if (structureStart14 != null && structureStart14 != StructureStart.DEFAULT) {
                structureStart14.generateStructure(world, random, new MutableIntBoundingBox(integer8, integer9, integer8 + 15, integer9 + 15), new ChunkPos(integer6, integer7));
                boolean10 = true;
            }
        }
        return boolean10;
    }
    
    protected StructureStart isInsideStructure(final IWorld world, final BlockPos pos, final boolean exact) {
        final List<StructureStart> list4 = this.getStructureStarts(world, pos.getX() >> 4, pos.getZ() >> 4);
        for (final StructureStart structureStart6 : list4) {
            if (structureStart6.hasChildren() && structureStart6.getBoundingBox().contains(pos)) {
                if (!exact) {
                    return structureStart6;
                }
                for (final StructurePiece structurePiece8 : structureStart6.getChildren()) {
                    if (structurePiece8.getBoundingBox().contains(pos)) {
                        return structureStart6;
                    }
                }
            }
        }
        return StructureStart.DEFAULT;
    }
    
    public boolean isApproximatelyInsideStructure(final IWorld iWorld, final BlockPos blockPos) {
        return this.isInsideStructure(iWorld, blockPos, false).hasChildren();
    }
    
    public boolean isInsideStructure(final IWorld iWorld, final BlockPos blockPos) {
        return this.isInsideStructure(iWorld, blockPos, true).hasChildren();
    }
    
    @Nullable
    public BlockPos locateStructure(final World world, final ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, final BlockPos blockPos, final int integer, final boolean skipExistingChunks) {
        if (!chunkGenerator.getBiomeSource().hasStructureFeature(this)) {
            return null;
        }
        final int integer2 = blockPos.getX() >> 4;
        final int integer3 = blockPos.getZ() >> 4;
        int integer4 = 0;
        final ChunkRandom chunkRandom9 = new ChunkRandom();
        while (integer4 <= integer) {
            for (int integer5 = -integer4; integer5 <= integer4; ++integer5) {
                final boolean boolean11 = integer5 == -integer4 || integer5 == integer4;
                for (int integer6 = -integer4; integer6 <= integer4; ++integer6) {
                    final boolean boolean12 = integer6 == -integer4 || integer6 == integer4;
                    if (boolean11 || boolean12) {
                        final ChunkPos chunkPos14 = this.getStart(chunkGenerator, chunkRandom9, integer2, integer3, integer5, integer6);
                        final StructureStart structureStart15 = world.getChunk(chunkPos14.x, chunkPos14.z, ChunkStatus.STRUCTURE_STARTS).getStructureStart(this.getName());
                        if (structureStart15 != null && structureStart15.hasChildren()) {
                            if (skipExistingChunks && structureStart15.isInExistingChunk()) {
                                structureStart15.incrementReferences();
                                return structureStart15.getPos();
                            }
                            if (!skipExistingChunks) {
                                return structureStart15.getPos();
                            }
                        }
                        if (integer4 == 0) {
                            break;
                        }
                    }
                }
                if (integer4 == 0) {
                    break;
                }
            }
            ++integer4;
        }
        return null;
    }
    
    private List<StructureStart> getStructureStarts(final IWorld world, final int chunkX, final int chunkZ) {
        final List<StructureStart> list4 = Lists.newArrayList();
        final Chunk chunk5 = world.getChunk(chunkX, chunkZ, ChunkStatus.STRUCTURE_REFERENCES);
        final LongIterator longIterator6 = chunk5.getStructureReferences(this.getName()).iterator();
        while (longIterator6.hasNext()) {
            final long long7 = longIterator6.nextLong();
            final BlockViewWithStructures blockViewWithStructures9 = world.getChunk(ChunkPos.getPackedX(long7), ChunkPos.getPackedZ(long7), ChunkStatus.STRUCTURE_STARTS);
            final StructureStart structureStart10 = blockViewWithStructures9.getStructureStart(this.getName());
            if (structureStart10 != null) {
                list4.add(structureStart10);
            }
        }
        return list4;
    }
    
    protected ChunkPos getStart(final ChunkGenerator<?> chunkGenerator, final Random random, final int integer3, final int integer4, final int integer5, final int integer6) {
        return new ChunkPos(integer3 + integer5, integer4 + integer6);
    }
    
    public abstract boolean shouldStartAt(final ChunkGenerator<?> arg1, final Random arg2, final int arg3, final int arg4);
    
    public abstract StructureStartFactory getStructureStartFactory();
    
    public abstract String getName();
    
    public abstract int getRadius();
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public interface StructureStartFactory
    {
        StructureStart create(final StructureFeature<?> arg1, final int arg2, final int arg3, final Biome arg4, final MutableIntBoundingBox arg5, final int arg6, final long arg7);
    }
}
