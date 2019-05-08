package net.minecraft.entity.thrown;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public class SnowballEntity extends ThrownItemEntity
{
    public SnowballEntity(final EntityType<? extends SnowballEntity> type, final World world) {
        super(type, world);
    }
    
    public SnowballEntity(final World world, final LivingEntity livingEntity) {
        super(EntityType.SNOWBALL, livingEntity, world);
    }
    
    public SnowballEntity(final World world, final double double2, final double double4, final double double6) {
        super(EntityType.SNOWBALL, double2, double4, double6, world);
    }
    
    @Override
    protected Item getDefaultItem() {
        return Items.kD;
    }
    
    @Environment(EnvType.CLIENT)
    private ParticleParameters getParticleParameters() {
        final ItemStack itemStack1 = this.getItem();
        return itemStack1.isEmpty() ? ParticleTypes.I : new ItemStackParticleParameters(ParticleTypes.G, itemStack1);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(final byte status) {
        if (status == 3) {
            final ParticleParameters particleParameters2 = this.getParticleParameters();
            for (int integer3 = 0; integer3 < 8; ++integer3) {
                this.world.addParticle(particleParameters2, this.x, this.y, this.z, 0.0, 0.0, 0.0);
            }
        }
    }
    
    @Override
    protected void onCollision(final HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            final Entity entity2 = ((EntityHitResult)hitResult).getEntity();
            final int integer3 = (entity2 instanceof BlazeEntity) ? 3 : 0;
            entity2.damage(DamageSource.thrownProjectile(this, this.getOwner()), (float)integer3);
        }
        if (!this.world.isClient) {
            this.world.sendEntityStatus(this, (byte)3);
            this.remove();
        }
    }
}
