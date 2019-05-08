package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.AreaEffectCloudEntity;

public class SittingFlamingPhase extends AbstractSittingPhase
{
    private int b;
    private int c;
    private AreaEffectCloudEntity d;
    
    public SittingFlamingPhase(final EnderDragonEntity dragon) {
        super(dragon);
    }
    
    @Override
    public void b() {
        ++this.b;
        if (this.b % 2 == 0 && this.b < 10) {
            final Vec3d vec3d1 = this.dragon.u(1.0f).normalize();
            vec3d1.rotateY(-0.7853982f);
            final double double2 = this.dragon.partHead.x;
            final double double3 = this.dragon.partHead.y + this.dragon.partHead.getHeight() / 2.0f;
            final double double4 = this.dragon.partHead.z;
            for (int integer8 = 0; integer8 < 8; ++integer8) {
                final double double5 = double2 + this.dragon.getRand().nextGaussian() / 2.0;
                final double double6 = double3 + this.dragon.getRand().nextGaussian() / 2.0;
                final double double7 = double4 + this.dragon.getRand().nextGaussian() / 2.0;
                for (int integer9 = 0; integer9 < 6; ++integer9) {
                    this.dragon.world.addParticle(ParticleTypes.i, double5, double6, double7, -vec3d1.x * 0.07999999821186066 * integer9, -vec3d1.y * 0.6000000238418579, -vec3d1.z * 0.07999999821186066 * integer9);
                }
                vec3d1.rotateY(0.19634955f);
            }
        }
    }
    
    @Override
    public void c() {
        ++this.b;
        if (this.b >= 200) {
            if (this.c >= 4) {
                this.dragon.getPhaseManager().setPhase(PhaseType.TAKEOFF);
            }
            else {
                this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_SCANNING);
            }
        }
        else if (this.b == 10) {
            final Vec3d vec3d1 = new Vec3d(this.dragon.partHead.x - this.dragon.x, 0.0, this.dragon.partHead.z - this.dragon.z).normalize();
            final float float2 = 5.0f;
            final double double3 = this.dragon.partHead.x + vec3d1.x * 5.0 / 2.0;
            final double double4 = this.dragon.partHead.z + vec3d1.z * 5.0 / 2.0;
            double double5 = this.dragon.partHead.y + this.dragon.partHead.getHeight() / 2.0f;
            final BlockPos.Mutable mutable9 = new BlockPos.Mutable(double3, double5, double4);
            while (this.dragon.world.isAir(mutable9)) {
                --double5;
                mutable9.set(double3, double5, double4);
            }
            double5 = MathHelper.floor(double5) + 1;
            (this.d = new AreaEffectCloudEntity(this.dragon.world, double3, double5, double4)).setOwner(this.dragon);
            this.d.setRadius(5.0f);
            this.d.setDuration(200);
            this.d.setParticleType(ParticleTypes.i);
            this.d.addEffect(new StatusEffectInstance(StatusEffects.g));
            this.dragon.world.spawnEntity(this.d);
        }
    }
    
    @Override
    public void beginPhase() {
        this.b = 0;
        ++this.c;
    }
    
    @Override
    public void endPhase() {
        if (this.d != null) {
            this.d.remove();
            this.d = null;
        }
    }
    
    @Override
    public PhaseType<SittingFlamingPhase> getType() {
        return PhaseType.SITTING_FLAMING;
    }
    
    public void j() {
        this.c = 0;
    }
}
