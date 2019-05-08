package net.minecraft.world.gen.feature;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Property;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.block.VineBlock;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.Heightmap;
import java.util.Random;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.util.math.BlockPos;
import java.util.Set;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;

public class SwampTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig>
{
    private static final BlockState LOG;
    private static final BlockState LEAVES;
    
    public SwampTreeFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer, false);
    }
    
    public boolean generate(final Set<BlockPos> logPositions, final ModifiableTestableWorld world, final Random random, BlockPos pos) {
        final int integer5 = random.nextInt(4) + 5;
        pos = world.getTopPosition(Heightmap.Type.d, pos);
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
                integer7 = 3;
            }
            final BlockPos.Mutable mutable9 = new BlockPos.Mutable();
            for (int integer8 = pos.getX() - integer7; integer8 <= pos.getX() + integer7 && boolean6; ++integer8) {
                for (int integer9 = pos.getZ() - integer7; integer9 <= pos.getZ() + integer7 && boolean6; ++integer9) {
                    if (integer6 >= 0 && integer6 < 256) {
                        mutable9.set(integer8, integer6, integer9);
                        if (!AbstractTreeFeature.isAirOrLeaves(world, mutable9)) {
                            if (AbstractTreeFeature.isWater(world, mutable9)) {
                                if (integer6 > pos.getY()) {
                                    boolean6 = false;
                                }
                            }
                            else {
                                boolean6 = false;
                            }
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
        if (!AbstractTreeFeature.isNaturalDirtOrGrass(world, pos.down()) || pos.getY() >= 256 - integer5 - 1) {
            return false;
        }
        this.setToDirt(world, pos.down());
        for (int integer6 = pos.getY() - 3 + integer5; integer6 <= pos.getY() + integer5; ++integer6) {
            final int integer7 = integer6 - (pos.getY() + integer5);
            for (int integer10 = 2 - integer7 / 2, integer8 = pos.getX() - integer10; integer8 <= pos.getX() + integer10; ++integer8) {
                final int integer9 = integer8 - pos.getX();
                for (int integer11 = pos.getZ() - integer10; integer11 <= pos.getZ() + integer10; ++integer11) {
                    final int integer12 = integer11 - pos.getZ();
                    if (Math.abs(integer9) == integer10 && Math.abs(integer12) == integer10) {
                        if (random.nextInt(2) == 0) {
                            continue;
                        }
                        if (integer7 == 0) {
                            continue;
                        }
                    }
                    final BlockPos blockPos14 = new BlockPos(integer8, integer6, integer11);
                    if (AbstractTreeFeature.isAirOrLeaves(world, blockPos14) || AbstractTreeFeature.isReplaceablePlant(world, blockPos14)) {
                        this.setBlockState(world, blockPos14, SwampTreeFeature.LEAVES);
                    }
                }
            }
        }
        for (int integer6 = 0; integer6 < integer5; ++integer6) {
            final BlockPos blockPos15 = pos.up(integer6);
            if (AbstractTreeFeature.isAirOrLeaves(world, blockPos15) || AbstractTreeFeature.isWater(world, blockPos15)) {
                this.setBlockState(logPositions, world, blockPos15, SwampTreeFeature.LOG);
            }
        }
        for (int integer6 = pos.getY() - 3 + integer5; integer6 <= pos.getY() + integer5; ++integer6) {
            final int integer7 = integer6 - (pos.getY() + integer5);
            final int integer10 = 2 - integer7 / 2;
            final BlockPos.Mutable mutable10 = new BlockPos.Mutable();
            for (int integer9 = pos.getX() - integer10; integer9 <= pos.getX() + integer10; ++integer9) {
                for (int integer11 = pos.getZ() - integer10; integer11 <= pos.getZ() + integer10; ++integer11) {
                    mutable10.set(integer9, integer6, integer11);
                    if (AbstractTreeFeature.isLeaves(world, mutable10)) {
                        final BlockPos blockPos16 = mutable10.west();
                        final BlockPos blockPos14 = mutable10.east();
                        final BlockPos blockPos17 = mutable10.north();
                        final BlockPos blockPos18 = mutable10.south();
                        if (random.nextInt(4) == 0 && AbstractTreeFeature.isAir(world, blockPos16)) {
                            this.makeVines(world, blockPos16, VineBlock.EAST);
                        }
                        if (random.nextInt(4) == 0 && AbstractTreeFeature.isAir(world, blockPos14)) {
                            this.makeVines(world, blockPos14, VineBlock.WEST);
                        }
                        if (random.nextInt(4) == 0 && AbstractTreeFeature.isAir(world, blockPos17)) {
                            this.makeVines(world, blockPos17, VineBlock.SOUTH);
                        }
                        if (random.nextInt(4) == 0 && AbstractTreeFeature.isAir(world, blockPos18)) {
                            this.makeVines(world, blockPos18, VineBlock.NORTH);
                        }
                    }
                }
            }
        }
        return true;
    }
    
    private void makeVines(final ModifiableTestableWorld world, BlockPos pos, final BooleanProperty directionProperty) {
        final BlockState blockState4 = ((AbstractPropertyContainer<O, BlockState>)Blocks.dH.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)directionProperty, true);
        this.setBlockState(world, pos, blockState4);
        int integer5;
        for (integer5 = 4, pos = pos.down(); AbstractTreeFeature.isAir(world, pos) && integer5 > 0; pos = pos.down(), --integer5) {
            this.setBlockState(world, pos, blockState4);
        }
    }
    
    static {
        LOG = Blocks.I.getDefaultState();
        LEAVES = Blocks.ag.getDefaultState();
    }
}
