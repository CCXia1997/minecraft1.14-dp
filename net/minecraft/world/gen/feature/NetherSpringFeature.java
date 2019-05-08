package net.minecraft.world.gen.feature;

import net.minecraft.fluid.Fluids;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;

public class NetherSpringFeature extends Feature<NetherSpringFeatureConfig>
{
    private static final BlockState NETHERRACK;
    
    public NetherSpringFeature(final Function<Dynamic<?>, ? extends NetherSpringFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final NetherSpringFeatureConfig config) {
        if (world.getBlockState(pos.up()) != NetherSpringFeature.NETHERRACK) {
            return false;
        }
        if (!world.getBlockState(pos).isAir() && world.getBlockState(pos) != NetherSpringFeature.NETHERRACK) {
            return false;
        }
        int integer6 = 0;
        if (world.getBlockState(pos.west()) == NetherSpringFeature.NETHERRACK) {
            ++integer6;
        }
        if (world.getBlockState(pos.east()) == NetherSpringFeature.NETHERRACK) {
            ++integer6;
        }
        if (world.getBlockState(pos.north()) == NetherSpringFeature.NETHERRACK) {
            ++integer6;
        }
        if (world.getBlockState(pos.south()) == NetherSpringFeature.NETHERRACK) {
            ++integer6;
        }
        if (world.getBlockState(pos.down()) == NetherSpringFeature.NETHERRACK) {
            ++integer6;
        }
        int integer7 = 0;
        if (world.isAir(pos.west())) {
            ++integer7;
        }
        if (world.isAir(pos.east())) {
            ++integer7;
        }
        if (world.isAir(pos.north())) {
            ++integer7;
        }
        if (world.isAir(pos.south())) {
            ++integer7;
        }
        if (world.isAir(pos.down())) {
            ++integer7;
        }
        if ((!config.insideRock && integer6 == 4 && integer7 == 1) || integer6 == 5) {
            world.setBlockState(pos, Blocks.B.getDefaultState(), 2);
            world.getFluidTickScheduler().schedule(pos, Fluids.LAVA, 0);
        }
        return true;
    }
    
    static {
        NETHERRACK = Blocks.cJ.getDefaultState();
    }
}
