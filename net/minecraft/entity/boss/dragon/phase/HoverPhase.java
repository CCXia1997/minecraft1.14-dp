package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.Vec3d;

public class HoverPhase extends AbstractPhase
{
    private Vec3d b;
    
    public HoverPhase(final EnderDragonEntity dragon) {
        super(dragon);
    }
    
    @Override
    public void c() {
        if (this.b == null) {
            this.b = new Vec3d(this.dragon.x, this.dragon.y, this.dragon.z);
        }
    }
    
    @Override
    public boolean a() {
        return true;
    }
    
    @Override
    public void beginPhase() {
        this.b = null;
    }
    
    @Override
    public float f() {
        return 1.0f;
    }
    
    @Nullable
    @Override
    public Vec3d getTarget() {
        return this.b;
    }
    
    @Override
    public PhaseType<HoverPhase> getType() {
        return PhaseType.HOVER;
    }
}
