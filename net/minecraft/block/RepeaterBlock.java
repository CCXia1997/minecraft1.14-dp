package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DustParticleParameters;
import java.util.Random;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.BooleanProperty;

public class RepeaterBlock extends AbstractRedstoneGateBlock
{
    public static final BooleanProperty LOCKED;
    public static final IntegerProperty DELAY;
    
    protected RepeaterBlock(final Settings settings) {
        super(settings);
        this.setDefaultState((((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)RepeaterBlock.FACING, Direction.NORTH)).with((Property<Comparable>)RepeaterBlock.DELAY, 1)).with((Property<Comparable>)RepeaterBlock.LOCKED, false)).<Comparable, Boolean>with((Property<Comparable>)RepeaterBlock.POWERED, false));
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        if (!player.abilities.allowModifyWorld) {
            return false;
        }
        world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable>cycle((Property<Comparable>)RepeaterBlock.DELAY), 3);
        return true;
    }
    
    @Override
    protected int getUpdateDelayInternal(final BlockState blockState) {
        return blockState.<Integer>get((Property<Integer>)RepeaterBlock.DELAY) * 2;
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final BlockState blockState2 = super.getPlacementState(ctx);
        return ((AbstractPropertyContainer<O, BlockState>)blockState2).<Comparable, Boolean>with((Property<Comparable>)RepeaterBlock.LOCKED, this.isLocked(ctx.getWorld(), ctx.getBlockPos(), blockState2));
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (!world.isClient() && facing.getAxis() != state.<Direction>get((Property<Direction>)RepeaterBlock.FACING).getAxis()) {
            return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)RepeaterBlock.LOCKED, this.isLocked(world, pos, state));
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public boolean isLocked(final ViewableWorld world, final BlockPos pos, final BlockState state) {
        return this.getMaxInputLevelSides(world, pos, state) > 0;
    }
    
    @Override
    protected boolean isValidInput(final BlockState blockState) {
        return AbstractRedstoneGateBlock.isRedstoneGate(blockState);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(final BlockState state, final World world, final BlockPos pos, final Random rnd) {
        if (!state.<Boolean>get((Property<Boolean>)RepeaterBlock.POWERED)) {
            return;
        }
        final Direction direction5 = state.<Direction>get((Property<Direction>)RepeaterBlock.FACING);
        final double double6 = pos.getX() + 0.5f + (rnd.nextFloat() - 0.5f) * 0.2;
        final double double7 = pos.getY() + 0.4f + (rnd.nextFloat() - 0.5f) * 0.2;
        final double double8 = pos.getZ() + 0.5f + (rnd.nextFloat() - 0.5f) * 0.2;
        float float12 = -5.0f;
        if (rnd.nextBoolean()) {
            float12 = (float)(state.<Integer>get((Property<Integer>)RepeaterBlock.DELAY) * 2 - 1);
        }
        float12 /= 16.0f;
        final double double9 = float12 * direction5.getOffsetX();
        final double double10 = float12 * direction5.getOffsetZ();
        world.addParticle(DustParticleParameters.RED, double6 + double9, double7, double8 + double10, 0.0, 0.0, 0.0);
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(RepeaterBlock.FACING, RepeaterBlock.DELAY, RepeaterBlock.LOCKED, RepeaterBlock.POWERED);
    }
    
    static {
        LOCKED = Properties.LOCKED;
        DELAY = Properties.DELAY;
    }
}
