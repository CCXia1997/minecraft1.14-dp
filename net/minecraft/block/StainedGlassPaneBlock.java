package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Property;
import net.minecraft.util.DyeColor;
import net.minecraft.client.block.ColoredBlock;

public class StainedGlassPaneBlock extends PaneBlock implements ColoredBlock
{
    private final DyeColor color;
    
    public StainedGlassPaneBlock(final DyeColor dyeColor, final Settings settings) {
        super(settings);
        this.color = dyeColor;
        this.setDefaultState(((((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)StainedGlassPaneBlock.NORTH, false)).with((Property<Comparable>)StainedGlassPaneBlock.EAST, false)).with((Property<Comparable>)StainedGlassPaneBlock.SOUTH, false)).with((Property<Comparable>)StainedGlassPaneBlock.WEST, false)).<Comparable, Boolean>with((Property<Comparable>)StainedGlassPaneBlock.WATERLOGGED, false));
    }
    
    @Override
    public DyeColor getColor() {
        return this.color;
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }
}
