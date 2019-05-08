package net.minecraft.item;

import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class SpectralArrowItem extends ArrowItem
{
    public SpectralArrowItem(final Settings settings) {
        super(settings);
    }
    
    @Override
    public ProjectileEntity createProjectile(final World world, final ItemStack stack, final LivingEntity livingShooter) {
        return new SpectralArrowEntity(world, livingShooter);
    }
}
