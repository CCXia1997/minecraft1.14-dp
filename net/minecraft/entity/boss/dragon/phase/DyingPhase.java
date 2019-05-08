package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.EndPortalFeature;
import net.minecraft.world.Heightmap;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.Vec3d;

public class DyingPhase extends AbstractPhase
{
    private Vec3d b;
    private int c;
    
    public DyingPhase(final EnderDragonEntity dragon) {
        super(dragon);
    }
    
    @Override
    public void b() {
        if (this.c++ % 10 == 0) {
            final float float1 = (this.dragon.getRand().nextFloat() - 0.5f) * 8.0f;
            final float float2 = (this.dragon.getRand().nextFloat() - 0.5f) * 4.0f;
            final float float3 = (this.dragon.getRand().nextFloat() - 0.5f) * 8.0f;
            this.dragon.world.addParticle(ParticleTypes.v, this.dragon.x + float1, this.dragon.y + 2.0 + float2, this.dragon.z + float3, 0.0, 0.0, 0.0);
        }
    }
    
    @Override
    public void c() {
        ++this.c;
        if (this.b == null) {
            final BlockPos blockPos1 = this.dragon.world.getTopPosition(Heightmap.Type.e, EndPortalFeature.ORIGIN);
            this.b = new Vec3d(blockPos1.getX(), blockPos1.getY(), blockPos1.getZ());
        }
        final double double1 = this.b.squaredDistanceTo(this.dragon.x, this.dragon.y, this.dragon.z);
        if (double1 < 100.0 || double1 > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            this.dragon.setHealth(0.0f);
        }
        else {
            this.dragon.setHealth(1.0f);
        }
    }
    
    @Override
    public void beginPhase() {
        this.b = null;
        this.c = 0;
    }
    
    @Override
    public float f() {
        return 3.0f;
    }
    
    @Nullable
    @Override
    public Vec3d getTarget() {
        return this.b;
    }
    
    @Override
    public PhaseType<DyingPhase> getType() {
        return PhaseType.DYING;
    }
}
