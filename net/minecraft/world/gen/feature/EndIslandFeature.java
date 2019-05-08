package net.minecraft.world.gen.feature;

import net.minecraft.world.ModifiableWorld;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class EndIslandFeature extends Feature<DefaultFeatureConfig>
{
    public EndIslandFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final DefaultFeatureConfig config) {
        float float6 = (float)(random.nextInt(3) + 4);
        for (int integer7 = 0; float6 > 0.5f; float6 -= (float)(random.nextInt(2) + 0.5), --integer7) {
            for (int integer8 = MathHelper.floor(-float6); integer8 <= MathHelper.ceil(float6); ++integer8) {
                for (int integer9 = MathHelper.floor(-float6); integer9 <= MathHelper.ceil(float6); ++integer9) {
                    if (integer8 * integer8 + integer9 * integer9 <= (float6 + 1.0f) * (float6 + 1.0f)) {
                        this.setBlockState(world, pos.add(integer8, integer7, integer9), Blocks.dW.getDefaultState());
                    }
                }
            }
        }
        return true;
    }
}
