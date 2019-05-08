package net.minecraft.block;

import net.minecraft.world.ViewableWorld;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.util.DyeColor;
import net.minecraft.util.shape.VoxelShape;

public class CarpetBlock extends Block
{
    protected static final VoxelShape SHAPE;
    private final DyeColor color;
    
    protected CarpetBlock(final DyeColor dyeColor, final Settings settings) {
        super(settings);
        this.color = dyeColor;
    }
    
    public DyeColor getColor() {
        return this.color;
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return CarpetBlock.SHAPE;
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        return !world.isAir(pos.down());
    }
    
    static {
        SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
    }
}
