package net.minecraft.entity.thrown;

import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ItemStackParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public class ThrownEggEntity extends ThrownItemEntity
{
    public ThrownEggEntity(final EntityType<? extends ThrownEggEntity> type, final World world) {
        super(type, world);
    }
    
    public ThrownEggEntity(final World world, final LivingEntity livingEntity) {
        super(EntityType.EGG, livingEntity, world);
    }
    
    public ThrownEggEntity(final World world, final double double2, final double double4, final double double6) {
        super(EntityType.EGG, double2, double4, double6, world);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(final byte status) {
        if (status == 3) {
            final double double2 = 0.08;
            for (int integer4 = 0; integer4 < 8; ++integer4) {
                this.world.addParticle(new ItemStackParticleParameters(ParticleTypes.G, this.getStack()), this.x, this.y, this.z, (this.random.nextFloat() - 0.5) * 0.08, (this.random.nextFloat() - 0.5) * 0.08, (this.random.nextFloat() - 0.5) * 0.08);
            }
        }
    }
    
    @Override
    protected void onCollision(final HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            ((EntityHitResult)hitResult).getEntity().damage(DamageSource.thrownProjectile(this, this.getOwner()), 0.0f);
        }
        if (!this.world.isClient) {
            if (this.random.nextInt(8) == 0) {
                int integer2 = 1;
                if (this.random.nextInt(32) == 0) {
                    integer2 = 4;
                }
                for (int integer3 = 0; integer3 < integer2; ++integer3) {
                    final ChickenEntity chickenEntity4 = EntityType.CHICKEN.create(this.world);
                    chickenEntity4.setBreedingAge(-24000);
                    chickenEntity4.setPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0f);
                    this.world.spawnEntity(chickenEntity4);
                }
            }
            this.world.sendEntityStatus(this, (byte)3);
            this.remove();
        }
    }
    
    @Override
    protected Item getDefaultItem() {
        return Items.kW;
    }
}
