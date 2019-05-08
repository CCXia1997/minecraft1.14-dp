package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class GravelBlock extends FallingBlock
{
    public GravelBlock(final Settings settings) {
        super(settings);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public int getColor(final BlockState state) {
        return -8356741;
    }
}
