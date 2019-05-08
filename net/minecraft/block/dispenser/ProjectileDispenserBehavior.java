package net.minecraft.block.dispenser;

import net.minecraft.entity.projectile.Projectile;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;

public abstract class ProjectileDispenserBehavior extends ItemDispenserBehavior
{
    public ItemStack dispenseStack(final BlockPointer pointer, final ItemStack stack) {
        final World world3 = pointer.getWorld();
        final Position position4 = DispenserBlock.getOutputLocation(pointer);
        final Direction direction5 = pointer.getBlockState().<Direction>get((Property<Direction>)DispenserBlock.FACING);
        final Projectile projectile6 = this.createProjectile(world3, position4, stack);
        projectile6.setVelocity(direction5.getOffsetX(), direction5.getOffsetY() + 0.1f, direction5.getOffsetZ(), this.getForce(), this.getVariation());
        world3.spawnEntity((Entity)projectile6);
        stack.subtractAmount(1);
        return stack;
    }
    
    @Override
    protected void playSound(final BlockPointer blockPointer) {
        blockPointer.getWorld().playLevelEvent(1002, blockPointer.getBlockPos(), 0);
    }
    
    protected abstract Projectile createProjectile(final World arg1, final Position arg2, final ItemStack arg3);
    
    protected float getVariation() {
        return 6.0f;
    }
    
    protected float getForce() {
        return 1.1f;
    }
}
