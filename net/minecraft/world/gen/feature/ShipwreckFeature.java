package net.minecraft.world.gen.feature;

import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import java.util.Random;
import net.minecraft.structure.ShipwreckGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.BlockRotation;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.structure.StructureStart;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class ShipwreckFeature extends AbstractTempleFeature<ShipwreckFeatureConfig>
{
    public ShipwreckFeature(final Function<Dynamic<?>, ? extends ShipwreckFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public String getName() {
        return "Shipwreck";
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
        return 165745295;
    }
    
    @Override
    protected int getSpacing(final ChunkGenerator<?> chunkGenerator) {
        return ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getShipwreckSpacing();
    }
    
    @Override
    protected int getSeparation(final ChunkGenerator<?> chunkGenerator) {
        return ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getShipwreckSeparation();
    }
    
    public static class Start extends StructureStart
    {
        public Start(final StructureFeature<?> structureFeature, final int chunkX, final int chunkZ, final Biome biome, final MutableIntBoundingBox mutableIntBoundingBox, final int integer6, final long long7) {
            super(structureFeature, chunkX, chunkZ, biome, mutableIntBoundingBox, integer6, long7);
        }
        
        @Override
        public void initialize(final ChunkGenerator<?> chunkGenerator, final StructureManager structureManager, final int x, final int z, final Biome biome) {
            final ShipwreckFeatureConfig shipwreckFeatureConfig6 = chunkGenerator.<ShipwreckFeatureConfig>getStructureConfig(biome, Feature.SHIPWRECK);
            final BlockRotation blockRotation7 = BlockRotation.values()[this.random.nextInt(BlockRotation.values().length)];
            final BlockPos blockPos8 = new BlockPos(x * 16, 90, z * 16);
            ShipwreckGenerator.addParts(structureManager, blockPos8, blockRotation7, this.children, this.random, shipwreckFeatureConfig6);
            this.setBoundingBoxFromChildren();
        }
    }
}
