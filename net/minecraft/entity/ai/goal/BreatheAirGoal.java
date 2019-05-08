package net.minecraft.entity.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.world.BlockView;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.Blocks;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import java.util.Iterator;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import java.util.EnumSet;
import net.minecraft.entity.mob.MobEntityWithAi;

public class BreatheAirGoal extends Goal
{
    private final MobEntityWithAi owner;
    
    public BreatheAirGoal(final MobEntityWithAi mobEntityWithAi) {
        this.owner = mobEntityWithAi;
        this.setControls(EnumSet.<Control>of(Control.a, Control.b));
    }
    
    @Override
    public boolean canStart() {
        return this.owner.getBreath() < 140;
    }
    
    @Override
    public boolean shouldContinue() {
        return this.canStart();
    }
    
    @Override
    public boolean canStop() {
        return false;
    }
    
    @Override
    public void start() {
        this.moveToAir();
    }
    
    private void moveToAir() {
        final Iterable<BlockPos> iterable1 = BlockPos.iterate(MathHelper.floor(this.owner.x - 1.0), MathHelper.floor(this.owner.y), MathHelper.floor(this.owner.z - 1.0), MathHelper.floor(this.owner.x + 1.0), MathHelper.floor(this.owner.y + 8.0), MathHelper.floor(this.owner.z + 1.0));
        BlockPos blockPos2 = null;
        for (final BlockPos blockPos3 : iterable1) {
            if (this.isAirPos(this.owner.world, blockPos3)) {
                blockPos2 = blockPos3;
                break;
            }
        }
        if (blockPos2 == null) {
            blockPos2 = new BlockPos(this.owner.x, this.owner.y + 8.0, this.owner.z);
        }
        this.owner.getNavigation().startMovingTo(blockPos2.getX(), blockPos2.getY() + 1, blockPos2.getZ(), 1.0);
    }
    
    @Override
    public void tick() {
        this.moveToAir();
        this.owner.updateVelocity(0.02f, new Vec3d(this.owner.sidewaysSpeed, this.owner.upwardSpeed, this.owner.forwardSpeed));
        this.owner.move(MovementType.a, this.owner.getVelocity());
    }
    
    private boolean isAirPos(final ViewableWorld viewableWorld, final BlockPos blockPos) {
        final BlockState blockState3 = viewableWorld.getBlockState(blockPos);
        return (viewableWorld.getFluidState(blockPos).isEmpty() || blockState3.getBlock() == Blocks.kU) && blockState3.canPlaceAtSide(viewableWorld, blockPos, BlockPlacementEnvironment.a);
    }
}
