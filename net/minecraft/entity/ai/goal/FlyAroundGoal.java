package net.minecraft.entity.ai.goal;

import net.minecraft.block.Block;
import java.util.Iterator;
import net.minecraft.tag.BlockTags;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.mob.MobEntityWithAi;

public class FlyAroundGoal extends WanderAroundFarGoal
{
    public FlyAroundGoal(final MobEntityWithAi owner, final double speed) {
        super(owner, speed);
    }
    
    @Nullable
    @Override
    protected Vec3d getWanderTarget() {
        Vec3d vec3d1 = null;
        if (this.owner.isInsideWater()) {
            vec3d1 = PathfindingUtil.findTargetStraight(this.owner, 15, 15);
        }
        if (this.owner.getRand().nextFloat() >= this.farWanderProbability) {
            vec3d1 = this.j();
        }
        return (vec3d1 == null) ? super.getWanderTarget() : vec3d1;
    }
    
    @Nullable
    private Vec3d j() {
        final BlockPos blockPos1 = new BlockPos(this.owner);
        final BlockPos.Mutable mutable2 = new BlockPos.Mutable();
        final BlockPos.Mutable mutable3 = new BlockPos.Mutable();
        final Iterable<BlockPos> iterable4 = BlockPos.iterate(MathHelper.floor(this.owner.x - 3.0), MathHelper.floor(this.owner.y - 6.0), MathHelper.floor(this.owner.z - 3.0), MathHelper.floor(this.owner.x + 3.0), MathHelper.floor(this.owner.y + 6.0), MathHelper.floor(this.owner.z + 3.0));
        for (final BlockPos blockPos2 : iterable4) {
            if (blockPos1.equals(blockPos2)) {
                continue;
            }
            final Block block7 = this.owner.world.getBlockState(mutable3.set(blockPos2).setOffset(Direction.DOWN)).getBlock();
            final boolean boolean8 = block7 instanceof LeavesBlock || block7.matches(BlockTags.o);
            if (boolean8 && this.owner.world.isAir(blockPos2) && this.owner.world.isAir(mutable2.set(blockPos2).setOffset(Direction.UP))) {
                return new Vec3d(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
            }
        }
        return null;
    }
}
