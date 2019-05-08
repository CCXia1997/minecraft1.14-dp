package net.minecraft.entity.ai.goal;

import net.minecraft.block.Material;
import net.minecraft.world.World;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.Entity;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.mob.MobEntity;

public abstract class DoorInteractGoal extends Goal
{
    protected MobEntity owner;
    protected BlockPos doorPos;
    protected boolean f;
    private boolean shouldStop;
    private float b;
    private float c;
    
    public DoorInteractGoal(final MobEntity mobEntity) {
        this.doorPos = BlockPos.ORIGIN;
        this.owner = mobEntity;
        if (!(mobEntity.getNavigation() instanceof MobNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
        }
    }
    
    protected boolean g() {
        if (!this.f) {
            return false;
        }
        final BlockState blockState1 = this.owner.world.getBlockState(this.doorPos);
        if (!(blockState1.getBlock() instanceof DoorBlock)) {
            return this.f = false;
        }
        return blockState1.<Boolean>get((Property<Boolean>)DoorBlock.OPEN);
    }
    
    protected void setDoorOpen(final boolean open) {
        if (this.f) {
            final BlockState blockState2 = this.owner.world.getBlockState(this.doorPos);
            if (blockState2.getBlock() instanceof DoorBlock) {
                ((DoorBlock)blockState2.getBlock()).setOpen(this.owner.world, this.doorPos, open);
            }
        }
    }
    
    @Override
    public boolean canStart() {
        if (!this.owner.horizontalCollision) {
            return false;
        }
        final MobNavigation mobNavigation1 = (MobNavigation)this.owner.getNavigation();
        final Path path2 = mobNavigation1.getCurrentPath();
        if (path2 == null || path2.isFinished() || !mobNavigation1.canEnterOpenDoors()) {
            return false;
        }
        for (int integer3 = 0; integer3 < Math.min(path2.getCurrentNodeIndex() + 2, path2.getLength()); ++integer3) {
            final PathNode pathNode4 = path2.getNode(integer3);
            this.doorPos = new BlockPos(pathNode4.x, pathNode4.y + 1, pathNode4.z);
            if (this.owner.squaredDistanceTo(this.doorPos.getX(), this.owner.y, this.doorPos.getZ()) <= 2.25) {
                this.f = getDoor(this.owner.world, this.doorPos);
                if (this.f) {
                    return true;
                }
            }
        }
        this.doorPos = new BlockPos(this.owner).up();
        return this.f = getDoor(this.owner.world, this.doorPos);
    }
    
    @Override
    public boolean shouldContinue() {
        return !this.shouldStop;
    }
    
    @Override
    public void start() {
        this.shouldStop = false;
        this.b = (float)(this.doorPos.getX() + 0.5f - this.owner.x);
        this.c = (float)(this.doorPos.getZ() + 0.5f - this.owner.z);
    }
    
    @Override
    public void tick() {
        final float float1 = (float)(this.doorPos.getX() + 0.5f - this.owner.x);
        final float float2 = (float)(this.doorPos.getZ() + 0.5f - this.owner.z);
        final float float3 = this.b * float1 + this.c * float2;
        if (float3 < 0.0f) {
            this.shouldStop = true;
        }
    }
    
    public static boolean getDoor(final World world, final BlockPos blockPos) {
        final BlockState blockState3 = world.getBlockState(blockPos);
        return blockState3.getBlock() instanceof DoorBlock && blockState3.getMaterial() == Material.WOOD;
    }
}
