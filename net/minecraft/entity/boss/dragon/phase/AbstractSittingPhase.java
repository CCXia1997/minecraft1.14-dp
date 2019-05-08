package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

public abstract class AbstractSittingPhase extends AbstractPhase
{
    public AbstractSittingPhase(final EnderDragonEntity dragon) {
        super(dragon);
    }
    
    @Override
    public boolean a() {
        return true;
    }
    
    @Override
    public float modifyDamageTaken(final DamageSource damageSource, final float float2) {
        if (damageSource.getSource() instanceof ProjectileEntity) {
            damageSource.getSource().setOnFireFor(1);
            return 0.0f;
        }
        return super.modifyDamageTaken(damageSource, float2);
    }
}
