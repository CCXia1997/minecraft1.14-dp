package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import javax.annotation.Nullable;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.world.LightType;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ViewableWorld;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.IntegerProperty;

public class SnowBlock extends Block
{
    public static final IntegerProperty LAYERS;
    protected static final VoxelShape[] LAYERS_TO_SHAPE;
    
    protected SnowBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)SnowBlock.LAYERS, 1));
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        switch (env) {
            case a: {
                return world.<Integer>get((Property<Integer>)SnowBlock.LAYERS) < 5;
            }
            case b: {
                return false;
            }
            case c: {
                return false;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return SnowBlock.LAYERS_TO_SHAPE[state.<Integer>get((Property<Integer>)SnowBlock.LAYERS)];
    }
    
    @Override
    public VoxelShape getCollisionShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition ePos) {
        return SnowBlock.LAYERS_TO_SHAPE[state.<Integer>get((Property<Integer>)SnowBlock.LAYERS) - 1];
    }
    
    @Override
    public boolean n(final BlockState state) {
        return true;
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        final BlockState blockState4 = world.getBlockState(pos.down());
        final Block block5 = blockState4.getBlock();
        return block5 != Blocks.cB && block5 != Blocks.gL && block5 != Blocks.gg && (Block.isFaceFullSquare(blockState4.getCollisionShape(world, pos.down()), Direction.UP) || (block5 == this && blockState4.<Integer>get((Property<Integer>)SnowBlock.LAYERS) == 8));
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (world.getLightLevel(LightType.BLOCK, pos) > 11) {
            Block.dropStacks(state, world, pos);
            world.clearBlockState(pos, false);
        }
    }
    
    @Override
    public boolean canReplace(final BlockState state, final ItemPlacementContext ctx) {
        final int integer3 = state.<Integer>get((Property<Integer>)SnowBlock.LAYERS);
        if (ctx.getItemStack().getItem() == this.getItem() && integer3 < 8) {
            return !ctx.c() || ctx.getFacing() == Direction.UP;
        }
        return integer3 == 1;
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final BlockState blockState2 = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (blockState2.getBlock() == this) {
            final int integer3 = blockState2.<Integer>get((Property<Integer>)SnowBlock.LAYERS);
            return ((AbstractPropertyContainer<O, BlockState>)blockState2).<Comparable, Integer>with((Property<Comparable>)SnowBlock.LAYERS, Math.min(8, integer3 + 1));
        }
        return super.getPlacementState(ctx);
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(SnowBlock.LAYERS);
    }
    
    static {
        LAYERS = Properties.LAYERS;
        LAYERS_TO_SHAPE = new VoxelShape[] { VoxelShapes.empty(), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0) };
    }
}
