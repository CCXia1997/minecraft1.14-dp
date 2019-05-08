package net.minecraft.block;

import net.minecraft.state.property.Property;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.world.ViewableWorld;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.world.BlockView;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.shape.VoxelShape;

public abstract class AbstractRailBlock extends Block
{
    protected static final VoxelShape STRAIGHT_SHAPE;
    protected static final VoxelShape ASCENDING_SHAPE;
    private final boolean allowCurves;
    
    public static boolean isRail(final World world, final BlockPos pos) {
        return isRail(world.getBlockState(pos));
    }
    
    public static boolean isRail(final BlockState state) {
        return state.matches(BlockTags.B);
    }
    
    protected AbstractRailBlock(final boolean boolean1, final Settings settings) {
        super(settings);
        this.allowCurves = boolean1;
    }
    
    public boolean canMakeCurves() {
        return this.allowCurves;
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        final RailShape railShape5 = (state.getBlock() == this) ? state.<RailShape>get(this.getShapeProperty()) : null;
        if (railShape5 != null && railShape5.isAscending()) {
            return AbstractRailBlock.ASCENDING_SHAPE;
        }
        return AbstractRailBlock.STRAIGHT_SHAPE;
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        return Block.isSolidMediumSquare(world, pos.down());
    }
    
    @Override
    public void onBlockAdded(BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean boolean5) {
        if (oldState.getBlock() == state.getBlock()) {
            return;
        }
        if (!world.isClient) {
            state = this.updateBlockState(world, pos, state, true);
            if (this.allowCurves) {
                state.neighborUpdate(world, pos, this, pos, boolean5);
            }
        }
    }
    
    @Override
    public void neighborUpdate(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighborPos, final boolean boolean6) {
        if (world.isClient) {
            return;
        }
        final RailShape railShape7 = state.<RailShape>get(this.getShapeProperty());
        boolean boolean7 = false;
        final BlockPos blockPos9 = pos.down();
        if (!Block.isSolidMediumSquare(world, blockPos9)) {
            boolean7 = true;
        }
        final BlockPos blockPos10 = pos.east();
        if (railShape7 == RailShape.c && !Block.isSolidMediumSquare(world, blockPos10)) {
            boolean7 = true;
        }
        else {
            final BlockPos blockPos11 = pos.west();
            if (railShape7 == RailShape.d && !Block.isSolidMediumSquare(world, blockPos11)) {
                boolean7 = true;
            }
            else {
                final BlockPos blockPos12 = pos.north();
                if (railShape7 == RailShape.e && !Block.isSolidMediumSquare(world, blockPos12)) {
                    boolean7 = true;
                }
                else {
                    final BlockPos blockPos13 = pos.south();
                    if (railShape7 == RailShape.f && !Block.isSolidMediumSquare(world, blockPos13)) {
                        boolean7 = true;
                    }
                }
            }
        }
        if (boolean7 && !world.isAir(pos)) {
            if (!boolean6) {
                Block.dropStacks(state, world, pos);
            }
            world.clearBlockState(pos, boolean6);
        }
        else {
            this.updateBlockState(state, world, pos, block);
        }
    }
    
    protected void updateBlockState(final BlockState state, final World world, final BlockPos pos, final Block neighbor) {
    }
    
    protected BlockState updateBlockState(final World world, final BlockPos blockPos, final BlockState blockState, final boolean forceUpdate) {
        if (world.isClient) {
            return blockState;
        }
        return new RailPlacementHelper(world, blockPos, blockState).updateBlockState(world.isReceivingRedstonePower(blockPos), forceUpdate).getBlockState();
    }
    
    @Override
    public PistonBehavior getPistonBehavior(final BlockState state) {
        return PistonBehavior.a;
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    public void onBlockRemoved(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean boolean5) {
        if (boolean5) {
            return;
        }
        super.onBlockRemoved(state, world, pos, newState, boolean5);
        if (state.<RailShape>get(this.getShapeProperty()).isAscending()) {
            world.updateNeighborsAlways(pos.up(), this);
        }
        if (this.allowCurves) {
            world.updateNeighborsAlways(pos, this);
            world.updateNeighborsAlways(pos.down(), this);
        }
    }
    
    public abstract Property<RailShape> getShapeProperty();
    
    static {
        STRAIGHT_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
        ASCENDING_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    }
}
