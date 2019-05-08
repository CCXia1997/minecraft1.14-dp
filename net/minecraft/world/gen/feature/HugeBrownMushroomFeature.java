package net.minecraft.world.gen.feature;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.block.MushroomBlock;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.Vec3i;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class HugeBrownMushroomFeature extends Feature<DefaultFeatureConfig>
{
    public HugeBrownMushroomFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final DefaultFeatureConfig config) {
        int integer6 = random.nextInt(3) + 4;
        if (random.nextInt(12) == 0) {
            integer6 *= 2;
        }
        final int integer7 = pos.getY();
        if (integer7 < 1 || integer7 + integer6 + 1 >= 256) {
            return false;
        }
        final Block block8 = world.getBlockState(pos.down()).getBlock();
        if (!Block.isNaturalDirt(block8) && block8 != Blocks.i && block8 != Blocks.dL) {
            return false;
        }
        final BlockPos.Mutable mutable9 = new BlockPos.Mutable();
        for (int integer8 = 0; integer8 <= 1 + integer6; ++integer8) {
            for (int integer9 = (integer8 <= 3) ? 0 : 3, integer10 = -integer9; integer10 <= integer9; ++integer10) {
                for (int integer11 = -integer9; integer11 <= integer9; ++integer11) {
                    final BlockState blockState14 = world.getBlockState(mutable9.set(pos).setOffset(integer10, integer8, integer11));
                    if (!blockState14.isAir() && !blockState14.matches(BlockTags.C)) {
                        return false;
                    }
                }
            }
        }
        final BlockState blockState15 = (((AbstractPropertyContainer<O, BlockState>)Blocks.dx.getDefaultState()).with((Property<Comparable>)MushroomBlock.UP, true)).<Comparable, Boolean>with((Property<Comparable>)MushroomBlock.DOWN, false);
        final int integer9 = 3;
        for (int integer10 = -3; integer10 <= 3; ++integer10) {
            for (int integer11 = -3; integer11 <= 3; ++integer11) {
                final boolean boolean14 = integer10 == -3;
                final boolean boolean15 = integer10 == 3;
                final boolean boolean16 = integer11 == -3;
                final boolean boolean17 = integer11 == 3;
                final boolean boolean18 = boolean14 || boolean15;
                final boolean boolean19 = boolean16 || boolean17;
                if (!boolean18 || !boolean19) {
                    mutable9.set(pos).setOffset(integer10, integer6, integer11);
                    if (!world.getBlockState(mutable9).isFullOpaque(world, mutable9)) {
                        final boolean boolean20 = boolean14 || (boolean19 && integer10 == -2);
                        final boolean boolean21 = boolean15 || (boolean19 && integer10 == 2);
                        final boolean boolean22 = boolean16 || (boolean18 && integer11 == -2);
                        final boolean boolean23 = boolean17 || (boolean18 && integer11 == 2);
                        this.setBlockState(world, mutable9, (((((AbstractPropertyContainer<O, BlockState>)blockState15).with((Property<Comparable>)MushroomBlock.WEST, boolean20)).with((Property<Comparable>)MushroomBlock.EAST, boolean21)).with((Property<Comparable>)MushroomBlock.NORTH, boolean22)).<Comparable, Boolean>with((Property<Comparable>)MushroomBlock.SOUTH, boolean23));
                    }
                }
            }
        }
        final BlockState blockState16 = (((AbstractPropertyContainer<O, BlockState>)Blocks.dz.getDefaultState()).with((Property<Comparable>)MushroomBlock.UP, false)).<Comparable, Boolean>with((Property<Comparable>)MushroomBlock.DOWN, false);
        for (int integer11 = 0; integer11 < integer6; ++integer11) {
            mutable9.set(pos).setOffset(Direction.UP, integer11);
            if (!world.getBlockState(mutable9).isFullOpaque(world, mutable9)) {
                this.setBlockState(world, mutable9, blockState16);
            }
        }
        return true;
    }
}
