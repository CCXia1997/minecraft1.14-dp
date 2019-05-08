package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import java.util.Random;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.math.BlockPos;
import java.util.EnumSet;
import net.minecraft.world.World;
import net.minecraft.entity.mob.MobEntityWithAi;

public class EscapeSunlightGoal extends Goal
{
    protected final MobEntityWithAi owner;
    private double targetX;
    private double targetY;
    private double targetZ;
    private final double speed;
    private final World world;
    
    public EscapeSunlightGoal(final MobEntityWithAi owner, final double speed) {
        this.owner = owner;
        this.speed = speed;
        this.world = owner.world;
        this.setControls(EnumSet.<Control>of(Control.a));
    }
    
    @Override
    public boolean canStart() {
        return this.owner.getTarget() == null && this.world.isDaylight() && this.owner.isOnFire() && this.world.isSkyVisible(new BlockPos(this.owner.x, this.owner.getBoundingBox().minY, this.owner.z)) && this.owner.getEquippedStack(EquipmentSlot.HEAD).isEmpty() && this.g();
    }
    
    protected boolean g() {
        final Vec3d vec3d1 = this.locateShadedPos();
        if (vec3d1 == null) {
            return false;
        }
        this.targetX = vec3d1.x;
        this.targetY = vec3d1.y;
        this.targetZ = vec3d1.z;
        return true;
    }
    
    @Override
    public boolean shouldContinue() {
        return !this.owner.getNavigation().isIdle();
    }
    
    @Override
    public void start() {
        this.owner.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
    }
    
    @Nullable
    protected Vec3d locateShadedPos() {
        final Random random1 = this.owner.getRand();
        final BlockPos blockPos2 = new BlockPos(this.owner.x, this.owner.getBoundingBox().minY, this.owner.z);
        for (int integer3 = 0; integer3 < 10; ++integer3) {
            final BlockPos blockPos3 = blockPos2.add(random1.nextInt(20) - 10, random1.nextInt(6) - 3, random1.nextInt(20) - 10);
            if (!this.world.isSkyVisible(blockPos3) && this.owner.getPathfindingFavor(blockPos3) < 0.0f) {
                return new Vec3d(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ());
            }
        }
        return null;
    }
}
