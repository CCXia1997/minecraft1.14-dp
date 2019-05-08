package net.minecraft.world;

import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.FluidState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public enum EmptyBlockView implements BlockView
{
    a;
    
    @Nullable
    @Override
    public BlockEntity getBlockEntity(final BlockPos pos) {
        return null;
    }
    
    @Override
    public BlockState getBlockState(final BlockPos pos) {
        return Blocks.AIR.getDefaultState();
    }
    
    @Override
    public FluidState getFluidState(final BlockPos pos) {
        return Fluids.EMPTY.getDefaultState();
    }
}
