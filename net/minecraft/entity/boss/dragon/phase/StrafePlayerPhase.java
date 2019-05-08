package net.minecraft.entity.boss.dragon.phase;

import org.apache.logging.log4j.LogManager;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.ai.pathing.Path;
import org.apache.logging.log4j.Logger;

public class StrafePlayerPhase extends AbstractPhase
{
    private static final Logger LOGGER;
    private int c;
    private Path d;
    private Vec3d e;
    private LivingEntity f;
    private boolean g;
    
    public StrafePlayerPhase(final EnderDragonEntity dragon) {
        super(dragon);
    }
    
    @Override
    public void c() {
        if (this.f == null) {
            StrafePlayerPhase.LOGGER.warn("Skipping player strafe phase because no player was found");
            this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
            return;
        }
        if (this.d != null && this.d.isFinished()) {
            final double double1 = this.f.x;
            final double double2 = this.f.z;
            final double double3 = double1 - this.dragon.x;
            final double double4 = double2 - this.dragon.z;
            final double double5 = MathHelper.sqrt(double3 * double3 + double4 * double4);
            final double double6 = Math.min(0.4000000059604645 + double5 / 80.0 - 1.0, 10.0);
            this.e = new Vec3d(double1, this.f.y + double6, double2);
        }
        final double double1 = (this.e == null) ? 0.0 : this.e.squaredDistanceTo(this.dragon.x, this.dragon.y, this.dragon.z);
        if (double1 < 100.0 || double1 > 22500.0) {
            this.j();
        }
        final double double2 = 64.0;
        if (this.f.squaredDistanceTo(this.dragon) < 4096.0) {
            if (this.dragon.canSee(this.f)) {
                ++this.c;
                final Vec3d vec3d5 = new Vec3d(this.f.x - this.dragon.x, 0.0, this.f.z - this.dragon.z).normalize();
                final Vec3d vec3d6 = new Vec3d(MathHelper.sin(this.dragon.yaw * 0.017453292f), 0.0, -MathHelper.cos(this.dragon.yaw * 0.017453292f)).normalize();
                final float float7 = (float)vec3d6.dotProduct(vec3d5);
                float float8 = (float)(Math.acos(float7) * 57.2957763671875);
                float8 += 0.5f;
                if (this.c >= 5 && float8 >= 0.0f && float8 < 10.0f) {
                    final double double5 = 1.0;
                    final Vec3d vec3d7 = this.dragon.getRotationVec(1.0f);
                    final double double7 = this.dragon.partHead.x - vec3d7.x * 1.0;
                    final double double8 = this.dragon.partHead.y + this.dragon.partHead.getHeight() / 2.0f + 0.5;
                    final double double9 = this.dragon.partHead.z - vec3d7.z * 1.0;
                    final double double10 = this.f.x - double7;
                    final double double11 = this.f.y + this.f.getHeight() / 2.0f - (double8 + this.dragon.partHead.getHeight() / 2.0f);
                    final double double12 = this.f.z - double9;
                    this.dragon.world.playLevelEvent(null, 1017, new BlockPos(this.dragon), 0);
                    final DragonFireballEntity dragonFireballEntity24 = new DragonFireballEntity(this.dragon.world, this.dragon, double10, double11, double12);
                    dragonFireballEntity24.setPositionAndAngles(double7, double8, double9, 0.0f, 0.0f);
                    this.dragon.world.spawnEntity(dragonFireballEntity24);
                    this.c = 0;
                    if (this.d != null) {
                        while (!this.d.isFinished()) {
                            this.d.next();
                        }
                    }
                    this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
                }
            }
            else if (this.c > 0) {
                --this.c;
            }
        }
        else if (this.c > 0) {
            --this.c;
        }
    }
    
    private void j() {
        if (this.d == null || this.d.isFinished()) {
            int integer2;
            final int integer1 = integer2 = this.dragon.l();
            if (this.dragon.getRand().nextInt(8) == 0) {
                this.g = !this.g;
                integer2 += 6;
            }
            if (this.g) {
                ++integer2;
            }
            else {
                --integer2;
            }
            if (this.dragon.getFight() == null || this.dragon.getFight().getAliveEndCrystals() <= 0) {
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
            this.d = this.dragon.a(integer1, integer2, null);
            if (this.d != null) {
                this.d.next();
            }
        }
        this.k();
    }
    
    private void k() {
        if (this.d != null && !this.d.isFinished()) {
            final Vec3d vec3d1 = this.d.getCurrentPosition();
            this.d.next();
            final double double2 = vec3d1.x;
            final double double3 = vec3d1.z;
            double double4;
            do {
                double4 = vec3d1.y + this.dragon.getRand().nextFloat() * 20.0f;
            } while (double4 < vec3d1.y);
            this.e = new Vec3d(double2, double4, double3);
        }
    }
    
    @Override
    public void beginPhase() {
        this.c = 0;
        this.e = null;
        this.d = null;
        this.f = null;
    }
    
    public void a(final LivingEntity livingEntity) {
        this.f = livingEntity;
        final int integer2 = this.dragon.l();
        final int integer3 = this.dragon.k(this.f.x, this.f.y, this.f.z);
        final int integer4 = MathHelper.floor(this.f.x);
        final int integer5 = MathHelper.floor(this.f.z);
        final double double6 = integer4 - this.dragon.x;
        final double double7 = integer5 - this.dragon.z;
        final double double8 = MathHelper.sqrt(double6 * double6 + double7 * double7);
        final double double9 = Math.min(0.4000000059604645 + double8 / 80.0 - 1.0, 10.0);
        final int integer6 = MathHelper.floor(this.f.y + double9);
        final PathNode pathNode15 = new PathNode(integer4, integer6, integer5);
        this.d = this.dragon.a(integer2, integer3, pathNode15);
        if (this.d != null) {
            this.d.next();
            this.k();
        }
    }
    
    @Nullable
    @Override
    public Vec3d getTarget() {
        return this.e;
    }
    
    @Override
    public PhaseType<StrafePlayerPhase> getType() {
        return PhaseType.STRAFE_PLAYER;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
