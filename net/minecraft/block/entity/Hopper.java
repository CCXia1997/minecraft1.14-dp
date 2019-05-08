package net.minecraft.block.entity;

import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.block.Block;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.inventory.Inventory;

public interface Hopper extends Inventory
{
    public static final VoxelShape INSIDE_SHAPE = Block.createCuboidShape(2.0, 11.0, 2.0, 14.0, 16.0, 14.0);
    public static final VoxelShape ABOVE_SHAPE = Block.createCuboidShape(0.0, 16.0, 0.0, 16.0, 32.0, 16.0);
    public static final VoxelShape INPUT_AREA_SHAPE = VoxelShapes.union(Hopper.INSIDE_SHAPE, Hopper.ABOVE_SHAPE);
    
    default VoxelShape getInputAreaShape() {
        return Hopper.INPUT_AREA_SHAPE;
    }
    
    @Nullable
    World getWorld();
    
    double getHopperX();
    
    double getHopperY();
    
    double getHopperZ();
}
