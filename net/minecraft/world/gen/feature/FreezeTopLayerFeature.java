package net.minecraft.world.gen.feature;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.world.biome.Biome;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.block.SnowyBlock;
import net.minecraft.block.Blocks;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class FreezeTopLayerFeature extends Feature<DefaultFeatureConfig>
{
    public FreezeTopLayerFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final DefaultFeatureConfig config) {
        final BlockPos.Mutable mutable6 = new BlockPos.Mutable();
        final BlockPos.Mutable mutable7 = new BlockPos.Mutable();
        for (int integer8 = 0; integer8 < 16; ++integer8) {
            for (int integer9 = 0; integer9 < 16; ++integer9) {
                final int integer10 = pos.getX() + integer8;
                final int integer11 = pos.getZ() + integer9;
                final int integer12 = world.getTop(Heightmap.Type.e, integer10, integer11);
                mutable6.set(integer10, integer12, integer11);
                mutable7.set(mutable6).setOffset(Direction.DOWN, 1);
                final Biome biome13 = world.getBiome(mutable6);
                if (biome13.canSetSnow(world, mutable7, false)) {
                    world.setBlockState(mutable7, Blocks.cB.getDefaultState(), 2);
                }
                if (biome13.canSetIce(world, mutable6)) {
                    world.setBlockState(mutable6, Blocks.cA.getDefaultState(), 2);
                    final BlockState blockState14 = world.getBlockState(mutable7);
                    if (blockState14.<Comparable>contains((Property<Comparable>)SnowyBlock.SNOWY)) {
                        world.setBlockState(mutable7, ((AbstractPropertyContainer<O, BlockState>)blockState14).<Comparable, Boolean>with((Property<Comparable>)SnowyBlock.SNOWY, true), 2);
                    }
                }
            }
        }
        return true;
    }
}
