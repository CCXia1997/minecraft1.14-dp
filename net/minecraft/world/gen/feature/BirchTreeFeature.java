package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.TestableWorld;
import java.util.Random;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.util.math.BlockPos;
import java.util.Set;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;

public class BirchTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig>
{
    private static final BlockState LOG;
    private static final BlockState LEAVES;
    private final boolean alwaysTall;
    
    public BirchTreeFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory, final boolean emitNeighborBlockUpdates, final boolean alwaysTall) {
        super(configFactory, emitNeighborBlockUpdates);
        this.alwaysTall = alwaysTall;
    }
    
    public boolean generate(final Set<BlockPos> logPositions, final ModifiableTestableWorld world, final Random random, final BlockPos pos) {
        int integer5 = random.nextInt(3) + 5;
        if (this.alwaysTall) {
            integer5 += random.nextInt(7);
        }
        boolean boolean6 = true;
        if (pos.getY() < 1 || pos.getY() + integer5 + 1 > 256) {
            return false;
        }
        for (int integer6 = pos.getY(); integer6 <= pos.getY() + 1 + integer5; ++integer6) {
            int integer7 = 1;
            if (integer6 == pos.getY()) {
                integer7 = 0;
            }
            if (integer6 >= pos.getY() + 1 + integer5 - 2) {
                integer7 = 2;
            }
            final BlockPos.Mutable mutable9 = new BlockPos.Mutable();
            for (int integer8 = pos.getX() - integer7; integer8 <= pos.getX() + integer7 && boolean6; ++integer8) {
                for (int integer9 = pos.getZ() - integer7; integer9 <= pos.getZ() + integer7 && boolean6; ++integer9) {
                    if (integer6 >= 0 && integer6 < 256) {
                        if (!AbstractTreeFeature.canTreeReplace(world, mutable9.set(integer8, integer6, integer9))) {
                            boolean6 = false;
                        }
                    }
                    else {
                        boolean6 = false;
                    }
                }
            }
        }
        if (!boolean6) {
            return false;
        }
        if (!AbstractTreeFeature.isDirtOrGrass(world, pos.down()) || pos.getY() >= 256 - integer5 - 1) {
            return false;
        }
        this.setToDirt(world, pos.down());
        for (int integer6 = pos.getY() - 3 + integer5; integer6 <= pos.getY() + integer5; ++integer6) {
            final int integer7 = integer6 - (pos.getY() + integer5);
            for (int integer10 = 1 - integer7 / 2, integer8 = pos.getX() - integer10; integer8 <= pos.getX() + integer10; ++integer8) {
                final int integer9 = integer8 - pos.getX();
                for (int integer11 = pos.getZ() - integer10; integer11 <= pos.getZ() + integer10; ++integer11) {
                    final int integer12 = integer11 - pos.getZ();
                    if (Math.abs(integer9) == integer10 && Math.abs(integer12) == integer10) {
                        if (random.nextInt(2) == 0) {
                            continue;
                        }
                        if (integer7 == 0) {
                            continue;
                        }
                    }
                    final BlockPos blockPos14 = new BlockPos(integer8, integer6, integer11);
                    if (AbstractTreeFeature.isAirOrLeaves(world, blockPos14)) {
                        this.setBlockState(world, blockPos14, BirchTreeFeature.LEAVES);
                    }
                }
            }
        }
        for (int integer6 = 0; integer6 < integer5; ++integer6) {
            if (AbstractTreeFeature.isAirOrLeaves(world, pos.up(integer6))) {
                this.setBlockState(logPositions, world, pos.up(integer6), BirchTreeFeature.LOG);
            }
        }
        return true;
    }
    
    static {
        LOG = Blocks.K.getDefaultState();
        LEAVES = Blocks.ai.getDefaultState();
    }
}
