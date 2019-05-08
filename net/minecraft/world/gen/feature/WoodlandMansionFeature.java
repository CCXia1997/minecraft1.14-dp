package net.minecraft.world.gen.feature;

import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import java.util.List;
import net.minecraft.structure.StructurePiece;
import java.util.Collection;
import net.minecraft.structure.WoodlandMansionGenerator;
import com.google.common.collect.Lists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.util.BlockRotation;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.structure.StructureStart;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.chunk.ChunkPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class WoodlandMansionFeature extends StructureFeature<DefaultFeatureConfig>
{
    public WoodlandMansionFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    protected ChunkPos getStart(final ChunkGenerator<?> chunkGenerator, final Random random, final int integer3, final int integer4, final int integer5, final int integer6) {
        final int integer7 = ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getMansionDistance();
        final int integer8 = ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getMansionSeparation();
        final int integer9 = integer3 + integer7 * integer5;
        final int integer10 = integer4 + integer7 * integer6;
        final int integer11 = (integer9 < 0) ? (integer9 - integer7 + 1) : integer9;
        final int integer12 = (integer10 < 0) ? (integer10 - integer7 + 1) : integer10;
        int integer13 = integer11 / integer7;
        int integer14 = integer12 / integer7;
        ((ChunkRandom)random).setStructureSeed(chunkGenerator.getSeed(), integer13, integer14, 10387319);
        integer13 *= integer7;
        integer14 *= integer7;
        integer13 += (random.nextInt(integer7 - integer8) + random.nextInt(integer7 - integer8)) / 2;
        integer14 += (random.nextInt(integer7 - integer8) + random.nextInt(integer7 - integer8)) / 2;
        return new ChunkPos(integer13, integer14);
    }
    
    @Override
    public boolean shouldStartAt(final ChunkGenerator<?> chunkGenerator, final Random random, final int chunkX, final int chunkZ) {
        final ChunkPos chunkPos5 = this.getStart(chunkGenerator, random, chunkX, chunkZ, 0, 0);
        if (chunkX == chunkPos5.x && chunkZ == chunkPos5.z) {
            final Set<Biome> set6 = chunkGenerator.getBiomeSource().getBiomesInArea(chunkX * 16 + 9, chunkZ * 16 + 9, 32);
            for (final Biome biome8 : set6) {
                if (!chunkGenerator.hasStructure(biome8, Feature.WOODLAND_MANSION)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }
    
    @Override
    public String getName() {
        return "Mansion";
    }
    
    @Override
    public int getRadius() {
        return 8;
    }
    
    public static class Start extends StructureStart
    {
        public Start(final StructureFeature<?> structureFeature, final int chunkX, final int chunkZ, final Biome biome, final MutableIntBoundingBox mutableIntBoundingBox, final int integer6, final long long7) {
            super(structureFeature, chunkX, chunkZ, biome, mutableIntBoundingBox, integer6, long7);
        }
        
        @Override
        public void initialize(final ChunkGenerator<?> chunkGenerator, final StructureManager structureManager, final int x, final int z, final Biome biome) {
            final BlockRotation blockRotation6 = BlockRotation.values()[this.random.nextInt(BlockRotation.values().length)];
            int integer7 = 5;
            int integer8 = 5;
            if (blockRotation6 == BlockRotation.ROT_90) {
                integer7 = -5;
            }
            else if (blockRotation6 == BlockRotation.ROT_180) {
                integer7 = -5;
                integer8 = -5;
            }
            else if (blockRotation6 == BlockRotation.ROT_270) {
                integer8 = -5;
            }
            final int integer9 = (x << 4) + 7;
            final int integer10 = (z << 4) + 7;
            final int integer11 = chunkGenerator.getHeightInGround(integer9, integer10, Heightmap.Type.a);
            final int integer12 = chunkGenerator.getHeightInGround(integer9, integer10 + integer8, Heightmap.Type.a);
            final int integer13 = chunkGenerator.getHeightInGround(integer9 + integer7, integer10, Heightmap.Type.a);
            final int integer14 = chunkGenerator.getHeightInGround(integer9 + integer7, integer10 + integer8, Heightmap.Type.a);
            final int integer15 = Math.min(Math.min(integer11, integer12), Math.min(integer13, integer14));
            if (integer15 < 60) {
                return;
            }
            final BlockPos blockPos16 = new BlockPos(x * 16 + 8, integer15 + 1, z * 16 + 8);
            final List<WoodlandMansionGenerator.Piece> list17 = Lists.newLinkedList();
            WoodlandMansionGenerator.addPieces(structureManager, blockPos16, blockRotation6, list17, this.random);
            this.children.addAll(list17);
            this.setBoundingBoxFromChildren();
        }
        
        @Override
        public void generateStructure(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            super.generateStructure(world, random, boundingBox, pos);
            final int integer5 = this.boundingBox.minY;
            for (int integer6 = boundingBox.minX; integer6 <= boundingBox.maxX; ++integer6) {
                for (int integer7 = boundingBox.minZ; integer7 <= boundingBox.maxZ; ++integer7) {
                    final BlockPos blockPos8 = new BlockPos(integer6, integer5, integer7);
                    if (!world.isAir(blockPos8) && this.boundingBox.contains(blockPos8)) {
                        boolean boolean9 = false;
                        for (final StructurePiece structurePiece11 : this.children) {
                            if (structurePiece11.getBoundingBox().contains(blockPos8)) {
                                boolean9 = true;
                                break;
                            }
                        }
                        if (boolean9) {
                            for (int integer8 = integer5 - 1; integer8 > 1; --integer8) {
                                final BlockPos blockPos9 = new BlockPos(integer6, integer8, integer7);
                                if (!world.isAir(blockPos9) && !world.getBlockState(blockPos9).getMaterial().isLiquid()) {
                                    break;
                                }
                                world.setBlockState(blockPos9, Blocks.m.getDefaultState(), 2);
                            }
                        }
                    }
                }
            }
        }
    }
}
