package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.ai.TargetPredicate;

public class SittingScanningPhase extends AbstractSittingPhase
{
    private static final TargetPredicate PLAYER_WITHIN_RANGE_PREDICATE;
    private final TargetPredicate CLOSE_PLAYER_PREDICATE;
    private int d;
    
    public SittingScanningPhase(final EnderDragonEntity dragon) {
        super(dragon);
        this.CLOSE_PLAYER_PREDICATE = new TargetPredicate().setBaseMaxDistance(20.0).setPredicate(livingEntity -> Math.abs(livingEntity.y - dragon.y) <= 10.0);
    }
    
    @Override
    public void c() {
        ++this.d;
        LivingEntity livingEntity1 = this.dragon.world.getClosestPlayer(this.CLOSE_PLAYER_PREDICATE, this.dragon, this.dragon.x, this.dragon.y, this.dragon.z);
        if (livingEntity1 != null) {
            if (this.d > 25) {
                this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_ATTACKING);
            }
            else {
                final Vec3d vec3d2 = new Vec3d(livingEntity1.x - this.dragon.x, 0.0, livingEntity1.z - this.dragon.z).normalize();
                final Vec3d vec3d3 = new Vec3d(MathHelper.sin(this.dragon.yaw * 0.017453292f), 0.0, -MathHelper.cos(this.dragon.yaw * 0.017453292f)).normalize();
                final float float4 = (float)vec3d3.dotProduct(vec3d2);
                final float float5 = (float)(Math.acos(float4) * 57.2957763671875) + 0.5f;
                if (float5 < 0.0f || float5 > 10.0f) {
                    final double double6 = livingEntity1.x - this.dragon.partHead.x;
                    final double double7 = livingEntity1.z - this.dragon.partHead.z;
                    final double double8 = MathHelper.clamp(MathHelper.wrapDegrees(180.0 - MathHelper.atan2(double6, double7) * 57.2957763671875 - this.dragon.yaw), -100.0, 100.0);
                    final EnderDragonEntity dragon = this.dragon;
                    dragon.be *= 0.8f;
                    final float float7;
                    float float6 = float7 = MathHelper.sqrt(double6 * double6 + double7 * double7) + 1.0f;
                    if (float6 > 40.0f) {
                        float6 = 40.0f;
                    }
                    final EnderDragonEntity dragon2 = this.dragon;
                    dragon2.be += (float)(double8 * (0.7f / float6 / float7));
                    final EnderDragonEntity dragon3 = this.dragon;
                    dragon3.yaw += this.dragon.be;
                }
            }
        }
        else if (this.d >= 100) {
            livingEntity1 = this.dragon.world.getClosestPlayer(SittingScanningPhase.PLAYER_WITHIN_RANGE_PREDICATE, this.dragon, this.dragon.x, this.dragon.y, this.dragon.z);
            this.dragon.getPhaseManager().setPhase(PhaseType.TAKEOFF);
            if (livingEntity1 != null) {
                this.dragon.getPhaseManager().setPhase(PhaseType.CHARGING_PLAYER);
                this.dragon.getPhaseManager().<ChargingPlayerPhase>create(PhaseType.CHARGING_PLAYER).setTarget(new Vec3d(livingEntity1.x, livingEntity1.y, livingEntity1.z));
            }
        }
    }
    
    @Override
    public void beginPhase() {
        this.d = 0;
    }
    
    @Override
    public PhaseType<SittingScanningPhase> getType() {
        return PhaseType.SITTING_SCANNING;
    }
    
    static {
        PLAYER_WITHIN_RANGE_PREDICATE = new TargetPredicate().setBaseMaxDistance(150.0);
    }
}
