package net.minecraft.entity;

import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;

public interface VerticalEntityPosition
{
    default VerticalEntityPosition minValue() {
        return VerticalEntityPositionImpl.MIN_VALUE;
    }
    
    default VerticalEntityPosition fromEntity(final Entity entity) {
        return new VerticalEntityPositionImpl(entity);
    }
    
    boolean isSneaking();
    
    boolean isAboveBlock(final VoxelShape arg1, final BlockPos arg2, final boolean arg3);
    
    boolean a(final Item arg1);
}
