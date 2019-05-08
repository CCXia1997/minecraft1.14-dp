package net.minecraft.entity.ai.goal;

import net.minecraft.util.math.Vec3d;
import net.minecraft.fluid.FluidState;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.passive.DolphinEntity;

public class DolphinJumpGoal extends DiveJumpingGoal
{
    private static final int[] a;
    private final DolphinEntity dolphin;
    private final int chance;
    private boolean d;
    
    public DolphinJumpGoal(final DolphinEntity dolphinEntity, final int chance) {
        this.dolphin = dolphinEntity;
        this.chance = chance;
    }
    
    @Override
    public boolean canStart() {
        if (this.dolphin.getRand().nextInt(this.chance) != 0) {
            return false;
        }
        final Direction direction1 = this.dolphin.getMovementDirection();
        final int integer2 = direction1.getOffsetX();
        final int integer3 = direction1.getOffsetZ();
        final BlockPos blockPos4 = new BlockPos(this.dolphin);
        for (final int integer4 : DolphinJumpGoal.a) {
            if (!this.isWater(blockPos4, integer2, integer3, integer4) || !this.isAir(blockPos4, integer2, integer3, integer4)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isWater(final BlockPos pos, final int integer2, final int integer3, final int integer4) {
        final BlockPos blockPos5 = pos.add(integer2 * integer4, 0, integer3 * integer4);
        return this.dolphin.world.getFluidState(blockPos5).matches(FluidTags.a) && !this.dolphin.world.getBlockState(blockPos5).getMaterial().blocksMovement();
    }
    
    private boolean isAir(final BlockPos pos, final int integer2, final int integer3, final int integer4) {
        return this.dolphin.world.getBlockState(pos.add(integer2 * integer4, 1, integer3 * integer4)).isAir() && this.dolphin.world.getBlockState(pos.add(integer2 * integer4, 2, integer3 * integer4)).isAir();
    }
    
    @Override
    public boolean shouldContinue() {
        final double double1 = this.dolphin.getVelocity().y;
        return (double1 * double1 >= 0.029999999329447746 || this.dolphin.pitch == 0.0f || Math.abs(this.dolphin.pitch) >= 10.0f || !this.dolphin.isInsideWater()) && !this.dolphin.onGround;
    }
    
    @Override
    public boolean canStop() {
        return false;
    }
    
    @Override
    public void start() {
        final Direction direction1 = this.dolphin.getMovementDirection();
        this.dolphin.setVelocity(this.dolphin.getVelocity().add(direction1.getOffsetX() * 0.6, 0.7, direction1.getOffsetZ() * 0.6));
        this.dolphin.getNavigation().stop();
    }
    
    @Override
    public void stop() {
        this.dolphin.pitch = 0.0f;
    }
    
    @Override
    public void tick() {
        final boolean boolean1 = this.d;
        if (!boolean1) {
            final FluidState fluidState2 = this.dolphin.world.getFluidState(new BlockPos(this.dolphin));
            this.d = fluidState2.matches(FluidTags.a);
        }
        if (this.d && !boolean1) {
            this.dolphin.playSound(SoundEvents.bR, 1.0f, 1.0f);
        }
        final Vec3d vec3d2 = this.dolphin.getVelocity();
        if (vec3d2.y * vec3d2.y < 0.029999999329447746 && this.dolphin.pitch != 0.0f) {
            this.dolphin.pitch = this.updatePitch(this.dolphin.pitch, 0.0f, 0.2f);
        }
        else {
            final double double3 = Math.sqrt(Entity.squaredHorizontalLength(vec3d2));
            final double double4 = Math.signum(-vec3d2.y) * Math.acos(double3 / vec3d2.length()) * 57.2957763671875;
            this.dolphin.pitch = (float)double4;
        }
    }
    
    static {
        a = new int[] { 0, 1, 4, 5, 6, 7 };
    }
}
