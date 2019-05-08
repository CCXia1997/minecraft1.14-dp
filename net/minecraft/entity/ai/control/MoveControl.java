package net.minecraft.entity.ai.control;

import net.minecraft.util.shape.VoxelShape;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;

public class MoveControl
{
    protected final MobEntity entity;
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected double speed;
    protected float f;
    protected float g;
    protected State state;
    
    public MoveControl(final MobEntity entity) {
        this.state = State.a;
        this.entity = entity;
    }
    
    public boolean isMoving() {
        return this.state == State.b;
    }
    
    public double getSpeed() {
        return this.speed;
    }
    
    public void moveTo(final double x, final double y, final double z, final double speed) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
        this.speed = speed;
        if (this.state != State.d) {
            this.state = State.b;
        }
    }
    
    public void a(final float float1, final float float2) {
        this.state = State.c;
        this.f = float1;
        this.g = float2;
        this.speed = 0.25;
    }
    
    public void tick() {
        if (this.state == State.c) {
            final float float1 = (float)this.entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue();
            float float2 = (float)this.speed * float1;
            float float3 = this.f;
            float float4 = this.g;
            float float5 = MathHelper.sqrt(float3 * float3 + float4 * float4);
            if (float5 < 1.0f) {
                float5 = 1.0f;
            }
            float5 = float2 / float5;
            float3 *= float5;
            float4 *= float5;
            final float float6 = MathHelper.sin(this.entity.yaw * 0.017453292f);
            final float float7 = MathHelper.cos(this.entity.yaw * 0.017453292f);
            final float float8 = float3 * float7 - float4 * float6;
            final float float9 = float4 * float7 + float3 * float6;
            final EntityNavigation entityNavigation10 = this.entity.getNavigation();
            if (entityNavigation10 != null) {
                final PathNodeMaker pathNodeMaker11 = entityNavigation10.getNodeMaker();
                if (pathNodeMaker11 != null && pathNodeMaker11.getPathNodeType(this.entity.world, MathHelper.floor(this.entity.x + float8), MathHelper.floor(this.entity.y), MathHelper.floor(this.entity.z + float9)) != PathNodeType.c) {
                    this.f = 1.0f;
                    this.g = 0.0f;
                    float2 = float1;
                }
            }
            this.entity.setMovementSpeed(float2);
            this.entity.setForwardSpeed(this.f);
            this.entity.setSidewaysSpeed(this.g);
            this.state = State.a;
        }
        else if (this.state == State.b) {
            this.state = State.a;
            final double double1 = this.targetX - this.entity.x;
            final double double2 = this.targetZ - this.entity.z;
            final double double3 = this.targetY - this.entity.y;
            final double double4 = double1 * double1 + double3 * double3 + double2 * double2;
            if (double4 < 2.500000277905201E-7) {
                this.entity.setForwardSpeed(0.0f);
                return;
            }
            final float float9 = (float)(MathHelper.atan2(double2, double1) * 57.2957763671875) - 90.0f;
            this.entity.yaw = this.changeAngle(this.entity.yaw, float9, 90.0f);
            this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue()));
            final BlockPos blockPos10 = new BlockPos(this.entity.x, this.entity.y, this.entity.z);
            final BlockState blockState11 = this.entity.world.getBlockState(blockPos10);
            final VoxelShape voxelShape12 = blockState11.getCollisionShape(this.entity.world, blockPos10);
            if ((double3 > this.entity.stepHeight && double1 * double1 + double2 * double2 < Math.max(1.0f, this.entity.getWidth())) || (!voxelShape12.isEmpty() && this.entity.y < voxelShape12.getMaximum(Direction.Axis.Y) + blockPos10.getY())) {
                this.entity.getJumpControl().setActive();
                this.state = State.d;
            }
        }
        else if (this.state == State.d) {
            this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue()));
            if (this.entity.onGround) {
                this.state = State.a;
            }
        }
        else {
            this.entity.setForwardSpeed(0.0f);
        }
    }
    
    protected float changeAngle(final float from, final float to, final float max) {
        float float4 = MathHelper.wrapDegrees(to - from);
        if (float4 > max) {
            float4 = max;
        }
        if (float4 < -max) {
            float4 = -max;
        }
        float float5 = from + float4;
        if (float5 < 0.0f) {
            float5 += 360.0f;
        }
        else if (float5 > 360.0f) {
            float5 -= 360.0f;
        }
        return float5;
    }
    
    public double getTargetX() {
        return this.targetX;
    }
    
    public double getTargetY() {
        return this.targetY;
    }
    
    public double getTargetZ() {
        return this.targetZ;
    }
    
    public enum State
    {
        a, 
        b, 
        c, 
        d;
    }
}
