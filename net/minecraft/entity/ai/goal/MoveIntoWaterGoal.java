package net.minecraft.entity.ai.goal;

import java.util.Iterator;
import net.minecraft.util.math.MathHelper;
import net.minecraft.tag.FluidTags;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.mob.MobEntityWithAi;

public class MoveIntoWaterGoal extends Goal
{
    private final MobEntityWithAi owner;
    
    public MoveIntoWaterGoal(final MobEntityWithAi mobEntityWithAi) {
        this.owner = mobEntityWithAi;
    }
    
    @Override
    public boolean canStart() {
        return this.owner.onGround && !this.owner.world.getFluidState(new BlockPos(this.owner)).matches(FluidTags.a);
    }
    
    @Override
    public void start() {
        BlockPos blockPos1 = null;
        final Iterable<BlockPos> iterable2 = BlockPos.iterate(MathHelper.floor(this.owner.x - 2.0), MathHelper.floor(this.owner.y - 2.0), MathHelper.floor(this.owner.z - 2.0), MathHelper.floor(this.owner.x + 2.0), MathHelper.floor(this.owner.y), MathHelper.floor(this.owner.z + 2.0));
        for (final BlockPos blockPos2 : iterable2) {
            if (this.owner.world.getFluidState(blockPos2).matches(FluidTags.a)) {
                blockPos1 = blockPos2;
                break;
            }
        }
        if (blockPos1 != null) {
            this.owner.getMoveControl().moveTo(blockPos1.getX(), blockPos1.getY(), blockPos1.getZ(), 1.0);
        }
    }
}
