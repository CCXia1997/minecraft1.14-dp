package net.minecraft.entity.ai.goal;

import net.minecraft.util.math.Vec3i;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Position;
import net.minecraft.world.ViewableWorld;
import java.util.EnumSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.mob.MobEntityWithAi;

public abstract class MoveToTargetPosGoal extends Goal
{
    protected final MobEntityWithAi owner;
    public final double speed;
    protected int cooldown;
    protected int tryingTime;
    private int safeWaitingTime;
    protected BlockPos targetPos;
    private boolean reached;
    private final int range;
    private final int maxYDifference;
    protected int lowestY;
    
    public MoveToTargetPosGoal(final MobEntityWithAi owner, final double speed, final int range) {
        this(owner, speed, range, 1);
    }
    
    public MoveToTargetPosGoal(final MobEntityWithAi owner, final double speed, final int range, final int maxYDifference) {
        this.targetPos = BlockPos.ORIGIN;
        this.owner = owner;
        this.speed = speed;
        this.range = range;
        this.lowestY = 0;
        this.maxYDifference = maxYDifference;
        this.setControls(EnumSet.<Control>of(Control.a, Control.c));
    }
    
    @Override
    public boolean canStart() {
        if (this.cooldown > 0) {
            --this.cooldown;
            return false;
        }
        this.cooldown = this.getInterval(this.owner);
        return this.findTargetPos();
    }
    
    protected int getInterval(final MobEntityWithAi mob) {
        return 200 + mob.getRand().nextInt(200);
    }
    
    @Override
    public boolean shouldContinue() {
        return this.tryingTime >= -this.safeWaitingTime && this.tryingTime <= 1200 && this.isTargetPos(this.owner.world, this.targetPos);
    }
    
    @Override
    public void start() {
        this.startMovingToTarget();
        this.tryingTime = 0;
        this.safeWaitingTime = this.owner.getRand().nextInt(this.owner.getRand().nextInt(1200) + 1200) + 1200;
    }
    
    protected void startMovingToTarget() {
        this.owner.getNavigation().startMovingTo((float)this.targetPos.getX() + 0.5, this.targetPos.getY() + 1, (float)this.targetPos.getZ() + 0.5, this.speed);
    }
    
    public double getDesiredSquaredDistanceToTarget() {
        return 1.0;
    }
    
    @Override
    public void tick() {
        if (!this.targetPos.up().isWithinDistance(this.owner.getPos(), this.getDesiredSquaredDistanceToTarget())) {
            this.reached = false;
            ++this.tryingTime;
            if (this.shouldResetPath()) {
                this.owner.getNavigation().startMovingTo((float)this.targetPos.getX() + 0.5, this.targetPos.getY() + 1, (float)this.targetPos.getZ() + 0.5, this.speed);
            }
        }
        else {
            this.reached = true;
            --this.tryingTime;
        }
    }
    
    public boolean shouldResetPath() {
        return this.tryingTime % 40 == 0;
    }
    
    protected boolean hasReached() {
        return this.reached;
    }
    
    protected boolean findTargetPos() {
        final int integer1 = this.range;
        final int integer2 = this.maxYDifference;
        final BlockPos blockPos3 = new BlockPos(this.owner);
        final BlockPos.Mutable mutable4 = new BlockPos.Mutable();
        for (int integer3 = this.lowestY; integer3 <= integer2; integer3 = ((integer3 > 0) ? (-integer3) : (1 - integer3))) {
            for (int integer4 = 0; integer4 < integer1; ++integer4) {
                for (int integer5 = 0; integer5 <= integer4; integer5 = ((integer5 > 0) ? (-integer5) : (1 - integer5))) {
                    for (int integer6 = (integer5 < integer4 && integer5 > -integer4) ? integer4 : 0; integer6 <= integer4; integer6 = ((integer6 > 0) ? (-integer6) : (1 - integer6))) {
                        mutable4.set(blockPos3).setOffset(integer5, integer3 - 1, integer6);
                        if (this.owner.isInWalkTargetRange(mutable4) && this.isTargetPos(this.owner.world, mutable4)) {
                            this.targetPos = mutable4;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    protected abstract boolean isTargetPos(final ViewableWorld arg1, final BlockPos arg2);
}
