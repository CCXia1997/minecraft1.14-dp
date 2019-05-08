package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.util.math.BlockPos;

public class VoidStartPlatformFeature extends Feature<DefaultFeatureConfig>
{
    private static final BlockPos a;
    private static final ChunkPos aS;
    
    public VoidStartPlatformFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    private static int a(final int integer1, final int integer2, final int integer3, final int integer4) {
        return Math.max(Math.abs(integer1 - integer3), Math.abs(integer2 - integer4));
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final DefaultFeatureConfig config) {
        final ChunkPos chunkPos6 = new ChunkPos(pos);
        if (a(chunkPos6.x, chunkPos6.z, VoidStartPlatformFeature.aS.x, VoidStartPlatformFeature.aS.z) > 1) {
            return true;
        }
        final BlockPos.Mutable mutable7 = new BlockPos.Mutable();
        for (int integer8 = chunkPos6.getStartZ(); integer8 <= chunkPos6.getEndZ(); ++integer8) {
            for (int integer9 = chunkPos6.getStartX(); integer9 <= chunkPos6.getEndX(); ++integer9) {
                if (a(VoidStartPlatformFeature.a.getX(), VoidStartPlatformFeature.a.getZ(), integer9, integer8) <= 16) {
                    mutable7.set(integer9, VoidStartPlatformFeature.a.getY(), integer8);
                    if (mutable7.equals(VoidStartPlatformFeature.a)) {
                        world.setBlockState(mutable7, Blocks.m.getDefaultState(), 2);
                    }
                    else {
                        world.setBlockState(mutable7, Blocks.b.getDefaultState(), 2);
                    }
                }
            }
        }
        return true;
    }
    
    static {
        a = new BlockPos(8, 3, 8);
        aS = new ChunkPos(VoidStartPlatformFeature.a);
    }
}
