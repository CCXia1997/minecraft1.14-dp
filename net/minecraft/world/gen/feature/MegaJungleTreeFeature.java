package net.minecraft.world.gen.feature;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Property;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.block.VineBlock;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.util.math.MathHelper;
import java.util.Random;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.util.math.BlockPos;
import java.util.Set;
import net.minecraft.block.BlockState;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class MegaJungleTreeFeature extends MegaTreeFeature<DefaultFeatureConfig>
{
    public MegaJungleTreeFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, final boolean emitNeighborBlockUpdates, final int baseHeight, final int maxExtraHeight, final BlockState log, final BlockState leaves) {
        super(function, emitNeighborBlockUpdates, baseHeight, maxExtraHeight, log, leaves);
    }
    
    public boolean generate(final Set<BlockPos> logPositions, final ModifiableTestableWorld world, final Random random, final BlockPos pos) {
        final int integer5 = this.getHeight(random);
        if (!this.checkTreeFitsAndReplaceGround(world, pos, integer5)) {
            return false;
        }
        this.makeLeaves(world, pos.up(integer5), 2);
        for (int integer6 = pos.getY() + integer5 - 2 - random.nextInt(4); integer6 > pos.getY() + integer5 / 2; integer6 -= 2 + random.nextInt(4)) {
            final float float7 = random.nextFloat() * 6.2831855f;
            int integer7 = pos.getX() + (int)(0.5f + MathHelper.cos(float7) * 4.0f);
            int integer8 = pos.getZ() + (int)(0.5f + MathHelper.sin(float7) * 4.0f);
            for (int integer9 = 0; integer9 < 5; ++integer9) {
                integer7 = pos.getX() + (int)(1.5f + MathHelper.cos(float7) * integer9);
                integer8 = pos.getZ() + (int)(1.5f + MathHelper.sin(float7) * integer9);
                this.setBlockState(logPositions, world, new BlockPos(integer7, integer6 - 3 + integer9 / 2, integer8), this.log);
            }
            int integer9 = 1 + random.nextInt(2);
            for (int integer10 = integer6, integer11 = integer10 - integer9; integer11 <= integer10; ++integer11) {
                final int integer12 = integer11 - integer10;
                this.makeRoundLeafLayer(world, new BlockPos(integer7, integer11, integer8), 1 - integer12);
            }
        }
        for (int integer13 = 0; integer13 < integer5; ++integer13) {
            final BlockPos blockPos8 = pos.up(integer13);
            if (AbstractTreeFeature.canTreeReplace(world, blockPos8)) {
                this.setBlockState(logPositions, world, blockPos8, this.log);
                if (integer13 > 0) {
                    this.tryMakingVine(world, random, blockPos8.west(), VineBlock.EAST);
                    this.tryMakingVine(world, random, blockPos8.north(), VineBlock.SOUTH);
                }
            }
            if (integer13 < integer5 - 1) {
                final BlockPos blockPos9 = blockPos8.east();
                if (AbstractTreeFeature.canTreeReplace(world, blockPos9)) {
                    this.setBlockState(logPositions, world, blockPos9, this.log);
                    if (integer13 > 0) {
                        this.tryMakingVine(world, random, blockPos9.east(), VineBlock.WEST);
                        this.tryMakingVine(world, random, blockPos9.north(), VineBlock.SOUTH);
                    }
                }
                final BlockPos blockPos10 = blockPos8.south().east();
                if (AbstractTreeFeature.canTreeReplace(world, blockPos10)) {
                    this.setBlockState(logPositions, world, blockPos10, this.log);
                    if (integer13 > 0) {
                        this.tryMakingVine(world, random, blockPos10.east(), VineBlock.WEST);
                        this.tryMakingVine(world, random, blockPos10.south(), VineBlock.NORTH);
                    }
                }
                final BlockPos blockPos11 = blockPos8.south();
                if (AbstractTreeFeature.canTreeReplace(world, blockPos11)) {
                    this.setBlockState(logPositions, world, blockPos11, this.log);
                    if (integer13 > 0) {
                        this.tryMakingVine(world, random, blockPos11.west(), VineBlock.EAST);
                        this.tryMakingVine(world, random, blockPos11.south(), VineBlock.NORTH);
                    }
                }
            }
        }
        return true;
    }
    
    private void tryMakingVine(final ModifiableTestableWorld world, final Random random, final BlockPos pos, final BooleanProperty directionProperty) {
        if (random.nextInt(3) > 0 && AbstractTreeFeature.isAir(world, pos)) {
            this.setBlockState(world, pos, ((AbstractPropertyContainer<O, BlockState>)Blocks.dH.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)directionProperty, true));
        }
    }
    
    private void makeLeaves(final ModifiableTestableWorld world, final BlockPos pos, final int integer) {
        final int integer2 = 2;
        for (int integer3 = -2; integer3 <= 0; ++integer3) {
            this.makeSquaredLeafLayer(world, pos.up(integer3), integer + 1 - integer3);
        }
    }
}
