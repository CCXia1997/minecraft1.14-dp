package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.TestableWorld;
import java.util.Random;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.util.math.BlockPos;
import java.util.Set;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;

public class MegaPineTreeFeature extends MegaTreeFeature<DefaultFeatureConfig>
{
    private static final BlockState LOG;
    private static final BlockState LEAVES;
    private static final BlockState PODZOL;
    private final boolean aY;
    
    public MegaPineTreeFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, final boolean boolean2, final boolean boolean3) {
        super(function, boolean2, 13, 15, MegaPineTreeFeature.LOG, MegaPineTreeFeature.LEAVES);
        this.aY = boolean3;
    }
    
    public boolean generate(final Set<BlockPos> logPositions, final ModifiableTestableWorld world, final Random random, final BlockPos pos) {
        final int integer5 = this.getHeight(random);
        if (!this.checkTreeFitsAndReplaceGround(world, pos, integer5)) {
            return false;
        }
        this.makeTopLeaves(world, pos.getX(), pos.getZ(), pos.getY() + integer5, 0, random);
        for (int integer6 = 0; integer6 < integer5; ++integer6) {
            if (AbstractTreeFeature.isAirOrLeaves(world, pos.up(integer6))) {
                this.setBlockState(logPositions, world, pos.up(integer6), this.log);
            }
            if (integer6 < integer5 - 1) {
                if (AbstractTreeFeature.isAirOrLeaves(world, pos.add(1, integer6, 0))) {
                    this.setBlockState(logPositions, world, pos.add(1, integer6, 0), this.log);
                }
                if (AbstractTreeFeature.isAirOrLeaves(world, pos.add(1, integer6, 1))) {
                    this.setBlockState(logPositions, world, pos.add(1, integer6, 1), this.log);
                }
                if (AbstractTreeFeature.isAirOrLeaves(world, pos.add(0, integer6, 1))) {
                    this.setBlockState(logPositions, world, pos.add(0, integer6, 1), this.log);
                }
            }
        }
        this.replaceGround(world, random, pos);
        return true;
    }
    
    private void makeTopLeaves(final ModifiableTestableWorld modifiableTestableWorld, final int integer2, final int integer3, final int integer4, final int integer5, final Random random) {
        final int integer6 = random.nextInt(5) + (this.aY ? this.baseHeight : 3);
        int integer7 = 0;
        for (int integer8 = integer4 - integer6; integer8 <= integer4; ++integer8) {
            final int integer9 = integer4 - integer8;
            final int integer10 = integer5 + MathHelper.floor(integer9 / (float)integer6 * 3.5f);
            this.makeSquaredLeafLayer(modifiableTestableWorld, new BlockPos(integer2, integer8, integer3), integer10 + ((integer9 > 0 && integer10 == integer7 && (integer8 & 0x1) == 0x0) ? 1 : 0));
            integer7 = integer10;
        }
    }
    
    public void replaceGround(final ModifiableTestableWorld world, final Random random, final BlockPos pos) {
        this.replaceGroundNear(world, pos.west().north());
        this.replaceGroundNear(world, pos.east(2).north());
        this.replaceGroundNear(world, pos.west().south(2));
        this.replaceGroundNear(world, pos.east(2).south(2));
        for (int integer4 = 0; integer4 < 5; ++integer4) {
            final int integer5 = random.nextInt(64);
            final int integer6 = integer5 % 8;
            final int integer7 = integer5 / 8;
            if (integer6 == 0 || integer6 == 7 || integer7 == 0 || integer7 == 7) {
                this.replaceGroundNear(world, pos.add(-3 + integer6, 0, -3 + integer7));
            }
        }
    }
    
    private void replaceGroundNear(final ModifiableTestableWorld modifiableTestableWorld, final BlockPos blockPos) {
        for (int integer3 = -2; integer3 <= 2; ++integer3) {
            for (int integer4 = -2; integer4 <= 2; ++integer4) {
                if (Math.abs(integer3) != 2 || Math.abs(integer4) != 2) {
                    this.prepareGroundColumn(modifiableTestableWorld, blockPos.add(integer3, 0, integer4));
                }
            }
        }
    }
    
    private void prepareGroundColumn(final ModifiableTestableWorld modifiableTestableWorld, final BlockPos blockPos) {
        for (int integer3 = 2; integer3 >= -3; --integer3) {
            final BlockPos blockPos2 = blockPos.up(integer3);
            if (AbstractTreeFeature.isNaturalDirtOrGrass(modifiableTestableWorld, blockPos2)) {
                this.setBlockState(modifiableTestableWorld, blockPos2, MegaPineTreeFeature.PODZOL);
                break;
            }
            if (!AbstractTreeFeature.isAir(modifiableTestableWorld, blockPos2) && integer3 < 0) {
                break;
            }
        }
    }
    
    static {
        LOG = Blocks.J.getDefaultState();
        LEAVES = Blocks.ah.getDefaultState();
        PODZOL = Blocks.l.getDefaultState();
    }
}
