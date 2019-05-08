package net.minecraft.block;

import net.minecraft.world.ViewableWorld;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.util.shape.VoxelShape;

public class DragonEggBlock extends FallingBlock
{
    protected static final VoxelShape SHAPE;
    
    public DragonEggBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return DragonEggBlock.SHAPE;
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        this.teleport(state, world, pos);
        return true;
    }
    
    @Override
    public void onBlockBreakStart(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player) {
        this.teleport(state, world, pos);
    }
    
    private void teleport(final BlockState state, final World world, final BlockPos pos) {
        for (int integer4 = 0; integer4 < 1000; ++integer4) {
            final BlockPos blockPos5 = pos.add(world.random.nextInt(16) - world.random.nextInt(16), world.random.nextInt(8) - world.random.nextInt(8), world.random.nextInt(16) - world.random.nextInt(16));
            if (world.getBlockState(blockPos5).isAir()) {
                if (world.isClient) {
                    for (int integer5 = 0; integer5 < 128; ++integer5) {
                        final double double7 = world.random.nextDouble();
                        final float float9 = (world.random.nextFloat() - 0.5f) * 0.2f;
                        final float float10 = (world.random.nextFloat() - 0.5f) * 0.2f;
                        final float float11 = (world.random.nextFloat() - 0.5f) * 0.2f;
                        final double double8 = MathHelper.lerp(double7, blockPos5.getX(), pos.getX()) + (world.random.nextDouble() - 0.5) + 0.5;
                        final double double9 = MathHelper.lerp(double7, blockPos5.getY(), pos.getY()) + world.random.nextDouble() - 0.5;
                        final double double10 = MathHelper.lerp(double7, blockPos5.getZ(), pos.getZ()) + (world.random.nextDouble() - 0.5) + 0.5;
                        world.addParticle(ParticleTypes.O, double8, double9, double10, float9, float10, float11);
                    }
                }
                else {
                    world.setBlockState(blockPos5, state, 2);
                    world.clearBlockState(pos, false);
                }
                return;
            }
        }
    }
    
    @Override
    public int getTickRate(final ViewableWorld world) {
        return 5;
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    static {
        SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
    }
}
