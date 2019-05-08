package net.minecraft.block;

import net.minecraft.world.IWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Property;
import net.minecraft.world.ViewableWorld;
import javax.annotation.Nullable;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.item.ItemPlacementContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.state.property.EnumProperty;

public class TallSeagrassBlock extends ReplaceableTallPlantBlock implements FluidFillable
{
    public static final EnumProperty<DoubleBlockHalf> HALF;
    protected static final VoxelShape SHAPE;
    
    public TallSeagrassBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return TallSeagrassBlock.SHAPE;
    }
    
    @Override
    protected boolean canPlantOnTop(final BlockState floor, final BlockView view, final BlockPos pos) {
        return Block.isSolidFullSquare(floor, view, pos, Direction.UP) && floor.getBlock() != Blocks.iB;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack getPickStack(final BlockView world, final BlockPos pos, final BlockState state) {
        return new ItemStack(Blocks.aT);
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final BlockState blockState2 = super.getPlacementState(ctx);
        if (blockState2 != null) {
            final FluidState fluidState3 = ctx.getWorld().getFluidState(ctx.getBlockPos().up());
            if (fluidState3.matches(FluidTags.a) && fluidState3.getLevel() == 8) {
                return blockState2;
            }
        }
        return null;
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        if (state.<DoubleBlockHalf>get(TallSeagrassBlock.HALF) == DoubleBlockHalf.a) {
            final BlockState blockState4 = world.getBlockState(pos.down());
            return blockState4.getBlock() == this && blockState4.<DoubleBlockHalf>get(TallSeagrassBlock.HALF) == DoubleBlockHalf.b;
        }
        final FluidState fluidState4 = world.getFluidState(pos);
        return super.canPlaceAt(state, world, pos) && fluidState4.matches(FluidTags.a) && fluidState4.getLevel() == 8;
    }
    
    @Override
    public FluidState getFluidState(final BlockState state) {
        return Fluids.WATER.getStill(false);
    }
    
    @Override
    public boolean canFillWithFluid(final BlockView view, final BlockPos pos, final BlockState state, final Fluid fluid) {
        return false;
    }
    
    @Override
    public boolean tryFillWithFluid(final IWorld world, final BlockPos pos, final BlockState state, final FluidState fluidState) {
        return false;
    }
    
    static {
        HALF = ReplaceableTallPlantBlock.HALF;
        SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    }
}
