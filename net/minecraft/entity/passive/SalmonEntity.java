package net.minecraft.entity.passive;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public class SalmonEntity extends SchoolingFishEntity
{
    public SalmonEntity(final EntityType<? extends SalmonEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    public int getMaxGroupSize() {
        return 5;
    }
    
    @Override
    protected ItemStack getFishBucketItem() {
        return new ItemStack(Items.kI);
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.jB;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.jC;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.jE;
    }
    
    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.jD;
    }
}
