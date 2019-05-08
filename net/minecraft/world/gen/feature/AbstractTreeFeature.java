package net.minecraft.world.gen.feature;

import net.minecraft.state.AbstractPropertyContainer;
import java.util.Iterator;
import java.util.List;
import net.minecraft.state.property.Property;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Direction;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import java.util.Set;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.block.Material;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TestableWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public abstract class AbstractTreeFeature<T extends FeatureConfig> extends Feature<T>
{
    public AbstractTreeFeature(final Function<Dynamic<?>, ? extends T> configDeserializer, final boolean emitNeighborBlockUpdates) {
        super(configDeserializer, emitNeighborBlockUpdates);
    }
    
    protected static boolean canTreeReplace(final TestableWorld world, final BlockPos pos) {
        final Block block2;
        return world.testBlockState(pos, blockState -> {
            block2 = blockState.getBlock();
            return blockState.isAir() || blockState.matches(BlockTags.C) || block2 == Blocks.i || Block.isNaturalDirt(block2) || block2.matches(BlockTags.o) || block2.matches(BlockTags.n) || block2 == Blocks.dH;
        });
    }
    
    protected static boolean isAir(final TestableWorld world, final BlockPos pos) {
        return world.testBlockState(pos, BlockState::isAir);
    }
    
    protected static boolean isNaturalDirt(final TestableWorld world, final BlockPos pos) {
        return world.testBlockState(pos, blockState -> Block.isNaturalDirt(blockState.getBlock()));
    }
    
    protected static boolean isWater(final TestableWorld world, final BlockPos pos) {
        return world.testBlockState(pos, blockState -> blockState.getBlock() == Blocks.A);
    }
    
    protected static boolean isLeaves(final TestableWorld world, final BlockPos pos) {
        return world.testBlockState(pos, blockState -> blockState.matches(BlockTags.C));
    }
    
    protected static boolean isAirOrLeaves(final TestableWorld world, final BlockPos pos) {
        return world.testBlockState(pos, blockState -> blockState.isAir() || blockState.matches(BlockTags.C));
    }
    
    protected static boolean isNaturalDirtOrGrass(final TestableWorld world, final BlockPos pos) {
        final Block block2;
        return world.testBlockState(pos, blockState -> {
            block2 = blockState.getBlock();
            return Block.isNaturalDirt(block2) || block2 == Blocks.i;
        });
    }
    
    protected static boolean isDirtOrGrass(final TestableWorld world, final BlockPos pos) {
        final Block block2;
        return world.testBlockState(pos, blockState -> {
            block2 = blockState.getBlock();
            return Block.isNaturalDirt(block2) || block2 == Blocks.i || block2 == Blocks.bV;
        });
    }
    
    protected static boolean isReplaceablePlant(final TestableWorld world, final BlockPos pos) {
        final Material material2;
        return world.testBlockState(pos, blockState -> {
            material2 = blockState.getMaterial();
            return material2 == Material.REPLACEABLE_PLANT;
        });
    }
    
    protected void setToDirt(final ModifiableTestableWorld world, final BlockPos pos) {
        if (!isNaturalDirt(world, pos)) {
            this.setBlockState(world, pos, Blocks.j.getDefaultState());
        }
    }
    
    @Override
    protected void setBlockState(final ModifiableWorld world, final BlockPos pos, final BlockState state) {
        this.setBlockStateWithoutUpdatingNeighbors(world, pos, state);
    }
    
    protected final void setBlockState(final Set<BlockPos> logPositions, final ModifiableWorld world, final BlockPos pos, final BlockState state) {
        this.setBlockStateWithoutUpdatingNeighbors(world, pos, state);
        if (BlockTags.o.contains(state.getBlock())) {
            logPositions.add(pos.toImmutable());
        }
    }
    
    private void setBlockStateWithoutUpdatingNeighbors(final ModifiableWorld modifiableWorld, final BlockPos blockPos, final BlockState blockState) {
        if (this.emitNeighborBlockUpdates) {
            modifiableWorld.setBlockState(blockPos, blockState, 19);
        }
        else {
            modifiableWorld.setBlockState(blockPos, blockState, 18);
        }
    }
    
    @Override
    public final boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final T config) {
        final Set<BlockPos> set6 = Sets.newHashSet();
        final boolean boolean7 = this.generate(set6, world, random, pos);
        final List<Set<BlockPos>> list8 = Lists.newArrayList();
        final int integer9 = 6;
        for (int integer10 = 0; integer10 < 6; ++integer10) {
            list8.add(Sets.newHashSet());
        }
        try (final BlockPos.PooledMutable pooledMutable10 = BlockPos.PooledMutable.get()) {
            if (boolean7 && !set6.isEmpty()) {
                for (final BlockPos blockPos13 : Lists.<BlockPos>newArrayList(set6)) {
                    for (final Direction direction17 : Direction.values()) {
                        pooledMutable10.set((Vec3i)blockPos13).setOffset(direction17);
                        if (!set6.contains(pooledMutable10)) {
                            final BlockState blockState18 = world.getBlockState(pooledMutable10);
                            if (blockState18.<Comparable>contains((Property<Comparable>)Properties.DISTANCE_1_7)) {
                                list8.get(0).add(pooledMutable10.toImmutable());
                                this.setBlockStateWithoutUpdatingNeighbors(world, pooledMutable10, ((AbstractPropertyContainer<O, BlockState>)blockState18).<Comparable, Integer>with((Property<Comparable>)Properties.DISTANCE_1_7, 1));
                            }
                        }
                    }
                }
            }
            for (int integer11 = 1; integer11 < 6; ++integer11) {
                final Set<BlockPos> set7 = list8.get(integer11 - 1);
                final Set<BlockPos> set8 = list8.get(integer11);
                for (final BlockPos blockPos14 : set7) {
                    for (final Direction direction18 : Direction.values()) {
                        pooledMutable10.set((Vec3i)blockPos14).setOffset(direction18);
                        if (!set7.contains(pooledMutable10)) {
                            if (!set8.contains(pooledMutable10)) {
                                final BlockState blockState19 = world.getBlockState(pooledMutable10);
                                if (blockState19.<Comparable>contains((Property<Comparable>)Properties.DISTANCE_1_7)) {
                                    final int integer12 = blockState19.<Integer>get((Property<Integer>)Properties.DISTANCE_1_7);
                                    if (integer12 > integer11 + 1) {
                                        final BlockState blockState20 = ((AbstractPropertyContainer<O, BlockState>)blockState19).<Comparable, Integer>with((Property<Comparable>)Properties.DISTANCE_1_7, integer11 + 1);
                                        this.setBlockStateWithoutUpdatingNeighbors(world, pooledMutable10, blockState20);
                                        set8.add(pooledMutable10.toImmutable());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return boolean7;
    }
    
    protected abstract boolean generate(final Set<BlockPos> arg1, final ModifiableTestableWorld arg2, final Random arg3, final BlockPos arg4);
}
