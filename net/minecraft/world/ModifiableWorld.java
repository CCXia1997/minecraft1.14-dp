package net.minecraft.world;

import net.minecraft.entity.Entity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface ModifiableWorld
{
    boolean setBlockState(final BlockPos arg1, final BlockState arg2, final int arg3);
    
    boolean clearBlockState(final BlockPos arg1, final boolean arg2);
    
    boolean breakBlock(final BlockPos arg1, final boolean arg2);
    
    default boolean spawnEntity(final Entity entity) {
        return false;
    }
}
