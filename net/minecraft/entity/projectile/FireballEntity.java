package net.minecraft.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.entity.LivingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public class FireballEntity extends AbstractFireballEntity
{
    public int explosionPower;
    
    public FireballEntity(final EntityType<? extends FireballEntity> type, final World world) {
        super(type, world);
        this.explosionPower = 1;
    }
    
    @Environment(EnvType.CLIENT)
    public FireballEntity(final World world, final double double2, final double double4, final double double6, final double double8, final double double10, final double double12) {
        super(EntityType.FIREBALL, double2, double4, double6, double8, double10, double12, world);
        this.explosionPower = 1;
    }
    
    public FireballEntity(final World world, final LivingEntity livingEntity, final double double3, final double double5, final double double7) {
        super(EntityType.FIREBALL, livingEntity, double3, double5, double7, world);
        this.explosionPower = 1;
    }
    
    @Override
    protected void onCollision(final HitResult hitResult) {
        if (!this.world.isClient) {
            if (hitResult.getType() == HitResult.Type.ENTITY) {
                final Entity entity2 = ((EntityHitResult)hitResult).getEntity();
                entity2.damage(DamageSource.explosiveProjectile(this, this.owner), 6.0f);
                this.dealDamage(this.owner, entity2);
            }
            final boolean boolean2 = this.world.getGameRules().getBoolean("mobGriefing");
            this.world.createExplosion(null, this.x, this.y, this.z, (float)this.explosionPower, boolean2, boolean2 ? Explosion.DestructionType.c : Explosion.DestructionType.a);
            this.remove();
        }
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("ExplosionPower", this.explosionPower);
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.containsKey("ExplosionPower", 99)) {
            this.explosionPower = tag.getInt("ExplosionPower");
        }
    }
}
