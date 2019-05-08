package net.minecraft.entity.mob;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public class HuskEntity extends ZombieEntity
{
    public HuskEntity(final EntityType<? extends HuskEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    public boolean canSpawn(final IWorld iWorld, final SpawnType spawnType) {
        return super.canSpawn(iWorld, spawnType) && (spawnType == SpawnType.c || iWorld.isSkyVisible(new BlockPos(this)));
    }
    
    @Override
    protected boolean burnsInDaylight() {
        return false;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.eX;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.fa;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.eZ;
    }
    
    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.fb;
    }
    
    @Override
    public boolean tryAttack(final Entity entity) {
        final boolean boolean2 = super.tryAttack(entity);
        if (boolean2 && this.getMainHandStack().isEmpty() && entity instanceof LivingEntity) {
            final float float3 = this.world.getLocalDifficulty(new BlockPos(this)).getLocalDifficulty();
            ((LivingEntity)entity).addPotionEffect(new StatusEffectInstance(StatusEffects.q, 140 * (int)float3));
        }
        return boolean2;
    }
    
    @Override
    protected boolean canConvertInWater() {
        return true;
    }
    
    @Override
    protected void convertInWater() {
        this.convertTo(EntityType.ZOMBIE);
        this.world.playLevelEvent(null, 1041, new BlockPos((int)this.x, (int)this.y, (int)this.z), 0);
    }
    
    @Override
    protected ItemStack getSkull() {
        return ItemStack.EMPTY;
    }
}
