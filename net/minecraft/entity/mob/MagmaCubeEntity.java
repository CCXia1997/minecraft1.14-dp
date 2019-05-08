package net.minecraft.entity.mob;

import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.tag.FluidTags;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.loot.LootTables;
import net.minecraft.util.Identifier;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticleParameters;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.Difficulty;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.IWorld;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public class MagmaCubeEntity extends SlimeEntity
{
    public MagmaCubeEntity(final EntityType<? extends MagmaCubeEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224);
    }
    
    @Override
    public boolean canSpawn(final IWorld iWorld, final SpawnType spawnType) {
        return iWorld.getDifficulty() != Difficulty.PEACEFUL;
    }
    
    @Override
    public boolean canSpawn(final ViewableWorld world) {
        return world.intersectsEntities(this) && !world.intersectsFluid(this.getBoundingBox());
    }
    
    @Override
    protected void setSize(final int size, final boolean heal) {
        super.setSize(size, heal);
        this.getAttributeInstance(EntityAttributes.ARMOR).setBaseValue(size * 3);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public int getLightmapCoordinates() {
        return 15728880;
    }
    
    @Override
    public float getBrightnessAtEyes() {
        return 1.0f;
    }
    
    @Override
    protected ParticleParameters getParticles() {
        return ParticleTypes.A;
    }
    
    @Override
    protected Identifier getLootTableId() {
        return this.isSmall() ? LootTables.EMPTY : this.getType().getLootTableId();
    }
    
    @Override
    public boolean isOnFire() {
        return false;
    }
    
    @Override
    protected int getTicksUntilNextJump() {
        return super.getTicksUntilNextJump() * 4;
    }
    
    @Override
    protected void updateStretch() {
        this.targetStretch *= 0.9f;
    }
    
    @Override
    protected void jump() {
        final Vec3d vec3d1 = this.getVelocity();
        this.setVelocity(vec3d1.x, 0.42f + this.getSize() * 0.1f, vec3d1.z);
        this.velocityDirty = true;
    }
    
    @Override
    protected void swimUpward(final Tag<Fluid> fluid) {
        if (fluid == FluidTags.b) {
            final Vec3d vec3d2 = this.getVelocity();
            this.setVelocity(vec3d2.x, 0.22f + this.getSize() * 0.05f, vec3d2.z);
            this.velocityDirty = true;
        }
        else {
            super.swimUpward(fluid);
        }
    }
    
    @Override
    public void handleFallDamage(final float fallDistance, final float damageMultiplier) {
    }
    
    @Override
    protected boolean isBig() {
        return this.canMoveVoluntarily();
    }
    
    @Override
    protected int getDamageAmount() {
        return super.getDamageAmount() + 2;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        if (this.isSmall()) {
            return SoundEvents.kL;
        }
        return SoundEvents.gj;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        if (this.isSmall()) {
            return SoundEvents.kK;
        }
        return SoundEvents.gi;
    }
    
    @Override
    protected SoundEvent getSquishSound() {
        if (this.isSmall()) {
            return SoundEvents.kM;
        }
        return SoundEvents.gl;
    }
    
    @Override
    protected SoundEvent getJumpSound() {
        return SoundEvents.gk;
    }
}
