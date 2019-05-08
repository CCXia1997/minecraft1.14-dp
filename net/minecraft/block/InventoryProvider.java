package net.minecraft.block;

import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public interface InventoryProvider
{
    SidedInventory getInventory(final BlockState arg1, final IWorld arg2, final BlockPos arg3);
}
