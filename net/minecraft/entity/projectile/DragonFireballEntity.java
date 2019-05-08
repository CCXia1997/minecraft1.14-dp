package net.minecraft.entity.projectile;

import net.minecraft.entity.damage.DamageSource;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.entity.LivingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public class DragonFireballEntity extends ExplosiveProjectileEntity
{
    public DragonFireballEntity(final EntityType<? extends DragonFireballEntity> type, final World world) {
        super(type, world);
    }
    
    @Environment(EnvType.CLIENT)
    public DragonFireballEntity(final World world, final double double2, final double double4, final double double6, final double double8, final double double10, final double double12) {
        super(EntityType.DRAGON_FIREBALL, double2, double4, double6, double8, double10, double12, world);
    }
    
    public DragonFireballEntity(final World world, final LivingEntity livingEntity, final double double3, final double double5, final double double7) {
        super(EntityType.DRAGON_FIREBALL, livingEntity, double3, double5, double7, world);
    }
    
    @Override
    protected void onCollision(final HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.ENTITY && ((EntityHitResult)hitResult).getEntity().isPartOf(this.owner)) {
            return;
        }
        if (!this.world.isClient) {
            final List<LivingEntity> list2 = this.world.<LivingEntity>getEntities(LivingEntity.class, this.getBoundingBox().expand(4.0, 2.0, 4.0));
            final AreaEffectCloudEntity areaEffectCloudEntity3 = new AreaEffectCloudEntity(this.world, this.x, this.y, this.z);
            areaEffectCloudEntity3.setOwner(this.owner);
            areaEffectCloudEntity3.setParticleType(ParticleTypes.i);
            areaEffectCloudEntity3.setRadius(3.0f);
            areaEffectCloudEntity3.setDuration(600);
            areaEffectCloudEntity3.setRadiusGrowth((7.0f - areaEffectCloudEntity3.getRadius()) / areaEffectCloudEntity3.getDuration());
            areaEffectCloudEntity3.addEffect(new StatusEffectInstance(StatusEffects.g, 1, 1));
            if (!list2.isEmpty()) {
                for (final LivingEntity livingEntity5 : list2) {
                    final double double6 = this.squaredDistanceTo(livingEntity5);
                    if (double6 < 16.0) {
                        areaEffectCloudEntity3.setPosition(livingEntity5.x, livingEntity5.y, livingEntity5.z);
                        break;
                    }
                }
            }
            this.world.playLevelEvent(2006, new BlockPos(this.x, this.y, this.z), 0);
            this.world.spawnEntity(areaEffectCloudEntity3);
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
    protected ParticleParameters getParticleType() {
        return ParticleTypes.i;
    }
    
    @Override
    protected boolean isBurning() {
        return false;
    }
}
