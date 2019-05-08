package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.mob.MobEntityWithAi;

public class WanderAroundFarGoal extends WanderAroundGoal
{
    protected final float farWanderProbability;
    
    public WanderAroundFarGoal(final MobEntityWithAi owner, final double speed) {
        this(owner, speed, 0.001f);
    }
    
    public WanderAroundFarGoal(final MobEntityWithAi mobEntityWithAi, final double double2, final float float4) {
        super(mobEntityWithAi, double2);
        this.farWanderProbability = float4;
    }
    
    @Nullable
    @Override
    protected Vec3d getWanderTarget() {
        if (this.owner.isInsideWaterOrBubbleColumn()) {
            final Vec3d vec3d1 = PathfindingUtil.findTargetStraight(this.owner, 15, 7);
            return (vec3d1 == null) ? super.getWanderTarget() : vec3d1;
        }
        if (this.owner.getRand().nextFloat() >= this.farWanderProbability) {
            return PathfindingUtil.findTargetStraight(this.owner, 10, 7);
        }
        return super.getWanderTarget();
    }
}
