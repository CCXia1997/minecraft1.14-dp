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

public class HugeRedMushroomFeature extends Feature<DefaultFeatureConfig>
{
    public HugeRedMushroomFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
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
        for (int integer8 = 0; integer8 <= integer6; ++integer8) {
            int integer9 = 0;
            if (integer8 < integer6 && integer8 >= integer6 - 3) {
                integer9 = 2;
            }
            else if (integer8 == integer6) {
                integer9 = 1;
            }
            for (int integer10 = -integer9; integer10 <= integer9; ++integer10) {
                for (int integer11 = -integer9; integer11 <= integer9; ++integer11) {
                    final BlockState blockState14 = world.getBlockState(mutable9.set(pos).setOffset(integer10, integer8, integer11));
                    if (!blockState14.isAir() && !blockState14.matches(BlockTags.C)) {
                        return false;
                    }
                }
            }
        }
        final BlockState blockState15 = ((AbstractPropertyContainer<O, BlockState>)Blocks.dy.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)MushroomBlock.DOWN, false);
        for (int integer9 = integer6 - 3; integer9 <= integer6; ++integer9) {
            final int integer10 = (integer9 < integer6) ? 2 : 1;
            final int integer11 = 0;
            for (int integer12 = -integer10; integer12 <= integer10; ++integer12) {
                for (int integer13 = -integer10; integer13 <= integer10; ++integer13) {
                    final boolean boolean16 = integer12 == -integer10;
                    final boolean boolean17 = integer12 == integer10;
                    final boolean boolean18 = integer13 == -integer10;
                    final boolean boolean19 = integer13 == integer10;
                    final boolean boolean20 = boolean16 || boolean17;
                    final boolean boolean21 = boolean18 || boolean19;
                    if (integer9 >= integer6 || boolean20 != boolean21) {
                        mutable9.set(pos).setOffset(integer12, integer9, integer13);
                        if (!world.getBlockState(mutable9).isFullOpaque(world, mutable9)) {
                            this.setBlockState(world, mutable9, ((((((AbstractPropertyContainer<O, BlockState>)blockState15).with((Property<Comparable>)MushroomBlock.UP, integer9 >= integer6 - 1)).with((Property<Comparable>)MushroomBlock.WEST, integer12 < 0)).with((Property<Comparable>)MushroomBlock.EAST, integer12 > 0)).with((Property<Comparable>)MushroomBlock.NORTH, integer13 < 0)).<Comparable, Boolean>with((Property<Comparable>)MushroomBlock.SOUTH, integer13 > 0));
                        }
                    }
                }
            }
        }
        final BlockState blockState16 = (((AbstractPropertyContainer<O, BlockState>)Blocks.dz.getDefaultState()).with((Property<Comparable>)MushroomBlock.UP, false)).<Comparable, Boolean>with((Property<Comparable>)MushroomBlock.DOWN, false);
        for (int integer10 = 0; integer10 < integer6; ++integer10) {
            mutable9.set(pos).setOffset(Direction.UP, integer10);
            if (!world.getBlockState(mutable9).isFullOpaque(world, mutable9)) {
                this.setBlockState(world, mutable9, blockState16);
            }
        }
        return true;
    }
}
