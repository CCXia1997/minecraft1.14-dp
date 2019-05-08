package net.minecraft.entity.ai.goal;

import net.minecraft.util.math.Vec3d;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import java.util.EnumSet;
import net.minecraft.entity.passive.LlamaEntity;

public class FormCaravanGoal extends Goal
{
    public final LlamaEntity owner;
    private double speed;
    private int counter;
    
    public FormCaravanGoal(final LlamaEntity llamaEntity, final double double2) {
        this.owner = llamaEntity;
        this.speed = double2;
        this.setControls(EnumSet.<Control>of(Control.a));
    }
    
    @Override
    public boolean canStart() {
        if (this.owner.isLeashed() || this.owner.isFollowing()) {
            return false;
        }
        final EntityType<?> entityType2;
        final List<Entity> list1 = this.owner.world.getEntities(this.owner, this.owner.getBoundingBox().expand(9.0, 4.0, 9.0), entity -> {
            entityType2 = entity.getType();
            return entityType2 == EntityType.LLAMA || entityType2 == EntityType.ax;
        });
        LlamaEntity llamaEntity2 = null;
        double double3 = Double.MAX_VALUE;
        for (final Entity entity2 : list1) {
            final LlamaEntity llamaEntity3 = (LlamaEntity)entity2;
            if (llamaEntity3.isFollowing()) {
                if (llamaEntity3.eH()) {
                    continue;
                }
                final double double4 = this.owner.squaredDistanceTo(llamaEntity3);
                if (double4 > double3) {
                    continue;
                }
                double3 = double4;
                llamaEntity2 = llamaEntity3;
            }
        }
        if (llamaEntity2 == null) {
            for (final Entity entity2 : list1) {
                final LlamaEntity llamaEntity3 = (LlamaEntity)entity2;
                if (!llamaEntity3.isLeashed()) {
                    continue;
                }
                if (llamaEntity3.eH()) {
                    continue;
                }
                final double double4 = this.owner.squaredDistanceTo(llamaEntity3);
                if (double4 > double3) {
                    continue;
                }
                double3 = double4;
                llamaEntity2 = llamaEntity3;
            }
        }
        if (llamaEntity2 == null) {
            return false;
        }
        if (double3 < 4.0) {
            return false;
        }
        if (!llamaEntity2.isLeashed() && !this.canFollow(llamaEntity2, 1)) {
            return false;
        }
        this.owner.a(llamaEntity2);
        return true;
    }
    
    @Override
    public boolean shouldContinue() {
        if (!this.owner.isFollowing() || !this.owner.getFollowing().isAlive() || !this.canFollow(this.owner, 0)) {
            return false;
        }
        final double double1 = this.owner.squaredDistanceTo(this.owner.getFollowing());
        if (double1 > 676.0) {
            if (this.speed <= 3.0) {
                this.speed *= 1.2;
                this.counter = 40;
                return true;
            }
            if (this.counter == 0) {
                return false;
            }
        }
        if (this.counter > 0) {
            --this.counter;
        }
        return true;
    }
    
    @Override
    public void stop() {
        this.owner.eG();
        this.speed = 2.1;
    }
    
    @Override
    public void tick() {
        if (!this.owner.isFollowing()) {
            return;
        }
        final LlamaEntity llamaEntity1 = this.owner.getFollowing();
        final double double2 = this.owner.distanceTo(llamaEntity1);
        final float float4 = 2.0f;
        final Vec3d vec3d5 = new Vec3d(llamaEntity1.x - this.owner.x, llamaEntity1.y - this.owner.y, llamaEntity1.z - this.owner.z).normalize().multiply(Math.max(double2 - 2.0, 0.0));
        this.owner.getNavigation().startMovingTo(this.owner.x + vec3d5.x, this.owner.y + vec3d5.y, this.owner.z + vec3d5.z, this.speed);
    }
    
    private boolean canFollow(final LlamaEntity llamaEntity, int length) {
        return length <= 8 && llamaEntity.isFollowing() && (llamaEntity.getFollowing().isLeashed() || this.canFollow(llamaEntity.getFollowing(), ++length));
    }
}
