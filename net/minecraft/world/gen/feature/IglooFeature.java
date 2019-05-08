package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.structure.IglooGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.structure.StructureManager;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.structure.StructureStart;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class IglooFeature extends AbstractTempleFeature<DefaultFeatureConfig>
{
    public IglooFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public String getName() {
        return "Igloo";
    }
    
    @Override
    public int getRadius() {
        return 3;
    }
    
    @Override
    public StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }
    
    @Override
    protected int getSeedModifier() {
        return 14357618;
    }
    
    public static class Start extends StructureStart
    {
        public Start(final StructureFeature<?> structureFeature, final int chunkX, final int chunkZ, final Biome biome, final MutableIntBoundingBox mutableIntBoundingBox, final int integer6, final long long7) {
            super(structureFeature, chunkX, chunkZ, biome, mutableIntBoundingBox, integer6, long7);
        }
        
        @Override
        public void initialize(final ChunkGenerator<?> chunkGenerator, final StructureManager structureManager, final int x, final int z, final Biome biome) {
            final DefaultFeatureConfig defaultFeatureConfig6 = chunkGenerator.<DefaultFeatureConfig>getStructureConfig(biome, Feature.IGLOO);
            final int integer7 = x * 16;
            final int integer8 = z * 16;
            final BlockPos blockPos9 = new BlockPos(integer7, 90, integer8);
            final BlockRotation blockRotation10 = BlockRotation.values()[this.random.nextInt(BlockRotation.values().length)];
            IglooGenerator.addPieces(structureManager, blockPos9, blockRotation10, this.children, this.random, defaultFeatureConfig6);
            this.setBoundingBoxFromChildren();
        }
    }
}
