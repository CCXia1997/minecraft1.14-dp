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

public class DarkOakTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig>
{
    private static final BlockState LOG;
    private static final BlockState LEAVES;
    
    public DarkOakTreeFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer, final boolean emitNeighborBlockUpdates) {
        super(configDeserializer, emitNeighborBlockUpdates);
    }
    
    public boolean generate(final Set<BlockPos> logPositions, final ModifiableTestableWorld world, final Random random, final BlockPos pos) {
        final int integer5 = random.nextInt(3) + random.nextInt(2) + 6;
        final int integer6 = pos.getX();
        final int integer7 = pos.getY();
        final int integer8 = pos.getZ();
        if (integer7 < 1 || integer7 + integer5 + 1 >= 256) {
            return false;
        }
        final BlockPos blockPos9 = pos.down();
        if (!AbstractTreeFeature.isNaturalDirtOrGrass(world, blockPos9)) {
            return false;
        }
        if (!this.doesTreeFit(world, pos, integer5)) {
            return false;
        }
        this.setToDirt(world, blockPos9);
        this.setToDirt(world, blockPos9.east());
        this.setToDirt(world, blockPos9.south());
        this.setToDirt(world, blockPos9.south().east());
        final Direction direction10 = Direction.Type.HORIZONTAL.random(random);
        final int integer9 = integer5 - random.nextInt(4);
        int integer10 = 2 - random.nextInt(3);
        int integer11 = integer6;
        int integer12 = integer8;
        final int integer13 = integer7 + integer5 - 1;
        for (int integer14 = 0; integer14 < integer5; ++integer14) {
            if (integer14 >= integer9 && integer10 > 0) {
                integer11 += direction10.getOffsetX();
                integer12 += direction10.getOffsetZ();
                --integer10;
            }
            final int integer15 = integer7 + integer14;
            final BlockPos blockPos10 = new BlockPos(integer11, integer15, integer12);
            if (AbstractTreeFeature.isAirOrLeaves(world, blockPos10)) {
                this.addLog(logPositions, world, blockPos10);
                this.addLog(logPositions, world, blockPos10.east());
                this.addLog(logPositions, world, blockPos10.south());
                this.addLog(logPositions, world, blockPos10.east().south());
            }
        }
        for (int integer14 = -2; integer14 <= 0; ++integer14) {
            for (int integer15 = -2; integer15 <= 0; ++integer15) {
                int integer16 = -1;
                this.addLeaves(world, integer11 + integer14, integer13 + integer16, integer12 + integer15);
                this.addLeaves(world, 1 + integer11 - integer14, integer13 + integer16, integer12 + integer15);
                this.addLeaves(world, integer11 + integer14, integer13 + integer16, 1 + integer12 - integer15);
                this.addLeaves(world, 1 + integer11 - integer14, integer13 + integer16, 1 + integer12 - integer15);
                if (integer14 > -2 || integer15 > -1) {
                    if (integer14 != -1 || integer15 != -2) {
                        integer16 = 1;
                        this.addLeaves(world, integer11 + integer14, integer13 + integer16, integer12 + integer15);
                        this.addLeaves(world, 1 + integer11 - integer14, integer13 + integer16, integer12 + integer15);
                        this.addLeaves(world, integer11 + integer14, integer13 + integer16, 1 + integer12 - integer15);
                        this.addLeaves(world, 1 + integer11 - integer14, integer13 + integer16, 1 + integer12 - integer15);
                    }
                }
            }
        }
        if (random.nextBoolean()) {
            this.addLeaves(world, integer11, integer13 + 2, integer12);
            this.addLeaves(world, integer11 + 1, integer13 + 2, integer12);
            this.addLeaves(world, integer11 + 1, integer13 + 2, integer12 + 1);
            this.addLeaves(world, integer11, integer13 + 2, integer12 + 1);
        }
        for (int integer14 = -3; integer14 <= 4; ++integer14) {
            for (int integer15 = -3; integer15 <= 4; ++integer15) {
                if ((integer14 != -3 || integer15 != -3) && (integer14 != -3 || integer15 != 4) && (integer14 != 4 || integer15 != -3)) {
                    if (integer14 != 4 || integer15 != 4) {
                        if (Math.abs(integer14) < 3 || Math.abs(integer15) < 3) {
                            this.addLeaves(world, integer11 + integer14, integer13, integer12 + integer15);
                        }
                    }
                }
            }
        }
        for (int integer14 = -1; integer14 <= 2; ++integer14) {
            for (int integer15 = -1; integer15 <= 2; ++integer15) {
                if (integer14 < 0 || integer14 > 1 || integer15 < 0 || integer15 > 1) {
                    if (random.nextInt(3) <= 0) {
                        for (int integer16 = random.nextInt(3) + 2, integer17 = 0; integer17 < integer16; ++integer17) {
                            this.addLog(logPositions, world, new BlockPos(integer6 + integer14, integer13 - integer17 - 1, integer8 + integer15));
                        }
                        for (int integer17 = -1; integer17 <= 1; ++integer17) {
                            for (int integer18 = -1; integer18 <= 1; ++integer18) {
                                this.addLeaves(world, integer11 + integer14 + integer17, integer13, integer12 + integer15 + integer18);
                            }
                        }
                        for (int integer17 = -2; integer17 <= 2; ++integer17) {
                            for (int integer18 = -2; integer18 <= 2; ++integer18) {
                                if (Math.abs(integer17) != 2 || Math.abs(integer18) != 2) {
                                    this.addLeaves(world, integer11 + integer14 + integer17, integer13 - 1, integer12 + integer15 + integer18);
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
    
    private boolean doesTreeFit(final TestableWorld world, final BlockPos pos, final int treeHeight) {
        final int integer4 = pos.getX();
        final int integer5 = pos.getY();
        final int integer6 = pos.getZ();
        final BlockPos.Mutable mutable7 = new BlockPos.Mutable();
        for (int integer7 = 0; integer7 <= treeHeight + 1; ++integer7) {
            int integer8 = 1;
            if (integer7 == 0) {
                integer8 = 0;
            }
            if (integer7 >= treeHeight - 1) {
                integer8 = 2;
            }
            for (int integer9 = -integer8; integer9 <= integer8; ++integer9) {
                for (int integer10 = -integer8; integer10 <= integer8; ++integer10) {
                    if (!AbstractTreeFeature.canTreeReplace(world, mutable7.set(integer4 + integer9, integer5 + integer7, integer6 + integer10))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private void addLog(final Set<BlockPos> logPositions, final ModifiableTestableWorld world, final BlockPos pos) {
        if (AbstractTreeFeature.canTreeReplace(world, pos)) {
            this.setBlockState(logPositions, world, pos, DarkOakTreeFeature.LOG);
        }
    }
    
    private void addLeaves(final ModifiableTestableWorld world, final int x, final int y, final int z) {
        final BlockPos blockPos5 = new BlockPos(x, y, z);
        if (AbstractTreeFeature.isAir(world, blockPos5)) {
            this.setBlockState(world, blockPos5, DarkOakTreeFeature.LEAVES);
        }
    }
    
    static {
        LOG = Blocks.N.getDefaultState();
        LEAVES = Blocks.al.getDefaultState();
    }
}
