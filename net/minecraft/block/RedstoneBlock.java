package net.minecraft.block;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class RedstoneBlock extends Block
{
    public RedstoneBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public boolean emitsRedstonePower(final BlockState state) {
        return true;
    }
    
    @Override
    public int getWeakRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        return 15;
    }
}
