package net.minecraft.block;

import net.minecraft.item.ItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class EndGatewayBlock extends BlockWithEntity
{
    protected EndGatewayBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new EndGatewayBlockEntity();
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(final BlockState state, final World world, final BlockPos pos, final Random rnd) {
        final BlockEntity blockEntity5 = world.getBlockEntity(pos);
        if (!(blockEntity5 instanceof EndGatewayBlockEntity)) {
            return;
        }
        for (int integer6 = ((EndGatewayBlockEntity)blockEntity5).getDrawnSidesCount(), integer7 = 0; integer7 < integer6; ++integer7) {
            double double8 = pos.getX() + rnd.nextFloat();
            final double double9 = pos.getY() + rnd.nextFloat();
            double double10 = pos.getZ() + rnd.nextFloat();
            double double11 = (rnd.nextFloat() - 0.5) * 0.5;
            final double double12 = (rnd.nextFloat() - 0.5) * 0.5;
            double double13 = (rnd.nextFloat() - 0.5) * 0.5;
            final int integer8 = rnd.nextInt(2) * 2 - 1;
            if (rnd.nextBoolean()) {
                double10 = pos.getZ() + 0.5 + 0.25 * integer8;
                double13 = rnd.nextFloat() * 2.0f * integer8;
            }
            else {
                double8 = pos.getX() + 0.5 + 0.25 * integer8;
                double11 = rnd.nextFloat() * 2.0f * integer8;
            }
            world.addParticle(ParticleTypes.O, double8, double9, double10, double11, double12, double13);
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack getPickStack(final BlockView world, final BlockPos pos, final BlockState state) {
        return ItemStack.EMPTY;
    }
}
