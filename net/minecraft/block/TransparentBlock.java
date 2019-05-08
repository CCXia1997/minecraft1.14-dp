package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Direction;

public class TransparentBlock extends Block
{
    protected TransparentBlock(final Settings settings) {
        super(settings);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean skipRenderingSide(final BlockState state, final BlockState neighbor, final Direction facing) {
        return neighbor.getBlock() == this || super.skipRenderingSide(state, neighbor, facing);
    }
}
