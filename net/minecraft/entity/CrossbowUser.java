package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.item.ItemStack;

public interface CrossbowUser
{
    void setCharging(final boolean arg1);
    
    void shoot(final LivingEntity arg1, final ItemStack arg2, final Projectile arg3, final float arg4);
    
    @Nullable
    LivingEntity getTarget();
}
