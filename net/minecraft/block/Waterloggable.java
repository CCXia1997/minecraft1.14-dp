package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.world.ViewableWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.world.IWorld;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Property;
import net.minecraft.state.property.Properties;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public interface Waterloggable extends FluidDrainable, FluidFillable
{
    default boolean canFillWithFluid(final BlockView view, final BlockPos pos, final BlockState state, final Fluid fluid) {
        return !state.<Boolean>get((Property<Boolean>)Properties.WATERLOGGED) && fluid == Fluids.WATER;
    }
    
    default boolean tryFillWithFluid(final IWorld world, final BlockPos pos, final BlockState state, final FluidState fluidState) {
        if (!state.<Boolean>get((Property<Boolean>)Properties.WATERLOGGED) && fluidState.getFluid() == Fluids.WATER) {
            if (!world.isClient()) {
                world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)Properties.WATERLOGGED, true), 3);
                world.getFluidTickScheduler().schedule(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
            }
            return true;
        }
        return false;
    }
    
    default Fluid tryDrainFluid(final IWorld world, final BlockPos pos, final BlockState state) {
        if (state.<Boolean>get((Property<Boolean>)Properties.WATERLOGGED)) {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)Properties.WATERLOGGED, false), 3);
            return Fluids.WATER;
        }
        return Fluids.EMPTY;
    }
}
