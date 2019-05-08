package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.Entity;
import java.util.Iterator;
import net.minecraft.tag.FluidTags;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.IntegerProperty;

public class CactusBlock extends Block
{
    public static final IntegerProperty AGE;
    protected static final VoxelShape COLLISION_SHAPE;
    protected static final VoxelShape OUTLINE_SHAPE;
    
    protected CactusBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)CactusBlock.AGE, 0));
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
            return;
        }
        final BlockPos blockPos5 = pos.up();
        if (!world.isAir(blockPos5)) {
            return;
        }
        int integer6;
        for (integer6 = 1; world.getBlockState(pos.down(integer6)).getBlock() == this; ++integer6) {}
        if (integer6 >= 3) {
            return;
        }
        final int integer7 = state.<Integer>get((Property<Integer>)CactusBlock.AGE);
        if (integer7 == 15) {
            world.setBlockState(blockPos5, this.getDefaultState());
            final BlockState blockState8 = ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)CactusBlock.AGE, 0);
            world.setBlockState(pos, blockState8, 4);
            blockState8.neighborUpdate(world, blockPos5, this, pos, false);
        }
        else {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)CactusBlock.AGE, integer7 + 1), 4);
        }
    }
    
    @Override
    public VoxelShape getCollisionShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition ePos) {
        return CactusBlock.COLLISION_SHAPE;
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return CactusBlock.OUTLINE_SHAPE;
    }
    
    @Override
    public boolean isFullBoundsCubeForCulling(final BlockState state) {
        return true;
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            world.getBlockTickScheduler().schedule(pos, this, 1);
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        for (final Direction direction5 : Direction.Type.HORIZONTAL) {
            final BlockState blockState6 = world.getBlockState(pos.offset(direction5));
            final Material material7 = blockState6.getMaterial();
            if (material7.isSolid() || world.getFluidState(pos.offset(direction5)).matches(FluidTags.b)) {
                return false;
            }
        }
        final Block block4 = world.getBlockState(pos.down()).getBlock();
        return (block4 == Blocks.cD || block4 == Blocks.C || block4 == Blocks.D) && !world.getBlockState(pos.up()).getMaterial().isLiquid();
    }
    
    @Override
    public void onEntityCollision(final BlockState state, final World world, final BlockPos pos, final Entity entity) {
        entity.damage(DamageSource.CACTUS, 1.0f);
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(CactusBlock.AGE);
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    static {
        AGE = Properties.AGE_15;
        COLLISION_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);
        OUTLINE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
    }
}
