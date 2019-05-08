package net.minecraft.block;

import net.minecraft.item.ItemPlacementContext;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.state.property.EnumProperty;

public class ReplaceableTallPlantBlock extends TallPlantBlock
{
    public static final EnumProperty<DoubleBlockHalf> HALF;
    
    public ReplaceableTallPlantBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public boolean canReplace(final BlockState state, final ItemPlacementContext ctx) {
        final boolean boolean3 = super.canReplace(state, ctx);
        return (!boolean3 || ctx.getItemStack().getItem() != this.getItem()) && boolean3;
    }
    
    static {
        HALF = TallPlantBlock.HALF;
    }
}
