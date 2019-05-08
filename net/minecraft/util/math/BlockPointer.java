package net.minecraft.util.math;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;

public interface BlockPointer extends WorldPositionPointer
{
    double getX();
    
    double getY();
    
    double getZ();
    
    BlockPos getBlockPos();
    
    BlockState getBlockState();
    
     <T extends BlockEntity> T getBlockEntity();
}
