package net.minecraft.world.gen.chunk;

import net.minecraft.block.Blocks;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.util.registry.Registry;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.IWorld;
import net.minecraft.block.BlockState;
import java.util.List;

public class DebugChunkGenerator extends ChunkGenerator<DebugChunkGeneratorConfig>
{
    private static final List<BlockState> BLOCK_STATES;
    private static final int X_SIDE_LENGTH;
    private static final int Z_SIDE_LENGTH;
    protected static final BlockState AIR;
    protected static final BlockState BARRIER;
    
    public DebugChunkGenerator(final IWorld world, final BiomeSource biomeSource, final DebugChunkGeneratorConfig config) {
        super(world, biomeSource, config);
    }
    
    @Override
    public void buildSurface(final Chunk chunk) {
    }
    
    @Override
    public void carve(final Chunk chunk, final GenerationStep.Carver carverStep) {
    }
    
    @Override
    public int getSpawnHeight() {
        return this.world.getSeaLevel() + 1;
    }
    
    @Override
    public void generateFeatures(final ChunkRegion chunkRegion) {
        final BlockPos.Mutable mutable2 = new BlockPos.Mutable();
        final int integer3 = chunkRegion.getCenterChunkX();
        final int integer4 = chunkRegion.getCenterChunkZ();
        for (int integer5 = 0; integer5 < 16; ++integer5) {
            for (int integer6 = 0; integer6 < 16; ++integer6) {
                final int integer7 = (integer3 << 4) + integer5;
                final int integer8 = (integer4 << 4) + integer6;
                chunkRegion.setBlockState(mutable2.set(integer7, 60, integer8), DebugChunkGenerator.BARRIER, 2);
                final BlockState blockState9 = getBlockState(integer7, integer8);
                if (blockState9 != null) {
                    chunkRegion.setBlockState(mutable2.set(integer7, 70, integer8), blockState9, 2);
                }
            }
        }
    }
    
    @Override
    public void populateNoise(final IWorld world, final Chunk chunk) {
    }
    
    @Override
    public int getHeightOnGround(final int x, final int z, final Heightmap.Type heightmapType) {
        return 0;
    }
    
    public static BlockState getBlockState(int x, int z) {
        BlockState blockState3 = DebugChunkGenerator.AIR;
        if (x > 0 && z > 0 && x % 2 != 0 && z % 2 != 0) {
            x /= 2;
            z /= 2;
            if (x <= DebugChunkGenerator.X_SIDE_LENGTH && z <= DebugChunkGenerator.Z_SIDE_LENGTH) {
                final int integer4 = MathHelper.abs(x * DebugChunkGenerator.X_SIDE_LENGTH + z);
                if (integer4 < DebugChunkGenerator.BLOCK_STATES.size()) {
                    blockState3 = DebugChunkGenerator.BLOCK_STATES.get(integer4);
                }
            }
        }
        return blockState3;
    }
    
    static {
        BLOCK_STATES = StreamSupport.<Block>stream(Registry.BLOCK.spliterator(), false).flatMap(block -> block.getStateFactory().getStates().stream()).collect(Collectors.toList());
        X_SIDE_LENGTH = MathHelper.ceil(MathHelper.sqrt((float)DebugChunkGenerator.BLOCK_STATES.size()));
        Z_SIDE_LENGTH = MathHelper.ceil(DebugChunkGenerator.BLOCK_STATES.size() / (float)DebugChunkGenerator.X_SIDE_LENGTH);
        AIR = Blocks.AIR.getDefaultState();
        BARRIER = Blocks.gg.getDefaultState();
    }
}
