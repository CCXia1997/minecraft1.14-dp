package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.state.property.Property;
import net.minecraft.state.property.IntegerProperty;

public class FrostedIceBlock extends IceBlock
{
    public static final IntegerProperty AGE;
    
    public FrostedIceBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)FrostedIceBlock.AGE, 0));
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if ((random.nextInt(3) == 0 || this.canMelt(world, pos, 4)) && world.getLightLevel(pos) > 11 - state.<Integer>get((Property<Integer>)FrostedIceBlock.AGE) - state.getLightSubtracted(world, pos) && this.increaseAge(state, world, pos)) {
            try (final BlockPos.PooledMutable pooledMutable5 = BlockPos.PooledMutable.get()) {
                for (final Direction direction10 : Direction.values()) {
                    pooledMutable5.set((Vec3i)pos).setOffset(direction10);
                    final BlockState blockState11 = world.getBlockState(pooledMutable5);
                    if (blockState11.getBlock() == this && !this.increaseAge(blockState11, world, pooledMutable5)) {
                        world.getBlockTickScheduler().schedule(pooledMutable5, this, MathHelper.nextInt(random, 20, 40));
                    }
                }
            }
            return;
        }
        world.getBlockTickScheduler().schedule(pos, this, MathHelper.nextInt(random, 20, 40));
    }
    
    private boolean increaseAge(final BlockState state, final World world, final BlockPos pos) {
        final int integer4 = state.<Integer>get((Property<Integer>)FrostedIceBlock.AGE);
        if (integer4 < 3) {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)FrostedIceBlock.AGE, integer4 + 1), 2);
            return false;
        }
        this.melt(state, world, pos);
        return true;
    }
    
    @Override
    public void neighborUpdate(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighborPos, final boolean boolean6) {
        if (block == this && this.canMelt(world, pos, 2)) {
            this.melt(state, world, pos);
        }
        super.neighborUpdate(state, world, pos, block, neighborPos, boolean6);
    }
    
    private boolean canMelt(final BlockView world, final BlockPos pos, final int maxNeighbors) {
        int integer4 = 0;
        try (final BlockPos.PooledMutable pooledMutable5 = BlockPos.PooledMutable.get()) {
            for (final Direction direction10 : Direction.values()) {
                pooledMutable5.set((Vec3i)pos).setOffset(direction10);
                if (world.getBlockState(pooledMutable5).getBlock() == this && ++integer4 >= maxNeighbors) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(FrostedIceBlock.AGE);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack getPickStack(final BlockView world, final BlockPos pos, final BlockState state) {
        return ItemStack.EMPTY;
    }
    
    static {
        AGE = Properties.AGE_3;
    }
}
