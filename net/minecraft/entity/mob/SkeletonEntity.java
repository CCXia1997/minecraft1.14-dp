package net.minecraft.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public class SkeletonEntity extends AbstractSkeletonEntity
{
    public SkeletonEntity(final EntityType<? extends SkeletonEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.kn;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.kx;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ko;
    }
    
    @Override
    SoundEvent getStepSound() {
        return SoundEvents.kz;
    }
    
    @Override
    protected void dropEquipment(final DamageSource damageSource, final int addedDropChance, final boolean dropAllowed) {
        super.dropEquipment(damageSource, addedDropChance, dropAllowed);
        final Entity entity4 = damageSource.getAttacker();
        if (entity4 instanceof CreeperEntity) {
            final CreeperEntity creeperEntity5 = (CreeperEntity)entity4;
            if (creeperEntity5.shouldDropHead()) {
                creeperEntity5.onHeadDropped();
                this.dropItem(Items.SKELETON_SKULL);
            }
        }
    }
}
