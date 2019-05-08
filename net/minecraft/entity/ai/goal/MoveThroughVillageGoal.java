package net.minecraft.entity.ai.goal;

import java.util.Iterator;
import java.util.Objects;
import net.minecraft.util.math.Position;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.util.math.Vec3i;
import java.util.Optional;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.ai.pathing.MobNavigation;
import java.util.EnumSet;
import com.google.common.collect.Lists;
import java.util.function.BooleanSupplier;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntityWithAi;

public class MoveThroughVillageGoal extends Goal
{
    protected final MobEntityWithAi a;
    private final double b;
    private Path c;
    private BlockPos d;
    private final boolean e;
    private final List<BlockPos> f;
    private final int g;
    private final BooleanSupplier h;
    
    public MoveThroughVillageGoal(final MobEntityWithAi mobEntityWithAi, final double double2, final boolean boolean4, final int integer, final BooleanSupplier booleanSupplier6) {
        this.f = Lists.newArrayList();
        this.a = mobEntityWithAi;
        this.b = double2;
        this.e = boolean4;
        this.g = integer;
        this.h = booleanSupplier6;
        this.setControls(EnumSet.<Control>of(Control.a));
        if (!(mobEntityWithAi.getNavigation() instanceof MobNavigation)) {
            throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
        }
    }
    
    @Override
    public boolean canStart() {
        this.g();
        if (this.e && this.a.world.isDaylight()) {
            return false;
        }
        final ServerWorld serverWorld1 = (ServerWorld)this.a.world;
        final BlockPos blockPos4 = new BlockPos(this.a);
        if (!serverWorld1.isNearOccupiedPointOfInterest(blockPos4, 6)) {
            return false;
        }
        final ServerWorld serverWorld2;
        Optional<BlockPos> optional4;
        final Vec3i vec;
        final Vec3d vec3d3 = PathfindingUtil.findTargetStraight(this.a, 15, 7, blockPos3 -> {
            if (!serverWorld2.isNearOccupiedPointOfInterest(blockPos3)) {
                return Double.NEGATIVE_INFINITY;
            }
            else {
                optional4 = serverWorld2.getPointOfInterestStorage().getPosition(PointOfInterestType.ALWAYS_TRUE, this::a, blockPos3, 10, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED);
                if (!optional4.isPresent()) {
                    return Double.NEGATIVE_INFINITY;
                }
                else {
                    return -optional4.get().getSquaredDistance(vec);
                }
            }
        });
        if (vec3d3 == null) {
            return false;
        }
        final Optional<BlockPos> optional5 = serverWorld1.getPointOfInterestStorage().getPosition(PointOfInterestType.ALWAYS_TRUE, this::a, new BlockPos(vec3d3), 10, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED);
        if (!optional5.isPresent()) {
            return false;
        }
        this.d = optional5.get().toImmutable();
        final MobNavigation mobNavigation5 = (MobNavigation)this.a.getNavigation();
        final boolean boolean6 = mobNavigation5.canEnterOpenDoors();
        mobNavigation5.setCanPathThroughDoors(this.h.getAsBoolean());
        this.c = mobNavigation5.findPathTo(this.d);
        mobNavigation5.setCanPathThroughDoors(boolean6);
        if (this.c == null) {
            final Vec3d vec3d4 = PathfindingUtil.a(this.a, 10, 7, new Vec3d(this.d.getX(), this.d.getY(), this.d.getZ()));
            if (vec3d4 == null) {
                return false;
            }
            mobNavigation5.setCanPathThroughDoors(this.h.getAsBoolean());
            this.c = this.a.getNavigation().findPathTo(vec3d4.x, vec3d4.y, vec3d4.z);
            mobNavigation5.setCanPathThroughDoors(boolean6);
            if (this.c == null) {
                return false;
            }
        }
        for (int integer7 = 0; integer7 < this.c.getLength(); ++integer7) {
            final PathNode pathNode8 = this.c.getNode(integer7);
            final BlockPos blockPos5 = new BlockPos(pathNode8.x, pathNode8.y + 1, pathNode8.z);
            if (DoorInteractGoal.getDoor(this.a.world, blockPos5)) {
                this.c = this.a.getNavigation().findPathTo(pathNode8.x, pathNode8.y, pathNode8.z);
                break;
            }
        }
        return this.c != null;
    }
    
    @Override
    public boolean shouldContinue() {
        return !this.a.getNavigation().isIdle() && !this.d.isWithinDistance(this.a.getPos(), this.a.getWidth() + this.g);
    }
    
    @Override
    public void start() {
        this.a.getNavigation().startMovingAlong(this.c, this.b);
    }
    
    @Override
    public void stop() {
        if (this.a.getNavigation().isIdle() || this.d.isWithinDistance(this.a.getPos(), this.g)) {
            this.f.add(this.d);
        }
    }
    
    private boolean a(final BlockPos blockPos) {
        for (final BlockPos blockPos2 : this.f) {
            if (Objects.equals(blockPos, blockPos2)) {
                return false;
            }
        }
        return true;
    }
    
    private void g() {
        if (this.f.size() > 15) {
            this.f.remove(0);
        }
    }
}
