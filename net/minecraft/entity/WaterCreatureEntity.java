package net.minecraft.entity;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.ViewableWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.entity.mob.MobEntityWithAi;

public abstract class WaterCreatureEntity extends MobEntityWithAi
{
    protected WaterCreatureEntity(final EntityType<? extends WaterCreatureEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    public boolean canBreatheInWater() {
        return true;
    }
    
    @Override
    public EntityGroup getGroup() {
        return EntityGroup.AQUATIC;
    }
    
    @Override
    protected boolean a(final IWorld iWorld, final SpawnType spawnType, final BlockPos blockPos) {
        return iWorld.getFluidState(blockPos).matches(FluidTags.a);
    }
    
    @Override
    public boolean canSpawn(final ViewableWorld world) {
        return world.intersectsEntities(this);
    }
    
    @Override
    public int getMinAmbientSoundDelay() {
        return 120;
    }
    
    @Override
    public boolean canImmediatelyDespawn(final double distanceSquared) {
        return true;
    }
    
    @Override
    protected int getCurrentExperience(final PlayerEntity playerEntity) {
        return 1 + this.world.random.nextInt(3);
    }
    
    protected void tickBreath(final int breath) {
        if (this.isAlive() && !this.isInsideWaterOrBubbleColumn()) {
            this.setBreath(breath - 1);
            if (this.getBreath() == -20) {
                this.setBreath(0);
                this.damage(DamageSource.DROWN, 2.0f);
            }
        }
        else {
            this.setBreath(300);
        }
    }
    
    @Override
    public void baseTick() {
        final int integer1 = this.getBreath();
        super.baseTick();
        this.tickBreath(integer1);
    }
    
    @Override
    public boolean canFly() {
        return false;
    }
    
    @Override
    public boolean canBeLeashedBy(final PlayerEntity player) {
        return false;
    }
}
