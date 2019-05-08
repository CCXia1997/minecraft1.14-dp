package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.Direction;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WetSpongeBlock extends Block
{
    protected WetSpongeBlock(final Settings settings) {
        super(settings);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(final BlockState state, final World world, final BlockPos pos, final Random rnd) {
        final Direction direction5 = Direction.random(rnd);
        if (direction5 == Direction.UP) {
            return;
        }
        final BlockPos blockPos6 = pos.offset(direction5);
        final BlockState blockState7 = world.getBlockState(blockPos6);
        if (state.isFullBoundsCubeForCulling() && Block.isSolidFullSquare(blockState7, world, blockPos6, direction5.getOpposite())) {
            return;
        }
        double double8 = pos.getX();
        double double9 = pos.getY();
        double double10 = pos.getZ();
        if (direction5 == Direction.DOWN) {
            double9 -= 0.05;
            double8 += rnd.nextDouble();
            double10 += rnd.nextDouble();
        }
        else {
            double9 += rnd.nextDouble() * 0.8;
            if (direction5.getAxis() == Direction.Axis.X) {
                double10 += rnd.nextDouble();
                if (direction5 == Direction.EAST) {
                    double8 += 1.1;
                }
                else {
                    double8 += 0.05;
                }
            }
            else {
                double8 += rnd.nextDouble();
                if (direction5 == Direction.SOUTH) {
                    double10 += 1.1;
                }
                else {
                    double10 += 0.05;
                }
            }
        }
        world.addParticle(ParticleTypes.m, double8, double9, double10, 0.0, 0.0, 0.0);
    }
}
