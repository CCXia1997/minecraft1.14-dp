package net.minecraft.entity.ai.brain;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;

public class BlockPosLookTarget implements LookTarget
{
    private final BlockPos blockPos;
    private final Vec3d pos;
    
    public BlockPosLookTarget(final BlockPos blockPos) {
        this.blockPos = blockPos;
        this.pos = new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
    }
    
    @Override
    public BlockPos getBlockPos() {
        return this.blockPos;
    }
    
    @Override
    public Vec3d getPos() {
        return this.pos;
    }
    
    @Override
    public boolean isSeenBy(final LivingEntity livingEntity) {
        return true;
    }
}
