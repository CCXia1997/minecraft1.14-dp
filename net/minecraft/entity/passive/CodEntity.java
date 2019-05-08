package net.minecraft.entity.passive;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public class CodEntity extends SchoolingFishEntity
{
    public CodEntity(final EntityType<? extends CodEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    protected ItemStack getFishBucketItem() {
        return new ItemStack(Items.kJ);
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.bc;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.bd;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.bf;
    }
    
    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.be;
    }
}
