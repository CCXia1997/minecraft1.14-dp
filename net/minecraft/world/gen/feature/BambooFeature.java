package net.minecraft.world.gen.feature;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.state.property.Property;
import net.minecraft.block.BambooBlock;
import net.minecraft.util.math.Direction;
import net.minecraft.tag.BlockTags;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ViewableWorld;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.ProbabilityConfig;

public class BambooFeature extends Feature<ProbabilityConfig>
{
    private static final BlockState BAMBOO;
    private static final BlockState BAMBOO_TOP_1;
    private static final BlockState BAMBOO_TOP_2;
    private static final BlockState BAMBOO_TOP_3;
    
    public BambooFeature(final Function<Dynamic<?>, ? extends ProbabilityConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final ProbabilityConfig config) {
        int integer6 = 0;
        final BlockPos.Mutable mutable7 = new BlockPos.Mutable(pos);
        final BlockPos.Mutable mutable8 = new BlockPos.Mutable(pos);
        if (world.isAir(mutable7)) {
            if (Blocks.kQ.getDefaultState().canPlaceAt(world, mutable7)) {
                final int integer7 = random.nextInt(12) + 5;
                if (random.nextFloat() < config.probability) {
                    for (int integer8 = random.nextInt(4) + 1, integer9 = pos.getX() - integer8; integer9 <= pos.getX() + integer8; ++integer9) {
                        for (int integer10 = pos.getZ() - integer8; integer10 <= pos.getZ() + integer8; ++integer10) {
                            final int integer11 = integer9 - pos.getX();
                            final int integer12 = integer10 - pos.getZ();
                            if (integer11 * integer11 + integer12 * integer12 <= integer8 * integer8) {
                                mutable8.set(integer9, world.getTop(Heightmap.Type.b, integer9, integer10) - 1, integer10);
                                if (world.getBlockState(mutable8).getBlock().matches(BlockTags.S)) {
                                    world.setBlockState(mutable8, Blocks.l.getDefaultState(), 2);
                                }
                            }
                        }
                    }
                }
                for (int integer8 = 0; integer8 < integer7 && world.isAir(mutable7); ++integer8) {
                    world.setBlockState(mutable7, BambooFeature.BAMBOO, 2);
                    mutable7.setOffset(Direction.UP, 1);
                }
                if (mutable7.getY() - pos.getY() >= 3) {
                    world.setBlockState(mutable7, BambooFeature.BAMBOO_TOP_1, 2);
                    world.setBlockState(mutable7.setOffset(Direction.DOWN, 1), BambooFeature.BAMBOO_TOP_2, 2);
                    world.setBlockState(mutable7.setOffset(Direction.DOWN, 1), BambooFeature.BAMBOO_TOP_3, 2);
                }
            }
            ++integer6;
        }
        return integer6 > 0;
    }
    
    static {
        BAMBOO = ((((AbstractPropertyContainer<O, BlockState>)Blocks.kQ.getDefaultState()).with((Property<Comparable>)BambooBlock.AGE, 1)).with(BambooBlock.LEAVES, BambooLeaves.a)).<Comparable, Integer>with((Property<Comparable>)BambooBlock.STAGE, 0);
        BAMBOO_TOP_1 = (((AbstractPropertyContainer<O, BlockState>)BambooFeature.BAMBOO).with(BambooBlock.LEAVES, BambooLeaves.c)).<Comparable, Integer>with((Property<Comparable>)BambooBlock.STAGE, 1);
        BAMBOO_TOP_2 = ((AbstractPropertyContainer<O, BlockState>)BambooFeature.BAMBOO).<BambooLeaves, BambooLeaves>with(BambooBlock.LEAVES, BambooLeaves.c);
        BAMBOO_TOP_3 = ((AbstractPropertyContainer<O, BlockState>)BambooFeature.BAMBOO).<BambooLeaves, BambooLeaves>with(BambooBlock.LEAVES, BambooLeaves.b);
    }
}
