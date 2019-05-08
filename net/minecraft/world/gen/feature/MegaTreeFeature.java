package net.minecraft.world.gen.feature;

import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TestableWorld;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;

public abstract class MegaTreeFeature<T extends FeatureConfig> extends AbstractTreeFeature<T>
{
    protected final int baseHeight;
    protected final BlockState log;
    protected final BlockState leaves;
    protected final int maxExtraHeight;
    
    public MegaTreeFeature(final Function<Dynamic<?>, ? extends T> function, final boolean emitNeighborBlockUpdates, final int baseHeight, final int maxExtraHeight, final BlockState log, final BlockState leaves) {
        super(function, emitNeighborBlockUpdates);
        this.baseHeight = baseHeight;
        this.maxExtraHeight = maxExtraHeight;
        this.log = log;
        this.leaves = leaves;
    }
    
    protected int getHeight(final Random random) {
        int integer2 = random.nextInt(3) + this.baseHeight;
        if (this.maxExtraHeight > 1) {
            integer2 += random.nextInt(this.maxExtraHeight);
        }
        return integer2;
    }
    
    private boolean doesTreeFit(final TestableWorld world, final BlockPos pos, final int height) {
        boolean boolean4 = true;
        if (pos.getY() < 1 || pos.getY() + height + 1 > 256) {
            return false;
        }
        for (int integer5 = 0; integer5 <= 1 + height; ++integer5) {
            int integer6 = 2;
            if (integer5 == 0) {
                integer6 = 1;
            }
            else if (integer5 >= 1 + height - 2) {
                integer6 = 2;
            }
            for (int integer7 = -integer6; integer7 <= integer6 && boolean4; ++integer7) {
                for (int integer8 = -integer6; integer8 <= integer6 && boolean4; ++integer8) {
                    if (pos.getY() + integer5 < 0 || pos.getY() + integer5 >= 256 || !AbstractTreeFeature.canTreeReplace(world, pos.add(integer7, integer5, integer8))) {
                        boolean4 = false;
                    }
                }
            }
        }
        return boolean4;
    }
    
    private boolean replaceGround(final ModifiableTestableWorld world, final BlockPos pos) {
        final BlockPos blockPos3 = pos.down();
        if (!AbstractTreeFeature.isNaturalDirtOrGrass(world, blockPos3) || pos.getY() < 2) {
            return false;
        }
        this.setToDirt(world, blockPos3);
        this.setToDirt(world, blockPos3.east());
        this.setToDirt(world, blockPos3.south());
        this.setToDirt(world, blockPos3.south().east());
        return true;
    }
    
    protected boolean checkTreeFitsAndReplaceGround(final ModifiableTestableWorld world, final BlockPos pos, final int height) {
        return this.doesTreeFit(world, pos, height) && this.replaceGround(world, pos);
    }
    
    protected void makeSquaredLeafLayer(final ModifiableTestableWorld world, final BlockPos pos, final int radius) {
        final int integer4 = radius * radius;
        for (int integer5 = -radius; integer5 <= radius + 1; ++integer5) {
            for (int integer6 = -radius; integer6 <= radius + 1; ++integer6) {
                final int integer7 = Math.min(Math.abs(integer5), Math.abs(integer5 - 1));
                final int integer8 = Math.min(Math.abs(integer6), Math.abs(integer6 - 1));
                if (integer7 + integer8 < 7) {
                    if (integer7 * integer7 + integer8 * integer8 <= integer4) {
                        final BlockPos blockPos9 = pos.add(integer5, 0, integer6);
                        if (AbstractTreeFeature.isAirOrLeaves(world, blockPos9)) {
                            this.setBlockState(world, blockPos9, this.leaves);
                        }
                    }
                }
            }
        }
    }
    
    protected void makeRoundLeafLayer(final ModifiableTestableWorld world, final BlockPos pos, final int radius) {
        final int integer4 = radius * radius;
        for (int integer5 = -radius; integer5 <= radius; ++integer5) {
            for (int integer6 = -radius; integer6 <= radius; ++integer6) {
                if (integer5 * integer5 + integer6 * integer6 <= integer4) {
                    final BlockPos blockPos7 = pos.add(integer5, 0, integer6);
                    if (AbstractTreeFeature.isAirOrLeaves(world, blockPos7)) {
                        this.setBlockState(world, blockPos7, this.leaves);
                    }
                }
            }
        }
    }
}
