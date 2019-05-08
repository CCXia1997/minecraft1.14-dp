package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.world.BlockView;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.mob.MobEntityWithAi;

public class SwimAroundGoal extends WanderAroundGoal
{
    public SwimAroundGoal(final MobEntityWithAi owner, final double speed, final int chance) {
        super(owner, speed, chance);
    }
    
    @Nullable
    @Override
    protected Vec3d getWanderTarget() {
        Vec3d vec3d1 = PathfindingUtil.findTarget(this.owner, 10, 7);
        for (int integer2 = 0; vec3d1 != null && !this.owner.world.getBlockState(new BlockPos(vec3d1)).canPlaceAtSide(this.owner.world, new BlockPos(vec3d1), BlockPlacementEnvironment.b) && integer2++ < 10; vec3d1 = PathfindingUtil.findTarget(this.owner, 10, 7)) {}
        return vec3d1;
    }
}
