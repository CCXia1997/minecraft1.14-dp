package net.minecraft.block.dispenser;

import net.minecraft.util.math.BlockPos;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.state.property.Property;
import net.minecraft.block.DispenserBlock;
import net.minecraft.util.math.Direction;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;

public class BlockPlacementDispenserBehavior extends FallibleItemDispenserBehavior
{
    @Override
    protected ItemStack dispenseStack(final BlockPointer pointer, final ItemStack stack) {
        this.success = false;
        final Item item3 = stack.getItem();
        if (item3 instanceof BlockItem) {
            final Direction direction4 = pointer.getBlockState().<Direction>get((Property<Direction>)DispenserBlock.FACING);
            final BlockPos blockPos5 = pointer.getBlockPos().offset(direction4);
            final Direction direction5 = pointer.getWorld().isAir(blockPos5.down()) ? direction4 : Direction.UP;
            this.success = (((BlockItem)item3).place(new AutomaticItemPlacementContext(pointer.getWorld(), blockPos5, direction4, stack, direction5)) == ActionResult.a);
            if (this.success) {
                stack.subtractAmount(1);
            }
        }
        return stack;
    }
}
