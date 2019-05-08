package net.minecraft.entity.mob;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import javax.annotation.Nullable;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.SkeletonHorseGoal;
import net.minecraft.entity.passive.HorseBaseEntity;

public class SkeletonHorseEntity extends HorseBaseEntity
{
    private final SkeletonHorseGoal bJ;
    private boolean bK;
    private int bL;
    
    public SkeletonHorseEntity(final EntityType<? extends SkeletonHorseEntity> type, final World world) {
        super(type, world);
        this.bJ = new SkeletonHorseGoal(this);
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(15.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224);
        this.getAttributeInstance(SkeletonHorseEntity.JUMP_STRENGTH).setBaseValue(this.getChildJumpStrengthBonus());
    }
    
    @Override
    protected void initCustomGoals() {
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        if (this.isInFluid(FluidTags.a)) {
            return SoundEvents.kt;
        }
        return SoundEvents.kp;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.kq;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        super.getHurtSound(source);
        return SoundEvents.kr;
    }
    
    @Override
    protected SoundEvent getSwimSound() {
        if (this.onGround) {
            if (!this.hasPassengers()) {
                return SoundEvents.kw;
            }
            ++this.soundTicks;
            if (this.soundTicks > 5 && this.soundTicks % 3 == 0) {
                return SoundEvents.ku;
            }
            if (this.soundTicks <= 5) {
                return SoundEvents.kw;
            }
        }
        return SoundEvents.ks;
    }
    
    @Override
    protected void playSwimSound(final float volume) {
        if (this.onGround) {
            super.playSwimSound(0.3f);
        }
        else {
            super.playSwimSound(Math.min(0.1f, volume * 25.0f));
        }
    }
    
    @Override
    protected void playJumpSound() {
        if (this.isInsideWater()) {
            this.playSound(SoundEvents.kv, 0.4f, 1.0f);
        }
        else {
            super.playJumpSound();
        }
    }
    
    @Override
    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }
    
    @Override
    public double getMountedHeightOffset() {
        return super.getMountedHeightOffset() - 0.1875;
    }
    
    @Override
    public void updateState() {
        super.updateState();
        if (this.dV() && this.bL++ >= 18000) {
            this.remove();
        }
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putBoolean("SkeletonTrap", this.dV());
        tag.putInt("SkeletonTrapTime", this.bL);
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.r(tag.getBoolean("SkeletonTrap"));
        this.bL = tag.getInt("SkeletonTrapTime");
    }
    
    @Override
    public boolean canBeRiddenInWater() {
        return true;
    }
    
    @Override
    protected float getBaseMovementSpeedMultiplier() {
        return 0.96f;
    }
    
    public boolean dV() {
        return this.bK;
    }
    
    public void r(final boolean boolean1) {
        if (boolean1 == this.bK) {
            return;
        }
        this.bK = boolean1;
        if (boolean1) {
            this.goalSelector.add(1, this.bJ);
        }
        else {
            this.goalSelector.remove(this.bJ);
        }
    }
    
    @Nullable
    @Override
    public PassiveEntity createChild(final PassiveEntity mate) {
        return EntityType.SKELETON_HORSE.create(this.world);
    }
    
    @Override
    public boolean interactMob(final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack3 = player.getStackInHand(hand);
        if (itemStack3.getItem() instanceof SpawnEggItem) {
            return super.interactMob(player, hand);
        }
        if (!this.isTame()) {
            return false;
        }
        if (this.isChild()) {
            return super.interactMob(player, hand);
        }
        if (player.isSneaking()) {
            this.openInventory(player);
            return true;
        }
        if (this.hasPassengers()) {
            return super.interactMob(player, hand);
        }
        if (!itemStack3.isEmpty()) {
            if (itemStack3.getItem() == Items.kB && !this.isSaddled()) {
                this.openInventory(player);
                return true;
            }
            if (itemStack3.interactWithEntity(player, this, hand)) {
                return true;
            }
        }
        this.putPlayerOnBack(player);
        return true;
    }
}
