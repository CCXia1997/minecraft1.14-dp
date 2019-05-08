package net.minecraft.entity.mob;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public class StrayEntity extends AbstractSkeletonEntity
{
    public StrayEntity(final EntityType<? extends StrayEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    public boolean canSpawn(final IWorld iWorld, final SpawnType spawnType) {
        return super.canSpawn(iWorld, spawnType) && (spawnType == SpawnType.c || iWorld.isSkyVisible(new BlockPos(this)));
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.lv;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.lx;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.lw;
    }
    
    @Override
    SoundEvent getStepSound() {
        return SoundEvents.ly;
    }
    
    @Override
    protected ProjectileEntity createArrowProjectile(final ItemStack arrow, final float float2) {
        final ProjectileEntity projectileEntity3 = super.createArrowProjectile(arrow, float2);
        if (projectileEntity3 instanceof ArrowEntity) {
            ((ArrowEntity)projectileEntity3).addEffect(new StatusEffectInstance(StatusEffects.b, 600));
        }
        return projectileEntity3;
    }
}
