package net.minecraft.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public abstract class MobEntityWithAi extends MobEntity
{
    protected MobEntityWithAi(final EntityType<? extends MobEntityWithAi> type, final World world) {
        super(type, world);
    }
    
    public float getPathfindingFavor(final BlockPos blockPos) {
        return this.getPathfindingFavor(blockPos, this.world);
    }
    
    public float getPathfindingFavor(final BlockPos pos, final ViewableWorld world) {
        return 0.0f;
    }
    
    @Override
    public boolean canSpawn(final IWorld iWorld, final SpawnType spawnType) {
        return super.canSpawn(iWorld, spawnType) && this.getPathfindingFavor(new BlockPos(this.x, this.getBoundingBox().minY, this.z), iWorld) >= 0.0f;
    }
    
    public boolean isNavigating() {
        return !this.getNavigation().isIdle();
    }
    
    @Override
    protected void updateLeash() {
        super.updateLeash();
        final Entity entity1 = this.getHoldingEntity();
        if (entity1 != null && entity1.world == this.world) {
            this.setWalkTarget(new BlockPos((int)entity1.x, (int)entity1.y, (int)entity1.z), 5);
            final float float2 = this.distanceTo(entity1);
            if (this instanceof TameableEntity && ((TameableEntity)this).isSitting()) {
                if (float2 > 10.0f) {
                    this.detachLeash(true, true);
                }
                return;
            }
            this.updateForLeashLength(float2);
            if (float2 > 10.0f) {
                this.detachLeash(true, true);
                this.goalSelector.disableControl(Goal.Control.a);
            }
            else if (float2 > 6.0f) {
                final double double3 = (entity1.x - this.x) / float2;
                final double double4 = (entity1.y - this.y) / float2;
                final double double5 = (entity1.z - this.z) / float2;
                this.setVelocity(this.getVelocity().add(Math.copySign(double3 * double3 * 0.4, double3), Math.copySign(double4 * double4 * 0.4, double4), Math.copySign(double5 * double5 * 0.4, double5)));
            }
            else {
                this.goalSelector.enableControl(Goal.Control.a);
                final float float3 = 2.0f;
                final Vec3d vec3d4 = new Vec3d(entity1.x - this.x, entity1.y - this.y, entity1.z - this.z).normalize().multiply(Math.max(float2 - 2.0f, 0.0f));
                this.getNavigation().startMovingTo(this.x + vec3d4.x, this.y + vec3d4.y, this.z + vec3d4.z, this.getRunFromLeashSpeed());
            }
        }
    }
    
    protected double getRunFromLeashSpeed() {
        return 1.0;
    }
    
    protected void updateForLeashLength(final float leashLength) {
    }
}
