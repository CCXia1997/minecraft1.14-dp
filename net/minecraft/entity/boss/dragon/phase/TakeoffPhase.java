package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.world.gen.feature.EndPortalFeature;
import net.minecraft.world.Heightmap;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.ai.pathing.Path;

public class TakeoffPhase extends AbstractPhase
{
    private boolean b;
    private Path c;
    private Vec3d d;
    
    public TakeoffPhase(final EnderDragonEntity dragon) {
        super(dragon);
    }
    
    @Override
    public void c() {
        if (this.b || this.c == null) {
            this.b = false;
            this.j();
        }
        else {
            final BlockPos blockPos1 = this.dragon.world.getTopPosition(Heightmap.Type.f, EndPortalFeature.ORIGIN);
            if (!blockPos1.isWithinDistance(this.dragon.getPos(), 100.0)) {
                this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
            }
        }
    }
    
    @Override
    public void beginPhase() {
        this.b = true;
        this.c = null;
        this.d = null;
    }
    
    private void j() {
        final int integer1 = this.dragon.l();
        final Vec3d vec3d2 = this.dragon.u(1.0f);
        int integer2 = this.dragon.k(-vec3d2.x * 40.0, 105.0, -vec3d2.z * 40.0);
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
        this.c = this.dragon.a(integer1, integer2, null);
        if (this.c != null) {
            this.c.next();
            this.k();
        }
    }
    
    private void k() {
        final Vec3d vec3d1 = this.c.getCurrentPosition();
        this.c.next();
        double double2;
        do {
            double2 = vec3d1.y + this.dragon.getRand().nextFloat() * 20.0f;
        } while (double2 < vec3d1.y);
        this.d = new Vec3d(vec3d1.x, double2, vec3d1.z);
    }
    
    @Nullable
    @Override
    public Vec3d getTarget() {
        return this.d;
    }
    
    @Override
    public PhaseType<TakeoffPhase> getType() {
        return PhaseType.TAKEOFF;
    }
}
