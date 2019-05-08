package net.minecraft.item;

import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class ArrowItem extends Item
{
    public ArrowItem(final Settings settings) {
        super(settings);
    }
    
    public ProjectileEntity createProjectile(final World world, final ItemStack stack, final LivingEntity livingShooter) {
        final ArrowEntity arrowEntity4 = new ArrowEntity(world, livingShooter);
        arrowEntity4.initFromStack(stack);
        return arrowEntity4;
    }
}
