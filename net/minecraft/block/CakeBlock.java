package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.stat.Stats;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.IntegerProperty;

public class CakeBlock extends Block
{
    public static final IntegerProperty BITES;
    protected static final VoxelShape[] BITES_TO_SHAPE;
    
    protected CakeBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)CakeBlock.BITES, 0));
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return CakeBlock.BITES_TO_SHAPE[state.<Integer>get((Property<Integer>)CakeBlock.BITES)];
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        if (world.isClient) {
            final ItemStack itemStack7 = player.getStackInHand(hand);
            return this.tryEat(world, pos, state, player) || itemStack7.isEmpty();
        }
        return this.tryEat(world, pos, state, player);
    }
    
    private boolean tryEat(final IWorld world, final BlockPos pos, final BlockState state, final PlayerEntity player) {
        if (!player.canConsume(false)) {
            return false;
        }
        player.incrementStat(Stats.S);
        player.getHungerManager().add(2, 0.1f);
        final int integer5 = state.<Integer>get((Property<Integer>)CakeBlock.BITES);
        if (integer5 < 6) {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)CakeBlock.BITES, integer5 + 1), 3);
        }
        else {
            world.clearBlockState(pos, false);
        }
        return true;
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (facing == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        return world.getBlockState(pos.down()).getMaterial().isSolid();
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(CakeBlock.BITES);
    }
    
    @Override
    public int getComparatorOutput(final BlockState state, final World world, final BlockPos pos) {
        return (7 - state.<Integer>get((Property<Integer>)CakeBlock.BITES)) * 2;
    }
    
    @Override
    public boolean hasComparatorOutput(final BlockState state) {
        return true;
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    static {
        BITES = Properties.BITES;
        BITES_TO_SHAPE = new VoxelShape[] { Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 8.0, 15.0), Block.createCuboidShape(3.0, 0.0, 1.0, 15.0, 8.0, 15.0), Block.createCuboidShape(5.0, 0.0, 1.0, 15.0, 8.0, 15.0), Block.createCuboidShape(7.0, 0.0, 1.0, 15.0, 8.0, 15.0), Block.createCuboidShape(9.0, 0.0, 1.0, 15.0, 8.0, 15.0), Block.createCuboidShape(11.0, 0.0, 1.0, 15.0, 8.0, 15.0), Block.createCuboidShape(13.0, 0.0, 1.0, 15.0, 8.0, 15.0) };
    }
}
