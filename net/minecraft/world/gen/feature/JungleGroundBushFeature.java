package net.minecraft.world.gen.feature;

import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.Heightmap;
import java.util.Random;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.util.math.BlockPos;
import java.util.Set;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;

public class JungleGroundBushFeature extends AbstractTreeFeature<DefaultFeatureConfig>
{
    private final BlockState leaves;
    private final BlockState log;
    
    public JungleGroundBushFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory, final BlockState log, final BlockState leaves) {
        super(configFactory, false);
        this.log = log;
        this.leaves = leaves;
    }
    
    public boolean generate(final Set<BlockPos> logPositions, final ModifiableTestableWorld world, final Random random, BlockPos pos) {
        pos = world.getTopPosition(Heightmap.Type.f, pos).down();
        if (AbstractTreeFeature.isNaturalDirtOrGrass(world, pos)) {
            pos = pos.up();
            this.setBlockState(logPositions, world, pos, this.log);
            for (int integer5 = pos.getY(); integer5 <= pos.getY() + 2; ++integer5) {
                final int integer6 = integer5 - pos.getY();
                for (int integer7 = 2 - integer6, integer8 = pos.getX() - integer7; integer8 <= pos.getX() + integer7; ++integer8) {
                    final int integer9 = integer8 - pos.getX();
                    for (int integer10 = pos.getZ() - integer7; integer10 <= pos.getZ() + integer7; ++integer10) {
                        final int integer11 = integer10 - pos.getZ();
                        if (Math.abs(integer9) != integer7 || Math.abs(integer11) != integer7 || random.nextInt(2) != 0) {
                            final BlockPos blockPos12 = new BlockPos(integer8, integer5, integer10);
                            if (AbstractTreeFeature.isAirOrLeaves(world, blockPos12)) {
                                this.setBlockState(world, blockPos12, this.leaves);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
