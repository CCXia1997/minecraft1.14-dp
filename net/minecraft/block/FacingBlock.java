package net.minecraft.block;

import net.minecraft.state.property.Properties;
import net.minecraft.state.property.DirectionProperty;

public abstract class FacingBlock extends Block
{
    public static final DirectionProperty FACING;
    
    protected FacingBlock(final Settings settings) {
        super(settings);
    }
    
    static {
        FACING = Properties.FACING;
    }
}
