package net.minecraft.entity.ai.brain;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;

public class WalkTarget
{
    private final LookTarget lookTarget;
    private final float speed;
    private final int completionRange;
    
    public WalkTarget(final BlockPos pos, final float speed, final int completionRange) {
        this(new BlockPosLookTarget(pos), speed, completionRange);
    }
    
    public WalkTarget(final Vec3d pos, final float speedFactor, final int completionRange) {
        this(new BlockPosLookTarget(new BlockPos(pos)), speedFactor, completionRange);
    }
    
    public WalkTarget(final LookTarget lookTarget, final float speed, final int completionRange) {
        this.lookTarget = lookTarget;
        this.speed = speed;
        this.completionRange = completionRange;
    }
    
    public LookTarget getLookTarget() {
        return this.lookTarget;
    }
    
    public float getSpeed() {
        return this.speed;
    }
    
    public int getCompletionRange() {
        return this.completionRange;
    }
}
