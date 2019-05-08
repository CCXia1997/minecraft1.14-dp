package net.minecraft.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public class SpectralArrowEntity extends ProjectileEntity
{
    private int duration;
    
    public SpectralArrowEntity(final EntityType<? extends SpectralArrowEntity> type, final World world) {
        super(type, world);
        this.duration = 200;
    }
    
    public SpectralArrowEntity(final World world, final LivingEntity livingEntity) {
        super(EntityType.SPECTRAL_ARROW, livingEntity, world);
        this.duration = 200;
    }
    
    public SpectralArrowEntity(final World world, final double double2, final double double4, final double double6) {
        super(EntityType.SPECTRAL_ARROW, double2, double4, double6, world);
        this.duration = 200;
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.world.isClient && !this.inGround) {
            this.world.addParticle(ParticleTypes.F, this.x, this.y, this.z, 0.0, 0.0, 0.0);
        }
    }
    
    @Override
    protected ItemStack asItemStack() {
        return new ItemStack(Items.oT);
    }
    
    @Override
    protected void onHit(final LivingEntity livingEntity) {
        super.onHit(livingEntity);
        final StatusEffectInstance statusEffectInstance2 = new StatusEffectInstance(StatusEffects.x, this.duration, 0);
        livingEntity.addPotionEffect(statusEffectInstance2);
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.containsKey("Duration")) {
            this.duration = tag.getInt("Duration");
        }
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("Duration", this.duration);
    }
}
