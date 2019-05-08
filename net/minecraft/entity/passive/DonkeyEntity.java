package net.minecraft.entity.passive;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public class DonkeyEntity extends AbstractDonkeyEntity
{
    public DonkeyEntity(final EntityType<? extends DonkeyEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.bV;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.bY;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        super.getHurtSound(source);
        return SoundEvents.bZ;
    }
    
    @Override
    public boolean canBreedWith(final AnimalEntity other) {
        return other != this && (other instanceof DonkeyEntity || other instanceof HorseEntity) && this.canBreed() && ((HorseBaseEntity)other).canBreed();
    }
    
    @Override
    public PassiveEntity createChild(final PassiveEntity mate) {
        final EntityType<? extends HorseBaseEntity> entityType2 = (mate instanceof HorseEntity) ? EntityType.MULE : EntityType.DONKEY;
        final HorseBaseEntity horseBaseEntity3 = (HorseBaseEntity)entityType2.create(this.world);
        this.setChildAttributes(mate, horseBaseEntity3);
        return horseBaseEntity3;
    }
}
