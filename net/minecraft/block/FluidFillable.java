package net.minecraft.block;

import net.minecraft.fluid.FluidState;
import net.minecraft.world.IWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public interface FluidFillable
{
    boolean canFillWithFluid(final BlockView arg1, final BlockPos arg2, final BlockState arg3, final Fluid arg4);
    
    boolean tryFillWithFluid(final IWorld arg1, final BlockPos arg2, final BlockState arg3, final FluidState arg4);
}
