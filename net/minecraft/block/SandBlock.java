package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class SandBlock extends FallingBlock
{
    private final int color;
    
    public SandBlock(final int integer, final Settings settings) {
        super(settings);
        this.color = integer;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public int getColor(final BlockState state) {
        return this.color;
    }
}
