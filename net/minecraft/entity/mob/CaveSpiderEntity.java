package net.minecraft.entity.mob;

import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.Difficulty;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public class CaveSpiderEntity extends SpiderEntity
{
    public CaveSpiderEntity(final EntityType<? extends CaveSpiderEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(12.0);
    }
    
    @Override
    public boolean tryAttack(final Entity entity) {
        if (super.tryAttack(entity)) {
            if (entity instanceof LivingEntity) {
                int integer2 = 0;
                if (this.world.getDifficulty() == Difficulty.NORMAL) {
                    integer2 = 7;
                }
                else if (this.world.getDifficulty() == Difficulty.HARD) {
                    integer2 = 15;
                }
                if (integer2 > 0) {
                    ((LivingEntity)entity).addPotionEffect(new StatusEffectInstance(StatusEffects.s, integer2 * 20, 0));
                }
            }
            return true;
        }
        return false;
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable final EntityData entityData, @Nullable final CompoundTag compoundTag) {
        return entityData;
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return 0.45f;
    }
}
