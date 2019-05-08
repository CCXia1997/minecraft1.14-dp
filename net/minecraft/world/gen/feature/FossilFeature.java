package net.minecraft.world.gen.feature;

import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.world.Heightmap;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.Identifier;

public class FossilFeature extends Feature<DefaultFeatureConfig>
{
    private static final Identifier SPINE_1;
    private static final Identifier SPINE_2;
    private static final Identifier SPINE_3;
    private static final Identifier SPINE_4;
    private static final Identifier SPINE_1_COAL;
    private static final Identifier SPINE_2_COAL;
    private static final Identifier SPINE_3_COAL;
    private static final Identifier SPINE_4_COAL;
    private static final Identifier SKULL_1;
    private static final Identifier SKULL_2;
    private static final Identifier SKULL_3;
    private static final Identifier SKULL_4;
    private static final Identifier SKULL_1_COAL;
    private static final Identifier SKULL_2_COAL;
    private static final Identifier SKULL_3_COAL;
    private static final Identifier SKULL_4_COAL;
    private static final Identifier[] FOSSILS;
    private static final Identifier[] COAL_FOSSILS;
    
    public FossilFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final DefaultFeatureConfig config) {
        final Random random2 = world.getRandom();
        final BlockRotation[] arr7 = BlockRotation.values();
        final BlockRotation blockRotation8 = arr7[random2.nextInt(arr7.length)];
        final int integer9 = random2.nextInt(FossilFeature.FOSSILS.length);
        final StructureManager structureManager10 = ((ServerWorld)world.getWorld()).getSaveHandler().getStructureManager();
        final Structure structure11 = structureManager10.getStructureOrBlank(FossilFeature.FOSSILS[integer9]);
        final Structure structure12 = structureManager10.getStructureOrBlank(FossilFeature.COAL_FOSSILS[integer9]);
        final ChunkPos chunkPos13 = new ChunkPos(pos);
        final MutableIntBoundingBox mutableIntBoundingBox14 = new MutableIntBoundingBox(chunkPos13.getStartX(), 0, chunkPos13.getStartZ(), chunkPos13.getEndX(), 256, chunkPos13.getEndZ());
        final StructurePlacementData structurePlacementData15 = new StructurePlacementData().setRotation(blockRotation8).setBoundingBox(mutableIntBoundingBox14).setRandom(random2).addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
        final BlockPos blockPos16 = structure11.a(blockRotation8);
        final int integer10 = random2.nextInt(16 - blockPos16.getX());
        final int integer11 = random2.nextInt(16 - blockPos16.getZ());
        int integer12 = 256;
        for (int integer13 = 0; integer13 < blockPos16.getX(); ++integer13) {
            for (int integer14 = 0; integer14 < blockPos16.getZ(); ++integer14) {
                integer12 = Math.min(integer12, world.getTop(Heightmap.Type.c, pos.getX() + integer13 + integer10, pos.getZ() + integer14 + integer11));
            }
        }
        int integer13 = Math.max(integer12 - 15 - random2.nextInt(10), 10);
        final BlockPos blockPos17 = structure11.a(pos.add(integer10, integer13, integer11), BlockMirror.NONE, blockRotation8);
        final BlockRotStructureProcessor blockRotStructureProcessor22 = new BlockRotStructureProcessor(0.9f);
        structurePlacementData15.clearProcessors().addProcessor(blockRotStructureProcessor22);
        structure11.a(world, blockPos17, structurePlacementData15, 4);
        structurePlacementData15.removeProcessor(blockRotStructureProcessor22);
        final BlockRotStructureProcessor blockRotStructureProcessor23 = new BlockRotStructureProcessor(0.1f);
        structurePlacementData15.clearProcessors().addProcessor(blockRotStructureProcessor23);
        structure12.a(world, blockPos17, structurePlacementData15, 4);
        return true;
    }
    
    static {
        SPINE_1 = new Identifier("fossil/spine_1");
        SPINE_2 = new Identifier("fossil/spine_2");
        SPINE_3 = new Identifier("fossil/spine_3");
        SPINE_4 = new Identifier("fossil/spine_4");
        SPINE_1_COAL = new Identifier("fossil/spine_1_coal");
        SPINE_2_COAL = new Identifier("fossil/spine_2_coal");
        SPINE_3_COAL = new Identifier("fossil/spine_3_coal");
        SPINE_4_COAL = new Identifier("fossil/spine_4_coal");
        SKULL_1 = new Identifier("fossil/skull_1");
        SKULL_2 = new Identifier("fossil/skull_2");
        SKULL_3 = new Identifier("fossil/skull_3");
        SKULL_4 = new Identifier("fossil/skull_4");
        SKULL_1_COAL = new Identifier("fossil/skull_1_coal");
        SKULL_2_COAL = new Identifier("fossil/skull_2_coal");
        SKULL_3_COAL = new Identifier("fossil/skull_3_coal");
        SKULL_4_COAL = new Identifier("fossil/skull_4_coal");
        FOSSILS = new Identifier[] { FossilFeature.SPINE_1, FossilFeature.SPINE_2, FossilFeature.SPINE_3, FossilFeature.SPINE_4, FossilFeature.SKULL_1, FossilFeature.SKULL_2, FossilFeature.SKULL_3, FossilFeature.SKULL_4 };
        COAL_FOSSILS = new Identifier[] { FossilFeature.SPINE_1_COAL, FossilFeature.SPINE_2_COAL, FossilFeature.SPINE_3_COAL, FossilFeature.SPINE_4_COAL, FossilFeature.SKULL_1_COAL, FossilFeature.SKULL_2_COAL, FossilFeature.SKULL_3_COAL, FossilFeature.SKULL_4_COAL };
    }
}
