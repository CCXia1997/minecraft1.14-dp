package net.minecraft.entity.mob;

import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public abstract class FlyingEntity extends MobEntity
{
    protected FlyingEntity(final EntityType<? extends FlyingEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    public void handleFallDamage(final float fallDistance, final float damageMultiplier) {
    }
    
    @Override
    protected void fall(final double heightDifference, final boolean onGround, final BlockState blockState, final BlockPos blockPos) {
    }
    
    @Override
    public void travel(final Vec3d movementInput) {
        if (this.isInsideWater()) {
            this.updateVelocity(0.02f, movementInput);
            this.move(MovementType.a, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.800000011920929));
        }
        else if (this.isTouchingLava()) {
            this.updateVelocity(0.02f, movementInput);
            this.move(MovementType.a, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.5));
        }
        else {
            float float2 = 0.91f;
            if (this.onGround) {
                float2 = this.world.getBlockState(new BlockPos(this.x, this.getBoundingBox().minY - 1.0, this.z)).getBlock().getFrictionCoefficient() * 0.91f;
            }
            final float float3 = 0.16277137f / (float2 * float2 * float2);
            float2 = 0.91f;
            if (this.onGround) {
                float2 = this.world.getBlockState(new BlockPos(this.x, this.getBoundingBox().minY - 1.0, this.z)).getBlock().getFrictionCoefficient() * 0.91f;
            }
            this.updateVelocity(this.onGround ? (0.1f * float3) : 0.02f, movementInput);
            this.move(MovementType.a, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(float2));
        }
        this.lastLimbDistance = this.limbDistance;
        final double double2 = this.x - this.prevX;
        final double double3 = this.z - this.prevZ;
        float float4 = MathHelper.sqrt(double2 * double2 + double3 * double3) * 4.0f;
        if (float4 > 1.0f) {
            float4 = 1.0f;
        }
        this.limbDistance += (float4 - this.limbDistance) * 0.4f;
        this.limbAngle += this.limbDistance;
    }
    
    @Override
    public boolean isClimbing() {
        return false;
    }
}
