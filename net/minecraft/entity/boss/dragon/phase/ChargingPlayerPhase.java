package net.minecraft.entity.boss.dragon.phase;

import org.apache.logging.log4j.LogManager;
import javax.annotation.Nullable;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.Logger;

public class ChargingPlayerPhase extends AbstractPhase
{
    private static final Logger LOGGER;
    private Vec3d target;
    private int d;
    
    public ChargingPlayerPhase(final EnderDragonEntity dragon) {
        super(dragon);
    }
    
    @Override
    public void c() {
        if (this.target == null) {
            ChargingPlayerPhase.LOGGER.warn("Aborting charge player as no target was set.");
            this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
            return;
        }
        if (this.d > 0 && this.d++ >= 10) {
            this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
            return;
        }
        final double double1 = this.target.squaredDistanceTo(this.dragon.x, this.dragon.y, this.dragon.z);
        if (double1 < 100.0 || double1 > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            ++this.d;
        }
    }
    
    @Override
    public void beginPhase() {
        this.target = null;
        this.d = 0;
    }
    
    public void setTarget(final Vec3d target) {
        this.target = target;
    }
    
    @Override
    public float f() {
        return 3.0f;
    }
    
    @Nullable
    @Override
    public Vec3d getTarget() {
        return this.target;
    }
    
    @Override
    public PhaseType<ChargingPlayerPhase> getType() {
        return PhaseType.CHARGING_PLAYER;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
