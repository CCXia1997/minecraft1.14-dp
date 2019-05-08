package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.Entity;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import java.util.EnumSet;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import java.util.function.Predicate;
import net.minecraft.entity.mob.MobEntity;

public class FollowMobGoal extends Goal
{
    private final MobEntity a;
    private final Predicate<MobEntity> b;
    private MobEntity c;
    private final double d;
    private final EntityNavigation e;
    private int f;
    private final float g;
    private float h;
    private final float i;
    
    public FollowMobGoal(final MobEntity mobEntity, final double double2, final float float4, final float float5) {
        this.a = mobEntity;
        this.b = (mobEntity2 -> mobEntity2 != null && mobEntity.getClass() != mobEntity2.getClass());
        this.d = double2;
        this.e = mobEntity.getNavigation();
        this.g = float4;
        this.i = float5;
        this.setControls(EnumSet.<Control>of(Control.a, Control.b));
        if (!(mobEntity.getNavigation() instanceof MobNavigation) && !(mobEntity.getNavigation() instanceof BirdNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
        }
    }
    
    @Override
    public boolean canStart() {
        final List<MobEntity> list1 = this.a.world.<MobEntity>getEntities(MobEntity.class, this.a.getBoundingBox().expand(this.i), this.b);
        if (!list1.isEmpty()) {
            for (final MobEntity mobEntity3 : list1) {
                if (mobEntity3.isInvisible()) {
                    continue;
                }
                this.c = mobEntity3;
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean shouldContinue() {
        return this.c != null && !this.e.isIdle() && this.a.squaredDistanceTo(this.c) > this.g * this.g;
    }
    
    @Override
    public void start() {
        this.f = 0;
        this.h = this.a.getPathNodeTypeWeight(PathNodeType.g);
        this.a.setPathNodeTypeWeight(PathNodeType.g, 0.0f);
    }
    
    @Override
    public void stop() {
        this.c = null;
        this.e.stop();
        this.a.setPathNodeTypeWeight(PathNodeType.g, this.h);
    }
    
    @Override
    public void tick() {
        if (this.c == null || this.a.isLeashed()) {
            return;
        }
        this.a.getLookControl().lookAt(this.c, 10.0f, (float)this.a.getLookPitchSpeed());
        if (--this.f > 0) {
            return;
        }
        this.f = 10;
        final double double1 = this.a.x - this.c.x;
        final double double2 = this.a.y - this.c.y;
        final double double3 = this.a.z - this.c.z;
        final double double4 = double1 * double1 + double2 * double2 + double3 * double3;
        if (double4 <= this.g * this.g) {
            this.e.stop();
            final LookControl lookControl9 = this.c.getLookControl();
            if (double4 <= this.g || (lookControl9.getLookX() == this.a.x && lookControl9.getLookY() == this.a.y && lookControl9.getLookZ() == this.a.z)) {
                final double double5 = this.c.x - this.a.x;
                final double double6 = this.c.z - this.a.z;
                this.e.startMovingTo(this.a.x - double5, this.a.y, this.a.z - double6, this.d);
            }
            return;
        }
        this.e.startMovingTo(this.c, this.d);
    }
}
