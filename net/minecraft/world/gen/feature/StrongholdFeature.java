package net.minecraft.world.gen.feature;

import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StrongholdGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.MutableIntBoundingBox;
import java.util.Iterator;
import net.minecraft.world.biome.Biome;
import net.minecraft.util.registry.Registry;
import javax.annotation.Nullable;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.World;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.structure.StructureStart;
import java.util.List;
import net.minecraft.world.chunk.ChunkPos;

public class StrongholdFeature extends StructureFeature<DefaultFeatureConfig>
{
    private boolean stateStillValid;
    private ChunkPos[] startPositions;
    private final List<StructureStart> starts;
    private long lastSeed;
    
    public StrongholdFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
        this.starts = Lists.newArrayList();
    }
    
    @Override
    public boolean shouldStartAt(final ChunkGenerator<?> chunkGenerator, final Random random, final int chunkX, final int chunkZ) {
        if (this.lastSeed != chunkGenerator.getSeed()) {
            this.invalidateState();
        }
        if (!this.stateStillValid) {
            this.initialize(chunkGenerator);
            this.stateStillValid = true;
        }
        for (final ChunkPos chunkPos8 : this.startPositions) {
            if (chunkX == chunkPos8.x && chunkZ == chunkPos8.z) {
                return true;
            }
        }
        return false;
    }
    
    private void invalidateState() {
        this.stateStillValid = false;
        this.startPositions = null;
        this.starts.clear();
    }
    
    @Override
    public StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }
    
    @Override
    public String getName() {
        return "Stronghold";
    }
    
    @Override
    public int getRadius() {
        return 8;
    }
    
    @Nullable
    @Override
    public BlockPos locateStructure(final World world, final ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, final BlockPos blockPos, final int integer, final boolean skipExistingChunks) {
        if (!chunkGenerator.getBiomeSource().hasStructureFeature(this)) {
            return null;
        }
        if (this.lastSeed != world.getSeed()) {
            this.invalidateState();
        }
        if (!this.stateStillValid) {
            this.initialize(chunkGenerator);
            this.stateStillValid = true;
        }
        BlockPos blockPos2 = null;
        final BlockPos.Mutable mutable7 = new BlockPos.Mutable();
        double double8 = Double.MAX_VALUE;
        for (final ChunkPos chunkPos13 : this.startPositions) {
            mutable7.set((chunkPos13.x << 4) + 8, 32, (chunkPos13.z << 4) + 8);
            final double double9 = mutable7.getSquaredDistance(blockPos);
            if (blockPos2 == null) {
                blockPos2 = new BlockPos(mutable7);
                double8 = double9;
            }
            else if (double9 < double8) {
                blockPos2 = new BlockPos(mutable7);
                double8 = double9;
            }
        }
        return blockPos2;
    }
    
    private void initialize(final ChunkGenerator<?> chunkGenerator) {
        this.lastSeed = chunkGenerator.getSeed();
        final List<Biome> list2 = Lists.newArrayList();
        for (final Biome biome4 : Registry.BIOME) {
            if (biome4 != null && chunkGenerator.hasStructure(biome4, Feature.STRONGHOLD)) {
                list2.add(biome4);
            }
        }
        final int integer3 = ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getStrongholdDistance();
        final int integer4 = ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getStrongholdCount();
        int integer5 = ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getStrongholdSpread();
        this.startPositions = new ChunkPos[integer4];
        int integer6 = 0;
        for (final StructureStart structureStart8 : this.starts) {
            if (integer6 < this.startPositions.length) {
                this.startPositions[integer6++] = new ChunkPos(structureStart8.getChunkX(), structureStart8.getChunkZ());
            }
        }
        final Random random7 = new Random();
        random7.setSeed(chunkGenerator.getSeed());
        double double8 = random7.nextDouble() * 3.141592653589793 * 2.0;
        final int integer7 = integer6;
        if (integer7 < this.startPositions.length) {
            int integer8 = 0;
            int integer9 = 0;
            for (int integer10 = 0; integer10 < this.startPositions.length; ++integer10) {
                final double double9 = 4 * integer3 + integer3 * integer9 * 6 + (random7.nextDouble() - 0.5) * (integer3 * 2.5);
                int integer11 = (int)Math.round(Math.cos(double8) * double9);
                int integer12 = (int)Math.round(Math.sin(double8) * double9);
                final BlockPos blockPos18 = chunkGenerator.getBiomeSource().locateBiome((integer11 << 4) + 8, (integer12 << 4) + 8, 112, list2, random7);
                if (blockPos18 != null) {
                    integer11 = blockPos18.getX() >> 4;
                    integer12 = blockPos18.getZ() >> 4;
                }
                if (integer10 >= integer7) {
                    this.startPositions[integer10] = new ChunkPos(integer11, integer12);
                }
                double8 += 6.283185307179586 / integer5;
                if (++integer8 == integer5) {
                    ++integer9;
                    integer8 = 0;
                    integer5 += 2 * integer5 / (integer9 + 1);
                    integer5 = Math.min(integer5, this.startPositions.length - integer10);
                    double8 += random7.nextDouble() * 3.141592653589793 * 2.0;
                }
            }
        }
    }
    
    public static class Start extends StructureStart
    {
        public Start(final StructureFeature<?> structureFeature, final int chunkX, final int chunkZ, final Biome biome, final MutableIntBoundingBox mutableIntBoundingBox, final int integer6, final long long7) {
            super(structureFeature, chunkX, chunkZ, biome, mutableIntBoundingBox, integer6, long7);
        }
        
        @Override
        public void initialize(final ChunkGenerator<?> chunkGenerator, final StructureManager structureManager, final int x, final int z, final Biome biome) {
            int integer6 = 0;
            final long long7 = chunkGenerator.getSeed();
            StrongholdGenerator.Start start9;
            do {
                this.children.clear();
                this.boundingBox = MutableIntBoundingBox.empty();
                this.random.setStructureSeed(long7 + integer6++, x, z);
                StrongholdGenerator.a();
                start9 = new StrongholdGenerator.Start(this.random, (x << 4) + 2, (z << 4) + 2);
                this.children.add(start9);
                start9.a(start9, this.children, this.random);
                final List<StructurePiece> list10 = start9.c;
                while (!list10.isEmpty()) {
                    final int integer7 = this.random.nextInt(list10.size());
                    final StructurePiece structurePiece12 = list10.remove(integer7);
                    structurePiece12.a(start9, this.children, this.random);
                }
                this.setBoundingBoxFromChildren();
                this.a(chunkGenerator.getSeaLevel(), this.random, 10);
            } while (this.children.isEmpty() || start9.b == null);
            ((StrongholdFeature)this.getFeature()).starts.add(this);
        }
    }
}
