package net.minecraft.entity.ai.brain;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;

public interface LookTarget
{
    BlockPos getBlockPos();
    
    Vec3d getPos();
    
    boolean isSeenBy(final LivingEntity arg1);
}
