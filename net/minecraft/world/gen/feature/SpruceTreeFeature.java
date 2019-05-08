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

public class SpruceTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig>
{
    private static final BlockState LOG;
    private static final BlockState LEAVES;
    
    public SpruceTreeFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer, final boolean emitNeighborBlockUpdates) {
        super(configDeserializer, emitNeighborBlockUpdates);
    }
    
    public boolean generate(final Set<BlockPos> logPositions, final ModifiableTestableWorld world, final Random random, final BlockPos pos) {
        final int integer5 = random.nextInt(4) + 6;
        final int integer6 = 1 + random.nextInt(2);
        final int integer7 = integer5 - integer6;
        final int integer8 = 2 + random.nextInt(2);
        boolean boolean9 = true;
        if (pos.getY() < 1 || pos.getY() + integer5 + 1 > 256) {
            return false;
        }
        for (int integer9 = pos.getY(); integer9 <= pos.getY() + 1 + integer5 && boolean9; ++integer9) {
            int integer10;
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
                        mutable12.set(integer11, integer9, integer12);
                        if (!AbstractTreeFeature.isAirOrLeaves(world, mutable12)) {
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
        if (!AbstractTreeFeature.isDirtOrGrass(world, pos.down()) || pos.getY() >= 256 - integer5 - 1) {
            return false;
        }
        this.setToDirt(world, pos.down());
        int integer9 = random.nextInt(2);
        int integer10 = 1;
        int integer13 = 0;
        for (int integer11 = 0; integer11 <= integer7; ++integer11) {
            final int integer12 = pos.getY() + integer5 - integer11;
            for (int integer14 = pos.getX() - integer9; integer14 <= pos.getX() + integer9; ++integer14) {
                final int integer15 = integer14 - pos.getX();
                for (int integer16 = pos.getZ() - integer9; integer16 <= pos.getZ() + integer9; ++integer16) {
                    final int integer17 = integer16 - pos.getZ();
                    if (Math.abs(integer15) != integer9 || Math.abs(integer17) != integer9 || integer9 <= 0) {
                        final BlockPos blockPos19 = new BlockPos(integer14, integer12, integer16);
                        if (AbstractTreeFeature.isAirOrLeaves(world, blockPos19) || AbstractTreeFeature.isReplaceablePlant(world, blockPos19)) {
                            this.setBlockState(world, blockPos19, SpruceTreeFeature.LEAVES);
                        }
                    }
                }
            }
            if (integer9 >= integer10) {
                integer9 = integer13;
                integer13 = 1;
                if (++integer10 > integer8) {
                    integer10 = integer8;
                }
            }
            else {
                ++integer9;
            }
        }
        int integer11;
        for (integer11 = random.nextInt(3), int integer12 = 0; integer12 < integer5 - integer11; ++integer12) {
            if (AbstractTreeFeature.isAirOrLeaves(world, pos.up(integer12))) {
                this.setBlockState(logPositions, world, pos.up(integer12), SpruceTreeFeature.LOG);
            }
        }
        return true;
    }
    
    static {
        LOG = Blocks.J.getDefaultState();
        LEAVES = Blocks.ah.getDefaultState();
    }
}
