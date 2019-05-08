package net.minecraft.entity.ai;

import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.MathHelper;
import java.util.Random;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.BlockPos;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.mob.MobEntityWithAi;

public class PathfindingUtil
{
    @Nullable
    public static Vec3d findTarget(final MobEntityWithAi mobEntityWithAi, final int integer2, final int integer3) {
        return findTarget(mobEntityWithAi, integer2, integer3, null);
    }
    
    @Nullable
    public static Vec3d findTargetStraight(final MobEntityWithAi mobEntityWithAi, final int integer2, final int integer3) {
        return findTargetStraight(mobEntityWithAi, integer2, integer3, mobEntityWithAi::getPathfindingFavor);
    }
    
    @Nullable
    public static Vec3d findTargetStraight(final MobEntityWithAi mobEntityWithAi, final int integer2, final int integer3, final ToDoubleFunction<BlockPos> toDoubleFunction) {
        return findTarget(mobEntityWithAi, integer2, integer3, null, false, 0.0, toDoubleFunction);
    }
    
    @Nullable
    public static Vec3d a(final MobEntityWithAi mobEntityWithAi, final int integer2, final int integer3, final Vec3d vec3d) {
        final Vec3d vec3d2 = vec3d.subtract(mobEntityWithAi.x, mobEntityWithAi.y, mobEntityWithAi.z);
        return findTarget(mobEntityWithAi, integer2, integer3, vec3d2);
    }
    
    @Nullable
    public static Vec3d a(final MobEntityWithAi mobEntityWithAi, final int integer2, final int integer3, final Vec3d vec3d, final double double5) {
        final Vec3d vec3d2 = vec3d.subtract(mobEntityWithAi.x, mobEntityWithAi.y, mobEntityWithAi.z);
        return findTarget(mobEntityWithAi, integer2, integer3, vec3d2, true, double5, mobEntityWithAi::getPathfindingFavor);
    }
    
    @Nullable
    public static Vec3d b(final MobEntityWithAi mobEntityWithAi, final int integer2, final int integer3, final Vec3d vec3d) {
        final Vec3d vec3d2 = new Vec3d(mobEntityWithAi.x, mobEntityWithAi.y, mobEntityWithAi.z).subtract(vec3d);
        return findTarget(mobEntityWithAi, integer2, integer3, vec3d2);
    }
    
    @Nullable
    private static Vec3d findTarget(final MobEntityWithAi mobEntityWithAi, final int integer2, final int integer3, @Nullable final Vec3d vec3d) {
        return findTarget(mobEntityWithAi, integer2, integer3, vec3d, true, 1.5707963705062866, mobEntityWithAi::getPathfindingFavor);
    }
    
    @Nullable
    private static Vec3d findTarget(final MobEntityWithAi mobEntityWithAi, final int integer2, final int integer3, @Nullable final Vec3d vec3d, final boolean boolean5, final double double6, final ToDoubleFunction<BlockPos> toDoubleFunction8) {
        final EntityNavigation entityNavigation9 = mobEntityWithAi.getNavigation();
        final Random random10 = mobEntityWithAi.getRand();
        final boolean boolean6 = mobEntityWithAi.hasWalkTargetRange() && mobEntityWithAi.getWalkTarget().isWithinDistance(mobEntityWithAi.getPos(), mobEntityWithAi.getWalkTargetRange() + integer2 + 1.0);
        boolean boolean7 = false;
        double double7 = Double.NEGATIVE_INFINITY;
        int integer4 = 0;
        int integer5 = 0;
        int integer6 = 0;
        for (int integer7 = 0; integer7 < 10; ++integer7) {
            final BlockPos blockPos19 = a(random10, integer2, integer3, vec3d, double6);
            if (blockPos19 != null) {
                int integer8 = blockPos19.getX();
                final int integer9 = blockPos19.getY();
                int integer10 = blockPos19.getZ();
                if (mobEntityWithAi.hasWalkTargetRange() && integer2 > 1) {
                    final BlockPos blockPos20 = mobEntityWithAi.getWalkTarget();
                    if (mobEntityWithAi.x > blockPos20.getX()) {
                        integer8 -= random10.nextInt(integer2 / 2);
                    }
                    else {
                        integer8 += random10.nextInt(integer2 / 2);
                    }
                    if (mobEntityWithAi.z > blockPos20.getZ()) {
                        integer10 -= random10.nextInt(integer2 / 2);
                    }
                    else {
                        integer10 += random10.nextInt(integer2 / 2);
                    }
                }
                BlockPos blockPos20 = new BlockPos(integer8 + mobEntityWithAi.x, integer9 + mobEntityWithAi.y, integer10 + mobEntityWithAi.z);
                if (!boolean6 || mobEntityWithAi.isInWalkTargetRange(blockPos20)) {
                    if (entityNavigation9.isValidPosition(blockPos20)) {
                        if (!boolean5) {
                            blockPos20 = a(blockPos20, mobEntityWithAi);
                            if (isWater(blockPos20, mobEntityWithAi)) {
                                continue;
                            }
                        }
                        final double double8 = toDoubleFunction8.applyAsDouble(blockPos20);
                        if (double8 > double7) {
                            double7 = double8;
                            integer4 = integer8;
                            integer5 = integer9;
                            integer6 = integer10;
                            boolean7 = true;
                        }
                    }
                }
            }
        }
        if (boolean7) {
            return new Vec3d(integer4 + mobEntityWithAi.x, integer5 + mobEntityWithAi.y, integer6 + mobEntityWithAi.z);
        }
        return null;
    }
    
    @Nullable
    private static BlockPos a(final Random random, final int integer2, final int integer3, @Nullable final Vec3d vec3d, final double double5) {
        if (vec3d == null || double5 >= 3.141592653589793) {
            final int integer4 = random.nextInt(2 * integer2 + 1) - integer2;
            final int integer5 = random.nextInt(2 * integer3 + 1) - integer3;
            final int integer6 = random.nextInt(2 * integer2 + 1) - integer2;
            return new BlockPos(integer4, integer5, integer6);
        }
        final double double6 = MathHelper.atan2(vec3d.z, vec3d.x) - 1.5707963705062866;
        final double double7 = double6 + (2.0f * random.nextFloat() - 1.0f) * double5;
        final double double8 = Math.sqrt(random.nextDouble()) * MathHelper.SQUARE_ROOT_OF_TWO * integer2;
        final double double9 = -double8 * Math.sin(double7);
        final double double10 = double8 * Math.cos(double7);
        if (Math.abs(double9) > integer2 || Math.abs(double10) > integer2) {
            return null;
        }
        final int integer7 = random.nextInt(2 * integer3 + 1) - integer3;
        return new BlockPos(double9, integer7, double10);
    }
    
    private static BlockPos a(final BlockPos blockPos, final MobEntityWithAi mobEntityWithAi) {
        if (mobEntityWithAi.world.getBlockState(blockPos).getMaterial().isSolid()) {
            BlockPos blockPos2;
            for (blockPos2 = blockPos.up(); blockPos2.getY() < mobEntityWithAi.world.getHeight() && mobEntityWithAi.world.getBlockState(blockPos2).getMaterial().isSolid(); blockPos2 = blockPos2.up()) {}
            return blockPos2;
        }
        return blockPos;
    }
    
    private static boolean isWater(final BlockPos blockPos, final MobEntityWithAi mobEntityWithAi) {
        return mobEntityWithAi.world.getFluidState(blockPos).matches(FluidTags.a);
    }
}
