package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.EndPortalFeature;
import net.minecraft.world.Heightmap;
import javax.annotation.Nullable;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.TargetPredicate;

public class HoldingPatternPhase extends AbstractPhase
{
    private static final TargetPredicate PLAYERS_IN_RANGE_PREDICATE;
    private Path c;
    private Vec3d d;
    private boolean e;
    
    public HoldingPatternPhase(final EnderDragonEntity dragon) {
        super(dragon);
    }
    
    @Override
    public PhaseType<HoldingPatternPhase> getType() {
        return PhaseType.HOLDING_PATTERN;
    }
    
    @Override
    public void c() {
        final double double1 = (this.d == null) ? 0.0 : this.d.squaredDistanceTo(this.dragon.x, this.dragon.y, this.dragon.z);
        if (double1 < 100.0 || double1 > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            this.j();
        }
    }
    
    @Override
    public void beginPhase() {
        this.c = null;
        this.d = null;
    }
    
    @Nullable
    @Override
    public Vec3d getTarget() {
        return this.d;
    }
    
    private void j() {
        if (this.c != null && this.c.isFinished()) {
            final BlockPos blockPos1 = this.dragon.world.getTopPosition(Heightmap.Type.f, new BlockPos(EndPortalFeature.ORIGIN));
            final int integer2 = (this.dragon.getFight() == null) ? 0 : this.dragon.getFight().getAliveEndCrystals();
            if (this.dragon.getRand().nextInt(integer2 + 3) == 0) {
                this.dragon.getPhaseManager().setPhase(PhaseType.LANDING_APPROACH);
                return;
            }
            double double3 = 64.0;
            final PlayerEntity playerEntity5 = this.dragon.world.getClosestPlayer(HoldingPatternPhase.PLAYERS_IN_RANGE_PREDICATE, blockPos1.getX(), blockPos1.getY(), blockPos1.getZ());
            if (playerEntity5 != null) {
                double3 = blockPos1.getSquaredDistance(playerEntity5.getPos(), true) / 512.0;
            }
            if (playerEntity5 != null && !playerEntity5.abilities.invulnerable && (this.dragon.getRand().nextInt(MathHelper.abs((int)double3) + 2) == 0 || this.dragon.getRand().nextInt(integer2 + 2) == 0)) {
                this.a(playerEntity5);
                return;
            }
        }
        if (this.c == null || this.c.isFinished()) {
            int integer2;
            final int integer3 = integer2 = this.dragon.l();
            if (this.dragon.getRand().nextInt(8) == 0) {
                this.e = !this.e;
                integer2 += 6;
            }
            if (this.e) {
                ++integer2;
            }
            else {
                --integer2;
            }
            if (this.dragon.getFight() == null || this.dragon.getFight().getAliveEndCrystals() < 0) {
                integer2 -= 12;
                integer2 &= 0x7;
                integer2 += 12;
            }
            else {
                integer2 %= 12;
                if (integer2 < 0) {
                    integer2 += 12;
                }
            }
            this.c = this.dragon.a(integer3, integer2, null);
            if (this.c != null) {
                this.c.next();
            }
        }
        this.k();
    }
    
    private void a(final PlayerEntity playerEntity) {
        this.dragon.getPhaseManager().setPhase(PhaseType.STRAFE_PLAYER);
        this.dragon.getPhaseManager().<StrafePlayerPhase>create(PhaseType.STRAFE_PLAYER).a(playerEntity);
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
    
    @Override
    public void crystalDestroyed(final EnderCrystalEntity crystal, final BlockPos pos, final DamageSource source, @Nullable final PlayerEntity player) {
        if (player != null && !player.abilities.invulnerable) {
            this.a(player);
        }
    }
    
    static {
        PLAYERS_IN_RANGE_PREDICATE = new TargetPredicate().setBaseMaxDistance(64.0);
    }
}
