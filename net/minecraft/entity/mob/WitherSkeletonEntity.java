package net.minecraft.entity.mob;

import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.IWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public class WitherSkeletonEntity extends AbstractSkeletonEntity
{
    public WitherSkeletonEntity(final EntityType<? extends WitherSkeletonEntity> type, final World world) {
        super(type, world);
        this.setPathNodeTypeWeight(PathNodeType.f, 8.0f);
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.nr;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.nt;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ns;
    }
    
    @Override
    SoundEvent getStepSound() {
        return SoundEvents.nu;
    }
    
    @Override
    protected void dropEquipment(final DamageSource damageSource, final int addedDropChance, final boolean dropAllowed) {
        super.dropEquipment(damageSource, addedDropChance, dropAllowed);
        final Entity entity4 = damageSource.getAttacker();
        if (entity4 instanceof CreeperEntity) {
            final CreeperEntity creeperEntity5 = (CreeperEntity)entity4;
            if (creeperEntity5.shouldDropHead()) {
                creeperEntity5.onHeadDropped();
                this.dropItem(Items.WITHER_SKELETON_SKULL);
            }
        }
    }
    
    @Override
    protected void initEquipment(final LocalDifficulty localDifficulty) {
        this.setEquippedStack(EquipmentSlot.HAND_MAIN, new ItemStack(Items.jr));
    }
    
    @Override
    protected void updateEnchantments(final LocalDifficulty difficulty) {
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable final EntityData entityData, @Nullable final CompoundTag compoundTag) {
        final EntityData entityData2 = super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
        this.updateAttackType();
        return entityData2;
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return 2.1f;
    }
    
    @Override
    public boolean tryAttack(final Entity entity) {
        if (!super.tryAttack(entity)) {
            return false;
        }
        if (entity instanceof LivingEntity) {
            ((LivingEntity)entity).addPotionEffect(new StatusEffectInstance(StatusEffects.t, 200));
        }
        return true;
    }
    
    @Override
    protected ProjectileEntity createArrowProjectile(final ItemStack arrow, final float float2) {
        final ProjectileEntity projectileEntity3 = super.createArrowProjectile(arrow, float2);
        projectileEntity3.setOnFireFor(100);
        return projectileEntity3;
    }
    
    @Override
    public boolean isPotionEffective(final StatusEffectInstance statusEffectInstance) {
        return statusEffectInstance.getEffectType() != StatusEffects.t && super.isPotionEffective(statusEffectInstance);
    }
}
