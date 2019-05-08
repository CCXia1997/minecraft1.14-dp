package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.world.gen.feature.EndPortalFeature;
import net.minecraft.world.Heightmap;
import javax.annotation.Nullable;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.TargetPredicate;

public class LandingApproachPhase extends AbstractPhase
{
    private static final TargetPredicate PLAYERS_IN_RANGE_PREDICATE;
    private Path c;
    private Vec3d d;
    
    public LandingApproachPhase(final EnderDragonEntity dragon) {
        super(dragon);
    }
    
    @Override
    public PhaseType<LandingApproachPhase> getType() {
        return PhaseType.LANDING_APPROACH;
    }
    
    @Override
    public void beginPhase() {
        this.c = null;
        this.d = null;
    }
    
    @Override
    public void c() {
        final double double1 = (this.d == null) ? 0.0 : this.d.squaredDistanceTo(this.dragon.x, this.dragon.y, this.dragon.z);
        if (double1 < 100.0 || double1 > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            this.j();
        }
    }
    
    @Nullable
    @Override
    public Vec3d getTarget() {
        return this.d;
    }
    
    private void j() {
        if (this.c == null || this.c.isFinished()) {
            final int integer1 = this.dragon.l();
            final BlockPos blockPos2 = this.dragon.world.getTopPosition(Heightmap.Type.f, EndPortalFeature.ORIGIN);
            final PlayerEntity playerEntity3 = this.dragon.world.getClosestPlayer(LandingApproachPhase.PLAYERS_IN_RANGE_PREDICATE, blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
            int integer2;
            if (playerEntity3 != null) {
                final Vec3d vec3d5 = new Vec3d(playerEntity3.x, 0.0, playerEntity3.z).normalize();
                integer2 = this.dragon.k(-vec3d5.x * 40.0, 105.0, -vec3d5.z * 40.0);
            }
            else {
                integer2 = this.dragon.k(40.0, blockPos2.getY(), 0.0);
            }
            final PathNode pathNode5 = new PathNode(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
            this.c = this.dragon.a(integer1, integer2, pathNode5);
            if (this.c != null) {
                this.c.next();
            }
        }
        this.k();
        if (this.c != null && this.c.isFinished()) {
            this.dragon.getPhaseManager().setPhase(PhaseType.LANDING);
        }
    }
    
    private void k() {
        if (this.c != null && !this.c.isFinished()) {
            final Vec3d vec3d1 = this.c.getCurrentPosition();
            this.c.next();
            final double double2 = vec3d1.x;
            final double double3 = vec3d1.z;
            double double4;
            do {
                double4 = vec3d1.y + this.dragon.getRand().nextFloat() * 20.0f;
            } while (double4 < vec3d1.y);
            this.d = new Vec3d(double2, double4, double3);
        }
    }
    
    static {
        PLAYERS_IN_RANGE_PREDICATE = new TargetPredicate().setBaseMaxDistance(128.0);
    }
}
