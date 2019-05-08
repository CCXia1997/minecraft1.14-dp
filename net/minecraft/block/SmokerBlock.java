package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Property;
import java.util.Random;
import net.minecraft.stat.Stats;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.entity.SmokerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class SmokerBlock extends AbstractFurnaceBlock
{
    protected SmokerBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new SmokerBlockEntity();
    }
    
    @Override
    protected void openContainer(final World world, final BlockPos pos, final PlayerEntity player) {
        final BlockEntity blockEntity4 = world.getBlockEntity(pos);
        if (blockEntity4 instanceof SmokerBlockEntity) {
            player.openContainer((NameableContainerProvider)blockEntity4);
            player.incrementStat(Stats.ar);
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(final BlockState state, final World world, final BlockPos pos, final Random rnd) {
        if (!state.<Boolean>get((Property<Boolean>)SmokerBlock.LIT)) {
            return;
        }
        final double double5 = pos.getX() + 0.5;
        final double double6 = pos.getY();
        final double double7 = pos.getZ() + 0.5;
        if (rnd.nextDouble() < 0.1) {
            world.playSound(double5, double6, double7, SoundEvents.kR, SoundCategory.e, 1.0f, 1.0f, false);
        }
        world.addParticle(ParticleTypes.Q, double5, double6 + 1.1, double7, 0.0, 0.0, 0.0);
    }
}
