package net.minecraft.block.dispenser;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.tag.FluidTags;
import net.minecraft.state.property.Property;
import net.minecraft.block.DispenserBlock;
import net.minecraft.util.math.Direction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.entity.vehicle.BoatEntity;

public class BoatDispenserBehavior extends ItemDispenserBehavior
{
    private final ItemDispenserBehavior itemDispenser;
    private final BoatEntity.Type boatType;
    
    public BoatDispenserBehavior(final BoatEntity.Type type) {
        this.itemDispenser = new ItemDispenserBehavior();
        this.boatType = type;
    }
    
    public ItemStack dispenseStack(final BlockPointer pointer, final ItemStack stack) {
        final Direction direction3 = pointer.getBlockState().<Direction>get((Property<Direction>)DispenserBlock.FACING);
        final World world4 = pointer.getWorld();
        final double double5 = pointer.getX() + direction3.getOffsetX() * 1.125f;
        final double double6 = pointer.getY() + direction3.getOffsetY() * 1.125f;
        final double double7 = pointer.getZ() + direction3.getOffsetZ() * 1.125f;
        final BlockPos blockPos11 = pointer.getBlockPos().offset(direction3);
        double double8;
        if (world4.getFluidState(blockPos11).matches(FluidTags.a)) {
            double8 = 1.0;
        }
        else {
            if (!world4.getBlockState(blockPos11).isAir() || !world4.getFluidState(blockPos11.down()).matches(FluidTags.a)) {
                return this.itemDispenser.dispense(pointer, stack);
            }
            double8 = 0.0;
        }
        final BoatEntity boatEntity14 = new BoatEntity(world4, double5, double6 + double8, double7);
        boatEntity14.setBoatType(this.boatType);
        boatEntity14.yaw = direction3.asRotation();
        world4.spawnEntity(boatEntity14);
        stack.subtractAmount(1);
        return stack;
    }
    
    @Override
    protected void playSound(final BlockPointer blockPointer) {
        blockPointer.getWorld().playLevelEvent(1000, blockPointer.getBlockPos(), 0);
    }
}
