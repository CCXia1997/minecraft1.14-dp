package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MyceliumBlock extends SpreadableBlock
{
    public MyceliumBlock(final Settings settings) {
        super(settings);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(final BlockState state, final World world, final BlockPos pos, final Random rnd) {
        super.randomDisplayTick(state, world, pos, rnd);
        if (rnd.nextInt(10) == 0) {
            world.addParticle(ParticleTypes.L, pos.getX() + rnd.nextFloat(), pos.getY() + 1.1f, pos.getZ() + rnd.nextFloat(), 0.0, 0.0, 0.0);
        }
    }
}
