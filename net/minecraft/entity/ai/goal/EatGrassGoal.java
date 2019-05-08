package net.minecraft.entity.ai.goal;

import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import java.util.EnumSet;
import net.minecraft.world.World;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.block.BlockState;
import java.util.function.Predicate;

public class EatGrassGoal extends Goal
{
    private static final Predicate<BlockState> GRASS_PREDICATE;
    private final MobEntity owner;
    private final World world;
    private int timer;
    
    public EatGrassGoal(final MobEntity entity) {
        this.owner = entity;
        this.world = entity.world;
        this.setControls(EnumSet.<Control>of(Control.a, Control.b, Control.c));
    }
    
    @Override
    public boolean canStart() {
        if (this.owner.getRand().nextInt(this.owner.isChild() ? 50 : 1000) != 0) {
            return false;
        }
        final BlockPos blockPos1 = new BlockPos(this.owner.x, this.owner.y, this.owner.z);
        return EatGrassGoal.GRASS_PREDICATE.test(this.world.getBlockState(blockPos1)) || this.world.getBlockState(blockPos1.down()).getBlock() == Blocks.i;
    }
    
    @Override
    public void start() {
        this.timer = 40;
        this.world.sendEntityStatus(this.owner, (byte)10);
        this.owner.getNavigation().stop();
    }
    
    @Override
    public void stop() {
        this.timer = 0;
    }
    
    @Override
    public boolean shouldContinue() {
        return this.timer > 0;
    }
    
    public int getTimer() {
        return this.timer;
    }
    
    @Override
    public void tick() {
        this.timer = Math.max(0, this.timer - 1);
        if (this.timer != 4) {
            return;
        }
        final BlockPos blockPos1 = new BlockPos(this.owner.x, this.owner.y, this.owner.z);
        if (EatGrassGoal.GRASS_PREDICATE.test(this.world.getBlockState(blockPos1))) {
            if (this.world.getGameRules().getBoolean("mobGriefing")) {
                this.world.breakBlock(blockPos1, false);
            }
            this.owner.onEatingGrass();
        }
        else {
            final BlockPos blockPos2 = blockPos1.down();
            if (this.world.getBlockState(blockPos2).getBlock() == Blocks.i) {
                if (this.world.getGameRules().getBoolean("mobGriefing")) {
                    this.world.playLevelEvent(2001, blockPos2, Block.getRawIdFromState(Blocks.i.getDefaultState()));
                    this.world.setBlockState(blockPos2, Blocks.j.getDefaultState(), 2);
                }
                this.owner.onEatingGrass();
            }
        }
    }
    
    static {
        GRASS_PREDICATE = BlockStatePredicate.forBlock(Blocks.aQ);
    }
}
