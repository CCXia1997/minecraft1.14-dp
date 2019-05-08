package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.feature.EndPortalFeature;
import net.minecraft.world.Heightmap;
import java.util.Random;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.Vec3d;

public class LandingPhase extends AbstractPhase
{
    private Vec3d b;
    
    public LandingPhase(final EnderDragonEntity dragon) {
        super(dragon);
    }
    
    @Override
    public void b() {
        final Vec3d vec3d1 = this.dragon.u(1.0f).normalize();
        vec3d1.rotateY(-0.7853982f);
        final double double2 = this.dragon.partHead.x;
        final double double3 = this.dragon.partHead.y + this.dragon.partHead.getHeight() / 2.0f;
        final double double4 = this.dragon.partHead.z;
        for (int integer8 = 0; integer8 < 8; ++integer8) {
            final Random random9 = this.dragon.getRand();
            final double double5 = double2 + random9.nextGaussian() / 2.0;
            final double double6 = double3 + random9.nextGaussian() / 2.0;
            final double double7 = double4 + random9.nextGaussian() / 2.0;
            final Vec3d vec3d2 = this.dragon.getVelocity();
            this.dragon.world.addParticle(ParticleTypes.i, double5, double6, double7, -vec3d1.x * 0.07999999821186066 + vec3d2.x, -vec3d1.y * 0.30000001192092896 + vec3d2.y, -vec3d1.z * 0.07999999821186066 + vec3d2.z);
            vec3d1.rotateY(0.19634955f);
        }
    }
    
    @Override
    public void c() {
        if (this.b == null) {
            this.b = new Vec3d(this.dragon.world.getTopPosition(Heightmap.Type.f, EndPortalFeature.ORIGIN));
        }
        if (this.b.squaredDistanceTo(this.dragon.x, this.dragon.y, this.dragon.z) < 1.0) {
            this.dragon.getPhaseManager().<SittingFlamingPhase>create(PhaseType.SITTING_FLAMING).j();
            this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_SCANNING);
        }
    }
    
    @Override
    public float f() {
        return 1.5f;
    }
    
    @Override
    public float h() {
        final float float1 = MathHelper.sqrt(Entity.squaredHorizontalLength(this.dragon.getVelocity())) + 1.0f;
        final float float2 = Math.min(float1, 40.0f);
        return float2 / float1;
    }
    
    @Override
    public void beginPhase() {
        this.b = null;
    }
    
    @Nullable
    @Override
    public Vec3d getTarget() {
        return this.b;
    }
    
    @Override
    public PhaseType<LandingPhase> getType() {
        return PhaseType.LANDING;
    }
}
