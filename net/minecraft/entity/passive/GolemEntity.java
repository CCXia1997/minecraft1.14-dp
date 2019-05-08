package net.minecraft.entity.passive;

import net.minecraft.entity.damage.DamageSource;
import javax.annotation.Nullable;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntityWithAi;

public abstract class GolemEntity extends MobEntityWithAi
{
    protected GolemEntity(final EntityType<? extends GolemEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    public void handleFallDamage(final float fallDistance, final float damageMultiplier) {
    }
    
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }
    
    @Nullable
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return null;
    }
    
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }
    
    @Override
    public int getMinAmbientSoundDelay() {
        return 120;
    }
    
    @Override
    public boolean canImmediatelyDespawn(final double distanceSquared) {
        return false;
    }
}
