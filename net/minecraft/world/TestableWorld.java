package net.minecraft.world;

import net.minecraft.block.BlockState;
import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;

public interface TestableWorld
{
    boolean testBlockState(final BlockPos arg1, final Predicate<BlockState> arg2);
    
    BlockPos getTopPosition(final Heightmap.Type arg1, final BlockPos arg2);
}
