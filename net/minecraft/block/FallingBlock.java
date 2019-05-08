package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.BlockStateParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import java.util.Random;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FallingBlock extends Block
{
    public FallingBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean boolean5) {
        world.getBlockTickScheduler().schedule(pos, this, this.getTickRate(world));
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        world.getBlockTickScheduler().schedule(pos, this, this.getTickRate(world));
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (!world.isClient) {
            this.tryStartFalling(world, pos);
        }
    }
    
    private void tryStartFalling(final World world, final BlockPos pos) {
        if (!canFallThrough(world.getBlockState(pos.down())) || pos.getY() < 0) {
            return;
        }
        if (!world.isClient) {
            final FallingBlockEntity fallingBlockEntity3 = new FallingBlockEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, world.getBlockState(pos));
            this.configureFallingBlockEntity(fallingBlockEntity3);
            world.spawnEntity(fallingBlockEntity3);
        }
    }
    
    protected void configureFallingBlockEntity(final FallingBlockEntity entity) {
    }
    
    @Override
    public int getTickRate(final ViewableWorld world) {
        return 2;
    }
    
    public static boolean canFallThrough(final BlockState state) {
        final Block block2 = state.getBlock();
        final Material material3 = state.getMaterial();
        return state.isAir() || block2 == Blocks.bM || material3.isLiquid() || material3.isReplaceable();
    }
    
    public void onLanding(final World world, final BlockPos pos, final BlockState fallingBlockState, final BlockState currentStateInPos) {
    }
    
    public void onDestroyedOnLanding(final World world, final BlockPos pos) {
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(final BlockState state, final World world, final BlockPos pos, final Random rnd) {
        if (rnd.nextInt(16) == 0) {
            final BlockPos blockPos5 = pos.down();
            if (canFallThrough(world.getBlockState(blockPos5))) {
                final double double6 = pos.getX() + rnd.nextFloat();
                final double double7 = pos.getY() - 0.05;
                final double double8 = pos.getZ() + rnd.nextFloat();
                world.addParticle(new BlockStateParticleParameters(ParticleTypes.x, state), double6, double7, double8, 0.0, 0.0, 0.0);
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    public int getColor(final BlockState state) {
        return -16777216;
    }
}
