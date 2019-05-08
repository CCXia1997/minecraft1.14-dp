package net.minecraft.world.gen.feature;

import java.util.Iterator;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import net.minecraft.block.Blocks;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.predicate.block.BlockStatePredicate;

public class DesertWellFeature extends Feature<DefaultFeatureConfig>
{
    private static final BlockStatePredicate CAN_GENERATE;
    private final BlockState slab;
    private final BlockState wall;
    private final BlockState fluidInside;
    
    public DesertWellFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
        this.slab = Blocks.hK.getDefaultState();
        this.wall = Blocks.as.getDefaultState();
        this.fluidInside = Blocks.A.getDefaultState();
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, BlockPos pos, final DefaultFeatureConfig config) {
        for (pos = pos.up(); world.isAir(pos) && pos.getY() > 2; pos = pos.down()) {}
        if (!DesertWellFeature.CAN_GENERATE.a(world.getBlockState(pos))) {
            return false;
        }
        for (int integer6 = -2; integer6 <= 2; ++integer6) {
            for (int integer7 = -2; integer7 <= 2; ++integer7) {
                if (world.isAir(pos.add(integer6, -1, integer7)) && world.isAir(pos.add(integer6, -2, integer7))) {
                    return false;
                }
            }
        }
        for (int integer6 = -1; integer6 <= 0; ++integer6) {
            for (int integer7 = -2; integer7 <= 2; ++integer7) {
                for (int integer8 = -2; integer8 <= 2; ++integer8) {
                    world.setBlockState(pos.add(integer7, integer6, integer8), this.wall, 2);
                }
            }
        }
        world.setBlockState(pos, this.fluidInside, 2);
        for (final Direction direction7 : Direction.Type.HORIZONTAL) {
            world.setBlockState(pos.offset(direction7), this.fluidInside, 2);
        }
        for (int integer6 = -2; integer6 <= 2; ++integer6) {
            for (int integer7 = -2; integer7 <= 2; ++integer7) {
                if (integer6 == -2 || integer6 == 2 || integer7 == -2 || integer7 == 2) {
                    world.setBlockState(pos.add(integer6, 1, integer7), this.wall, 2);
                }
            }
        }
        world.setBlockState(pos.add(2, 1, 0), this.slab, 2);
        world.setBlockState(pos.add(-2, 1, 0), this.slab, 2);
        world.setBlockState(pos.add(0, 1, 2), this.slab, 2);
        world.setBlockState(pos.add(0, 1, -2), this.slab, 2);
        for (int integer6 = -1; integer6 <= 1; ++integer6) {
            for (int integer7 = -1; integer7 <= 1; ++integer7) {
                if (integer6 == 0 && integer7 == 0) {
                    world.setBlockState(pos.add(integer6, 4, integer7), this.wall, 2);
                }
                else {
                    world.setBlockState(pos.add(integer6, 4, integer7), this.slab, 2);
                }
            }
        }
        for (int integer6 = 1; integer6 <= 3; ++integer6) {
            world.setBlockState(pos.add(-1, integer6, -1), this.wall, 2);
            world.setBlockState(pos.add(-1, integer6, 1), this.wall, 2);
            world.setBlockState(pos.add(1, integer6, -1), this.wall, 2);
            world.setBlockState(pos.add(1, integer6, 1), this.wall, 2);
        }
        return true;
    }
    
    static {
        CAN_GENERATE = BlockStatePredicate.forBlock(Blocks.C);
    }
}
