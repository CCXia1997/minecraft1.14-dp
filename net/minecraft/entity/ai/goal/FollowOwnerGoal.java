package net.minecraft.entity.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import java.util.EnumSet;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.world.ViewableWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;

public class FollowOwnerGoal extends Goal
{
    protected final TameableEntity caller;
    private LivingEntity owner;
    protected final ViewableWorld world;
    private final double d;
    private final EntityNavigation e;
    private int f;
    private final float g;
    private final float minDistance;
    private float i;
    
    public FollowOwnerGoal(final TameableEntity caller, final double double2, final float float4, final float float5) {
        this.caller = caller;
        this.world = caller.world;
        this.d = double2;
        this.e = caller.getNavigation();
        this.minDistance = float4;
        this.g = float5;
        this.setControls(EnumSet.<Control>of(Control.a, Control.b));
        if (!(caller.getNavigation() instanceof MobNavigation) && !(caller.getNavigation() instanceof BirdNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }
    
    @Override
    public boolean canStart() {
        final LivingEntity livingEntity1 = this.caller.getOwner();
        if (livingEntity1 == null) {
            return false;
        }
        if (livingEntity1 instanceof PlayerEntity && ((PlayerEntity)livingEntity1).isSpectator()) {
            return false;
        }
        if (this.caller.isSitting()) {
            return false;
        }
        if (this.caller.squaredDistanceTo(livingEntity1) < this.minDistance * this.minDistance) {
            return false;
        }
        this.owner = livingEntity1;
        return true;
    }
    
    @Override
    public boolean shouldContinue() {
        return !this.e.isIdle() && this.caller.squaredDistanceTo(this.owner) > this.g * this.g && !this.caller.isSitting();
    }
    
    @Override
    public void start() {
        this.f = 0;
        this.i = this.caller.getPathNodeTypeWeight(PathNodeType.g);
        this.caller.setPathNodeTypeWeight(PathNodeType.g, 0.0f);
    }
    
    @Override
    public void stop() {
        this.owner = null;
        this.e.stop();
        this.caller.setPathNodeTypeWeight(PathNodeType.g, this.i);
    }
    
    @Override
    public void tick() {
        this.caller.getLookControl().lookAt(this.owner, 10.0f, (float)this.caller.getLookPitchSpeed());
        if (this.caller.isSitting()) {
            return;
        }
        if (--this.f > 0) {
            return;
        }
        this.f = 10;
        if (this.e.startMovingTo(this.owner, this.d)) {
            return;
        }
        if (this.caller.isLeashed() || this.caller.hasVehicle()) {
            return;
        }
        if (this.caller.squaredDistanceTo(this.owner) < 144.0) {
            return;
        }
        final int integer1 = MathHelper.floor(this.owner.x) - 2;
        final int integer2 = MathHelper.floor(this.owner.z) - 2;
        final int integer3 = MathHelper.floor(this.owner.getBoundingBox().minY);
        for (int integer4 = 0; integer4 <= 4; ++integer4) {
            for (int integer5 = 0; integer5 <= 4; ++integer5) {
                if (integer4 < 1 || integer5 < 1 || integer4 > 3 || integer5 > 3) {
                    if (this.a(new BlockPos(integer1 + integer4, integer3 - 1, integer2 + integer5))) {
                        this.caller.setPositionAndAngles(integer1 + integer4 + 0.5f, integer3, integer2 + integer5 + 0.5f, this.caller.yaw, this.caller.pitch);
                        this.e.stop();
                        return;
                    }
                }
            }
        }
    }
    
    protected boolean a(final BlockPos blockPos) {
        final BlockState blockState2 = this.world.getBlockState(blockPos);
        return blockState2.allowsSpawning(this.world, blockPos, this.caller.getType()) && this.world.isAir(blockPos.up()) && this.world.isAir(blockPos.up(2));
    }
}
