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

public class PineTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig>
{
    private static final BlockState LOG;
    private static final BlockState LEAVES;
    
    public PineTreeFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer, false);
    }
    
    public boolean generate(final Set<BlockPos> logPositions, final ModifiableTestableWorld world, final Random random, final BlockPos pos) {
        final int integer5 = random.nextInt(5) + 7;
        final int integer6 = integer5 - random.nextInt(2) - 3;
        final int integer7 = integer5 - integer6;
        final int integer8 = 1 + random.nextInt(integer7 + 1);
        if (pos.getY() < 1 || pos.getY() + integer5 + 1 > 256) {
            return false;
        }
        boolean boolean9 = true;
        for (int integer9 = pos.getY(); integer9 <= pos.getY() + 1 + integer5 && boolean9; ++integer9) {
            int integer10 = 1;
            if (integer9 - pos.getY() < integer6) {
                integer10 = 0;
            }
            else {
                integer10 = integer8;
            }
            final BlockPos.Mutable mutable12 = new BlockPos.Mutable();
            for (int integer11 = pos.getX() - integer10; integer11 <= pos.getX() + integer10 && boolean9; ++integer11) {
                for (int integer12 = pos.getZ() - integer10; integer12 <= pos.getZ() + integer10 && boolean9; ++integer12) {
                    if (integer9 >= 0 && integer9 < 256) {
                        if (!AbstractTreeFeature.canTreeReplace(world, mutable12.set(integer11, integer9, integer12))) {
                            boolean9 = false;
                        }
                    }
                    else {
                        boolean9 = false;
                    }
                }
            }
        }
        if (!boolean9) {
            return false;
        }
        if (!AbstractTreeFeature.isNaturalDirtOrGrass(world, pos.down()) || pos.getY() >= 256 - integer5 - 1) {
            return false;
        }
        this.setToDirt(world, pos.down());
        int integer9 = 0;
        for (int integer10 = pos.getY() + integer5; integer10 >= pos.getY() + integer6; --integer10) {
            for (int integer13 = pos.getX() - integer9; integer13 <= pos.getX() + integer9; ++integer13) {
                final int integer11 = integer13 - pos.getX();
                for (int integer12 = pos.getZ() - integer9; integer12 <= pos.getZ() + integer9; ++integer12) {
                    final int integer14 = integer12 - pos.getZ();
                    if (Math.abs(integer11) != integer9 || Math.abs(integer14) != integer9 || integer9 <= 0) {
                        final BlockPos blockPos16 = new BlockPos(integer13, integer10, integer12);
                        if (AbstractTreeFeature.isAirOrLeaves(world, blockPos16)) {
                            this.setBlockState(world, blockPos16, PineTreeFeature.LEAVES);
                        }
                    }
                }
            }
            if (integer9 >= 1 && integer10 == pos.getY() + integer6 + 1) {
                --integer9;
            }
            else if (integer9 < integer8) {
                ++integer9;
            }
        }
        for (int integer10 = 0; integer10 < integer5 - 1; ++integer10) {
            if (AbstractTreeFeature.isAirOrLeaves(world, pos.up(integer10))) {
                this.setBlockState(logPositions, world, pos.up(integer10), PineTreeFeature.LOG);
            }
        }
        return true;
    }
    
    static {
        LOG = Blocks.J.getDefaultState();
        LEAVES = Blocks.ah.getDefaultState();
    }
}
