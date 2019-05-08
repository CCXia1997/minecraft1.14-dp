package net.minecraft.entity.passive;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public class MuleEntity extends AbstractDonkeyEntity
{
    public MuleEntity(final EntityType<? extends MuleEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.gA;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.gC;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        super.getHurtSound(source);
        return SoundEvents.gD;
    }
    
    @Override
    protected void playAddChestSound() {
        this.playSound(SoundEvents.gB, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
    }
}
