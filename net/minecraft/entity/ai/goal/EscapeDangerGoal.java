package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.util.math.Vec3i;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import java.util.EnumSet;
import net.minecraft.entity.mob.MobEntityWithAi;

public class EscapeDangerGoal extends Goal
{
    protected final MobEntityWithAi owner;
    protected final double speed;
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    
    public EscapeDangerGoal(final MobEntityWithAi owner, final double speed) {
        this.owner = owner;
        this.speed = speed;
        this.setControls(EnumSet.<Control>of(Control.a));
    }
    
    @Override
    public boolean canStart() {
        if (this.owner.getAttacker() == null && !this.owner.isOnFire()) {
            return false;
        }
        if (this.owner.isOnFire()) {
            final BlockPos blockPos1 = this.locateClosestWater(this.owner.world, this.owner, 5, 4);
            if (blockPos1 != null) {
                this.targetX = blockPos1.getX();
                this.targetY = blockPos1.getY();
                this.targetZ = blockPos1.getZ();
                return true;
            }
        }
        return this.findTarget();
    }
    
    protected boolean findTarget() {
        final Vec3d vec3d1 = PathfindingUtil.findTarget(this.owner, 5, 4);
        if (vec3d1 == null) {
            return false;
        }
        this.targetX = vec3d1.x;
        this.targetY = vec3d1.y;
        this.targetZ = vec3d1.z;
        return true;
    }
    
    @Override
    public void start() {
        this.owner.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
    }
    
    @Override
    public boolean shouldContinue() {
        return !this.owner.getNavigation().isIdle();
    }
    
    @Nullable
    protected BlockPos locateClosestWater(final BlockView blockView, final Entity entity, final int rangeX, final int rangeY) {
        final BlockPos blockPos5 = new BlockPos(entity);
        final int integer6 = blockPos5.getX();
        final int integer7 = blockPos5.getY();
        final int integer8 = blockPos5.getZ();
        float float9 = (float)(rangeX * rangeX * rangeY * 2);
        BlockPos blockPos6 = null;
        final BlockPos.Mutable mutable11 = new BlockPos.Mutable();
        for (int integer9 = integer6 - rangeX; integer9 <= integer6 + rangeX; ++integer9) {
            for (int integer10 = integer7 - rangeY; integer10 <= integer7 + rangeY; ++integer10) {
                for (int integer11 = integer8 - rangeX; integer11 <= integer8 + rangeX; ++integer11) {
                    mutable11.set(integer9, integer10, integer11);
                    if (blockView.getFluidState(mutable11).matches(FluidTags.a)) {
                        final float float10 = (float)((integer9 - integer6) * (integer9 - integer6) + (integer10 - integer7) * (integer10 - integer7) + (integer11 - integer8) * (integer11 - integer8));
                        if (float10 < float9) {
                            float9 = float10;
                            blockPos6 = new BlockPos(mutable11);
                        }
                    }
                }
            }
        }
        return blockPos6;
    }
}
