package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.IWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.block.enums.PistonType;
import net.minecraft.state.property.EnumProperty;

public class PistonHeadBlock extends FacingBlock
{
    public static final EnumProperty<PistonType> TYPE;
    public static final BooleanProperty SHORT;
    protected static final VoxelShape d;
    protected static final VoxelShape e;
    protected static final VoxelShape f;
    protected static final VoxelShape g;
    protected static final VoxelShape h;
    protected static final VoxelShape i;
    protected static final VoxelShape j;
    protected static final VoxelShape k;
    protected static final VoxelShape w;
    protected static final VoxelShape x;
    protected static final VoxelShape y;
    protected static final VoxelShape z;
    protected static final VoxelShape A;
    protected static final VoxelShape B;
    protected static final VoxelShape C;
    protected static final VoxelShape D;
    protected static final VoxelShape E;
    protected static final VoxelShape F;
    
    public PistonHeadBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)PistonHeadBlock.FACING, Direction.NORTH)).with(PistonHeadBlock.TYPE, PistonType.a)).<Comparable, Boolean>with((Property<Comparable>)PistonHeadBlock.SHORT, false));
    }
    
    private VoxelShape j(final BlockState blockState) {
        switch (blockState.<Direction>get((Property<Direction>)PistonHeadBlock.FACING)) {
            default: {
                return PistonHeadBlock.i;
            }
            case UP: {
                return PistonHeadBlock.h;
            }
            case NORTH: {
                return PistonHeadBlock.g;
            }
            case SOUTH: {
                return PistonHeadBlock.f;
            }
            case WEST: {
                return PistonHeadBlock.e;
            }
            case EAST: {
                return PistonHeadBlock.d;
            }
        }
    }
    
    @Override
    public boolean n(final BlockState state) {
        return true;
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return VoxelShapes.union(this.j(state), this.q(state));
    }
    
    private VoxelShape q(final BlockState blockState) {
        final boolean boolean2 = blockState.<Boolean>get((Property<Boolean>)PistonHeadBlock.SHORT);
        switch (blockState.<Direction>get((Property<Direction>)PistonHeadBlock.FACING)) {
            default: {
                return boolean2 ? PistonHeadBlock.B : PistonHeadBlock.k;
            }
            case UP: {
                return boolean2 ? PistonHeadBlock.A : PistonHeadBlock.j;
            }
            case NORTH: {
                return boolean2 ? PistonHeadBlock.D : PistonHeadBlock.x;
            }
            case SOUTH: {
                return boolean2 ? PistonHeadBlock.C : PistonHeadBlock.w;
            }
            case WEST: {
                return boolean2 ? PistonHeadBlock.F : PistonHeadBlock.z;
            }
            case EAST: {
                return boolean2 ? PistonHeadBlock.E : PistonHeadBlock.y;
            }
        }
    }
    
    @Override
    public void onBreak(final World world, final BlockPos pos, final BlockState state, final PlayerEntity player) {
        if (!world.isClient && player.abilities.creativeMode) {
            final BlockPos blockPos5 = pos.offset(state.<Direction>get((Property<Direction>)PistonHeadBlock.FACING).getOpposite());
            final Block block6 = world.getBlockState(blockPos5).getBlock();
            if (block6 == Blocks.aV || block6 == Blocks.aO) {
                world.clearBlockState(blockPos5, false);
            }
        }
        super.onBreak(world, pos, state, player);
    }
    
    @Override
    public void onBlockRemoved(final BlockState state, final World world, BlockPos pos, final BlockState newState, final boolean boolean5) {
        if (state.getBlock() == newState.getBlock()) {
            return;
        }
        super.onBlockRemoved(state, world, pos, newState, boolean5);
        final Direction direction6 = state.<Direction>get((Property<Direction>)PistonHeadBlock.FACING).getOpposite();
        pos = pos.offset(direction6);
        final BlockState blockState7 = world.getBlockState(pos);
        if ((blockState7.getBlock() == Blocks.aV || blockState7.getBlock() == Blocks.aO) && blockState7.<Boolean>get((Property<Boolean>)PistonBlock.EXTENDED)) {
            Block.dropStacks(blockState7, world, pos);
            world.clearBlockState(pos, false);
        }
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (facing.getOpposite() == state.<Direction>get((Property<Direction>)PistonHeadBlock.FACING) && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        final Block block4 = world.getBlockState(pos.offset(state.<Direction>get((Property<Direction>)PistonHeadBlock.FACING).getOpposite())).getBlock();
        return block4 == Blocks.aV || block4 == Blocks.aO || block4 == Blocks.bn;
    }
    
    @Override
    public void neighborUpdate(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighborPos, final boolean boolean6) {
        if (state.canPlaceAt(world, pos)) {
            final BlockPos blockPos7 = pos.offset(state.<Direction>get((Property<Direction>)PistonHeadBlock.FACING).getOpposite());
            world.getBlockState(blockPos7).neighborUpdate(world, blockPos7, block, neighborPos, false);
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack getPickStack(final BlockView world, final BlockPos pos, final BlockState state) {
        return new ItemStack((state.<PistonType>get(PistonHeadBlock.TYPE) == PistonType.b) ? Blocks.aO : Blocks.aV);
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)PistonHeadBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)PistonHeadBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.<Direction>get((Property<Direction>)PistonHeadBlock.FACING)));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(PistonHeadBlock.FACING, PistonHeadBlock.TYPE, PistonHeadBlock.SHORT);
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    static {
        TYPE = Properties.PISTON_TYPE;
        SHORT = Properties.SHORT;
        d = Block.createCuboidShape(12.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        e = Block.createCuboidShape(0.0, 0.0, 0.0, 4.0, 16.0, 16.0);
        f = Block.createCuboidShape(0.0, 0.0, 12.0, 16.0, 16.0, 16.0);
        g = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 4.0);
        h = Block.createCuboidShape(0.0, 12.0, 0.0, 16.0, 16.0, 16.0);
        i = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);
        j = Block.createCuboidShape(6.0, -4.0, 6.0, 10.0, 12.0, 10.0);
        k = Block.createCuboidShape(6.0, 4.0, 6.0, 10.0, 20.0, 10.0);
        w = Block.createCuboidShape(6.0, 6.0, -4.0, 10.0, 10.0, 12.0);
        x = Block.createCuboidShape(6.0, 6.0, 4.0, 10.0, 10.0, 20.0);
        y = Block.createCuboidShape(-4.0, 6.0, 6.0, 12.0, 10.0, 10.0);
        z = Block.createCuboidShape(4.0, 6.0, 6.0, 20.0, 10.0, 10.0);
        A = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 12.0, 10.0);
        B = Block.createCuboidShape(6.0, 4.0, 6.0, 10.0, 16.0, 10.0);
        C = Block.createCuboidShape(6.0, 6.0, 0.0, 10.0, 10.0, 12.0);
        D = Block.createCuboidShape(6.0, 6.0, 4.0, 10.0, 10.0, 16.0);
        E = Block.createCuboidShape(0.0, 6.0, 6.0, 12.0, 10.0, 10.0);
        F = Block.createCuboidShape(4.0, 6.0, 6.0, 16.0, 10.0, 10.0);
    }
}
