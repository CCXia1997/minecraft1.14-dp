package net.minecraft.entity.ai.goal;

import net.minecraft.util.math.Direction;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntityWithAi;

public class ChaseBoatGoal extends Goal
{
    private int a;
    private final MobEntityWithAi owner;
    private LivingEntity passenger;
    private ChaseBoatState state;
    
    public ChaseBoatGoal(final MobEntityWithAi owner) {
        this.owner = owner;
    }
    
    @Override
    public boolean canStart() {
        final List<BoatEntity> list1 = this.owner.world.<BoatEntity>getEntities(BoatEntity.class, this.owner.getBoundingBox().expand(5.0));
        boolean boolean2 = false;
        for (final BoatEntity boatEntity4 : list1) {
            if (boatEntity4.getPrimaryPassenger() != null && (MathHelper.abs(((LivingEntity)boatEntity4.getPrimaryPassenger()).sidewaysSpeed) > 0.0f || MathHelper.abs(((LivingEntity)boatEntity4.getPrimaryPassenger()).forwardSpeed) > 0.0f)) {
                boolean2 = true;
                break;
            }
        }
        return (this.passenger != null && (MathHelper.abs(this.passenger.sidewaysSpeed) > 0.0f || MathHelper.abs(this.passenger.forwardSpeed) > 0.0f)) || boolean2;
    }
    
    @Override
    public boolean canStop() {
        return true;
    }
    
    @Override
    public boolean shouldContinue() {
        return this.passenger != null && this.passenger.hasVehicle() && (MathHelper.abs(this.passenger.sidewaysSpeed) > 0.0f || MathHelper.abs(this.passenger.forwardSpeed) > 0.0f);
    }
    
    @Override
    public void start() {
        final List<BoatEntity> list1 = this.owner.world.<BoatEntity>getEntities(BoatEntity.class, this.owner.getBoundingBox().expand(5.0));
        for (final BoatEntity boatEntity3 : list1) {
            if (boatEntity3.getPrimaryPassenger() != null && boatEntity3.getPrimaryPassenger() instanceof LivingEntity) {
                this.passenger = (LivingEntity)boatEntity3.getPrimaryPassenger();
                break;
            }
        }
        this.a = 0;
        this.state = ChaseBoatState.a;
    }
    
    @Override
    public void stop() {
        this.passenger = null;
    }
    
    @Override
    public void tick() {
        final boolean boolean1 = MathHelper.abs(this.passenger.sidewaysSpeed) > 0.0f || MathHelper.abs(this.passenger.forwardSpeed) > 0.0f;
        final float float2 = (this.state == ChaseBoatState.b) ? (boolean1 ? 0.17999999f : 0.0f) : 0.135f;
        this.owner.updateVelocity(float2, new Vec3d(this.owner.sidewaysSpeed, this.owner.upwardSpeed, this.owner.forwardSpeed));
        this.owner.move(MovementType.a, this.owner.getVelocity());
        final int a = this.a - 1;
        this.a = a;
        if (a > 0) {
            return;
        }
        this.a = 10;
        if (this.state == ChaseBoatState.a) {
            BlockPos blockPos3 = new BlockPos(this.passenger).offset(this.passenger.getHorizontalFacing().getOpposite());
            blockPos3 = blockPos3.add(0, -1, 0);
            this.owner.getNavigation().startMovingTo(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ(), 1.0);
            if (this.owner.distanceTo(this.passenger) < 4.0f) {
                this.a = 0;
                this.state = ChaseBoatState.b;
            }
        }
        else if (this.state == ChaseBoatState.b) {
            final Direction direction3 = this.passenger.getMovementDirection();
            final BlockPos blockPos4 = new BlockPos(this.passenger).offset(direction3, 10);
            this.owner.getNavigation().startMovingTo(blockPos4.getX(), blockPos4.getY() - 1, blockPos4.getZ(), 1.0);
            if (this.owner.distanceTo(this.passenger) > 12.0f) {
                this.a = 0;
                this.state = ChaseBoatState.a;
            }
        }
    }
}
