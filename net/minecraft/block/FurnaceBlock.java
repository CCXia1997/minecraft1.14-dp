package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Direction;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Property;
import java.util.Random;
import net.minecraft.stat.Stats;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class FurnaceBlock extends AbstractFurnaceBlock
{
    protected FurnaceBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new FurnaceBlockEntity();
    }
    
    @Override
    protected void openContainer(final World world, final BlockPos pos, final PlayerEntity player) {
        final BlockEntity blockEntity4 = world.getBlockEntity(pos);
        if (blockEntity4 instanceof FurnaceBlockEntity) {
            player.openContainer((NameableContainerProvider)blockEntity4);
            player.incrementStat(Stats.ak);
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(final BlockState state, final World world, final BlockPos pos, final Random rnd) {
        if (!state.<Boolean>get((Property<Boolean>)FurnaceBlock.LIT)) {
            return;
        }
        final double double5 = pos.getX() + 0.5;
        final double double6 = pos.getY();
        final double double7 = pos.getZ() + 0.5;
        if (rnd.nextDouble() < 0.1) {
            world.playSound(double5, double6, double7, SoundEvents.dD, SoundCategory.e, 1.0f, 1.0f, false);
        }
        final Direction direction11 = state.<Direction>get((Property<Direction>)FurnaceBlock.FACING);
        final Direction.Axis axis12 = direction11.getAxis();
        final double double8 = 0.52;
        final double double9 = rnd.nextDouble() * 0.6 - 0.3;
        final double double10 = (axis12 == Direction.Axis.X) ? (direction11.getOffsetX() * 0.52) : double9;
        final double double11 = rnd.nextDouble() * 6.0 / 16.0;
        final double double12 = (axis12 == Direction.Axis.Z) ? (direction11.getOffsetZ() * 0.52) : double9;
        world.addParticle(ParticleTypes.Q, double5 + double10, double6 + double11, double7 + double12, 0.0, 0.0, 0.0);
        world.addParticle(ParticleTypes.A, double5 + double10, double6 + double11, double7 + double12, 0.0, 0.0, 0.0);
    }
}
