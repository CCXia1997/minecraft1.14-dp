package net.minecraft.world;

import net.minecraft.util.math.MathHelper;
import java.util.function.Function;
import java.util.function.BiFunction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.fluid.FluidState;
import net.minecraft.block.BlockState;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public interface BlockView
{
    @Nullable
    BlockEntity getBlockEntity(final BlockPos arg1);
    
    BlockState getBlockState(final BlockPos arg1);
    
    FluidState getFluidState(final BlockPos arg1);
    
    default int getLuminance(final BlockPos pos) {
        return this.getBlockState(pos).getLuminance();
    }
    
    default int getMaxLightLevel() {
        return 15;
    }
    
    default int getHeight() {
        return 256;
    }
    
    default BlockHitResult rayTrace(final RayTraceContext rayTraceContext) {
        final BlockState blockState3;
        final FluidState fluidState4;
        final Vec3d vec3d5;
        final Vec3d vec3d6;
        final VoxelShape voxelShape7;
        final BlockHitResult blockHitResult8;
        final VoxelShape voxelShape8;
        final BlockHitResult blockHitResult9;
        final double double11;
        final double double12;
        final Vec3d vec3d7;
        return BlockView.<BlockHitResult>rayTrace(rayTraceContext, (rayTraceContext, blockPos) -> {
            blockState3 = this.getBlockState(blockPos);
            fluidState4 = this.getFluidState(blockPos);
            vec3d5 = rayTraceContext.getStart();
            vec3d6 = rayTraceContext.getEnd();
            voxelShape7 = rayTraceContext.getBlockShape(blockState3, this, blockPos);
            blockHitResult8 = this.rayTraceBlock(vec3d5, vec3d6, blockPos, voxelShape7, blockState3);
            voxelShape8 = rayTraceContext.getFluidShape(fluidState4, this, blockPos);
            blockHitResult9 = voxelShape8.rayTrace(vec3d5, vec3d6, blockPos);
            double11 = ((blockHitResult8 == null) ? Double.MAX_VALUE : rayTraceContext.getStart().squaredDistanceTo(blockHitResult8.getPos()));
            double12 = ((blockHitResult9 == null) ? Double.MAX_VALUE : rayTraceContext.getStart().squaredDistanceTo(blockHitResult9.getPos()));
            return (double11 <= double12) ? blockHitResult8 : blockHitResult9;
        }, rayTraceContext -> {
            vec3d7 = rayTraceContext.getStart().subtract(rayTraceContext.getEnd());
            return BlockHitResult.createMissed(rayTraceContext.getEnd(), Direction.getFacing(vec3d7.x, vec3d7.y, vec3d7.z), new BlockPos(rayTraceContext.getEnd()));
        });
    }
    
    @Nullable
    default BlockHitResult rayTraceBlock(final Vec3d vec3d1, final Vec3d vec3d2, final BlockPos blockPos, final VoxelShape voxelShape, final BlockState blockState) {
        final BlockHitResult blockHitResult6 = voxelShape.rayTrace(vec3d1, vec3d2, blockPos);
        if (blockHitResult6 != null) {
            final BlockHitResult blockHitResult7 = blockState.getRayTraceShape(this, blockPos).rayTrace(vec3d1, vec3d2, blockPos);
            if (blockHitResult7 != null && blockHitResult7.getPos().subtract(vec3d1).lengthSquared() < blockHitResult6.getPos().subtract(vec3d1).lengthSquared()) {
                return blockHitResult6.withSide(blockHitResult7.getSide());
            }
        }
        return blockHitResult6;
    }
    
    default <T> T rayTrace(final RayTraceContext rayTraceContext, final BiFunction<RayTraceContext, BlockPos, T> biFunction, final Function<RayTraceContext, T> function) {
        final Vec3d vec3d4 = rayTraceContext.getStart();
        final Vec3d vec3d5 = rayTraceContext.getEnd();
        if (vec3d4.equals(vec3d5)) {
            return function.apply(rayTraceContext);
        }
        final double double6 = MathHelper.lerp(-1.0E-7, vec3d5.x, vec3d4.x);
        final double double7 = MathHelper.lerp(-1.0E-7, vec3d5.y, vec3d4.y);
        final double double8 = MathHelper.lerp(-1.0E-7, vec3d5.z, vec3d4.z);
        final double double9 = MathHelper.lerp(-1.0E-7, vec3d4.x, vec3d5.x);
        final double double10 = MathHelper.lerp(-1.0E-7, vec3d4.y, vec3d5.y);
        final double double11 = MathHelper.lerp(-1.0E-7, vec3d4.z, vec3d5.z);
        int integer18 = MathHelper.floor(double9);
        int integer19 = MathHelper.floor(double10);
        int integer20 = MathHelper.floor(double11);
        final BlockPos.Mutable mutable21 = new BlockPos.Mutable(integer18, integer19, integer20);
        final T object22 = biFunction.apply(rayTraceContext, mutable21);
        if (object22 != null) {
            return object22;
        }
        final double double12 = double6 - double9;
        final double double13 = double7 - double10;
        final double double14 = double8 - double11;
        final int integer21 = MathHelper.sign(double12);
        final int integer22 = MathHelper.sign(double13);
        final int integer23 = MathHelper.sign(double14);
        final double double15 = (integer21 == 0) ? Double.MAX_VALUE : (integer21 / double12);
        final double double16 = (integer22 == 0) ? Double.MAX_VALUE : (integer22 / double13);
        final double double17 = (integer23 == 0) ? Double.MAX_VALUE : (integer23 / double14);
        double double18 = double15 * ((integer21 > 0) ? (1.0 - MathHelper.fractionalPart(double9)) : MathHelper.fractionalPart(double9));
        double double19 = double16 * ((integer22 > 0) ? (1.0 - MathHelper.fractionalPart(double10)) : MathHelper.fractionalPart(double10));
        double double20 = double17 * ((integer23 > 0) ? (1.0 - MathHelper.fractionalPart(double11)) : MathHelper.fractionalPart(double11));
        while (double18 <= 1.0 || double19 <= 1.0 || double20 <= 1.0) {
            if (double18 < double19) {
                if (double18 < double20) {
                    integer18 += integer21;
                    double18 += double15;
                }
                else {
                    integer20 += integer23;
                    double20 += double17;
                }
            }
            else if (double19 < double20) {
                integer19 += integer22;
                double19 += double16;
            }
            else {
                integer20 += integer23;
                double20 += double17;
            }
            final T object23 = biFunction.apply(rayTraceContext, mutable21.set(integer18, integer19, integer20));
            if (object23 != null) {
                return object23;
            }
        }
        return function.apply(rayTraceContext);
    }
}
