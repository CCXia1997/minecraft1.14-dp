package net.minecraft.entity.projectile;

import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Blocks;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public class SmallFireballEntity extends AbstractFireballEntity
{
    public SmallFireballEntity(final EntityType<? extends SmallFireballEntity> type, final World world) {
        super(type, world);
    }
    
    public SmallFireballEntity(final World world, final LivingEntity livingEntity, final double double3, final double double5, final double double7) {
        super(EntityType.SMALL_FIREBALL, livingEntity, double3, double5, double7, world);
    }
    
    public SmallFireballEntity(final World world, final double double2, final double double4, final double double6, final double double8, final double double10, final double double12) {
        super(EntityType.SMALL_FIREBALL, double2, double4, double6, double8, double10, double12, world);
    }
    
    @Override
    protected void onCollision(final HitResult hitResult) {
        if (!this.world.isClient) {
            if (hitResult.getType() == HitResult.Type.ENTITY) {
                final Entity entity2 = ((EntityHitResult)hitResult).getEntity();
                if (!entity2.isFireImmune()) {
                    entity2.setOnFireFor(5);
                    final boolean boolean3 = entity2.damage(DamageSource.explosiveProjectile(this, this.owner), 5.0f);
                    if (boolean3) {
                        this.dealDamage(this.owner, entity2);
                    }
                }
            }
            else if (this.owner == null || !(this.owner instanceof MobEntity) || this.world.getGameRules().getBoolean("mobGriefing")) {
                final BlockHitResult blockHitResult2 = (BlockHitResult)hitResult;
                final BlockPos blockPos3 = blockHitResult2.getBlockPos().offset(blockHitResult2.getSide());
                if (this.world.isAir(blockPos3)) {
                    this.world.setBlockState(blockPos3, Blocks.bM.getDefaultState());
                }
            }
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
}
