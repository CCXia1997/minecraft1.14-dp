package net.minecraft.world.gen.feature;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.Blocks;
import java.util.Iterator;
import net.minecraft.util.math.Direction;
import net.minecraft.block.VineBlock;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.TestableWorld;
import java.util.Random;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.util.math.BlockPos;
import java.util.Set;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;

public class OakTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig>
{
    private static final BlockState LOG;
    private static final BlockState LEAVES;
    protected final int height;
    private final boolean hasVinesAndCocoa;
    private final BlockState log;
    private final BlockState leaves;
    
    public OakTreeFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer, final boolean emitNeighborBlockUpdates) {
        this(configDeserializer, emitNeighborBlockUpdates, 4, OakTreeFeature.LOG, OakTreeFeature.LEAVES, false);
    }
    
    public OakTreeFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, final boolean boolean2, final int height, final BlockState log, final BlockState leaves, final boolean hasVinesAndCocoa) {
        super(function, boolean2);
        this.height = height;
        this.log = log;
        this.leaves = leaves;
        this.hasVinesAndCocoa = hasVinesAndCocoa;
    }
    
    public boolean generate(final Set<BlockPos> logPositions, final ModifiableTestableWorld world, final Random random, final BlockPos pos) {
        final int integer5 = this.getTreeHeight(random);
        boolean boolean6 = true;
        if (pos.getY() < 1 || pos.getY() + integer5 + 1 > 256) {
            return false;
        }
        for (int integer6 = pos.getY(); integer6 <= pos.getY() + 1 + integer5; ++integer6) {
            int integer7 = 1;
            if (integer6 == pos.getY()) {
                integer7 = 0;
            }
            if (integer6 >= pos.getY() + 1 + integer5 - 2) {
                integer7 = 2;
            }
            final BlockPos.Mutable mutable9 = new BlockPos.Mutable();
            for (int integer8 = pos.getX() - integer7; integer8 <= pos.getX() + integer7 && boolean6; ++integer8) {
                for (int integer9 = pos.getZ() - integer7; integer9 <= pos.getZ() + integer7 && boolean6; ++integer9) {
                    if (integer6 >= 0 && integer6 < 256) {
                        if (!AbstractTreeFeature.canTreeReplace(world, mutable9.set(integer8, integer6, integer9))) {
                            boolean6 = false;
                        }
                    }
                    else {
                        boolean6 = false;
                    }
                }
            }
        }
        if (!boolean6) {
            return false;
        }
        if (!AbstractTreeFeature.isDirtOrGrass(world, pos.down()) || pos.getY() >= 256 - integer5 - 1) {
            return false;
        }
        this.setToDirt(world, pos.down());
        int integer6 = 3;
        int integer7 = 0;
        for (int integer10 = pos.getY() - 3 + integer5; integer10 <= pos.getY() + integer5; ++integer10) {
            final int integer8 = integer10 - (pos.getY() + integer5);
            for (int integer9 = 1 - integer8 / 2, integer11 = pos.getX() - integer9; integer11 <= pos.getX() + integer9; ++integer11) {
                final int integer12 = integer11 - pos.getX();
                for (int integer13 = pos.getZ() - integer9; integer13 <= pos.getZ() + integer9; ++integer13) {
                    final int integer14 = integer13 - pos.getZ();
                    if (Math.abs(integer12) == integer9 && Math.abs(integer14) == integer9) {
                        if (random.nextInt(2) == 0) {
                            continue;
                        }
                        if (integer8 == 0) {
                            continue;
                        }
                    }
                    final BlockPos blockPos16 = new BlockPos(integer11, integer10, integer13);
                    if (AbstractTreeFeature.isAirOrLeaves(world, blockPos16) || AbstractTreeFeature.isReplaceablePlant(world, blockPos16)) {
                        this.setBlockState(world, blockPos16, this.leaves);
                    }
                }
            }
        }
        for (int integer10 = 0; integer10 < integer5; ++integer10) {
            if (AbstractTreeFeature.isAirOrLeaves(world, pos.up(integer10)) || AbstractTreeFeature.isReplaceablePlant(world, pos.up(integer10))) {
                this.setBlockState(logPositions, world, pos.up(integer10), this.log);
                if (this.hasVinesAndCocoa && integer10 > 0) {
                    if (random.nextInt(3) > 0 && AbstractTreeFeature.isAir(world, pos.add(-1, integer10, 0))) {
                        this.makeVine(world, pos.add(-1, integer10, 0), VineBlock.EAST);
                    }
                    if (random.nextInt(3) > 0 && AbstractTreeFeature.isAir(world, pos.add(1, integer10, 0))) {
                        this.makeVine(world, pos.add(1, integer10, 0), VineBlock.WEST);
                    }
                    if (random.nextInt(3) > 0 && AbstractTreeFeature.isAir(world, pos.add(0, integer10, -1))) {
                        this.makeVine(world, pos.add(0, integer10, -1), VineBlock.SOUTH);
                    }
                    if (random.nextInt(3) > 0 && AbstractTreeFeature.isAir(world, pos.add(0, integer10, 1))) {
                        this.makeVine(world, pos.add(0, integer10, 1), VineBlock.NORTH);
                    }
                }
            }
        }
        if (this.hasVinesAndCocoa) {
            for (int integer10 = pos.getY() - 3 + integer5; integer10 <= pos.getY() + integer5; ++integer10) {
                final int integer8 = integer10 - (pos.getY() + integer5);
                final int integer9 = 2 - integer8 / 2;
                final BlockPos.Mutable mutable10 = new BlockPos.Mutable();
                for (int integer12 = pos.getX() - integer9; integer12 <= pos.getX() + integer9; ++integer12) {
                    for (int integer13 = pos.getZ() - integer9; integer13 <= pos.getZ() + integer9; ++integer13) {
                        mutable10.set(integer12, integer10, integer13);
                        if (AbstractTreeFeature.isLeaves(world, mutable10)) {
                            final BlockPos blockPos17 = mutable10.west();
                            final BlockPos blockPos16 = mutable10.east();
                            final BlockPos blockPos18 = mutable10.north();
                            final BlockPos blockPos19 = mutable10.south();
                            if (random.nextInt(4) == 0 && AbstractTreeFeature.isAir(world, blockPos17)) {
                                this.makeVineColumn(world, blockPos17, VineBlock.EAST);
                            }
                            if (random.nextInt(4) == 0 && AbstractTreeFeature.isAir(world, blockPos16)) {
                                this.makeVineColumn(world, blockPos16, VineBlock.WEST);
                            }
                            if (random.nextInt(4) == 0 && AbstractTreeFeature.isAir(world, blockPos18)) {
                                this.makeVineColumn(world, blockPos18, VineBlock.SOUTH);
                            }
                            if (random.nextInt(4) == 0 && AbstractTreeFeature.isAir(world, blockPos19)) {
                                this.makeVineColumn(world, blockPos19, VineBlock.NORTH);
                            }
                        }
                    }
                }
            }
            if (random.nextInt(5) == 0 && integer5 > 5) {
                for (int integer10 = 0; integer10 < 2; ++integer10) {
                    for (final Direction direction11 : Direction.Type.HORIZONTAL) {
                        if (random.nextInt(4 - integer10) == 0) {
                            final Direction direction12 = direction11.getOpposite();
                            this.makeCocoa(world, random.nextInt(3), pos.add(direction12.getOffsetX(), integer5 - 5 + integer10, direction12.getOffsetZ()), direction11);
                        }
                    }
                }
            }
        }
        return true;
    }
    
    protected int getTreeHeight(final Random random) {
        return this.height + random.nextInt(3);
    }
    
    private void makeCocoa(final ModifiableWorld worlf, final int age, final BlockPos pos, final Direction direction) {
        this.setBlockState(worlf, pos, (((AbstractPropertyContainer<O, BlockState>)Blocks.dZ.getDefaultState()).with((Property<Comparable>)CocoaBlock.AGE, age)).<Comparable, Direction>with((Property<Comparable>)CocoaBlock.FACING, direction));
    }
    
    private void makeVine(final ModifiableWorld world, final BlockPos pos, final BooleanProperty directionProperty) {
        this.setBlockState(world, pos, ((AbstractPropertyContainer<O, BlockState>)Blocks.dH.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)directionProperty, true));
    }
    
    private void makeVineColumn(final ModifiableTestableWorld world, BlockPos pos, final BooleanProperty directionProperty) {
        this.makeVine(world, pos, directionProperty);
        int integer4;
        for (integer4 = 4, pos = pos.down(); AbstractTreeFeature.isAir(world, pos) && integer4 > 0; pos = pos.down(), --integer4) {
            this.makeVine(world, pos, directionProperty);
        }
    }
    
    static {
        LOG = Blocks.I.getDefaultState();
        LEAVES = Blocks.ag.getDefaultState();
    }
}
