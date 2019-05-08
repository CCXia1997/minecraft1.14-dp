package net.minecraft.block.dispenser;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.Position;
import net.minecraft.state.property.Property;
import net.minecraft.block.DispenserBlock;
import net.minecraft.util.math.Direction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;

public class ItemDispenserBehavior implements DispenserBehavior
{
    @Override
    public final ItemStack dispense(final BlockPointer location, final ItemStack stack) {
        final ItemStack itemStack3 = this.dispenseStack(location, stack);
        this.playSound(location);
        this.spawnParticles(location, location.getBlockState().<Direction>get((Property<Direction>)DispenserBlock.FACING));
        return itemStack3;
    }
    
    protected ItemStack dispenseStack(final BlockPointer pointer, final ItemStack stack) {
        final Direction direction3 = pointer.getBlockState().<Direction>get((Property<Direction>)DispenserBlock.FACING);
        final Position position4 = DispenserBlock.getOutputLocation(pointer);
        final ItemStack itemStack5 = stack.split(1);
        dispenseItem(pointer.getWorld(), itemStack5, 6, direction3, position4);
        return stack;
    }
    
    public static void dispenseItem(final World world, final ItemStack itemStack, final int integer, final Direction direction, final Position position) {
        final double double6 = position.getX();
        double double7 = position.getY();
        final double double8 = position.getZ();
        if (direction.getAxis() == Direction.Axis.Y) {
            double7 -= 0.125;
        }
        else {
            double7 -= 0.15625;
        }
        final ItemEntity itemEntity12 = new ItemEntity(world, double6, double7, double8, itemStack);
        final double double9 = world.random.nextDouble() * 0.1 + 0.2;
        itemEntity12.setVelocity(world.random.nextGaussian() * 0.007499999832361937 * integer + direction.getOffsetX() * double9, world.random.nextGaussian() * 0.007499999832361937 * integer + 0.20000000298023224, world.random.nextGaussian() * 0.007499999832361937 * integer + direction.getOffsetZ() * double9);
        world.spawnEntity(itemEntity12);
    }
    
    protected void playSound(final BlockPointer blockPointer) {
        blockPointer.getWorld().playLevelEvent(1000, blockPointer.getBlockPos(), 0);
    }
    
    protected void spawnParticles(final BlockPointer block, final Direction direction) {
        block.getWorld().playLevelEvent(2000, block.getBlockPos(), direction.getId());
    }
}
