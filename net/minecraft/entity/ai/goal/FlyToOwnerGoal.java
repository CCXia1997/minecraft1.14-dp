package net.minecraft.entity.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.passive.TameableEntity;

public class FlyToOwnerGoal extends FollowOwnerGoal
{
    public FlyToOwnerGoal(final TameableEntity caller, final double double2, final float float4, final float float5) {
        super(caller, double2, float4, float5);
    }
    
    @Override
    protected boolean a(final BlockPos blockPos) {
        final BlockState blockState2 = this.world.getBlockState(blockPos);
        return (blockState2.hasSolidTopSurface(this.world, blockPos, this.caller) || blockState2.matches(BlockTags.C)) && this.world.isAir(blockPos.up()) && this.world.isAir(blockPos.up(2));
    }
}
