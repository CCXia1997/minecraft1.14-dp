package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.StateFactory;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.state.property.Property;
import net.minecraft.state.property.BooleanProperty;

public class RedstoneLampBlock extends Block
{
    public static final BooleanProperty LIT;
    
    public RedstoneLampBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)RedstoneLampBlock.LIT, false));
    }
    
    @Override
    public int getLuminance(final BlockState state) {
        return state.<Boolean>get((Property<Boolean>)RedstoneLampBlock.LIT) ? super.getLuminance(state) : 0;
    }
    
    @Override
    public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean boolean5) {
        super.onBlockAdded(state, world, pos, oldState, boolean5);
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)RedstoneLampBlock.LIT, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
    }
    
    @Override
    public void neighborUpdate(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighborPos, final boolean boolean6) {
        if (world.isClient) {
            return;
        }
        final boolean boolean7 = state.<Boolean>get((Property<Boolean>)RedstoneLampBlock.LIT);
        if (boolean7 != world.isReceivingRedstonePower(pos)) {
            if (boolean7) {
                world.getBlockTickScheduler().schedule(pos, this, 4);
            }
            else {
                world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable>cycle((Property<Comparable>)RedstoneLampBlock.LIT), 2);
            }
        }
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (world.isClient) {
            return;
        }
        if (state.<Boolean>get((Property<Boolean>)RedstoneLampBlock.LIT) && !world.isReceivingRedstonePower(pos)) {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable>cycle((Property<Comparable>)RedstoneLampBlock.LIT), 2);
        }
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(RedstoneLampBlock.LIT);
    }
    
    static {
        LIT = RedstoneTorchBlock.LIT;
    }
}
