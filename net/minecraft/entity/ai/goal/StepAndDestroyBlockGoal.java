package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.util.math.Vec3d;
import java.util.Random;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ItemStackParticleParameters;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.BlockView;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.block.Block;

public class StepAndDestroyBlockGoal extends MoveToTargetPosGoal
{
    private final Block targetBlock;
    private final MobEntity owner;
    private int counter;
    
    public StepAndDestroyBlockGoal(final Block targetBlock, final MobEntityWithAi owner, final double speed, final int maxYDifference) {
        super(owner, speed, 24, maxYDifference);
        this.targetBlock = targetBlock;
        this.owner = owner;
    }
    
    @Override
    public boolean canStart() {
        if (!this.owner.world.getGameRules().getBoolean("mobGriefing")) {
            return false;
        }
        if (this.cooldown > 0) {
            --this.cooldown;
            return false;
        }
        if (this.hasAvailableTarget()) {
            this.cooldown = 20;
            return true;
        }
        this.cooldown = this.getInterval(this.owner);
        return false;
    }
    
    private boolean hasAvailableTarget() {
        return (this.targetPos != null && this.owner.world.isBlockLoaded(this.targetPos) && this.isTargetPos(this.owner.world, this.targetPos)) || this.findTargetPos();
    }
    
    @Override
    public void stop() {
        super.stop();
        this.owner.fallDistance = 1.0f;
    }
    
    @Override
    public void start() {
        super.start();
        this.counter = 0;
    }
    
    public void tickStepping(final IWorld world, final BlockPos pos) {
    }
    
    public void onDestroyBlock(final World world, final BlockPos pos) {
    }
    
    @Override
    public void tick() {
        super.tick();
        final World world1 = this.owner.world;
        final BlockPos blockPos2 = new BlockPos(this.owner);
        final BlockPos blockPos3 = this.tweakToProperPos(blockPos2, world1);
        final Random random4 = this.owner.getRand();
        if (this.hasReached() && blockPos3 != null) {
            if (this.counter > 0) {
                final Vec3d vec3d5 = this.owner.getVelocity();
                this.owner.setVelocity(vec3d5.x, 0.3, vec3d5.z);
                if (!world1.isClient) {
                    final double double6 = 0.08;
                    ((ServerWorld)world1).<ItemStackParticleParameters>spawnParticles(new ItemStackParticleParameters(ParticleTypes.G, new ItemStack(Items.kW)), blockPos3.getX() + 0.5, blockPos3.getY() + 0.7, blockPos3.getZ() + 0.5, 3, (random4.nextFloat() - 0.5) * 0.08, (random4.nextFloat() - 0.5) * 0.08, (random4.nextFloat() - 0.5) * 0.08, 0.15000000596046448);
                }
            }
            if (this.counter % 2 == 0) {
                final Vec3d vec3d5 = this.owner.getVelocity();
                this.owner.setVelocity(vec3d5.x, -0.3, vec3d5.z);
                if (this.counter % 6 == 0) {
                    this.tickStepping(world1, this.targetPos);
                }
            }
            if (this.counter > 60) {
                world1.clearBlockState(blockPos3, false);
                if (!world1.isClient) {
                    for (int integer5 = 0; integer5 < 20; ++integer5) {
                        final double double6 = random4.nextGaussian() * 0.02;
                        final double double7 = random4.nextGaussian() * 0.02;
                        final double double8 = random4.nextGaussian() * 0.02;
                        ((ServerWorld)world1).<DefaultParticleType>spawnParticles(ParticleTypes.N, blockPos3.getX() + 0.5, blockPos3.getY(), blockPos3.getZ() + 0.5, 1, double6, double7, double8, 0.15000000596046448);
                    }
                    this.onDestroyBlock(world1, blockPos3);
                }
            }
            ++this.counter;
        }
    }
    
    @Nullable
    private BlockPos tweakToProperPos(final BlockPos pos, final BlockView view) {
        if (view.getBlockState(pos).getBlock() == this.targetBlock) {
            return pos;
        }
        final BlockPos[] array;
        final BlockPos[] arr3 = array = new BlockPos[] { pos.down(), pos.west(), pos.east(), pos.north(), pos.south(), pos.down().down() };
        for (final BlockPos blockPos7 : array) {
            if (view.getBlockState(blockPos7).getBlock() == this.targetBlock) {
                return blockPos7;
            }
        }
        return null;
    }
    
    @Override
    protected boolean isTargetPos(final ViewableWorld world, final BlockPos pos) {
        final Block block3 = world.getBlockState(pos).getBlock();
        return block3 == this.targetBlock && world.getBlockState(pos.up()).isAir() && world.getBlockState(pos.up(2)).isAir();
    }
}
