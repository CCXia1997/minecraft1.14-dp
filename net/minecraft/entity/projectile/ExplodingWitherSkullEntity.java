package net.minecraft.entity.projectile;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.Difficulty;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;

public class ExplodingWitherSkullEntity extends ExplosiveProjectileEntity
{
    private static final TrackedData<Boolean> CHARGED;
    
    public ExplodingWitherSkullEntity(final EntityType<? extends ExplodingWitherSkullEntity> type, final World world) {
        super(type, world);
    }
    
    public ExplodingWitherSkullEntity(final World world, final LivingEntity livingEntity, final double double3, final double double5, final double double7) {
        super(EntityType.WITHER_SKULL, livingEntity, double3, double5, double7, world);
    }
    
    @Environment(EnvType.CLIENT)
    public ExplodingWitherSkullEntity(final World world, final double double2, final double double4, final double double6, final double double8, final double double10, final double double12) {
        super(EntityType.WITHER_SKULL, double2, double4, double6, double8, double10, double12, world);
    }
    
    @Override
    protected float getDrag() {
        return this.isCharged() ? 0.73f : super.getDrag();
    }
    
    @Override
    public boolean isOnFire() {
        return false;
    }
    
    @Override
    public float getEffectiveExplosionResistance(final Explosion explosion, final BlockView world, final BlockPos pos, final BlockState blockState, final FluidState state, final float float6) {
        if (this.isCharged() && WitherEntity.canDestroy(blockState)) {
            return Math.min(0.8f, float6);
        }
        return float6;
    }
    
    @Override
    protected void onCollision(final HitResult hitResult) {
        if (!this.world.isClient) {
            if (hitResult.getType() == HitResult.Type.ENTITY) {
                final Entity entity2 = ((EntityHitResult)hitResult).getEntity();
                if (this.owner != null) {
                    if (entity2.damage(DamageSource.mob(this.owner), 8.0f)) {
                        if (entity2.isAlive()) {
                            this.dealDamage(this.owner, entity2);
                        }
                        else {
                            this.owner.heal(5.0f);
                        }
                    }
                }
                else {
                    entity2.damage(DamageSource.MAGIC, 5.0f);
                }
                if (entity2 instanceof LivingEntity) {
                    int integer3 = 0;
                    if (this.world.getDifficulty() == Difficulty.NORMAL) {
                        integer3 = 10;
                    }
                    else if (this.world.getDifficulty() == Difficulty.HARD) {
                        integer3 = 40;
                    }
                    if (integer3 > 0) {
                        ((LivingEntity)entity2).addPotionEffect(new StatusEffectInstance(StatusEffects.t, 20 * integer3, 1));
                    }
                }
            }
            final Explosion.DestructionType destructionType2 = this.world.getGameRules().getBoolean("mobGriefing") ? Explosion.DestructionType.c : Explosion.DestructionType.a;
            this.world.createExplosion(this, this.x, this.y, this.z, 1.0f, false, destructionType2);
            this.remove();
        }
    }
    
    @Override
    public boolean collides() {
        return false;
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        return false;
    }
    
    @Override
    protected void initDataTracker() {
        this.dataTracker.<Boolean>startTracking(ExplodingWitherSkullEntity.CHARGED, false);
    }
    
    public boolean isCharged() {
        return this.dataTracker.<Boolean>get(ExplodingWitherSkullEntity.CHARGED);
    }
    
    public void setCharged(final boolean boolean1) {
        this.dataTracker.<Boolean>set(ExplodingWitherSkullEntity.CHARGED, boolean1);
    }
    
    @Override
    protected boolean isBurning() {
        return false;
    }
    
    static {
        CHARGED = DataTracker.<Boolean>registerData(ExplodingWitherSkullEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
}
