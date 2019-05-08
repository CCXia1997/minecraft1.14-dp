package net.minecraft.block;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public interface FluidDrainable
{
    Fluid tryDrainFluid(final IWorld arg1, final BlockPos arg2, final BlockState arg3);
}
