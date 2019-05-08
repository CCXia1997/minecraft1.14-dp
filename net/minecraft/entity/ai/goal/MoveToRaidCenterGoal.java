package net.minecraft.entity.ai.goal;

import java.util.Random;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Collection;
import net.minecraft.entity.raid.RaidManager;
import com.google.common.collect.Sets;
import net.minecraft.entity.raid.Raid;
import net.minecraft.world.Heightmap;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import java.util.EnumSet;
import net.minecraft.entity.raid.RaiderEntity;

public class MoveToRaidCenterGoal<T extends RaiderEntity> extends Goal
{
    private final T owner;
    
    public MoveToRaidCenterGoal(final T raiderEntity) {
        this.owner = raiderEntity;
        this.setControls(EnumSet.<Control>of(Control.a));
    }
    
    @Override
    public boolean canStart() {
        return this.owner.getTarget() == null && !this.owner.hasPassengers() && this.owner.hasActiveRaid() && !this.owner.getRaid().isFinished() && !((ServerWorld)this.owner.world).isNearOccupiedPointOfInterest(new BlockPos(this.owner));
    }
    
    @Override
    public boolean shouldContinue() {
        return this.owner.hasActiveRaid() && !this.owner.getRaid().isFinished() && this.owner.world instanceof ServerWorld && !((ServerWorld)this.owner.world).isNearOccupiedPointOfInterest(new BlockPos(this.owner));
    }
    
    @Override
    public void tick() {
        if (this.owner.hasActiveRaid()) {
            final Raid raid1 = this.owner.getRaid();
            if (this.owner.age % 20 == 0) {
                this.includeFreeRaiders(raid1);
            }
            if (!this.owner.isNavigating()) {
                Vec3d vec3d2 = new Vec3d(raid1.getCenter());
                final Vec3d vec3d3 = new Vec3d(this.owner.x, this.owner.y, this.owner.z);
                final Vec3d vec3d4 = vec3d3.subtract(vec3d2);
                vec3d2 = vec3d4.multiply(0.4).add(vec3d2);
                final Vec3d vec3d5 = vec3d2.subtract(vec3d3).normalize().multiply(10.0).add(vec3d3);
                BlockPos blockPos6 = new BlockPos(vec3d5);
                blockPos6 = this.owner.world.getTopPosition(Heightmap.Type.f, blockPos6);
                if (!this.owner.getNavigation().startMovingTo(blockPos6.getX(), blockPos6.getY(), blockPos6.getZ(), 1.0)) {
                    this.moveToAlternativePosition();
                }
            }
        }
    }
    
    private void includeFreeRaiders(final Raid raid) {
        if (raid.isActive()) {
            final Set<RaiderEntity> set2 = Sets.newHashSet();
            final List<RaiderEntity> list3 = this.owner.world.<RaiderEntity>getEntities(RaiderEntity.class, this.owner.getBoundingBox().expand(16.0), raiderEntity -> !raiderEntity.hasActiveRaid() && RaidManager.isValidRaiderFor(raiderEntity, raid));
            set2.addAll(list3);
            for (final RaiderEntity raiderEntity2 : set2) {
                raid.addRaider(raid.getGroupsSpawned(), raiderEntity2, null, true);
            }
        }
    }
    
    private void moveToAlternativePosition() {
        final Random random1 = this.owner.getRand();
        final BlockPos blockPos2 = this.owner.world.getTopPosition(Heightmap.Type.f, new BlockPos(this.owner).add(-8 + random1.nextInt(16), 0, -8 + random1.nextInt(16)));
        this.owner.getNavigation().startMovingTo(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ(), 1.0);
    }
}
