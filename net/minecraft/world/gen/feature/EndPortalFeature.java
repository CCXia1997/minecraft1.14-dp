package net.minecraft.world.gen.feature;

import net.minecraft.state.AbstractPropertyContainer;
import java.util.Iterator;
import net.minecraft.state.property.Property;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Vec3i;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;

public class EndPortalFeature extends Feature<DefaultFeatureConfig>
{
    public static final BlockPos ORIGIN;
    private final boolean open;
    
    public EndPortalFeature(final boolean open) {
        super(DefaultFeatureConfig::deserialize);
        this.open = open;
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final DefaultFeatureConfig config) {
        for (final BlockPos blockPos7 : BlockPos.iterate(new BlockPos(pos.getX() - 4, pos.getY() - 1, pos.getZ() - 4), new BlockPos(pos.getX() + 4, pos.getY() + 32, pos.getZ() + 4))) {
            final boolean boolean8 = blockPos7.isWithinDistance(pos, 2.5);
            if (boolean8 || blockPos7.isWithinDistance(pos, 3.5)) {
                if (blockPos7.getY() < pos.getY()) {
                    if (boolean8) {
                        this.setBlockState(world, blockPos7, Blocks.z.getDefaultState());
                    }
                    else {
                        if (blockPos7.getY() >= pos.getY()) {
                            continue;
                        }
                        this.setBlockState(world, blockPos7, Blocks.dW.getDefaultState());
                    }
                }
                else if (blockPos7.getY() > pos.getY()) {
                    this.setBlockState(world, blockPos7, Blocks.AIR.getDefaultState());
                }
                else if (!boolean8) {
                    this.setBlockState(world, blockPos7, Blocks.z.getDefaultState());
                }
                else if (this.open) {
                    this.setBlockState(world, new BlockPos(blockPos7), Blocks.dU.getDefaultState());
                }
                else {
                    this.setBlockState(world, new BlockPos(blockPos7), Blocks.AIR.getDefaultState());
                }
            }
        }
        for (int integer6 = 0; integer6 < 4; ++integer6) {
            this.setBlockState(world, pos.up(integer6), Blocks.z.getDefaultState());
        }
        final BlockPos blockPos8 = pos.up(2);
        for (final Direction direction8 : Direction.Type.HORIZONTAL) {
            this.setBlockState(world, blockPos8.offset(direction8), ((AbstractPropertyContainer<O, BlockState>)Blocks.bL.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)WallTorchBlock.FACING, direction8));
        }
        return true;
    }
    
    static {
        ORIGIN = BlockPos.ORIGIN;
    }
}
