package net.minecraft.block;

import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public interface Fertilizable
{
    boolean isFertilizable(final BlockView arg1, final BlockPos arg2, final BlockState arg3, final boolean arg4);
    
    boolean canGrow(final World arg1, final Random arg2, final BlockPos arg3, final BlockState arg4);
    
    void grow(final World arg1, final Random arg2, final BlockPos arg3, final BlockState arg4);
}
