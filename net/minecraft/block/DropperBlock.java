package net.minecraft.block;

import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import net.minecraft.item.ItemStack;
import net.minecraft.block.dispenser.DispenserBehavior;

public class DropperBlock extends DispenserBlock
{
    private static final DispenserBehavior BEHAVIOR;
    
    public DropperBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    protected DispenserBehavior getBehaviorForItem(final ItemStack itemStack) {
        return DropperBlock.BEHAVIOR;
    }
    
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new DropperBlockEntity();
    }
    
    @Override
    protected void dispense(final World world, final BlockPos pos) {
        final BlockPointerImpl blockPointerImpl3 = new BlockPointerImpl(world, pos);
        final DispenserBlockEntity dispenserBlockEntity4 = blockPointerImpl3.<DispenserBlockEntity>getBlockEntity();
        final int integer5 = dispenserBlockEntity4.chooseNonEmptySlot();
        if (integer5 < 0) {
            world.playLevelEvent(1001, pos, 0);
            return;
        }
        final ItemStack itemStack6 = dispenserBlockEntity4.getInvStack(integer5);
        if (itemStack6.isEmpty()) {
            return;
        }
        final Direction direction7 = world.getBlockState(pos).<Direction>get((Property<Direction>)DropperBlock.FACING);
        final Inventory inventory8 = HopperBlockEntity.getInventoryAt(world, pos.offset(direction7));
        ItemStack itemStack7;
        if (inventory8 == null) {
            itemStack7 = DropperBlock.BEHAVIOR.dispense(blockPointerImpl3, itemStack6);
        }
        else {
            itemStack7 = HopperBlockEntity.transfer(dispenserBlockEntity4, inventory8, itemStack6.copy().split(1), direction7.getOpposite());
            if (itemStack7.isEmpty()) {
                itemStack7 = itemStack6.copy();
                itemStack7.subtractAmount(1);
            }
            else {
                itemStack7 = itemStack6.copy();
            }
        }
        dispenserBlockEntity4.setInvStack(integer5, itemStack7);
    }
    
    static {
        BEHAVIOR = new ItemDispenserBehavior();
    }
}
