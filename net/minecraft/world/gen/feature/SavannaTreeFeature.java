package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TestableWorld;
import java.util.Random;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.util.math.BlockPos;
import java.util.Set;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;

public class SavannaTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig>
{
    private static final BlockState LOG;
    private static final BlockState LEAVES;
    
    public SavannaTreeFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer, final boolean emitNeighborBlockUpdates) {
        super(configDeserializer, emitNeighborBlockUpdates);
    }
    
    public boolean generate(final Set<BlockPos> logPositions, final ModifiableTestableWorld world, final Random random, final BlockPos pos) {
        final int integer5 = random.nextInt(3) + random.nextInt(3) + 5;
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
        if (!AbstractTreeFeature.isNaturalDirtOrGrass(world, pos.down()) || pos.getY() >= 256 - integer5 - 1) {
            return false;
        }
        this.setToDirt(world, pos.down());
        final Direction direction7 = Direction.Type.HORIZONTAL.random(random);
        int integer7 = integer5 - random.nextInt(4) - 1;
        int integer10 = 3 - random.nextInt(3);
        int integer8 = pos.getX();
        int integer9 = pos.getZ();
        int integer11 = 0;
        for (int integer12 = 0; integer12 < integer5; ++integer12) {
            final int integer13 = pos.getY() + integer12;
            if (integer12 >= integer7 && integer10 > 0) {
                integer8 += direction7.getOffsetX();
                integer9 += direction7.getOffsetZ();
                --integer10;
            }
            final BlockPos blockPos15 = new BlockPos(integer8, integer13, integer9);
            if (AbstractTreeFeature.isAirOrLeaves(world, blockPos15)) {
                this.addLog(logPositions, world, blockPos15);
                integer11 = integer13;
            }
        }
        BlockPos blockPos16 = new BlockPos(integer8, integer11, integer9);
        for (int integer13 = -3; integer13 <= 3; ++integer13) {
            for (int integer14 = -3; integer14 <= 3; ++integer14) {
                if (Math.abs(integer13) != 3 || Math.abs(integer14) != 3) {
                    this.addLeaves(world, blockPos16.add(integer13, 0, integer14));
                }
            }
        }
        blockPos16 = blockPos16.up();
        for (int integer13 = -1; integer13 <= 1; ++integer13) {
            for (int integer14 = -1; integer14 <= 1; ++integer14) {
                this.addLeaves(world, blockPos16.add(integer13, 0, integer14));
            }
        }
        this.addLeaves(world, blockPos16.east(2));
        this.addLeaves(world, blockPos16.west(2));
        this.addLeaves(world, blockPos16.south(2));
        this.addLeaves(world, blockPos16.north(2));
        integer8 = pos.getX();
        integer9 = pos.getZ();
        final Direction direction8 = Direction.Type.HORIZONTAL.random(random);
        if (direction8 != direction7) {
            final int integer13 = integer7 - random.nextInt(2) - 1;
            int integer14 = 1 + random.nextInt(3);
            integer11 = 0;
            for (int integer15 = integer13; integer15 < integer5 && integer14 > 0; ++integer15, --integer14) {
                if (integer15 >= 1) {
                    final int integer16 = pos.getY() + integer15;
                    integer8 += direction8.getOffsetX();
                    integer9 += direction8.getOffsetZ();
                    final BlockPos blockPos17 = new BlockPos(integer8, integer16, integer9);
                    if (AbstractTreeFeature.isAirOrLeaves(world, blockPos17)) {
                        this.addLog(logPositions, world, blockPos17);
                        integer11 = integer16;
                    }
                }
            }
            if (integer11 > 0) {
                BlockPos blockPos18 = new BlockPos(integer8, integer11, integer9);
                for (int integer16 = -2; integer16 <= 2; ++integer16) {
                    for (int integer17 = -2; integer17 <= 2; ++integer17) {
                        if (Math.abs(integer16) != 2 || Math.abs(integer17) != 2) {
                            this.addLeaves(world, blockPos18.add(integer16, 0, integer17));
                        }
                    }
                }
                blockPos18 = blockPos18.up();
                for (int integer16 = -1; integer16 <= 1; ++integer16) {
                    for (int integer17 = -1; integer17 <= 1; ++integer17) {
                        this.addLeaves(world, blockPos18.add(integer16, 0, integer17));
                    }
                }
            }
        }
        return true;
    }
    
    private void addLog(final Set<BlockPos> logPositions, final ModifiableWorld world, final BlockPos pos) {
        this.setBlockState(logPositions, world, pos, SavannaTreeFeature.LOG);
    }
    
    private void addLeaves(final ModifiableTestableWorld world, final BlockPos pos) {
        if (AbstractTreeFeature.isAirOrLeaves(world, pos)) {
            this.setBlockState(world, pos, SavannaTreeFeature.LEAVES);
        }
    }
    
    static {
        LOG = Blocks.M.getDefaultState();
        LEAVES = Blocks.ak.getDefaultState();
    }
}
