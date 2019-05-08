package net.minecraft.world.gen.feature;

import net.minecraft.structure.BuriedTreasureGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.structure.StructureStart;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class BuriedTreasureFeature extends StructureFeature<BuriedTreasureFeatureConfig>
{
    public BuriedTreasureFeature(final Function<Dynamic<?>, ? extends BuriedTreasureFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean shouldStartAt(final ChunkGenerator<?> chunkGenerator, final Random random, final int chunkX, final int chunkZ) {
        final Biome biome5 = chunkGenerator.getBiomeSource().getBiome(new BlockPos((chunkX << 4) + 9, 0, (chunkZ << 4) + 9));
        if (chunkGenerator.hasStructure(biome5, Feature.BURIED_TREASURE)) {
            ((ChunkRandom)random).setStructureSeed(chunkGenerator.getSeed(), chunkX, chunkZ, 10387320);
            final BuriedTreasureFeatureConfig buriedTreasureFeatureConfig6 = chunkGenerator.<BuriedTreasureFeatureConfig>getStructureConfig(biome5, Feature.BURIED_TREASURE);
            return random.nextFloat() < buriedTreasureFeatureConfig6.probability;
        }
        return false;
    }
    
    @Override
    public StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }
    
    @Override
    public String getName() {
        return "Buried_Treasure";
    }
    
    @Override
    public int getRadius() {
        return 1;
    }
    
    public static class Start extends StructureStart
    {
        public Start(final StructureFeature<?> structureFeature, final int chunkX, final int chunkZ, final Biome biome, final MutableIntBoundingBox mutableIntBoundingBox, final int integer6, final long long7) {
            super(structureFeature, chunkX, chunkZ, biome, mutableIntBoundingBox, integer6, long7);
        }
        
        @Override
        public void initialize(final ChunkGenerator<?> chunkGenerator, final StructureManager structureManager, final int x, final int z, final Biome biome) {
            final int integer6 = x * 16;
            final int integer7 = z * 16;
            final BlockPos blockPos8 = new BlockPos(integer6 + 9, 90, integer7 + 9);
            this.children.add(new BuriedTreasureGenerator.Piece(blockPos8));
            this.setBoundingBoxFromChildren();
        }
        
        @Override
        public BlockPos getPos() {
            return new BlockPos((this.getChunkX() << 4) + 9, 0, (this.getChunkZ() << 4) + 9);
        }
    }
}
