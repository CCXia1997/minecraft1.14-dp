package net.minecraft.block;

import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class LogBlock extends PillarBlock
{
    private final MaterialColor endMaterialColor;
    
    public LogBlock(final MaterialColor endMaterialColor, final Settings settings) {
        super(settings);
        this.endMaterialColor = endMaterialColor;
    }
    
    @Override
    public MaterialColor getMapColor(final BlockState state, final BlockView view, final BlockPos pos) {
        return (state.<Direction.Axis>get(LogBlock.AXIS) == Direction.Axis.Y) ? this.endMaterialColor : this.materialColor;
    }
}
